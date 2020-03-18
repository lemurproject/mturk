package com.example.springboot;

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
	
	@Value("${documents.path}")
	private String documentsPath;

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

	public String getDocumentsPath() {
		return documentsPath;
	}

	public void setDocumentsPath(String documentsPath) {
		this.documentsPath = documentsPath;
	}

}
