/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

import it.pisa.jade.data.activePlatform.RecordPlatform;

/**
 * @author Domenico Trimboli
 * 
 */
public class RemoveAction extends ActionGui {
	private RecordPlatform record;

	public RemoveAction(RecordPlatform rec) {
		super(true);
		record = rec;
	}

	public RecordPlatform getRecord() {
		return record;
	}
}
