/*
 * Created on Jul 1, 2004
 *
 */

/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebService Integration Gateway (WSIG).
 *
 * The Initial Developer of the Original Code is
 * Whitestein Technologies AG.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Jozef Nagy (jna at whitestein.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.whitestein.wsig.fipa;

import jade.content.Concept;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsObject;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.df;
import jade.domain.DFGUIManagement.DFAppletOntology;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.ExceptionVocabulary;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.Gateway;
import com.whitestein.wsig.fipa.FIPAMessage;
import com.whitestein.wsig.fipa.FIPAReturnMessageListener;
import com.whitestein.wsig.fipa.FIPAServiceIdentificator;
import com.whitestein.wsig.fipa.SL0Helper;
import com.whitestein.wsig.struct.Call;
import com.whitestein.wsig.struct.ReturnMessageListener;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.whitestein.wsig.translator.FIPASL0ToSOAP;

import java.util.Hashtable;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Category;


/**
 * @author jna
 *
 * is gateway agent running in FIPA container.
 */
/* It extends df as the JADE implementation of DF to manage agents.
 * Remark: its functional and used version is placed in jade.domain package.
 *   Must be implemented in near future.
 */
public class GatewayAgent extends Agent {
	
	private static final Object synchObject = new Object();
	private static GatewayAgent instance;
	private static int conversationId = 0;
	private Hashtable conversationId2listener = new Hashtable();
	private Hashtable listener2fipaMessage = new Hashtable();
	private static Category cat = Category.getInstance( GatewayAgent.class.getName());
	private static SLCodec codecSL0 = new SLCodec(0);
	private Hashtable callStore = new Hashtable();
	private static final String DF_LOCAL_NAME = "df";
	// public static final String WEB_SERVICE = "web-service";
	
	private DFMethodListener dfMethodListener = null;
	private DFAgentDescription dfad = new DFAgentDescription();
	private AID dfAID = new AID( DF_LOCAL_NAME, AID.ISLOCALNAME );
	

	
	/**
	 * creates new GatewayAgent
	 *
	 */
	public GatewayAgent() {
		super();
		Gateway.getInstance();  // start also Gateway
	}
	
	/**
	 * returns singleton instance.
	 * Running instance is returned.
	 * 
	 * @return an instance
	 */
	public static GatewayAgent getInstance() {
		synchronized ( synchObject ) {
			while ( null == instance ) {
				// wait for setup() method invocation for this
				cat.debug( " instance is null, waiting" );
				try {
					synchObject.wait(1000);
					//Thread.sleep(1000);
				}catch (Exception e) {
				}
			}
		}
		return instance;
	}
	
	/**
	 * sends a FIPA message.
	 * The message is going to be changed.
	 * 
	 * @param msg message to be sent
	 * @param listener listener for returned message
	 */
	public void send( FIPAMessage msg, ReturnMessageListener listener ) {
		ACLMessage acl = msg.getACLMessage();
		fillACLParameters( acl );
		synchronized( conversationId2listener ) {
			conversationId2listener.put(
				msg.getACLMessage().getConversationId(),
				listener );
		}
		synchronized ( listener2fipaMessage ) {
			listener2fipaMessage.put(
				listener,
				msg);
		}
		// testing print out
		cat.debug(" WSIGS: ACL message created. " + SL0Helper.toString(acl));
		send( acl );
	}
	
	public void sendACL( ACLMessage acl ) {
		// used by FIPAReturnMessageListener
		// create reply does not fill a sender
		acl.setSender( Configuration.getInstance().getGatewayAID() );
		cat.debug(" a response is " + SL0Helper.toString(acl));
		send(acl);
	}
	/**
	 * removes a listener registered
	 * 
	 * @param listener a listener registered
	 */
	public void removeReturnMessageListener( ReturnMessageListener listener ) {
		FIPAMessage msg;
		synchronized( listener2fipaMessage ) {
			msg = (FIPAMessage)
				listener2fipaMessage.get( listener );
			listener2fipaMessage.remove( listener );
		}
		ACLMessage cancel = msg.getACLMessage();
		cancel.setPerformative( ACLMessage.CANCEL );

		// waiting for cancel's response may produce a long time out
		// remove the listener now, then send the cancel
		String convId = cancel.getConversationId();
		synchronized( conversationId2listener ) {
			listener = (ReturnMessageListener)
				conversationId2listener.get( convId );
			conversationId2listener.remove( convId );
		}
		send( cancel );
	}
	
	/**
	 * fills ACL mandatory parameters
	 * 
	 * @param acl message to fill
	 */
	private void fillACLParameters( ACLMessage acl ) {
		acl.setSender( Configuration.getInstance().getGatewayAID() );
		acl.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		acl.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		acl.setConversationId( getConversationId() );
	}
	
	/**
	 * creates an unique conversation Id
	 * 
	 * @return conversation Id
	 */
	private synchronized String getConversationId() {
		// return Integer.toString( conversationId ++ );
		// return Configuration.getInstance().getGatewayAID() + Integer.toString( conversationId ++ );
		return "Query" + Configuration.getInstance().getGatewayAID() + (new Date()).getTime();
	}

	/**
	 * sets up this agent
	 */
	protected void setup() {
		super.setup();
		// initialize an instance
		try {
			synchronized ( synchObject ) {
				instance = this;
				dfMethodListener = Configuration.getInstance().getDFMethodListener();
				synchObject.notifyAll();
				cat.info("WSIG's GatewayAgent is set up.   " + (null != instance) );
			}
		}catch (Exception e) {
			cat.debug( e );
		}


		try {
			getContentManager().registerLanguage( codecSL0 );
			getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		}catch (Exception e ) {
			
		}

		// add behaviour of the GatewayAgent
		this.addBehaviour( new CyclicBehaviour( this ) {
			private MessageTemplate template = MessageTemplate.and(
					constructNoDFMessageTemplate(),
					//MessageTemplate.and(
							MessageTemplate.MatchProtocol(
									FIPANames.InteractionProtocol.FIPA_REQUEST) ); //,
					//		MessageTemplate.or(
					//		MessageTemplate.MatchLanguage(
					//				FIPANames.ContentLanguage.FIPA_SL0 )));
					// all SL family must be tested
			public void action() {
				// receive all messages, do not ignore DFMessageTemplate
				ACLMessage msg = myAgent.receive(); // template );
				if ( msg != null ) {
					try {
						cat.debug("A request for WSIG:" + SL0Helper.toString(msg));
					}catch ( Exception e ) {
						cat.error(e);
					}
					
					switch ( msg.getPerformative() ) {
						case ACLMessage.REQUEST:
							doFIPARequest( msg );
							break;
						case ACLMessage.CANCEL:
							doFIPACancel( msg );
							break;
						case ACLMessage.REFUSE:
						case ACLMessage.FAILURE:
						case ACLMessage.INFORM:
						case ACLMessage.NOT_UNDERSTOOD:
							informListener( msg );
							break;
						case ACLMessage.AGREE:
							// agreement is expected as default
							// gateway is waiting for an inform or a failure message
							break;
						default:
							// not in fipa-request protocol
							doNoFipaRequest( msg );
					}
					
				}else {
					block();
				}
			}
		});

		// register into a df
		registerMe();
	}

	/**
	 *  registers a WSIG into a DF
	 *
	 */
	private void registerMe() {
		//register itself to a DF
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( dfAID ); // Configuration.getInstance().getGatewayAID());
		/*
		msg.setSender( this.getAID());
		msg.setConversationId( getConversationId() );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		msg.setOntology(FIPAManagementVocabulary.NAME);
		*/
		fillACLParameters( msg );
		msg.setOntology(FIPAManagementVocabulary.NAME);
		
		dfad.setName( this.getAID());
		dfad.addLanguages( FIPANames.ContentLanguage.FIPA_SL0 );
		dfad.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );

		//set register's argument
		Register reg = new Register();
		reg.setDescription(dfad);
		
		// create registration's action
		Action action = new Action( this.getAID(), reg );

		try {
			//getContentManager().registerLanguage( codec );
			//getContentManager().registerOntology(FIPAManagementOntology.getInstance());

			getContentManager().fillContent(msg, action);
			send(msg);
			
			//DFService.register( this, dfad );
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//cat.debug( SL0Helper.toString(msg));

	}
	
	/**
	 * takes down this agent.
	 * A configuration used is stored.
	 */
	protected void takeDown() {
		Configuration.store();

		//deregister itself from a DF
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( dfAID ); // Configuration.getInstance().getGatewayAID());
		fillACLParameters( msg );
		msg.setOntology(FIPAManagementVocabulary.NAME);

		//set deregister's argument
		Deregister dereg = new Deregister();
		dereg.setDescription(dfad);
		
		// create registration's action
		Action action = new Action( this.getAID(), dereg );

		try {
			getContentManager().fillContent(msg, action);
			send(msg);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		cat.debug( SL0Helper.toString(msg));
		cat.debug("A gateway is taken down now.");
	}


	
	/**
	 * sends inform-done for a cancel operation.
	 * Nothing special is performed, only a requester is informed.
	 * 
	 * @param acl a request for a cancel
	 */
	private void doFIPACancel( ACLMessage acl ) {
		// cancel a connection into a Web Service
		
		// find a call
		// remove a call from a structure
		Call call = removeFromCallStore( acl.getSender(), acl.getConversationId());
		if ( null != call ) {
			// close a call
			call.close();
		}else {
			return;
		}
		
		// send an acl
		if ( ! call.isReceived() ) {
			send( SL0Helper.createInformDoneForCancel(acl));
		}else {
			// an answer is already communicated
		}
	}
	
	/**
	 * serves a request for a service.
	 * It has not been implemented yet.
	 * 
	 * @param acl a request message
	 */
	private void doFIPARequest( ACLMessage acl ) {
		FIPAMessage msg = new FIPAMessage(acl);
		// a message is a request, not a response
		msg.setResponse(false);

		//  extract FIPAServiceIdentificator
		//  find a ServedOperation
		ServedOperation so = ServedOperationStore.getInstance().find(
				getFIPAServiceId(msg) );

		// create connection into a Web Service
		//  perform call
		Call call = so.createCall();
		try {
			cat.debug(" WSIG is called by an agent now.");
			call.setMessage( msg );
			ReturnMessageListener listener = new FIPAReturnMessageListener( acl );
			call.setReturnMessageListener( listener );
			// store a call for a cancel
			storeACall( acl.getSender(), acl.getConversationId(), call );
			call.invoke();
		}catch (Exception e) {
			cat.error(e);
		}
	}
	
	/**
	 * creates and sends not understood for bad performative.
	 * A good performative is in the fipa-request protocol [FIPA SC00026H].
	 * They are: request, refuse, agree, failure, inform, cancel and not-understood.
	 * 
	 * @param acl message received
	 */
	private void doNoFipaRequest( ACLMessage acl ) {
		ACLMessage r = acl.createReply();
		r.setPerformative( ACLMessage.NOT_UNDERSTOOD );
		r.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		r.setContent(
				"( " + SL0Helper.toStringAclAsAction(acl) + "\n" +
				"  (error-message \"Performative is not in the fipa-request protocol.\")) )" );
		send( r );
	}
	
	/**
	 * performs actions on a ACL message returned.
	 * A listener registered is informed.
	 * The method is invoked for messages, which does not have got a continuation.
	 * ( refuse, failure, inform, not-understood ).
	 * 
	 * @param acl message received
	 */
	private void informListener( ACLMessage acl ) {
		if( acl.getPerformative() == ACLMessage.INFORM
			&& DF_LOCAL_NAME.equalsIgnoreCase( acl.getSender().getLocalName()) ) {
			// the acl is inform from a df
			
			// check, if a WSIG is an only receiver
			Iterator it = acl.getAllReceiver();
			int count = 0;
			while ( count < 2 && it.hasNext() ) {
				count ++;
				it.next();
			}
			
			if( count > 1){
				// the acl is a response from other then a WSIG's registration 
				informFromDFManagement( acl );
				return;
			}else {
				// is a response related to a WSIG's request 
				cat.debug( "A df's inform message received for WSIG DF's management: " + SL0Helper.toString( acl ));
				return;  // only as a quick hack, do a propper registration latter
			}
		}
		
		// other messages
		ReturnMessageListener listener;
		String convId = acl.getConversationId();
		synchronized( conversationId2listener ) {
			listener = (ReturnMessageListener)
				conversationId2listener.get( convId );
			conversationId2listener.remove( convId );
		}
		if ( listener != null ) {
			FIPAMessage fipa = new FIPAMessage( acl );
			fipa.setResponse( true );
			listener.setReturnedMessage( fipa );
			cat.debug("listener invoked for conversationId " + convId );
		}else {
			cat.debug("listener is null for conversationId " + convId );
		}
	}

	/**
	 * proceses an inform-done message from DF's management.
	 * Management's requests from another agents to DF is also redirected
	 * into a WSIG, when they are successfull.
	 * A search response is not redirected.
	 * 
	 * @param acl message from DF
	 */
	private void informFromDFManagement( ACLMessage acl ) {
		if( null == dfMethodListener ) {
			// no listener
			return;
		}
	    Action slAction = null;
	    Done slDone = null;
		try {
			cat.debug( "A df's inform message received from another agent: " + SL0Helper.toString( acl ));
		    slDone = (Done) getContentManager().extractContent(acl);
		    slAction = (Action) slDone.getAction();
		    Concept action = slAction.getAction();

		    // REGISTER
		  	if (action instanceof Register) {
		  		dfMethodListener.registerAction((Register) action, acl.getSender());
		  	}
		  	// DEREGISTER
		  	else if (action instanceof Deregister) {
		  		dfMethodListener.deregisterAction((Deregister) action, acl.getSender());
		  	}
		  	// MODIFY
		  	else if (action instanceof Modify) {
		  		dfMethodListener.modifyAction((Modify) action, acl.getSender());
		  	}

		// nothing bad is expected
		}catch (OntologyException oe) {
		}catch (CodecException ce) {
		}catch (FIPAException fe) {
		}
	}
	
	/**
	 * construct message template for not DF.
	 * A MessageTemplate is created for no DF communication.
	 * The JADE DF implementation is taken as MessageTemplate refference.
	 *  
	 * @return message template fo no DF communication
	 */
	private MessageTemplate constructNoDFMessageTemplate() {
		// Create template for receiving messages
		MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

		// Behaviour dealing with FIPA management actions
		MessageTemplate	mtFIPA = MessageTemplate.and(mt, MessageTemplate.MatchOntology(FIPAManagementOntology.getInstance().getName()));

	    // Behaviour dealing with JADE management actions
	    MessageTemplate mtJADE = MessageTemplate.and(mt, MessageTemplate.MatchOntology(JADEManagementOntology.getInstance().getName()));

	    // Behaviour dealing with DFApplet management actions
	    MessageTemplate mtDFApplet = MessageTemplate.and(mt, MessageTemplate.MatchOntology(DFAppletOntology.getInstance().getName()));

		// Behaviour dealing with subscriptions
		MessageTemplate mtSub = MessageTemplate.and(
			MessageTemplate.MatchOntology(FIPAManagementOntology.getInstance().getName()),
			MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.SUBSCRIBE), MessageTemplate.MatchPerformative(ACLMessage.CANCEL)));
		
		// JADE DF messages
		MessageTemplate mtDFServices =
			MessageTemplate.or(
				MessageTemplate.or(mtFIPA,mtJADE),
				MessageTemplate.or(mtDFApplet,mtSub) );

		// Ignore JADE df specified templates
		MessageTemplate mtNoDFServices = MessageTemplate.not(mtDFServices);

		return mtNoDFServices;
	}
	
	
	/**
	 * gives a FIPA service's identificator
	 *  
	 * @param msg a message to identify
	 * @return an identificator
	 */
	private FIPAServiceIdentificator getFIPAServiceId( FIPAMessage msg ) {
		// has a meaning in a request's message

		AbsContentElement ac;
		AbsObject action;
		String service = "";
		try {
			ac = codecSL0.decode(
					BasicOntology.getInstance(),
					msg.getACLMessage().getContent() );
			if ( SL0Vocabulary.ACTION.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
				action = FIPASL0ToSOAP.getActionSlot( ac );
				service = action.getTypeName();
			}	
		}catch (Exception e) {
			cat.error(e);
		}
		return new FIPAServiceIdentificator(
				(AID) msg.getACLMessage().getAllReceiver().next(),
				service);
	}

	/**
	 * stores a call into a storage
	 * 
	 * @param originator an originator of the call
	 * @param conversationId a conversation identificator of the call
	 * @param call the call to be stored
	 */
	private void storeACall( AID originator, String conversationId, Call call ) {
		synchronized ( callStore ) {
			Hashtable convIds = (Hashtable) callStore.get( originator );
			if( null == convIds ) {
				convIds = new Hashtable();
				callStore.put(originator, convIds);
			}
			convIds.put( conversationId, call );
		}
	}
	
	/**
	 * removes and gives a call identified by a originator and a conversation identificator
	 *  
	 * @param originator an originator of the call
	 * @param conversationId a conversation identificator of the call
	 * @return the call to be erased
	 */
	Call removeFromCallStore( AID originator, String conversationId ) {
		synchronized (callStore) {
			Hashtable convIds = (Hashtable) callStore.get( originator );
			if( null != convIds ) {
				Call call = (Call) convIds.get(conversationId);
				convIds.remove(conversationId);
				return call;
			}
			return null;
		}
	}
	
	/**
	 * adds an operation into an DF agent description's structure.
	 * The operation is offered by gateway.
	 * 
	 * @param op operation added
	 */
	public synchronized void addToDFAgentDescription( ServedOperation op ) {
		//add a service
		dfad.addServices( createSDforOperation(op));
		sendModifyDF( dfad);
	}
	
	/**
	 * removes an operation from an DF agent description's structure.
	 * The operation is not offered by gateway anymore.
	 * 
	 * @param op operation removed
	 */
	public synchronized void removeFromDFAgentDescription( ServedOperation op ) {
		// remove a service
		dfad.removeServices( createSDforOperation( op ) );
		sendModifyDF( dfad);
	}

	/**
	 * creates a ServiceDescription for an operation
	 * @param op operation
	 * @return description created
	 */
	private ServiceDescription createSDforOperation( ServedOperation op ) {
		//prepare a FIPA's service
		ServiceDescription sd;
		sd = new ServiceDescription();
		sd.setName( op.getOperationID().getFIPAServiceIdentificator().getServiceName() );
		sd.addLanguages( FIPANames.ContentLanguage.FIPA_SL0 );
		sd.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );
		sd.setType( Configuration.WEB_SERVICE );
		
		Property p = new Property(Configuration.WEB_SERVICE + ".operation",
				op.getOperationID().getUDDIOperationIdentificator().getWSDLOperation());
		sd.addProperties( p );
		p = new Property(Configuration.WEB_SERVICE + ".accessPoint",
				op.getOperationID().getUDDIOperationIdentificator().getAccessPoint());
		sd.addProperties( p );
		p = new Property("type", "(set " + Configuration.WEB_SERVICE + ")");
		sd.addProperties( p );
		
		return sd;
	}
	
	/**
	 * sends a modify request into a DF
	 * 
	 * @param dfad agent description
	 */
	private void sendModifyDF( DFAgentDescription dfad ) {
		//prepare an ACL
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( dfAID ); // Configuration.getInstance().getGatewayAID());
		fillACLParameters( msg );
		msg.setOntology(FIPAManagementVocabulary.NAME);
		
		// create modification's action
		Modify modify = new Modify();
		modify.setDescription(dfad);
		Action action = new Action( this.getAID(), modify );

		try {
			getContentManager().fillContent(msg, action);
			send(msg);
			
			//DFService.modify( this, dfad );
		}catch (Exception e) {
			cat.error(e);
		}
	}
	
}
