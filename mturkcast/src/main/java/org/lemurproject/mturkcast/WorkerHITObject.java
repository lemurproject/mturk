package org.lemurproject.mturkcast;

import com.opencsv.bean.CsvBindByName;

public class WorkerHITObject extends HITObject {

	public WorkerHITObject(String workerId, String topicId, HITObject hitObject) {
		this.setWorkerId(workerId);
		this.setTopicId(topicId);
		this.setDocument1(hitObject.getDocument1());
		this.setDocument2(hitObject.getDocument2());
		this.setDocument3(hitObject.getDocument3());
		this.setDocument4(hitObject.getDocument4());
		this.setDocument5(hitObject.getDocument5());
	}

	@CsvBindByName
	private String workerId;

	@CsvBindByName
	private String topicId;

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

}
