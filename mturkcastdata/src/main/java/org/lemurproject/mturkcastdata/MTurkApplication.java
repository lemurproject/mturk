package org.lemurproject.mturkcastdata;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.lemurproject.mturkcastdata")
public class MTurkApplication implements CommandLineRunner {
	
	@Autowired
	private CreateMTurkCastData mturk;
	
	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(MTurkApplication.class, args);
		
	}

	@Override
	public void run(String... args) throws Exception {
		mturk.createData();
	}

}
