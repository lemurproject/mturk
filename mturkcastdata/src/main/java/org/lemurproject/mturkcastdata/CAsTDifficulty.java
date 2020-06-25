package org.lemurproject.mturkcastdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

public class CAsTDifficulty {

	public static void main(String[] args) throws IOException {
		Map<String, Map<String, Integer>> topicScoreMap = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> topicBinaryScoreMap = new HashMap<String, Map<String, Integer>>();

		// Scanner scanner = new Scanner(new File("CAST2019.qrels"));
		Scanner scanner = new Scanner(new File("new_poolruns_3_10.txt"));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String[] lineParts = line.split(" ");
			// if (lineParts[2].startsWith("MARCO")) {
			Qrel qrel = new Qrel();
			qrel.setDocId(lineParts[1]);
			qrel.setQueryNum(lineParts[0]);
			qrel.setGroupQueryNum(lineParts[0].substring(0, lineParts[0].indexOf("_")));
			qrel.setSubQueryNum(lineParts[0].substring(lineParts[0].indexOf("_") + 1));
			// qrel.setScore(lineParts[3]);
			qrel.setScore("0");

			topicBinaryScoreMap.putIfAbsent(qrel.getGroupQueryNum(), new HashMap<String, Integer>());
			topicBinaryScoreMap.get(qrel.getGroupQueryNum()).putIfAbsent(qrel.getBinaryScore(), Integer.valueOf(0));
			topicBinaryScoreMap.get(qrel.getGroupQueryNum()).put(qrel.getBinaryScore(),
					topicBinaryScoreMap.get(qrel.getGroupQueryNum()).get(qrel.getBinaryScore()) + 1);

			topicScoreMap.putIfAbsent(qrel.getGroupQueryNum(), new HashMap<String, Integer>());
			topicScoreMap.get(qrel.getGroupQueryNum()).putIfAbsent(qrel.getScore(), Integer.valueOf(0));
			topicScoreMap.get(qrel.getGroupQueryNum()).put(qrel.getScore(),
					topicScoreMap.get(qrel.getGroupQueryNum()).get(qrel.getScore()) + 1);

			// }
		}
		scanner.close();

//		Writer scoreWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("castScores.csv"), "UTF8"));
//		scoreWriter.write("Topic Number,0,1,2,3,4\n");
//		for (int i = 31; i < 80; i++) {
//			if (topicScoreMap.get(String.valueOf(i)) != null) {
//				StringJoiner lineBuffer = new StringJoiner(",");
//				lineBuffer.add(String.valueOf(i));
//				Map<String, Integer> topicScores = topicScoreMap.get(String.valueOf(i));
//				lineBuffer.add(topicScores.get("0").toString());
//				lineBuffer.add(topicScores.get("1").toString());
//				lineBuffer.add(topicScores.get("2").toString());
//				lineBuffer.add(topicScores.get("3").toString());
//				lineBuffer.add(topicScores.get("4").toString());
//				scoreWriter.write(lineBuffer.toString());
//				scoreWriter.write("\n");
//			}
//		}
//		scoreWriter.close();

		Writer binaryWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream("poolScores.csv"), "UTF8"));
		binaryWriter.write("Topic Number,0,1\n");
		for (int i = 31; i < 80; i++) {
			if (topicBinaryScoreMap.get(String.valueOf(i)) != null) {
				StringJoiner lineBuffer = new StringJoiner(",");
				lineBuffer.add(String.valueOf(i));
				Map<String, Integer> topicScores = topicBinaryScoreMap.get(String.valueOf(i));
				lineBuffer.add(topicScores.get("0").toString());
				// lineBuffer.add(topicScores.get("1").toString());
				binaryWriter.write(lineBuffer.toString());
				binaryWriter.write("\n");
			}
		}
		binaryWriter.close();
	}

}
