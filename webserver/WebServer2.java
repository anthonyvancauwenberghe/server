package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Arsen Maxyutov.
 */
public class WebServer2 extends Thread {

	public static void main(String[] args) throws IOException {
		new WebServer2().start();
	}

	/**
	 * The server port.
	 */
	public static final int PORT = 8080;

	/**
	 * The server socket.
	 */
	private ServerSocket serverSocket;

	/**
	 * The running flag.
	 */
	private boolean running;

	/**
	 * Creates a new single threaded WebServer running on port <code>PORT</code>.
	 *
	 * @throws IOException
	 */
	public WebServer2() throws IOException {
		serverSocket = new ServerSocket(PORT);
		running = true;
	}

	@Override
	public void run() {
		while(running) {
			System.out.println("Started the webserver!");
			try {
				Socket clientSocket = serverSocket.accept();
				handleSession(clientSocket);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles a socket.
	 *
	 * @param socket
	 * @throws IOException
	 */
	public void handleSession(Socket socket) throws IOException {
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		PrintStream out = new PrintStream(socket.getOutputStream());
		System.out.println("Incoming HTTP Request..");
		String line;
		while((line = in.readLine()) != null && line.trim().length() > 0) {
			//todo
		}
		System.out.println("Sending response");
		out.println("HTTP /1.1 200 OK");
		out.println();
		out.println("<html>");
		out.println("<head>");
		out.println("<title> Arsen's web server derpinadog</title>");
		out.println("</head>");
		out.println("<b>Testttttttt</b>");
		out.println("<body>");
		out.println("</body>");
		out.println("</html>");
		in.close();
		out.close();
		socket.close();
	}

	public List<String> getResponseBody() {
		List<String> body = new LinkedList<String>();
		return body;
	}


}
