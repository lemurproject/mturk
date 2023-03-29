Applications for Relevance Assessments Using Amazon Mechanical Turk
========

This project contains the applications that are needed to run relevance assessments with Amazon Mechanical Turk.  Both the CAsT and the ClueWeb datasets have two appications each.  
One application is the web application that displays the data to the mechanical turk workers and submits that data to Amazon Mechanical Turk.  The second application, is the 
Mechanical Turk administration application that can create and delete HITs as well as get the HIT data from Amazon.

# ClueWeb
## Relevance Assessment Web Application (mturkcw)
In the ClueWeb Mechanical Turk HIT, workers are asked to select one of five random categories and enter a query pertaining to that category.  Once they submit, they are shown 
the documents that are returned from search engine or reranker.  We use a BERT reranker, which runs on a gpu, on top of a BM25 initial ranking provided by Solr.  This logic 
layer in this web application does some simple queueing so that no more than one query can go to the GPU at one time.

The ClueWeb MTurk application is a simple web application that calls a RESTful webservice to get the documents to display.  This application can be hooked up to any REST endpoint 
and display any number of documents.  The web application will accept any number of documents that come back as a json list where each document has the fields: docId, title, 
url, and highlight (also known as a snippet).  The worker can select what they feel are the relevant documents, and their response is sent to Mechanical Turk.

Here is an example of a json response to the query: dinosaur.
```
[{
	"docId": "clueweb09-en0008-23-28819",
	"title": "Dinosaurs MySpace Layouts 2.0 and Dinosaurs MySpace Backgrounds",
	"url": "http://www.layoutsparks.com/myspace-layouts/dinosaurs_0",
	"highlight": "Dinosaurs MySpace Layouts 2.0 and Dinosaurs MySpace Backgrounds Find Layouts and Backgrounds Myspace Layouts Myspace Graphics Glitter Graphics Myspace Layouts 2.0   Dinosaurs MySpace Layouts and Backgrounds page 1 of 1   This is page 1 of the results found for 'Dinosaurs'. There are the MySpace Profile 2.0 Layouts 0 to 2 of 2 2.0 Layouts found. Layoutsparks offers many more similar layouts.  "
}, {
	"docId": "random-clueweb09-en0002-82-08074",
	"title": "Getting around Bali",
	"url": "http://indonesia.sawadee.com/getting-around.htm",
	"highlight": "Getting around Bali \nBali hotels and resorts Online Reservations. \nBali Indonesia travel information map and tourist attractions with special rates for hotels in Bali. Bali Home Bali info Bali Hotels & Resorts Bali Map Bali Travel Guide Regency of Bali Bali Link Bali Indonesia Bali Home Bali information Bali Hotels & Resorts Bali Travel Guide Bali map Getting to Bali Bali Regency Bali Regency Bali index Badung"
}]
```

## Administration Application (mturkadmincw)
We host our Mechanical Turk projects externally so that we can create multi page HITs and have more control over the data and how they look.  As a result we must be able to manage 
our HITs programatically.  The administration application provides this functionality.

To use the administration application, you can alter the application.properties file.  To see the main functionality, take a look at MTurkAdminCW main class.  Amazon credentials also need to be set up to run MTurk admin tasks.

The application.properties
```
function=[create|get|approve|deletebyqual]
environment=[prod|sandbox]
experimentpath=[path to experiment data] *optional 
filename=[BASE_FILE_NAME].csv -> I usually make this a date.csv (e.g. 20230339.csv)

#create hits parameters
question.filename=externalquestion_cw.xml -> Can be a full path if .xml is not in the same directory
hit.price=0.30 -> Price paid per HIT.  MTurk adds an additional 40% to this price (e.g. it costs us $0.42 for a $0.30 HIT)
hit.number=10 -> The number of HITs each worker can complete
hit.assignments=10 -> The number of workers that can do each HIT.  The total number of queries is hit.assignments * hit.number.
hit.lifetime.hours=24 -> The number of hours the HIT is live.

#Qualification parameters
qualification.type=[Qualification ID] -> Only needed for createhits and deletebyqual functions.  Can be looked up in file: hitids_[FILENAME].csv
qualification.score=4 -> The qualification test score the worker must score greater than or equal to.
qualification.name=Stars:\ date\ 230328,\ 100\ HITs -> The name of the qualification (helpful for looking up qualifications on requester website.)
qualification.filename=qualification13.xml -> The qualification test file.  Located on boston: /bos/www/htdocs/mturk/qualification/cw22/.
qualification.answerkey=qualification13_answerkey.xml -> The qualification answer key.
```

# CAsT
## Relevance Assessment Web Application (mturkcast)
In the CAsT Mechanical Turk HIT, workers are shown a QA "conversation" at the top of the page and shown 5 short documents.  The worker is asked to assess how relevant each 
document is.  The data for the documents is loaded from a csv file, and the questions and documents must be shown in order so that the "conversation" makes sense.

## Administration Application (mturkadmincast)
This administration application is nearly identical to the ClueWeb administration, but we use different qualifiers.  For the ClueWeb experiments, we post a "qualification test"
to get workers.  For this application, we had known workers, which were assigned Qualifications through the Mechanical Turk web application.
