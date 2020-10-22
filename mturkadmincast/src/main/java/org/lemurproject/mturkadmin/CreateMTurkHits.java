package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	public void createHits() throws IOException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		Reader reader = Files.newBufferedReader(Paths.get(properties.getDataFilename()));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();
		// hitIterator.next();

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

		String topic = "";
		while (hitIterator.hasNext()) {
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
			// request.setAssignmentDurationInSeconds(60l);
			// Reward is a USD dollar amount - USD$0.20 in the example below
			request.setReward("0.45");
			topic = hitObject.getQueryNum().substring(0, hitObject.getQueryNum().indexOf("_"));
			request.setTitle("How relevant is the document to the question/conversation?  (Topic: "
					+ properties.getTopicName() + ")");
			request.setKeywords("document, relevance");
			request.setDescription(
					"Given the conversation, how relevant is the given document to the last topic of the conversation? (Topic: "
							+ properties.getTopicName() + ")");
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
			hitDataWriter.write(String.join("", result.getHIT().getHITId(), ",", topic, "\n"));

//			Map<String, String> parameters = new HashMap<>();
//			parameters.put("topic", topic);
//			parameters.put("hitId", result.getHIT().getHITId());
//			parameters.put("hitMapFile", properties.getHit2topicFilename());
//			String parameterString = getParamsString(parameters);

//			HttpGet addHitRequest = new HttpGet(String.join("?", properties.getAddTopicsUrl(), parameterString));
//			try (CloseableHttpResponse response = httpClient.execute(addHitRequest)) {
//				System.out.println("Successfully added HIT: " + result.getHIT().getHITId());
//			} catch (Exception e) {
//				System.out.println("Failed adding HIT: " + result.getHIT().getHITId());
//			}

		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put("topic", topic);
		parameters.put("topicName", properties.getTopicName());
		String parameterString = getParamsString(parameters);
		HttpGet addTopicRequest = new HttpGet(String.join("?", properties.getAddTopicNameUrl(), parameterString));
		try (CloseableHttpResponse response = httpClient.execute(addTopicRequest)) {
			System.out.println("Successfully added topic: " + topic + " - " + properties.getTopicName());
		} catch (Exception e) {
			System.out.println("Failed adding HIT: " + topic + " - " + properties.getTopicName());
		}

		hitDataWriter.close();
		httpClient.close();

	}

	public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}

		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}

}
