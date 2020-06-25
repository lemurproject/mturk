package org.lemurproject.mturkcastdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

@Component
public class CreateMTurkCastData {

	@Autowired
	private GlobalProperties properties;

	public void createData() throws IOException, ParseException {
		List<CASTQuery> queries = getQueries();
		List<Qrel> qrels = getQrels();

		Directory marcoDir = FSDirectory.open(Paths.get(properties.getMarcoIndex()));
		IndexReader marcoReader = DirectoryReader.open(marcoDir);
		IndexSearcher marcoSearcher = new IndexSearcher(marcoReader);

		Directory carDir = FSDirectory.open(Paths.get(properties.getCarIndex()));
		IndexReader carReader = DirectoryReader.open(carDir);
		IndexSearcher carSearcher = new IndexSearcher(carReader);

		// BufferedWriter csvWriter = new BufferedWriter(new
		// FileWriter("mturk_cast.csv"), "UTF8");

		QueryParser qp = new QueryParser("externalId", new StandardAnalyzer());

		int qrelNum = 0;
		String prevQueryNum = "";
		List<CASTDocument> docs = new ArrayList<CASTDocument>();
		List<CASTHIT> allHITs = new ArrayList<CASTHIT>();
		Qrel lastQrel = null;
		for (Qrel qrel : qrels) {
			qrelNum++;
			if (qrelNum % 1 == 0) {
				String currentQueryNum = qrel.getQueryNum();
				if (!currentQueryNum.contentEquals(prevQueryNum)) {
					if (docs.size() > 0) {
						allHITs.addAll(createRandomHITs(docs, lastQrel, queries));
						// System.out.println("Created HITs for: " + lastQrel.getQueryNum());
						// allHITs.addAll(createHITs(docs, lastQrel, queries));
					}
					docs = new ArrayList<CASTDocument>();
				}
				String docId = qrel.getDocId();
				Document doc = null;
				if (docId.startsWith("MARCO")) {
					String docNum = docId.substring(docId.indexOf("_") + 1);
					Query query = qp.parse(docNum);
					TopDocs hitDocs = marcoSearcher.search(query, 1);
					ScoreDoc[] scoreDocs = hitDocs.scoreDocs;

					if (scoreDocs.length == 0) {
						System.out.println("No doc found MARCO " + docId);
					}

					for (ScoreDoc scoreDoc : scoreDocs) {
						int docid = scoreDoc.doc;
						doc = marcoSearcher.doc(docid);
					}
				} else {
					String docNum = docId.substring(docId.indexOf("_") + 1);
					Query query = qp.parse(docNum);
					TopDocs hitDocs = carSearcher.search(query, 1);
					ScoreDoc[] scoreDocs = hitDocs.scoreDocs;

					if (scoreDocs.length == 0) {
						System.out.println("No doc found CAR " + docId);
					}

					for (ScoreDoc scoreDoc : scoreDocs) {
						int docid = scoreDoc.doc;
						doc = carSearcher.doc(docid);
					}
				}
				if (doc != null) {
					String docText = doc.get("body");
					docText = docText.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?! ]", "");
					CASTDocument castDoc = new CASTDocument();
					castDoc.setDocId(docId);
					castDoc.setDocText(docText);
					castDoc.setScore(Integer.valueOf(qrel.getScore()));
					docs.add(castDoc);
				}
				prevQueryNum = currentQueryNum;
				lastQrel = qrel;
			}
		}

		allHITs.addAll(createRandomHITs(docs, lastQrel, queries));
		// writeDocsCSV(allHITs);
		writeAllCSV(allHITs);
		// writeGroupCSV(allHITs);
	}

	private void writeGroupCSV(List<CASTHIT> hits) throws IOException {
		Writer csv3Writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_30s.csv"), "UTF8"));
		csv3Writer.write(
				"queryNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5\n");
		Writer csv4Writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_40s.csv"), "UTF8"));
		csv4Writer.write(
				"queryNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5\n");
		Writer csv5Writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_50s.csv"), "UTF8"));
		csv5Writer.write(
				"queryNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5\n");
		Writer csv6Writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_60s.csv"), "UTF8"));
		csv6Writer.write(
				"queryNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5\n");
		Writer csv7Writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_70s.csv"), "UTF8"));
		csv7Writer.write(
				"queryNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5\n");
		for (CASTHIT hit : hits) {
			StringJoiner hitLineBuffer = new StringJoiner(",");
			hitLineBuffer.add(hit.getQueryNum());
			hitLineBuffer.add(String.valueOf(hit.getSubQueryNum()));
			hitLineBuffer.add(hit.getQueryText());
			for (CASTDocument castDoc : hit.getDocs()) {
				hitLineBuffer.add(castDoc.getDocText());
				hitLineBuffer.add(castDoc.getDocId());
				hitLineBuffer.add(String.valueOf(castDoc.getScore()));
			}
			if (hit.getTopicQueryNum() < 40) {
				csv3Writer.write(String.join("", hitLineBuffer.toString(), "\n"));
			} else if (hit.getTopicQueryNum() < 50) {
				csv4Writer.write(String.join("", hitLineBuffer.toString(), "\n"));
			} else if (hit.getTopicQueryNum() < 60) {
				csv5Writer.write(String.join("", hitLineBuffer.toString(), "\n"));
			} else if (hit.getTopicQueryNum() < 70) {
				csv6Writer.write(String.join("", hitLineBuffer.toString(), "\n"));
			} else if (hit.getTopicQueryNum() < 80) {
				csv7Writer.write(String.join("", hitLineBuffer.toString(), "\n"));
			}
		}
		csv3Writer.close();
		csv4Writer.close();
		csv5Writer.close();
		csv6Writer.close();
		csv7Writer.close();
	}

	private void writeDocsCSV(List<CASTHIT> hits) throws IOException {
		Writer csvDocsWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_31_docs_testDups.csv"), "UTF8"));
		csvDocsWriter.write("documentId,text");
		for (CASTHIT hit : hits) {

			for (CASTDocument castDoc : hit.getDocs()) {
				if (castDoc != null) {
					StringJoiner hitLineBuffer = new StringJoiner(",");
					hitLineBuffer.add(castDoc.getDocId());
					hitLineBuffer.add(castDoc.getDocText());
					csvDocsWriter.write(String.join("", hitLineBuffer.toString(), "\n"));
				}
			}
		}

		csvDocsWriter.close();
	}

	private void writeAllCSV(List<CASTHIT> hits) throws IOException {
		Writer csvAllWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(properties.getHitCsv()), "UTF8"));
		csvAllWriter.write(
				"hitId,queryNum,topicNum,subQueryNum,query,hitCount,isLast,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5,workerId,mturkHitId\n");
		int hitId = 0;
		String prevQuery = "";
		Map<String, List<String>> duplicates = new HashMap<String, List<String>>();
		for (CASTHIT hit : hits) {
			boolean isNewQuery = false;
			if (!hit.getQueryNum().equals(prevQuery)) {
				isNewQuery = true;
				prevQuery = hit.getQueryNum();
			}

			StringJoiner hitLineBuffer = new StringJoiner(",");
			hitLineBuffer.add(String.valueOf(hitId));
			hitLineBuffer.add(hit.getQueryNum());
			hitLineBuffer.add(String.valueOf(hit.getTopicQueryNum()));
			hitLineBuffer.add(String.valueOf(hit.getSubQueryNum()));
			hitLineBuffer.add(hit.getQueryText());
			hitLineBuffer.add(hit.getHitCount());
			hitLineBuffer.add(String.valueOf(hit.isLastHITinQuestion()));

			Set<String> docIds = new HashSet<String>();
			for (CASTDocument castDoc : hit.getDocs()) {
				if (castDoc != null) {
					String docText = castDoc.getDocText();
					if (isNewQuery) {
						docText = String.join(" ", "<span>*** NEW QUERY ***</span>", docText);
						isNewQuery = false;
					}
					docText = String.join("", "\" ", docText, "\"");
					hitLineBuffer.add(docText);
					hitLineBuffer.add(castDoc.getDocId());
					hitLineBuffer.add(String.valueOf(castDoc.getScore()));

					String queryDocId = String.join("_", hit.getQueryNum(), castDoc.getDocId());
					duplicates.putIfAbsent(queryDocId, new ArrayList<String>());
					duplicates.get(queryDocId).add(String.valueOf(hitId));

					if (!docIds.add(castDoc.getDocId())) {
						System.out.println("HIT numer: " + hitId);
						System.out.println("Duplicate Document: " + castDoc.getDocId());
					}

				} else {
					System.out.println("Doc is null for HIT: " + hitLineBuffer.toString());
				}
			}
			hitLineBuffer.add("");
			hitLineBuffer.add("");
			// System.out.println(String.join("", hitLineBuffer.toString(), "\n"));
			csvAllWriter.write(String.join("", hitLineBuffer.toString(), "\n"));
			hitId++;
		}

		System.out.println("Duplicate docs:");
		for (String docId : duplicates.keySet()) {
			List<String> hitsNums = duplicates.get(docId);
			if (hitsNums.size() > 1) {
				System.out.println(docId + ": " + String.join(", ", hitsNums));
			}
		}

		csvAllWriter.close();
	}

	private List<CASTHIT> createRandomHITs(List<CASTDocument> docs, Qrel qrel, List<CASTQuery> queries) {
		// Get number of random documents needed
		int numDocs = docs.size();
		double fivePercent = docs.size() * 0.05;
		long numRepeatDocs = Math.round(fivePercent);
		if ((docs.size() + numRepeatDocs) % 5 != 0) {
			numRepeatDocs = numRepeatDocs + (5 - ((docs.size() + numRepeatDocs) % 5));
		}

		// Create map of scores to list of docs
		Map<Integer, List<CASTDocument>> docsPerScoreMap = new HashMap<Integer, List<CASTDocument>>();
		for (CASTDocument doc : docs) {
			docsPerScoreMap.putIfAbsent(doc.getScore(), new ArrayList<CASTDocument>());
			docsPerScoreMap.get(doc.getScore()).add(doc);
		}

		// Create queue of types of repeat docs
		List<Integer> randomDocScores = new ArrayList<Integer>();
		List<Integer> scoreValues = new ArrayList<Integer>(docsPerScoreMap.keySet());
		Collections.reverse(scoreValues);
		while (randomDocScores.size() < numRepeatDocs) {
			for (Integer scoreValue : scoreValues) {
				randomDocScores.add(scoreValue);
				if (randomDocScores.size() >= numRepeatDocs) {
					break;
				}
			}
		}

		List<CASTHIT> hits = new ArrayList<CASTHIT>();

		// Calculate number of HITs
		long totalDocs = docs.size() + numRepeatDocs;
		long numHITs = totalDocs / 5;

		Collections.shuffle(randomDocScores);
		Queue<Integer> randomScoreQueue = new LinkedList<Integer>(randomDocScores);

		// Generate random doc placements
		Queue<Long> randomDocPositions = new LinkedList<Long>();
		long startDoc = totalDocs / 4;
		if (startDoc < 5) {
			startDoc = 5l;
		}
		long randomDocDist = Double.valueOf(Math.floor((totalDocs - startDoc) / (double) numRepeatDocs)).longValue();
		randomDocPositions.add(startDoc);
		long prevDoc = startDoc;
		for (int i = 1; i <= numRepeatDocs; i++) {
			long nextPos = prevDoc + randomDocDist;
			randomDocPositions.add(nextPos);
			prevDoc = nextPos;
		}

		// Iterate through HITs
		int docNum = 0;
		Queue<CASTDocument> originalDocQueue = new LinkedList<CASTDocument>(docs);
		Long currentRandomDocPos = Long.valueOf(-1);
		if (randomDocPositions != null && randomDocPositions.size() > 0) {
			currentRandomDocPos = randomDocPositions.poll();
		}
		List<CASTDocument> prevSeenDocs = new ArrayList<CASTDocument>();
		for (int i = 0; i < numHITs; i++) {
			CASTHIT hit = new CASTHIT();
			hit.setQueryNum(qrel.getQueryNum());
			hit.setTopicQueryNum(Integer.valueOf(qrel.getGroupQueryNum()));
			hit.setSubQueryNum(Integer.valueOf(qrel.getSubQueryNum()));
			hit.setQueryText(getQueryText(queries, qrel));
			hit.setHitCount(String.join(" ", "HIT number", String.valueOf(i + 1), "of", String.valueOf(numHITs),
					"for question", qrel.getSubQueryNum()));

			boolean lastHIT = false;
			if (i == numHITs - 1) {
				lastHIT = true;
			}
			hit.setLastHITinQuestion(lastHIT);

			List<CASTDocument> hitDocs = new ArrayList<CASTDocument>();
			for (int j = 0; j < 5; j++) {
				if (currentRandomDocPos != null && currentRandomDocPos.intValue() == docNum) {
					int randomScoreValue = 4;
					if (randomScoreQueue != null && randomScoreQueue.size() > 0) {
						randomScoreValue = randomScoreQueue.poll();
					}
					Iterator<CASTDocument> docIter = prevSeenDocs.iterator();
					int numLoopTimes = 0;
					int maxLoopTimes = scoreValues.size();
					if (randomScoreQueue.size() > scoreValues.size()) {
						maxLoopTimes = randomScoreQueue.size() * 100;
					}
					maxLoopTimes *= prevSeenDocs.size();
					while (currentRandomDocPos.intValue() == docNum && docIter.hasNext()
							&& numLoopTimes < maxLoopTimes) {
						// System.out.println("In random doc loop");
						CASTDocument tempDoc = docIter.next();
						if (tempDoc.getScore() == randomScoreValue) {
							hitDocs.add(tempDoc);
							prevSeenDocs.remove(tempDoc);
							docNum++;
						} else if (!docIter.hasNext()) {
							randomScoreQueue.add(randomScoreValue);
							randomScoreValue = randomScoreQueue.poll();
							docIter = prevSeenDocs.iterator();
						}
						numLoopTimes++;
					}
					// If no other document works
					if (currentRandomDocPos.intValue() == docNum) {
						System.out.println("Loading default random doc");
						if (prevSeenDocs.size() > 0) {
							if (prevSeenDocs.get(0) == null) {
								System.out.println("Doc is null");
							}
							hitDocs.add(prevSeenDocs.get(0));
							prevSeenDocs.remove(0);
							docNum++;
						} else {
							System.out.println("No previously seen docs!");
						}
					}
					currentRandomDocPos = randomDocPositions.poll();
				} else {
					CASTDocument originalDoc = originalDocQueue.poll();
					if (originalDoc == null) {
						System.out.println("Original doc is null");
					}
					hitDocs.add(originalDoc);
					prevSeenDocs.add(originalDoc);
					docNum++;
				}
			}
			hit.setDocs(hitDocs);
			hits.add(hit);
			// prevSeenDocs.addAll(hitDocs);
		}

		return hits;
	}

	private List<CASTHIT> createHITs(List<CASTDocument> docs, Qrel qrel, List<CASTQuery> queries) {
		// Get number of random documents needed
		double fivePercent = docs.size() * 0.05;
		long numRepeatDocs = Math.round(fivePercent);
		if ((docs.size() + numRepeatDocs) % 5 != 0) {
			numRepeatDocs = numRepeatDocs + (5 - ((docs.size() + numRepeatDocs) % 5));
		}

		// Create map of scores to list of docs
		Map<Integer, List<CASTDocument>> docsPerScoreMap = new HashMap<Integer, List<CASTDocument>>();
		for (CASTDocument doc : docs) {
			docsPerScoreMap.putIfAbsent(doc.getScore(), new ArrayList<CASTDocument>());
			docsPerScoreMap.get(doc.getScore()).add(doc);
		}

		// Find repeat docs
		List<CASTDocument> repeatDocs = new ArrayList<CASTDocument>();
		List<Integer> scoreValues = new ArrayList<Integer>(docsPerScoreMap.keySet());
		Collections.reverse(scoreValues);
		int indexNum = 0;
		while (repeatDocs.size() < numRepeatDocs) {
			for (Integer scoreValue : scoreValues) {
				List<CASTDocument> docsForScore = docsPerScoreMap.get(scoreValue);
				if (docsForScore.size() > indexNum) {
					repeatDocs.add(docsForScore.get(indexNum));
					if (repeatDocs.size() >= numRepeatDocs) {
						break;
					}
				}
			}
			indexNum++;
		}
		docs.addAll(repeatDocs);

		List<CASTHIT> hits = new ArrayList<CASTHIT>();
		int docNum = 0;
		for (int i = 0; i < (docs.size() / 5); i++) {
			CASTHIT hit = new CASTHIT();
			hit.setQueryNum(qrel.getQueryNum());
			hit.setTopicQueryNum(Integer.valueOf(qrel.getGroupQueryNum()));
			hit.setSubQueryNum(Integer.valueOf(qrel.getSubQueryNum()));
			hit.setQueryText(getQueryText(queries, qrel));

			List<CASTDocument> hitDocs = new ArrayList<CASTDocument>();
			for (int j = docNum; j < (docNum + 5); j++) {
				hitDocs.add(docs.get(j));
			}
			docNum += 5;
			hit.setDocs(hitDocs);
			hits.add(hit);
		}

		return hits;
	}

	private String getQueryText(List<CASTQuery> queries, Qrel qrel) {
		String firstQueryNum = qrel.getGroupQueryNum();
		int secondQueryNum = Integer.valueOf(qrel.getSubQueryNum()).intValue();

		StringJoiner queryBuffer = new StringJoiner("<br/>");

		for (CASTQuery query : queries) {
			if (query.getQueryNum().startsWith(firstQueryNum)) {
				if (Integer.valueOf(query.getQueryNum().substring(query.getQueryNum().indexOf("_") + 1))
						.intValue() < secondQueryNum) {
					queryBuffer.add(query.getQueryText());
				} else if (Integer.valueOf(query.getQueryNum().substring(query.getQueryNum().indexOf("_") + 1))
						.intValue() == secondQueryNum) {
					String highlightedQuery = String.join("", "<span>", query.getQueryText(), "</span>");
					queryBuffer.add(highlightedQuery);
				}
			}
		}
		String queryString = String.join("", "\" ", queryBuffer.toString(), "\"");
		return queryString;
	}

	public List<CASTQuery> getQueries() throws FileNotFoundException {
		List<CASTQuery> queries = new ArrayList<CreateMTurkCastData.CASTQuery>();

		Scanner scanner = new Scanner(new File(properties.getQueries()));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split("\t");

			CASTQuery query = new CASTQuery();
			query.setQueryNum(lineParts[0]);
			query.setQueryText(lineParts[1].trim());

//			int splitIndex = lineParts[1].indexOf(' ');
//			CASTQuery query = new CASTQuery();
//			query.setQueryNum(lineParts[1].substring(0, splitIndex));
//			query.setQueryText(lineParts[1].substring(splitIndex).trim());

			queries.add(query);
		}
		scanner.close();
		return queries;
	}

	public List<CASTQuery> getQueriesJson() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		List<CASTQuery> queries = new ArrayList<CreateMTurkCastData.CASTQuery>();

		Gson gson = new Gson();

		QueryObject[] queriesObject = gson.fromJson(new FileReader(properties.getQueries()), QueryObject[].class);
		for (QueryObject query : queriesObject) {
			for (SubQueryObject subQuery : query.getTurn()) {
				CASTQuery castQuery = new CASTQuery();
				castQuery.setQueryNum(String.join("_", query.getNumber().toString(), subQuery.getNumber().toString()));
				castQuery.setQueryText(subQuery.getRaw_utterance());
				queries.add(castQuery);
			}
		}

		return queries;
	}

	public List<Qrel> getQrels() throws FileNotFoundException {
		List<Qrel> qrels = new ArrayList<Qrel>();
		Map<String, List<Qrel>> topic2QrelMap = new HashMap<String, List<Qrel>>();

		Scanner scanner = new Scanner(new File(properties.getQrels()));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split(" ");
			Qrel qrel = new Qrel();
			if (lineParts.length > 2) {
				qrel.setDocId(lineParts[2]);
				qrel.setQueryNum(lineParts[0]);
				qrel.setGroupQueryNum(lineParts[0].substring(0, lineParts[0].indexOf("_")));
				qrel.setSubQueryNum(lineParts[0].substring(lineParts[0].indexOf("_") + 1));
				qrel.setScore(lineParts[3]);
			} else {
				qrel.setDocId(lineParts[1]);
				qrel.setQueryNum(lineParts[0]);
				qrel.setGroupQueryNum(lineParts[0].substring(0, lineParts[0].indexOf("_")));
				qrel.setSubQueryNum(lineParts[0].substring(lineParts[0].indexOf("_") + 1));
				qrel.setScore("0");
			}
			topic2QrelMap.putIfAbsent(qrel.getGroupQueryNum(), new ArrayList<Qrel>());
			topic2QrelMap.get(qrel.getGroupQueryNum()).add(qrel);
		}
		scanner.close();

		String[] topicNums = properties.getTopicNums().split(",");
		for (String topic : topicNums) {
			List<Qrel> topicQrels = topic2QrelMap.get(topic);
			Comparator<Qrel> compareById = new Comparator<Qrel>() {
				@Override
				public int compare(Qrel q1, Qrel q2) {
					return q1.getSubQueryNum().compareTo(q2.getSubQueryNum());
				}
			};
			Collections.sort(topicQrels, compareById);
			qrels.addAll(topicQrels);
		}

		return qrels;
	}

	public class CASTQuery {
		private String queryNum;
		private String queryText;

		public String getQueryNum() {
			return queryNum;
		}

		public void setQueryNum(String queryNum) {
			this.queryNum = queryNum;
		}

		public String getQueryText() {
			return queryText;
		}

		public void setQueryText(String queryText) {
			this.queryText = queryText;
		}
	}

}
