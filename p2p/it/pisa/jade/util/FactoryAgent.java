/**
 * 
 */
package it.pisa.jade.util;

import it.pisa.jade.agents.PingAgent;
import it.pisa.jade.agents.SearchServiceAgent;
import it.pisa.jade.agents.SubDFRemote;
import it.pisa.jade.agents.chatAgent.ChatAgent;
import it.pisa.jade.agents.federatorAgent.FederatorAgent;
import it.pisa.jade.agents.guiAgent.GuiAgent;
import it.pisa.jade.agents.multicastAgent.MulticastManagerAgent;
import it.pisa.jade.agents.peerAgent.PeerAgent;
import jade.tools.rma.rma;

/**
 * It realize the Factory pattern to return the class name of 
 * the agent 
 * 
 * @author Domenico Trimboli
 * 
 */
public class FactoryAgent {

	public static final String JAVASERIALIZATION_LANGUAGE = "JavaSerialization";

	public static final String STRING_LANGUAGE = "String";

	public static Class getAgent(AgentName name) {
		if (name.equals(AgentName.federatorAgent)) {
			return FederatorAgent.class;
		}
		if (name.equals(AgentName.ping)) {
			return PingAgent.class;
		}
		if (name.equals(AgentName.multicastManagerAgent)) {
			return MulticastManagerAgent.class;
		}
		if (name.equals(AgentName.chatAgent)) {
			return ChatAgent.class;
		}
		if (name.equals(AgentName.subDf)) {
			return SubDFRemote.class;
		}
		if (name.equals(AgentName.searchServiceAgent)) {
			return SearchServiceAgent.class;
		}
		if (name.equals(AgentName.rma)) {
			return rma.class;
		}
		if (name.equals(AgentName.guiAgent)) {
			return GuiAgent.class;
		}
		if(name.equals(AgentName.peerAgent))
			return PeerAgent.class;
		return null;
	}
}
