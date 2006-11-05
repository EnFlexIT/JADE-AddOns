/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

/**
 * @author Domenico Trimboli
 *
 */
public class DesubscribeAction extends ActionGui {
	private String message;
	
	public DesubscribeAction(String message,boolean result) {
		super(result);
		this.message=message;
	}

	public String getMessage() {
		return message;
	}

}
