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
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.tools.logging.ontology.LogManagementOntology;
import jade.tools.logging.ontology.SetLevel;

/**
 * Set the logger log level
 * 
 * Search for a remote logger controller and then send a
 * jade.tools.logging.ontology.setLevel action
 * 
 * @author Salvatore Soldatini - TILAB
 *
 */

public class SetLogLevelBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -4922271299290884248L;

	private AID helperController = null;
	private String loggerName;
	private int logLevel;
	
	public SetLogLevelBehaviour(String loggerName, int logLevel){
		this.loggerName = loggerName;
		this.logLevel   = logLevel;
	}
	
	@Override
	public void action() {
		try {
			
			AMSAgentDescription template = new AMSAgentDescription();
			template.setName(new AID("loggerController", AID.ISLOCALNAME));

			SearchConstraints sc = new SearchConstraints();
			sc.setMaxResults(new Long(1));
			AMSAgentDescription[] result = AMSService.search(myAgent, template, sc);
			if(result.length==0){
				throw new Exception("Remote logger controller not found");	
			}

			helperController = ((AMSAgentDescription)result[0]).getName();

			remoteSetLogLevel(helperController, loggerName, logLevel);
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void remoteSetLogLevel(AID helper, String name, int level) throws FIPAException {
		ACLMessage request = createHelperRequest(helper);
		
		SetLevel sl = new SetLevel(name, level);
		
		Action act = new Action();
		act.setActor(helper);
		act.setAction(sl);
		
		try {
			myAgent.getContentManager().registerLanguage(new SLCodec());
			myAgent.getContentManager().registerOntology(LogManagementOntology.getInstance());
			myAgent.getContentManager().fillContent(request, act);
			
			ACLMessage inform = FIPAService.doFipaRequestClient(myAgent, request, 10000);
			if (inform == null) {
				throw new FIPAException("Response timeout expired");
			}
		}
		catch (FIPAException fe) {
			throw fe;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new FIPAException(e.getMessage());
		}
	}
	
	private ACLMessage createHelperRequest(AID helper) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(helper);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		request.setOntology(LogManagementOntology.getInstance().getName());
		return request;
	}

}
