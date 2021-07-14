package org.lemurproject.mturkadmin;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MTurkAdminCW implements CommandLineRunner {

	@Autowired
	private CreateMTurkHits mturk;

	@Autowired
	private GetSubmittedHITs getSubmittedHITs;

	@Autowired
	private ApproveHITs approveHITs;

	@Autowired
	private DeleteHITsByQualification deleteHITsByQualification;

	@Autowired
	private CreateMTurkQualificationType createQualType;

	@Autowired
	private GetMTurkQualifications getQualResults;

	@Autowired
	private AcceptMTurkQualificationResults acceptQualResults;

	@Autowired
	private MTurkProperties properties;

	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(MTurkAdminCW.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		if (properties.getFunction().equalsIgnoreCase("create")) {
			mturk.createHits();
		} else if (properties.getFunction().equalsIgnoreCase("get")) {
			getSubmittedHITs.getSubmittedAssignments();
		} else if (properties.getFunction().equalsIgnoreCase("approve")) {
			approveHITs.approveAssignments();
		} else if (properties.getFunction().equalsIgnoreCase("deletebyqual")) {
			deleteHITsByQualification.deleteAssignments();
		} else if (properties.getFunction().equalsIgnoreCase("createqual")) {
			createQualType.createQualification();
		} else if (properties.getFunction().equalsIgnoreCase("getqual")) {
			getQualResults.getQualifications();
		} else if (properties.getFunction().equalsIgnoreCase("acceptqual")) {
			acceptQualResults.acceptQualificationRequests();
		}
	}

}
