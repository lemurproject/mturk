package com.example.springboot;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DocumentController {

	@Autowired
	private HelloService helloService;
	
	private Model setModelInfo(HttpSession session, Model model) throws FileNotFoundException {
		
		TaskInformation taskInfo = (TaskInformation)session.getAttribute("taskInfo");
		List<DocumentRelevanceObject> docValues = (List<DocumentRelevanceObject>)session.getAttribute("docValues");
		String docId = taskInfo.getDocIds().get(docValues.size());
		DocumentRelevanceObject docRelevance = new DocumentRelevanceObject();
		docRelevance.setDocId(docId);
		
		String document = helloService.getDocument(docId);
		model.addAttribute("document", document);
		
		Map<Integer, Map<Integer, String>> queries = helloService.getQueries();
		
		Map<Integer, String> queryMap = queries.get(Integer.valueOf(taskInfo.getQueryNum()));
		String subQuery = queryMap.get(Integer.valueOf(taskInfo.getSubQueryNum()));
		//Collection<String> queryList = queryMap.values();
		List<String> subQueryList = new ArrayList<String>();
		queryMap.forEach((subQueryNum, query) -> {
			if (subQueryNum.intValue() <= Integer.valueOf(taskInfo.getSubQueryNum()).intValue() ) {
				subQueryList.add(query);
			}
		});
		
		model.addAttribute("queryList", subQueryList);
		model.addAttribute("subQuery", subQuery);
		model.addAttribute("docRelevance", docRelevance);
		model.addAttribute("doc5Id", docId);
		
		return model;
	}

	@GetMapping({"/document", "/document?"})
	public String document(HttpServletRequest request, Model model) throws FileNotFoundException {
		HttpSession session = request.getSession();
		setModelInfo(session, model);

		return "document";
	}
	
	@PostMapping({"/document"})
	public String documentSubmit(HttpServletRequest request, @ModelAttribute DocumentRelevanceObject docRelevance, Model model) throws FileNotFoundException {
		HttpSession session = request.getSession();
		List<DocumentRelevanceObject> docValues = (List<DocumentRelevanceObject>)session.getAttribute("docValues");
		
		docValues.add(docRelevance);
		session.setAttribute("docValues", docValues);
		
		setModelInfo(session, model);
		
		if (docValues.size() == 4) {
			model.addAttribute("doc1Id", docValues.get(0).getDocId());
			model.addAttribute("doc1Score", docValues.get(0).getRelevance());
			model.addAttribute("doc2Id", docValues.get(1).getDocId());
			model.addAttribute("doc2Score", docValues.get(1).getRelevance());
			model.addAttribute("doc3Id", docValues.get(2).getDocId());
			model.addAttribute("doc3Score", docValues.get(2).getRelevance());
			model.addAttribute("doc4Id", docValues.get(3).getDocId());
			model.addAttribute("doc4Score", docValues.get(3).getRelevance());
			
			session.setAttribute("docValues", new ArrayList<DocumentRelevanceObject>());
			
			return "finaldocument";
		} else {
			return "document";
		}
		
	}
	
	

}
