package org.lemurproject.mturkadmin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
/*
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
import com.amazonaws.services.mturk.model.Assignment;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITRequest;
import com.amazonaws.services.mturk.model.ListAssignmentsForHITResult;

/* 
 * Before connecting to MTurk, set up your AWS account and IAM settings as described here:
 * https://blog.mturk.com/how-to-use-iam-to-control-api-access-to-your-mturk-account-76fe2c2e66e2
 * 
 * Configure your AWS credentials as described here:
 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
 *
 */
@Component
public class GetSubmittedHITs {

	@Autowired
	private MTurkClientHelper clientHelper;

	@Autowired
	private MTurkFilenameHelper filenameHelper;

	public void getSubmittedAssignments(MTurkProperties properties, String status)
			throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		AmazonMTurk client = clientHelper.getClient(properties.getEnvironment());

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		QueryResponseObject testObject = new QueryResponseObject();
		String fullFilename = filenameHelper.getFullJudgedDocFilename();
		Writer hitDataWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullFilename), "UTF8"));
		hitDataWriter.write(testObject.getCsvHeaders());

		String shortFilename = filenameHelper.getShortJudgedDocFilename();
		Writer shortDataWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(shortFilename), "UTF8"));
		shortDataWriter.write(testObject.getCsvHeadersShort());

		List<QueryResponseObject> responses = new ArrayList<QueryResponseObject>();
		Scanner scanner = new Scanner(new File(filenameHelper.getHitFilename()));
		while (scanner.hasNext()) {
			String hitLine = scanner.next();
			String hitId = hitLine.split(",")[0].trim();
			System.out.println(hitId);

			ListAssignmentsForHITRequest listHITRequest = new ListAssignmentsForHITRequest();
			listHITRequest.setHITId(hitId);

			// Get a maximum of 10 completed assignments for this HIT
			listHITRequest.setMaxResults(100);
			List<Assignment> assignmentList = new ArrayList<Assignment>();
			try {
				ListAssignmentsForHITResult listHITResult = client.listAssignmentsForHIT(listHITRequest);
				assignmentList = listHITResult.getAssignments();
			} catch (Exception e) {
				System.out.println("No HIT: " + hitId);
			}

			// Iterate through all the assignments received
			String assignmentStatus = "";
			for (Assignment asn : assignmentList) {

				QueryResponseObject response = new QueryResponseObject();
				assignmentStatus = asn.getAssignmentStatus();
				if (assignmentStatus.equalsIgnoreCase(status)) {
					long startTime = asn.getAcceptTime().getTime();
					LocalDateTime date = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault())
							.toLocalDateTime();
					DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
					response.setExperimentTimeOfDay(formatter.format(date));
					response.setExperimentDayOfWeek(date.getDayOfWeek().toString());
					response.setStartTime(asn.getAcceptTime().getTime());
					response.setEndTime(asn.getSubmitTime().getTime());
					double time = (asn.getSubmitTime().getTime() - asn.getAcceptTime().getTime()) / 1000d;
					response.setTime(time);
					double minutes = time / 60d;
					response.setMinutes(minutes);
					response.setAssignmentId(asn.getAssignmentId());
					response.setHITid(hitId);
					response.setMturkAssessorId(asn.getWorkerId());

					int numNRSelected = 0;
					int numRelevantSelected = 0;

					if (asn.getAssignmentId().equals("3GA6AFUKOT4QIC83M8MSNQHOY8GH3X")) {
						System.out.println("Problem Assignment");
					}
					if (asn.getAnswer() == null) {
						System.out.println("  assignment: " + asn.getAssignmentId() + " in HIT " + hitId + " is null");
					} else {
						String answerString = asn.getAnswer();
						// if (answerString.contains("<QuestionIdentifier>query</QuestionIdentifier>"))
						// {
						Document doc = dBuilder.parse(new InputSource(new StringReader(asn.getAnswer())));
						NodeList nList = doc.getElementsByTagName("Answer");
						boolean NRdoc = false;
						for (int i = 0; i < nList.getLength(); i++) {
							Node nNode = nList.item(i);
							if (nNode.getNodeType() == Node.ELEMENT_NODE) {
								Element eElement = (Element) nNode;
								String questionIdentifier = eElement.getElementsByTagName("QuestionIdentifier").item(0)
										.getTextContent();
								String freeTextInput = eElement.getElementsByTagName("FreeText").item(0)
										.getTextContent().trim();
								if (freeTextInput.startsWith("nr-") || freeTextInput.startsWith("random-")) {
									NRdoc = true;
								} else if (freeTextInput.startsWith("clueweb22-")) {
									NRdoc = false;
								}
								String freeText = String.join("", "\" ", freeTextInput, "\"");
								// System.out.println(questionIdentifier + ": " + freeText);

								if (questionIdentifier.equalsIgnoreCase("query")) {
									response.setQuery(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("description")) {
									response.setDescription(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("category")) {
									response.setCategory(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("filteredDocs")) {
									response.setFilteredDocs(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("inputTime")) {
									double inputTime = Double.valueOf(freeTextInput.trim()).doubleValue() / 1000d;
									response.setInputTime(inputTime);
								} else if (questionIdentifier.equalsIgnoreCase("queryTime")) {
									double queryTime = Double.valueOf(freeTextInput.trim()).doubleValue() / 1000d;
									response.setInputTime(queryTime);
								} else if (questionIdentifier.equalsIgnoreCase("documents[0].score")) {
									response.setDoc1score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[0].docId")) {
									response.setDoc1id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[0].selected")) {
									response.setDoc1selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[1].score")) {
									response.setDoc2score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[1].docId")) {
									response.setDoc2id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[1].selected")) {
									response.setDoc2selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[2].score")) {
									response.setDoc3score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[2].docId")) {
									response.setDoc3id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[2].selected")) {
									response.setDoc3selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[3].score")) {
									response.setDoc4score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[3].docId")) {
									response.setDoc4id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[3].selected")) {
									response.setDoc4selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[4].score")) {
									response.setDoc5score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[4].docId")) {
									response.setDoc5id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[4].selected")) {
									response.setDoc5selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[5].score")) {
									response.setDoc6score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[5].docId")) {
									response.setDoc6id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[5].selected")) {
									response.setDoc6selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[6].score")) {
									response.setDoc7score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[6].docId")) {
									response.setDoc7id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[6].selected")) {
									response.setDoc7selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[7].score")) {
									response.setDoc8score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[7].docId")) {
									response.setDoc8id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[7].selected")) {
									response.setDoc8selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[8].score")) {
									response.setDoc9score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[8].docId")) {
									response.setDoc9id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[8].selected")) {
									response.setDoc9selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[9].score")) {
									response.setDoc10score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[9].docId")) {
									response.setDoc10id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[9].selected")) {
									response.setDoc10selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[10].score")) {
									response.setDoc11score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[10].docId")) {
									response.setDoc11id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[10].selected")) {
									response.setDoc11selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[11].score")) {
									response.setDoc12score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[11].docId")) {
									response.setDoc12id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[11].selected")) {
									response.setDoc12selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								} else if (questionIdentifier.equalsIgnoreCase("documents[12].score")) {
									response.setDoc13score(freeTextInput.trim());
								} else if (questionIdentifier.equalsIgnoreCase("documents[12].docId")) {
									response.setDoc13id(freeText);
								} else if (questionIdentifier.equalsIgnoreCase("documents[12].selected")) {
									response.setDoc13selected(true);
									if (NRdoc)
										numNRSelected = numNRSelected + 1;
									else
										numRelevantSelected += 1;
									NRdoc = false;
								}
							}
						}
						response.setNumRelevantSelected(String.valueOf(numRelevantSelected));
						response.setNumNRselected(String.valueOf(numNRSelected));
						responses.add(response);
						// System.out.println("HIT id: " + hitId + " submitted with assignment Id: " +
						// asn.getAssignmentId());
						if (status.equalsIgnoreCase("Submitted")) {
							shortDataWriter.write(response.getCsvValuesShort());
						}
						hitDataWriter.write(response.getCsvValues());
						// Thread.sleep(1000);
					}
					// }
				}
			}
		}
		hitDataWriter.close();
		shortDataWriter.close();
	}

}