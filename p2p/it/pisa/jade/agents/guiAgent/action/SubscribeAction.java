/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

/**
 * @author Domenico Trimboli
 *
 */
public class SubscribeAction extends ActionGui {
	private String message;
	
	public SubscribeAction(String message,boolean result) {
		super(result);
		this.message=message;
	}

	public String getMessage() {
		return message;
	}


}
