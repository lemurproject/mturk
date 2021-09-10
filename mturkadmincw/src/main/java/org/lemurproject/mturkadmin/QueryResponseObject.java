package org.lemurproject.mturkadmin;

public class QueryResponseObject {

	private String mturkAssessorId;
	private String HITid;
	private String AssignmentId;
	private String experimentTimeOfDay;
	private String experimentDayOfWeek;
	private long startTime;
	private long endTime;
	private double time;
	private double minutes;
	private double inputTime;
	private double queryTime;
	private String query;
	private String description;
	private String category;
	private String doc1id;
	private boolean doc1selected = false;
	private String doc2id;
	private boolean doc2selected = false;
	private String doc3id;
	private boolean doc3selected = false;
	private String doc4id;
	private boolean doc4selected = false;
	private String doc5id;
	private boolean doc5selected = false;
	private String doc6id;
	private boolean doc6selected = false;
	private String doc7id;
	private boolean doc7selected = false;
	private String doc8id;
	private boolean doc8selected = false;
	private String doc9id;
	private boolean doc9selected = false;
	private String doc10id;
	private boolean doc10selected = false;
	private String doc11id;
	private boolean doc11selected = false;
	private String doc12id;
	private boolean doc12selected = false;
	private String doc13id;
	private boolean doc13selected = false;
	private String numNRselected;
	private String numRelevantSelected;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDoc1id() {
		return doc1id;
	}

	public void setDoc1id(String doc1id) {
		this.doc1id = doc1id;
	}

	public boolean isDoc1selected() {
		return doc1selected;
	}

	public void setDoc1selected(boolean doc1selected) {
		this.doc1selected = doc1selected;
	}

	public String getDoc2id() {
		return doc2id;
	}

	public void setDoc2id(String doc2id) {
		this.doc2id = doc2id;
	}

	public boolean isDoc2selected() {
		return doc2selected;
	}

	public void setDoc2selected(boolean doc2selected) {
		this.doc2selected = doc2selected;
	}

	public String getDoc3id() {
		return doc3id;
	}

	public void setDoc3id(String doc3id) {
		this.doc3id = doc3id;
	}

	public boolean isDoc3selected() {
		return doc3selected;
	}

	public void setDoc3selected(boolean doc3selected) {
		this.doc3selected = doc3selected;
	}

	public String getDoc4id() {
		return doc4id;
	}

	public void setDoc4id(String doc4id) {
		this.doc4id = doc4id;
	}

	public boolean isDoc4selected() {
		return doc4selected;
	}

	public void setDoc4selected(boolean doc4selected) {
		this.doc4selected = doc4selected;
	}

	public String getDoc5id() {
		return doc5id;
	}

	public void setDoc5id(String doc5id) {
		this.doc5id = doc5id;
	}

	public boolean isDoc5selected() {
		return doc5selected;
	}

	public void setDoc5selected(boolean doc5selected) {
		this.doc5selected = doc5selected;
	}

	public String getDoc6id() {
		return doc6id;
	}

	public void setDoc6id(String doc6id) {
		this.doc6id = doc6id;
	}

	public boolean isDoc6selected() {
		return doc6selected;
	}

	public void setDoc6selected(boolean doc6selected) {
		this.doc6selected = doc6selected;
	}

	public String getDoc7id() {
		return doc7id;
	}

	public void setDoc7id(String doc7id) {
		this.doc7id = doc7id;
	}

	public boolean isDoc7selected() {
		return doc7selected;
	}

	public void setDoc7selected(boolean doc7selected) {
		this.doc7selected = doc7selected;
	}

	public String getDoc8id() {
		return doc8id;
	}

	public void setDoc8id(String doc8id) {
		this.doc8id = doc8id;
	}

	public boolean isDoc8selected() {
		return doc8selected;
	}

	public void setDoc8selected(boolean doc8selected) {
		this.doc8selected = doc8selected;
	}

	public String getDoc9id() {
		return doc9id;
	}

	public void setDoc9id(String doc9id) {
		this.doc9id = doc9id;
	}

	public boolean isDoc9selected() {
		return doc9selected;
	}

	public void setDoc9selected(boolean doc9selected) {
		this.doc9selected = doc9selected;
	}

	public String getDoc10id() {
		return doc10id;
	}

	public void setDoc10id(String doc10id) {
		this.doc10id = doc10id;
	}

	public boolean isDoc10selected() {
		return doc10selected;
	}

	public void setDoc10selected(boolean doc10selected) {
		this.doc10selected = doc10selected;
	}

	public String getMturkAssessorId() {
		return mturkAssessorId;
	}

	public void setMturkAssessorId(String mturkAssessorId) {
		this.mturkAssessorId = mturkAssessorId;
	}

	public String getHITid() {
		return HITid;
	}

	public void setHITid(String hITid) {
		HITid = hITid;
	}

	public String getAssignmentId() {
		return AssignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		AssignmentId = assignmentId;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public String getCsvHeaders() {
		String headers = String.join(",", "HITid", "AssignmentId", "WorkerId", "Time of Day", "Day of Week",
				"Start Time", "End Time", "Time", "Minutes", "Input Time (s)", "Query Time (s)", "Query", "Description",
				"Category", "doc1id", "doc1selected", "doc2id", "doc2selected", "doc3id", "doc3selected", "doc4id",
				"doc4selected", "doc5id", "doc5selected", "doc6id", "doc6selected", "doc7id", "doc7selected", "doc8id",
				"doc8selected", "doc9id", "doc9selected", "doc10id", "doc10selected", "doc11id", "doc11selected",
				"doc12id", "doc12selected", "doc13id", "doc13selected", "Num Relevant Selected", "Num NR Selected");
		return String.join("", headers, "\n");
	}

	public String getCsvValues() {
		String values = String.join(",", HITid, AssignmentId, mturkAssessorId, experimentTimeOfDay, experimentDayOfWeek,
				String.valueOf(startTime), String.valueOf(endTime), String.valueOf(time), String.valueOf(minutes),
				String.valueOf(inputTime), String.valueOf(queryTime), query, description, category, doc1id,
				String.valueOf(doc1selected), doc2id, String.valueOf(doc2selected), doc3id,
				String.valueOf(doc3selected), doc4id, String.valueOf(doc4selected), doc5id,
				String.valueOf(doc5selected), doc6id, String.valueOf(doc6selected), doc7id,
				String.valueOf(doc7selected), doc8id, String.valueOf(doc8selected), doc9id,
				String.valueOf(doc9selected), doc10id, String.valueOf(doc10selected), doc11id,
				String.valueOf(doc11selected), doc12id, String.valueOf(doc12selected), doc13id,
				String.valueOf(doc13selected), numRelevantSelected, numNRselected);
		return String.join("", values, "\n");
	}

	public String getDoc11id() {
		return doc11id;
	}

	public void setDoc11id(String doc11id) {
		this.doc11id = doc11id;
	}

	public boolean isDoc11selected() {
		return doc11selected;
	}

	public void setDoc11selected(boolean doc11selected) {
		this.doc11selected = doc11selected;
	}

	public String getDoc12id() {
		return doc12id;
	}

	public void setDoc12id(String doc12id) {
		this.doc12id = doc12id;
	}

	public boolean isDoc12selected() {
		return doc12selected;
	}

	public void setDoc12selected(boolean doc12selected) {
		this.doc12selected = doc12selected;
	}

	public String getNumNRselected() {
		return numNRselected;
	}

	public void setNumNRselected(String numNRselected) {
		this.numNRselected = numNRselected;
	}

	public String getNumRelevantSelected() {
		return numRelevantSelected;
	}

	public void setNumRelevantSelected(String numRelevantSelected) {
		this.numRelevantSelected = numRelevantSelected;
	}

	public double getMinutes() {
		return minutes;
	}

	public void setMinutes(double minutes) {
		this.minutes = minutes;
	}

	public double getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(double queryTime) {
		this.queryTime = queryTime;
	}

	public double getInputTime() {
		return inputTime;
	}

	public void setInputTime(double inputTime) {
		this.inputTime = inputTime;
	}

	public String getDoc13id() {
		return doc13id;
	}

	public void setDoc13id(String doc13id) {
		this.doc13id = doc13id;
	}

	public boolean isDoc13selected() {
		return doc13selected;
	}

	public void setDoc13selected(boolean doc13selected) {
		this.doc13selected = doc13selected;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public String getExperimentTimeOfDay() {
		return experimentTimeOfDay;
	}

	public void setExperimentTimeOfDay(String experimentTimeOfDay) {
		this.experimentTimeOfDay = experimentTimeOfDay;
	}

	public String getExperimentDayOfWeek() {
		return experimentDayOfWeek;
	}

	public void setExperimentDayOfWeek(String experimentDayOfWeek) {
		this.experimentDayOfWeek = experimentDayOfWeek;
	}
}
