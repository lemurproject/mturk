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
	private ApproveAndRejectHITs approveAndRejectHITs;

	@Autowired
	private DeleteHITsByQualification deleteHITsByQualification;

	@Autowired
	private DeleteHITsById deleteHITsById;

	@Autowired
	private CreateMTurkQualificationType createQualType;

	@Autowired
	private GetMTurkQualifications getQualResults;

	@Autowired
	private CreateMTurkQualAndHits createQualAndHits;

	@Autowired
	private GetMTurkOpenHITs getOpenHits;

	@Autowired
	private GetMTurkApprovedHITs getApprovedHits;

	@Autowired
	private MTurkProperties properties;

	public static void main(String[] args) throws IOException, ParseException {
		SpringApplication.run(MTurkAdminCW.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		if (properties.getFunction().equalsIgnoreCase("createhits")) {
			mturk.createHits(properties);
		} else if (properties.getFunction().equalsIgnoreCase("get")) {
			getSubmittedHITs.getSubmittedAssignments(properties, "Submitted");
		} else if (properties.getFunction().equalsIgnoreCase("getapproved")) {
			getSubmittedHITs.getSubmittedAssignments(properties, "Approved");
		} else if (properties.getFunction().equalsIgnoreCase("getrejected")) {
			getSubmittedHITs.getSubmittedAssignments(properties, "Rejected");
		} else if (properties.getFunction().equalsIgnoreCase("approveallhits")) {
			approveHITs.approveAssignments(properties);
		} else if (properties.getFunction().equalsIgnoreCase("approve")) {
			approveAndRejectHITs.approveAssignments(properties);
		} else if (properties.getFunction().equalsIgnoreCase("deletebyqual")) {
			deleteHITsByQualification.deleteAssignments(properties);
		} else if (properties.getFunction().equalsIgnoreCase("deletebyid")) {
			deleteHITsById.deleteHITsById(properties);
		} else if (properties.getFunction().equalsIgnoreCase("createqual")) {
			createQualType.createQualification(properties);
		} else if (properties.getFunction().equalsIgnoreCase("getqual")) {
			getQualResults.getQualifications(properties);
		} else if (properties.getFunction().equalsIgnoreCase("create")) {
			createQualAndHits.createHits(properties);
		} else if (properties.getFunction().equalsIgnoreCase("list")) {
			getOpenHits.getQualifications(properties);
		} else if (properties.getFunction().equalsIgnoreCase("listapproved")) {
			getApprovedHits.getAllApprovedHITs(properties);
		}
	}

}
