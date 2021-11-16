package org.lemurproject.mturkcwdeep;

import java.util.StringJoiner;

import com.opencsv.bean.CsvBindByName;

public class HITSimpleObject {

	@CsvBindByName
	private String docId;

	@CsvBindByName
	private String queryNum;

	@CsvBindByName
	private String workerId;

	@CsvBindByName
	private String mturkHitId;

	public String getMturkHitId() {
		return mturkHitId;
	}

	public void setMturkHitId(String mturkHitId) {
		this.mturkHitId = mturkHitId;
	}

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

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	@Override
	public String toString() {
		StringJoiner hitObjectBuffer = new StringJoiner(",");
		// "hitId,queryNum,topicNum,subQueryNum,workerId\n"
		hitObjectBuffer.add(String.join("", "\"", docId, "\""));
		hitObjectBuffer.add(String.join("", "\"", queryNum, "\""));
		hitObjectBuffer.add(String.join("", "\"", workerId, "\""));
		hitObjectBuffer.add(String.join("", "\"", mturkHitId, "\""));
		return hitObjectBuffer.toString();
	}

}
