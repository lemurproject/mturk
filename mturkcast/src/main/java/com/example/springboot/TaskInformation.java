package com.example.springboot;

import java.util.List;

public class TaskInformation {
	
	private String queryNum;
	private String subQueryNum;
	private List<String> docIds;
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
	public List<String> getDocIds() {
		return docIds;
	}
	public void setDocIds(List<String> docIds) {
		this.docIds = docIds;
	}

}
