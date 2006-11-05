/*
 * Created on 8-ott-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.pisa.jade.agents.guiAgent.action;

import jade.lang.acl.ACLMessage;

/**
 * This action is used for the generic error
 * @author Domenico Trimboli
 *
 */
public class ErrorAction extends ActionGui {
	private String message;
	private ACLMessage aclMessage;

	/**
	 * @param string
	 * @param msg
	 */
	public ErrorAction(String string, ACLMessage msg) {
		super(true);
		message=string;
		aclMessage=msg;
	}

	/**
	 * @return Returns the aclMessage.
	 */
	public ACLMessage getAclMessage() {
		return aclMessage;
	}
	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}
}
