package org.lemurproject.mturkcwdeep;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties(DataProperties.class)
public class MTurkCAST extends SpringBootServletInitializer {

	@Autowired
	private ClueWebDeepService castService;

	public static void main(String[] args) {
		SpringApplication.run(MTurkCAST.class, args);
	}

	@PostConstruct
	public void init() throws Exception {
		castService.loadData();
	}

}
