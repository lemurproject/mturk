package org.lemurproject.mturkcwdeep;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class DataProperties {

	@Value("${hit.csv}")
	private String hitCsv;

	@Value("${hit.map}")
	private String hitMap;

	@Value("${query.name.map}")
	private String queryNameMap;

	@Value("${seen.file}")
	private String seenFile;

	@Value("${state.file}")
	private String stateFile;

	@Value("${mturk.endpoint}")
	private String endpoint;

	@Value("${question.filename}")
	private String questionFilename;

	@Value("${qualification.type}")
	private String qualificationType;

	@Value("${hit.lifetime}")
	private String hitLifetime;

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

	public String getHitLifetime() {
		return hitLifetime;
	}

	public void setHitLifetime(String hitLifetime) {
		this.hitLifetime = hitLifetime;
	}

	public String getHitCsv() {
		return hitCsv;
	}

	public void setHitCsv(String hitCsv) {
		this.hitCsv = hitCsv;
	}

	public String getHitMap() {
		return hitMap;
	}

	public void setHitMap(String hitMap) {
		this.hitMap = hitMap;
	}

	public String getStateFile() {
		return stateFile;
	}

	public void setStateFile(String stateFile) {
		this.stateFile = stateFile;
	}

	public String getSeenFile() {
		return seenFile;
	}

	public void setSeenFile(String seenFile) {
		this.seenFile = seenFile;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getQueryNameMap() {
		return queryNameMap;
	}

	public void setQueryNameMap(String queryNameMap) {
		this.queryNameMap = queryNameMap;
	}

}
