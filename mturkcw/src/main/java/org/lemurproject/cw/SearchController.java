package org.lemurproject.cw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;

	@GetMapping({ "/sampleSearch" })
	public String sampleSearch(@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request,
			Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		searchObject.setAssignmentId(assignmentId);
		model.addAttribute("searchObject", searchObject);
		// model.addAttribute("assignmentId", assignmentId);

		return "sampleSearch";
	}

	@PostMapping({ "/sampleResults" })
	public String getSampleResults(@RequestParam(name = "queryString") String query,
			@RequestParam(name = "queryDescription") String description,
			@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request, Model model)
			throws SolrServerException, IOException {
		SearchResult searchResult = searchService.getSampleResults(query);
		searchResult.setAssignmentId(assignmentId);
		searchResult.setDescription(description);
		model.addAttribute("searchResult", searchResult);
		return "results";
	}

	@PostMapping({ "/sampleSubmit" })
	public String getSampleSubmit(@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request,
			Model model, @ModelAttribute SearchResult searchResult) throws SolrServerException, IOException {
		model.addAttribute("assignmentId", assignmentId);
		return "sampleSubmit";
	}

	@GetMapping({ "/categories" })
	public String categories(@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request,
			Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		return "categories";
	}

	@GetMapping({ "/search" })
	public String search(@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request,
			Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		return "search";
	}

	@GetMapping({ "/searchcategories" })
	public String searchcategories(@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request,
			Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		List<String> categories = searchService.getSearchCategories();
		model.addAttribute("categories", categories);
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		return "searchcategories";
	}

	@PostMapping({ "/searchResults" })
	public String getSearchResults(@RequestParam(name = "queryString") String query,
			@RequestParam(name = "queryDescription") String description,
			@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request, Model model)
			throws SolrServerException, IOException {
		SearchResult searchResult = searchService.search(query);
		searchResult.setDescription(description);
		searchResult.setAssignmentId(assignmentId);
		model.addAttribute("searchResult", searchResult);
		return "results";
	}

	@PostMapping({ "/searchResultsBERT" })
	public String getSearchResultsBERT(@RequestParam(name = "queryString") String query,
			@RequestParam(name = "queryDescription") String description,
			@RequestParam(name = "category") String category, @RequestParam(name = "assignmentId") String assignmentId,
			HttpServletRequest request, Model model) throws SolrServerException, IOException {
		SearchResult searchResult = searchService.bertSearch(query);
		if (searchResult.getDocuments() != null && searchResult.getDocuments().size() > 0) {
			searchResult.setDescription(description);
			searchResult.setCategory(category);
			searchResult.setAssignmentId(assignmentId);
			model.addAttribute("searchResult", searchResult);
			return "results";
		} else {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage", "There were no results for your query.  Please try another search");
			return "searchcategories";
		}
	}

	@GetMapping({ "/document" })
	public String document(@RequestParam String docid, HttpServletRequest request, Model model)
			throws SolrServerException, IOException {
		String docText = searchService.getDocumentText(docid);
		model.addAttribute("docText", docText);

		return "document";
	}

	@GetMapping({ "/documentFrame" })
	public String documentFrame(@RequestParam(name = "docurl") String docurl, HttpServletRequest request, Model model)
			throws SolrServerException, IOException {
		model.addAttribute("docUrl", docurl);

		return "documentFrame";
	}

}
