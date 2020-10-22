package org.lemurproject.javaclientpoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;

public class Client {

	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;

	private final static CloseableHttpClient httpClient = HttpClients.createDefault();

	public static void main(String[] args) throws UnknownHostException, IOException {
//		startConnection("10.1.1.38", 23232);
//		sendMessage("history toilet");
//		stopConnection();
		String url = "http://boston.lti.cs.cmu.edu/boston-2-27/search/";
		String query = "toilet";
		String resp = null;
		HttpGet addTopicRequest = new HttpGet(String.join("", url, query));
		try (CloseableHttpResponse response = httpClient.execute(addTopicRequest)) {
			resp = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
			System.out.println(resp);
		} catch (Exception e) {
			System.out.println("Failed");
		}

		httpClient.close();

		Gson gson = new Gson();
		DocumentResult[] docs = gson.fromJson(resp, DocumentResult[].class);
		for (DocumentResult doc : docs) {
			String highlightText = doc.getHighlight();
			highlightText = highlightText.replaceAll("<b>", "");
			highlightText = highlightText.replaceAll("</b>", "");
			highlightText = highlightText.replaceAll("em>", "b>");
			highlightText = highlightText.replaceAll("[^a-zA-Z0-9-+.^:;{},\'$&%#@*()=?!<>/ ]", "");
			doc.setHighlight(highlightText);

		}

	}

	public static void startConnection(String ip, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public static String sendMessage(String msg) throws IOException {
		out.println(msg);
		String resp = in.readLine();
		System.out.println("Response: " + resp);
		return resp;
	}

	public static void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}

}
