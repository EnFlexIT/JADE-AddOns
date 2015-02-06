package com.tilab.wsig.agent;

import jade.content.Predicate;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tilab.wsig.store.OperationResult;

public class WSIGPredicateBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = -2646187183850828508L;

	public static final int UNKNOWN_STATUS  = 0;
	public static final int SUCCESS_STATUS  = 1;
	public static final int FAILURE_STATUS  = 2;

	private OperationResult opResult;
	private int status;
	private String error;
	private SLCodec codec = new SLCodec();
	private Ontology onto;
	private Predicate predicate;
	private AID agentExecutor;
	private Map<String, String> userDefinedParameters;
	
	public WSIGPredicateBehaviour(AID agentExecutor, Predicate predicate,  Ontology onto, Map<String, String> userDefinedParameters) {
		super();
		
		this.status = UNKNOWN_STATUS;
		this.onto = onto;
		this.predicate = predicate;
		this.agentExecutor = agentExecutor;
		this.userDefinedParameters = userDefinedParameters;
	}
	
	public void onStart() {
		super.onStart();
		
		myAgent.getContentManager().registerOntology(onto);
		myAgent.getContentManager().registerLanguage(codec);
	}

	public void action() {
		try {
			ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
			inform.setLanguage(codec.getName());
			inform.setOntology(onto.getName());
			inform.addReceiver(agentExecutor);
			inform.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");

			if (userDefinedParameters != null) {
				Set<Entry<String, String>> udpSet = userDefinedParameters.entrySet();
				for (Entry<String, String> udpEntry : udpSet) {
					inform.addUserDefinedParameter(udpEntry.getKey(), udpEntry.getValue());
				}
			}
			
			myAgent.getContentManager().fillContent(inform, predicate);

			myAgent.send(inform);
			
			status = SUCCESS_STATUS;
			opResult = new OperationResult(OperationResult.Result.OK, null, null);
		} 
		catch (Exception e) {
			status = FAILURE_STATUS;
			error = e.getMessage();
		}
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
