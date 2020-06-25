package org.lemurproject.mturkcastdata;

public class Qrel {
	private String queryNum;
	private String groupQueryNum;
	private String subQueryNum;
	private String docId;
	private String score;
	private String binaryScore;

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

	public String getBinaryScore() {
		return binaryScore;
	}

	public void setScore(String score) {
		this.score = score;
		int intScore = Integer.valueOf(score).intValue();
		if (intScore < 2) {
			binaryScore = "0";
		} else {
			binaryScore = "1";
		}
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

	public String getGroupQueryNum() {
		return groupQueryNum;
	}

	public void setGroupQueryNum(String groupQueryNum) {
		this.groupQueryNum = groupQueryNum;
	}

}
