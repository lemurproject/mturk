package org.lemurproject.mturkadmin;

import org.springframework.stereotype.Component;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.AmazonMTurkClientBuilder;

@Component
public class MTurkClientHelper {

	private static final String SANDBOX_ENDPOINT = "mturk-requester-sandbox.us-east-1.amazonaws.com";
	private static final String PROD_ENDPOINT = "https://mturk-requester.us-east-1.amazonaws.com";
	private static final String SIGNING_REGION = "us-east-1";

	public AmazonMTurk getClient(String environment) {
		if (environment.equalsIgnoreCase("prod") || environment.equalsIgnoreCase("production")) {
			return getProdClient();
		} else {
			return getSandboxClient();
		}
	}

	private AmazonMTurk getSandboxClient() {
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(SANDBOX_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

	private AmazonMTurk getProdClient() {
		AmazonMTurkClientBuilder builder = AmazonMTurkClientBuilder.standard();
		builder.setEndpointConfiguration(new EndpointConfiguration(PROD_ENDPOINT, SIGNING_REGION));
		return builder.build();
	}

}
