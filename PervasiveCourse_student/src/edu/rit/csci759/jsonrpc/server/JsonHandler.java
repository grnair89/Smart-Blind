package edu.rit.csci759.jsonrpc.server;

/**
 * Demonstration of the JSON-RPC 2.0 Server framework usage. The request
 * handlers are implemented as static nested classes for convenience, but in 
 * real life applications may be defined as regular classes within their old 
 * source files.
 *
 * @author Vladimir Dzhuvinov
 * @version 2011-03-05
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minidev.json.JSONArray;
import net.sourceforge.jFuzzyLogic.demo.tipper.TipperAnimation;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import edu.rit.csci759.fuzzylogic.BlindMonitor;
import edu.rit.csci759.rspi.RpiIndicatorImplementation;

public class JsonHandler {
	static BlindMonitor blinder = new BlindMonitor();
	static RpiIndicatorImplementation rpi = new RpiIndicatorImplementation();

	// Implements a handler for an "echo" JSON-RPC method
	public static class EchoHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {
			return new String[] { "echo" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			if (req.getMethod().equals("echo")) {
				// Echo first parameter
				List params = (List) req.getParams();
				Object input = params.get(0);
				return new JSONRPC2Response(input, req.getID());
			} else {
				// Method name not supported
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	/**
	 * @author Shikha Soni This class handles the temperature and ambient of the
	 *         system. Using the RPI object it send the use the temp and ambient
	 * 
	 */
	public static class TempAmbientHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {
			return new String[] { "getTemp", "getAmbient" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
			//System.out.println("in temp handler: " + req);
			String hostname = "unknown";

			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			if (req.getMethod().equals("getTemp")) {
				RpiIndicatorImplementation Object = new RpiIndicatorImplementation();
				String temp = String.valueOf(Object.read_temperature());
				return new JSONRPC2Response(hostname + " " + temp, req.getID());

			} else if (req.getMethod().equals("getAmbient")) {
				RpiIndicatorImplementation Object = new RpiIndicatorImplementation();
				String ambient = String.valueOf(Object
						.read_ambient_light_intensity());
				return new JSONRPC2Response(hostname + " " + ambient,
						req.getID());
			} else {
				// Method name not supported
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	public static class DateTimeHandler implements RequestHandler {

		// Reports the method names of the handled requests
		public String[] handledRequests() {
			return new String[] { "getDate", "getTime" };
		}

		// Processes the requests
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			if (req.getMethod().equals("getDate")) {
				DateFormat df = DateFormat.getDateInstance();
				String date = df.format(new Date());
				return new JSONRPC2Response(hostname + " " + date, req.getID());
			} else if (req.getMethod().equals("getTime")) {
				DateFormat df = DateFormat.getTimeInstance();
				String time = df.format(new Date());
				return new JSONRPC2Response(hostname + " " + time, req.getID());
			} else {
				// Method name not supported
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	/**
	 * @author srs6573 Shikha Soni This class handles the sending and receiving
	 *         of the rules of a given client
	 * 
	 */
	public static class RuleHandler implements RequestHandler {

		@Override
		public String[] handledRequests() {
			// TODO Auto-generated method stub
			return new String[] { "RegisterRule", "DeleteRule", "getRules" };
		}

		@Override
		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {
			//System.out.println(req.toJSONString());
			JSONArray ruleMethod = (JSONArray) req.getParams();
			String hostname = "unknown";
			try {
				hostname = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			// System.out.println(req.getMethod());
			if (req.getMethod().equals("RegisterRule")) {
				boolean returnstate = blinder.setRule(ruleMethod);
				if (!returnstate) {
					return new JSONRPC2Response(hostname + "Failure",
							req.getID());
				} else {
					return new JSONRPC2Response(hostname + "Success",
							req.getID());
				}
			} else if (req.getMethod().equals("DeleteRule")) {
				blinder.deleteRule(ruleMethod);
				return new JSONRPC2Response(hostname + "Success", req.getID());
			} else if (req.getMethod().equals("getRules")) {
				return new JSONRPC2Response(hostname + blinder.sendRules(),
						req.getID());
			} else {
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,
						req.getID());
			}
		}
	}

	/**
	 * @author Shikha Soni This class send the temp changes to the user as well
	 *         as runs a blind monitoring in a background thread
	 * 
	 */
	public static class ChangeHandler extends Thread {
		
		int temp, ambient;

		public List<Object> send() {
			// System.out.println("Inside change");
			temp = rpi.read_temperature();
			blinder.setTemp(temp);
			ambient = rpi.read_ambient_light_intensity();
			blinder.setAmbient(ambient);
			List<Object> readings = new ArrayList<>();
			readings.add(Integer.toString(temp));
			readings.add(blinder.TempPos());
			// System.out.println("Returning with: " + temp + ", " + ambient);
			return readings;
		}

		public void run() {
			// System.out.println("Inside monitor");
			int temp, ambient;
			while (true) {
				try {
					Thread.sleep(10000);
					temp = rpi.read_temperature();
					blinder.setTemp(temp);
					ambient = rpi.read_ambient_light_intensity();
					// System.out.println("In run temp: " + temp + " : " +
					// ambient);
					blinder.setAmbient(ambient);
					List<Object> readings = new ArrayList<>();
					readings.add(temp);
					readings.add(ambient);
					String blind = blinder.blindPos();
					// System.out.println("Blind: " + blind);
					if (blind.equals("[blind IS open]")) {
						rpi.led_when_high();
						Thread.sleep(5000);
						rpi.led_all_off();
					} else if (blind.equals("[blind IS half]")) {
						rpi.led_when_mid();
						Thread.sleep(5000);
						rpi.led_all_off();
					} else if (blind.equals("[blind IS close]")) {
						rpi.led_when_low();
						Thread.sleep(5000);
						rpi.led_all_off();
					} else {
						System.out.println(blind);
					}
					Thread.sleep(8000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// System.out.println("Thread "+ this.isAlive());
			}
		}
	}
}

