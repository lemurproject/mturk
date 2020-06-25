package org.lemurproject.mturkcastdata;

import java.util.List;

public class CASTHIT {

	private String queryNum;
	private int topicQueryNum;
	private int subQueryNum;
	private String queryText;
	private String hitCount;
	private boolean lastHITinQuestion;
	private List<CASTDocument> docs;

	public String getQueryNum() {
		return queryNum;
	}

	public void setQueryNum(String queryNum) {
		this.queryNum = queryNum;
	}

	public int getSubQueryNum() {
		return subQueryNum;
	}

	public void setSubQueryNum(int subQueryNum) {
		this.subQueryNum = subQueryNum;
	}

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public List<CASTDocument> getDocs() {
		return docs;
	}

	public void setDocs(List<CASTDocument> docs) {
		this.docs = docs;
	}

	public int getTopicQueryNum() {
		return topicQueryNum;
	}

	public void setTopicQueryNum(int topicQueryNum) {
		this.topicQueryNum = topicQueryNum;
	}

	public String getHitCount() {
		return hitCount;
	}

	public void setHitCount(String hitCount) {
		this.hitCount = hitCount;
	}

	public boolean isLastHITinQuestion() {
		return lastHITinQuestion;
	}

	public void setLastHITinQuestion(boolean lastHITinQuestion) {
		this.lastHITinQuestion = lastHITinQuestion;
	}

}
