package org.lemurproject.cw;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class GlobalProperties {

	@Value("${hostName}")
	private String hostName;
	
	@Value("${hostPort}")
	private String hostPort;
	
	@Value("${collectionName}")
	private String collectionName;
	
	@Value("${ltrModel}")
	private String ltrModel;
	
	@Value("${featureStore}")
	private String featureStore;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostPort() {
		return hostPort;
	}

	public void setHostPort(String hostPort) {
		this.hostPort = hostPort;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getLtrModel() {
		return ltrModel;
	}

	public void setLtrModel(String ltrModel) {
		this.ltrModel = ltrModel;
	}

	public String getFeatureStore() {
		return featureStore;
	}

	public void setFeatureStore(String featureStore) {
		this.featureStore = featureStore;
	}
	


}
