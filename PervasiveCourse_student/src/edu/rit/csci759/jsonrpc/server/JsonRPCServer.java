package edu.rit.csci759.jsonrpc.server;

//The JSON-RPC 2.0 Base classes that define the 
//JSON-RPC 2.0 protocol messages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
//The JSON-RPC 2.0 server framework package
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

import edu.rit.csci759.jsonrpc.client.JsonRPCClient;
import edu.rit.csci759.rspi.RpiIndicatorImplementation;

public class JsonRPCServer {
	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = 8080;

	/**
	 * A handler thread class. Handlers are spawned from the listening loop and
	 * are responsible for a dealing with a single client and broadcasting its
	 * messages.
	 */
	private static class Handler extends Thread {
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		private Dispatcher dispatcher;
		int id;
		static List<Socket> clients = new ArrayList<>();

		/**
		 * Constructs a handler thread, squirreling away the socket. All the
		 * interesting work is done in the run method.
		 */
		public Handler(Socket socket, int id) {
			this.socket = socket;
			this.id = id;
			clients.add(socket);
			// Create a new JSON-RPC 2.0 request dispatcher
			this.dispatcher = new Dispatcher();

			// Register the "echo", "getDate" and "getTime" handlers with it
			dispatcher.register(new JsonHandler.EchoHandler());
			dispatcher.register(new JsonHandler.DateTimeHandler());
			dispatcher.register(new JsonHandler.TempAmbientHandler());
			dispatcher.register(new JsonHandler.RuleHandler());
		}

		public Handler(int id) {
			this.id = id;
		}

		public void streamHandler() {
			try {
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				// read request
				String line;
				line = in.readLine();
				// System.out.println(line);
				StringBuilder raw = new StringBuilder();
				raw.append("" + line);
				boolean isPost = line.startsWith("POST");
				int contentLength = 0;
				while (!(line = in.readLine()).equals("")) {
					// System.out.println(line);
					raw.append('\n' + line);
					if (isPost) {
						final String contentHeader = "Content-Length: ";
						if (line.startsWith(contentHeader)) {
							contentLength = Integer.parseInt(line
									.substring(contentHeader.length()));
						}
					}
				}
				StringBuilder body = new StringBuilder();
				if (isPost) {
					int c = 0;
					for (int i = 0; i < contentLength; i++) {
						c = in.read();
						body.append((char) c);
					}
				}

				System.out.println(body.toString());
				JSONRPC2Request request = JSONRPC2Request
						.parse(body.toString());
				JSONRPC2Response resp = dispatcher.process(request, null);
				System.out.println(resp.toJSONString());
				// send response
				out.write("HTTP/1.1 200 OK\r\n");
				out.write("Content-Type: application/json\r\n");
				out.write("\r\n");
				out.write(resp.toJSONString());
				// do not in.close();
				out.flush();
				out.close();
				socket.close();
			} catch (IOException e) {
				System.out.println(e);
			} catch (JSONRPC2ParseException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		/**
		 * Services this thread's client by repeatedly requesting a screen name
		 * until a unique one has been submitted, then acknowledges the name and
		 * registers the output stream for the client in a global set, then
		 * repeatedly gets inputs and broadcasts them.
		 */
		public void run() {
			if (id == 1) {
				streamHandler();
			} else {
				while (true) {
					sendALL();
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * This method sends the temperature readings to all its clients once
		 * they are connected to the server
		 */
		public void sendALL() {
			List<Object> readings = new JsonHandler.ChangeHandler().send();
			if (readings == null) {
				System.out.println("Null readings");
			}
			// System.out.println("Sending to all clients");
			for (int index = 0; index < clients.size(); index++) {
				String IP = clients.get(index).getInetAddress()
						.getHostAddress();
				// System.out.println("Client called "+ IP );
				new JsonRPCClient(readings, IP);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		System.out.println("The server is running.");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Handler(listener.accept(), 1).start();
				// System.out.println("Connection received");
				Thread.sleep(5000);
				new Handler(2).start();
				// System.out.println("Started sending updates");
				new JsonHandler.ChangeHandler().start();
			}
		} finally {
			listener.close();
		}
	}
}
