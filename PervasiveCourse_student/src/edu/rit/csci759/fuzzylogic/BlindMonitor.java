/**
 * @author Shikha Soni
 * @author Harsh Patil 
 * @author Ganesh Rajasekharan
 */
package edu.rit.csci759.fuzzylogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minidev.json.JSONArray;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrBoundedSum;

public class BlindMonitor {
	FIS fis;
	static FunctionBlock function_block;
	static RuleBlock rule_block;
	Double currentTemp;
	Double currentAmbient;
	private static HashMap<String, RuleBlock> ruleBlockMap;
	private static List<Rule> rulesList;
	private String filename = "";

	/*
	 * public static void main(String[] args) throws Exception { //String
	 * filename = "FuzzyLogic/tipper.fcl"; FIS fis = FIS.load(filename, true);
	 * 
	 * JSONArray array = null;
	 * 
	 * MyTipperClass tipperobj= new MyTipperClass();
	 * 
	 * tipperobj.setRule(array); tipperobj.deleteRule(array);
	 * tipperobj.setRule(array); }
	 */

	/**
	 * Constructor It initiates the function block and the rule block with the
	 * file blinder.fcl
	 */
	public BlindMonitor() {
		// loading the fuzzy file
		filename = "FuzzyLogic/blinder.fcl";
		fis = FIS.load(filename, true);

		// call the function block and ruleblock
		function_block = fis.getFunctionBlock(null);
		rule_block = function_block.getFuzzyRuleBlock("No1");
		ruleBlockMap=function_block.getRuleBlocks();
	}

	/**
	 * Sets the temperature
	 * 
	 * @param temp
	 *            double value assigned from the GPIO pin readings
	 */
	public void setTemp(double temp) {
		currentTemp = temp;
		System.out.println("Temp: " + temp);
		function_block.setVariable("temp", currentTemp);
	}

	/**
	 * Sets the ambient
	 * 
	 * @param ambient
	 *            double value assigned from the GPIO pin readings
	 */
	public void setAmbient(double ambient) {
		currentAmbient = ambient;
		System.out.println("Ambient: " + ambient);
		function_block.setVariable("ambient", currentAmbient);
	}

	public String TempPos() {
		function_block.evaluate();
		double dim = function_block.getVariable("ambient").getMembership("dim");
		System.out.println(dim);
		double bright = function_block.getVariable("ambient").getMembership(
				"bright");
		System.out.println(bright);
		double dark = function_block.getVariable("ambient").getMembership(
				"dark");
		System.out.println(dark);
		if (dim > bright) {
			if (dim > dark) {
				System.out.println("DIM");
				return "DIM";
			} else {
				System.out.println("DARK");
				return "DARK";
			}
		} else if (bright > dark) {
			System.out.println("BRIGHT");
			return "BRIGHT";
		} else {
			System.out.println("DARK");
			return "DARK";
		}
	}

	/**
	 * sets the rule block with the rules sent by the client
	 * 
	 * @param array
	 *            the array of rules sent by the user
	 * @return returns a true if the rules were successfully added
	 */
	public boolean setRule(JSONArray array) {
		// System.out.println("inside setrule");
		String temp = array.toJSONString();
		rulesList = rule_block.getRules();
		temp = temp.replace("[", "").replace("]", "").replace("\"", "");
		temp = temp.replace("\\", "");
		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}
		String[] ruleList = temp.split(":");
		// check if it is a conflicting rule
		for (Rule r : fis.getFunctionBlock("tipper").getFuzzyRuleBlock("No1")
				.getRules()) {
			if (r.getAntecedents().getTerm1().equals(ruleList[0])
					&& r.getAntecedents().getTerm1().equals(ruleList[2])) {
				System.out.println("Conflicting rule added");
				return false;
			}
		}
		// Increase rule updates counter
		Rule rule = new Rule(
				Integer.toString((rule_block.getRules().size()) + 1),
				rule_block);
		RuleTerm term1 = new RuleTerm(function_block.getVariable("temp"),
				ruleList[0], false);
		RuleTerm term2 = new RuleTerm(function_block.getVariable("ambient"),
				ruleList[2], false);
		RuleExpression antecedent;
		if (ruleList[1].equalsIgnoreCase("and")) {
			antecedent = new RuleExpression(term1, term2,
					RuleConnectionMethodAndMin.get());
		} else {
			antecedent = new RuleExpression(term1, term2,
					RuleConnectionMethodOrBoundedSum.get());
		}
		rule.setAntecedents(antecedent);
		rule.addConsequent(function_block.getVariable("blind"), ruleList[3],
				false);
		String formatter = rule.toStringFcl().replace(" ", "");
		for (int i = 0; i < rulesList.size(); i++) {
			if (rulesList.get(i) != null) {
				formatter = "";
				formatter = rulesList.get(i).toStringFcl();
				formatter = formatter.replace('(', ' ').replace(')', ' ')
						.replace(" ", "");
				if (rulesList.get(i).toStringFcl().contains(formatter)) {
					System.out.println("Rule to be added already exists!!");
					return false;
				}
			}
		}
		rule_block.add(rule);
		// Get default function block
		ruleBlockMap = function_block.getRuleBlocks();
		ruleBlockMap.put("No1", rule_block);
		function_block.setRuleBlocks(ruleBlockMap);
		fis.addFunctionBlock("tipper", function_block);
		for (Rule r : fis.getFunctionBlock("tipper").getFuzzyRuleBlock("No1")
				.getRules()) {
			System.out.println(r);
		}
		return true;
	}

	/**
	 * This function deletes the rules that user wishes to remove
	 * 
	 * @param array
	 */
	public synchronized void deleteRule(JSONArray array) {
		String temp = array.toJSONString();
		//System.out.println(temp);
		temp = temp.replace("[", "").replace("]", "").replace("\"", "");
		temp = temp.replace("\\", "");
		String[] deleteRuleList = temp.split(":");
		 //rule_block = function_block.getFuzzyRuleBlock("No1");
		Rule rule = new Rule(Integer.toString(ruleBlockMap.size()), rule_block);
		RuleTerm term1 = new RuleTerm(function_block.getVariable("temp"),
				deleteRuleList[0], false);
		RuleTerm term2 = new RuleTerm(function_block.getVariable("ambient"),
				deleteRuleList[2], false);
		RuleExpression antecedent;
		if (deleteRuleList[1].equalsIgnoreCase("and")) {
			antecedent = new RuleExpression(term1, term2,
					RuleConnectionMethodAndMin.get());
		} else {
			antecedent = new RuleExpression(term1, term2,
					RuleConnectionMethodOrBoundedSum.get());
		}

		rule.setAntecedents(antecedent);
		rule.addConsequent(function_block.getVariable("blind"),
				deleteRuleList[3], false);
		String delRule = rule.toStringFcl();
		delRule = delRule.replace(" ", "");
		rulesList = rule_block.getRules();
		String formatter;
		for (int i = 0; i < rulesList.size(); i++) {
			if (rulesList.get(i) != null) {
				formatter = "";
				formatter = rulesList.get(i).toStringFcl();
				formatter = formatter.replace('(', ' ').replace(')', ' ')
						.replace(" ", "");

				if (formatter.contains(delRule)) {
					rulesList.remove(i);
				}
			}
		}
		// update the FIS object with the new rules list after deletion of a
		// single rule from it.
		rule_block.setRules(rulesList);
		rule_block.reset();
		ruleBlockMap = function_block.getRuleBlocks();
		ruleBlockMap.put("No1", rule_block);

		function_block.setRuleBlocks(ruleBlockMap);
		fis.addFunctionBlock("tipper", function_block);
	}

	/**
	 * This function is used to send the rules to the user when the android
	 * activity is open after destroying
	 * 
	 * @return returns a list of all the rules that is set by the user
	 */
	public List<String> sendRules() {
		List<String> send = new ArrayList<>();
		List<Rule> rules = fis.getFunctionBlock("tipper")
				.getFuzzyRuleBlock("No1").getRules();
		for (Rule r : fis.getFunctionBlock("tipper").getFuzzyRuleBlock("No1")
				.getRules()) {
			String s = r.getAntecedents().getTerm1().toString().split("\\ ")[2]
					+ ":"
					+ r.getAntecedents().getRuleConnectionMethod().toString().replace(" : MIN;", "")
					+ ":"
					+ r.getAntecedents().getTerm2().toString().split("\\ ")[2]
					+ ":"
					+ r.getConsequents().toString().replace("[", "")
							.replace("]", "").split("\\ ")[2];
			send.add(s);
			System.out.println(s);
		}
		return send;
	}

	/**
	 * This evaluates the the rules according to the time and ambient value and
	 * finds the highest degree of support to find out the blind position
	 * 
	 * @return returns a string of the blind position
	 */
	public String blindPos() {

		function_block.evaluate();
		String cons = null;
		double degree = 0;
		// check for the degree
		for (Rule r : fis.getFunctionBlock("tipper").getFuzzyRuleBlock("No1")
				.getRules()) {
			// System.out.println("Inside for");
			if (r.getDegreeOfSupport() > degree) {
				degree = r.getDegreeOfSupport();
				// System.out.println(degree);
				cons = r.getConsequents().toString();
			}
		}
		double pos = function_block.getVariable("blind").defuzzify();
		// System.out.println("Defuzzify: " + pos);
		if (cons == null) {
			return "no rule";
		}
		return cons;
	}
}
