package org.lemurproject.mturkadmin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.GetHITRequest;
import com.amazonaws.services.mturk.model.GetHITResult;
import com.amazonaws.services.mturk.model.HIT;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;

@Component
public class ListHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void listHITs() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		Scanner scanner = new Scanner(new File(properties.getHit2topicFilename()));
		if (scanner.hasNext()) {
			scanner.next();
		}

		// for (HIT hit : listHitResult.getHITs()) {
		while (scanner.hasNext()) {
			// String hitId = hit.getHITId();
			String nextLine = scanner.next();
			String hitId = nextLine.substring(0, nextLine.indexOf(","));

			try {

				GetHITRequest getHITRequest = new GetHITRequest();
				getHITRequest.setHITId(hitId);
				GetHITResult hitResult = client.getHIT(getHITRequest);
				HIT hit = hitResult.getHIT();
				// System.out.println("HIT with ID: " + hit.getHITId() + " has status: " +
				// hit.getHITStatus());

				if (hit.getHITStatus().equalsIgnoreCase("Assignable")) {
					System.out.println("HIT with ID: " + hit.getHITId() + " has status: " + hit.getHITStatus());

					ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
					listHITRequest.setHITId(hitId);
					// listHITRequest.setAssignmentStatuses(Collections.singletonList(AssignmentStatus.Submitted.name()));

					// Get a maximum of 10 completed assignments for this HIT
					listHITRequest.setMaxResults(100);
					ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
					List<Assignment> assignmentList = listHITResult.getAssignments();

					// Iterate through all the assignments received
					for (Assignment asn : assignmentList) {
						System.out.println("Assignment has statusL " + asn.getAssignmentStatus());
					}
				}
			} catch (Exception e) {
				System.out.println("No HIT with ID: " + hitId);
			}

		}
		scanner.close();
	}

}
