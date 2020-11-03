package org.lemurproject.cw;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient.Builder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component("searchService")
public class SearchService {

	@Autowired
	private GlobalProperties searchProps;

	private CloudSolrClient solrClient;

	private final CloseableHttpClient httpClient = HttpClients.createDefault();

//	private static Socket clientSocket;
//	private static PrintWriter out;
//	private static BufferedReader in;

	@PostConstruct
	public void init() throws UnknownHostException, IOException {
		List<String> zkHosts = new ArrayList<String>();
		String zkHost = String.join(":", searchProps.getHostName(), searchProps.getHostPort());
		zkHosts.add(zkHost);
		Builder builder = new CloudSolrClient.Builder(zkHosts, java.util.Optional.empty());
		solrClient = builder.build();
		solrClient.setDefaultCollection(searchProps.getCollectionName());
//		startConnection("10.1.1.35", 23232);
	}

	public SearchResult search(String queryString) throws SolrServerException, IOException {

		String[] queryWords = queryString.split(" ");
		StringJoiner queryBuffer = new StringJoiner(" ");
		for (String word : queryWords) {
			queryBuffer.add(String.join(":", "fulltext", word));
		}

		SolrQuery query = new SolrQuery();
		System.out.println(queryBuffer.toString());
		query.setQuery(queryBuffer.toString());
		System.out.println(String.join("", "{!ltr model=", searchProps.getLtrModel(), " efi.text=", queryString, "}"));
		query.set("rq", String.join("", "{!ltr model=", searchProps.getLtrModel(), " reRankDocs=100 efi.text=",
				queryString, "}"));
		query.set("fl", String.join("", "id,url,title,score,[features store=", searchProps.getFeatureStore(),
				" efi.text=", queryString, "]"));
		query.set("hl", "true");
		query.set("hl.fl", "fulltext");
		query.set("hl.fragsize", "500");
		query.setSort("score", ORDER.desc);
		query.setRows(Integer.valueOf(10));
		System.out.println(query.toString());

		QueryResponse response = solrClient.query(query);
		SolrDocumentList results = response.getResults();
		Map<String, Map<String, List<String>>> highlightsMap = response.getHighlighting();
		List<DocumentResult> documentResults = new ArrayList<DocumentResult>();
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
			highlightText = highlightText.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?!<>/ ]", "");

			DocumentResult searchResult = new DocumentResult();
			searchResult.setDocId(docId);
			searchResult.setTitle((String) results.get(i).get("title"));
			searchResult.setUrl((String) results.get(i).get("url"));
			searchResult.setScore((Float) results.get(i).get("score"));
			searchResult.setHighlight(String.join("", highlightText, "..."));
			documentResults.add(searchResult);
		}
		SearchResult searchResult = new SearchResult();
		searchResult.setQuery(queryString);
		searchResult.setDocuments(documentResults);

		return searchResult;
	}

	public SearchResult bertSearch(String queryString) throws IOException {
//		out.println(queryString);
//		String resp = in.readLine();
//		System.out.println(resp);

		String url = "http://boston.lti.cs.cmu.edu/boston-2-25/search/";
		String resp = null;
		String query = queryString.replace(" ", "%20");
		HttpGet addTopicRequest = new HttpGet(String.join("", url, query));
		try (CloseableHttpResponse response = httpClient.execute(addTopicRequest)) {
			resp = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
			// System.out.println(resp);
		} catch (Exception e) {
			System.out.println("Failed");
		}

		Gson gson = new Gson();
		DocumentResult[] docs = gson.fromJson(resp, DocumentResult[].class);
		for (DocumentResult doc : docs) {
			String titleText = doc.getTitle();
			titleText = titleText.replaceAll("<b>", "");
			titleText = titleText.replaceAll("</b>", "");
			titleText = titleText.replaceAll("em>", "b>");
			titleText = titleText.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?!<>/ ]", "");

			if (titleText.length() > 100) {
				int endIndex = titleText.indexOf(" ", 100);
				if (endIndex > 100) {
					titleText = String.join(" ", titleText.substring(0, endIndex), "...");
				}
			}
			doc.setTitle(titleText);

			String highlightText = doc.getHighlight();
			highlightText = highlightText.replaceAll("<b>", "");
			highlightText = highlightText.replaceAll("</b>", "");
			highlightText = highlightText.replaceAll("em>", "b>");
			highlightText = highlightText.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?!<>/ ]", "");
			doc.setHighlight(highlightText);

		}
		SearchResult searchResult = new SearchResult();
		searchResult.setQuery(queryString);
		searchResult.setDocuments(Arrays.asList(docs));

		return searchResult;
	}

	public SearchResult getSampleResults(String queryString) {

		List<DocumentResult> documentResults = new ArrayList<DocumentResult>();
		List<String> urls = new ArrayList<String>();
		urls.add("https://en.wikipedia.org/wiki/Family_of_Barack_Obama");
		urls.add("https://www.cnn.com/");
		urls.add("https://www.horsehealthproducts.com/horsemans-report/hoof-leg-care/hoof-anatomy");
		urls.add("https://www.audubon.org/birding");
		urls.add("https://www.cmu.edu/");
		urls.add("https://lti.cs.cmu.edu/");
		urls.add("https://www.bbc.com/");
		urls.add("https://www.bbc.com/news/health-52439005");
		urls.add("https://www.alleghenycounty.us/Health-Department/Resources/COVID-19/COVID-19.aspx");
		urls.add("https://www.instagram.com/p/B_GCZXQpIDT/?utm_source=ig_web_copy_link");
		for (int i = 0; i < 10; ++i) {
			String docId = String.join("", "Doc", String.valueOf(i));

			DocumentResult documentResult = new DocumentResult();
			documentResult.setDocId(docId);
			documentResult.setTitle(String.join("", "Doc Title ", String.valueOf(i)));
			documentResult.setUrl(urls.get(i));
			documentResult.setScore(Float.valueOf(i));
			documentResult.setHighlight(String.join("", "Doc Snippet ", String.valueOf(i),
					" lalalalalalalla lalalalalal lalalalalalala lalalalalala lalalalalalalala lalalalalalala lalalalalala llalalala lalalalalalala lalalalalalala lalalalalalala lalalalalla lalala"));
			documentResults.add(documentResult);
		}
		SearchResult searchResult = new SearchResult();
		searchResult.setQuery(queryString);
		searchResult.setDocuments(documentResults);

		return searchResult;

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
		doctext = doctext.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?!<>/ ]", "");
		doctext = doctext.replaceAll("/n", "<br/>");
		doctext = doctext.replaceAll("/t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

		return doctext;
	}

//	public static void startConnection(String ip, int port) throws UnknownHostException, IOException {
//		clientSocket = new Socket(ip, port);
//		out = new PrintWriter(clientSocket.getOutputStream(), true);
//		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//	}
//
//	public static String sendMessage(String msg) throws IOException {
//		out.println(msg);
//		String resp = in.readLine();
//		System.out.println("Response: " + resp);
//		return resp;
//	}
//
//	public static void stopConnection() throws IOException {
//		in.close();
//		out.close();
//		clientSocket.close();
//	}

}
