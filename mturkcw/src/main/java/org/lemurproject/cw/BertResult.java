package org.lemurproject.cw;

import org.springframework.stereotype.Component;

@Component
public class BertResult {

	private String docId;
	private String title;
	private String url;
	private String highlight;

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

	public String getHighlight() {
		return highlight;
	}

	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

}
