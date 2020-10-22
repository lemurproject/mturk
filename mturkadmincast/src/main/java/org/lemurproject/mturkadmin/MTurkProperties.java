package org.lemurproject.mturkadmin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class MTurkProperties {

	@Value("${function}")
	private String function;

	@Value("${environment}")
	private String environment;

	@Value("${addtopics.url}")
	private String addTopicsUrl;

	@Value("${addtopicname.url}")
	private String addTopicNameUrl;

	@Value("${question.filename}")
	private String questionFilename;

	@Value("${data.filename}")
	private String dataFilename;

	@Value("${hit2topic.filename}")
	private String hit2topicFilename;

	@Value("${qualification.type}")
	private String qualificationType;

	@Value("${topic.name}")
	private String topicName;

	@Value("${hit.lifetime}")
	private String hitLifetime;

	@Value("${judgeddocuments.csv}")
	private String judgedDocumentsCsvName;

	@Value("${highest.hit}")
	private String highestSeenHIT;

	public String getQuestionFilename() {
		return questionFilename;
	}

	public void setQuestionFilename(String questionFilename) {
		this.questionFilename = questionFilename;
	}

	public String getDataFilename() {
		return dataFilename;
	}

	public void setDataFilename(String dataFilename) {
		this.dataFilename = dataFilename;
	}

	public String getHit2topicFilename() {
		return hit2topicFilename;
	}

	public void setHit2topicFilename(String hit2topicFilename) {
		this.hit2topicFilename = hit2topicFilename;
	}

	public String getQualificationType() {
		return qualificationType;
	}

	public void setQualificationType(String qualificationType) {
		this.qualificationType = qualificationType;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getJudgedDocumentsCsvName() {
		return judgedDocumentsCsvName;
	}

	public void setJudgedDocumentsCsvName(String judgedDocumentsCsvName) {
		this.judgedDocumentsCsvName = judgedDocumentsCsvName;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getHighestSeenHIT() {
		return highestSeenHIT;
	}

	public void setHighestSeenHIT(String highestSeenHIT) {
		this.highestSeenHIT = highestSeenHIT;
	}

	public String getAddTopicsUrl() {
		return addTopicsUrl;
	}

	public void setAddTopicsUrl(String addTopicsUrl) {
		this.addTopicsUrl = addTopicsUrl;
	}

	public String getHitLifetime() {
		return hitLifetime;
	}

	public void setHitLifetime(String hitLifetime) {
		this.hitLifetime = hitLifetime;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getAddTopicNameUrl() {
		return addTopicNameUrl;
	}

	public void setAddTopicNameUrl(String addTopicNameUrl) {
		this.addTopicNameUrl = addTopicNameUrl;
	}

}
