/**
 * 
 */
package it.pisa.jade.agents.multicastAgent.ontologies;

/**
 * @author Domenico Trimboli
 * 
 */
public enum ConstantTypeMsg {
	PING("pingMessage"), PONG("pongMessage"), EXIT("exit");

	ConstantTypeMsg(String s) {
		this.value = s;
	}

	private final String value;

	public String value() {
		return value;
	}

	public String toString() {
		return value();
	}
}
