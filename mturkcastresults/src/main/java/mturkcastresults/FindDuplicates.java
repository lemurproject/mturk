package mturkcastresults;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class FindDuplicates {

	private Map<String, HITObject> hitMap;

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(new File("submitteddocuments_43_20200602.csv"));
		Map<String, Map<String, List<String>>> dupMap = new HashMap<String, Map<String, List<String>>>();
		while (scanner.hasNext()) {
			String docLine = scanner.nextLine();
			String[] docParts = docLine.split(",");
			String queryId = docParts[1];
			String docId = docParts[2];
			String score = docParts[7];
			dupMap.putIfAbsent(queryId, new HashMap<String, List<String>>());
			dupMap.get(queryId).putIfAbsent(docId, new ArrayList<String>());
			dupMap.get(queryId).get(docId).add(score);
		}

		Writer dupsWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("duplicates_43_3.csv"), "UTF8"));
		for (String queryId : dupMap.keySet()) {
			for (String docId : dupMap.get(queryId).keySet()) {
				if (dupMap.get(queryId).get(docId).size() > 1) {
					dupsWriter.append(String.join(",", queryId, docId));
					dupsWriter.append(",");
					dupsWriter.append(String.join(",", dupMap.get(queryId).get(docId)));
					dupsWriter.append("\n");
				}
			}
		}

		dupsWriter.close();
		scanner.close();
	}

	public List<AnswerObject> getAllHITs() throws IOException {
		Reader reader = Files.newBufferedReader(Paths.get("mturk_cast_43.csv"));
		CsvToBean<HITObject> csvToBean = new CsvToBeanBuilder(reader).withType(HITObject.class)
				.withIgnoreLeadingWhiteSpace(true).build();
		Iterator<HITObject> hitIterator = csvToBean.iterator();

		List<AnswerObject> answers = new ArrayList<AnswerObject>();
		hitMap = new HashMap<String, HITObject>();
		while (hitIterator.hasNext()) {
			HITObject hitObject = hitIterator.next();

			hitMap.put(hitObject.getHitId(), hitObject);

			AnswerObject answer = new AnswerObject();
			answer.setHitId(hitObject.getHitId());
			answer.setDocId_1(hitObject.getDocument1());
			answer.setDocId_2(hitObject.getDocument2());
			answer.setDocId_3(hitObject.getDocument3());
			answer.setDocId_4(hitObject.getDocument4());
			answer.setDocId_5(hitObject.getDocument5());
			answers.add(answer);
		}

		return answers;
	}

}
