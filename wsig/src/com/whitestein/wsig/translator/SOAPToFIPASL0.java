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

import jade.content.lang.sl.SL0Vocabulary;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.content.lang.sl.SLCodec;
import jade.lang.acl.ACLMessage;
//import jade.content.lang.xml.XMLCodec;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.BasicOntology;
import jade.content.onto.basic.Action;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsTerm;
import jade.content.abs.AbsPrimitive;
import jade.content.abs.AbsAgentAction;
import jade.content.abs.AbsAggregate;
import jade.core.AID;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.FIPAMessage;
import com.whitestein.wsig.fipa.FIPAServiceIdentificator;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.whitestein.wsig.ws.UDDIOperationIdentificator;
import com.whitestein.wsig.ws.WSMessage;
//import com.whitestein.wsigs.struct.OperationID;
//import com.whitestein.wsigs.test.TestSOAPClient;
import javax.xml.soap.SOAPMessage;
//import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Name;
import javax.xml.soap.Text;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.axis.message.PrefixedQName;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;


/**
 * @author jna
 *
 * convertor from SOAP to FIPA SL0 message content.
 *
 */
public class SOAPToFIPASL0 implements Translator {

	private static SLCodec codecSL0 = new SLCodec(0);
	public static String XML_ATTRIBUTES = Configuration.getInstance().getFIPAAttrForXMLAttributes();
	public static String XML_ELEMENT = Configuration.getInstance().getFIPAAttrForXMLElement();
	public static String XML_CONTENT = Configuration.getInstance().getFIPAAttrForXMLContent();
	public static String XML_TAG_ = Configuration.getInstance().getFIPAPrefixForXMLTag();
	private static Category cat = Category.getInstance(SOAPToFIPASL0.class.getName());
	private static Logger log = Logger.getLogger(SOAPToFIPASL0.class.getName());
	public static String NONE = "none";

	/**
	 * converts FIPA SL0 to SOAP.
	 * Takes a SOAP content and translates one into FIPA SL0 structure.
	 * 
	 * @param soapContent content translated
	 * @return FIPA translation
	 */
	public Collection translate( CalledMessage soap ) throws Exception {
		if ( soap == null
			|| !(soap instanceof WSMessage) ) {
			throw new Exception("An input type expected is " + getInputType());
		}
		return translate( (WSMessage) soap );
	}

	/**
	 * converts SOAP to FIPA SL0.
	 * Takes a SOAP content and translates one into FIPA SL0 structure.
	 * 
	 * @param soapContent content translated
	 * @return FIPA translation
	 */
	public Collection translate( WSMessage soap ) throws Exception {
		Collection c = new ArrayList();
		SOAPElement el;
		AbsContentElement ace;
		UDDIOperationIdentificator uddiOpId;
		ServedOperation op;
		FIPAMessage fipa;
		try {
			if ( null == soap.getSOAPMessage() ) {
				// empty response, treat it as inform-done
				ACLMessage acl = new ACLMessage( ACLMessage.INFORM );
				acl.setContent("((DONE " + SOAPToFIPASL0.NONE + "))");
				acl.setLanguage( FIPANames.ContentLanguage.FIPA_SL0 );
				fipa = new FIPAMessage(acl);
				fipa.setResponse( soap.isResponse());
				c.add( fipa );
				return c;
			}
			SOAPBody sb = soap.getSOAPMessage().getSOAPPart().getEnvelope().getBody();
			Iterator i = sb.getChildElements();
			if( i.hasNext() ) {
				// check cases for a SOAP content
				el = (SOAPElement) i.next();
				if ( el instanceof SOAPBodyElement ) {
					if( soap.isResponse() ) {
						//c = translateResponse( soap, actionSL0Requested );

						// a response is translated
						c.add( translateResponse( sb ));
						return c;
					}else {
						c = translateAction( soap );
						return c;
					}
				}else if ( el instanceof SOAPFault ) {
					if ( i.hasNext() ) {
						//create fault: a SOAP format error
						// only one fault element is permited
						return c;
					}
					//c = translateFault( soap, actionSL0Requested );
					
					// quick hack to produce a result
					c.add( translateResponse( sb ));
					return c;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	/**
	 * converts SOAP response into FIPA SL0.
	 * Takes a SOAP content and translates one into FIPA SL0 structure.
	 * An action's field of a RESULT's structure must be inserted from an original request.
	 * 
	 * @param sb SOAP body's content translated
	 * @return FIPA a part of a translation
	 */
	public FIPAMessage translateResponse( SOAPBody sb ) throws Exception {
		ACLMessage acl = new ACLMessage( ACLMessage.INFORM );
		AbsAggregate agg = new AbsAggregate( SL0Vocabulary.SEQUENCE );
		AbsTerm gen = null;
		SOAPElement sEl;
		Iterator i = sb.getChildElements();
		int count = 0;
		while( i.hasNext() ) {
			count ++ ;
			sEl = (SOAPElement) i.next();
			gen = generateTypedAbs( sEl );
			if ( gen != null ) {
				agg.add( gen );
			}
		}
		// to construct a part of the RESULT
		AbsPredicate ap = new AbsPredicate( SL0Vocabulary.RESULT );
		if ( 0 == count || (1 == count && null == gen) ) {
			cat.debug(" translated response is null");
			return null;
		}else if ( 1 == count ) {
			// one XML element is not converted into a SEQUENCE
			ap.set( SL0Vocabulary.RESULT_VALUE, gen );
		}else {
			// more than one are converted into a SEQUENCE
			ap.set( SL0Vocabulary.RESULT_VALUE, agg );
		}
		
		// fill an action's field, but only as null
		ap.set( SL0Vocabulary.RESULT_ACTION, NONE );
		
		try {
			acl.setContent( codecSL0.encode( ap ));
		
			cat.debug( "translated response is " + codecSL0.encode(ap) );
		} catch ( CodecException ce ) {
			log.debug( ce );
			acl.setContent( "( " + NONE + " (error-message \"A translation fails.\"))");
			acl.setPerformative( ACLMessage.FAILURE );
		}
		
		FIPAMessage fipa = new FIPAMessage( acl );
		fipa.setResponse( true );
		return fipa;
	}

	/**
	 * converts SOAP to FIPA SL0.
	 * Takes a SOAP content and translates one into FIPA SL0 structure.
	 * 
	 * @param soapContent content translated
	 * @return FIPA translation
	 */
	public Collection translateAction( WSMessage soap ) throws Exception {
		Collection c = new ArrayList();
		SOAPBodyElement el;
		AbsContentElement ace;
		UDDIOperationIdentificator uddiOpId;
		ServedOperation op;
		FIPAMessage fipa;
		try {
			SOAPBody sb = soap.getSOAPMessage().getSOAPPart().getEnvelope().getBody();
			Iterator i = sb.getChildElements();
			while( i.hasNext() ) {
				el = (SOAPBodyElement) i.next();
				uddiOpId = new UDDIOperationIdentificator(
						soap.getAccessPoint(),
						el.getElementName().getLocalName() );
				op = ServedOperationStore.getInstance().find( uddiOpId );
				if ( null == op ) {
					// failure may be sent back
					log.debug(" no operation for  ap: " + uddiOpId.getAccessPoint() +
						" op: " + uddiOpId.getWSDLOperation());
					continue;
				}
				// other then ACTION messages are also possible, will be added
				// a case is based on an element name
				ace = generateAbsAgentAction(
						el,
						op.getOperationID().getFIPAServiceIdentificator() );
				ACLMessage acl = new ACLMessage( ACLMessage.REQUEST );
				acl.setPerformative( ACLMessage.REQUEST );
				acl.addReceiver(
						op.getOperationID().getFIPAServiceIdentificator().getAgentID());
				acl.setContent(
						codecSL0.encode( ace ));
				fipa = new FIPAMessage(acl);
				fipa.setResponse( false );
				c.add( fipa );
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	/**
	 * gives input type for this translator
	 * 
	 * @return input type
	 */
	public String getInputType() {
		return WSMessage.TYPE;
	}
	
	/**
	 * gives output type for this translator
	 * 
	 * @return output type
	 */
	public String getOutputType() {
		return FIPAMessage.TYPE;
	}
	
	/**
	 * generates FIPA messages from SOAP message
	 * 
	 * @deprecated
	 * 
	 * @param soap message to convert
	 * @return converted SL0 content(s)
	 */
	private Collection createSL0Content( SOAPMessage soap, String accessPoint ) {
		Collection c = new ArrayList();
		SOAPBodyElement el;
		AbsContentElement ace;
		UDDIOperationIdentificator uddiOpId;
		ServedOperation op;
		FIPAMessage fipa;
		try {
			SOAPBody sb = soap.getSOAPPart().getEnvelope().getBody();
			Iterator i = sb.getChildElements();
			while( i.hasNext() ) {
				el = (SOAPBodyElement) i.next();
				uddiOpId = new UDDIOperationIdentificator( accessPoint, el.getElementName().getLocalName() );
				op = ServedOperationStore.getInstance().find( uddiOpId );
				if ( null == op ) {
					continue;
				}
				// other then ACTION messages are also possible, will be added
				ace = generateAbsAgentAction(
						el,
						op.getOperationID().getFIPAServiceIdentificator() );
				fipa = new FIPAMessage();
				fipa.getACLMessage().addReceiver(op.getOperationID().getFIPAServiceIdentificator().getAgentID());
				fipa.getACLMessage().setContent(
						codecSL0.encode( ace ));
				c.add( fipa );
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	/**
	 * converts one SOAP body element
	 * 
	 * @param soap element to convert
	 * @param actor action's actor
	 * @return agent action generated
	 */
	private AbsAgentAction generateAbsAgentAction( SOAPBodyElement soap, FIPAServiceIdentificator fipaId ) {
		AbsAgentAction absAction = null;
		AbsAggregate agg = null;
		SOAPElement sEl;
		AbsTerm gen, term;
		AID actor = fipaId.getAgentID();
		try {
			absAction = (AbsAgentAction) 
				BasicOntology.getInstance().fromObject( new Action() );
			//agg = new AbsAggregate( soap.getElementName().getLocalName());
			agg = new AbsAggregate( fipaId.getServiceName());
			// collect action's arguments
			Iterator i = soap.getChildElements();
			while( i.hasNext() ) {
				sEl = (SOAPElement) i.next();
				gen = generateTypedAbs( sEl );
				if ( gen != null ) {
					agg.add( gen );
				}
			}
			// check for XML attributes
			if ( isWithXMLAttributes(soap) ) {
				term = generateAbsXMLAttributed(
						soap,
						agg );
			}else {
				term = agg;
			}
			AbsConcept absAID = (AbsConcept)
				BasicOntology.getInstance().fromObject( actor );
			absAction.set( SL0Vocabulary.ACTION_ACTOR, absAID );
			absAction.set( SL0Vocabulary.ACTION_ACTION, term );
		}catch (Exception e) {
			e.printStackTrace();
		}
		return absAction;
	}

	/**
	 * creates set of XML's attributes used.
	 * An attribute "N=V" is converted into an SL0 structure (property :name N :value V).
	 * 
	 * @param soap a XML element to be examined
	 * @return a set of attributes
	 */
	private AbsTerm generateAbsXMLAttributes( SOAPElement soap ) {
		Name n;
		AbsConcept property;
		Iterator i = soap.getAllAttributes();
		AbsAggregate aSet = new AbsAggregate( SL0Vocabulary.SET );
		while( i.hasNext()) {
			n = (Name) i.next();
			if ( ! isXMLAttributeIgnored(n) ) {
				property = new AbsConcept( FIPAManagementVocabulary.PROPERTY );
				property.set( FIPAManagementVocabulary.PROPERTY_NAME,
						n.getLocalName());
				property.set( FIPAManagementVocabulary.PROPERTY_VALUE,
						soap.getAttributeValue( n ));
				aSet.add( property );
			}
		}
		return aSet;
	}

	/**
	 * fills an element with XML attributes encoded by fipa AbsObject.
	 * The method is used to include XML's attributes into an XML's element.
	 * A message's format used is generated by generateAbsXMLAttributed method.  
	 * 
	 * @param el an element to be set
	 * @param attr attributes inserted into an element
	 * @param uri uri for a PrefixedQName
	 * @param prefix prefix for a PrefixedQName
	 */
	public static void fillAttributes( SOAPElement el, AbsObject attr, String uri, String prefix ) {
		// a SET is expected
		Name n;
		AbsConcept property;
		AbsAggregate agg = (AbsAggregate) attr;
		String[] name = agg.getNames();
		String attr_name, attr_value;
		for ( int i = 0; i < agg.getCount(); i ++ ) {
			property = (AbsConcept) agg.getAbsObject( name[i] );
			attr_name = property.getString( FIPAManagementVocabulary.PROPERTY_NAME );
			n = new PrefixedQName(uri, attr_name, prefix );
			attr_value = property.getString( FIPAManagementVocabulary.PROPERTY_VALUE );
			try {
				el.addAttribute(n,attr_value);
			}catch( SOAPException se ) {
				cat.debug("An XML's attribute is not set.");
			}
		}
	}

	/**
	 * creates a structure wrapped a plain XML element converted
	 * 
	 * @param soap original XML element
	 * @param a is a structure translated without XML's attributes
	 * @return a wrapped AbsTerm with XML's attributes translated
	 */
	private AbsTerm generateAbsXMLAttributed( SOAPElement soap, AbsTerm a ) {
		AbsConcept xml = new AbsConcept( XML_TAG_ + a.getTypeName());
		xml.set( XML_ATTRIBUTES, generateAbsXMLAttributes(soap));
		xml.set( XML_ELEMENT, a );
		return xml;
	}
	private AbsTerm generateAbsXMLAttributed( String name, SOAPElement soap, AbsTerm a ) {
		AbsConcept xml = new AbsConcept( XML_TAG_ + name );
		xml.set( XML_ATTRIBUTES, generateAbsXMLAttributes(soap));
		AbsAggregate agg = new AbsAggregate( name );
		agg.add( a );
		xml.set( XML_ELEMENT, agg );
		return xml;
	}
	
	private AbsTerm generateTypedAbs( SOAPElement soap ) {
		AbsConcept a;
		AbsTerm absRet;
		AbsTerm absRet2;
		
		if ( soap == null ) {
			log.debug(" soap is null " );
			return null;
		}

		//print out childs
		/*
		Iterator it = soap.getChildElements();
		System.out.println(" " + soap.getElementName().getLocalName() + " value: " + soap.getValue());
		SOAPElement te;
		while ( it.hasNext()) {
			te = (SOAPElement) it.next();
			log.debug("   child: " + te.getElementName().getLocalName() + " value: " + te.getValue());
		}
		*/
		
		String name = soap.getElementName().getLocalName();
		log.debug(" generateTypedAbs for: " + name );

		//AbsPredicate ap = new AbsPredicate( SL0Vocabulary.RESULT );
		//ap.set( SL0Vocabulary.RESULT_ACTION, NONE );
		
		// primitive type
		if ( isLeaf(soap) ) {
			log.debug(" " + name + " is leaf");
			absRet = createPrimitive( soap );
			if ( isWithXMLAttributes(soap) ) {
				absRet2 = generateAbsXMLAttributed( name,
						soap,
						absRet );
			}else {
				absRet2 = absRet;
			}
			if ( null == absRet2 ) {
				log.debug(" null is at primitive : " + name );
			}
			/*
			try {
				ap.set( SL0Vocabulary.RESULT_VALUE, absRet2 );
				codecSL0.encode( ap );
				log.debug( "translated part is " + codecSL0.encode(ap));
			} catch ( CodecException ce ) {
				log.debug( ce );
			}
			 */
		
			return absRet2;
		}

		// System.out.println("  " + name + "    " + soap.getElementName() );
		SOAPElement s, s2;
		AbsTerm gen;
		Iterator i = soap.getChildElements();

		// an aggregate or some child element is not attribute
		if ( FIPASL0ToSOAP.isAggregate(name)
			|| ! isAttributeAllChilds( soap ) ) {
			AbsAggregate agg = new AbsAggregate( name );
			while ( i.hasNext() ) {
				s = (SOAPElement) i.next();
				gen = generateTypedAbs( s );
				if ( gen != null ) {
					agg.add( gen );
				}
			}
			absRet = agg;
		}else {
			// all childs are a FIPA attribute name
			AbsConcept type = new AbsConcept( name );
			String attrName;
			Iterator sub;
			while ( i.hasNext() ) {
				s = (SOAPElement) i.next();
				attrName = s.getElementName().getLocalName();
				
				sub = s.getChildElements();
				s2 = (SOAPElement) sub.next();
				
				gen = generateTypedAbs( s2 );
				if ( gen != null ) {
					type.set( attrName, gen);
				}
			}
			absRet = type;
		}
		// check for XML attributes
		if ( isWithXMLAttributes(soap) ) {
			absRet = generateAbsXMLAttributed( name,
					soap,
					absRet );
		}
		if ( null == absRet ) {
			log.debug(" null is at name: " + name );
		}
		/*
		try {
			ap.set( SL0Vocabulary.RESULT_VALUE, absRet );
			codecSL0.encode( ap );
			cat.debug( "translated part is " + codecSL0.encode(ap));
		} catch ( CodecException ce ) {
			log.debug( ce );
		}
		 */
		return absRet;
	}

	private String[] JADE_TYPE = {
			BasicOntology.STRING,
			BasicOntology.FLOAT,
			BasicOntology.INTEGER,
			BasicOntology.BOOLEAN,
			BasicOntology.DATE,
			BasicOntology.BYTE_SEQUENCE
		};
	
	private AbsTerm createPrimitive( SOAPElement soap ) {
		AbsPrimitive a = AbsPrimitive.wrap("");
		if ( soap == null ) {
			log.debug( "createPrimitive == null" );
			return a;
		}
		String type = soap.getElementName().getLocalName();
		if ( type == null ) {
			// try also a content
			String c = soap.getValue();
			if ( null != c ) {
				a = AbsPrimitive.wrap(BasicOntology.STRING);
				a.set( soap.getValue());
			}
			log.debug( "createPrimitive as string" );
			return a;
		} else {
			log.debug( "createPrimitive for type = " + type );
		}


// FIXME: type must be correctly translated
//        now only JADE_TYPE: BO_Integer, BO_Float, BO_Boolean, BO_String
//        add also default xsd types
/*
		for ( int i = 0; i < JADE_TYPE.length; i ++ ) {
			if ( type.equalsIgnoreCase(JADE_TYPE[i]) ) {
				// a = new AbsPrimitive( JADE_TYPE[i]);
				a = AbsPrimitive.wrap( JADE_TYPE[i] );
				if ( null != soap.getValue() ) {
					a.set(String.trim(soap.getValue()));
				} else {
					a.set("");
				}
				log.debug( "createPrimitive as type " + JADE_TYPE[i] );
				return a;
			}
		}
 */
		String str = soap.getValue();
		if ( null != str ) {
			str = str.trim();
		} else {
			str = "";
		}
		// if ( str.length() > 0 ) {
		  try {
			  a = AbsPrimitive.wrap( Long.parseLong( str ));
			  log.debug( "createPrimitive as BO_Integer " );
			  return a;
		  }catch (NumberFormatException nfe) {
		  }
		  try {
			  a = AbsPrimitive.wrap( Double.parseDouble( str ));
			  log.debug( "createPrimitive as BO_Float" );
			  return a;
		  }catch (NumberFormatException nfe) {
		  }
		  if( "true".equalsIgnoreCase( str )) {
			  log.debug( "createPrimitive as BO_Boolean true" );
			  a = AbsPrimitive.wrap( true );
			  return a;
		  } else if ( "false".equalsIgnoreCase( str )){
			  log.debug( "createPrimitive as BO_Boolean false" );
			  a = AbsPrimitive.wrap( false );
			  return a;
		  }
		// }
		  
		// type not known
		a = AbsPrimitive.wrap(BasicOntology.STRING);
		if ( null != soap.getValue() ) {
			a.set( soap.getValue());
		} else {
			a.set("");
		}
		log.debug( "createPrimitive as default string" );
		return a;
		//AbsAggregate agg = new AbsAggregate( type );
		//agg.add( AbsPrimitive.wrap( soap.getValue()));
		//return agg;
	}
	
	/**
	 * tests, if string is primitive type in FIPA.
	 * The JADE's basic ontology is taken as a refference.
	 * 
	 * @param type string checked
	 * @return
	 */
	private boolean isPrimitive( String type ) {
		for ( int i = 0; i < JADE_TYPE.length; i ++ ) {
			if ( type.equalsIgnoreCase(JADE_TYPE[i]) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * tells if soap element is Leaf in a tree structure
	 *  
	 * @param soap soap element tested
	 * @return true if it is a leaf
	 */
	private boolean isLeaf( SOAPElement soap ) {
		// not null and
		//   has got no childs or
		//   has got childs, which are all a text node
		boolean res = soap != null &&
			isAllChildsText(soap);
		cat.debug(" isLeaf method for: " + soap.getElementName().getLocalName() + " value: " + soap.getValue() + " is leaf: " + res);
		return res;
		//
		//	( ! soap.getChildElements().hasNext()
		//	|| (soap.getValue() != null && soap.getValue().trim().length() > 0) );
	}
	
	/**
	 * cheks, if all childs are Text
	 * If there is no childs, then true is also returned.
	 * 
	 * @param soap tested soap
	 * @return
	 */
	private boolean isAllChildsText(SOAPElement soap) {
		boolean isTextOnly = true;
		Iterator i = soap.getChildElements();
		while ( i.hasNext() ) {
			if( i.next() instanceof Text ) {
				continue;
			}else {
				// it contains non Text element
				isTextOnly = false;
				break;
			}
		}
		return isTextOnly;
	}
	
	private boolean isAttributeAllChilds( SOAPElement soap ) {
		Iterator i = soap.getChildElements();
		SOAPElement el;
		while( i.hasNext() ){
			el = (SOAPElement) i.next();
			if( ! isAttribute( el )) {
				return false;
			}
		}
		return true;
	}

	/**
	 * tells if an element is FIPA attribute.
	 * An element equal to a FIPA attribute has got an xml attribute "fipa-attribute" set to "true".
	 *  
	 * @param soap an element examined
	 * @return true, if it is a FIPA attribute
	 */
	private boolean isAttribute(SOAPElement soap) {
		Iterator i;
		Name n;
		if ( isLeaf( soap )) {
			// a leaf is not an attribute
			return false;
		}
		i = soap.getChildElements();
		i.next();
		if ( i.hasNext() ){
			// more than one value for an attribute is not permitted
			return false;
		}
		i = soap.getAllAttributes();
		while( i.hasNext()) {
			n = (Name) i.next();
			if ( n.getLocalName() != null
					&& n.getLocalName().equalsIgnoreCase( FIPASL0ToSOAP.FIPA_ATTRIBUTE ) ) {
				return soap.getAttributeValue(n) != null
						&& soap.getAttributeValue(n).equalsIgnoreCase("true");
			}
		}
		return false;
	}

	/**
	 * checks if an element has any aditional attribute.
	 * 
	 * @param el an element checked
	 * @return true, if any additional attribute is appeared
	 */
	private boolean isWithXMLAttributes( SOAPElement el ) {
		String ns = "xmlns:" + Configuration.getInstance().getLocalNamespacePrefix();
		Name n;
		Iterator i = el.getAllAttributes();
		while( i.hasNext() ) {
			n = (Name) i.next();
			if ( n.getLocalName() != null
				&& ! isXMLAttributeIgnored(n) ) {
				log.debug(" is with XML attributes, localName = " + n.getLocalName());
				return true;
			}
		}
		return false;
	}

	/**
	 * tells, if an attribute may be ignored.
	 * Ignorable attributes are FIPASL0ToSOAP.FIPA_ATTRIBUTE and "xmlns:tns",
	 *  where a "tns" part is configurable.
	 * To ignore these attributes is for a beautifull SL0 message generated. 
	 * 
	 * @param n an attribute name
	 * @return true if it is ignorable
	 */
	private boolean isXMLAttributeIgnored( Name n ) {
		String ns = "xmlns:" + Configuration.getInstance().getLocalNamespacePrefix();
		return n.getLocalName() != null
				&& ( n.getLocalName().equalsIgnoreCase( FIPASL0ToSOAP.FIPA_ATTRIBUTE )
					 || n.getLocalName().equalsIgnoreCase( ns ) );
	}
		
}
