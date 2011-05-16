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

package jade.cli.behaviours;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.ContainerID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.tools.logging.ontology.LogManagementOntology;

/**
 * 
 * Create a remote loggerController:jade.tools.logging.LogHelperAgent
 * 
 * @author Salvatore Soldatini - TILAB
 *
 */

public class CreateRemoteLoggerControllerBehaviour extends OneShotBehaviour {
		
	private static final long serialVersionUID = -4820827332888712692L;
	private String containerName;
		
	public CreateRemoteLoggerControllerBehaviour(String containerName){
		if(containerName == null || containerName.equals("")){
			this.containerName = AgentContainer.MAIN_CONTAINER_NAME;
		}else
			this.containerName = containerName;
	}

	@Override
	public void action() {
		try {
			createController(containerName);			
		}
		catch(Exception fe) {
			fe.printStackTrace();
		}
	}

	private AID createController(String containerName) throws FIPAException {
		ACLMessage request = createAMSRequest();
		
		CreateAgent ca = new CreateAgent();
		//String localName = myAgent.getLocalName()+"-helper-on-"+containerName;
		String localName = "loggerController";
		ca.setAgentName(localName);
		ca.setClassName("jade.tools.logging.LogHelperAgent");
		ca.addArguments(myAgent.getAID());
		ca.setContainer(new ContainerID(containerName, null));
		
		Action act = new Action();
		act.setActor(myAgent.getAMS());
		act.setAction(ca);
		
		try {
			myAgent.getContentManager().registerOntology(JADEManagementOntology.getInstance());
			myAgent.getContentManager().registerOntology(LogManagementOntology.getInstance());
			myAgent.getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL);
			myAgent.getContentManager().fillContent(request, act);

			ACLMessage inform = FIPAService.doFipaRequestClient(myAgent, request, 10000);
			if (inform != null) {
				return new AID(localName, AID.ISLOCALNAME);
			}
			else {
				throw new FIPAException("Response timeout expired");
			}
		}
		catch (FIPAException fe) {
			throw fe;
		}
		catch (Exception e) {
			// Should never happen
			e.printStackTrace();
		}
		return null;
	}
		
	private ACLMessage createAMSRequest() {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(myAgent.getAMS());
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		request.setOntology(JADEManagementOntology.getInstance().getName());
		return request;
	}
}
