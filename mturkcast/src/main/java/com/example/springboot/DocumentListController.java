package com.example.springboot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DocumentListController {

	@Autowired
	private HelloService helloService;

	@GetMapping({"/", "/welcome"})
	public String welcome(HttpServletRequest request, Model model) throws FileNotFoundException {
		HttpSession session = request.getSession();
		session.setAttribute("docValues", new ArrayList<DocumentRelevanceObject>());
		
		Map<Integer, Map<Integer, String>> queries = helloService.getQueries();
		TaskInformation taskInfo = helloService.getTaskInformation();
		session.setAttribute("taskInfo", taskInfo);
		
		Map<Integer, String> queryMap = queries.get(Integer.valueOf(taskInfo.getQueryNum()));
		String subQuery = queryMap.get(Integer.valueOf(taskInfo.getSubQueryNum()));
		//Collection<String> queryList = queryMap.values();
		List<String> subQueryList = new ArrayList<String>();
		queryMap.forEach((subQueryNum, query) -> {
			if (subQueryNum.intValue() <= Integer.valueOf(taskInfo.getSubQueryNum()).intValue() ) {
				subQueryList.add(query);
			}
		});
		
//		Map<Integer, Map<Integer, List<Qrel>>> qrels = helloService.getQrels();
//		List<Qrel> documents = qrels.get(Integer.valueOf(queryNum)).get(Integer.valueOf(subQueryNum));
		
		String name = helloService.getName();
		model.addAttribute("message", name);
		model.addAttribute("queryList", subQueryList);
		model.addAttribute("subQuery", subQuery);
		model.addAttribute("documents", taskInfo.getDocIds());
		model.addAttribute("queryNum", taskInfo.getQueryNum());
		model.addAttribute("subQueryNum", taskInfo.getSubQueryNum());
		model.addAttribute("docRelevance", new DocumentRelevanceObject());

		return "welcome";
	}
	
//	@PostMapping({"/welcome"})
//	public String judgeDocuments(HttpSession session, Model model) throws FileNotFoundException {
//		TaskInformation taskInfo = (TaskInformation)session.getAttribute("taskInfo");
//		List<DocumentRelevanceObject> docValues = (List<DocumentRelevanceObject>)session.getAttribute("docValues");
//		String docId = taskInfo.getDocIds().get(docValues.size());
//		DocumentRelevanceObject docRelevance = new DocumentRelevanceObject();
//		docRelevance.setDocId(docId);
//		
//		String document = helloService.getDocument(docId);
//		model.addAttribute("document", document);
//		
//		Map<Integer, Map<Integer, String>> queries = helloService.getQueries();
//		
//		Map<Integer, String> queryMap = queries.get(Integer.valueOf(taskInfo.getQueryNum()));
//		String subQuery = queryMap.get(Integer.valueOf(taskInfo.getSubQueryNum()));
//		//Collection<String> queryList = queryMap.values();
//		List<String> subQueryList = new ArrayList<String>();
//		queryMap.forEach((subQueryNum, query) -> {
//			if (subQueryNum.intValue() <= Integer.valueOf(taskInfo.getSubQueryNum()).intValue() ) {
//				subQueryList.add(query);
//			}
//		});
//		
//		model.addAttribute("queryList", subQueryList);
//		model.addAttribute("subQuery", subQuery);
//		model.addAttribute("docRelevance", docRelevance);
//		model.addAttribute("doc5Id", docId);
//		
//		return "document";
//	}

}
