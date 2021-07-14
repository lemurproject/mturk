package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
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
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.amazonaws.services.mturk.model.HIT;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;
import com.amazonaws.services.mturk.model.ListHITsForQualificationTypeRequest;
import com.amazonaws.services.mturk.model.ListHITsForQualificationTypeResult;

/* 
 * Before connecting to MTurk, set up your AWS account and IAM settings as described here:
 * https://blog.mturk.com/how-to-use-iam-to-control-api-access-to-your-mturk-account-76fe2c2e66e2
 * 
 * Configure your AWS credentials as described here:
 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
 *
 */
@Component
public class GetSubmittedHITsByQual {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void getSubmittedAssignments()
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());
		ListHITsForQualificationTypeRequest listHITsRequest = new ListHITsForQualificationTypeRequest();
		listHITsRequest.setQualificationTypeId(properties.getQualificationType());
		listHITsRequest.setMaxResults(100);
		ListHITsForQualificationTypeResult listHitResult = client.listHITsForQualificationType(listHITsRequest);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		for (HIT hit : listHitResult.getHITs()) {
			String hitId = hit.getHITId();
			// System.out.println(hitId + " - " + hit.getHITStatus());

			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(100);
			ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
			List<Assignment> assignmentList = listHITResult.getAssignments();

			// Iterate through all the assignments received
			String assignmentStatus = "";
			for (Assignment asn : assignmentList) {
				System.out.println(" assignment: " + asn.getAssignmentId() + " - " + asn.getAssignmentStatus());
				assignmentStatus = asn.getAssignmentStatus();

				if (assignmentStatus.equalsIgnoreCase("Submitted")) {
					long time = asn.getSubmitTime().getTime() - asn.getAcceptTime().getTime();

					Document doc = dBuilder.parse(new InputSource(new StringReader(asn.getAnswer())));
					NodeList nList = doc.getElementsByTagName("Answer");
					for (int i = 0; i < nList.getLength(); i++) {
						Node nNode = nList.item(i);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) nNode;
							String questionIdentifier = eElement.getElementsByTagName("QuestionIdentifier").item(0)
									.getTextContent();
							String freeText = eElement.getElementsByTagName("FreeText").item(0).getTextContent();

							System.out.println(questionIdentifier + ": " + freeText);

						}
					}
					System.out.println("------------------------");
				}
			}
		}
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

}