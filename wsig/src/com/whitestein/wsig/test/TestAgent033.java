/*
 * Created on Aug 19, 2004
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
package com.whitestein.wsig.test;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.SL0Helper;
import com.whitestein.wsig.translator.SOAPToFIPASL0;

import org.apache.log4j.Category;


/**
 * @author jna
 *
 * testing agent as a client.
 * A client agent is implemented for testing purpose.
 */
public class TestAgent033 extends Agent {

	private static String nickName = "TestAgent033";
	private int convId = 0;
	private Category cat = Category.getInstance(TestAgent033.class.getName());
	public static final String FIPA_SERVICE = "operation0";
	public static final String OPERATION = "getVersion";
	//public static final String FIPA_SERVICE = "getTestVersion";
	//public static final String OPERATION = "getVersion";

	public static String getNickName() {
		return nickName;
	}

	protected void setup() {
		
		// set a nick name by informations registered
		nickName = getAID().getLocalName();
		
		cat.info("A " + nickName + " is starting.");

		// add behaviours of this Agent
		
		this.addBehaviour( new TickerBehaviour( this, 5000 ) {
			private int d = 1;
			public void onTick() {
				ACLMessage request;
				switch(d) {
					case 1:
						//d = 2;
						d = 1;
						request = createRequest();
						break;
					case 2:
					default:
						d = 1;
						request = createRequestXMLtagged();
						break;
				}
				cat.debug( SL0Helper.toString(request));
				send(request);
			}
		});

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
					try {
						cat.debug("A " + nickName + " receives: " + SL0Helper.toString(msg));
					}catch ( Exception e ) {
						cat.error(e);
					}
				}else {
					block();
				}
			}
		});
		
		// make an external class to access AID
		cat.debug("A " + nickName + " is started.");
	}
		
	private synchronized ACLMessage createRequest() {
		//register itself to the gateway
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( Configuration.getInstance().getGatewayAID());
		msg.setSender( this.getAID());
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setConversationId( "conv_" + convId ++ );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		msg.setOntology("AnOntology");
		msg.setContent(
				"((action                                                       \n" +
				"		(agent-identifier                                         \n" +
				"			:name " + Configuration.getInstance().getGatewayAID() + " )                       \n" +
				"		(" + FIPA_SERVICE + "                         \n" +
	            "   ) ))");
		
		return msg;
	}
	
	private ACLMessage createRequestXMLtagged() {
		ACLMessage msg = createRequest();
		msg.setContent(
				"((action                                                       \n" +
				"		(agent-identifier                                         \n" +
				"			:name " + Configuration.getInstance().getGatewayAID() + " )                       \n" +
				"		(" + SOAPToFIPASL0.XML_TAG_ + FIPA_SERVICE + "\n" +
				"          :" + SOAPToFIPASL0.XML_ATTRIBUTES + " (set (property :name color :value blue))\n" +
				"          :" + SOAPToFIPASL0.XML_ELEMENT + " (" + FIPA_SERVICE + ")\n" +
	            "       ) ))");
		
		return msg;
	}
	
	protected void takeDown() {
		cat.debug("A " + nickName + " is taken down now.");
	}

}
