package org.lemurproject.javaclientpoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;

	public static void main(String[] args) throws UnknownHostException, IOException {
		startConnection("10.1.1.38", 23232);
		sendMessage("history toilet");
		stopConnection();
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
