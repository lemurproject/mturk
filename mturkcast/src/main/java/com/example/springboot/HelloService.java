package com.example.springboot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("helloService")
public class HelloService {
	
	@Autowired
	private GlobalProperties properties;
	
	private int userNum = 0;
	private List<TaskInformation> tasks;
	
	public HelloService() {
		tasks = new ArrayList<TaskInformation>();
		
		TaskInformation taskInfo = new TaskInformation();
		taskInfo.setQueryNum("33");
		taskInfo.setSubQueryNum("1");
		List<String> docIds = new ArrayList<String>();
		docIds.add("MARCO_1001214");
		docIds.add("MARCO_1011346");
		docIds.add("MARCO_1495774");
		docIds.add("MARCO_2451103");
		docIds.add("MARCO_2715451");
		taskInfo.setDocIds(docIds);
		tasks.add(taskInfo);
		
		TaskInformation taskInfo2 = new TaskInformation();
		taskInfo2.setQueryNum("33");
		taskInfo2.setSubQueryNum("2");
		taskInfo2.setDocIds(docIds);
		tasks.add(taskInfo2);
		
		TaskInformation taskInfo3 = new TaskInformation();
		taskInfo3.setQueryNum("33");
		taskInfo3.setSubQueryNum("3");
		taskInfo3.setDocIds(docIds);
		tasks.add(taskInfo3);
		
		TaskInformation taskInfo4 = new TaskInformation();
		taskInfo4.setQueryNum("33");
		taskInfo4.setSubQueryNum("4");
		taskInfo4.setDocIds(docIds);
		tasks.add(taskInfo4);
		
		TaskInformation taskInfo5 = new TaskInformation();
		taskInfo5.setQueryNum("33");
		taskInfo5.setSubQueryNum("5");
		taskInfo5.setDocIds(docIds);
		tasks.add(taskInfo5);
	}

	public TaskInformation getTaskInformation() {
		TaskInformation taskInfo = tasks.get(userNum);
		userNum++;
		userNum = userNum % 5;
		return taskInfo;
	}
	
	public String getName() {
		return "Autowired";
	}

	public Map<Integer, Map<Integer, String>> getQueries() throws FileNotFoundException {
		Map<Integer, Map<Integer, String>> queries = new HashMap<Integer, Map<Integer, String>>();

		Scanner scanner = new Scanner(new File(properties.getQueries()));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split("\t");
			int splitIndex = lineParts[1].indexOf(' ');
			String queryNumWhole = lineParts[1].substring(0, splitIndex);
			String queryText = lineParts[1].substring(splitIndex).trim();
			String[] queryNumParts = queryNumWhole.split("_");
			Integer queryNum = Integer.valueOf(queryNumParts[0]);
			Integer subQueryNum = Integer.valueOf(queryNumParts[1]);

			queries.putIfAbsent(queryNum, new HashMap<Integer, String>());
			queries.get(queryNum).put(subQueryNum, queryText);
		}
		scanner.close();
		return queries;
	}

	public Map<Integer, Map<Integer, List<Qrel>>> getQrels() throws FileNotFoundException {
		Map<Integer, Map<Integer, List<Qrel>>> qrels = new HashMap<Integer, Map<Integer, List<Qrel>>>();

		Scanner scanner = new Scanner(new File(properties.getQrels()));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split(" ");
			if (lineParts[2].startsWith("MARCO")) {
				Qrel qrel = new Qrel();
				qrel.setDocId(lineParts[2]);
				qrel.setQueryNum(lineParts[0]);
				qrel.setScore(lineParts[3]);

				String[] queryNumParts = lineParts[0].split("_");
				Integer queryNum = Integer.valueOf(queryNumParts[0]);
				Integer subQueryNum = Integer.valueOf(queryNumParts[1]);

				qrels.putIfAbsent(queryNum, new HashMap<Integer, List<Qrel>>());
				qrels.get(queryNum).putIfAbsent(subQueryNum, new ArrayList<HelloService.Qrel>());
				qrels.get(queryNum).get(subQueryNum).add(qrel);
			}
		}
		scanner.close();
		return qrels;
	}
	
	public String getDocument(String docId) {
		String document = null;
		try {
			String path = String.join(File.separator, properties.getDocumentsPath(), docId);

	        try {
	            // default StandardCharsets.UTF_8
	            document = Files.readString(Paths.get(path));

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} catch (Exception e) {
			
		}
		return document;
	}

	public class Qrel {
		private String queryNum;
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

	}

}
