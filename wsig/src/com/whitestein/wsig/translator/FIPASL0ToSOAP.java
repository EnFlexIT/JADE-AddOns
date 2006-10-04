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
package com.whitestein.wsig.translator;

import jade.content.lang.sl.SLCodec;
import jade.content.lang.Codec.CodecException;
import jade.lang.acl.ACLMessage;
import jade.content.abs.AbsContentElement;
//import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;
import jade.content.abs.AbsAggregate;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.onto.BasicOntology;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.FIPAMessage;
import com.whitestein.wsig.fipa.SL0Helper;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.ws.WSMessage;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPElement;
import org.apache.axis.message.PrefixedQName;
import org.apache.log4j.Category;

import jade.core.AID;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * @author jna
 *
 * convertor from FIPA SL0 to SOAP message content.
 *
 */
public class FIPASL0ToSOAP implements Translator {


	/**
	 * XML attribute for a FIPA attribute.
	 * A FIPA attribute name is transformed into an element with the attribute.
	 */
	public static final String FIPA_ATTRIBUTE = "fipa_attribute";
	private static final String UNNAMED = "_JADE.UNNAMED";
	
	private static SLCodec codecSL0 = new SLCodec(0);
	private static MessageFactory mf; // SOAP v1.1
	private static SOAPFactory soapFactory;
	private static Category cat = Category.getInstance(FIPASL0ToSOAP.class.getName());

	static {
		try {
			mf = MessageFactory.newInstance();
			soapFactory = SOAPFactory.newInstance();
		}catch (SOAPException e) {
			cat.error(e);
		}
	}
	
	/**
	 * converts FIPA SL0 to SOAP.
	 * Takes a FIPA SL0 content and translates one into SOAP structure.
	 * The types of messages are checked.
	 * 
	 * @param fipa message translated
	 * @return SOAP translation
	 */
	public Collection translate( CalledMessage fipa ) throws Exception{
		if ( fipa == null
			|| !(fipa instanceof FIPAMessage) ) {
			throw new Exception("An expected input type is " + getInputType());
		}
		return translate( (FIPAMessage) fipa );
	}
	
	/**
	 * gives input type for this translator
	 * 
	 * @return input type
	 */
	public String getInputType() {
		return FIPAMessage.TYPE;
	}
	
	/**
	 * gives output type for this translator
	 * 
	 * @return output type
	 */
	public String getOutputType() {
		return WSMessage.TYPE;
	}
	
	/**
	 * converts FIPA SL0 to SOAP.
	 * Takes a SOAP content and translates one into FIPA SL0 structure.
	 * 
	 * @param fipa message translated
	 * @return SOAP translation
	 */
	public Collection translate( FIPAMessage fipa ) throws Exception {
		cat.debug(" a translator's input: " + SL0Helper.toString( fipa.getACLMessage()));
		AbsContentElement ac = null;
		AbsObject action = null;
		//String uri = "http://T20java:8080/myWSDL";
		SOAPMessage soap = null;
		soap = createFaultSOAP("Receiver","Operation is not able to translate.", fipa);
		try {
			ac = codecSL0.decode(
					BasicOntology.getInstance(),
					fipa.getACLMessage().getContent() );
			switch ( fipa.getACLMessage().getPerformative() ) {
				case ACLMessage.REQUEST:
					if ( SL0Vocabulary.ACTION.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
							action = getActionSlot( ac );
							soap = createActionSOAP( action, fipa);
					}
					break;
				case ACLMessage.CANCEL:
					// do not send as SOAP, only close connection
					soap = null;
					break;
				case ACLMessage.FAILURE:
					// a tuple: an action and a proposition
					if ( ac.getCount() == 2 ) {
						//String[] str = ac.getNames();
						//AbsObject a = ac.getAbsObject( str[1]);
						soap = createFaultSOAP("Sender","Operation failure.", fipa);
 					}
					break;
				case ACLMessage.REFUSE:
					// a tuple: an action and a proposition
					if ( ac.getCount() == 2 ) {
						//String[] str = ac.getNames();
						//AbsObject a = ac.getAbsObject( str[1]);
						soap = createFaultSOAP("Sender","Operation refused.", fipa);
 					}
					break;
				case ACLMessage.INFORM:
					// a proposition of following forms:
					//   ( result action value )
					//  or  ( done action )
					if ( SL0Vocabulary.DONE.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
						//action = getActionSlot(ac.getAbsObject( SL0Vocabulary.DONE_ACTION ));
						//soap = createResultSOAP(
						//		action,
						//		AbsPrimitive.wrap(true),
						//		fipa );
						soap = null;  // inform-done only produces "204, No content" message
					}else if ( SL0Vocabulary.RESULT.compareToIgnoreCase( ac.getTypeName()) == 0 ) {
						action = getActionSlot(ac.getAbsObject( SL0Vocabulary.RESULT_ACTION ));
						//ac.getAbsObject( SL0Vocabulary.RESULT_ACTION );
						AbsObject result = ac.getAbsObject( SL0Vocabulary.RESULT_VALUE );
						soap = createResultSOAP( action, result, fipa );
					}
					break;
				case ACLMessage.NOT_UNDERSTOOD:
					// a tuple: an action/event and a explanation
					soap = createFaultSOAP("Client","Operation is not understood.", fipa);
					break;
			}
		}catch ( CodecException ce ) {
			cat.error(ce);
		}catch (SOAPException se ) {
			throw new Exception( se );
		}catch (Exception e ) {
			throw e;
		}

		WSMessage wsMsg;
		Collection col = new ArrayList(); // an empty collection as a default value returned
		if ( soap != null ) {
			wsMsg = new WSMessage( soap );
			wsMsg.setResponse( fipa.isResponse());
			col.add( wsMsg ); 
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				soap.writeTo(baos);
				cat.debug(" a translator's output: " + baos.toString());
			}catch (Exception e ){
				cat.error(e);
			}
		}else {
			cat.debug(" a translator's output: null");
		}
		
		return col;
	}
	
	
	/**
	 * generates SOAP message for a FIPA action-action.
	 * 
	 * @param a a abstract object of a action attribute of a FIPA action
	 * @param fipa an auxiliary original FIPA message
	 * @return created SOAP Message
	 * @throws SOAPException if some problems are arrised with SOAP elements generation and manipullation
	 */
	private SOAPMessage createActionSOAP( AbsObject a, FIPAMessage fipa ) throws SOAPException, Exception {
		if ( mf == null ) {
			// new Message( a );
		}
		SOAPMessage msg = mf.createMessage();
		if ( a == null ) {
			return msg;
		}

		String nsURI = extractNamespaceURI( fipa ); // from a wsdl
		String prefix = Configuration.getInstance().getLocalNamespacePrefix();
		SOAPBody sb = msg.getSOAPPart().getEnvelope().getBody();
		SOAPElement env = msg.getSOAPPart().getEnvelope();
		env.addNamespaceDeclaration("xsi","http://www.w3.org/1999/XMLSchema-instance");
		env.addNamespaceDeclaration("xsd","http://www.w3.org/1999/XMLSchema");

		String wsOperationName = a.getTypeName();
		try {
			wsOperationName = fipa.getServedOperation().getOperationID(
					).getUDDIOperationIdentificator().getWSDLOperation();
		}catch (NullPointerException npe) {
			cat.debug(npe);
			throw new Exception("An operation " + a.getTypeName() + " is not registered.");
		}
		// prepare an operation in a SOAP's body
		//  for the generateXML() method
		PrefixedQName n;
		n = new PrefixedQName( nsURI, wsOperationName, prefix);
		sb.addBodyElement(n);
		
		// in case a operation is XML-attributed,
		//  there is also added a ServedOperation for a XML-attributed in ServedOperationStore
		//  consult also a WSDL

		// JADE uses "_JADE.UNNAMEDn" for anonymouse FIPA attribute names
		// which are consequently translated into XML element's name
		// A plain string content is better for a translation than an AbsObject representation.
		
		generateXML( sb, a, prefix, nsURI );
	
		/*
		// the root AbsObject is SOAP body element 
		PrefixedQName n = new PrefixedQName( nsURI, a.getTypeName(), prefix);
		SOAPBodyElement sbe = sb.addBodyElement(n);
		
		// collect all childs as parameters of the operation
		String[] name = a.getNames();
		SOAPElement soapEl;
		for(int i = 0; i < a.getCount(); i ++ ) {
			try {
				soapEl = generateXML( null, a.getAbsObject( name[i] ), prefix, nsURI );
				if ( soapEl != null ) {
					sbe.addChildElement( soapEl );
				}
			}catch (SOAPException e) {
				// Both setObjectValue and xml are never used in MessageElement for addChildElement().
				// A generateXML()'s SOAPException is treated as a null element generation.
			}
		}
		*/

		return msg;
	}

	/**
	 * generates XML from JADE abstract object.
	 * If ao is null or an alement creation problem is arrised then the returned value is null.
	 * "_JADE.UNNAMEDx" attributes are left and an order of parameters is based on the "x" appended as a number. 
	 * 
	 * @param body if not null, then it is used for new XML element genarating
	 * @param ao abstract object
	 * @param xmlURI XML namespace for the XML generated
	 * @return XML document
	 */
	private SOAPElement generateXML( SOAPBody body, AbsObject ao, String prefix, String xmlURI ) throws SOAPException {
		return generateXML( body, ao, prefix, xmlURI, null );
	}

	private SOAPElement generateXML( SOAPBody body, AbsObject ao, String prefix, String xmlURI, SOAPElement prevElement ) throws SOAPException {
		cat.debug(this.getClass().getName() + ".generateXML(...) enters.");
		SOAPElement el, el2;
		if ( ao == null ) {
			return null;
		}

		String typeName = ao.getTypeName();
		cat.debug(" for type " + typeName );

		// to treat with XML attributes
		AbsObject absAttributes = null;
		if ( typeName.startsWith(SOAPToFIPASL0.XML_TAG_) ) {
			// old version
			absAttributes = ao.getAbsObject( SOAPToFIPASL0.XML_ATTRIBUTES );
			ao = ao.getAbsObject( SOAPToFIPASL0.XML_ELEMENT );
			typeName = ao.getTypeName();
		}else if ( isXMLAttributed( ao ) ) {
			// an attribute is occured
			absAttributes = ao.getAbsObject( SOAPToFIPASL0.XML_ATTRIBUTES );
			// shift on a content
			ao = ao.getAbsObject( SOAPToFIPASL0.XML_CONTENT );
		}
		
		boolean operationLevel = false;
		PrefixedQName n;
		// a SOAPException throwed is traversed into a caller
		if ( body != null ) {
			if ( body.getChildElements().hasNext() ) {
				// is called by request
				el = (SOAPElement) body.getChildElements().next();
				operationLevel = true;
			}else {
				// a new element is a direct child of a SOAPBody 
				n = new PrefixedQName( xmlURI, typeName, prefix);
				el = (SOAPElement) body.addBodyElement(n);
			}
		}else {
			el = soapFactory.createElement( typeName, prefix, xmlURI );
		}
		
		if ( absAttributes != null ) {
			SOAPToFIPASL0.fillAttributes( el, absAttributes, xmlURI, prefix );
			// take a name again after a SOAP's element initialization
			//   in case the ao is XML attributed
			typeName = ao.getTypeName();
		}
		
		// set content to a leaf element node
		if ( isLeaf( ao )) {
			try {
				if ( ! operationLevel ) {
					if( ao instanceof AbsPrimitive ) {
						cat.debug( "" + ao.getTypeName() + " is AbsPrimitive. " );
						String str = asString( (AbsPrimitive) ao );
						// String str = ((AbsPrimitive) ao ).getObject().toString();
						if ( null != prevElement ) {
							// primitive value is a text node of parent's element
							prevElement.addTextNode( str );
							return null;
						} else {
							// not happened
							el.addTextNode( str );
							cat.debug( " previous element is null for AbsPrimitive " );
						}
					} else {
						cat.debug( "" + ao.getTypeName() + " is not AbsPrimitive. " );

						// none args to add
						// el is already created
					}
				}
			}catch ( SOAPException e ) {
				cat.error(e);
			}
			return el;
		}
		
		// no leaf node, generate an XML tree recursivelly

		boolean withUnnamed = isAggregate( typeName ) || isWithUnnamed(ao);
		String[] name = ao.getNames();
		SOAPElement gen;
		for(int i = 0; i < ao.getCount(); i ++ ) {
			try {
				if ( ! withUnnamed ) {
					// name are attributes
					n = new PrefixedQName( xmlURI, FIPA_ATTRIBUTE, prefix);
					el2 = soapFactory.createElement(
							new PrefixedQName(xmlURI, name[i], prefix) );
					//el2 = soapFactory.createElement( name[i], prefix, xmlURI );
					el2.addAttribute( n, "true");
					gen = generateXML( null, ao.getAbsObject( name[i] ), prefix, xmlURI, el2 );
					if ( gen != null ) {
						el2.addChildElement(gen);
					}
				}else {
					// unnamed slot is left in aggregate case
					//el2 = generateXML( null, ao.getAbsObject( name[i] ), prefix, xmlURI );
					// ordering information is in a number appended
					el2 = generateXML( null, ao.getAbsObject( UNNAMED+i ), prefix, xmlURI, el );
				}
				if ( el2 != null ) {
					el.addChildElement( el2 );
				}
			}catch (SOAPException e) {
				// some problems are in element
				cat.error(e);
			}
		}
		
		return el;
	}
	
	/**
	 * creates SOAP fault message
	 * 
	 * @param faultCode SOAP fault code
	 * @param faultReason SOAP fault reason
	 * @param fipa an auxiliary original FIPA message
	 * @return a SOAP Fault message 
	 * @throws SOAPException when a SOAP creation problem is occured
	 */
	private SOAPMessage createFaultSOAP( String faultCode, String faultReason, FIPAMessage fipa ) throws SOAPException {
		if ( mf == null ) {
			// new Message( a );
		}
		SOAPMessage msg = mf.createMessage();

		SOAPBody sb = msg.getSOAPPart().getEnvelope().getBody();
		
		// String prefix = Configuration.getInstance().getLocalNamespacePrefix();
		// String nsUri = "http://T20java:8080/myWSDL";
		// msg.getSOAPPart().getEnvelope().addNamespaceDeclaration(prefix, nsUri);
		
		// the root AbsObject is SOAP body element 
		SOAPFault fault = sb.addFault();

		//PrefixedQName n;
		//n = new PrefixedQName( nsUri, faultCode, prefix);
		
		fault.setFaultCode( faultCode );
		fault.setFaultString( faultReason );

		return msg;
	}
	
	/**
	 * creates SOAP result message
	 * 
	 * @param action result's action-action part
	 * @param result result value
	 * @param fipa an auxiliary original FIPA message
	 * @return a SOAP Fault message 
	 * @throws SOAPException when a SOAP creation problem is occured
	 */
	private SOAPMessage createResultSOAP( AbsObject action, AbsObject result, FIPAMessage fipa ) throws SOAPException {
		SOAPMessage msg = mf.createMessage();
		SOAPBody sb = msg.getSOAPPart().getEnvelope().getBody();

		String nsURI = extractNamespaceURI( fipa );
		String prefix = Configuration.getInstance().getLocalNamespacePrefix();
		
		// collect all childs of the result
		generateXML( sb, result, prefix, nsURI );
	
		return msg;
	}


	private String[] TYPE = {
		BasicOntology.STRING,
		BasicOntology.FLOAT,
		BasicOntology.INTEGER,
		BasicOntology.BOOLEAN,
		BasicOntology.DATE,
		BasicOntology.BYTE_SEQUENCE
	};

	/**
	 * converts primitive types in FIPA into a java's string.
	 * The JADE's basic ontology is taken as a refference.
	 *
	 * @param ap an abstract primitive
	 * @return a string java representation
	 */
	private String asString( AbsPrimitive ap ) {
		String type = ap.getTypeName();
		if( BasicOntology.STRING.equals( type ) ) {
			return ap.getString();
		}
		if( BasicOntology.FLOAT.equals( type ) ) {
			return (new Double( ap.getDouble())).toString();  // wider type
		}
		if( BasicOntology.INTEGER.equals( type ) ) {
			return (new Long(ap.getLong())).toString();  // wider type
		}
		if( BasicOntology.BOOLEAN.equals( type ) ) {
			return (new Boolean(ap.getBoolean())).toString();
		}
		if( BasicOntology.DATE.equals( type ) ) {
			SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-ddTHH:mm:ssZ");
			return df.format( ap.getDate());
		}

		// FIXME: correctness of byte sequence's encoding must be checked
		if( BasicOntology.BYTE_SEQUENCE.equals( type ) ) {
			return new String(ap.getByteSequence() );
		}
		return ap.toString();
	}

	/**
	 * tells if AbsObject is Leaf in a tree structure
	 *  
	 * @param ao abstract object tested
	 * @return true if it is a leaf
	 */
	private boolean isLeaf( AbsObject ao ) {
		// cat.debug( "isLeaf( " + ao.getTypeName() + " ) -> 0 == " + ao.getCount() );
		return ao.getCount() == 0;
	}
	
	/**
	 * gives an action slot for action abstract object
	 * 
	 * @param a abstract object, type of action
	 * @return value of the action slot
	 */
	public static AbsObject getActionSlot( AbsObject a ) {
		if ( a != null && a.getCount() > 0 ) {
			return a.getAbsObject( SL0Vocabulary.ACTION_ACTION );
		}else {
			return null;
		}
	}
	
	/**
	 * gives AID for action
	 * 
	 * @param a is action
	 * @return action_actor AID stored
	 */
	private AID getActorSlotAID( AbsObject a ) {
		if ( a != null && a.getCount() > 0 ) {
			AbsObject agent = a.getAbsObject(
					SL0Vocabulary.ACTION_ACTOR ).getAbsObject( SL0Vocabulary.AID_NAME );
			return new AID( agent.toString(), AID.ISGUID );
		}else {
			return null;
		}
	}
	
	/**
	 * tells if name is aggregation type
	 * All known aggregate types are SET and SEQUENCE in FIPA SL.
	 * 
	 * @param name name of element
	 * @return true if name is aggregate
	 */
	public static boolean isAggregate( String name ) {
		return name != null &&
			( SL0Vocabulary.SET.compareToIgnoreCase( name ) == 0
			  || SL0Vocabulary.SEQUENCE.compareToIgnoreCase( name ) == 0 );
	}
	
	/**
	 * tells if a structure has got unnamed parameters.
	 * Unnamed parameters has got a name in a format "_JADE.UNNAMEDx"
	 * where x is an increasing non-negative integer. 
	 * @param ao a structure tested
	 * @return if parameters are unnamed 
	 */
	public static boolean isWithUnnamed( AbsObject ao ) {
		return ao.getCount() > 0 &&
			ao.getNames()[0].startsWith( UNNAMED );
	}
	
	/**
	 * extracts a namespace uri from message
	 * 
	 * @param fipa a message
	 * @return a extracted URI
	 */
	private String extractNamespaceURI( FIPAMessage fipa ) {
		String nsURI;
		try {
			nsURI = fipa.getServedOperation().getWSDL().getDefinition(
				).getQName().getNamespaceURI();
		}catch ( NullPointerException e ) {
			// e.printStackTrace();
			// throw new SOAPException( "A namespace resolving problem." );
			nsURI = "empty";
		}
		return nsURI;
	}

	/**
	 * checks, if an abstract object encloses XML's attributes.
	 * Attributes are "SOAPToFIPASL0.XML_ATTRIBUTES" and "SOAPToFIPASL0.XML_CONTENT".
	 *
	 * @param ao an abstract object tested
	 * @return true, if ao contains an XML's attribute
	 */
	private boolean isXMLAttributed( AbsObject ao ) {
		String[] attr = ao.getNames();
		for ( int i = 0; i < ao.getCount(); i ++ ) {
			if ( SOAPToFIPASL0.XML_ATTRIBUTES.equals( attr[i] )
			  || SOAPToFIPASL0.XML_CONTENT.equals( attr[i] ) ) {
				// one of XML fipa's attributes are occured
				return true;
			}
		}
		return false;
	}

}
