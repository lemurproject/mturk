package org.lemurproject.mturkcast;

import java.util.StringJoiner;

import com.opencsv.bean.CsvBindByName;

public class HITSimpleObject {

	@CsvBindByName
	private String hitId;

	@CsvBindByName
	private String topicNum;

	@CsvBindByName
	private String queryNum;

	@CsvBindByName
	private String subQueryNum;

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

	public String getSubQueryNum() {
		return subQueryNum;
	}

	public void setSubQueryNum(String subQueryNum) {
		this.subQueryNum = subQueryNum;
	}

	public String getHitId() {
		return hitId;
	}

	public void setHitId(String hitId) {
		this.hitId = hitId;
	}

	public String getTopicNum() {
		return topicNum;
	}

	public void setTopicNum(String topicNum) {
		this.topicNum = topicNum;
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
		hitObjectBuffer.add(String.join("", "\"", hitId, "\""));
		hitObjectBuffer.add(String.join("", "\"", queryNum, "\""));
		hitObjectBuffer.add(String.join("", "\"", topicNum, "\""));
		hitObjectBuffer.add(String.join("", "\"", subQueryNum, "\""));
		hitObjectBuffer.add(String.join("", "\"", workerId, "\""));
		hitObjectBuffer.add(String.join("", "\"", mturkHitId, "\""));
		return hitObjectBuffer.toString();
	}

}
