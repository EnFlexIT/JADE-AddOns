/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

import it.pisa.jade.data.activePlatform.RecordPlatform;

/**
 * @author Domenico Trimboli
 *
 */
public class DefederationAction extends ActionGui {
	private String message;
	private RecordPlatform record;

	public DefederationAction(String msg, boolean b) {
		super(b);
		this.message=msg;
	}

	public DefederationAction(String string, boolean b, RecordPlatform record) {
		this(string,b);
		this.record=record;
	}


	public String getMessage() {
		return message;
	}

	public RecordPlatform getRecord() {
		return record;
	}

}
