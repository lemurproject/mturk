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
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.Comparator;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;
import com.amazonaws.services.mturk.model.QualificationRequirement;

@Component
public class CreateMTurkHits {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkFilenameHelper filenameHelper;

	public void createHits(MTurkProperties properties) throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		List<QualificationRequirement> qualifications = new ArrayList<QualificationRequirement>();

//		QualificationRequirement localeRequirement = new QualificationRequirement();
//		localeRequirement.setQualificationTypeId("00000000000000000071");
//		localeRequirement.setComparator(Comparator.In);
//		List<Locale> localeValues = new ArrayList<>();
//		localeValues.add(new Locale().withCountry("US"));
//		localeValues.add(new Locale().withCountry("CA"));
//		localeValues.add(new Locale().withCountry("GB"));
//		localeValues.add(new Locale().withCountry("AU"));
////		localeValues.add(new Locale().withCountry("IN"));
//		localeRequirement.setLocaleValues(localeValues);
//		qualifications.add(localeRequirement);

		QualificationRequirement approvalRateRequirement = new QualificationRequirement();
		approvalRateRequirement.setQualificationTypeId("000000000000000000L0");
		approvalRateRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
		approvalRateRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(90)));
		qualifications.add(approvalRateRequirement);

		QualificationRequirement testRequirement = new QualificationRequirement();
		testRequirement.setQualificationTypeId(properties.getQualificationType());
		testRequirement.setComparator(Comparator.GreaterThanOrEqualTo);
		List<Integer> integerValues = new ArrayList<Integer>();
		integerValues.add(Integer.valueOf(properties.getQualificationScore()));
		testRequirement.setIntegerValues(integerValues);
		qualifications.add(testRequirement);

		LocalDateTime date = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

		String hitFilename = filenameHelper.getHitFilename();
		Writer hitDataWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hitFilename), "UTF8"));
		// hitDataWriter.write("hitId\n");
		System.out.println(formatter.format(date));

		String url = "";
		int numHits = Integer.valueOf(properties.getHitNumber()).intValue();
		int numAssignments = Integer.valueOf(properties.getHitAssignments()).intValue();
		for (int i = 0; i < numHits; i++) {

			CreateHITRequest request = new CreateHITRequest();
			// Read the question XML into a String
			String questionSample = new String(Files.readAllBytes(Paths.get(properties.getQuestionFilename())));
			request.setQuestion(questionSample);

			request.setMaxAssignments(numAssignments);
			int hitHours = Integer.valueOf(properties.getHitLifetimeHours()).intValue();
			long lifetime = 60 * 60L * hitHours;
			request.setLifetimeInSeconds(lifetime);
			long duration = 60 * 20L;
			request.setAssignmentDurationInSeconds(duration);
			// Reward is a USD dollar amount - USD$0.20 in the example below
			request.setReward(properties.getHitPrice());
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
			date = LocalDateTime.now();
			hitDataWriter.write(String.join("", result.getHIT().getHITId(), ",", formatter.format(date), ",",
					properties.getQualificationType(), "\n"));
			System.out.println(String.join("", "HIT Id: ", result.getHIT().getHITId(), "\n"));
		}
		System.out.println(String.join(": ", "url: ", url));
		System.out.println(formatter.format(date));

		hitDataWriter.close();

	}

}
