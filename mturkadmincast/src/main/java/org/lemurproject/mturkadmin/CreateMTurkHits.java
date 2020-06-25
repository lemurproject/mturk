package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.Comparator;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;
import com.amazonaws.services.mturk.model.HITAccessActions;
import com.amazonaws.services.mturk.model.QualificationRequirement;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Component
public class CreateMTurkHits {

	@Autowired
	private MTurkProperties properties;

	@Autowired
	private MTurkClientHelper clientHelper;

	public void createHits() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		Reader reader = Files.newBufferedReader(Paths.get(properties.getDataFilename()));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();
		// hitIterator.next();

		Writer topicUrlWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(properties.getTopic2urlFilename()), "UTF8"));
		Writer hitDataWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(properties.getHit2topicFilename()), "UTF8"));
		hitDataWriter.write(String.join("", "hitId", ",", "dataRowId", "\n"));

		List<QualificationRequirement> qualifications = new ArrayList<QualificationRequirement>();

		QualificationRequirement workerRequirement = new QualificationRequirement();
		workerRequirement.setQualificationTypeId(properties.getQualificationType());
		workerRequirement.setComparator(Comparator.EqualTo);
		workerRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(1)));
		workerRequirement.setActionsGuarded(HITAccessActions.DiscoverPreviewAndAccept);
		qualifications.add(workerRequirement);

		Map<String, String> topicToUrlMap = new HashMap<String, String>();
		int numHits = 0;
		while (hitIterator.hasNext()) {
			numHits++;
			HITObject hitObject = hitIterator.next();

			CreateHITRequest request = new CreateHITRequest();
			String questionSample = new String(Files.readAllBytes(Paths.get(properties.getQuestionFilename())));
			request.setQuestion(questionSample);

			request.setMaxAssignments(1);
			long lifetime = Long.valueOf(properties.getHitLifetime()).longValue();
			// long lifetime = 60 * 60L * 24 * 28;
			// long lifetime = 60 * 60L * 2;
			request.setLifetimeInSeconds(lifetime);
			request.setAssignmentDurationInSeconds(900l);
			// Reward is a USD dollar amount - USD$0.20 in the example below
			request.setReward("0.45");
			String topic = hitObject.getQueryNum().substring(0, hitObject.getQueryNum().indexOf("_"));
			request.setTitle("How relevant is the document to the question/conversation?  (Topic Id: " + topic + ")");
			request.setKeywords("document, relevance");
			request.setDescription(
					"Given the conversation, how relevant is the given document to the last topic of the conversation? (Topic Id: "
							+ topic + ")");
			request.setQualificationRequirements(qualifications);

			CreateHITResult result = client.createHIT(request);

			String url = String.join("", "https://workersandbox.mturk.com/mturk/preview?groupId=",
					result.getHIT().getHITTypeId());
			if (properties.getEnvironment().equalsIgnoreCase("prod")
					|| properties.getEnvironment().equalsIgnoreCase("production")) {
				url = String.join("", "https://worker.mturk.com/mturk/preview?groupId=",
						result.getHIT().getHITTypeId());
			}
			System.out.println(String.join(": ", topic, url));
			topicToUrlMap.put(topic, url);
			hitDataWriter.write(String.join("", result.getHIT().getHITId(), ",", topic, "\n"));
		}

		topicToUrlMap.forEach((topic, url) -> {
			try {
				topicUrlWriter.write(String.join("", topic, ",", url, "\n"));
			} catch (IOException e) {
				System.out.println("Could not write to topic url csv");
			}
		});
		topicUrlWriter.close();
		hitDataWriter.close();

		String addTopicsUrl = properties.getAddTopicsUrl();
		String urlParameters = String.join("", "hitMapFile=", properties.getHit2topicFilename(), "&hitCsvFile=",
				properties.getDataFilename());
		System.out.println(String.join("?", addTopicsUrl, urlParameters));
		URL url = new URL(String.join("?", addTopicsUrl, urlParameters));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

	}

}
