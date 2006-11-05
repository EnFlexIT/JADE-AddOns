/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

/**
 * @author Domenico Trimboli
 *
 */
public abstract  class ActionGui {
	private boolean ok;
	public ActionGui(boolean flag){
		ok=flag;
	}
	public boolean isOK() {
		return ok;
	}

}
