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
public class GetUnsubmittedHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	private Map<String, HITObject> hitMap;

	public void getUnsubmittedAssignments()
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

//		ListHITsRequest listHITsRequest = new ListHITsRequest();
//		listHITsRequest.setMaxResults(500);
//		ListHITsResult listHitResult = client.listHITs(listHITsRequest);

		Scanner scanner = new Scanner(new File(properties.getHit2topicFilename()));
		if (scanner.hasNext()) {
			scanner.next();
		}

		List<AnswerObject> allHITs = getAllHITs();
		List<AnswerObject> missedHITS = new ArrayList<AnswerObject>();
		// for (HIT hit : listHitResult.getHITs()) {
		while (scanner.hasNext()) {
			// String hitId = hit.getHITId();
			String nextLine = scanner.next();
			String hitId = nextLine.substring(0, nextLine.indexOf(","));
			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);
			List<String> statuses = new ArrayList<String>();
			statuses.add(AssignmentStatus.Submitted.name());
			statuses.add(AssignmentStatus.Approved.name());
			listHITRequest.setAssignmentStatuses(statuses);

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
					AnswerObject[] answers = gson.fromJson(jsonAnswer, AnswerObject[].class);
					for (AnswerObject answer : answers) {
						allHITs.remove(answer);
					}
				}
				// Thread.sleep(2000);
			}

		}
		scanner.close();

		String orig = properties.getDataFilename();
		String fileName = String.join("", orig.substring(0, orig.indexOf(".")), "_missed.csv");
		Writer missedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, false), "UTF8"));
		missedWriter.write(
				"hitId,queryNum,topicNum,subQueryNum,query,hitCount,isLast,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5,workerId,mturkHitId\n");
		int i = 1;
		for (AnswerObject hit : allHITs) {
			if (Integer.valueOf(hit.getHitId()).intValue() <= Integer.valueOf(properties.getHighestSeenHIT())
					.intValue()) {
				System.out.println("Missed HIT: " + hit.getHitId());
				HITObject hitObject = hitMap.get(hit.getHitId());
				hitObject.setIsLast("false");
				hitObject.setHitCount(String.join(" ", "HIT number", String.valueOf(i), "of",
						String.valueOf(allHITs.size()), "of missed HITs"));
				missedWriter.write(hitMap.get(hit.getHitId()).toString());
				missedWriter.write("\n");
				i++;
			}
		}
		System.out.println("Number HITS unsumbitted: " + allHITs.size());
		missedWriter.close();
	}

	public List<AnswerObject> getAllHITs() throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get(properties.getDataFilename()));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();

		List<AnswerObject> answers = new ArrayList<AnswerObject>();
		hitMap = new HashMap<String, HITObject>();
		while (hitIterator.hasNext()) {
			HITObject hitObject = hitIterator.next();

			hitMap.put(hitObject.getHitId(), hitObject);

			AnswerObject answer = new AnswerObject();
			answer.setHitId(hitObject.getHitId());
			answer.setDocId_1(hitObject.getDocument1());
			answer.setDocId_2(hitObject.getDocument2());
			answer.setDocId_3(hitObject.getDocument3());
			answer.setDocId_4(hitObject.getDocument4());
			answer.setDocId_5(hitObject.getDocument5());
			answers.add(answer);
		}

		return answers;
	}
}