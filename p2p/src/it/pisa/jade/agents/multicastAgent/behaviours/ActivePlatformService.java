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
package it.pisa.jade.agents.multicastAgent.behaviours;

import it.pisa.jade.agents.multicastAgent.ConstantMulticastAgent;
import it.pisa.jade.agents.multicastAgent.ontologies.ConstantTypeMsg;
import it.pisa.jade.agents.multicastAgent.ontologies.MulticastMessage;
import it.pisa.jade.data.activePlatform.ActivePlatformStub;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.AgentName;
import it.pisa.jade.util.ConstantForACL;
import it.pisa.jade.util.FactoryUtil;
import it.pisa.jade.util.JadeObserver;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

/**
 * This class receive the Agent's message and manage it in based its type. The
 * message are accepted if the
 * <code>conversationId=ConstantForACL.MANAGE_PLATFORM.value()</code> all
 * other message are reject.
 * 
 * managed performative are:
 * <ul>
 * <li>CFP: the agent answered with INFORM and the list of active platform as
 * object</li>
 * <li>PROPOSAL:force the refresh of activity platform, and notify if same
 * platform came unresearchable. </li>
 * <li>REQUEST:add a platform to the activity platform's list, the message must
 * contains a <code>RecordPlatform</code> object</li>
 * <li>REQUEST_WHEN:remove a platform to the activity platform's list, the
 * message must contains a <code>RecordPlatform</code> object</li>
 * <li>SUBSCRIBE:if the message's content is<code>ConstantMulticastAgent.SUBSCRIBE</code>
 * the sender agent recive all event, else if the message's content is<code>ConstantMulticastAgent.DESUBSCRIBE</code>
 * remove notify's service</li>
 * <li>other:the agent answered with NOT_UNDERSTOOD</li>
 * </ul>
 * 
 * For each performative it answered with <code>CONFIRM</code> or
 * <code>REFUSE</code>.
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
public class ActivePlatformService extends MulticastBehaviour implements
		JadeObserver {

	private Vector<AID> registeredFederator = new Vector<AID>();

	private boolean terminate = false;

	private MessageTemplate mt = MessageTemplate
			.MatchConversationId(ConstantForACL.MANAGE_PLATFORM.value());

	public ActivePlatformService(Agent agent, ActivePlatformStub activePlatform) {
		super(agent, activePlatform);
	}

	@Override
	public void action() {
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			ACLMessage reply = msg.createReply();
			if (msg.getPerformative() == ACLMessage.CFP) {
				sendActivePlatform(reply);

			} else if (msg.getPerformative() == ACLMessage.PROPOSE) {
				try {
					forceRefresh();
					reply.setPerformative(ACLMessage.CONFIRM);
				} catch (Exception e) {
					WrapperErrori.wrap("", e);
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent(e.getMessage());
				}

			} else if (msg.getPerformative() == ACLMessage.REQUEST) {
				addPlatform(msg, reply);
			} else if (msg.getPerformative() == ACLMessage.REQUEST_WHEN) {
				RecordPlatform record = extractRecord(msg);
				if (record != null) {
					activePlatform.removePlatform(record);
					reply.setPerformative(ACLMessage.CONFIRM);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
				}
			} else if (msg.getPerformative() == ACLMessage.SUBSCRIBE) {
				if (msg.getContent().equalsIgnoreCase(
						ConstantMulticastAgent.SUBSCRIBE.value())) {
					if (!registeredFederator.contains(msg.getSender())) {
						registeredFederator.add(msg.getSender());
					}
					reply.setPerformative(ACLMessage.CONFIRM);
				} else if (msg.getContent().equalsIgnoreCase(
						ConstantMulticastAgent.DESUBSCRIBE.value())) {
					if (!registeredFederator.contains(msg.getSender())) {
						registeredFederator.remove(msg.getSender());
					}
					reply.setPerformative(ACLMessage.CONFIRM);
				} else {
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("malformed request of subscribe");
				}
			} else {
				// risponde solo in caso di richieste in tutti gli altri
				// casi restituisce NOT_UNDERSTOOD
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			myAgent.send(reply);
		} else {
			block();
		}
	}

	private void addPlatform(ACLMessage msg, ACLMessage reply) {
		RecordPlatform record = null;
		try {
			record = extractRecord(msg);
			if (record != null
					&& !activePlatform.refreshRecord(record, Parameter
							.getInt(Values.ttlPlatformRecord))) {
				record.setTTL(Parameter.getInt(Values.ttlPlatformRecord));
				activePlatform.addPlatform(record);
				reply.setPerformative(ACLMessage.CONFIRM);
			} else {
				reply.setPerformative(ACLMessage.REFUSE);
			}
		} catch (Exception e) {
			WrapperErrori.wrap("", e);
			reply.setPerformative(ACLMessage.REFUSE);
			reply.setContent(e.getMessage());
		}
	}

	private RecordPlatform extractRecord(ACLMessage msg) {
		RecordPlatform record = null;
		if (FactoryUtil.JAVASERIALIZATION_LANGUAGE.equals(msg
				.getLanguage())) {
			try {
				Serializable o = msg.getContentObject();
				record = (RecordPlatform) o;
			} catch (UnreadableException e) {
				WrapperErrori.wrap("Errore nella ricezione della record", e);
			}
		} else if (FactoryUtil.STRING_LANGUAGE.equals(msg
				.getLanguage())) {
			record = parse(msg.getContent());
		} else
			return null;
		return record;
	}

	private RecordPlatform parse(String content) {
		return new RecordPlatform(content);
	}

	private void forceRefresh() {
		synchronized (activePlatform) {
			for (Iterator<RecordPlatform> it = activePlatform.getIterator(); it
					.hasNext();) {
				RecordPlatform r = it.next();
				if (r.getTTL() == 0) {
					it.remove();
					update(null, r);
				}
			}
			activePlatform.tick();
		}
	}

	public void sendActivePlatform(ACLMessage reply) {

		reply.setPerformative(ACLMessage.INFORM);
		reply.setConversationId(ConstantForACL.MANAGE_PLATFORM.value());
		reply.setLanguage(FactoryUtil.JAVASERIALIZATION_LANGUAGE);
		try {
			synchronized (activePlatform) {
				reply.setContentObject((ActivePlatformStub) activePlatform);
			}

		} catch (IOException e) {
			WrapperErrori.wrap("", e);
			// TODO cambiare questa parte magari progettando un
			// ontologia lasciando il codice in questo modo
			// potrebbero verificarsi degli errori in caso di
			// elevato numero di utenti, un'altra soluzione è quella
			// di utilizzare un file locale
			reply.setLanguage(FactoryUtil.STRING_LANGUAGE);
			reply.setContent(activePlatform.toString());
		}
		myAgent.send(reply);
	}

	@Override
	public void setTerminate(boolean flag) {
		terminate = flag;
	}

	@Override
	public boolean done() {
		return terminate;
	}

	public void update(Observable o, Object arg) {
		if (arg instanceof MulticastMessage) {
			ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
			aclmsg.setConversationId(ConstantForACL.MANAGE_FEDERATION.value());
			aclmsg.setLanguage(FactoryUtil.JAVASERIALIZATION_LANGUAGE);
			// aclmsg.addReceiver(new
			// AID(AgentName.federatorAgent.name(),false));
			MulticastMessage msg = (MulticastMessage) arg;
			boolean ok = true;
			if (msg.getType().equals(ConstantTypeMsg.EXIT)) {
				aclmsg.setPerformative(ACLMessage.INFORM);
				try {
					aclmsg.setContentObject(msg.getPlatform());
				} catch (IOException e) {
					WrapperErrori.wrap("", e);
					ok = false;
				}
			} else {
				// è sicuramente o un messaggio di ping o pong in ogni caso si
				// deve aggiungere una piattaforma
				aclmsg.setPerformative(ACLMessage.REQUEST);
				try {
					aclmsg.setContentObject(msg.getPlatform());
				} catch (IOException e) {
					WrapperErrori.wrap("", e);
					ok = false;
				}
			}
			if (ok) {
				for (AID receiver : registeredFederator) {
					aclmsg.addReceiver(receiver);
				}
				myAgent.send(aclmsg);
			}

		} else if (arg instanceof RecordPlatform) {
			// TODO per ora non faccio distinzione tra un uscita forzata ed una
			// regolare
			ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
			aclmsg.setConversationId(ConstantForACL.MANAGE_FEDERATION.value());
			aclmsg.setLanguage(FactoryUtil.JAVASERIALIZATION_LANGUAGE);
			aclmsg.addReceiver(new AID(AgentName.federatorAgent.name(), false));
			boolean ok = true;
			try {
				aclmsg.setContentObject((RecordPlatform) arg);
			} catch (IOException e) {
				WrapperErrori.wrap("", e);
				ok = false;
			}
			if (ok) {
				for (AID receiver : registeredFederator) {
					aclmsg.addReceiver(receiver);
				}
				myAgent.send(aclmsg);
			}
		}
	}

}
