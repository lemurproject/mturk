package org.lemurproject.mturkcast;

import com.opencsv.bean.CsvBindByName;

public class HITMapObject {
	@CsvBindByName
	private String hitId;
	@CsvBindByName
	private String dataRowId;

	public String getHitId() {
		return hitId;
	}

	public void setHitId(String hitId) {
		this.hitId = hitId;
	}

	public String getDataRowId() {
		return dataRowId;
	}

	public void setDataRowId(String dataRowId) {
		this.dataRowId = dataRowId;
	}

}
