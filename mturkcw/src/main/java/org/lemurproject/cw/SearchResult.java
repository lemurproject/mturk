package org.lemurproject.cw;

import java.util.List;

public class SearchResult {

	private String query;
	private String description;
	private String category;
	private List<DocumentResult> documents;
	private String assignmentId;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<DocumentResult> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentResult> documents) {
		this.documents = documents;
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
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

}