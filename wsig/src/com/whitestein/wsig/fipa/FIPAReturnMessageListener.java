/*
 * Created on Nov 8, 2004
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

//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;

//import javax.xml.soap.SOAPElement;
//import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Category;

import jade.content.abs.AbsAgentAction;
//import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsConcept;
//import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsPrimitive;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;

import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.struct.ReturnMessageListener;
import com.whitestein.wsig.translator.SOAPToFIPASL0;

/**
 * @author jna
 *
 * Provides links into objects used during a completition of an ACLMessage returned. 
 */
public class FIPAReturnMessageListener implements ReturnMessageListener {

	private ACLMessage originalRequest;
	private Category cat = Category.getInstance( FIPAReturnMessageListener.class.getName());

	
	public FIPAReturnMessageListener ( ACLMessage originalRequest ) {
		this.originalRequest = originalRequest;
	}
	
	/**
	 * takes a returned message to proccess 
	 *
	 * @param retMsg returned message
	 */
	public void setReturnedMessage( CalledMessage retMsg ) {
		// a response is composed by a retMsg and by original message's fields
		ACLMessage retACL = ((FIPAMessage)retMsg).getACLMessage();
		ACLMessage response = originalRequest.createReply();
		String action = SL0Helper.removeOneOutermostParanteses(
				originalRequest.getContent());
		String[] p;

		//cat.debug( " response is " + SL0Helper.toString( retACL ) );
		//cat.debug( " content is " + retACL.getContent()  );
		int actionIndex = -1;
		switch ( retACL.getPerformative() ) {
			case ACLMessage.FAILURE:
				// a tuple: an action and a proposition
			case ACLMessage.REFUSE:
				// a tuple: an action and a proposition
			case ACLMessage.NOT_UNDERSTOOD:
				// a tuple: an action/event and a explanation

				actionIndex = 0;
				try {
					// a content's level of paranteses
					p = SL0Helper.splitParanteses( retACL.getContent());

					// set a result_action's parameter
					if ( SOAPToFIPASL0.NONE.compareToIgnoreCase(p[actionIndex]) == 0 ) {
						p[actionIndex] = action;
					}
		
					// fill a content in form: (original_action proposition/explanation))
					response.setContent( "(" + p[0] + " " + p[1] + ")" );
	
				}catch (Exception e) {
					cat.error(e);
				}
	
				break;
			case ACLMessage.INFORM:
				// a proposition of following forms:
				//   ( result action value )
				//  or  ( done action )
				actionIndex = 1;
				try {
					// a content's level of paranteses
					p = SL0Helper.splitParanteses(	retACL.getContent());
					// a result's level of paranteses
					p = SL0Helper.splitParanteses(p[0]);
					
					// set a result_action's parameter
					if ( SOAPToFIPASL0.NONE.equalsIgnoreCase(p[actionIndex]) ) {
						p[actionIndex] = action;
					}else {
						cat.debug("An action translated is expeted in a form \""
							+ SOAPToFIPASL0.NONE + "\" in a response from a translator.");
					}
	
					// fill a content in form: ( result/done original_action [result]))
					String resp = "((";
					for( int i=0; i < p.length; i ++ ) {
						resp += p[i] + " ";
					}
					resp += "))";
					response.setContent( resp );
					
					//cat.debug( " response updated: " + resp);
				}catch (Exception e) {
					cat.error(e);
				}

				break;
			case ACLMessage.REQUEST:
			case ACLMessage.CANCEL:
			default:
				// an answer is expected
				break;
		}

		//AbsPredicate ap = SL0Helper.extractResult(retACL);
		// fill an action's field
		//ap.set( SL0Vocabulary.RESULT_ACTION, SL0Helper.extractAction(originalRequest));
		//SL0Helper.fillAbsPredicateToContent( response, ap );
		//
		//  problems are arrised with :_JADE.UNNAMEDnn attributes,
		//    which are generated but are unwandted
				
		// performative is set the same as a retACL's one
		response.setPerformative( retACL.getPerformative());

		//  a debug message
		// cat.debug(" response is " + SL0Helper.toString(response));
		// cat.debug(" response is " + response.getContent());

		// send a response by the GatewayAgent
		GatewayAgent.getInstance().sendACL(response);
		// cat.debug(" response is " + SL0Helper.toString(response));
		//  then a call is removed
		GatewayAgent.getInstance().removeFromCallStore(
				originalRequest.getSender(),
				originalRequest.getConversationId() );
	}

}
