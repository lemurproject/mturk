package org.lemurproject.mturk;

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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;
import com.amazonaws.services.mturk.model.ApproveAssignmentRequest;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.AssignmentStatus;
import com.amazonaws.services.mturk.model.HIT;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;
import com.amazonaws.services.mturk.model.ListHITsRequest;
import com.amazonaws.services.mturk.model.ListHITsResult;
import com.google.gson.Gson;

/* 
 * Before connecting to MTurk, set up your AWS account and IAM settings as described here:
 * https://blog.mturk.com/how-to-use-iam-to-control-api-access-to-your-mturk-account-76fe2c2e66e2
 * 
 * Configure your AWS credentials as described here:
 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
 *
 */

public class DeleteHITs {

	// TODO Change this to your HIT ID - see CreateHITSample.java for generating a
	// HIT
	private static final String HIT_ID_TO_APPROVE = "3KVQ0UJWPXLCPSFYQLCH2MCIK26W59";

	private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
	private static final String SIGNING_REGION = "us-east-1";

	public static void main(final String[] argv) throws IOException, ParserConfigurationException, SAXException {
		final DeleteHITs sandboxApp = new DeleteHITs(getSandboxClient());
		sandboxApp.deleteHits();
	}

	private final AmazonMTurk client;

	private DeleteHITs(final AmazonMTurk client) {
		this.client = client;
	}

	/*
	 * Use the Amazon Mechanical Turk Sandbox to publish test Human Intelligence
	 * Tasks (HITs) without paying any money. Make sure to sign up for a Sanbox
	 * account at https://requestersandbox.mturk.com/ with the same credentials as
	 * your main MTurk account.
	 */
	private static AmazonMTurk getSandboxClient() {
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

	private void deleteHits() throws ParserConfigurationException, SAXException, IOException {
		ListHITsRequest listHITsRequest = new ListHITsRequest();
		listHITsRequest.setMaxResults(100);
		ListHITsResult listHitResult = client.listHITs(listHITsRequest);

		int hitNum = 0;
		for (HIT hit : listHitResult.getHITs()) {
			hitNum++;
			// System.out.println("HIT num: " + hitNum);
			String hitId = hit.getHITId();
			approveAssignment(hitId);
		}
	}

	private void approveAssignment(final String hitId) throws ParserConfigurationException, SAXException, IOException {
		ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
		listHITRequest.setHITId(hitId);
		listHITRequest.setAssignmentStatuses(Collections.singletonList(AssignmentStatus.Submitted.name()));

		// Get a maximum of 10 completed assignments for this HIT
		listHITRequest.setMaxResults(10);
		ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
		List<Assignment> assignmentList = listHITResult.getAssignments();
		// System.out.println("The number of submitted assignments is " +
		// assignmentList.size());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Gson gson = new Gson();

		List<JudgedDocumentObject> judgedDocuments = new ArrayList<JudgedDocumentObject>();
		// Iterate through all the assignments received
		for (Assignment asn : assignmentList) {
			System.out.println("The worker with ID " + asn.getWorkerId() + " submitted assignment "
					+ asn.getAssignmentId() + " and gave the answer " + asn.getAnswer());
			long time = asn.getSubmitTime().getTime() - asn.getAcceptTime().getTime();

			Document doc = dBuilder.parse(new InputSource(new StringReader(asn.getAnswer())));
			NodeList nList = doc.getElementsByTagName("Answer");
			Node nNode = nList.item(0);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String jsonAnswer = eElement.getElementsByTagName("FreeText").item(0).getTextContent();

				AnswerObject[] answer = gson.fromJson(jsonAnswer, AnswerObject[].class);

				JudgedDocumentObject judgedDoc1 = new JudgedDocumentObject();
				judgedDoc1.setDocId(answer[0].getDocId_1());
				judgedDoc1.setWorkerId(asn.getWorkerId());
				judgedDoc1.setTime(String.valueOf(time));
				judgedDoc1.setTrecScore(answer[0].getScore_1());
				judgedDoc1.setWorkerScore(answer[0].getDoc1WorkerScore());
				judgedDocuments.add(judgedDoc1);

				JudgedDocumentObject judgedDoc2 = new JudgedDocumentObject();
				judgedDoc2.setDocId(answer[0].getDocId_2());
				judgedDoc2.setWorkerId(asn.getWorkerId());
				judgedDoc2.setTime(String.valueOf(time));
				judgedDoc2.setTrecScore(answer[0].getScore_2());
				judgedDoc2.setWorkerScore(answer[0].getDoc2WorkerScore());
				judgedDocuments.add(judgedDoc2);

				JudgedDocumentObject judgedDoc3 = new JudgedDocumentObject();
				judgedDoc3.setDocId(answer[0].getDocId_3());
				judgedDoc3.setWorkerId(asn.getWorkerId());
				judgedDoc3.setTime(String.valueOf(time));
				judgedDoc3.setTrecScore(answer[0].getScore_3());
				judgedDoc3.setWorkerScore(answer[0].getDoc3WorkerScore());
				judgedDocuments.add(judgedDoc3);

				JudgedDocumentObject judgedDoc4 = new JudgedDocumentObject();
				judgedDoc4.setDocId(answer[0].getDocId_4());
				judgedDoc4.setWorkerId(asn.getWorkerId());
				judgedDoc4.setTime(String.valueOf(time));
				judgedDoc4.setTrecScore(answer[0].getScore_4());
				judgedDoc4.setWorkerScore(answer[0].getDoc4WorkerScore());
				judgedDocuments.add(judgedDoc4);

				JudgedDocumentObject judgedDoc5 = new JudgedDocumentObject();
				judgedDoc5.setDocId(answer[0].getDocId_5());
				judgedDoc5.setWorkerId(asn.getWorkerId());
				judgedDoc5.setTime(String.valueOf(time));
				judgedDoc5.setTrecScore(answer[0].getScore_5());
				judgedDoc5.setWorkerScore(answer[0].getDoc5WorkerScore());
				judgedDocuments.add(judgedDoc5);
			}
			System.out.println("Created Judged Documents");
			// Approve the assignment
			ApproveAssignmentRequest approveRequest = new ApproveAssignmentRequest();
			approveRequest.setAssignmentId(asn.getAssignmentId());
			approveRequest.setRequesterFeedback("Good work, thank you!");
			approveRequest.setOverrideRejection(false);
			client.approveAssignment(approveRequest);
			System.out.println("Assignment has been approved: " + asn.getAssignmentId());
		}

//		DeleteHITRequest deleteHITRequest = new DeleteHITRequest();
//		deleteHITRequest.setHITId(hitId);
//		client.deleteHIT(deleteHITRequest);
//		System.out.println("Deleted: " + hitId);
	}
}