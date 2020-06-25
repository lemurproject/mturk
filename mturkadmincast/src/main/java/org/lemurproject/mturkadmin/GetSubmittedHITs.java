package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
/*
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.AssignmentStatus;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;
import com.google.gson.Gson;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

/* 
 * Before connecting to MTurk, set up your AWS account and IAM settings as described here:
 * https://blog.mturk.com/how-to-use-iam-to-control-api-access-to-your-mturk-account-76fe2c2e66e2
 * 
 * Configure your AWS credentials as described here:
 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
 *
 */
@Component
public class GetSubmittedHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void getSubmittedAssignments()
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());
		Map<String, Map<String, Map<String, HITObject>>> hitMap = getAllHITs();

//		ListHITsRequest listHITsRequest = new ListHITsRequest();
//		listHITsRequest.setMaxResults(500);
//		ListHITsResult listHitResult = client.listHITs(listHITsRequest);

		Scanner scanner = new Scanner(new File(properties.getHit2topicFilename()));
		if (scanner.hasNext()) {
			scanner.next();
		}

		List<JudgedDocumentObject> judgedDocuments = new ArrayList<JudgedDocumentObject>();
		// for (HIT hit : listHitResult.getHITs()) {
		while (scanner.hasNext()) {
			// String hitId = hit.getHITId();
			String nextLine = scanner.next();
			String hitId = nextLine.substring(0, nextLine.indexOf(","));
			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);
			listHITRequest.setAssignmentStatuses(Collections.singletonList(AssignmentStatus.Submitted.name()));
			// listHITRequest.setAssignmentStatuses(Collections.singletonList(AssignmentStatus.Approved.name()));

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(100);
			ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
			List<Assignment> assignmentList = listHITResult.getAssignments();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Gson gson = new Gson();

			// Iterate through all the assignments received
			for (Assignment asn : assignmentList) {
				long time = asn.getSubmitTime().getTime() - asn.getAcceptTime().getTime();

				Document doc = dBuilder.parse(new InputSource(new StringReader(asn.getAnswer())));
				NodeList nList = doc.getElementsByTagName("Answer");
				Node nNode = nList.item(0);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String jsonAnswer = eElement.getElementsByTagName("FreeText").item(0).getTextContent();

					AnswerObject[] answer = gson.fromJson(jsonAnswer, AnswerObject[].class);

					HITObject hit = hitMap.get(answer[0].getDocId_1()).get(answer[0].getDocId_2())
							.get(answer[0].getDocId_3());

					JudgedDocumentObject judgedDoc1 = new JudgedDocumentObject();
					judgedDoc1.setHitId(hit.getHitId());
					judgedDoc1.setQueryId(hit.getQueryNum());
					judgedDoc1.setDocId(answer[0].getDocId_1());
					judgedDoc1.setWorkerId(asn.getWorkerId());
					judgedDoc1.setAssignmentId(asn.getAssignmentId());
					judgedDoc1.setTime(String.valueOf(time));
					judgedDoc1.setTrecScore(answer[0].getScore_1());
					judgedDoc1.setWorkerScore(answer[0].getDoc1WorkerScore());
					judgedDocuments.add(judgedDoc1);

					JudgedDocumentObject judgedDoc2 = new JudgedDocumentObject();
					judgedDoc2.setHitId(hit.getHitId());
					judgedDoc2.setQueryId(hit.getQueryNum());
					judgedDoc2.setDocId(answer[0].getDocId_2());
					judgedDoc2.setWorkerId(asn.getWorkerId());
					judgedDoc2.setAssignmentId(asn.getAssignmentId());
					judgedDoc2.setTime(String.valueOf(time));
					judgedDoc2.setTrecScore(answer[0].getScore_2());
					judgedDoc2.setWorkerScore(answer[0].getDoc2WorkerScore());
					judgedDocuments.add(judgedDoc2);

					JudgedDocumentObject judgedDoc3 = new JudgedDocumentObject();
					judgedDoc3.setHitId(hit.getHitId());
					judgedDoc3.setQueryId(hit.getQueryNum());
					judgedDoc3.setDocId(answer[0].getDocId_3());
					judgedDoc3.setWorkerId(asn.getWorkerId());
					judgedDoc3.setAssignmentId(asn.getAssignmentId());
					judgedDoc3.setTime(String.valueOf(time));
					judgedDoc3.setTrecScore(answer[0].getScore_3());
					judgedDoc3.setWorkerScore(answer[0].getDoc3WorkerScore());
					judgedDocuments.add(judgedDoc3);

					JudgedDocumentObject judgedDoc4 = new JudgedDocumentObject();
					judgedDoc4.setHitId(hit.getHitId());
					judgedDoc4.setQueryId(hit.getQueryNum());
					judgedDoc4.setDocId(answer[0].getDocId_4());
					judgedDoc4.setWorkerId(asn.getWorkerId());
					judgedDoc4.setAssignmentId(asn.getAssignmentId());
					judgedDoc4.setTime(String.valueOf(time));
					judgedDoc4.setTrecScore(answer[0].getScore_4());
					judgedDoc4.setWorkerScore(answer[0].getDoc4WorkerScore());
					judgedDocuments.add(judgedDoc4);

					JudgedDocumentObject judgedDoc5 = new JudgedDocumentObject();
					judgedDoc5.setHitId(hit.getHitId());
					judgedDoc5.setQueryId(hit.getQueryNum());
					judgedDoc5.setDocId(answer[0].getDocId_5());
					judgedDoc5.setWorkerId(asn.getWorkerId());
					judgedDoc5.setAssignmentId(asn.getAssignmentId());
					judgedDoc5.setTime(String.valueOf(time));
					judgedDoc5.setTrecScore(answer[0].getScore_5());
					judgedDoc5.setWorkerScore(answer[0].getDoc5WorkerScore());
					judgedDocuments.add(judgedDoc5);
				}
				Thread.sleep(2000);
			}
		}
		scanner.close();
		writeCsv(judgedDocuments);

	}

	private void writeCsv(List<JudgedDocumentObject> judgedDocs) throws IOException {
		Writer csvWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(properties.getJudgedDocumentsCsvName()), "UTF8"));
		Writer dupsWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("duplicates_32.csv"), "UTF8"));
		if (judgedDocs != null && judgedDocs.size() > 0) {
			csvWriter.write(judgedDocs.get(0).getCsvHeaders());
			Map<String, List<String>> docIds2Scores = new HashMap<String, List<String>>();
			for (JudgedDocumentObject judgedDoc : judgedDocs) {
				csvWriter.write(judgedDoc.toString());
				docIds2Scores.putIfAbsent(judgedDoc.getDocId(), new ArrayList<String>());
				docIds2Scores.get(judgedDoc.getDocId()).add(judgedDoc.getWorkerScore());
			}
			for (String docId : docIds2Scores.keySet()) {
				List<String> workerScores = docIds2Scores.get(docId);
				if (workerScores.size() > 1) {
					dupsWriter.write(docId);
					dupsWriter.write(",");
					dupsWriter.write(String.join(",", workerScores));
					dupsWriter.write("\n");
				}
			}
		}
		csvWriter.close();
		dupsWriter.close();
	}

	public Map<String, Map<String, Map<String, HITObject>>> getAllHITs() throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(properties.getDataFilename()));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();

		Map<String, Map<String, Map<String, HITObject>>> hitMap = new HashMap<String, Map<String, Map<String, HITObject>>>();
		while (hitIterator.hasNext()) {
			HITObject hitObject = hitIterator.next();

			AnswerObject answer = new AnswerObject();
			answer.setHitId(hitObject.getHitId());
			answer.setDocId_1(hitObject.getDocument1());
			answer.setDocId_2(hitObject.getDocument2());
			answer.setDocId_3(hitObject.getDocument3());
			answer.setDocId_4(hitObject.getDocument4());
			answer.setDocId_5(hitObject.getDocument5());

			hitMap.putIfAbsent(hitObject.getDocument1(), new HashMap<String, Map<String, HITObject>>());
			hitMap.get(hitObject.getDocument1()).putIfAbsent(hitObject.getDocument2(),
					new HashMap<String, HITObject>());
			hitMap.get(hitObject.getDocument1()).get(hitObject.getDocument2()).put(hitObject.getDocument3(), hitObject);
		}

		return hitMap;
	}
}