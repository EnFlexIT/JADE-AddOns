/*
 * Created on 8-ott-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.pisa.jade.agents.guiAgent.action;

import jade.core.AID;

/**
 * @author Domenico Trimboli
 *
 */
public class KillAction extends ActionGui {
	private AID agentKilled;

	/**
	 * @param flag
	 */
	public KillAction(boolean flag,AID agentKilled) {
		super(flag);
		this.agentKilled=agentKilled;		
	}

	/**
	 * @return
	 */
	public AID getAgentKilled() {
		return agentKilled;
	}

}
