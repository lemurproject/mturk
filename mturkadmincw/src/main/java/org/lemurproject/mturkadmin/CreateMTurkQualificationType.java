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
	private MTurkClientHelper clientHelper;

	public String createQualification(MTurkProperties properties) throws IOException {
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

		return qualType.getQualificationTypeId();
	}

}
