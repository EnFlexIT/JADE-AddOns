/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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

package jade.cli;

import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.ContainerMonitorAgent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAService;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

import java.util.Properties;


public class DumpAgent extends CLICommand {
	
	@Option(value="<container name>", description="The container where the target agent lives (optional)")
	public static final String CONTAINER_OPTION = "container";
	@Option(value="<agent local name>", description="The agent that must be dumped")
	public static final String AGENT_OPTION = "agent";

	private String agentName = null;
	private String containerName = null;
	
	public static void main(String[] args) {
		CLIManager.execute(new DumpAgent(), args);
	}

	@Override
	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		agentName = CLIManager.getMandatoryOption(AGENT_OPTION, pp);
		containerName = pp.getProperty(CONTAINER_OPTION);
		return new SimpleAchieveREInitiator(null, null) {
			@Override
			protected ACLMessage prepareRequest(ACLMessage unused) {
				ACLMessage req = null;
				try {
					if (containerName == null) {
						containerName = retrieveContainerName(agentName, myAgent); 
					}
					req = new ACLMessage(ACLMessage.REQUEST);
					req.setSender(myAgent.getAID());
					req.addReceiver(new AID("monitor-"+containerName, AID.ISLOCALNAME));
					req.setOntology(ContainerMonitorAgent.CONTAINER_MONITOR_ONTOLOGY);
					req.setContent("DUMP-AGENT "+agentName);
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
				return req;
			}

			@Override
			protected void handleInform(ACLMessage inform) {
				System.out.println(inform.getContent());
			}

			@Override
			protected void handleFailure(ACLMessage failure) {
				System.out.println("ERROR: "+failure.getContent());
			}
		};
	}
	
	private String retrieveContainerName(String agentName, Agent a) throws Exception {
		WhereIsAgentAction wia = new WhereIsAgentAction();
		wia.setAgentIdentifier(new AID(agentName, AID.ISLOCALNAME));
		ACLMessage request = CLIManager.createAMSRequest(a, wia);
		ACLMessage response = FIPAService.doFipaRequestClient(a, request, 10000);
		if (response != null) {
			Result result = (Result) a.getContentManager().extractContent(response);
			ContainerID cid = (ContainerID) result.getValue();
			return cid.getName();
		}
		else {
			throw new Exception("Timeout expired while retrieving container for agent "+agentName+" from the AMS");
		}
	}

}
