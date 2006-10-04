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
 * Portions created by the Initial Developer are Copyright (C) 2004, 2005
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
package com.whitestein.wsig.test;

import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsObject;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.df;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.FIPAServiceIdentificator;
import com.whitestein.wsig.fipa.SL0Helper;
import com.whitestein.wsig.translator.SOAPToFIPASL0;

import org.apache.log4j.Category;


/**
 * @author jna
 *
 * testing agent as a server.
 * A server agent is implemented for testing purpose.
 */
public class TestAgent002 extends Agent {

	private final static String nickName = "TestAgent002";
	private int convId = 0;
	private Category cat = Category.getInstance(TestAgent002.class.getName());
	private SLCodec codec = new SLCodec(0);
	private DFAgentDescription dfad = new DFAgentDescription();
	public boolean isTerminating = false;
	public static AID myAID = null;
	public static final String OPERATION = "echo";
	public static final String OPERATION_2 = "get_true";
	private static SLCodec codecSL0 = new SLCodec(0);


	public DFAgentDescription getDfad() {
		return dfad;
	}
	
	public static String getNickName() {
		return nickName;
	}
	
	public static FIPAServiceIdentificator getFIPAServiceId() {
		return new FIPAServiceIdentificator( myAID, OPERATION );
	}
	
	protected void setup() {
		cat.info("A TestAgent001 is starting.");

		// add behaviour of the GatewayAgent
		this.addBehaviour( new CyclicBehaviour( this ) {
			private MessageTemplate template =
					MessageTemplate.and(
							MessageTemplate.MatchProtocol(
									FIPANames.InteractionProtocol.FIPA_REQUEST),
							MessageTemplate.MatchLanguage(
									FIPANames.ContentLanguage.FIPA_SL0 ));
			public void action() {
				ACLMessage msg = myAgent.receive(); //( template );
				if ( msg != null ) {
					switch ( msg.getPerformative() ) {
						case ACLMessage.REQUEST:
							doFIPARequest( msg );
							break;
						case ACLMessage.CANCEL:
							doFIPACancel( msg );
							break;
						case ACLMessage.INFORM:
							// DF inform is only expected
							if ( isTerminating ) {
								myAgent.doDelete();
							}
							break;
						case ACLMessage.REFUSE:
						case ACLMessage.FAILURE:
						case ACLMessage.NOT_UNDERSTOOD:
						case ACLMessage.AGREE:
							// agreement is expected as default
							// gateway is waiting for an inform or a failure message
							break;
						default:
							// not in fipa-request protocol
							//doNoFipaRequest( msg );
					}
					
					try {
						cat.debug("A testAgent001 receives: " + SL0Helper.toString(msg));
					}catch ( Exception e ) {
						cat.error(e);
					}
					
				}else {
					block();
				}
			}
		});
		
		// make an external class to access AID
		myAID= this.getAID();
		AID  dfAID = new AID( "df", AID.ISLOCALNAME );
			
		//register itself to the gateway
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( dfAID ); // Configuration.getInstance().getGatewayAID());
		msg.setSender( this.getAID());
		msg.setConversationId( "conv_" + convId ++ );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		msg.setOntology(FIPAManagementVocabulary.NAME);
		
		dfad.setName( this.getAID());
		dfad.addLanguages( FIPANames.ContentLanguage.FIPA_SL0 );
		dfad.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );
		ServiceDescription sd;
		sd = new ServiceDescription();
		sd.setName( OPERATION );
		sd.addLanguages( FIPANames.ContentLanguage.FIPA_SL0 );
		sd.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );
		sd.setType("web-service");
		Property p = new Property("type","(set web-service)");
		sd.addProperties( p );
		dfad.addServices(sd);
		sd = new ServiceDescription();
		sd.setName( OPERATION_2 );
		sd.addLanguages( FIPANames.ContentLanguage.FIPA_SL0 );
		sd.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );
		sd.setType("web-service");
		dfad.addServices(sd);
		
		//set register's argument
		Register reg = new Register();
		reg.setDescription(dfad);
		
		// create registration's action
		Action action = new Action( this.getAID(), reg );

		try {
			getContentManager().registerLanguage( codec );
			getContentManager().registerOntology(FIPAManagementOntology.getInstance());

			getContentManager().fillContent(msg, action);
			send(msg);
			
			//DFService.register( this, dfad );
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//cat.debug( SL0Helper.toString(msg));
		
		/*
		this.addBehaviour( new WakerBehaviour(this,100000) {
			protected void handleElapsedTimeout() {
				// deregister itself
				Deregister dereg = new Deregister();
				dereg.setDescription(getDfad());
				Action action = new Action( myAgent.getAID(), dereg );

				// fill in a message
				ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
				msg.addReceiver( Configuration.getInstance().getGatewayAID());
				msg.setSender( myAgent.getAID());
				msg.setConversationId( "conv_" + convId ++ );
				msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
				msg.setOntology(FIPAManagementVocabulary.NAME);
				try {
					getContentManager().fillContent(msg, action);
					cat.debug("A testAgent001 sends: " + SL0Helper.toString(msg));
					isTerminating = true;
					send(msg);
				}catch (Exception e) {
					cat.error(e);
				}
			}
		});
		*/
		
		cat.debug("A TestAgent001 is started.");
	}
	
	protected void takeDown() {
		//deregister itself from the gateway
		AID  dfAID = new AID( "df", AID.ISLOCALNAME );
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( dfAID ); // Configuration.getInstance().getGatewayAID());
		msg.setSender( this.getAID());
		msg.setConversationId( "conv_" + convId ++ );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		msg.setOntology(FIPAManagementVocabulary.NAME);

		//set deregister's argument
		Deregister dereg = new Deregister();
		dereg.setDescription(dfad);
		
		// create registration's action
		Action action = new Action( this.getAID(), dereg );

		try {
			getContentManager().registerLanguage( codec );
			getContentManager().registerOntology(FIPAManagementOntology.getInstance());

			getContentManager().fillContent(msg, action);
			send(msg);
			blockingReceive();  // ?
		}catch (Exception e) {
			cat.debug( e );
		}
		
		cat.debug( SL0Helper.toString(msg));
		cat.debug("A TestAgent001 is taken down now.");
	}

	private void doFIPARequest( ACLMessage acl ) {
		ACLMessage resp = acl.createReply();
		AbsContentElement ac = null;
		AbsObject action = null;
		AbsObject ao;
		try {
			ac = codecSL0.decode(
					BasicOntology.getInstance(),
					acl.getContent() );
			if ( SL0Vocabulary.ACTION.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
				action = com.whitestein.wsig.translator.FIPASL0ToSOAP.getActionSlot( ac );
				String opName = action.getTypeName();
				// to treat with XML attributes
				AbsObject absAttributes = null;
				if ( opName.startsWith(SOAPToFIPASL0.XML_TAG_) ) {
					ao = action.getAbsObject( SOAPToFIPASL0.XML_ELEMENT );
					opName = ao.getTypeName();
				}

				if ( opName.compareToIgnoreCase(OPERATION) == 0) {
					resp = SL0Helper.createInformDoneForCancel( acl );
				}else if ( opName.compareToIgnoreCase(OPERATION_2) == 0) {
					resp = SL0Helper.createInformResult( acl, "\"true\"" );
				}else {
					resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
					resp.setContent(
							"( (action \"originator\" \"communicative act\")" +
							"  (unknown (service " + opName + " )) )" );

				}
			}
		}catch ( Exception e ) {
			cat.error(e);
		}

		send(resp);
		cat.debug("Agent " + getNickName() + " sends response: " +
				resp.getContent() );
	}
	
	private void doFIPACancel( ACLMessage acl ) {
		send( SL0Helper.createInformDoneForCancel( acl ));
	}
}
