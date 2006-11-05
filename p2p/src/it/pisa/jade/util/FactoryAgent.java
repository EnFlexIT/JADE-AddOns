/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
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
