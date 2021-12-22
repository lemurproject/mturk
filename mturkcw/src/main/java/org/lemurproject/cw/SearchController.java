package org.lemurproject.cw;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

	@Autowired
	private SearchService searchService;

	@Autowired
	private CWProperties searchProps;

	private BufferedWriter trialResultWriter;

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

	@GetMapping({ "/qualificationtest" })
	public String qualificationtest() throws FileNotFoundException {
		return "qualtest1";
	}

	@GetMapping({ "/search" })
	public String search(@RequestParam(name = "assignmentId") String assignmentId, HttpServletRequest request,
			Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		return "search";
	}

	@GetMapping({ "/searchTest" })
	public String searchTest(HttpServletRequest request, Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		model.addAttribute("searchObject", searchObject);
		return "search";
	}

	@GetMapping({ "/searchcategories" })
	public String searchcategories1(@RequestParam(name = "assignmentId", required = false) String assignmentId,
			HttpServletRequest request, Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		List<String> categories = searchService.getSearchCategories();
		long inputStart = System.currentTimeMillis();
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		model.addAttribute("inputStart", inputStart);
		if (categories.size() > 0) {
			model.addAttribute("categories", categories);
			return "searchcategories";
		} else {
			model.addAttribute("category", "Any");
			return "searchnocategories";
		}

	}

	@PostMapping({ "/searchcategories" })
	public String searchcategories2(@RequestParam(name = "assignmentId", required = false) String assignmentId,
			HttpServletRequest request, @ModelAttribute("searchResult") SearchResult searchResult, Model model)
			throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		searchObject.setPrevQuery(searchResult.getQuery());
		List<String> categories = searchService.getSearchCategories();
		long inputStart = System.currentTimeMillis();
		model.addAttribute("prevQuery", searchResult.getQuery());
		model.addAttribute("prevDescription", searchResult.getDescription());
		model.addAttribute("prevCategory", searchResult.getCategory());
		model.addAttribute("prevDoc1id", searchResult.getDocuments().get(0).getDocId());
		model.addAttribute("prevDoc1selection", searchResult.getDocuments().get(0).getSelected().toString());
		model.addAttribute("prevDoc2id", searchResult.getDocuments().get(1).getDocId());
		model.addAttribute("prevDoc2selection", searchResult.getDocuments().get(1).getSelected().toString());
		model.addAttribute("prevDoc3id", searchResult.getDocuments().get(2).getDocId());
		model.addAttribute("prevDoc3selection", searchResult.getDocuments().get(2).getSelected().toString());
		model.addAttribute("prevDoc4id", searchResult.getDocuments().get(3).getDocId());
		model.addAttribute("prevDoc4selection", searchResult.getDocuments().get(3).getSelected().toString());
		model.addAttribute("prevDoc5id", searchResult.getDocuments().get(4).getDocId());
		model.addAttribute("prevDoc5selection", searchResult.getDocuments().get(4).getSelected().toString());
		model.addAttribute("prevDoc6id", searchResult.getDocuments().get(5).getDocId());
		model.addAttribute("prevDoc6selection", searchResult.getDocuments().get(5).getSelected().toString());
		model.addAttribute("prevDoc7id", searchResult.getDocuments().get(6).getDocId());
		model.addAttribute("prevDoc7selection", searchResult.getDocuments().get(6).getSelected().toString());
		model.addAttribute("prevDoc8id", searchResult.getDocuments().get(7).getDocId());
		model.addAttribute("prevDoc8selection", searchResult.getDocuments().get(7).getSelected().toString());
		model.addAttribute("prevDoc9id", searchResult.getDocuments().get(8).getDocId());
		model.addAttribute("prevDoc9selection", searchResult.getDocuments().get(8).getSelected().toString());
		model.addAttribute("prevDoc10id", searchResult.getDocuments().get(9).getDocId());
		model.addAttribute("prevDoc10selection", searchResult.getDocuments().get(9).getSelected().toString());
		model.addAttribute("prevDoc11id", searchResult.getDocuments().get(10).getDocId());
		model.addAttribute("prevDoc11selection", searchResult.getDocuments().get(10).getSelected().toString());
		model.addAttribute("prevDoc12id", searchResult.getDocuments().get(11).getDocId());
		model.addAttribute("prevDoc12selection", searchResult.getDocuments().get(11).getSelected().toString());
		model.addAttribute("prevDoc13id", searchResult.getDocuments().get(12).getDocId());
		model.addAttribute("prevDoc13selection", searchResult.getDocuments().get(12).getSelected().toString());

		model.addAttribute("categories", categories);
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		model.addAttribute("inputStart", inputStart);
		return "searchcategories";
	}

	@GetMapping({ "/searchcategoriestrial" })
	public String searchcategoriestrial(@RequestParam(name = "assignmentId") String assignmentId,
			HttpServletRequest request, Model model) throws FileNotFoundException {
		SearchObject searchObject = new SearchObject();
		List<String> categories = searchService.getSearchCategories();
		long inputStart = System.currentTimeMillis();
		model.addAttribute("categories", categories);
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		model.addAttribute("inputStart", inputStart);
		return "searchcategoriestrial";
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
	public String getSearchResultsBERT(@ModelAttribute("searchObject") SearchObject prevSearchObject,
			@RequestParam(name = "queryString") String query,
			// @RequestParam(name = "queryDescription") String description,
			@RequestParam(name = "category") String category,
			@RequestParam(name = "assignmentId", required = false) String assignmentId,
			@RequestParam(name = "inputStart") long inputStart, HttpServletRequest request, Model model)
			throws SolrServerException, IOException {
		long startTime = System.currentTimeMillis();
		String queryToLower = query.trim().toLowerCase();
		if (queryToLower.equalsIgnoreCase(category)) {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			long newInputStart = System.currentTimeMillis();
			model.addAttribute("inputStart", newInputStart);
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage", "The query cannot match the category name.  Please try another search.");
			return "searchcategories";
		} else if (searchService.checkPreviousQueries(queryToLower)) {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			long newInputStart = System.currentTimeMillis();
			model.addAttribute("inputStart", newInputStart);
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage", "This query matches a previous one.  Please try another search.");
			return "searchcategories";
		}

		SearchResult searchResult = searchService.bertSearch(query);
		searchResult.setSubmitUrl(searchProps.getSubmitUrl());
		long endTime = System.currentTimeMillis();
		long queryTime = endTime - startTime;
		long inputTime = startTime - inputStart;
		if (searchResult.getDocuments() != null && searchResult.getDocuments().size() >= 10) {
			// searchResult.setDescription(description);
			searchResult.setCategory(category);
			searchResult.setAssignmentId(assignmentId);
			searchResult.setQueryTime(queryTime);
			searchResult.setInputTime(inputTime);
			model.addAttribute("searchResult", searchResult);
			if (prevSearchObject.getPrevQuery() != null && prevSearchObject.getPrevQuery().trim().length() > 0) {
				searchResult.setPrevQuery(prevSearchObject.getPrevQuery());
				searchResult.setPrevDescription(prevSearchObject.getPrevDescription());
				searchResult.setPrevCategory(prevSearchObject.getPrevCategory());
				searchResult.setPrevDoc1id(prevSearchObject.getPrevDoc1id());
				searchResult.setPrevDoc1selection(prevSearchObject.getPrevDoc1selection());
				searchResult.setPrevDoc2id(prevSearchObject.getPrevDoc2id());
				searchResult.setPrevDoc2selection(prevSearchObject.getPrevDoc2selection());
				searchResult.setPrevDoc3id(prevSearchObject.getPrevDoc3id());
				searchResult.setPrevDoc3selection(prevSearchObject.getPrevDoc3selection());
				searchResult.setPrevDoc4id(prevSearchObject.getPrevDoc4id());
				searchResult.setPrevDoc4selection(prevSearchObject.getPrevDoc4selection());
				searchResult.setPrevDoc5id(prevSearchObject.getPrevDoc5id());
				searchResult.setPrevDoc5selection(prevSearchObject.getPrevDoc5selection());
				searchResult.setPrevDoc6id(prevSearchObject.getPrevDoc6id());
				searchResult.setPrevDoc6selection(prevSearchObject.getPrevDoc6selection());
				searchResult.setPrevDoc7id(prevSearchObject.getPrevDoc7id());
				searchResult.setPrevDoc7selection(prevSearchObject.getPrevDoc7selection());
				searchResult.setPrevDoc8id(prevSearchObject.getPrevDoc8id());
				searchResult.setPrevDoc8selection(prevSearchObject.getPrevDoc8selection());
				searchResult.setPrevDoc9id(prevSearchObject.getPrevDoc9id());
				searchResult.setPrevDoc9selection(prevSearchObject.getPrevDoc9selection());
				searchResult.setPrevDoc10id(prevSearchObject.getPrevDoc10id());
				searchResult.setPrevDoc10selection(prevSearchObject.getPrevDoc10selection());
				searchResult.setPrevDoc11id(prevSearchObject.getPrevDoc11id());
				searchResult.setPrevDoc11selection(prevSearchObject.getPrevDoc11selection());
				searchResult.setPrevDoc12id(prevSearchObject.getPrevDoc12id());
				searchResult.setPrevDoc12selection(prevSearchObject.getPrevDoc12selection());
				searchResult.setPrevDoc13id(prevSearchObject.getPrevDoc13id());
				searchResult.setPrevDoc13selection(prevSearchObject.getPrevDoc13selection());
				return "results";
			} else {
				return "results1";
			}
		} else if (searchResult.getDocuments() != null && searchResult.getDocuments().size() > 0) {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			long newInputStart = System.currentTimeMillis();
			model.addAttribute("inputStart", newInputStart);
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage",
					"There were under 10 results for your query.  Please try another search.");
			return "searchcategories";
		} else {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			long newInputStart = System.currentTimeMillis();
			model.addAttribute("inputStart", newInputStart);
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage", "There were no results for your query.  Please try another search.");
			return "searchcategories";
		}
	}

	@PostMapping({ "/submitResults" })
	public String submitResults(HttpServletRequest request, @ModelAttribute("searchResult") SearchResult searchResult,
			@RequestParam(name = "assignmentId") String assignmentId, Model model)
			throws SolrServerException, IOException {
		System.out.println("Submitted Results");
		QueryResponseObject response = new QueryResponseObject();
		response.setQuery(searchResult.getQuery());
		response.setCategory(searchResult.getCategory());
		response.setDoc1id(searchResult.getDocuments().get(0).getDocId());
		response.setDoc1selected(searchResult.getDocuments().get(0).getSelected());
		response.setDoc2id(searchResult.getDocuments().get(1).getDocId());
		response.setDoc2selected(searchResult.getDocuments().get(1).getSelected());
		response.setDoc3id(searchResult.getDocuments().get(2).getDocId());
		response.setDoc3selected(searchResult.getDocuments().get(2).getSelected());
		response.setDoc4id(searchResult.getDocuments().get(3).getDocId());
		response.setDoc4selected(searchResult.getDocuments().get(3).getSelected());
		response.setDoc5id(searchResult.getDocuments().get(4).getDocId());
		response.setDoc5selected(searchResult.getDocuments().get(4).getSelected());
		response.setDoc6id(searchResult.getDocuments().get(5).getDocId());
		response.setDoc6selected(searchResult.getDocuments().get(5).getSelected());
		response.setDoc7id(searchResult.getDocuments().get(6).getDocId());
		response.setDoc7selected(searchResult.getDocuments().get(6).getSelected());
		response.setDoc8id(searchResult.getDocuments().get(7).getDocId());
		response.setDoc8selected(searchResult.getDocuments().get(7).getSelected());
		response.setDoc9id(searchResult.getDocuments().get(8).getDocId());
		response.setDoc9selected(searchResult.getDocuments().get(8).getSelected());
		response.setDoc10id(searchResult.getDocuments().get(9).getDocId());
		response.setDoc10selected(searchResult.getDocuments().get(9).getSelected());
		response.setDoc11id(searchResult.getDocuments().get(10).getDocId());
		response.setDoc11selected(searchResult.getDocuments().get(10).getSelected());
		response.setDoc12id(searchResult.getDocuments().get(11).getDocId());
		response.setDoc12selected(searchResult.getDocuments().get(11).getSelected());
		response.setDoc13id(searchResult.getDocuments().get(12).getDocId());
		response.setDoc13selected(searchResult.getDocuments().get(12).getSelected());

		searchService.writeResults(response);

		SearchObject searchObject = new SearchObject();
		List<String> categories = searchService.getSearchCategories();
		long inputStart = System.currentTimeMillis();
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", assignmentId);
		model.addAttribute("inputStart", inputStart);
		if (categories.size() > 0) {
			model.addAttribute("categories", categories);
			return "searchcategories";
		} else {
			model.addAttribute("category", "Any");
			return "searchnocategories";
		}
	}

	@PostMapping({ "/searchResultsBERTTrial" })
	public String getSearchResultsBERTTrial(@RequestParam(name = "queryString") String query,
			@RequestParam(name = "queryDescription") String description,
			@RequestParam(name = "category") String category, @RequestParam(name = "assignmentId") String assignmentId,
			@RequestParam(name = "inputStart") long inputStart, HttpServletRequest request, Model model)
			throws SolrServerException, IOException {
		long startTime = System.currentTimeMillis();
		SearchResult searchResult = searchService.bertSearch(query);
		long endTime = System.currentTimeMillis();
		long queryTime = endTime - startTime;
		long inputTime = startTime - inputStart;
		if (searchResult.getDocuments() != null && searchResult.getDocuments().size() >= 10) {
			searchResult.setDescription(description);
			searchResult.setCategory(category);
			searchResult.setAssignmentId(assignmentId);
			searchResult.setQueryTime(queryTime);
			searchResult.setInputTime(inputTime);
			model.addAttribute("searchResult", searchResult);
			return "resultstrial";
		} else if (searchResult.getDocuments() != null && searchResult.getDocuments().size() > 0) {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage",
					"There were under 10 results for your query.  Please try another search");
			return "searchcategoriestrial";
		} else {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("assignmentId", assignmentId);
			model.addAttribute("errorMessage", "There were no results for your query.  Please try another search");
			return "searchcategoriestrial";
		}
	}

	@PostMapping({ "/searchResultsTest" })
	public String getSearchResultsTest(@RequestParam(name = "queryString") String query, HttpServletRequest request,
			Model model) throws SolrServerException, IOException {
		SearchResult searchResult = searchService.bertSearch(query);
		if (searchResult.getDocuments() != null && searchResult.getDocuments().size() > 0) {
			model.addAttribute("searchResult", searchResult);
			return "resultstest";
		} else {
			SearchObject searchObject = new SearchObject();
			List<String> categories = searchService.getSearchCategories();
			model.addAttribute("categories", categories);
			model.addAttribute("searchObject", searchObject);
			model.addAttribute("errorMessage", "There were no results for your query.  Please try another search");
			return "searchcategories";
		}
	}

	@PostMapping({ "/recordResults" })
	public String recordResults(@ModelAttribute("searchResult") SearchResult searchResult, ModelMap model)
			throws SolrServerException, IOException {
		if (trialResultWriter == null) {
			trialResultWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resulttrial.csv")));
		}
		StringJoiner documentScoreBuffer = new StringJoiner(",");
		for (DocumentResult doc : searchResult.getDocuments()) {
			documentScoreBuffer.add(doc.getDocId());
			if (doc.getSelected().booleanValue()) {
				documentScoreBuffer.add("1");
			} else {
				documentScoreBuffer.add("0");
			}
		}
		String resultLine = String.join(",", searchResult.getQuery(),
				String.join("", " \"", searchResult.getDescription()), "\"", documentScoreBuffer.toString());
		trialResultWriter.write(resultLine);
		trialResultWriter.write("\n");
		trialResultWriter.flush();

		SearchObject searchObject = new SearchObject();
		List<String> categories = searchService.getSearchCategories();
		long inputStart = System.currentTimeMillis();
		model.addAttribute("categories", categories);
		model.addAttribute("searchObject", searchObject);
		model.addAttribute("assignmentId", "test");
		model.addAttribute("inputStart", inputStart);
		return "searchcategoriestrial";
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
