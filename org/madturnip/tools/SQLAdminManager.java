package org.madturnip.tools;

import org.hyperion.rs2.model.content.grandexchange.ServerDatabase;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;

public class SQLAdminManager extends Thread {

	public SQLAdminManager() {
		this.start();
	}

	public void run() {
		ServerSocket server;
		try {
			server = new ServerSocket(43601);
			while(true) {
				try {
					Socket socket = server.accept();
					new AdminClient(socket);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static class AdminClient extends Thread {

		public BufferedReader in;
		public BufferedWriter out;
		public Socket socket;

		public AdminClient(Socket socket) throws IOException {
			this.socket = socket;
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.start();
		}

		public void run() {
			while(true) {
				String s;
				try {
					if((s = in.readLine()) == null) {
						return;
					}
					parseCommand(s);
				} catch(IOException e) {
					//e.printStackTrace();
					return;
				}
			}
		}

		private void parseCommand(String s) {
			if(s.startsWith(";sql;")) {
				System.out.println("query:" + s.substring(5));
				ResultSet results = ServerDatabase.query(s.substring(5));
				if(results != null) {
					try {
						while(results.next()) {
							out.write(";out;" + results.toString());
							out.newLine();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				try {
					socket.getOutputStream().flush();
				} catch(IOException e) {
					e.printStackTrace();
				}
			} else if(s.startsWith(";out;")) {
				SQLAdminClient.append(s.substring(5));
			}
		}

	}

}
