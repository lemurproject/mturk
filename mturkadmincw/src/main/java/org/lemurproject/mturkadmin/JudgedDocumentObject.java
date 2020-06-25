package org.lemurproject.mturkadmin;

import java.util.StringJoiner;

public class JudgedDocumentObject {

	private String docId;
	private String workerId;
	private String assignmentId;
	private String time;
	private String trecScore;
	private String workerScore;

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
	}

	public String getWorkerScore() {
		return workerScore;
	}

	public void setWorkerScore(String workerScore) {
		this.workerScore = workerScore;
	}

	public String getCsvHeaders() {
		return "docId,workerId,assignmentId,time,trecScore,workerScore\n";
	}

	@Override
	public String toString() {
		StringJoiner csvStringBuffer = new StringJoiner(",");
		csvStringBuffer.add(docId);
		csvStringBuffer.add(workerId);
		csvStringBuffer.add(assignmentId);
		csvStringBuffer.add(time);
		csvStringBuffer.add(trecScore);
		csvStringBuffer.add(workerScore);
		return String.join("", csvStringBuffer.toString(), "\n");
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

}
