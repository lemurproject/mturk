package org.lemurproject.cw;

import org.springframework.stereotype.Component;

@Component
public class DocumentResult {

	private String docId;
	private String title;
	private String url;
	private String score;
	private String highlight;
	private Boolean selected;
	private Boolean filtered;

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

	public Boolean getFiltered() {
		return filtered;
	}

	public void setFiltered(Boolean filtered) {
		this.filtered = filtered;
	}

}
