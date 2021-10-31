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

	@Value("${hit.filename}")
	private String hitFilename;

	@Value("${hit.price}")
	private String hitPrice;

	@Value("${judgeddocuments.csv}")
	private String judgedDocumentsCsvName;

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

	public String getHitFilename() {
		return hitFilename;
	}

	public void setHitFilename(String hitFilename) {
		this.hitFilename = hitFilename;
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

}
