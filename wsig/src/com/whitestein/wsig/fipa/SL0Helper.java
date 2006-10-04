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

import org.apache.log4j.Category;
import java.util.*;
//import com.whitestein.wsigs.struct.Call;
import com.whitestein.wsig.translator.FIPASL0ToSOAP;

import jade.content.abs.AbsAgentAction;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.content.onto.basic.Action;
import jade.content.onto.OntologyException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

/**
 * @author jna
 *
 * helps routines for SL0.
 */
public class SL0Helper {

	private static SLCodec codecSL0 = new SLCodec(0);
	private static Category cat = Category.getInstance(SL0Helper.class.getName());
	
	/**
	 * removes one couple of paranteses.
	 * An outermost pair of parantesses are removed.
	 * If it is not possible to remove, then the content unchanged is returned.
	 * 
	 * @param content SL0 content
	 * @return a content modified
	 */
	public static String removeOneOutermostParanteses( String content ) {
		int open = content.indexOf( "(" );
		int close = content.lastIndexOf( ")" );
		if ( open < 0 || close < 0 || open >= close ) {
			return content;
		}
		try {
			return content.substring( open + 1, close );
		}catch (IndexOutOfBoundsException e) {
			return content;
		}
	}
	
	/**
	 * splits an expression into an array.
	 * An expression is well parantesed.
	 * 
	 * @param content expression
	 * @return
	 * @throws Exception
	 */
	public static String[] splitParanteses( String content ) throws Exception {
		String[] part;
		int open = content.indexOf( "(" );
		int close = content.lastIndexOf( ")" );
		if ( open < 0 || close < 0 || open >= close ) {
			// no paranteses
			part = new String[0];
			return part;
		}
		int level = 0;
		String str = "", token = null;
		char c;
		Collection col = new ArrayList();
		boolean isString = false;
		boolean isBackslashed = false;
		
		// walk through the content
		for( int i = open + 1; i < close; i ++ ) {
			c = content.charAt(i);
			if ( isString && '"' != c && '\\' != c ) {
				// characters are not interpreted in a string
				isBackslashed = false;
				str += c;
				continue;
			}
			switch( c ) {
				case '(' :
					if( 0 == level ) {
						// a new token starts, old one is terminated
						token = str.trim();
					}
					level ++;
					isBackslashed = false;
					str += c;
					break;
				case ')' :
					level --;
					str += c;
					if( 0 == level ) {
						// a new token ends
						token = str.trim();
					}else if ( 0 > level ) {
						// a good parantesed expression is expected
						throw new Exception("A SL0Helper parser's error, expression is not well parantesed.");
					}
					isBackslashed = false;
					break;
				case '"' :
					str += c;
					if( ! isBackslashed ) {
						if( 0 == level ) {
							// construct a string at a level zerro, switch between
							//   a begin and an end character "
							if ( isString ) {
								token = str.trim();
								str = "";
							}
							isString = ! isString;
						}
					}
					isBackslashed = false;
					break;
				case '\\' :
					isBackslashed = true;
					str += c;
					break;
				default:
					if( 0 == level && Character.isWhitespace(c) && ! isString) {
						// whitespaces are only used as separators at a level zero 
						token = str.trim();
					}
					isBackslashed = false;
					str += c;
			}
			// cat.debug("   str = " + str);
			// cat.debug(" token = " + token);
			
			if ( null != token && token.length() > 0 ) {
				// it is not an empty substring
				col.add( token );
				//cat.debug(" token = " + token);
				token = null;
				str = "";
			}
		}
		
		// convert a collection into a array of Strings
		part = new String[ col.size() ];
		Iterator it = col.iterator();
		for(int i = 0; i < col.size(); i ++ ) {
			part[i] = (String) it.next();
		}
		return part;
	}
	
	/**
	 * creates inform-done for a message.
	 * A cancel message is expected and is not checked.
	 * If no error is occured an inform-done message is constructed.
	 * Otherwise a not-understood message is created.  
	 * 
	 * @param msg ACL message answered
	 * @return an answer
	 */
	public static ACLMessage createInformDoneForCancel( ACLMessage msg ) {
		ACLMessage resp = msg.createReply();
		AbsContentElement ac = null;
		String innerContent = removeOneOutermostParanteses( msg.getContent());
		resp.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		try {
			ac = codecSL0.decode(
					BasicOntology.getInstance(),
					msg.getContent() );
			if ( ac instanceof AbsContentElementList ) {
				// only one action is permited [FIPA SC00037J]
				resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
				resp.setContent(
						"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
						"  (unknown (content \"more than one acction\")) )" );
			}else if ( SL0Vocabulary.ACTION.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
				resp.setPerformative( ACLMessage.INFORM );
				resp.setContent( "((" + SL0Vocabulary.DONE + " " + innerContent + "))" );
			}else {
				// action only is expected
				resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
				if ( msg.getLanguage().compareToIgnoreCase(
						FIPANames.ContentLanguage.FIPA_SL0 ) != 0 ) {
					resp.setContent(
						"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
						"  (unknown (language \"" + msg.getLanguage() + "\")) )" );
				}else {
					resp.setContent(
							"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
							"  (error-message  \"The content is not a SL0 language and a request.\")) )" );
				}

			}
		}catch ( CodecException e ) {
			//e.printStackTrace();
			resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
			resp.setContent(
					"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
					"  (error-message \"" + e.getMessage() +  "\") )" );
		}
		
		return resp;
	}

	/**
	 * creates inform-result for a message.
	 * If no error is occured an inform-result message is constructed.
	 * Otherwise a not-understood message is created.  
	 * 
	 * @param msg ACL message answered
	 * @param result a result
	 * @return an answer
	 */
	public static ACLMessage createInformResult( ACLMessage msg, String result ) {
		ACLMessage resp = msg.createReply();
		AbsContentElement ac = null;
		String innerContent = removeOneOutermostParanteses( msg.getContent());
		resp.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
		try {
			ac = codecSL0.decode(
					BasicOntology.getInstance(),
					msg.getContent() );
			if ( ac instanceof AbsContentElementList ) {
				// only one action is permited [FIPA SC00037J]
				resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
				resp.setContent(
						"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
						"  (unknown (content \"more than one acction\")) )" );
			}else if ( SL0Vocabulary.ACTION.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
				resp.setPerformative( ACLMessage.INFORM );
				resp.setContent( "((" + SL0Vocabulary.RESULT + " " + innerContent + " " + result + "))" );
			}else {
				// action only is expected
				resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
				if ( msg.getLanguage().compareToIgnoreCase(
						FIPANames.ContentLanguage.FIPA_SL0 ) != 0 ) {
					resp.setContent(
						"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
						"  (unknown (language \"" + msg.getLanguage() + "\")) )" );
				}else {
					resp.setContent(
							"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
							"  (error-message  \"The content is not a SL0 language and a request.\")) )" );
				}

			}
		}catch ( CodecException e ) {
			//e.printStackTrace();
			resp.setPerformative( ACLMessage.NOT_UNDERSTOOD );
			resp.setContent(
					"( " + SL0Helper.toStringAclAsAction(msg) + "\n" +
					"  (error-message \"" + e.getMessage() +  "\") )" );
		}
		
		return resp;
	}
	
	public static AbsAgentAction generateActionForACL( ACLMessage acl ) {
		AbsAgentAction absAction = null; 
		try {
			absAction = (AbsAgentAction) 
				BasicOntology.getInstance().fromObject( new Action() );
	
			AbsConcept absAID = (AbsConcept)
				BasicOntology.getInstance().fromObject( acl.getSender() );
	
			AbsAgentAction absAcl = AbsHelper.externaliseACLMessage( acl, BasicOntology.getInstance());
	
			absAction.set( SL0Vocabulary.ACTION_ACTOR, absAID );
			absAction.set( SL0Vocabulary.ACTION_ACTION, absAcl );
		}catch (OntologyException oe) {
			cat.error(oe);
		}
		return absAction;
	}
	
	public static String toString( ACLMessage msg ) {
		try {
			// print out the message
			AbsObject a = AbsHelper.externaliseACLMessage( msg, BasicOntology.getInstance());
			AbsContentElementList aList = new AbsContentElementList();
			aList.add( (AbsContentElement) a );
			return removeOneOutermostParanteses( codecSL0.encode(aList));
		}catch (Exception e) {
			return "Bad ACL message.";
		}
	}

	public static String toStringAclAsAction( ACLMessage acl ) {
		try {
			// print out the message
			AbsContentElementList aList = new AbsContentElementList();
			aList.add( (AbsContentElement) generateActionForACL(acl) );
			String s = removeOneOutermostParanteses( codecSL0.encode(aList));
			return s; 
		}catch (Exception e) {
			return "Bad ACL message.";
		}
	}
	

	/**
	 * extracts a RESULT from an acl message.
	 * An acl's content as a RESULT is expected.
	 * 
	 * @param acl a message
	 * @return an action's content
	 */
	public static AbsPredicate extractResult( ACLMessage acl ) {
		AbsPredicate ap = null;
		try {
			ap = (AbsPredicate) codecSL0.decode(
					BasicOntology.getInstance(),
					acl.getContent() );
			if ( SL0Vocabulary.RESULT.compareToIgnoreCase( ap.getTypeName()) == 0 ) {
				AbsObject actionContent = FIPASL0ToSOAP.getActionSlot(ap.getAbsObject( SL0Vocabulary.RESULT_ACTION ));
				//ac.getAbsObject( SL0Vocabulary.RESULT_ACTION );
				AbsObject result = ap.getAbsObject( SL0Vocabulary.RESULT_VALUE );
			}else {
				cat.debug("A content is not RESULT.");
			}
		//}catch (OntologyException oe) {
		}catch (CodecException oe) {
		}
		return ap;
	}
	
	public static AbsAgentAction extractAction( ACLMessage acl ) {
		AbsAgentAction action = null;
		try {
			action = (AbsAgentAction) codecSL0.decode(
					BasicOntology.getInstance(),
					acl.getContent() );
			if ( SL0Vocabulary.ACTION.compareToIgnoreCase( action.getTypeName()) == 0 ) {
			}else {
				cat.debug("A content is not ACTION.");
			}
		//}catch (OntologyException oe) {
		}catch (CodecException oe) {
		}
		return action;
	}
	
	public static void fillAbsPredicateToContent( ACLMessage acl, AbsPredicate ap )
	throws CodecException {
		acl.setContent(codecSL0.encode( ap ));
	}

	/**
	 * fills a reply as NOT_UNDERSTOOD.
	 * If any one argument is null, nothing is done.
	 * 
	 * @param original an original message
	 * @param reply    a reply message, which will be modified
	 * @param description a description of a reason in SL0 format
	 */
	public static void fillAsNotUnderstood( ACLMessage original, ACLMessage reply, String description ) {
		if ( null == original || null == reply
		  || null == description ) {
			return;
		}
		reply.setPerformative( ACLMessage.NOT_UNDERSTOOD );
		reply.setContent(
			"( " + SL0Helper.toStringAclAsAction(original) + "\n" +
			description + " )" );
	}
}
