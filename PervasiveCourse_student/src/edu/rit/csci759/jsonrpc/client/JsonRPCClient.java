/**
 * @author srs6573 Shikha Soni
 * This is the client that send the android client constant temp changes 
 */
package edu.rit.csci759.jsonrpc.client;

//The Client sessions package
import java.net.MalformedURLException;
//For creating URLs
import java.net.URL;
import java.util.List;

//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

//The JSON Smart package for JSON encoding/decoding (optional)

public class JsonRPCClient {
	String send_method;
	List<Object> send;
	String server = null;

	/**
	 * Constructor of the client that sets the reading list and the server url
	 * @param redings List of objects toring the integer reading of the temperature and ambient
	 * @param id 
	 * @param URL
	 */
	public JsonRPCClient(List<Object> redings, String URL) {
		this.server = URL;
		this.send = redings;
		send();
	}

	/**
	 * sends the temp ambient to android server
	 */
	public void send() {
		URL serverURL = null;
		try {
			serverURL = new URL("http://" + server + ":2344");
		} catch (MalformedURLException e) {
			// handle exception...
		}
		// Create new JSON-RPC 2.0 client session
		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
		int requestID = 0;
		JSONRPC2Request request = new JSONRPC2Request("C_change", send, requestID);
		// Send request
		JSONRPC2Response response = null;
		try {
			// System.out.println("Sending to android");
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {

			System.err.println(e.getMessage());
			// handle exception...
		}

		// Print response result / error
		if (response.indicatesSuccess())
			System.out.println("Temp reading sent");
		else
			System.out.println(response.getError().getMessage());

	}
	/*
	 * public static void main(String args[]){ List<Object> l= new
	 * ArrayList<>(); l.add(43); l.add(56); new JsonRPCClient(l, 1); //new
	 * JsonRPCClient(54, 53, 2); }
	 */
}
