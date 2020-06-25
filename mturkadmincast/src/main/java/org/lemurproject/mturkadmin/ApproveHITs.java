package org.lemurproject.mturkadmin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.ApproveAssignmentRequest;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.AssignmentStatus;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;

@Component
public class ApproveHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void approveAssignments()
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

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

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(10);
			ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
			List<Assignment> assignmentList = listHITResult.getAssignments();

			for (Assignment asn : assignmentList) {
				// Approve the assignment
				ApproveAssignmentRequest approveRequest = new ApproveAssignmentRequest();
				approveRequest.setAssignmentId(asn.getAssignmentId());
				approveRequest.setRequesterFeedback("Good work, thank you!");
				approveRequest.setOverrideRejection(false);
				client.approveAssignment(approveRequest);
				System.out.println("Assignment has been approved: " + asn.getAssignmentId());
			}
		}
		scanner.close();
	}

}
