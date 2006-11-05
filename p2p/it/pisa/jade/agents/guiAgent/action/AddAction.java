/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

import it.pisa.jade.data.activePlatform.RecordPlatform;

/**
 * @author Domenico Trimboli
 *
 */
public class AddAction extends ActionGui {
	private RecordPlatform record;

	public AddAction(RecordPlatform rec) {
		super(true);
		record = rec;
	}

	public RecordPlatform getRecord() {
		return record;
	}
}
