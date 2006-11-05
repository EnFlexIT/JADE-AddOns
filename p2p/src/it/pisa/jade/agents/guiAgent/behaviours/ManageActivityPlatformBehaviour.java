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
package it.pisa.jade.agents.guiAgent.behaviours;

import it.pisa.jade.agents.guiAgent.GuiAgent;
import it.pisa.jade.agents.guiAgent.action.ActionGui;
import it.pisa.jade.agents.guiAgent.action.AddAction;
import it.pisa.jade.agents.guiAgent.action.DesubscribeAction;
import it.pisa.jade.agents.guiAgent.action.ErrorAction;
import it.pisa.jade.agents.guiAgent.action.FederationAction;
import it.pisa.jade.agents.guiAgent.action.KillAction;
import it.pisa.jade.agents.guiAgent.action.LoadPlatformAction;
import it.pisa.jade.agents.guiAgent.action.RemoveAction;
import it.pisa.jade.agents.guiAgent.action.SubscribeAction;
import it.pisa.jade.agents.multicastAgent.ConstantMulticastAgent;
import it.pisa.jade.data.activePlatform.ActivePlatform;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.ConstantForACL;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.WrapperErrori;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.DFGUIManagement.DFAppletOntology;
import jade.domain.DFGUIManagement.Federate;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.SimpleAchieveREInitiator;
import jade.tools.sl.SLFormatter;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial") public class ManageActivityPlatformBehaviour extends
		Behaviour {

	private final int DEFEDERATE = 5;

	private final int DESUBSCRIBE = 3;

	private final int FEDERATE = 4;

	private final int LOAD_ACTIVITY_PLATFORM = 1;

	private MessageTemplate mt = MessageTemplate
			.MatchConversationId(ConstantForACL.MANAGE_FEDERATION.value());

	private MessageTemplate mt1;

	private Object parameter;

	private int state = 1;

	private final int SUBSCRIBE = 2;

	private final int KILL = 6;

	public ManageActivityPlatformBehaviour(Agent a,
			ConstantBehaviourAction state, Object parameter) {
		super(a);
		this.parameter = parameter;
		this.state = state.value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override public void action() {

		switch (state) {
		case LOAD_ACTIVITY_PLATFORM: {// ConstantBehaviourAction.LOAD_ACTIVITY_PLATFORM
			// send a message to request the active platform
			loadActivePlatform();
			break;
		}
		case SUBSCRIBE: {// ConstantBehaviourAction.SUBSCRIBE
			subscribe();
			state = 21;// go to wait
			break;
		}
		case DESUBSCRIBE: {// ConstantBehaviourAction.DESUBSCRIBE
			desubscribe();
			state = 31;// go to wait
			break;
		}
		case FEDERATE: {// add new federate
			addFederate();
			state = -1;
			break;
		}
		case DEFEDERATE: {// remove federate
			removeFederate();
			state = -1;
			break;
		}
		case KILL: {
			killAgent();
			// state=KILL*10+1;
			state = -1;
			break;
		}
		case 21: {// wait for SUBSCRIBE's action state
			ACLMessage reply = myAgent.receive(mt1);
			if (reply != null) {
				SubscribeAction action = null;
				if (reply.getPerformative() == ACLMessage.CONFIRM) {
					action = new SubscribeAction("Subscribe ok", true);
					state = 22;// listen active platform's list
				} else {
					action = new SubscribeAction("Subscribe failure", false);
					state = -1;
				}
				((GuiAgent) myAgent).notifyObservers(action);
			} else {
				block();
			}
			break;
		}
		case 22: {
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				if (msg.getPerformative() == ACLMessage.INFORM
						&& msg.getLanguage().equals(
								FactoryUtil.JAVASERIALIZATION_LANGUAGE)) {
					RecordPlatform r = extractRecord(msg);
					RemoveAction action = new RemoveAction(r);
					((GuiAgent) myAgent).notifyObservers(action);
				} else if (msg.getPerformative() == ACLMessage.REQUEST
						&& msg.getLanguage().equals(
								FactoryUtil.JAVASERIALIZATION_LANGUAGE)) {
					RecordPlatform r = extractRecord(msg);
					AddAction action = new AddAction(r);
					((GuiAgent) myAgent).notifyObservers(action);
				} else {
					// reject message
				}
			} else
				block();
			break;
		}
		case 31: {// wait for DESUBSCRIBE's action state
			ACLMessage reply = myAgent.receive(mt1);
			if (reply != null) {
				DesubscribeAction action = null;
				if (reply.getPerformative() == ACLMessage.CONFIRM) {
					action = new DesubscribeAction("Desubscribe ok", true);
					((GuiAgent) myAgent).stopAction();
				} else {
					action = new DesubscribeAction("Desubscribe failure", false);
				}
				((GuiAgent) myAgent).notifyObservers(action);
				state = -1;
			} else {
				block();
			}
			break;
		}
		}
	}

	/**
	 * 
	 */
	private void killAgent() {
		KillAgent ka = new KillAgent();
		final AID agentToKill;
		if (parameter != null && parameter instanceof AID)
			ka.setAgent(agentToKill=(AID) parameter);
		else
			ka.setAgent(agentToKill=new AID(AgentName.multicastManagerAgent.name(),
					AID.ISLOCALNAME));
		Action a = new Action();
		a.setActor(myAgent.getAMS());
		a.setAction(ka);

		ACLMessage requestMsg = ((GuiAgent) myAgent).getAMSRequest();
		requestMsg.setOntology(JADEManagementOntology.NAME);
		try {
			myAgent.getContentManager().fillContent(requestMsg, a);
			myAgent.addBehaviour(new SimpleAchieveREInitiator(myAgent,
					requestMsg) {
				protected void handleInform(ACLMessage msg) {
					ActionGui a = new KillAction(true,agentToKill);
					((GuiAgent) myAgent).notifyObservers(a);
				}
				protected void handleAgree(ACLMessage msg){
					ActionGui a = new KillAction(true,agentToKill);
					((GuiAgent) myAgent).notifyObservers(a);
				}
				protected void handleFailure(ACLMessage msg) {
					ActionGui a = new ErrorAction("Kill action failure", msg);
					((GuiAgent) myAgent).notifyObservers(a);
				}

				protected void handleNotUnderstood(ACLMessage reply) {
					ActionGui a = new ErrorAction("Kill action notUnderstood",
							reply);
					((GuiAgent) myAgent).notifyObservers(a);
				}

				protected void handleRefuse(ACLMessage reply) {
					ActionGui a = new ErrorAction("Kill action refused", reply);
					((GuiAgent) myAgent).notifyObservers(a);
				}

			});
		} catch (CodecException e) {
			WrapperErrori.wrap("kill request fail", e);
		} catch (OntologyException e) {
			WrapperErrori.wrap("kill request fail", e);
		}
		/*
		 * ACLMessage msg=new ACLMessage(ACLMessage.REQUEST);
		 * msg.setConversationId(ConstantForACL.MANAGE_AGENT.value());
		 * msg.setContent(ManageAgentBehaviour.KILL); AID aid=new
		 * AID(AgentName.multicastManagerAgent.name(),AID.ISLOCALNAME);
		 * msg.addReceiver(aid); myAgent.send(msg);
		 * mt1=MessageTemplate.MatchConversationId(ConstantForACL.MANAGE_AGENT.value());
		 */
	}

	/**
	 * 
	 */
	private void addFederate() {
		if (parameter instanceof RecordPlatform) {
			final RecordPlatform record = (RecordPlatform) parameter;
			final AID childDF = myAgent.getDefaultDF();
			String sChildDF = "df@" + record.getPlatformName() + ", "
					+ record.getPlatformAddress();
			final AID parentDF = FactoryUtil.createAID(sChildDF);
			Federate f = new Federate();
			f.setDf(parentDF);
			Action action = new Action(childDF, f);
			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.addReceiver(childDF);
			request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
			request.setOntology(DFAppletOntology.NAME);
			try {
				myAgent.getContentManager().fillContent(request, action);
				myAgent.addBehaviour(new AchieveREInitiator(myAgent, request) {
					protected void handleFailure(ACLMessage failure) {
						String msg = new String("Federation between \n"
								+ childDF.getName() + " and \n"
								+ parentDF.getName() + " failed \n["
								+ SLFormatter.format(failure.getContent())
								+ "]");
						ActionGui actionGui = new FederationAction(msg, false,
								record);
						((GuiAgent) myAgent).notifyObservers(actionGui);
					}

					protected void handleInform(ACLMessage inform) {
						String msg = "Federation between \n"
								+ childDF.getName() + " and \n"
								+ parentDF.getName() + " confirmed \n["
								+ SLFormatter.format(inform.getContent()) + "]";
						ActionGui actionGui = new FederationAction(msg, true,
								record);
						((GuiAgent) myAgent).notifyObservers(actionGui);
					}

				});
			} catch (Exception e) {
				String msg = new String("Federation between \n"
						+ childDF.getName() + " and \n" + parentDF.getName()
						+ " failed \n[" + e.getMessage() + "]");
				WrapperErrori.wrap(msg, e);
				ActionGui actionGui = new FederationAction(msg, false, record);
				((GuiAgent) myAgent).notifyObservers(actionGui);
			}
		} else {
			ActionGui actionGui = new FederationAction("Parameter error", false);
			((GuiAgent) myAgent).notifyObservers(actionGui);
		}
	}

	/**
	 * 
	 */
	private void desubscribe() {
		ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
		message.setContent(ConstantMulticastAgent.DESUBSCRIBE.value());
		message.setConversationId(ConstantForACL.MANAGE_PLATFORM.value());
		message.addReceiver(new AID(AgentName.multicastManagerAgent.name(),
				false));
		String template = "desubscribe" + System.currentTimeMillis();
		message.setReplyWith(template);
		mt1 = MessageTemplate.MatchInReplyTo(template);
		myAgent.send(message);
	}

	@Override public boolean done() {
		if (state == -1)
			return true;
		else
			return false;
	}

	private RecordPlatform extractRecord(ACLMessage msg) {
		RecordPlatform rp = null;
		try {
			rp = (RecordPlatform) msg.getContentObject();
		} catch (UnreadableException e) {
			WrapperErrori.wrap("", e);
		}

		return rp;
	}

	/**
	 * 
	 */
	private void loadActivePlatform() {
		ACLMessage message = new ACLMessage(ACLMessage.CFP);
		message.setConversationId(ConstantForACL.MANAGE_PLATFORM.value());
		message.addReceiver(new AID(AgentName.multicastManagerAgent.name(),
				false));
		myAgent.send(message);
		MessageTemplate mt = MessageTemplate.and(MessageTemplate
				.MatchConversationId(ConstantForACL.MANAGE_PLATFORM.value()),
				MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage reply = myAgent.blockingReceive(mt);
		if (reply != null) {
			if (reply.getLanguage().equals(
					FactoryUtil.JAVASERIALIZATION_LANGUAGE)) {
				try {
					ActivePlatform ap = (ActivePlatform) reply
							.getContentObject();
					LoadPlatformAction action = new LoadPlatformAction(ap);
					((GuiAgent) myAgent).notifyObservers(action);
				} catch (UnreadableException e) {
					WrapperErrori
							.wrap(
									"Impossibile caricare le piattaforme attive arrivate via message",
									e);
				}
			}

		}// if
		state = -1;
	}

	/**
	 * 
	 */
	private void removeFederate() {
		if (parameter instanceof RecordPlatform) {
			RecordPlatform record = (RecordPlatform) parameter;
			String sChildDF = "df@" + record.getPlatformName() + ", "
					+ record.getPlatformAddress();
			final AID childDF = FactoryUtil.createAID(sChildDF);
			try {
				DFAgentDescription d = new DFAgentDescription();
				d.setName(childDF);
				DFService.deregister(myAgent, d);
				ActionGui actionGui = new FederationAction("Defederation"
						+ childDF.getName() + " done.", true, record);
				((GuiAgent) myAgent).notifyObservers(actionGui);
			} catch (FIPAException e) {
				WrapperErrori.wrap("", e);
				ActionGui actionGui = new FederationAction("Defederation"
						+ childDF.getName() + " failure.", false, record);
				((GuiAgent) myAgent).notifyObservers(actionGui);
			}
		} else {
			ActionGui actionGui = new FederationAction("Parameter error", false);
			((GuiAgent) myAgent).notifyObservers(actionGui);
		}
	}

	/**
	 * 
	 */
	private void subscribe() {
		ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
		message.setContent(ConstantMulticastAgent.SUBSCRIBE.value());
		message.setConversationId(ConstantForACL.MANAGE_PLATFORM.value());
		message.addReceiver(new AID(AgentName.multicastManagerAgent.name(),
				false));
		String template = "subscribe" + System.currentTimeMillis();
		message.setReplyWith(template);
		mt1 = MessageTemplate.MatchInReplyTo(template);
		myAgent.send(message);
	}

}
