/**
 * 
 */
package it.pisa.jade.agents.multicastAgent;

/**
 * @author Domenico Trimboli
 * 
 */
public enum ConstantMulticastAgent {
	
	SUBSCRIBE("subscribe"), DESUBSCRIBE("desubscribe");
	
	private ConstantMulticastAgent(String v) {
		value = v;
	}

	String value;

	public String value() {
		return value;
	}
}
