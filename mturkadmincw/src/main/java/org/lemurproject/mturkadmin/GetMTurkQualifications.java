package org.lemurproject.mturkadmin;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.ListWorkersWithQualificationTypeRequest;
import com.amazonaws.services.mturk.model.ListWorkersWithQualificationTypeResult;
import com.amazonaws.services.mturk.model.Qualification;

@Component
public class GetMTurkQualifications {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void getQualifications() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		// List workers that qualify
		ListWorkersWithQualificationTypeRequest workerReq = new ListWorkersWithQualificationTypeRequest();
		workerReq.setQualificationTypeId(properties.getQualificationType());
		workerReq.setMaxResults(100);
		ListWorkersWithQualificationTypeResult workers = client.listWorkersWithQualificationType(workerReq);
		int totalNum = 0;
		int numQual = 0;
		int numGreaterThanZero = 0;
		for (Qualification qualif : workers.getQualifications()) {
			System.out.println(qualif.getIntegerValue());
			totalNum++;
			if (qualif.getIntegerValue().intValue() >= 5) {
				numQual++;
			}
			if (qualif.getIntegerValue().intValue() >= 0) {
				numGreaterThanZero++;
			}
		}
		String nextToken = workers.getNextToken();
		while (nextToken != null) {
			workerReq.setNextToken(nextToken);
			ListWorkersWithQualificationTypeResult workers2 = client.listWorkersWithQualificationType(workerReq);
			for (Qualification qualif : workers2.getQualifications()) {
				System.out.println(qualif.getIntegerValue());
				totalNum++;
				if (qualif.getIntegerValue().intValue() >= 5) {
					numQual++;
				}
				if (qualif.getIntegerValue().intValue() >= 0) {
					numGreaterThanZero++;
				}
			}
			nextToken = workers2.getNextToken();
		}
		System.out.println("Number Qualified: " + numQual);
		System.out.println("Number greater than zero: " + numGreaterThanZero);
		System.out.println("Total: " + totalNum);

	}

}
