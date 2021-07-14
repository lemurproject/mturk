package org.lemurproject.mturkadmin;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.AcceptQualificationRequestRequest;

@Component
public class AcceptMTurkQualificationResults {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void acceptQualificationRequests() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		AcceptQualificationRequestRequest acceptQual = new AcceptQualificationRequestRequest();
		acceptQual.setQualificationRequestId("3IS5JR8S1V5SEI9T3GN3VQ0SC8QJLC");
	}

}
