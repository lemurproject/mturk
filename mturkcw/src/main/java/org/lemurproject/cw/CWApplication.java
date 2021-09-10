package org.lemurproject.cw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties(CWProperties.class)
public class CWApplication extends SpringBootServletInitializer {

	@Autowired
	private SearchService searchService;

	public static void main(String[] args) {
		SpringApplication.run(CWApplication.class, args);
	}

}
