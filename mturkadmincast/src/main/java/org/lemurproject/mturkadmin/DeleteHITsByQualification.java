package org.lemurproject.mturkadmin;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amazonaws.services.mturk.AmazonMTurk;
import com.amazonaws.services.mturk.model.ApproveAssignmentRequest;
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.HIT;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;
import com.amazonaws.services.mturk.model.ListHITsForQualificationTypeRequest;
import com.amazonaws.services.mturk.model.ListHITsForQualificationTypeResult;
import com.amazonaws.services.mturk.model.UpdateExpirationForHITRequest;

@Component
public class DeleteHITsByQualification {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkProperties properties;

	public void deleteAssignments()
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

//		List<HIT> hitResults = new ArrayList<HIT>();
//		boolean moreResults = true;
//		while (moreResults == true) {
		ListHITsForQualificationTypeRequest listHITsRequest = new ListHITsForQualificationTypeRequest();
		listHITsRequest.setQualificationTypeId(properties.getQualificationType());
		listHITsRequest.setMaxResults(100);
		ListHITsForQualificationTypeResult listHitResult = client.listHITsForQualificationType(listHITsRequest);
//			if (listHitResult.getHITs().size() > 0) {
//				hitResults.addAll(listHitResult.getHITs());
//				System.out.println("adding HIT results");
//			} else {
//				moreResults = false;
//				System.out.println("No more Results");
//			}
//		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		for (HIT hit : listHitResult.getHITs()) {
			String hitId = hit.getHITId();
			System.out.println(hitId + " - " + hit.getHITStatus());

			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(100);
			ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
			List<Assignment> assignmentList = listHITResult.getAssignments();

			// Iterate through all the assignments received
			String assignmentStatus = "";
			for (Assignment asn : assignmentList) {
				System.out.println("  assignment: " + asn.getAssignmentId() + " - " + asn.getAssignmentStatus());
				assignmentStatus = asn.getAssignmentStatus();

				if (assignmentStatus.equalsIgnoreCase("Submitted")) {
					Document doc = dBuilder.parse(new InputSource(new StringReader(asn.getAnswer())));
					NodeList nList = doc.getElementsByTagName("Answer");
					Node nNode = nList.item(0);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						String jsonAnswer = eElement.getElementsByTagName("FreeText").item(0).getTextContent();
						System.out.println(jsonAnswer);
					}
					ApproveAssignmentRequest approveRequest = new ApproveAssignmentRequest();
					approveRequest.setAssignmentId(asn.getAssignmentId());
					approveRequest.setRequesterFeedback("Good work, thank you!");
					approveRequest.setOverrideRejection(false);
					client.approveAssignment(approveRequest);
					System.out.println("Assignment has been approved: " + asn.getAssignmentId());

				}
			}
			if (!assignmentStatus.equalsIgnoreCase("Submitted")) {
				try {
					UpdateExpirationForHITRequest updateHITRequest = new UpdateExpirationForHITRequest();
					updateHITRequest.setHITId(hitId);
					// updateHITRequest.setExpireAt(new Date(0l));
					Date date = new GregorianCalendar(2020, 11, 30).getTime();
					updateHITRequest.setExpireAt(date);
					client.updateExpirationForHIT(updateHITRequest);
					System.out.println("Expired HIT: " + hitId);

//					DeleteHITRequest deleteHITRequest = new DeleteHITRequest();
//					deleteHITRequest.setHITId(hitId);
//					client.deleteHIT(deleteHITRequest);
//					System.out.println("Deleted: " + hitId);
				} catch (Exception e) {
					System.out.println("Could not delte HIT: " + hitId);
				}
			}
		}
	}
}