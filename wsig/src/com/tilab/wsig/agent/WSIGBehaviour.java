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

package com.tilab.wsig.agent;

import jade.content.AgentAction;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsTerm;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.tilab.wsig.store.OperationResult;

public class WSIGBehaviour extends AchieveREInitiator {

	private static final long serialVersionUID = 6463142354593931841L;
	
	public static final int UNKNOWN_STATUS  = 0;
	public static final int SUCCESS_STATUS  = 1;
	public static final int FAILURE_STATUS  = 2;
	public static final int APPLICATIVE_FAILURE_STATUS  = 3;

	private int status;
	private OperationResult opResult;
	private String error;
	private SLCodec codec = new SLCodec();
	private Ontology onto;
	private AgentAction agentAction;
	private AID agentExecutor;
	private int timeout = 0;
	private Map<String, String> userDefinedParameters;

	
	public WSIGBehaviour(AID agentExecutor, AgentAction agentAction,  Ontology onto, int timeout, Map<String, String> userDefinedParameters) {
		super(null, null);
		
		this.status = UNKNOWN_STATUS;
		this.onto = onto;
		this.agentAction = agentAction;
		this.timeout = timeout;
		this.agentExecutor = agentExecutor;
		this.userDefinedParameters = userDefinedParameters;
	}

	public void onStart() {
		super.onStart();
		
		myAgent.getContentManager().registerOntology(onto);
		myAgent.getContentManager().registerLanguage(codec);
	}

	protected Vector prepareRequests(ACLMessage msg) {
		Vector v = new Vector(1);
		try {
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.setLanguage(codec.getName());
			request.setOntology(onto.getName());
			request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			request.setReplyByDate(new Date(System.currentTimeMillis() + timeout));
			request.addReceiver(agentExecutor);

			if (userDefinedParameters != null) {
				Set<Entry<String, String>> udpSet = userDefinedParameters.entrySet();
				for (Entry<String, String> udpEntry : udpSet) {
					request.addUserDefinedParameter(udpEntry.getKey(), udpEntry.getValue());
				}
			}
			
			myAgent.getContentManager().fillContent(request, new Action(agentExecutor, agentAction));

			v.addElement(request);
			
		} catch (Exception e) {
			status = FAILURE_STATUS;
			error = e.getMessage();
		}
		return v;
	}
	
	protected void handleInform(ACLMessage message)	{
		try {
			status = SUCCESS_STATUS;

			AbsTerm resultValue;
			AbsContentElement content = myAgent.getContentManager().extractAbsContent(message);
			String resultType = content.getTypeName();
			if (BasicOntology.RESULT.equals(resultType)) {
				resultValue = ((AbsTerm)content.getAbsObject(BasicOntology.RESULT_VALUE));
				
			} else if (BasicOntology.DONE.equals(resultType)) {
				resultValue = null;
				
			} else {
				throw new Exception("Abs content element of type "+content.getTypeName()+" not supported");
			}
			
			opResult = new OperationResult(OperationResult.Result.OK, message, resultValue);
			
		} catch (Exception e) {
			status = FAILURE_STATUS;
			error = "Extracting result error: "+e.getMessage();
		}
	}

	protected void handleFailure(ACLMessage failure)	{
		// Check for AMS response
		if (failure.getSender().equals(myAgent.getAMS())) {
			// Executor agent unreachable
			error = "Agent "+agentExecutor.getLocalName()+" UNREACHABLE";
			status = FAILURE_STATUS;
		} else {
			// Applicative failure
			error = failure.getContent();
			status = APPLICATIVE_FAILURE_STATUS;
			opResult = new OperationResult(OperationResult.Result.KO, failure, null);
		}
	}

	protected void handleRefuse(ACLMessage refuse) {
		error = "Agent "+refuse.getSender().getLocalName()+" REFUSE request";
		status = APPLICATIVE_FAILURE_STATUS;
		opResult = new OperationResult(OperationResult.Result.KO, refuse, null);
	}

	protected void handleNotUnderstood(ACLMessage notUnderstood) {
		error = "Agent "+notUnderstood.getSender().getLocalName()+" NOT_UNDERSTOOD request";
		status = APPLICATIVE_FAILURE_STATUS;
		opResult = new OperationResult(OperationResult.Result.KO, notUnderstood, null);
	}
	
	public String getError() {
		return error;
	}

	public int getStatus() {
		return status;
	}

	public OperationResult getOperationResult() {
		return opResult;
	}
}
