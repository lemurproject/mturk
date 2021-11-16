package org.lemurproject.mturkcwdeep;

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
public class ClueWebDeepService {

	@Autowired
	private DataProperties dataProperties;

	private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
	private static final String PROD_ENDPOINT = "https://mturk-requester.us-east-1.amazonaws.com";
	private static final String SIGNING_REGION = "us-east-1";
	private AmazonMTurk client;

	private Writer seenWriter;
	private Writer hitWriter;

	private Map<String, String> queryNameMap;
	private Map<String, List<HITObject>> queryToDataList;
	private Map<String, Map<String, Integer>> userQueues;
	private Map<String, Map<String, List<HITSimpleObject>>> userPrevHits;
	private Map<String, Map<String, List<HITSimpleObject>>> userMissedHits;
	private Map<String, String> hitIdToQuery;
	private HITObject sampleHIT;
	private Map<String, List<QualificationRequirement>> qualificationMap;
	// private List<QualificationRequirement> qualifications;

	public HITObject getSampleData() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		return sampleHIT;
	}

	public HITObject getNextHIT(String hitId, String workerId)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String query = hitIdToQuery.get(hitId);
		if (query == null) {
			System.out.println("No data loaded for hitId: " + hitId);
			return null;
		}
		if (workerId == null) {
			System.out.println("Worker ID is null");
			return null;
		}
		userQueues.putIfAbsent(workerId, new HashMap<String, Integer>());
		userQueues.get(workerId).putIfAbsent(query, Integer.valueOf(0));

		userPrevHits.putIfAbsent(workerId, new HashMap<String, List<HITSimpleObject>>());
		userPrevHits.get(workerId).putIfAbsent(query, new ArrayList<HITSimpleObject>());

		userMissedHits.putIfAbsent(workerId, new HashMap<String, List<HITSimpleObject>>());
		userMissedHits.get(workerId).putIfAbsent(query, new ArrayList<HITSimpleObject>());

		// Check if previous HIT(s) was submitted
		if (userPrevHits.get(workerId) != null && userPrevHits.get(workerId).get(query) != null
				&& userPrevHits.get(workerId).get(query).size() > 0) {
			List<HITSimpleObject> hitList = userPrevHits.get(workerId).get(query);
			for (int i = userPrevHits.get(workerId).get(query).size() - 1; i >= 0; i--) {
				HITSimpleObject prevHit = hitList.get(i);
				boolean isAssignable = checkHITStatus(workerId, query, prevHit);
				if (isAssignable) {
					userMissedHits.get(workerId).get(query).add(prevHit);
					System.out.println("Adding missed HIT (" + prevHit.toString() + ")");
				}
			}
		}

		HITObject hitObject = null;

		if (userQueues.get(workerId).get(query) != null && queryToDataList.get(query).size() > 0) {
			int hitIndex = userQueues.get(workerId).get(query).intValue();

			// Display any missed HITs
			if (userMissedHits.get(workerId) != null && userMissedHits.get(workerId).get(query) != null
					&& userMissedHits.get(workerId).get(query).size() > 0) {
				int missedHITIndex = Integer.valueOf(userMissedHits.get(workerId).get(query).get(0).getDocId())
						.intValue();
				hitObject = queryToDataList.get(query).get(missedHITIndex);
				hitObject.setHitCount("Previous unsubmitted HIT");
				userMissedHits.get(workerId).get(query).remove(0);
				System.out.println("Displaying missed HIT Index (" + missedHITIndex + ")");
			} else if (hitIndex >= queryToDataList.get(query).size()) {
				// Go back to beginning HIT if there are no missed HITs
				System.out.println("There are no more HITS in the query, going back to index 0");
				hitObject = queryToDataList.get(query).get(0);
				userQueues.get(workerId).put(query, 0);
			} else {
				// Happy path
				hitObject = queryToDataList.get(query).get(hitIndex);
				int newHITIndex = userQueues.get(workerId).get(query).intValue() + 1;
				userQueues.get(workerId).put(query, newHITIndex);
			}
		}
		if (hitObject == null) {
			System.out.println("No more HITs in queue for " + workerId + " and query " + query);
		} else {
			hitObject.setWorkerId(workerId);
			hitObject.setMturkHitId(hitId);
			seenWriter.write(String.join("", hitObject.writeSimpleObject(), "\n"));
			seenWriter.flush();
			userPrevHits.get(workerId).get(query).add(hitObject.getSimpleHITObject());
		}

		return hitObject;
	}

	// Checks whether a previous HIT is still in Assignable state
	// Adds an additional HIT to experiment if the HIT is still assignable
	// Returns the whether the HIT is Assignable
	private boolean checkHITStatus(String workerId, String queryId, HITSimpleObject prevHIT) throws IOException {
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
			userPrevHits.get(workerId).get(queryId).remove(prevHIT);
			String queryName = queryNameMap.get(queryId);
			System.out.println(
					"Removed (" + prevHIT.toString() + ") for query (" + queryId + ") because HIT was not submitted");

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
					"How relevant is the document to the question/conversation?  (Query Id: " + queryName + ")");
			request.setKeywords("document, relevance");
			request.setDescription(
					"Given the conversation, how relevant is the given document to the last query of the conversation? (Query Id: "
							+ queryName + ")");
			request.setQualificationRequirements(qualificationMap.get(queryId));

			CreateHITResult result = client.createHIT(request);
			String newHITId = result.getHIT().getHITId();
			hitIdToQuery.put(newHITId, queryId);
			hitWriter.write(String.join("", newHITId, ",", queryId, "\n"));
			hitWriter.flush();
			System.out.println("Created new HIT (" + newHITId + ") for query (" + queryId + ")");
		} else if (hit.getHITStatus().equalsIgnoreCase(HITStatus.Reviewable.toString())) {
			// Remove previous HIT
			// TODO delete from mTurk
			userPrevHits.get(workerId).get(queryId).remove(prevHIT);
			System.out.println(
					"Removed (" + prevHIT.toString() + ") for query (" + queryId + ") because HIT has been submitted");
		}
		return isAssignable;
	}

	public void loadData() throws IOException {
		Reader hitMapReader = Files.newBufferedReader(Paths.get(dataProperties.getHitMap()));
		CsvToBean<HITMapObject> csvToHitMap = new CsvToBeanBuilder(hitMapReader).withType(HITMapObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITMapObject> hitMapIterator = csvToHitMap.iterator();
		hitIdToQuery = new HashMap<String, String>();
		while (hitMapIterator.hasNext()) {
			HITMapObject hitMapObject = hitMapIterator.next();
			hitIdToQuery.put(hitMapObject.getHitId(), hitMapObject.getDataRowId());
		}

		Reader reader = Files.newBufferedReader(Paths.get(dataProperties.getHitCsv()));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();
		queryToDataList = new HashMap<String, List<HITObject>>();
		while (hitIterator.hasNext()) {
			HITObject hitObject = hitIterator.next();
			if (sampleHIT == null) {
				sampleHIT = hitObject;
			}
			String query = hitObject.getQueryNum();
			queryToDataList.putIfAbsent(query, new ArrayList<HITObject>());
			queryToDataList.get(query).add(hitObject);
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
			seenWriter.write("hitId,queryNum,queryNum,subQueryNum,workerId,mturkHitId\n");
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
					userQueues.get(seenObject.getWorkerId()).put(seenObject.getQueryNum(),
							Integer.valueOf(seenObject.getDocId()) + 1);
					userPrevHits.putIfAbsent(seenObject.getWorkerId(), new HashMap<String, List<HITSimpleObject>>());
					userPrevHits.get(seenObject.getWorkerId()).putIfAbsent(seenObject.getQueryNum(),
							new ArrayList<HITSimpleObject>());
					userPrevHits.get(seenObject.getWorkerId()).get(seenObject.getQueryNum()).add(seenObject);
				}
			}
			for (String workerId : userPrevHits.keySet()) {
				Map<String, List<HITSimpleObject>> workerHits = userPrevHits.get(workerId);
				for (String queryId : workerHits.keySet()) {
					List<HITSimpleObject> workerQueryHits = workerHits.get(queryId);
					if (workerQueryHits.size() > 0) {
						for (int i = 0; i < workerQueryHits.size() - 1; i++) {
							userPrevHits.get(workerId).get(queryId).remove(i);
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

		queryNameMap = new HashMap<String, String>();
		Scanner queryScanner = new Scanner(new File(dataProperties.getQueryNameMap()));
		while (queryScanner.hasNext()) {
			String queryLine = queryScanner.nextLine();
			String[] queryLineParts = queryLine.split(",");
			queryNameMap.put(queryLineParts[0].trim(), queryLineParts[1].trim());
		}
		queryScanner.close();
	}

	public List<String> listUserQueues() {
		List<String> userQueueList = new ArrayList<String>();
		userQueues.forEach((workerId, userQueryMap) -> {
			userQueryMap.forEach((query, index) -> {
				userQueueList.add(String.join(", ", workerId, query, index.toString()));
			});
		});
		return userQueueList;
	}

	public List<String> listQueryHits() {
		List<String> queryHitList = new ArrayList<String>();
		hitIdToQuery.forEach((hitId, query) -> {
			queryHitList.add(String.join(", ", query, hitId));
		});
		return queryHitList;
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

	public String deleteQuery(String query) {
		if (queryToDataList.get(query) != null) {
			queryToDataList.remove(query);
		} else {
			return "No query to delete";
		}
		return "success";
	}

	public String transferQueryToNewUser(String oldWorkerId, String newWorkerId)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String message = "";
		if (userQueues.get(oldWorkerId) != null) {
			Map<String, Integer> queriesForUser = userQueues.get(oldWorkerId);
			userQueues.put(newWorkerId, queriesForUser);
		} else {
			message = String.join(" ", "Worker Id: ", oldWorkerId, "does not have any querys to transer.");
		}
		return message;
	}

	public String transferQueryToNewUser(String oldWorkerId, String newWorkerId, String query)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String message = "";
		if (userQueues.get(oldWorkerId) != null) {
			Map<String, Integer> queriesForUser = userQueues.get(oldWorkerId);
			if (queriesForUser.get(query) != null) {
				userQueues.putIfAbsent(newWorkerId, new HashMap<String, Integer>());
				userQueues.get(newWorkerId).put(query, queriesForUser.get(query));
			}
		} else {
			message = String.join(" ", "Worker Id: ", oldWorkerId, "does not have any querys to transer.");
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

	public String addQualification(String queryId, String qualification) throws IOException {
		qualificationMap.putIfAbsent(queryId, new ArrayList<QualificationRequirement>());

		QualificationRequirement workerRequirement = new QualificationRequirement();
		workerRequirement.setQualificationTypeId(qualification);
		workerRequirement.setComparator(Comparator.EqualTo);
		workerRequirement.setIntegerValues(Collections.singletonList(Integer.valueOf(1)));
		workerRequirement.setActionsGuarded(HITAccessActions.DiscoverPreviewAndAccept);
		qualificationMap.get(queryId).add(workerRequirement);

		// Add to qualification file
		BufferedWriter qualificationWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(dataProperties.getQualificationType(), true), "UTF8"));
		String qualificationString = String.join(",", queryId, qualification);
		qualificationWriter.write("\n");
		qualificationWriter.write(qualificationString);
		qualificationWriter.close();

		return qualificationString;
	}

	// Returns String of all querys that were added
	public String addQueries(String hitMapFile, String hitCsvFile) throws IOException {
		Reader hitMapReader = Files.newBufferedReader(Paths.get(hitMapFile));
		CsvToBean<HITMapObject> csvToHitMap = new CsvToBeanBuilder(hitMapReader).withType(HITMapObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITMapObject> hitMapIterator = csvToHitMap.iterator();
		Set<String> newQueryIds = new HashSet<String>();
		while (hitMapIterator.hasNext()) {
			HITMapObject hitMapObject = hitMapIterator.next();
			hitIdToQuery.put(hitMapObject.getHitId(), hitMapObject.getDataRowId());
			newQueryIds.add(hitMapObject.getHitId());
		}
		StringJoiner newQuerysBuffer = new StringJoiner(", ");
		for (String newQuery : newQueryIds) {
			newQuerysBuffer.add(newQuery);
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
			String query = hitObject.getQueryNum();
			queryToDataList.putIfAbsent(query, new ArrayList<HITObject>());
			queryToDataList.get(query).add(hitObject);
			String queryName = queryNameMap.putIfAbsent(hitObject.getQueryNum(), hitObject.getQuery());
			if (queryName == null) {
				// Add to qualification file
				BufferedWriter queryWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(dataProperties.getQueryNameMap(), true), "UTF8"));
				String queryString = String.join(",", query, hitObject.getQuery());
				queryWriter.write(queryString);
				queryWriter.write("\n");
				queryWriter.close();
				System.out.println("Wrote query: " + queryString);
			}
		}
		return newQuerysBuffer.toString();
	}

	public void addHIT(String query, String HITId, String hitMapFile) throws IOException {
		hitIdToQuery.put(HITId, query);
		boolean hitFileExists = new File(hitMapFile).exists();
		Writer queryHitWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(hitMapFile, true), "UTF8"));
		if (!hitFileExists) {
			queryHitWriter.write("hitId,dataRowId\n");
		}
		queryHitWriter.write(String.join(",", HITId, query));
		queryHitWriter.write("\n");
		queryHitWriter.close();
	}

}
