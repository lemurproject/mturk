package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.Comparator;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;
import com.amazonaws.services.mturk.model.Locale;
import com.amazonaws.services.mturk.model.QualificationRequirement;

@Component
public class CreateMTurkHits {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void createHits() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		List<QualificationRequirement> qualifications = new ArrayList<QualificationRequirement>();

		QualificationRequirement localeRequirement = new QualificationRequirement();
		localeRequirement.setQualificationTypeId("00000000000000000071");
		localeRequirement.setComparator(Comparator.In);
		List<Locale> localeValues = new ArrayList<>();
		localeValues.add(new Locale().withCountry("US"));
		localeRequirement.setLocaleValues(localeValues);
		qualifications.add(localeRequirement);

		QualificationRequirement testRequirement = new QualificationRequirement();
		testRequirement.setQualificationTypeId(properties.getQualificationType());
		testRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
		List<Integer> integerValues = new ArrayList<Integer>();
		integerValues.add(Integer.valueOf(2));
		testRequirement.setIntegerValues(integerValues);
		qualifications.add(testRequirement);

//		QualificationRequirement testRequirement2 = new QualificationRequirement();
//		testRequirement.setQualificationTypeId("33DXTGLH9KH8VXV57V2QXGRDPWIP3U");
//		testRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
//		List<Integer> integerValues2 = new ArrayList<Integer>();
//		integerValues2.add(Integer.valueOf(27));
//		testRequirement2.setIntegerValues(integerValues2);
//		qualifications.add(testRequirement2);

		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

		Writer hitDataWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(properties.getHitFilename()), "UTF8"));
		hitDataWriter.write("hitId\n");
		System.out.println(formatter.format(date));

		String url = "";
		for (int i = 0; i < 20; i++) {

			CreateHITRequest request = new CreateHITRequest();
			// Read the question XML into a String
			String questionSample = new String(Files.readAllBytes(Paths.get(properties.getQuestionFilename())));
			request.setQuestion(questionSample);

			request.setMaxAssignments(5);
			long lifetime = 60 * 60L * 24;
			request.setLifetimeInSeconds(lifetime);
			long duration = 60 * 20L;
			request.setAssignmentDurationInSeconds(duration);
			// Reward is a USD dollar amount - USD$0.20 in the example below
			request.setReward("0.40");
			request.setTitle("Enter a search topic and select the best results (~2 minutes)");
			request.setKeywords("search, document, relevance");
			request.setDescription("After performing a search, how relevant is the list of results to your search?");
			request.setQualificationRequirements(qualifications);

			CreateHITResult result = client.createHIT(request);

			url = String.join("", "https://workersandbox.mturk.com/mturk/preview?groupId=",
					result.getHIT().getHITTypeId());
			if (properties.getEnvironment().equalsIgnoreCase("prod")
					|| properties.getEnvironment().equalsIgnoreCase("production")) {
				url = String.join("", "https://worker.mturk.com/mturk/preview?groupId=",
						result.getHIT().getHITTypeId());
			}
			hitDataWriter.write(String.join("", result.getHIT().getHITId(), "\n"));
			System.out.println(String.join("", result.getHIT().getHITId(), "\n"));
		}
		System.out.println(String.join(": ", "url: ", url));
		System.out.println(formatter.format(date));

		hitDataWriter.close();

	}

}
