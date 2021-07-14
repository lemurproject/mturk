package org.lemurproject.mturkadmin;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.ListQualificationRequestsRequest;
import com.amazonaws.services.mturk.model.ListQualificationRequestsResult;
import com.amazonaws.services.mturk.model.QualificationRequest;

@Component
public class GetMTurkQualificationResults {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void getQualificationRequests() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		ListQualificationRequestsRequest qualRequests = new ListQualificationRequestsRequest();
		qualRequests.setQualificationTypeId("3NDLUB5I85KL3NW4SD801LS28RN46N");
		ListQualificationRequestsResult result = client.listQualificationRequests(qualRequests);

		for (QualificationRequest qualRequest : result.getQualificationRequests()) {
			System.out.println(qualRequest.getQualificationRequestId());
			// TODO: Print query and calculate score on pre-filled query
			System.out.println(qualRequest.getAnswer());
		}
	}

}
