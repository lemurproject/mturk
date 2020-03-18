package org.lemurproject.mturkcastdata;

import java.util.List;

public class CASTHIT {

	private String queryNum;
	private int topicQueryNum;
	private int subQueryNum;
	private String queryText;
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

}
