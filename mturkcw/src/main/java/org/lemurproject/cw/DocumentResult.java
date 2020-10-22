package org.lemurproject.cw;

import org.springframework.stereotype.Component;

@Component
public class DocumentResult {

	private String docId;
	private String title;
	private String url;
	private Float score;
	private String highlight;
	private Boolean selected;

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
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

}
