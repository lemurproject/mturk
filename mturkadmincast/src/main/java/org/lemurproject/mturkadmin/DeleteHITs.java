package org.lemurproject.mturkadmin;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.DeleteHITRequest;
import com.amazonaws.services.mturk.model.UpdateExpirationForHITRequest;

@Component
public class DeleteHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void deleteAssignments()
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
				updateHITRequest.setExpireAt(new Date(0l));
				client.updateExpirationForHIT(updateHITRequest);
				System.out.println("Expired HIT: " + hitId);

				DeleteHITRequest deleteHITRequest = new DeleteHITRequest();
				deleteHITRequest.setHITId(hitId);
				client.deleteHIT(deleteHITRequest);
				System.out.println("Deleted: " + hitId);
			} catch (Exception e) {
				System.out.println("Could not delte HIT: " + hitId);
			}
//			}
		}
		scanner.close();
	}

}
