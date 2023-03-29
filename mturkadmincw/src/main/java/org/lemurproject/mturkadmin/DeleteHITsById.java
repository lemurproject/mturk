package org.lemurproject.mturkadmin;

import java.io.File;
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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
import com.amazonaws.services.mturk.model.ApproveAssignmentRequest;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.DeleteHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;
import com.amazonaws.services.mturk.model.UpdateExpirationForHITRequest;

/* 
 * Before connecting to MTurk, set up your AWS account and IAM settings as described here:
 * https://blog.mturk.com/how-to-use-iam-to-control-api-access-to-your-mturk-account-76fe2c2e66e2
 * 
 * Configure your AWS credentials as described here:
 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
 *
 */
@Component
public class DeleteHITsById {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkFilenameHelper filenameHelper;

	public void deleteHITsById(MTurkProperties properties)
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Scanner scanner = new Scanner(new File(filenameHelper.getHitFilename()));
		while (scanner.hasNext()) {
			String hitLine = scanner.next();
			String hitId = hitLine.split(",")[0];
			System.out.println("HIT Id: " + hitId);

			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(100);
			ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
			List<Assignment> assignmentList = listHITResult.getAssignments();

			// Iterate through all the assignments received
			String assignmentStatus = "";
			for (Assignment asn : assignmentList) {
				System.out.println("  assignment: " + asn.getAssignmentId() + " - " + asn.getAssignmentStatus());
				assignmentStatus = asn.getAssignmentStatus();

				if (assignmentStatus.equalsIgnoreCase("Submitted")) {
					Document doc = dBuilder.parse(new InputSource(new StringReader(asn.getAnswer())));
					NodeList nList = doc.getElementsByTagName("Answer");
					Node nNode = nList.item(0);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						String jsonAnswer = eElement.getElementsByTagName("FreeText").item(0).getTextContent();
						System.out.println(jsonAnswer);
					}
					ApproveAssignmentRequest approveRequest = new ApproveAssignmentRequest();
					approveRequest.setAssignmentId(asn.getAssignmentId());
					approveRequest.setRequesterFeedback("Good work, thank you!");
					approveRequest.setOverrideRejection(false);
					client.approveAssignment(approveRequest);
					System.out.println("Assignment has been approved: " + asn.getAssignmentId());

				}
			}
			try {
				UpdateExpirationForHITRequest updateHITRequest = new UpdateExpirationForHITRequest();
				updateHITRequest.setHITId(hitId);
				// updateHITRequest.setExpireAt(new Date(0l));
				Date date = new GregorianCalendar(2020, 11, 30).getTime();
				updateHITRequest.setExpireAt(date);
				client.updateExpirationForHIT(updateHITRequest);
				System.out.println("Expired HIT: " + hitId);

				DeleteHITRequest deleteHITRequest = new DeleteHITRequest();
				deleteHITRequest.setHITId(hitId);
				client.deleteHIT(deleteHITRequest);
				System.out.println("Deleted: " + hitId);
			} catch (Exception e) {
				System.out.println("Could not delte HIT: " + hitId);
			}
		}
	}

}