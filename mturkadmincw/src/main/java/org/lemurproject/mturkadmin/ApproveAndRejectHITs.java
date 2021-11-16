package org.lemurproject.mturkadmin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.ApproveAssignmentRequest;
import com.amazonaws.services.mturk.model.GetAssignmentRequest;
import com.amazonaws.services.mturk.model.GetAssignmentResult;
import com.amazonaws.services.mturk.model.RejectAssignmentRequest;

@Component
public class ApproveAndRejectHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkFilenameHelper filenameHelper;

	public void approveAssignments(MTurkProperties properties)
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		BufferedReader br = new BufferedReader(new FileReader(filenameHelper.getShortJudgedDocFilename()));
		String nextLine = br.readLine();
		while ((nextLine = br.readLine()) != null) {

			String[] lineParts = nextLine.split(",");
			String assignmentId = lineParts[1];
			String acceptString = lineParts[7];
			boolean reject = false;
			if (acceptString.equalsIgnoreCase("r")) {
				reject = true;
			}

			GetAssignmentRequest getAssignment = new GetAssignmentRequest();
			getAssignment.setAssignmentId(assignmentId);
			GetAssignmentResult result = client.getAssignment(getAssignment);

			if (result.getAssignment().getAssignmentStatus().equalsIgnoreCase("Submitted")) {
				if (reject) {
					RejectAssignmentRequest rejectRequest = new RejectAssignmentRequest();
					rejectRequest.setAssignmentId(assignmentId);
					rejectRequest.setRequesterFeedback(
							"Rejected, the same query was entered multiple times and matched the qualifying query.");
					client.rejectAssignment(rejectRequest);
					System.out.println("Assignment has been rejected: " + assignmentId);
				} else {
					ApproveAssignmentRequest approveRequest = new ApproveAssignmentRequest();
					approveRequest.setAssignmentId(assignmentId);
					approveRequest.setRequesterFeedback("Good work, thank you!");
					approveRequest.setOverrideRejection(false);
					client.approveAssignment(approveRequest);
					System.out.println("Assignment has been approved: " + assignmentId);
				}
			}
		}
		br.close();
	}

}
