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
import java.util.List;
import java.util.Scanner;
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
		List<CASTQuery> queries = getQueriesJson();
		List<Qrel> qrels = getQrels();

		Directory marcoDir = FSDirectory.open(Paths.get(properties.getMarcoIndex()));
		IndexReader marcoReader = DirectoryReader.open(marcoDir);
		IndexSearcher marcoSearcher = new IndexSearcher(marcoReader);

		Directory carDir = FSDirectory.open(Paths.get(properties.getCarIndex()));
		IndexReader carReader = DirectoryReader.open(carDir);
		IndexSearcher carSearcher = new IndexSearcher(carReader);

		// BufferedWriter csvWriter = new BufferedWriter(new
		// FileWriter("mturk_cast.csv"), "UTF8");
		Writer csvWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("mturk_cast_500.csv"), "UTF8"));
		csvWriter.write("queryNum,subQueryNum,query,text,document,score\n");

		QueryParser qp = new QueryParser("externalId", new StandardAnalyzer());

		int num = 0;
		for (Qrel qrel : qrels) {
			num++;
			if (num % 4 == 0) {
				String docId = qrel.getDocId();
				StringJoiner docLine = new StringJoiner(",");
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
						// System.out.println(doc.get("body"));
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
						// System.out.println(doc.get("body"));
					}
				}
				docLine.add(qrel.getQueryNum());
				docLine.add(qrel.getSubQueryNum());
				String queryText = getQueryText(queries, qrel.queryNum);
				docLine.add(queryText);
				String docText = doc.get("body");
				docText = docText.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?! ]", "");
				docText = String.join("", "\" ", docText, "\"");
				docLine.add(docText);
				docLine.add(docId);
				docLine.add(qrel.getScore());
				csvWriter.write(docLine.toString());
				csvWriter.write("\n");
			}
			if (num >= 2000) {
				break;
			}
		}
		csvWriter.close();
	}

	private String getQueryText(List<CASTQuery> queries, String queryNum) {
		String[] queryNums = queryNum.split("_");
		String firstQueryNum = queryNums[0];
		int secondQueryNum = Integer.valueOf(queryNums[1]).intValue();

		StringJoiner queryBuffer = new StringJoiner("<br/>");

		for (CASTQuery query : queries) {
			if (query.getQueryNum().startsWith(firstQueryNum)) {
				if (Integer.valueOf(query.getQueryNum().substring(query.getQueryNum().indexOf("_") + 1))
						.intValue() < secondQueryNum) {
					queryBuffer.add(query.getQueryText());
				} else if (Integer.valueOf(query.getQueryNum().substring(query.getQueryNum().indexOf("_") + 1))
						.intValue() == secondQueryNum) {
					String highlightedQuery = String.join("", "<span>",
							query.getQueryText(), "</span>");
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
			int splitIndex = lineParts[1].indexOf(' ');
			CASTQuery query = new CASTQuery();
			query.setQueryNum(lineParts[1].substring(0, splitIndex));
			query.setQueryText(lineParts[1].substring(splitIndex).trim());

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
		List<Qrel> qrels = new ArrayList<CreateMTurkCastData.Qrel>();

		Scanner scanner = new Scanner(new File(properties.getQrels()));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split(" ");
			if (lineParts[2].startsWith("MARCO")) {
				Qrel qrel = new Qrel();
				qrel.setDocId(lineParts[2]);
				qrel.setQueryNum(lineParts[0]);
				qrel.setSubQueryNum(lineParts[0].substring(lineParts[0].indexOf("_") + 1));
				qrel.setScore(lineParts[3]);

				qrels.add(qrel);
			}
		}
		scanner.close();
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

	public class Qrel {
		private String queryNum;
		private String subQueryNum;
		private String docId;
		private String score;

		public String getQueryNum() {
			return queryNum;
		}

		public void setQueryNum(String queryNum) {
			this.queryNum = queryNum;
		}

		public String getDocId() {
			return docId;
		}

		public void setDocId(String docId) {
			this.docId = docId;
		}

		public String getScore() {
			return score;
		}

		public void setScore(String score) {
			this.score = score;
		}

		@Override
		public String toString() {
			return String.join(" ", queryNum, docId, score);
		}

		public String getSubQueryNum() {
			return subQueryNum;
		}

		public void setSubQueryNum(String subQueryNum) {
			this.subQueryNum = subQueryNum;
		}

	}

}
