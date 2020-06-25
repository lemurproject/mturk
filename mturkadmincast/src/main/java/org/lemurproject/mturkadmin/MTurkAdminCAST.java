package org.lemurproject.mturkadmin;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MTurkAdminCAST implements CommandLineRunner {

	@Autowired
	private CreateMTurkHits mturk;

	@Autowired
	private GetSubmittedHITs getSubmittedHITs;

	@Autowired
	private GetUnsubmittedHITs getUnsubmittedHITs;

	@Autowired
	private ApproveHITs approveHITs;

	@Autowired
	private DeleteHITs deleteHITs;

	@Autowired
	private ListHITs listHITs;

	@Autowired
	private MTurkProperties properties;

	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(MTurkAdminCAST.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		if (properties.getFunction().equalsIgnoreCase("create")) {
			mturk.createHits();
		} else if (properties.getFunction().equalsIgnoreCase("get")) {
			getSubmittedHITs.getSubmittedAssignments();
		} else if (properties.getFunction().equalsIgnoreCase("approve")) {
			approveHITs.approveAssignments();
		} else if (properties.getFunction().equalsIgnoreCase("delete")) {
			deleteHITs.deleteAssignments();
		} else if (properties.getFunction().equalsIgnoreCase("list")) {
			listHITs.listHITs();
		} else if (properties.getFunction().equalsIgnoreCase("unsubmitted")) {
			getUnsubmittedHITs.getUnsubmittedAssignments();
		}
	}

}
