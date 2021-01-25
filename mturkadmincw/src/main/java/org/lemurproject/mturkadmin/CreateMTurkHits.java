package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;

@Component
public class CreateMTurkHits {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void createHits() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

//		List<QualificationRequirement> qualifications = new ArrayList<QualificationRequirement>();
//
//		QualificationRequirement workerRequirement = new QualificationRequirement();
//		workerRequirement.setQualificationTypeId(properties.getQualificationType());
//		workerRequirement.setComparator(Comparator.EqualTo);
//		workerRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(1)));
//		workerRequirement.setActionsGuarded(HITAccessActions.DiscoverPreviewAndAccept);
//		qualifications.add(workerRequirement);

		Writer hitDataWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(properties.getHitFilename()), "UTF8"));
		hitDataWriter.write("hitId\n");

		String url = "";
		for (int i = 0; i < 10; i++) {

			CreateHITRequest request = new CreateHITRequest();
			// Read the question XML into a String
			String questionSample = new String(Files.readAllBytes(Paths.get(properties.getQuestionFilename())));
			request.setQuestion(questionSample);

			request.setMaxAssignments(20);
			long lifetime = 60 * 60L * 24 * 60;
			request.setLifetimeInSeconds(lifetime);
			long duration = 60 * 20L;
			request.setAssignmentDurationInSeconds(duration);
			// Reward is a USD dollar amount - USD$0.20 in the example below
			request.setReward("0.15");
			request.setTitle("How relevant are the document to your query?");
			request.setKeywords("document, relevance");
			request.setDescription("After performing a search, how relevant is the list of documents to your search?");
			// request.setQualificationRequirements(qualifications);

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

		hitDataWriter.close();

	}

}
