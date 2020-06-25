package org.lemurproject.mturkadmin;

import java.util.StringJoiner;

public class JudgedDocumentObject {

	private String hitId;
	private String queryId;

	private String docId;
	private String workerId;
	private String assignmentId;
	private String time;
	private String trecScore;
	private String trecBinaryScore;
	private String workerScore;
	private String workerBinaryScore;

	public String getHitId() {
		return hitId;
	}

	public void setHitId(String hitId) {
		this.hitId = hitId;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTrecScore() {
		return trecScore;
	}

	public void setTrecScore(String trecScore) {
		this.trecScore = trecScore;
		int trecScoreInt = Integer.valueOf(trecScore).intValue();
		if (trecScoreInt < 2) {
			this.trecBinaryScore = "0";
		} else {
			this.trecBinaryScore = "1";
		}
	}

	public String getWorkerScore() {
		return workerScore;
	}

	public void setWorkerScore(String workerScore) {
		this.workerScore = workerScore;
		int workerScoreInt = Integer.valueOf(workerScore).intValue();
		if (workerScoreInt < 2) {
			this.workerBinaryScore = "0";
		} else {
			this.workerBinaryScore = "1";
		}
	}

	public String getCsvHeaders() {
		return "hitId,queryId,docId,workerId,assignmentId,time,trecScore,workerScore,trecBinaryScore,workerBinaryScore\n";
	}

	@Override
	public String toString() {
		StringJoiner csvStringBuffer = new StringJoiner(",");
		csvStringBuffer.add(hitId);
		csvStringBuffer.add(queryId);
		csvStringBuffer.add(docId);
		csvStringBuffer.add(workerId);
		csvStringBuffer.add(assignmentId);
		csvStringBuffer.add(time);
		csvStringBuffer.add(trecScore);
		csvStringBuffer.add(workerScore);
		csvStringBuffer.add(trecBinaryScore);
		csvStringBuffer.add(workerBinaryScore);
		return String.join("", csvStringBuffer.toString(), "\n");
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

}
