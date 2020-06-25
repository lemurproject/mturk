package org.lemurproject.mturk;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
/*
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;
import com.amazonaws.services.mturk.model.Comparator;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;
import com.amazonaws.services.mturk.model.HITAccessActions;
import com.amazonaws.services.mturk.model.QualificationRequirement;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

/* 
 * Before connecting to MTurk, set up your AWS account and IAM settings as described here:
 * https://blog.mturk.com/how-to-use-iam-to-control-api-access-to-your-mturk-account-76fe2c2e66e2
 * 
 * Configure your AWS credentials as described here:
 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
 *
 */

public class CreateHITSample {

	private static final String QUESTION_XML_FILE_NAME = "externalquestion.xml";

	private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
	private static final String PROD_ENDPOINT = "https://mturk-requester.us-east-1.amazonaws.com";
	private static final String SIGNING_REGION = "us-east-1";

	public static void main(final String[] argv) throws IOException {
		/*
		 * Use the Amazon Mechanical Turk Sandbox to publish test Human Intelligence
		 * Tasks (HITs) without paying any money. Sign up for a Sandbox account at
		 * https://requestersandbox.mturk.com/ with the same credentials as your main
		 * MTurk account
		 * 
		 * Switch to getProdClient() in production. Uncomment line 60, 61, & 66 below to
		 * create your HIT in production.
		 * 
		 */

		final CreateHITSample sandboxApp = new CreateHITSample(getSandboxClient());
		sandboxApp.createHIT(QUESTION_XML_FILE_NAME);

		// final CreateHITSample prodApp = new CreateHITSample(getProdClient());
		// final HITInfo hitInfo = prodApp.createHIT(QUESTION_XML_FILE_NAME);

		// System.out.println("Your HIT has been created. You can see it at this
		// link:");

		// System.out.println("https://workersandbox.mturk.com/mturk/preview?groupId=" +
		// hitInfo.getHITTypeId());
		// System.out.println("https://www.mturk.com/mturk/preview?groupId=" +
		// hitInfo.getHITTypeId());

		// System.out.println("Your HIT ID is: " + hitInfo.getHITId());
	}

	private final AmazonMTurk client;

	private CreateHITSample(final AmazonMTurk client) {
		this.client = client;
	}

	private static AmazonMTurk getSandboxClient() {
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

	private static AmazonMTurk getProdClient() {
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(PROD_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

	private static final class HITInfo {
		private final String hitId;
		private final String hitTypeId;

		private HITInfo(final String hitId, final String hitTypeId) {
			this.hitId = hitId;
			this.hitTypeId = hitTypeId;
		}

		private String getHITId() {
			return this.hitId;
		}

		private String getHITTypeId() {
			return this.hitTypeId;
		}
	}

	private void createHIT(final String questionXmlFile) throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get("mturk_cast_32.csv"));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();
		// hitIterator.next();

		Writer topicUrlWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("topic_url.csv"), "UTF8"));
		Writer hitDataWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("hit_data.csv"), "UTF8"));
		hitDataWriter.write(String.join("", "hitId", ",", "dataRowId", "\n"));

		List<QualificationRequirement> qualifications = new ArrayList<QualificationRequirement>();

		QualificationRequirement workerRequirement = new QualificationRequirement();
		workerRequirement.setQualificationTypeId("3R5PEB0CKNUG8TUGU2G79JPXVDXO9I");
		workerRequirement.setComparator(Comparator.EqualTo);
		workerRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(1)));
		workerRequirement.setActionsGuarded(HITAccessActions.DiscoverPreviewAndAccept);
		qualifications.add(workerRequirement);

		Map<String, String> topicToUrlMap = new HashMap<String, String>();
		int hitNum = 0;
		while (hitIterator.hasNext() && hitNum < 100) {
			hitNum++;
			HITObject hitObject = hitIterator.next();

			CreateHITRequest request = new CreateHITRequest();
			// Read the question XML into a String
			String questionSample = new String(Files.readAllBytes(Paths.get(questionXmlFile)));
			request.setQuestion(questionSample);

			request.setMaxAssignments(1000);
			request.setLifetimeInSeconds(60 * 30L);
			request.setAssignmentDurationInSeconds(60 * 5L);
			// Reward is a USD dollar amount - USD$0.20 in the example below
			request.setReward("0.50");
			String topic = hitObject.getQueryNum().substring(0, hitObject.getQueryNum().indexOf("_"));
			request.setTitle("How relevant is the document to the question/conversation?  (Topid id: " + topic + ")");
			request.setKeywords("document, relevance");
			request.setDescription(
					"Given the conversation, how relevant is the given document to the last topic of the conversation? (Topid id: "
							+ topic + ")");
			request.setQualificationRequirements(qualifications);

			CreateHITResult result = client.createHIT(request);

			String url = String.join("", "https://workersandbox.mturk.com/mturk/preview?groupId=",
					result.getHIT().getHITTypeId());
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
	}

}
