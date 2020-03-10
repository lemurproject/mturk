package org.lemurproject.cw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;

	@GetMapping({"/search"})
	public String search(HttpServletRequest request, Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		model.addAttribute("searchObject", searchObject);

		return "search";
	}
	
	@PostMapping({"/searchResults"})
	public String getSearchResults(HttpServletRequest request, @ModelAttribute SearchObject searchObject, BindingResult bindingResult, Model model) throws SolrServerException, IOException {
		List<SearchResult> documentList = searchService.search(searchObject.getQueryString());
		List<String> docNames = new ArrayList<String>();
		for (SearchResult result : documentList) {
			docNames.add(result.getDocId());
		}
		model.addAttribute("documentList", docNames);
		model.addAttribute("fulldocs", documentList);
		return "results";
	}
	
	@GetMapping({"/document"})
	public String document(@RequestParam String docid, HttpServletRequest request, Model model) throws SolrServerException, IOException {
		String docText = searchService.getDocumentText(docid);
		model.addAttribute("docText", docText);

		return "document";
	}

}
