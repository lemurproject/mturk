package org.lemurproject.mturkcast;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

@Controller
public class CastController {

	@Autowired
	private CastService2 castService;

	@GetMapping({ "/cast", "/cast?" })
	public String document(@RequestParam(name = "hitId") String hitId, @RequestParam(required = false) String workerId,
			HttpServletRequest request, Model model)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		HITObject hit = null;
		if (workerId == null) {
			hit = castService.getSampleData();
			model.addAttribute("sample", "-1");
			model.addAttribute("hit", hit);
			model.addAttribute("newQuery", false);
			model.addAttribute("workerId", "sample");
		} else {
			hit = castService.getNextHIT(hitId, workerId);
			// boolean newQuery = castService.isNewQuery(workerId, hit);
			model.addAttribute("sample", 1);
			model.addAttribute("hit", hit);
			model.addAttribute("workerId", workerId);
			// model.addAttribute("newQuery", newQuery);
		}
		if (hit != null) {
			model.addAttribute("hitId", hitId);
			return "cast";
		} else {
			model.addAttribute("message", "No data for hit: " + hitId
					+ "<br/>Please contact Cameron Vandenberg at cmw2@andrew.cmu.edu with this hitId");
			return "error";
		}
	}

//	@GetMapping({ "/submit", "/submit?" })
//	public void submit(@RequestParam String hitId, @RequestParam String workerId)
//			throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException {
//		System.out.println("Submitted to Controller with workerId: " + workerId + " and hitId: " + hitId);
//		castService.submitHIT(hitId, workerId);
//	}

	@GetMapping({ "/viewUserQueues", "/viewUserQueues?" })
	public String viewUserQueues(HttpServletRequest request, Model model) {
		List<String> userQueueList = castService.listUserQueues();
		model.addAttribute("title", "Current User Queues");
		model.addAttribute("list", userQueueList);
		return "list";
	}

	@GetMapping({ "/viewHits4Topic", "/viewHits4Topic?" })
	public String viewHits4Topic(HttpServletRequest request, Model model) {
		List<String> topicHits = castService.listTopicHits();
		model.addAttribute("title", "Current User Queues");
		model.addAttribute("list", topicHits);
		return "list";
	}

	@GetMapping({ "/viewUserHITs", "/viewUserHITs?" })
	public String viewUserHITs(@RequestParam(name = "workerId") String workerId, HttpServletRequest request,
			Model model) {
		List<String> userHITs = castService.listUserHits(workerId);
		model.addAttribute("title", "Current User HITs");
		model.addAttribute("list", userHITs);
		return "list";
	}

	@GetMapping({ "/deleteTopic", "/deleteTopic?" })
	public String deleteTopic(@RequestParam(name = "topic") String topic) {
		castService.deleteTopic(topic);
		return "success";
	}

	@GetMapping({ "/addtopics" })
	public String addtopics(@RequestParam(name = "hitMapFile") String hitMapFile,
			@RequestParam(name = "hitCsvFile") String hitCsvFile, HttpServletRequest request, Model model)
			throws IOException {
		String newTopicsString = castService.addTopics(hitMapFile, hitCsvFile);
		model.addAttribute("newTopics", newTopicsString);
		return "newtopics";
	}

	@GetMapping({ "/addhit" })
	public String addhit(@RequestParam(name = "topic") String topic, @RequestParam(name = "hitId") String hitId,
			@RequestParam(name = "hitMapFile") String hitMapFile, HttpServletRequest request, Model model)
			throws IOException {
		castService.addHIT(topic, hitId, hitMapFile);
		return "success";
	}

	@GetMapping({ "/addqualification" })
	public String addqualification(@RequestParam(name = "topicId") String topicId,
			@RequestParam(name = "qualification") String qualification, HttpServletRequest request, Model model)
			throws IOException {
		String newQualificationString = castService.addQualification(topicId, qualification);
		model.addAttribute("newTopics", newQualificationString);
		return "newtopics";
	}

	@GetMapping({ "/addtopicname" })
	public String addtopicname(@RequestParam(name = "topic") String topic,
			@RequestParam(name = "topicName") String topicName, HttpServletRequest request, Model model)
			throws IOException {
		castService.addTopicName(topic, topicName);
		return "success";
	}

	@GetMapping({ "/transfertopic" })
	public String transferTopic(@RequestParam(name = "oldWorkerId") String oldWorkerId,
			@RequestParam(name = "newWorkerId") String newWorkerId, @RequestParam(required = false) String topic,
			HttpServletRequest request, Model model)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		String transferResponse = "";
		if (topic == null) {
			transferResponse = castService.transferTopicToNewUser(oldWorkerId, newWorkerId);
		} else {
			transferResponse = castService.transferTopicToNewUser(oldWorkerId, newWorkerId, topic);
		}
		model.addAttribute("transferResponse", transferResponse);
		return "transfertopic";
	}

	@GetMapping({ "/simpletest" })
	public String simpletest(HttpServletRequest request, Model model)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		HITObject hit = castService.getSampleData();
		model.addAttribute("workerId", "-1");
		model.addAttribute("hit", hit);
		model.addAttribute("newQuery", true);
		return "simpletest";
	}

}
