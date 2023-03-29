package org.lemurproject.mturkadmin;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.HIT;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;
import com.amazonaws.services.mturk.model.ListHITsRequest;
import com.amazonaws.services.mturk.model.ListHITsResult;

@Component
public class GetMTurkApprovedHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	public void getAllApprovedHITs(MTurkProperties properties) throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		ListHITsRequest listHitsReq = new ListHITsRequest();
		listHitsReq.setMaxResults(100);
		ListHITsResult hitsResult = client.listHITs(listHitsReq);
		for (HIT hit : hitsResult.getHITs()) {
			String hitId = hit.getHITId();
			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(100);
			ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
			List<Assignment> assignmentList = listHITResult.getAssignments();

			// Iterate through all the assignments received
			for (Assignment asn : assignmentList) {
				if (asn.getAssignmentStatus().equalsIgnoreCase("Approved")) {
					System.out.println(hitId + ", " + asn.getAssignmentId());
				}
			}
		}
		String nextToken = hitsResult.getNextToken();
		while (nextToken != null) {
			listHitsReq.setNextToken(nextToken);
			ListHITsResult hitsResult2 = client.listHITs(listHitsReq);
			for (HIT hit : hitsResult2.getHITs()) {
				String hitId = hit.getHITId();
				ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
				listHITRequest.setHITId(hitId);

				// Get a maximum of 10 completed assignments for this HIT
				listHITRequest.setMaxResults(100);
				ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
				List<Assignment> assignmentList = listHITResult.getAssignments();

				// Iterate through all the assignments received
				for (Assignment asn : assignmentList) {
					if (asn.getAssignmentStatus().equalsIgnoreCase("Approved")) {
						System.out.println(hitId + ", " + asn.getAssignmentId());
					}
				}
			}
		}

	}

}
