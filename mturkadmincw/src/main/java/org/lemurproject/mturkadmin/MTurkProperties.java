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

	@Value("${experimentpath}")
	private String experimentpath;

	@Value("${filename}")
	private String filename;

	@Value("${question.filename}")
	private String questionFilename;

	@Value("${qualification.type}")
	private String qualificationType;

	@Value("${qualification.score}")
	private String qualificationScore;

	@Value("${qualification.name}")
	private String qualificationName;

	@Value("${qualification.filename}")
	private String qualificationFilename;

	@Value("${qualification.answerkey}")
	private String qualificationAnswerkey;

	@Value("${hit.price}")
	private String hitPrice;

	@Value("${hit.number}")
	private String hitNumber;

	@Value("${hit.assignments}")
	private String hitAssignments;

	@Value("${hit.lifetime.hours}")
	private String hitLifetimeHours;

	public String getQuestionFilename() {
		return questionFilename;
	}

	public void setQuestionFilename(String questionFilename) {
		this.questionFilename = questionFilename;
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

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getQualificationScore() {
		return qualificationScore;
	}

	public void setQualificationScore(String qualificationScore) {
		this.qualificationScore = qualificationScore;
	}

	public String getQualificationName() {
		return qualificationName;
	}

	public void setQualificationName(String qualificationName) {
		this.qualificationName = qualificationName;
	}

	public String getQualificationFilename() {
		return qualificationFilename;
	}

	public void setQualificationFilename(String qualificationFilename) {
		this.qualificationFilename = qualificationFilename;
	}

	public String getQualificationAnswerkey() {
		return qualificationAnswerkey;
	}

	public void setQualificationAnswerkey(String qualificationAnswerkey) {
		this.qualificationAnswerkey = qualificationAnswerkey;
	}

	public String getHitPrice() {
		return hitPrice;
	}

	public void setHitPrice(String hitPrice) {
		this.hitPrice = hitPrice;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getHitNumber() {
		return hitNumber;
	}

	public void setHitNumber(String hitNumber) {
		this.hitNumber = hitNumber;
	}

	public String getHitAssignments() {
		return hitAssignments;
	}

	public void setHitAssignments(String hitAssignments) {
		this.hitAssignments = hitAssignments;
	}

	public String getHitLifetimeHours() {
		return hitLifetimeHours;
	}

	public void setHitLifetimeHours(String hitLifetimeHours) {
		this.hitLifetimeHours = hitLifetimeHours;
	}

	public String getExperimentpath() {
		return experimentpath;
	}

	public void setExperimentpath(String experimentpath) {
		this.experimentpath = experimentpath;
	}

}
