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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Component
public class CastService {

	@Autowired
	private DataProperties dataProperties;

	private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
	private static final String PROD_ENDPOINT = "https://mturk-requester.us-east-1.amazonaws.com";
	private static final String SIGNING_REGION = "us-east-1";
	private AmazonMTurk client;

	private Writer stateWriter;

	private Map<String, List<HITObject>> topicToDataList;
	private Map<String, Map<String, Queue<HITObject>>> userQueues;
	private Map<String, Map<String, Integer>> prevSubQuery;
	private Map<String, String> hitIdToTopic;
	private HITObject sampleHIT;

	public HITObject getSampleData() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		return sampleHIT;
	}

	public HITObject getNextHIT(String hitId, String workerId)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String topic = hitIdToTopic.get(hitId);
		loadUserQueues(topic, workerId);

		HITObject hitObject = sampleHIT;
//		if (userQueues.get(workerId).get(topic) == null) {
//			checkAssignments(workerId);
//		}
		if (userQueues.get(workerId).get(topic) != null) {
			hitObject = userQueues.get(workerId).get(topic).poll();
		}
		if (hitObject == null) {
			hitObject = sampleHIT;
			System.out.println("No more HITs in queue for " + workerId + " and topic " + topic
					+ ".  Displaying sample HIT: " + hitObject.toString());
		}
		hitObject.setWorkerId(workerId);
		stateWriter.write(String.join("", hitObject.toString(), "\n"));
		stateWriter.flush();
		return hitObject;
	}

	public boolean isNewQuery(String workerId, HITObject hitObject) {
		boolean isNew = false;
		if (prevSubQuery.get(workerId) != null) {
			if (prevSubQuery.get(workerId).get(hitObject.getTopicNum()) != null) {
				// Get the last seen HIT and compare it to the new
				Integer prevHIT = prevSubQuery.get(workerId).get(hitObject.getTopicNum());
				if (prevHIT.intValue() != Integer.valueOf(hitObject.getSubQueryNum()).intValue()) {
					isNew = true;
				}
			}
		}
		prevSubQuery.putIfAbsent(workerId, new HashMap<String, Integer>());
		prevSubQuery.get(workerId).put(hitObject.getTopicNum(), Integer.valueOf(hitObject.getSubQueryNum()));
		return isNew;
	}

	private void loadUserQueues(String topic, String workerId) throws IOException {
		if (userQueues.get(workerId) == null) {
			userQueues.putIfAbsent(workerId, new HashMap<String, Queue<HITObject>>());
			if (userQueues.get(workerId).get(topic) == null) {
				List<HITObject> hitForTopic = topicToDataList.get(topic);
				// Filter out HITs from state file
				Reader stateReader = Files.newBufferedReader(Paths.get(dataProperties.getStateFile()));
				CsvToBean<HITObject> csvToState = new CsvToBeanBuilder(stateReader).withType(HITObject.class)
						.withIgnoreLeadingWhiteSpace(true).build();
				Iterator<HITObject> stateIterator = csvToState.iterator();
				List<Integer> seenIds = new ArrayList<Integer>();
				HITObject lastSeenObject = null;
				while (stateIterator.hasNext()) {
					HITObject seenObject = stateIterator.next();
					if (seenObject.getWorkerId() != null && seenObject.getWorkerId().equals(workerId)) {
						lastSeenObject = seenObject;
						seenIds.add(Integer.valueOf(lastSeenObject.getHitId()));
					}
				}
				prevSubQuery.putIfAbsent(workerId, new HashMap<String, Integer>());
				if (lastSeenObject != null) {
					prevSubQuery.get(workerId).put(topic, Integer.valueOf(lastSeenObject.getSubQueryNum()));
				} else {
					prevSubQuery.get(workerId).put(topic, Integer.valueOf(1));
				}

				List<HITObject> unseenObjects = new ArrayList<HITObject>();
				for (HITObject hitObject : hitForTopic) {
					Integer hitId = Integer.valueOf(hitObject.getHitId());
					if (!seenIds.contains(hitId)) {
						unseenObjects.add(hitObject);
					}
				}
				userQueues.get(workerId).putIfAbsent(topic, new LinkedList<HITObject>(unseenObjects));
			}
		}
	}

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
		prevSubQuery = new HashMap<String, Map<String, Integer>>();

		if (dataProperties.getEndpoint().equalsIgnoreCase("prod")
				|| dataProperties.getEndpoint().equalsIgnoreCase("production")) {
			client = getProdClient();
		} else {
			client = getSandboxClient();
		}

		// Recover state data if it exists
		userQueues = new HashMap<String, Map<String, Queue<HITObject>>>();
		boolean stateFileExists = new File(dataProperties.getStateFile()).exists();
		stateWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dataProperties.getStateFile(), true), "UTF8"));
		if (!stateFileExists) {
			stateWriter.write(
					"hitId,queryNum,topicNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5,workerId\n");
			stateWriter.flush();
		}
	}

	public String transferTopicToNewUser(String oldWorkerId, String newWorkerId)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String message = "";
		if (userQueues.get(oldWorkerId) != null) {
			Map<String, Queue<HITObject>> topicsForUser = userQueues.get(oldWorkerId);
			userQueues.put(newWorkerId, topicsForUser);
			// Write data to state file so it can be recovered
			topicsForUser.forEach((topicId, hitQueue) -> {
				for (HITObject hitObject : hitQueue) {
					hitObject.setWorkerId(newWorkerId);
					try {
						stateWriter.write(String.join("", hitObject.toString(), "\n"));
					} catch (IOException e) {
						System.out.println("Could not write HITs to user state file for: " + newWorkerId);
						e.printStackTrace();
					}
				}
			});
			stateWriter.flush();
			userQueues.remove(oldWorkerId);
		} else {
			message = String.join(" ", "Worker Id: ", oldWorkerId, "does not have any topics to transer.");
		}

		return message;
	}

//	private void checkAssignments(String workerId) {
//		Map<String, HITObject> userSeenHITs = seenHITIds.get(workerId);
//		if (userSeenHITs != null) {
//			userSeenHITs.forEach((hitId, hitObject) -> {
//				// Add rejected back to the queue
//				Assignment asn = getWorkerAssignmentForHIT(hitId, workerId,
//						Collections.singletonList(AssignmentStatus.Rejected.name()));
//				if (asn != null) {
//					String topic = hitIdToTopic.get(hitId);
//					if (userQueues.get(workerId) != null && userQueues.get(workerId).get(topic) != null) {
//						userQueues.get(workerId).get(topic).add(hitObject);
//						System.out.println("Adding rejected HIT back to queue: " + hitObject.toString());
//					}
//				}
//
//				// Add unsubmitted back to the queue
//				Collection<String> statuses = new ArrayList<String>();
//				statuses.add(AssignmentStatus.Submitted.name());
//				statuses.add(AssignmentStatus.Approved.name());
//				statuses.add(AssignmentStatus.Rejected.name());
//				Assignment submittedAsn = getWorkerAssignmentForHIT(hitId, workerId, statuses);
//				if (submittedAsn == null) {
//					String topic = hitIdToTopic.get(hitId);
//					if (userQueues.get(workerId) != null && userQueues.get(workerId).get(topic) != null) {
//						userQueues.get(workerId).get(topic).add(hitObject);
//						System.out.println("Adding unsubmitted HIT back to queue: " + hitObject.toString());
//					}
//				}
//			});
//		}
//
//	}
//
//	private Assignment getWorkerAssignmentForHIT(String hitId, String workerId, Collection<String> statuses) {
//		ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
//		listHITRequest.setHITId(hitId);
//		listHITRequest.setAssignmentStatuses(statuses);
//
//		listHITRequest.setMaxResults(100);
//		ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
//		List<Assignment> assignmentList = listHITResult.getAssignments();
//
//		Assignment assignment = null;
//		for (Assignment asn : assignmentList) {
//			if (workerId.equals(asn.getWorkerId())) {
//				assignment = asn;
//			}
//		}
//
//		return assignment;
//	}

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

}
