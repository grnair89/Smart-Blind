package test.rit.harsh.myapplication;

import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JSONHandler {
    public static JSONRPC2Request request;

    public static String testJSONRequest(String server_URL_text, String method) {
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
        Log.d("Debug serverURL", server_URL_text);
        // The JSON-RPC 2.0 server URL
        URL serverURL = null;
        try {
            serverURL = new URL("http://" + server_URL_text);
        } catch (MalformedURLException e) {
            // handle exception...
        }
        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

        // Construct new request
        int requestID = 0;
        if (method.contains(",")) {
            // rules list for adding parameters
            List<Object> rules = new ArrayList<Object>();
            String newmethod[] = method.split(",");
            rules.add(newmethod[1]);
            // JSONRPC@Request for handling parametes
            request = new JSONRPC2Request(newmethod[0], rules, requestID);
        } else {
            request = new JSONRPC2Request(method, requestID);
        }
        // Send request
        JSONRPC2Response response = null;
        try {
            response = mySession.send(request);
        } catch (JSONRPC2SessionException e) {
            Log.e("error", e.getMessage().toString());
            // handle exception...
        }
        // Print response result / error
        if (response.indicatesSuccess())
            Log.d("debug", response.getResult().toString());
        else
            Log.e("error", response.getError().getMessage().toString());
        return response.getResult().toString();
    }

    public static class UpdateHandler implements RequestHandler {
        private static BGNotiService obj;

        UpdateHandler(BGNotiService obj) {
            this.obj = obj;
        }

        // Reports the method names of the handled requests
        public String[] handledRequests() {

            return new String[]{"C_change"};
        }

        // Processes the requests
        public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
            JSONArray incoming = (JSONArray) req.getParams();

            String hostname = "unknown";
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (req.getMethod().equals("C_change")) {
                Log.d("msg from process", "change reveived" + incoming.toString());
                String temp = incoming.toJSONString().replace("[", "").replace("]", "");
                obj.onUpdate(temp);
                return new JSONRPC2Response(hostname + "success", req.getID());

            } else {
                // Method name not supported
                return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND, req.getID());
            }
        }
    }
}
