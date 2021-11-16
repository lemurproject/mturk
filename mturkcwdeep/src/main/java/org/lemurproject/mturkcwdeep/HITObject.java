package org.lemurproject.mturkcwdeep;

import java.util.StringJoiner;

import com.opencsv.bean.CsvBindByName;

public class HITObject {

	@CsvBindByName
	private String queryNum;

	@CsvBindByName
	private String query;

	@CsvBindByName
	private String hitCount;

	@CsvBindByName
	private String isLast;

	@CsvBindByName
	private String pdf;

	@CsvBindByName
	private String docId;

	@CsvBindByName
	private String score;

	@CsvBindByName
	private String workerId;

	@CsvBindByName
	private String mturkHitId;

	public String getIsLast() {
		return isLast;
	}

	public void setIsLast(String isLast) {
		this.isLast = isLast;
	}

	public String getHitCount() {
		return hitCount;
	}

	public void setHitCount(String hitCount) {
		this.hitCount = hitCount;
	}

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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public HITSimpleObject getSimpleHITObject() {
		HITSimpleObject simpleHit = new HITSimpleObject();
		simpleHit.setDocId(docId);
		simpleHit.setQueryNum(queryNum);
		simpleHit.setWorkerId(workerId);
		simpleHit.setMturkHitId(mturkHitId);

		return simpleHit;
	}

	public String writeSimpleObject() {
		StringJoiner hitObjectBuffer = new StringJoiner(",");
		// "hitId,queryNum,topicNum,subQueryNum,workerId\n"
		hitObjectBuffer.add(String.join("", "\"", docId, "\""));
		hitObjectBuffer.add(String.join("", "\"", queryNum, "\""));
		hitObjectBuffer.add(String.join("", "\"", workerId, "\""));
		hitObjectBuffer.add(String.join("", "\"", mturkHitId, "\""));
		return hitObjectBuffer.toString();
	}

	@Override
	public String toString() {
		StringJoiner hitObjectBuffer = new StringJoiner(",");
		// "hitId,queryNum,topicNum,subQueryNum,query,text1,document1,score1,text2,document2,score2,text3,document3,score3,text4,document4,score4,text5,document5,score5,workerId\n"
		hitObjectBuffer.add(String.join("", "\"", docId, "\""));
		hitObjectBuffer.add(String.join("", "\"", queryNum, "\""));
		hitObjectBuffer.add(String.join("", "\"", query, "\""));
		hitObjectBuffer.add(String.join("", "\"", pdf, "\""));
		hitObjectBuffer.add(String.join("", "\"", score, "\""));
		hitObjectBuffer.add(String.join("", "\"", workerId, "\""));
		hitObjectBuffer.add(String.join("", "\"", mturkHitId, "\""));
		return hitObjectBuffer.toString();
	}

}
