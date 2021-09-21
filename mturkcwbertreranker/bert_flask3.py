from flask import Flask, render_template, request, redirect, jsonify, send_from_directory
import torch
from transformers import AutoModelForSequenceClassification, AutoConfig, AutoTokenizer
import simplejson
import pprint
import sys
from urllib3 import PoolManager
import urllib.request
import socket
import argparse
import datetime
import re
import string
from urllib.parse import urlparse
import random
import gc

app = Flask(__name__, static_url_path='')

def dict_cuda(dictionary):
    tensor_keys = []
    for k, v in dictionary.items():
        if isinstance(v, torch.Tensor):
            tensor_keys.append(k)
    for k in tensor_keys:
        dictionary[k] = dictionary.pop(k).cuda()

SCORE_IDX = 1
LENGTH_LIMIT = 128
RANDOM_DOC_NUM = 0

modelLocation = '/bos/tmp15/cmw2/BERT/fold1'

#Read in LtR model
model_dict = {}
with open("1_filtered.model", "r") as a_file:
  for line in a_file:
    line = line.strip()
    splitLine = line.split(':')
    featureName = splitLine[0]
    model_dict[featureName] = float(splitLine[1])

random_docs = []
with open('randomDocs.json') as f:
    random_docs = simplejson.load(f)

config = AutoConfig.from_pretrained(modelLocation, num_labels=2)
model = AutoModelForSequenceClassification.from_pretrained(modelLocation, config=config)
tokenizer = AutoTokenizer.from_pretrained(modelLocation, do_lower_case=True)
model.cuda()
model.half()
model.eval()
torch.cuda.empty_cache()

@app.route('/search/<query>', methods=['POST', 'GET'])
def search(query):    
    splitted = query.split(" ")
    splitted = [each for each in splitted if len(each) > 0]
    ltrquery = "+".join([x for x in splitted])
    ltrquery = ltrquery.replace("\n", "")
    query = "+".join(['fulltext:' + x for x in splitted])
    query = query.replace("\n", "")

    collection = "cw09b_1_8,cw09b_2_8,cw09b_3_8,cw09b_4_8,cw09b_5_8,cw09b_6_8,cw09b_7_8,cw09b_8_8"
    #collection = "cw09b_1_8,cw09b_3_8,cw09b_4_8,cw09b_5_8,cw09b_6_8,cw09b_7_8,cw09b_8_8"
    url        = 'http://10.1.1.26:23232/solr/' + collection + '/query?'
    q          = 'q=' + query
    fl         = 'fl=id,url,title,score,fulltext,[features+store=bert_feature_store+efi.text=\'' + ltrquery + '\']'
    sort       = "sort=score+desc"
    rows       = "rows=500"
    wt         = "wt=json"
    params     = [ q, fl, sort, wt, rows ]
    p          = "&".join(params)
    p += "&hl=true&hl.fl=fulltext&hl.fragsize=500"

    fullurl = url+p
    #app.logger.info(fullurl)
    connection = urllib.request.urlopen(fullurl)
    response   = simplejson.load(connection)

    #Create dictionary of feature values to find
    featureValue_dict = {}
    for doc in response["response"]["docs"]:
        featuresArray = doc["[features]"].split(",")
        for feature in featuresArray:
            featureSplit = feature.split("=")
            featureName = featureSplit[0]
            featureValue = float(featureSplit[1])
            if featureName not in featureValue_dict:
                featureValue_dict[featureSplit[0]] = []
            featureValue_dict[featureSplit[0]].append((featureValue))

    #Get min and max scores
    minscore_dict = {}
    maxscore_dict = {}
    for featureName in featureValue_dict:
        featureValues = sorted(featureValue_dict[featureName])
        minscore_dict[featureName] = featureValues[0]
        maxscore_dict[featureName] = featureValues[-1]

    #LtR reranking
    ltrDictList = []
    for doc in response["response"]["docs"]:
        featuresArray = doc["[features]"].split(",")
        ltrScore = doc["score"]*model_dict["doc_score"]
        for feature in featuresArray:
            featureSplit = feature.split("=")
            featureName = featureSplit[0]
            featureScore = float(featureSplit[1])
            minScore = minscore_dict[featureName]
            maxScore = maxscore_dict[featureName]
            normalizedScore = featureScore
            if maxScore - minScore != 0:
              normalizedScore = (featureScore - minScore) / (maxScore - minScore)
            modelValue = model_dict[featureName]
            ltrScore += (normalizedScore*modelValue)
        ltrResult_dict = {'id': doc["id"], 'fulltext': doc["fulltext"], 'score': ltrScore, 'url': doc["url"][0], 'title': doc["title"][0]}
        ltrDictList.append((ltrResult_dict))

    ltrResults = sorted(ltrDictList, key=lambda x: x['score'], reverse=True)

    #Add low ranking docs to bertResults
    prefix = "nr-"
    highlight_dict = response["highlighting"]
    listLen = len(highlight_dict)
    
    lastIndex = listLen - 1
    randomDoc1 = ltrDictList[lastIndex]
    randomDoc1["id"] = prefix + randomDoc1["id"]
    
    lastIndex2 = listLen - 2
    randomDoc2 = ltrDictList[lastIndex2]
    randomDoc2["id"] = prefix + randomDoc2["id"]

    #Filter results so that no more than 20 come from the same location
    docList = []
    site_dict = {}
    for doc in ltrResults:
        url = doc['url']
        parsedUrl = urlparse(url)
        netloc = parsedUrl.netloc
        if site_dict.get(netloc, None) == None:
            site_dict[netloc] = []
        site_dict[netloc].append(doc["id"])
        if len(site_dict[netloc]) <= 20:
            docList.append(doc)
        if len(docList) == 100:
            break

    #BERT reranking
    pairs, docBatch, results = [], [], []
    count = 1
    for doc in docList:
        fulltext = doc["fulltext"][0]
        fulltext = fulltext[:256]
        pairs.append((query, fulltext))
        docBatch.append((doc))

        if count % 20 == 0:
            torchStart = datetime.datetime.now()
            encoded_batch = tokenizer.batch_encode_plus(
                pairs,
                truncation=True,
                truncation_strategy='only_second',
                padding='max_length',
                max_length=LENGTH_LIMIT,
                return_tensors='pt'
            )

            dict_cuda(encoded_batch)

            scores = torch.softmax(model(**encoded_batch)[0], dim=-1)[:, SCORE_IDX].cpu().tolist()
            for doc_dict, score in zip(docBatch, scores):
                results.append((doc_dict, score))

            #app.logger.info(torch.cuda.memory_summary(device=None, abbreviated=False))
            
            del docBatch
            del encoded_batch
            del scores
            gc.collect()
            docBatch = []
            torch.cuda.empty_cache()
        count += 1
        

    bertResults = sorted(results, key=lambda x: x[1], reverse=True)

    #Append random results to bert results
    #position = random.randint(0,9)
    #bertResults.insert(position, (randomDoc1, 0))
    #bertResults.append((randomDoc2, 0))

    #Add highlights to results
    #highlight_dict = response["highlighting"]
    #app.logger.info("Highlight dict len " + str(len(highlight_dict)))
    #resultDictList = []
    #for topResult in bertResults:
    #    #app.logger.info(topResult[0])
    #    currentId = topResult[0]['id']
    #    if currentId.startswith(prefix):
    #        currentId = currentId[len(prefix):]
    #    highlight = highlight_dict[currentId]["fulltext"][0]
    #    result_dict = {'docId': topResult[0]['id'], 'title': topResult[0]['title'], 'url': topResult[0]['url'], 'highlight': highlight}
    #    resultDictList.append((result_dict))

    #Filter out urls that are the same netloc
    top10 =[]
    url_dict = {}
    for result_dict in bertResults:
        url = result_dict[0]["url"]
        parsedUrl = urlparse(url)
        netloc = parsedUrl.netloc
        if url_dict.get(netloc, None) == None:
            url_dict[netloc] = []
        url_dict[netloc].append(result_dict[0]["id"])
        if len(url_dict[netloc]) <= 2:
            top10.append(result_dict)
        if len(top10) == 10:
            break

    #Append random results to bert results
    position = random.randint(0,9)
    top10.insert(position, (randomDoc1, 0))
    top10.append((randomDoc2, 0))

    #Add highlights to results
    #highlight_dict = response["highlighting"]
    app.logger.info("Highlight dict len " + str(len(highlight_dict)))
    resultDictList = []
    for topResult in top10:
        #app.logger.info(topResult[0])
        currentId = topResult[0]['id']
        if currentId.startswith(prefix):
            currentId = currentId[len(prefix):]
        highlight = highlight_dict[currentId]["fulltext"][0]
        result_dict = {'docId': topResult[0]['id'], 'title': topResult[0]['title'], 'url': topResult[0]['url'], 'highlight': highlight}
        resultDictList.append((result_dict))

    #Insert random doc from file
    position2 = random.randint(0,11)
    global RANDOM_DOC_NUM
    random_doc_index = RANDOM_DOC_NUM % len(random_docs)
    random_doc = random_docs[random_doc_index]
    #app.logger.info(random_doc)
    resultDictList.insert(position2, (random_doc))
    #app.logger.info(resultDictList)
    RANDOM_DOC_NUM = RANDOM_DOC_NUM + 1

    jsonString = simplejson.dumps(resultDictList) + '\n'
    return jsonString
			
if __name__ == "__main__":
    app.run(host='0.0.0.0', port=23232, debug=True)
