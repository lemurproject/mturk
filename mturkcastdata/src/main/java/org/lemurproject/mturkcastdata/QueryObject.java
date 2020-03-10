package org.lemurproject.mturkcastdata;

import java.util.List;

public class QueryObject {
	
	private Integer number;
	private String title;
	private String description;
	private List<SubQueryObject> turn;
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<SubQueryObject> getTurn() {
		return turn;
	}
	public void setTurn(List<SubQueryObject> turn) {
		this.turn = turn;
	}

}
