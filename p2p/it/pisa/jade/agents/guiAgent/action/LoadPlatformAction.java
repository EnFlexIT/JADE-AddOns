/**
 * 
 */
package it.pisa.jade.agents.guiAgent.action;

import it.pisa.jade.data.activePlatform.ActivePlatform;

/**
 * @author Domenico Trimboli
 *
 */
public class LoadPlatformAction extends ActionGui {

	private ActivePlatform activePlatform;

	public LoadPlatformAction(ActivePlatform ap) {
		super(true);
		activePlatform=ap;
	}

	public ActivePlatform getActivePlatform() {
		return activePlatform;
	}

}
