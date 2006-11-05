/**
 * 
 */
package it.pisa.jade.agents.multicastAgent.behaviours;


import it.pisa.jade.data.activePlatform.ActivePlatformStub;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * This class provide to uniform the multicast agent's behaviour, to improve the
 * active platform list.
 * 
 * @author Domenico Trimboli
 * 
 */
public abstract class MulticastBehaviour extends Behaviour {

	protected ActivePlatformStub activePlatform;

	public MulticastBehaviour(Agent a, ActivePlatformStub activePlatform) {
		super(a);
		this.activePlatform = activePlatform;
	}

	public abstract void setTerminate(boolean flag);

}
