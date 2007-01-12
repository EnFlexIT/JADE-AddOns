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
 * Portions created by the Initial Developer are Copyright (C) 2005
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
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.SL0Helper;
import com.whitestein.wsig.translator.SOAPToFIPASL0;


/**
 * @author jna
 *
 * testing agent as a client.
 * A client agent is implemented for testing purpose.
 */
public class TestAgentForAmazon extends Agent {

	private static String nickName = "TestAgent_forAmazon";
	private int convId = 0;
	private Logger log = Logger.getLogger(TestAgentForAmazon.class.getName());
	public static final String wsdlOperation = "Help";

	public static String getNickName() {
		return nickName;
	}

	protected void setup() {
		
		// set a nick name by informations registered
		nickName = getAID().getLocalName();
		
		log.info("A " + nickName + " is starting.");

		// add behaviours of this Agent
		
		this.addBehaviour( new OneShotBehaviour( this ) {
			public void action() {
				doSearch();
			}
		});

		// make an external class to access AID
		log.debug("A " + nickName + " is started.");
	}

	private void doSearch() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		Property p = new Property(
			Configuration.WEB_SERVICE + ".operation",
			wsdlOperation );
		sd.addProperties( p );
		template.addServices( sd );
		try {
			DFAgentDescription[] res = DFService.search( this, template);
			if ( res.length < 1 ) {
				log.info( "No service is found." );
				doDelete();
				return;
			} else {
				AID aid;
				String serviceName;

				aid = res[0].getName();

				serviceName = findServiceName( res[0] );
				if ( null == serviceName ) {
					log.info( "No service is found in results." );
					doDelete();
					return;
				}


				this.addBehaviour( new CyclicBehaviour( this ) {
					public void action() {
						ACLMessage msg = myAgent.receive();
						if ( msg != null ) {
							processResponse( msg );
						}else {
							block();
						}
					}
				});

				final ACLMessage m = createRequest( aid, serviceName );
				this.addBehaviour( new OneShotBehaviour( this ) {
					public void action() {
						send( m );
					}
				});
			}
		
		} catch ( FIPAException fe ) {
			log.debug( fe );
		}
	}

	private String findServiceName( DFAgentDescription dfad ) {
		String res = null;
		ServiceDescription sd;
		Iterator it = dfad.getAllServices();
		while( it.hasNext() ) {
			sd = (ServiceDescription) it.next();
			res = findServiceName( sd ); 
			if ( null != res ) {
				break;
			}
		}
		return res;
	}

	private String findServiceName( ServiceDescription sd ) {
		Property p;
		Iterator it = sd.getAllProperties();
		while( it.hasNext() ) {
			p = (Property) it.next();
			if ( wsdlOperation.equalsIgnoreCase( (String) p.getValue()) ) {
				return sd.getName();
			}
		}
		return null;
	}

	private void processResponse( ACLMessage msg ) {
		log.debug(" response is: " + SL0Helper.toString(msg) );
		doDelete();
	}

	private synchronized ACLMessage createRequest( AID aid, String service ) {
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( aid );
		msg.setSender( this.getAID());
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setConversationId( "conv_" + convId ++ );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		msg.setOntology("AnOntology");
		msg.setContent(
				"((action \n" +
				"	(agent-identifier " +
				":name " + aid + " )\n" +
				"	(" + service + "\n" +

				// FIXME: a "Request" is translated as a FIPA keyword in FIPA SL0's code
				//   a translator generates aditional fipa-attributes
				// "(Request (Author  \"Tolkien\")" +
				// "  (Brand \"book\")" +
				// "  (Title \"The Lord of the Rings\") )" +

				"\n   ) ))");
		
		log.debug(" request is: " + SL0Helper.toString(msg) );
		return msg;
	}
	
	private synchronized ACLMessage createSearch( String wsdlOperation ) {
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( Configuration.getInstance().getGatewayAID());
		msg.setSender( this.getAID());
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setConversationId( "conv_" + convId ++ );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		msg.setOntology("FIPA-Agent-Management");
		msg.setContent(
				"((action " +

				"(agent-identifier " +
				":name " +
				Configuration.getInstance().getGatewayAID() +
				") " +

				"(search " +
				" (df-agent-description " +
				"  :services (set " +
				"     (service-description :properties (set " +
				"       (property :name web-service.operation :value " + wsdlOperation + " )" +
				" " +

				"     )) " +
				" ) ) " +
				" (search-constraint) " +

				") ))");
		
		return msg;
	}
	
	private ACLMessage createRequestXMLtagged( AID aid, String service ) {
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		msg.addReceiver( aid );
		msg.setSender( this.getAID());
		msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		msg.setConversationId( "conv_" + convId ++ );
		msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		msg.setOntology("AnOntology");
		msg.setContent(
				"((action                                                       \n" +
				"		(agent-identifier                                         \n" +
				"			:name " + aid + " )                       \n" +
				"		(" + SOAPToFIPASL0.XML_TAG_ + service + "\n" +
				"          :" + SOAPToFIPASL0.XML_ATTRIBUTES + " (set (property :name color :value blue))\n" +
				"          :" + SOAPToFIPASL0.XML_ELEMENT + " (" + service + ")\n" +
	            "       ) ))");
		
		return msg;
	}
	
	protected void takeDown() {
		log.debug("A " + nickName + " is taken down now.");
	}

}
