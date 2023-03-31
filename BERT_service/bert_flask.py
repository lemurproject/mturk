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

SCORE_IDX = 0
LENGTH_LIMIT = 512
RANDOM_DOC_NUM = 0

#modelLocation = '/bos/tmp2/cmw2/BERT/fold1'
modelLocation = '/bos/tmp2/cmw2/mturk/train_bert/training/train_100p/bert_model_marco_cw09_mturk'

random_docs = []
with open('randomDocs.json') as f:
    random_docs = simplejson.load(f)


config = AutoConfig.from_pretrained(modelLocation, num_labels=1)
model = AutoModelForSequenceClassification.from_pretrained(modelLocation, config=config)
tokenizer = AutoTokenizer.from_pretrained(modelLocation, do_lower_case=True)
SEP = tokenizer.sep_token

model.cuda()
model.half()
model.eval()
torch.cuda.empty_cache()

@app.route('/search/<query>', methods=['POST', 'GET'])
def search(query):    
    lowercasequery = query.lower()
    splitted = query.split(" ")
    splitted = [each for each in splitted if len(each) > 0]
    query = "+".join(['fulltext:' + x for x in splitted])
    query = query.replace("\n", "")

    #collection = "cw09b_1_8,cw09b_2_8,cw09b_3_8,cw09b_4_8,cw09b_5_8,cw09b_6_8,cw09b_7_8,cw09b_8_8"
    collection = "cw22B_0_11,cw22B_12_23,cw22B_24_35,cw22B_36_46"
    url        = 'http://10.1.1.27:23232/solr/' + collection + '/query?'
    q          = 'q=' + query
    fl         = 'fl=id,url,title,score,fulltext'
    sort       = "sort=score+desc"
    rows       = "rows=10"
    wt         = "wt=json"
    params     = [ q, fl, sort, wt, rows ]
    p          = "&".join(params)
    p += "&hl=true&hl.fl=fulltext&hl.fragsize=500"

    fullurl = url+p
    app.logger.info(fullurl)
    connection = urllib.request.urlopen(fullurl)
    response   = simplejson.load(connection)
    #app.logger.info('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')
    #app.logger.info(response)
    #app.logger.info('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')

    #LtR reranking
    ltrDictList = []
    for doc in response["response"]["docs"]:
        title = ""
        if "title" in doc:
            title = doc["title"][0]
        else:
            title = doc["fulltext"][0].split('\n')[0]
            if len(title) == 0:
                title = doc["fulltext"][0][:500]
        ltrResult_dict = {'id': doc["id"], 'fulltext': doc["fulltext"][0], 'score': doc["score"], 'url': doc["url"][0], 'title': title, 'filtered': False}
        ltrDictList.append((ltrResult_dict))

    #Add low ranking docs to bertResults
    prefix = "nr-"
    highlight_dict = response["highlighting"]
    randomDocList = []

    #Query Single Words from the queries to add as non-relevant documents
    stopwords = {'what','who','is','a','at','is','he','how','best','the','most','I','can','do','to','some','will','should','in','of','be','are','my','with','go','not','and','we','our','when','you','much','for','if','an','have','this','the','off','get','where','over','from','there','new','all','above','across','against','along','around','before','behind','below','by','down','into','toward','under','top','on'}
    querywords = lowercasequery.split()
    resultwords  = [word for word in querywords if word.lower() not in stopwords]
    resultquery = ' '.join(resultwords)
    if len(resultquery.split()) > 1: 
        splitted = resultquery.split(" ")
        for i in range(0,2):
           #app.logger.info(i)
           word = splitted[i]
           #app.logger.info(word)
           query2 = 'fulltext:' + word
           #app.logger.info(query2)

           #collection2 = "cw09b_1_8,cw09b_2_8,cw09b_3_8,cw09b_4_8,cw09b_5_8,cw09b_6_8,cw09b_7_8,cw09b_8_8"
           collection2 = "cw22B_0_11,cw22B_12_23,cw22B_24_35,cw22B_36_46"
           url2        = 'http://10.1.1.27:23232/solr/' + collection2 + '/query?'
           q2          = 'q=' + query2
           fl2         = 'fl=id,url,title,score,fulltext'
           sort2       = "sort=score+desc"
           rows2       = "rows=1"
           wt2         = "wt=json"
           params2     = [ q2, fl2, sort2, wt2, rows2]
           p2          = "&".join(params2)
           p2 += "&hl=true&hl.fl=fulltext&hl.fragsize=500"
           
           fullurl2 = url2+p2
           #app.logger.info(fullurl2)
           connection2 = urllib.request.urlopen(fullurl2)
           response2   = simplejson.load(connection2)
           #app.logger.info(response)

           doc2 = response2["response"]["docs"][0]
           title = ""
           if 'title' in doc2.keys():
               title = doc2["title"][0]
           else:
               title = doc2["fulltext"][0].split('\n')[0]
           doc_id2 = prefix + doc2["id"]
           highlight_dict_sub = response2["highlighting"]
           highlight2 = highlight_dict_sub[doc2["id"]]["fulltext"][0]
           doc_dict2 = {'docId': doc_id2, 'url': doc2["url"][0], 'title': title, 'highlight': highlight2, 'score': "-1 - -1", 'filtered': False}
           #app.logger.info(doc_dict2)
           randomDocList.append(doc_dict2)
    else:
        listLen = len(highlight_dict)

        lastIndex = listLen - 1
        randomDoc1 = ltrDictList[lastIndex]
        highlight1 = highlight_dict[randomDoc1["id"]]["fulltext"][0]
        randomDoc1["docId"] = prefix + randomDoc1["id"]
        randomDoc1["highlight"]=highlight1
        randomDoc1['score']=-1
        randomDoc1['filtered']=False
        randomDocList.append(randomDoc1)

        lastIndex2 = listLen - 2
        randomDoc2 = ltrDictList[lastIndex2]
        highlight2 = highlight_dict[randomDoc2["id"]]["fulltext"][0]
        randomDoc2["docId"] = prefix + randomDoc2["id"]
        randomDoc2["highlight"]=highlight2
        randomDoc2['score']=-1
        randomDoc2['filtered']=False
        randomDocList.append(randomDoc2)

    #Filter results so that no more than 20 come from the same location
    docList = []
    site_dict = {}
    numFiltered = 0
    for doc in ltrDictList:
        url = doc['url']
        parsedUrl = urlparse(url)
        netloc = parsedUrl.netloc
        if site_dict.get(netloc, None) == None:
            site_dict[netloc] = []
        site_dict[netloc].append(doc["id"])
        if len(site_dict[netloc]) <= 20:
            docList.append(doc)
        else:
            numFiltered += 1
        if len(docList) == 500:
            break

    app.logger.info("Filtered " + str(numFiltered) + " for query: " + query)

    #BERT reranking
    pairs, docBatch, results = [], [], []
    count = 1
    for doc in docList:
        #fulltext = doc["fulltext"][0]
        #title = fulltext.split('\n')[0]
        #fulltext = fulltext.split('\n')[1:]
        #fulltext = fulltext[:1024]
        #text = title + tokenizer.sep_token + fulltext
        
        #pairs.append((query, text))
        #docBatch.append((doc))

        fulltext = doc["fulltext"][0]
        fulltext = fulltext[:1024]
        url = doc["url"]
        if "title" not in doc:
            doc["title"] = fulltext.split("\n")[0]
        pairs.append((query, SEP + url + SEP + doc["title"] + SEP + fulltext))
        docBatch.append((doc))

        if count % 20 == 0 or count == len(docList):
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

            with torch.no_grad():
                output = model(**encoded_batch)
                scores = output[0].cpu().numpy()[:, SCORE_IDX].tolist()
            #scores = torch.softmax(model(**encoded_batch)[0])[:, SCORE_IDX].cpu().tolist()
            #app.logger.info('Scores: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')
            #app.logger.info(scores)
            for doc_dict, score in zip(docBatch, scores):
                #app.logger.info(score)
                doc_dict['bert_score'] = score
                #app.logger.info(doc_dict['score'])
                results.append((doc_dict, score))

            del pairs
            del docBatch
            del encoded_batch
            del scores
            gc.collect()
            pairs, docBatch = [], []
            torch.cuda.empty_cache()
        count += 1
        
    
    #app.logger.info('Doc Dict: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~')
    #for result in results:
    #    app.logger.info('Result scores: ' + str(result[1]) + ' - ' + str(result[0]['score']))
    bertResults = sorted(results, key=lambda x: (x[1],x[0]['score']), reverse=True)
    numFilteredResults = len(docList)
    numBertResults = len(bertResults)
    app.logger.info("# of filtered results: " + str(numFilteredResults))
    app.logger.info("# of BERT results: " + str(numBertResults))

    #Filter out urls that are the same netloc
    top10 =[]
    url_dict = {}
    num_not_filtered = 0
    for result_dict in bertResults:
        url = result_dict[0]["url"]
        bm25_score = result_dict[0]["score"]
        #result_dict[0]["score"] = result_dict[1]
        parsedUrl = urlparse(url)
        netloc = parsedUrl.netloc
        if url_dict.get(netloc, None) == None:
            url_dict[netloc] = []
        url_dict[netloc].append(result_dict[0]["id"])
        if len(url_dict[netloc]) <= 10:
            num_not_filtered+=1
        elif (bm25_score in x["score"] for x["score"] in top10):
            result_dict[0]['filtered']=True
            app.logger.info("Duplicate document: " + result_dict[0]['id'])
        else:
            result_dict[0]['filtered']=True
        top10.append(result_dict)
        if num_not_filtered == 10:
            break

    #Add highlights to results
    #highlight_dict = response["highlighting"]
    #app.logger.info("Highlight dict len " + str(len(highlight_dict)))
    resultDictList = []
    for topResult in top10:
        #app.logger.info(topResult[0])
        currentId = topResult[0]['id']
        if currentId.startswith(prefix):
            currentId = currentId[len(prefix):]
        highlight = highlight_dict[currentId]["fulltext"][0]
        #highlight = topResult[0]['fulltext'][:2000]
        score_string = str(topResult[0]['bert_score']) + " - " + str(topResult[0]['score'])
        result_dict = {'docId': topResult[0]['id'], 'title': topResult[0]['title'], 'url': topResult[0]['url'], 'highlight': highlight, 'score': score_string, 'filtered': topResult[0]['filtered']}
        resultDictList.append((result_dict))

    #Append random results to bert results
    #position = random.randint(0,9)
    resultDictList.append(randomDocList[0])
    resultDictList.append(randomDocList[1])

    #Insert random doc from file
    position2 = random.randint(0,11)
    global RANDOM_DOC_NUM
    random_doc_index = RANDOM_DOC_NUM % len(random_docs)
    random_doc = random_docs[random_doc_index]
    random_doc['score']="-2 - -2"
    random_doc['filtered']=False
    #app.logger.info(random_doc)
    resultDictList.append(random_doc)
    #app.logger.info(resultDictList)
    RANDOM_DOC_NUM = RANDOM_DOC_NUM + 1

    #app.logger.info(len(resultDictList))

    random.shuffle(resultDictList)
    jsonString = simplejson.dumps(resultDictList) + '\n'
    #app.logger.info(jsonString)
    return jsonString
			
if __name__ == "__main__":
    app.run(host='0.0.0.0', port=23232, debug=True)
