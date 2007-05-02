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
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.OneShotBehaviour;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;

import java.net.URL;
import java.util.*;

import javax.wsdl.factory.WSDLFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.message.PrefixedQName;

//import com.whitestein.wsigs.struct.CalledMessage;
import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.GatewayAgent;
import com.whitestein.wsig.fipa.*;
import com.whitestein.wsig.struct.OperationID;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.whitestein.wsig.translator.*;
import com.whitestein.wsig.ws.*;
//import com.whitestein.wsigs.Gateway;

/**
 * @author jna
 *
 * tests some parts of a code
 */
public class Test {

	private static final String ACCESS_POINT = "";
	private static MessageFactory mf;
	private static SOAPFactory soapFactory;

	static {
		try {
			mf = MessageFactory.newInstance();
			soapFactory = SOAPFactory.newInstance();
		}catch (SOAPException e) {
			e.printStackTrace();
		}
	}

	private static int conversationId = 0;
	
	private static synchronized String getConversationId() {
		return Integer.toString( conversationId ++ );
	}

	private void setupStore() {
		FIPAServiceIdentificator fipa = new FIPAServiceIdentificator(
				new AID("testAgent001@T20java:1099/JADE",AID.ISGUID), "service01");
			//	new AID("da0",AID.ISLOCALNAME), "service01");
		UDDIOperationIdentificator uddi = new UDDIOperationIdentificator(
				ACCESS_POINT, TestAgent002.OPERATION );
		OperationID id = new OperationID( fipa, uddi );
		FIPAEndPoint ep = new FIPAEndPoint(fipa);//,Gateway.getInstance());
		ServedOperation test = new ServedOperation( id, ep, false );
		WSDLDefinition wsdl = new WSDLDefinition();
		try {
			wsdl.setURL( new URL("http://T20java:8080/test/mywsdl.wsdl"));
			wsdl.setDefinition( WSDLFactory.newInstance().newWSDLReader(
				).readWSDL( "file:///C:/Program%20Files/Apache%20Software%20Foundation/Tomcat%205.0/webapps/test/mywsdl.wsdl" ) );
		}catch ( Exception e ) {
			e.printStackTrace();
		}
		//System.out.println( " QName is: " + wsdl.getDefinition().getQName().toString());
		test.setWSDL( wsdl );
		ServedOperationStore.getInstance().put(test);
	}


	public static ACLMessage generateACLMessage() {
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		//msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
		//msg.setSender( new AID("da0", AID.ISLOCALNAME) );
		msg.setLanguage("fipa-sl0");
		msg.setProtocol("fipa-request");
		msg.setOntology("fipa-agent-management");
		msg.setConversationId( getConversationId() );
		msg.setContent(
			"(                                                                " +
			"                                                                 \n" +
			"	(action                                                       \n" +
			"		(agent-identifier                                         \n" +
			"			:name wsigs@T20java:1099/JADE )                       \n" +
			"		(" + TestAgent002.OPERATION + "                         \n" +
            "  (sometype :age 10 :place \"here\")  )) ))"
	/*		
			"			(df-agent-description                                 \n" +
			"				:name                                             \n" +
			"					(agent-identifier                             \n" +
			"						:name da0@T20java:1099/JADE )             \n" +
			"				:protocols (set \"fipa-request\")                 \n" +
			"				:ontologies (set \"simple-ontology\")             \n" +
			"				:languages (set \"fipa-sl0\" \"XMLCodec\")        \n" +
			"				:services                                         \n" +
			"					(set                                          \n" +
			"						(service-description                      \n" +
			"							:name \"echo\"                        \n" +
			"							:type \"echo-type\"                   \n" +
			"							:ontologies	(set \"simple-ontology\") \n" +
			"							:properties                           \n" +
			"								(set                              \n" +
			"									(property                     \n" +
			"										:name \"description\"     \n" +
			"										:value \"echo service\" ) \n" +
			"									(property                     \n" +
			"										:name \"prop_2\"          \n" +
			"										:value \"value_2\" )))))) \n" +
			"	))                                                              "
			*/
		);
		return msg;
	}

	public static ACLMessage generateACLMessage2() {
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		//msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
		//msg.setSender( new AID("da0", AID.ISLOCALNAME) );
		msg.setLanguage("fipa-sl0");
		msg.setProtocol("fipa-request");
		msg.setOntology("fipa-agent-management");
		msg.setConversationId( getConversationId() );
		msg.setContent(
			"(                                                                " +
			"                                                                 \n" +
			"	(action                                                       \n" +
			"		(agent-identifier                                         \n" +
			"			:name wsigs@T20java:1099/JADE )                       \n" +
			"		(" + TestAgent002.OPERATION + "                         \n" +
            "  (sometype :age 10 :place (town 20 40 TOWER 3 4 5 6 7 8 9 10))  \n" +
			"  (set 0 1 2 3 4 5 6 7 8 9 10)  (sequence 0 1 2 3 4 5 6 7 8 9 10)     )) ))"
		);
		return msg;
	}

	public static ACLMessage generateACLMessage3() {
		ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
		//msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
		//msg.setSender( new AID("da0", AID.ISLOCALNAME) );
		msg.setLanguage("fipa-sl0");
		msg.setProtocol("fipa-request");
		msg.setOntology("fipa-agent-management");
		msg.setConversationId( getConversationId() );
		msg.setContent(
			"(                                                                " +
			"                                                                 \n" +
			"	(action                                                       \n" +
			"		(agent-identifier                                         \n" +
			"			:name wsigs@T20java:1099/JADE )                       \n" +
			"		(" + TestAgent002.OPERATION + "                         \n" +
            "  :x1 (sometype :age 10 :place (town 20 40 TOWER 3 4 5 6 7 8 9 10))  \n" +
			"  :x2 (set 0 1 2 3 4 5 6 7 8 9 10) \n" +
			"  :x3 (sequence 0 1 2 3 4 5 6 7 8 9 10) \n" +
			"  :x4 (xml-tag-bbb :xml-attributes (set (property :name color :value blue))\n" +
			"                 :xml-element (bbb 10))       )) ))"
		);
		// when a tag "b" is used insted "bbb" a parser error is occured
		return msg;
	}

	public static ACLMessage generateFailureACLMessage() {
		ACLMessage msg = new ACLMessage( ACLMessage.FAILURE );
		//msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
		//msg.setSender( new AID("da0", AID.ISLOCALNAME) );
		msg.setLanguage("fipa-sl0");
		msg.setProtocol("fipa-request");
		msg.setOntology("sample ontology");
		msg.setConversationId( getConversationId() );
		msg.setContent(
			"(                                                                " +
			"                                                                 \n" +
			"	(action                                                       \n" +
			"		(agent-identifier                                         \n" +
			"			:name wsigs@T20java:1099/JADE )                       \n" +
			"		(" + TestAgent002.OPERATION + "                         \n" +
			"			\"test\"						                     \n" +
			"			1002						          \n" +
			"		) \n" +
			"	)									 \n" +
			"	(error-message \" Testing fauilure. \")									 \n" +
			"	)                                                              "
		);
		return msg;
	}

	public static ACLMessage generateInformDoneACLMessage() {
		ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
		//msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
		//msg.setSender( new AID("da0", AID.ISLOCALNAME) );
		msg.setLanguage("fipa-sl0");
		msg.setProtocol("fipa-request");
		msg.setOntology("sample ontology");
		msg.setConversationId( getConversationId() );
		msg.setContent(
			"(                                                                \n" +
			"   (done                                                              \n" +
			"		(action                                                       \n" +
			"			(agent-identifier                                         \n" +
			"				:name wsigs@T20java:1099/JADE )                       \n" +
			"			(" + TestAgent002.OPERATION + "                         \n" +
			"				\"test\"				   		                     \n" +
			"				1002										          \n" +
			"			)							 \n" +
			"		)								 \n" +
			"	)									 \n" +
			")                                         "
		);
		return msg;
	}

	public static ACLMessage generateInformResultACLMessage() {
		ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
		//msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
		//msg.setSender( new AID("da0", AID.ISLOCALNAME) );
		msg.setLanguage("fipa-sl0");
		msg.setProtocol("fipa-request");
		msg.setOntology("sample ontology");
		msg.setConversationId( getConversationId() );
		msg.setContent(
			"(                                                               \n" +
			"   (result                                                              \n" +
			"		(action                                                       \n" +
			"			(agent-identifier                                         \n" +
			"				:name wsigs@T20java:1099/JADE )                       \n" +
			"			(" + TestAgent002.OPERATION + "                         \n" +
			"				\"test\"				   		                     \n" +
			"				1002										          \n" +
			"			)							 \n" +
			"		)								 \n" +
			"		\"Result is 234.\"								 \n" +
			"	)									 \n" +
			")                                         "
		);
		return msg;
	}
	
	public WSMessage generateSOAPRequest() {
		SOAPMessage soap = null;
		if ( mf == null ) {
			// new Message( a );
			System.out.println("Test SOAP client has got a null MessageFactory.");
			return null;
		}
		try {
			soap = mf.createMessage();

			String prefix = Configuration.getInstance().getLocalNamespacePrefix();
			SOAPBody sb = soap.getSOAPPart().getEnvelope().getBody();
			
			// the root AbsObject is SOAP body element 
			String uri = "http://t20java:2222/WSDL/001";
			PrefixedQName n = new PrefixedQName( uri, TestAgent002.OPERATION, prefix);
			SOAPBodyElement sbe = sb.addBodyElement(n);
			n = new PrefixedQName( uri, "color", prefix);
			sbe.addAttribute( n, "blue");
			
			// add elemnts into the body element
			SOAPElement el, stringElement;
			
			n = new PrefixedQName( uri, FIPASL0ToSOAP.FIPA_ATTRIBUTE, prefix);
			el = soapFactory.createElement( "name", prefix, uri );
			el.addAttribute( n, "true");
			// el.removeNamespaceDeclaration( prefix );
			sbe.addChildElement(el);
			
			// set content to a leaf element node
			stringElement = soapFactory.createElement( "BO_String", prefix, uri );
			stringElement.addTextNode("da0@T20java:1099/JADE");
			el.addChildElement( stringElement );
			
			//System.out.println("Test SOAP client generates a SOAP: ");
			//soap.writeTo( System.out );
			//System.out.println("\n  End of the Test SOAP client's SOAP.");
			
		}catch ( Exception e ) {
			e.printStackTrace();
		}

		WSMessage wsMsg = new WSMessage( soap );
		wsMsg.setAccessPoint(ACCESS_POINT);
		return wsMsg;
	}

	
	private void test3() {
		//test 1.a
		GatewayAgent.getInstance().addBehaviour(
				new OneShotBehaviour( GatewayAgent.getInstance() ) {
			public void action() {
				try {
					Thread.sleep(1000);
				}catch ( InterruptedException ie ) {
				}
				ACLMessage msg = new ACLMessage( ACLMessage.REQUEST );
				msg.addReceiver( new AID("wsigs", AID.ISLOCALNAME));
				msg.setSender( new AID("da0", AID.ISLOCALNAME) );
				msg.setLanguage("fipa-sl0");
				msg.setProtocol("fipa-request");
				msg.setOntology("fipa-agent-management");
				msg.setConversationId( getConversationId() );
				msg.setContent(
					"(                                                                " +
					"                                                                 \n" +
					"	(action                                                       \n" +
					"		(agent-identifier                                         \n" +
					"			:name wsigs@T20java:1099/JADE )                       \n" +
					"		(register                                                 \n" +
					"			(df-agent-description                                 \n" +
					"				:name                                             \n" +
					"					(agent-identifier                             \n" +
					"						:name da0@T20java:1099/JADE )             \n" +
					"				:protocols (set \"fipa-request\")                 \n" +
					"				:ontologies (set \"simple-ontology\")             \n" +
					"				:languages (set \"fipa-sl0\" \"XMLCodec\")        \n" +
					"				:services                                         \n" +
					"					(set                                          \n" +
					"						(service-description                      \n" +
					"							:name \"echo\"                        \n" +
					"							:type \"echo-type\"                   \n" +
					"							:ontologies	(set \"simple-ontology\") \n" +
					"							:properties                           \n" +
					"								(set                              \n" +
					"									(property                     \n" +
					"										:name \"description\"     \n" +
					"										:value \"echo service\" ) \n" +
					"									(property                     \n" +
					"										:name \"prop_2\"          \n" +
					"										:value \"value_2\" )))))) \n" +
					"	))                                                              "
				);
				//System.out.println( msg.getContent());
				//send(msg);
			}
		});

		
		
		//test 1.b
		GatewayAgent.getInstance().addBehaviour(
				new OneShotBehaviour( GatewayAgent.getInstance() ) {
			public void action() {
				ACLMessage msg = myAgent.receive();
				if ( msg != null ) {
					try {
						com.whitestein.wsig.fipa.FIPAMessage fipa =
							new com.whitestein.wsig.fipa.FIPAMessage( msg );
						FIPASL0ToSOAP tr = new FIPASL0ToSOAP();
						//tr.translate( fipa ).getMessage().writeTo(System.out);
						
						com.whitestein.wsig.fipa.FIPAMessage fipa2;
						WSMessage soap;
						FIPASL0ToSOAP fipa2soap = new FIPASL0ToSOAP();
						SOAPToFIPASL0 soap2fipa = new SOAPToFIPASL0();
							System.out.println("FIPA: ");
							System.out.println(fipa.getACLMessage().getContent());
							System.out.println(" -> FIPA codecSL0 decode: ");
							SLCodec codecSL0 = new SLCodec(0);
							System.out.println( codecSL0.encode(
									GatewayAgent.getInstance().getContentManager().extractAbsContent(
											fipa.getACLMessage() )));
							soap = (WSMessage) fipa2soap.translate( fipa ).iterator().next();
							System.out.println(" -> SOAP: ");
							soap.getSOAPMessage().writeTo(System.out);
							fipa2 = (FIPAMessage) soap2fipa.translate( soap ).iterator().next();
							System.out.println("");
							System.out.println(" -> FIPA: ");
							System.out.println(fipa2.getACLMessage().getContent());
						
						
						//System.out.println( "" + tr.translate( fipa ).getMessage() );
						/*
						//ObjectOutputStream oos = new ObjectOutputStream( System.out );
						ACLMessage m = new ACLMessage( ACLMessage.INFORM );
						m.setLanguage( XMLCodec.NAME );
						m.setOntology( msg.getOntology());
						AbsContentElement c = myAgent.getContentManager().extractAbsContent( msg );
						System.out.println( print( "", c ));
						// c.getAbsObject("action").getAbsObject("description").getAbsObject("languages");
						//XMLCodec xmlCodec = (XMLCodec) myAgent.getContentManager().lookupLanguage( XMLCodec.NAME );
						//String xmlContent = xmlCodec.encode( c );
						//System.out.println(" XML msg content is : " + xmlContent );
						myAgent.getContentManager().fillContent(m,c);
						System.out.println(" XML msg content : " + m.getContent());
						*/
						//oos.writeObject( c );
						//AbsContentElement absC = myAgent.getContentManager().extractAbsContent( msg );
						//System.out.println(" msg absContent is : " + absC );
						//oos.writeObject( absC );
					}catch ( Exception e ) {
						e.printStackTrace();
					}
				}else {
					block();
				}
			}
		});
	}

	private String print( String shift, AbsObject o ) {
		String str = shift + "Abstract object type=" + o.getTypeName();
		if ( o.getCount() < 1 ) {
			str += " \n";
			return str;
		}
		str += " attributes= \n";
		String names[] = o.getNames();
		for ( int i = 0; i < o.getCount(); i ++ ) {
			str += shift + "  " + names[i] + ": " + print( shift + "    ", o.getAbsObject(names[i]) );
		}
		return str;
	}

	private static void addOperation() {
		FIPAServiceIdentificator fipa = new FIPAServiceIdentificator(
				new AID("agent01@T20JAVA:1099/JADE",AID.ISGUID), "service01");
		UDDIOperationIdentificator uddi = new UDDIOperationIdentificator(
				ACCESS_POINT, TestAgent002.OPERATION );
		OperationID id = new OperationID( fipa, uddi );
		FIPAEndPoint ep = new FIPAEndPoint(fipa);//,Gateway.getInstance());
		ServedOperation test = new ServedOperation( id, ep, false );
		ServedOperationStore.getInstance().put(test);
		System.out.println(" into aStore ap: " + uddi.getAccessPoint() +
				" op: " + uddi.getWSDLOperation());
	}
	
	private static void translate( FIPAMessage fipa ) {
		if ( null == fipa ) {
			System.out.println(" fipa == null ");
			return;
		}
		if ( null == fipa.getACLMessage() ) {
			System.out.println(" fipa.getACLMessage() == null ");
			return;
		}
		FIPASL0ToSOAP fipa2soap = new FIPASL0ToSOAP();
		SLCodec codec = new SLCodec(0);
		try {
			System.out.println("FIPA: ");
			AbsObject a = AbsHelper.externaliseACLMessage( fipa.getACLMessage(), BasicOntology.getInstance());
			AbsContentElementList aList = new AbsContentElementList();
			aList.add( (AbsContentElement) a );
			System.out.println( codec.encode(aList));
			WSMessage ws = (WSMessage) fipa2soap.translate( fipa ).iterator().next();
			System.out.println(" -> SOAP: ");
			ws.getSOAPMessage().writeTo(System.out);
			System.out.println("");
		}catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static void translate( WSMessage wsMsg ) {
		if ( null == wsMsg ) {
			System.out.println(" WSMessage == null ");
			return;
		}
		SOAPToFIPASL0 soap2fipa = new SOAPToFIPASL0();
		SLCodec codec = new SLCodec(0);
		try {
			System.out.println("SOAP: ");
			System.out.println(" AccessPoint: " + wsMsg.getAccessPoint());
			wsMsg.getSOAPMessage().writeTo(System.out);
			System.out.println("\n -> FIPA: ");
			FIPAMessage fipa = (FIPAMessage) soap2fipa.translate( wsMsg ).iterator().next();
			AbsObject a = AbsHelper.externaliseACLMessage( fipa.getACLMessage(), BasicOntology.getInstance());
			AbsContentElementList aList = new AbsContentElementList();
			aList.add( (AbsContentElement) a );
			System.out.println( codec.encode(aList));
		}catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private void doTests() {
		Object o;
		Collection col = new ArrayList();
		col.add( new FIPAMessage( generateACLMessage()));
		col.add( new FIPAMessage( generateACLMessage2()));
		col.add( new FIPAMessage( generateFailureACLMessage()));
		col.add( new FIPAMessage( generateInformDoneACLMessage()));
		col.add( new FIPAMessage( generateInformResultACLMessage()));
		col.add( generateSOAPRequest());
		col.add( new FIPAMessage( generateACLMessage3()));
		FIPAMessage fipa = null;
		for ( Iterator i = col.iterator(); i.hasNext(); ) {
			o = i.next();
			if ( o instanceof FIPAMessage ) {
				translate( (FIPAMessage) o );
			}else if ( o instanceof WSMessage ) {
				translate( (WSMessage) o );
			}
		}
		
	}
	
	public static void main(String[] args) {
		// aTest();
		Test test = new Test();
		addOperation();
		test.doTests();
	}
	
	private static void aTest() {
		Test test = new Test();
		FIPAMessage fipa = new FIPAMessage( generateACLMessage());
		fipa = new FIPAMessage( generateFailureACLMessage());
		FIPAMessage fipa2;
		WSMessage soap;
		FIPASL0ToSOAP fipa2soap = new FIPASL0ToSOAP();
		SOAPToFIPASL0 soap2fipa = new SOAPToFIPASL0();
		addOperation();
		try {
			System.out.println("FIPA: ");
			System.out.println(fipa.getACLMessage().getContent());
			soap = (WSMessage) fipa2soap.translate( fipa ).iterator().next();
			System.out.println(" -> SOAP: ");
			soap.getSOAPMessage().writeTo(System.out);
			soap.setAccessPoint( ACCESS_POINT );
			fipa2 = (FIPAMessage) soap2fipa.translate( soap ).iterator().next();
			System.out.println("");
			System.out.println(" -> FIPA: ");
			System.out.println(fipa2.getACLMessage().getContent());
		}catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
