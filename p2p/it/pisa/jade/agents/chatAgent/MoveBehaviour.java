/**
 * 
 */
package it.pisa.jade.agents.chatAgent;

import it.pisa.jade.util.WrapperErrori;
import jade.core.AID;
import jade.core.Agent;
import jade.core.PlatformID;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;

/**
 * @author Domenico Trimboli
 *
 */
@SuppressWarnings("serial")
public class MoveBehaviour extends WakerBehaviour {


	private String platformName;
	private String platformAddress;

	public MoveBehaviour(Agent myAgent, AID peerAID) {
		super(myAgent,1000);
		platformName=peerAID.getName().substring(peerAID.getName().indexOf("@")+1);
		platformAddress=(String)peerAID.getAllAddresses().next();
	}
	@Override
	protected void onWake() {
		AID remoteAMS = new AID("ams@"+platformName, AID.ISGUID);
		remoteAMS.addAddresses(platformAddress);

		PlatformID destination = new PlatformID(remoteAMS);
		try {
			DFService.deregister(myAgent);
		} catch (FIPAException e) {
			WrapperErrori.wrap("",e);
		}

//		 We are supposing that we exectue this code from a behaviour

		myAgent.doMove(destination);
	}

}
