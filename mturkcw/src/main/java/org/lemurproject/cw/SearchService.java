package org.lemurproject.cw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient.Builder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("searchService")
public class SearchService {
	
	@Autowired
	private GlobalProperties searchProps;
	
	private CloudSolrClient solrClient;
	
	@PostConstruct
	public void init() {
		List<String> zkHosts = new ArrayList<String>();
		String zkHost = String.join(":", searchProps.getHostName(), searchProps.getHostPort());
		zkHosts.add(zkHost);
		Builder builder = new CloudSolrClient.Builder(zkHosts, java.util.Optional.empty());
		solrClient = builder.build();
		solrClient.setDefaultCollection(searchProps.getCollectionName());
	}

	public List<SearchResult> search(String queryString) throws SolrServerException, IOException {
		
		String[] queryWords = queryString.split(" ");
		StringJoiner queryBuffer = new StringJoiner(" ");
		for (String word : queryWords) {
			queryBuffer.add(String.join(":", "fulltext", word));
		}

		SolrQuery query = new SolrQuery();
		System.out.println(queryBuffer.toString());
		query.setQuery(queryBuffer.toString());
		System.out.println(
				String.join("", "{!ltr model=", searchProps.getLtrModel(), " efi.text=", queryString, "}"));
		query.set("rq", String.join("", "{!ltr model=", searchProps.getLtrModel(), " reRankDocs=100 efi.text=",
				queryString, "}"));
		query.set("fl", String.join("", "id,url,title,score,[features store=", searchProps.getFeatureStore(),
				" efi.text=", queryString, "]"));
		query.set("hl", "true");
		query.set("hl.fl", "fulltext");
		query.set("hl.fragsize", "500");
		query.setSort("score", ORDER.desc);
		query.setRows(Integer.valueOf(10));

		QueryResponse response = solrClient.query(query);
		SolrDocumentList results = response.getResults();
		Map<String, Map<String, List<String>>> highlightsMap = response.getHighlighting();
		List<SearchResult> searchResults = new ArrayList<SearchResult>();
		for (int i = 0; i < results.size(); ++i) {
			String docId = (String) results.get(i).get("id");
			
			if (highlightsMap == null) {
				System.out.println("No Highlights!");
			} else if (highlightsMap.get(docId) == null) {
				System.out.println("Document: " + docId + " does not have highlights");
			}
			List<String> highlights = highlightsMap.get(docId).get("fulltext");
			String highlightText = String.join("... ", highlights);
			highlightText = highlightText.replaceAll("<b>", "");
			highlightText = highlightText.replaceAll("</b>", "");
			highlightText = highlightText.replaceAll("em>", "b>");
			
			SearchResult searchResult = new SearchResult();
			searchResult.setDocId(docId);
			searchResult.setTitle((String) results.get(i).get("title"));
			searchResult.setUrl((String) results.get(i).get("url"));
			searchResult.setScore((Float)results.get(i).get("score"));
			searchResult.setSnippet(String.join("", highlightText, "..."));
			searchResults.add(searchResult);
		}
		
		return searchResults;
	}
	
	public String getDocumentText(String docid) throws SolrServerException, IOException {
		String doctext = "";
		SolrQuery query = new SolrQuery();
		String queryString = String.join(":", "id", docid);
		query.setQuery(queryString);
		query.set("fl", "fulltext");
		query.setRows(Integer.valueOf(1));
		
		QueryResponse response = solrClient.query(query);
		SolrDocumentList results = response.getResults();
		doctext = (String) results.get(0).get("fulltext");
			
		return doctext;
	}

}
