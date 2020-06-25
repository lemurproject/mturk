package org.lemurproject.mturkcastdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class GlobalProperties {

	@Value("${queries}")
	private String queries;

	@Value("${qrels}")
	private String qrels;

	@Value("${hit.csv}")
	private String hitCsv;

	@Value("${car.index}")
	private String carIndex;

	@Value("${marco.index}")
	private String marcoIndex;

	@Value("${topic.nums}")
	private String topicNums;

	public String getQueries() {
		return queries;
	}

	public void setQueries(String queries) {
		this.queries = queries;
	}

	public String getQrels() {
		return qrels;
	}

	public void setQrels(String qrels) {
		this.qrels = qrels;
	}

	public String getCarIndex() {
		return carIndex;
	}

	public void setCarIndex(String carIndex) {
		this.carIndex = carIndex;
	}

	public String getMarcoIndex() {
		return marcoIndex;
	}

	public void setMarcoIndex(String marcoIndex) {
		this.marcoIndex = marcoIndex;
	}

	public String getHitCsv() {
		return hitCsv;
	}

	public void setHitCsv(String hitCsv) {
		this.hitCsv = hitCsv;
	}

	public String getTopicNums() {
		return topicNums;
	}

	public void setTopicNums(String topicNums) {
		this.topicNums = topicNums;
	}

}
