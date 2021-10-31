package org.lemurproject.mturkadmin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.CreateQualificationTypeRequest;
import com.amazonaws.services.mturk.model.CreateQualificationTypeResult;
import com.amazonaws.services.mturk.model.QualificationType;

@Component
public class CreateMTurkQualificationType {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void createQualification() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		CreateQualificationTypeRequest createQualificationType = new CreateQualificationTypeRequest();
		createQualificationType.setName(properties.getQualificationName());
		createQualificationType.setKeywords("document, relevance");
		createQualificationType.setDescription("How relevant is the list of documents the given search?");
		createQualificationType.setQualificationTypeStatus("Active");
		String questionSample = new String(Files.readAllBytes(Paths.get(properties.getQualificationFilename())));
		createQualificationType.setTest(questionSample);
		String answerSample = new String(Files.readAllBytes(Paths.get(properties.getQualificationAnswerkey())));
		createQualificationType.setAnswerKey(answerSample);
		createQualificationType.setTestDurationInSeconds(1200L);

		CreateQualificationTypeResult result = client.createQualificationType(createQualificationType);
		QualificationType qualType = result.getQualificationType();
		System.out.println(qualType.getQualificationTypeId());

		// List workers that qualify
//		ListWorkersWithQualificationTypeRequest workerReq = new ListWorkersWithQualificationTypeRequest();
//		workerReq.setQualificationTypeId("3C2W12C2LKED64O3Z764CUY6UPQURI");
//		workerReq.setMaxResults(100);
//		ListWorkersWithQualificationTypeResult workers = client.listWorkersWithQualificationType(workerReq);
//		int totalNum = 0;
//		int numQual = 0;
//		int numGreaterThanZero = 0;
//		for (Qualification qualif : workers.getQualifications()) {
//			System.out.println(qualif.getIntegerValue());
//			totalNum++;
//			if (qualif.getIntegerValue().intValue() >= 1) {
//				numQual++;
//			}
//			if (qualif.getIntegerValue().intValue() >= 0) {
//				numGreaterThanZero++;
//			}
//		}
//		String nextToken = workers.getNextToken();
//		while (nextToken != null) {
//			workerReq.setNextToken(nextToken);
//			ListWorkersWithQualificationTypeResult workers2 = client.listWorkersWithQualificationType(workerReq);
//			for (Qualification qualif : workers2.getQualifications()) {
//				System.out.println(qualif.getIntegerValue());
//				totalNum++;
//				if (qualif.getIntegerValue().intValue() >= 1) {
//					numQual++;
//				}
//				if (qualif.getIntegerValue().intValue() >= 0) {
//					numGreaterThanZero++;
//				}
//			}
//			nextToken = workers2.getNextToken();
//		}
//		System.out.println("Number Qualified: " + numQual);
//		System.out.println("Number greater than zero: " + numGreaterThanZero);
//		System.out.println("Total: " + totalNum);

	}

}
