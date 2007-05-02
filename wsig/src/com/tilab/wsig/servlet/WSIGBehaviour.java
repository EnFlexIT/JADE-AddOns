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

package com.tilab.wsig.servlet;

import jade.content.AgentAction;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

public class WSIGBehaviour extends AchieveREInitiator {

	private static Logger log = Logger.getLogger(WSIGBehaviour.class.getName());
	
	public static final int UNKNOWN_STATUS  = 0;
	public static final int EXECUTED_STATUS = 1;
	public static final int FAILURE_STATUS  = 2;
	public static final int RUNNING_STATUS  = 3;
	public static final String TIMEOUT  = "TIMEOUT";
	
	private int status = UNKNOWN_STATUS;
	private Object result = null;
	private String error = null;
	private Exception exception = null;

	private SLCodec codec = new SLCodec();
	private Ontology onto = null;
	private AgentAction agentAction = null;
	private AID agentReceiver = null;
	private int timeout = 0;

	
	public WSIGBehaviour(AID agentReceiver, AgentAction agentAction,  Ontology onto, int timeout) {
		super(null, null);
		
		this.onto = onto;
		this.agentAction = agentAction;
		this.timeout = timeout;
		this.agentReceiver = agentReceiver;
	}

	public void onStart() {
		super.onStart();
		
		log.debug("WSIGBehaviour.onStart start");
		
		myAgent.getContentManager().registerOntology(onto);
		myAgent.getContentManager().registerLanguage(codec);
		myAgent.getContentManager().setValidationMode(false);
	}

	protected ACLMessage prepareRequest(ACLMessage request) {
		
		log.debug("WSIGBehaviour.prepareRequest");
		
		request = new ACLMessage(ACLMessage.REQUEST);
		request.setLanguage(codec.getName());
		request.setOntology(onto.getName());
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setReplyByDate(new Date(System.currentTimeMillis() + timeout));
		try {
			request.addReceiver(agentReceiver);
			myAgent.getContentManager().fillContent(request, new Action(myAgent.getAID(), agentAction));
			
		} catch (Exception e) {
			status = FAILURE_STATUS;
			exception = e;
			request = null;
			log.error(e);
		}
		return request;
	}
	
	protected Vector prepareRequests(ACLMessage request) {
		Vector v = new Vector(1);
		ACLMessage actualRequest = prepareRequest(request);
		if (actualRequest != null) {
			v.addElement(actualRequest);
		}
		return v;
	}
	
	protected void handleInform(ACLMessage message)	{
		
		log.debug("WSIGBehaviour.handleInform");
		
		status = EXECUTED_STATUS;
		try {
			result = ((Result)myAgent.getContentManager().extractContent(message)).getValue();
		} catch (Exception e) {
			status = FAILURE_STATUS;
			exception = e;
		}
	}

	public void handleError(ACLMessage msg)	{
		
		log.debug("WSIGBehaviour.handleError");
		
		status = FAILURE_STATUS;
		error = msg.getContent();
	}

	public void handleTimeout()	{
		
		log.debug("WSIGBehaviour.handleTimeout");
		
		status = FAILURE_STATUS;
		error = TIMEOUT;
	}

	public String getError() {
		return error;
	}

	public int getStatus() {
		return status;
	}

	public Object getResult() {
		return result;
	}

	public Exception getException() {
		return exception;
	}
}
