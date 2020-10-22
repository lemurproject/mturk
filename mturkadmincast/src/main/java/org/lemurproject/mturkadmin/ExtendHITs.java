package org.lemurproject.mturkadmin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.UpdateExpirationForHITRequest;

@Component
public class ExtendHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void extendAssignments()
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

//		ListHITsRequest listHITsRequest = new ListHITsRequest();
//		listHITsRequest.setMaxResults(100);
//		ListHITsResult listHitResult = client.listHITs(listHITsRequest);
//		List<HIT> hits = listHitResult.getHITs();

		Scanner scanner = new Scanner(new File(properties.getHit2topicFilename()));
		if (scanner.hasNext()) {
			scanner.next();
		}

		// for (HIT hit : listHitResult.getHITs()) {
		while (scanner.hasNext()) {
			// String hitId = hit.getHITId();
			String nextLine = scanner.next();
			String hitId = nextLine.substring(0, nextLine.indexOf(","));

//		// int index = 1;
//		for (HIT hit : hits) {
//			String description = hit.getDescription();
//			if (description.contains("35")) {
			// System.out.println(index + ": " + hit.getDescription());
			// index++;
//			String hitId = hit.getHITId();
			try {
				UpdateExpirationForHITRequest updateHITRequest = new UpdateExpirationForHITRequest();
				updateHITRequest.setHITId(hitId);
				Date expirationTime = new GregorianCalendar(2020, 8, 17, 12, 0).getTime();
				updateHITRequest.setExpireAt(expirationTime);
				client.updateExpirationForHIT(updateHITRequest);
				System.out.println("Extended HIT: " + hitId);

			} catch (Exception e) {
				System.out.println("Could not delte HIT: " + hitId);
			}
//			}
		}
		scanner.close();
	}

}
