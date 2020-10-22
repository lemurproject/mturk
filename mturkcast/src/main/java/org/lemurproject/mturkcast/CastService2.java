package org.lemurproject.mturkcast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;
import com.amazonaws.services.mturk.model.Comparator;
import com.amazonaws.services.mturk.model.CreateHITRequest;
import com.amazonaws.services.mturk.model.CreateHITResult;
import com.amazonaws.services.mturk.model.GetHITRequest;
import com.amazonaws.services.mturk.model.GetHITResult;
import com.amazonaws.services.mturk.model.HIT;
import com.amazonaws.services.mturk.model.HITAccessActions;
import com.amazonaws.services.mturk.model.HITStatus;
import com.amazonaws.services.mturk.model.QualificationRequirement;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Component
public class CastService2 {

	@Autowired
	private DataProperties dataProperties;

	private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
	private static final String PROD_ENDPOINT = "https://mturk-requester.us-east-1.amazonaws.com";
	private static final String SIGNING_REGION = "us-east-1";
	private AmazonMTurk client;

	private Writer seenWriter;
	private Writer hitWriter;
	// private Writer stateWriter;

	private Map<String, String> topicNameMap;
	private Map<String, List<HITObject>> topicToDataList;
	private Map<String, Map<String, Integer>> userQueues;
	private Map<String, Map<String, List<HITSimpleObject>>> userPrevHits;
	private Map<String, Map<String, List<HITSimpleObject>>> userMissedHits;
	private Map<String, String> hitIdToTopic;
	private HITObject sampleHIT;
	private Map<String, List<QualificationRequirement>> qualificationMap;
	// private List<QualificationRequirement> qualifications;

	public HITObject getSampleData() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		return sampleHIT;
	}

	public HITObject getNextHIT(String hitId, String workerId)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String topic = hitIdToTopic.get(hitId);
		if (topic == null) {
			System.out.println("No data loaded for hitId: " + hitId);
			return null;
		}
		if (workerId == null) {
			System.out.println("Worker ID is null");
			return null;
		}
		userQueues.putIfAbsent(workerId, new HashMap<String, Integer>());
		userQueues.get(workerId).putIfAbsent(topic, Integer.valueOf(0));

		userPrevHits.putIfAbsent(workerId, new HashMap<String, List<HITSimpleObject>>());
		userPrevHits.get(workerId).putIfAbsent(topic, new ArrayList<HITSimpleObject>());

		userMissedHits.putIfAbsent(workerId, new HashMap<String, List<HITSimpleObject>>());
		userMissedHits.get(workerId).putIfAbsent(topic, new ArrayList<HITSimpleObject>());

		// Check if previous HIT(s) was submitted
		if (userPrevHits.get(workerId) != null && userPrevHits.get(workerId).get(topic) != null
				&& userPrevHits.get(workerId).get(topic).size() > 0) {
			List<HITSimpleObject> hitList = userPrevHits.get(workerId).get(topic);
			for (int i = userPrevHits.get(workerId).get(topic).size() - 1; i >= 0; i--) {
				HITSimpleObject prevHit = hitList.get(i);
				boolean isAssignable = checkHITStatus(workerId, topic, prevHit);
				if (isAssignable) {
					userMissedHits.get(workerId).get(topic).add(prevHit);
					System.out.println("Adding missed HIT (" + prevHit.toString() + ")");
				}
			}
		}

		HITObject hitObject = null;

		if (userQueues.get(workerId).get(topic) != null && topicToDataList.get(topic).size() > 0) {
			int hitIndex = userQueues.get(workerId).get(topic).intValue();

			// Display any missed HITs
			if (userMissedHits.get(workerId) != null && userMissedHits.get(workerId).get(topic) != null
					&& userMissedHits.get(workerId).get(topic).size() > 0) {
				int missedHITIndex = Integer.valueOf(userMissedHits.get(workerId).get(topic).get(0).getHitId())
						.intValue();
				hitObject = topicToDataList.get(topic).get(missedHITIndex);
				hitObject.setHitCount("Previous unsubmitted HIT");
				userMissedHits.get(workerId).get(topic).remove(0);
				System.out.println("Displaying missed HIT Index (" + missedHITIndex + ")");
			} else if (hitIndex >= topicToDataList.get(topic).size()) {
				// Go back to beginning HIT if there are no missed HITs
				System.out.println("There are no more HITS in the topic, going back to index 0");
				hitObject = topicToDataList.get(topic).get(0);
				userQueues.get(workerId).put(topic, 0);
			} else {
				// Happy path
				hitObject = topicToDataList.get(topic).get(hitIndex);
				int newHITIndex = userQueues.get(workerId).get(topic).intValue() + 1;
				userQueues.get(workerId).put(topic, newHITIndex);
			}
		}
		if (hitObject == null) {
			System.out.println("No more HITs in queue for " + workerId + " and topic " + topic);
		} else {
			hitObject.setWorkerId(workerId);
			hitObject.setMturkHitId(hitId);
			seenWriter.write(String.join("", hitObject.writeSimpleObject(), "\n"));
			seenWriter.flush();
			userPrevHits.get(workerId).get(topic).add(hitObject.getSimpleHITObject());
		}

		return hitObject;
	}

	// Checks whether a previous HIT is still in Assignable state
	// Adds an additional HIT to experiment if the HIT is still assignable
	// Returns the whether the HIT is Assignable
	private boolean checkHITStatus(String workerId, String topicId, HITSimpleObject prevHIT) throws IOException {
		boolean isAssignable = false;
		GetHITRequest getHITRequest = new GetHITRequest();
		getHITRequest.setHITId(prevHIT.getMturkHitId());
		// try {
		GetHITResult hitResult = client.getHIT(getHITRequest);
		HIT hit = hitResult.getHIT();
		System.out.println("Previous hit status is: " + hit.getHITStatus());

		// If HIT is still assignable, add additional HIT to experiment
		if (hit.getHITStatus().equalsIgnoreCase("Assignable")) {
			isAssignable = true;

			// Remove previous HIT
			// TODO delete from mTurk
			userPrevHits.get(workerId).get(topicId).remove(prevHIT);
			String topicName = topicNameMap.get(topicId);
			System.out.println(
					"Removed (" + prevHIT.toString() + ") for topic (" + topicId + ") because HIT was not submitted");

			// Create new HIT
			CreateHITRequest request = new CreateHITRequest();
			String questionSample = new String(Files.readAllBytes(Paths.get(dataProperties.getQuestionFilename())));
			request.setQuestion(questionSample);

			request.setMaxAssignments(1);
			long lifetime = Long.valueOf(dataProperties.getHitLifetime()).longValue();
			request.setLifetimeInSeconds(lifetime);
			request.setAssignmentDurationInSeconds(900l);
			request.setReward("0.45");
			request.setTitle(
					"How relevant is the document to the question/conversation?  (Topic Id: " + topicName + ")");
			request.setKeywords("document, relevance");
			request.setDescription(
					"Given the conversation, how relevant is the given document to the last topic of the conversation? (Topic Id: "
							+ topicName + ")");
			request.setQualificationRequirements(qualificationMap.get(topicId));

			CreateHITResult result = client.createHIT(request);
			String newHITId = result.getHIT().getHITId();
			hitIdToTopic.put(newHITId, topicId);
			hitWriter.write(String.join("", newHITId, ",", topicId, "\n"));
			hitWriter.flush();
			System.out.println("Created new HIT (" + newHITId + ") for topic (" + topicId + ")");
		} else if (hit.getHITStatus().equalsIgnoreCase(HITStatus.Reviewable.toString())) {
			// Remove previous HIT
			// TODO delete from mTurk
			userPrevHits.get(workerId).get(topicId).remove(prevHIT);
			System.out.println(
					"Removed (" + prevHIT.toString() + ") for topic (" + topicId + ") because HIT has been submitted");
		}
//		} catch (Exception e) {
//			System.out.println("No HIT for HITId: " + prevHIT.getHitId());
//		}
		return isAssignable;
	}

//	public void submitHIT(String hitId, String workerId)
//			throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
//		System.out.println("Submitted to Service with workerId: " + workerId + " and hitId: " + hitId);
//		String topic = hitIdToTopic.get(hitId);
//		if (topic == null) {
//			System.out.println("Cannot submit HIT because there is not HIT loaded for: " + hitId);
//		}
//		if (topic != null && workerId != null && userQueues.get(workerId) != null) {
//			if (topic != null && userQueues.get(workerId).get(topic) != null) {
//				int hitIndex = userQueues.get(workerId).get(topic).intValue();
//				HITObject hitObject = topicToDataList.get(topic).get(hitIndex);
//				hitObject.setWorkerId(workerId);
//				stateWriter.write(String.join("", hitObject.writeSimpleObject(), "\n"));
//				stateWriter.flush();
//				// Increment hit number
//				hitIndex++;
//				userQueues.get(workerId).put(topic, hitIndex);
//			} else {
//				System.out.println("Cannot Submit:  topic (" + topic + ") for user: " + workerId);
//			}
//		} else {
//			System.out.println("Cannot Submit:  No current HITs for user: " + workerId);
//		}
//	}

	public void loadData() throws IOException {
		Reader hitMapReader = Files.newBufferedReader(Paths.get(dataProperties.getHitMap()));
		CsvToBean<HITMapObject> csvToHitMap = new CsvToBeanBuilder(hitMapReader).withType(HITMapObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITMapObject> hitMapIterator = csvToHitMap.iterator();
		hitIdToTopic = new HashMap<String, String>();
		while (hitMapIterator.hasNext()) {
			HITMapObject hitMapObject = hitMapIterator.next();
			hitIdToTopic.put(hitMapObject.getHitId(), hitMapObject.getDataRowId());
		}

		Reader reader = Files.newBufferedReader(Paths.get(dataProperties.getHitCsv()));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();
		topicToDataList = new HashMap<String, List<HITObject>>();
		while (hitIterator.hasNext()) {
			HITObject hitObject = hitIterator.next();
			if (sampleHIT == null) {
				sampleHIT = hitObject;
			}
			String topic = hitObject.getTopicNum();
			topicToDataList.putIfAbsent(topic, new ArrayList<HITObject>());
			topicToDataList.get(topic).add(hitObject);
		}

		System.out.println("The environment is: " + dataProperties.getEndpoint());
		if (dataProperties.getEndpoint().equalsIgnoreCase("prod")
				|| dataProperties.getEndpoint().equalsIgnoreCase("production")) {
			client = getProdClient();
		} else {
			client = getSandboxClient();
		}

		// Initialize seen object file
		userQueues = new HashMap<String, Map<String, Integer>>();
		userPrevHits = new HashMap<String, Map<String, List<HITSimpleObject>>>();
		userMissedHits = new HashMap<String, Map<String, List<HITSimpleObject>>>();
		boolean seenFileExists = new File(dataProperties.getSeenFile()).exists();
		seenWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dataProperties.getSeenFile(), true), "UTF8"));
		if (!seenFileExists) {
			seenWriter.write("hitId,queryNum,topicNum,subQueryNum,workerId,mturkHitId\n");
			seenWriter.flush();
		} else {
			Reader stateReader = Files.newBufferedReader(Paths.get(dataProperties.getSeenFile()));
			CsvToBean<HITSimpleObject> csvToState = new CsvToBeanBuilder(stateReader).withType(HITSimpleObject.class)
					.withIgnoreLeadingWhiteSpace(true).build();
			Iterator<HITSimpleObject> stateIterator = csvToState.iterator();
			while (stateIterator.hasNext()) {
				HITSimpleObject seenObject = stateIterator.next();
				if (seenObject.getWorkerId() != null) {
					userQueues.putIfAbsent(seenObject.getWorkerId(), new HashMap<String, Integer>());
					userQueues.get(seenObject.getWorkerId()).put(seenObject.getTopicNum(),
							Integer.valueOf(seenObject.getHitId()) + 1);
					userPrevHits.putIfAbsent(seenObject.getWorkerId(), new HashMap<String, List<HITSimpleObject>>());
					userPrevHits.get(seenObject.getWorkerId()).putIfAbsent(seenObject.getTopicNum(),
							new ArrayList<HITSimpleObject>());
					userPrevHits.get(seenObject.getWorkerId()).get(seenObject.getTopicNum()).add(seenObject);
				}
			}
			for (String workerId : userPrevHits.keySet()) {
				Map<String, List<HITSimpleObject>> workerHits = userPrevHits.get(workerId);
				for (String topicId : workerHits.keySet()) {
					List<HITSimpleObject> workerTopicHits = workerHits.get(topicId);
					if (workerTopicHits.size() > 0) {
						for (int i = 0; i < workerTopicHits.size() - 1; i++) {
							userPrevHits.get(workerId).get(topicId).remove(i);
						}
					}
				}
			}
		}

		hitWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dataProperties.getHitMap(), true), "UTF8"));

		qualificationMap = new HashMap<String, List<QualificationRequirement>>();
		Scanner scanner = new Scanner(new File(dataProperties.getQualificationType()));
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split(",");
			qualificationMap.putIfAbsent(lineParts[0], new ArrayList<QualificationRequirement>());

			QualificationRequirement workerRequirement = new QualificationRequirement();
			workerRequirement.setQualificationTypeId(lineParts[1]);
			workerRequirement.setComparator(Comparator.EqualTo);
			workerRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(1)));
			workerRequirement.setActionsGuarded(HITAccessActions.DiscoverPreviewAndAccept);
			qualificationMap.get(lineParts[0]).add(workerRequirement);
		}
		scanner.close();

		topicNameMap = new HashMap<String, String>();
		Scanner topicScanner = new Scanner(new File(dataProperties.getTopicNameMap()));
		while (topicScanner.hasNext()) {
			String topicLine = topicScanner.nextLine();
			String[] topicLineParts = topicLine.split(",");
			topicNameMap.put(topicLineParts[0].trim(), topicLineParts[1].trim());
		}
		topicScanner.close();
	}

	public List<String> listUserQueues() {
		List<String> userQueueList = new ArrayList<String>();
		userQueues.forEach((workerId, userTopicMap) -> {
			userTopicMap.forEach((topic, index) -> {
				userQueueList.add(String.join(", ", workerId, topic, index.toString()));
			});
		});
		return userQueueList;
	}

	public List<String> listTopicHits() {
		List<String> topicHitList = new ArrayList<String>();
		hitIdToTopic.forEach((hitId, topic) -> {
			topicHitList.add(String.join(", ", topic, hitId));
		});
		return topicHitList;
	}

	public List<String> listUserHits(String workerId) {
		List<String> userHITlist = new ArrayList<String>();
		Map<String, List<HITSimpleObject>> userHITMap = userPrevHits.get(workerId);
		if (userHITMap != null) {
			userHITMap.forEach((queryNum, hits) -> {
				for (HITSimpleObject hitObject : hits) {
					userHITlist.add(String.join(": ", queryNum, hitObject.toString()));
				}
			});
		}

		return userHITlist;
	}

	public String deleteTopic(String topic) {
		if (topicToDataList.get(topic) != null) {
			topicToDataList.remove(topic);
		} else {
			return "No topic to delete";
		}
		return "success";
	}

	public String transferTopicToNewUser(String oldWorkerId, String newWorkerId)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String message = "";
		if (userQueues.get(oldWorkerId) != null) {
			Map<String, Integer> topicsForUser = userQueues.get(oldWorkerId);
			userQueues.put(newWorkerId, topicsForUser);
		} else {
			message = String.join(" ", "Worker Id: ", oldWorkerId, "does not have any topics to transer.");
		}
		return message;
	}

	public String transferTopicToNewUser(String oldWorkerId, String newWorkerId, String topic)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String message = "";
		if (userQueues.get(oldWorkerId) != null) {
			Map<String, Integer> topicsForUser = userQueues.get(oldWorkerId);
			if (topicsForUser.get(topic) != null) {
				userQueues.putIfAbsent(newWorkerId, new HashMap<String, Integer>());
				userQueues.get(newWorkerId).put(topic, topicsForUser.get(topic));
			}
		} else {
			message = String.join(" ", "Worker Id: ", oldWorkerId, "does not have any topics to transer.");
		}
		return message;
	}

	private static AmazonMTurk getSandboxClient() {
		System.out.println("Initializing SANDBOX MTurk Client");
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

	private static AmazonMTurk getProdClient() {
		System.out.println("Initializing PROD MTurk Client");
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(PROD_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

	public String addQualification(String topicId, String qualification) throws IOException {
		qualificationMap.putIfAbsent(topicId, new ArrayList<QualificationRequirement>());

		QualificationRequirement workerRequirement = new QualificationRequirement();
		workerRequirement.setQualificationTypeId(qualification);
		workerRequirement.setComparator(Comparator.EqualTo);
		workerRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(1)));
		workerRequirement.setActionsGuarded(HITAccessActions.DiscoverPreviewAndAccept);
		qualificationMap.get(topicId).add(workerRequirement);

		// Add to qualification file
		BufferedWriter qualificationWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dataProperties.getQualificationType(), true), "UTF8"));
		String qualificationString = String.join(",", topicId, qualification);
		qualificationWriter.write("\n");
		qualificationWriter.write(qualificationString);
		qualificationWriter.close();

		return qualificationString;
	}

	// Returns String of all topics that were added
	public String addTopics(String hitMapFile, String hitCsvFile) throws IOException {
		Reader hitMapReader = Files.newBufferedReader(Paths.get(hitMapFile));
		CsvToBean<HITMapObject> csvToHitMap = new CsvToBeanBuilder(hitMapReader).withType(HITMapObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITMapObject> hitMapIterator = csvToHitMap.iterator();
		Set<String> newTopicIds = new HashSet<String>();
		while (hitMapIterator.hasNext()) {
			HITMapObject hitMapObject = hitMapIterator.next();
			hitIdToTopic.put(hitMapObject.getHitId(), hitMapObject.getDataRowId());
			newTopicIds.add(hitMapObject.getHitId());
		}
		StringJoiner newTopicsBuffer = new StringJoiner(", ");
		for (String newTopic : newTopicIds) {
			newTopicsBuffer.add(newTopic);
		}

		Reader reader = Files.newBufferedReader(Paths.get(hitCsvFile));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();
		while (hitIterator.hasNext()) {
			HITObject hitObject = hitIterator.next();
			if (sampleHIT == null) {
				sampleHIT = hitObject;
			}
			String topic = hitObject.getTopicNum();
			topicToDataList.putIfAbsent(topic, new ArrayList<HITObject>());
			topicToDataList.get(topic).add(hitObject);
		}
		return newTopicsBuffer.toString();
	}

	public void addHIT(String topic, String HITId, String hitMapFile) throws IOException {
		hitIdToTopic.put(HITId, topic);
		boolean hitFileExists = new File(hitMapFile).exists();
		Writer topicHitWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(hitMapFile, true), "UTF8"));
		if (!hitFileExists) {
			topicHitWriter.write("hitId,dataRowId\n");
		}
		topicHitWriter.write(String.join(",", HITId, topic));
		topicHitWriter.write("\n");
		topicHitWriter.close();
	}

	public void addTopicName(String topic, String topicName) throws IOException {
		if (topicNameMap == null) {
			topicNameMap = new HashMap<String, String>();
		}
		topicNameMap.put(topic, topicName);
		System.out.println("Adding topic: " + topic + " - " + topicName);

		// Add to qualification file
		BufferedWriter topicWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dataProperties.getTopicNameMap(), true), "UTF8"));
		String topicString = String.join(",", topic, topicName);
		topicWriter.write(topicString);
		topicWriter.write("\n");
		topicWriter.close();
		System.out.println("Wrote topic: " + topicString);
	}

}
