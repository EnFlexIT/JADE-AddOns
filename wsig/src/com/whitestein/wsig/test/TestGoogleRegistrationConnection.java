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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.io.*;

import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.soap.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.*;
import javax.wsdl.xml.*;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.namespace.*;

import org.apache.log4j.Logger;

import com.whitestein.wsig.net.Connection;
import com.whitestein.wsig.net.SOAPHandler;
import com.whitestein.wsig.net.TimeoutWatcher;
import com.whitestein.wsig.ws.WSDLDefinition;
import com.whitestein.wsig.ws.WSMessage;

/**
 * @author jna
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestGoogleRegistrationConnection extends Connection {

	private Logger log = Logger.getLogger(TestGoogleRegistrationConnection.class.getName());
	private static final String BASE_LOCATION = "/";
	private static final String WSDL_LOCATION =  BASE_LOCATION + "GoogleSearch.wsdl";
	private String wsdlTargetNamespace = "missing";
	private String accessPoint = "none";
	public static final String OP_1 = "echo";
	

	/**
	 * returns a URI for an access point
	 */
	public URI getAccessPoint() {
		URI uri = null;
	    if ( null != this.httpServer ) {
	        String hostName = this.httpServer.getHostName();
	        int port = this.httpServer.getServerSocket().getLocalPort();
	        try {
	            uri = new URI( "http://"+hostName+":"+port+BASE_LOCATION );
	        }catch ( URISyntaxException e ) {
	            uri = null;
	        }
	    }
	    return uri;
	}
	
	/**
	 * returns a WSDL's location
	 * 
	 * @return a url location or null if an error is occured through a URL parsing
	 */
	public URL getWSDLLocation() {
	    URL url = null;
	    if ( null != this.httpServer ) {
	        String hostName = this.httpServer.getHostName();
	        int port = this.httpServer.getServerSocket().getLocalPort();
	        try {
	            url = new URL( "http://"+hostName+":"+port+WSDL_LOCATION );
	        }catch ( MalformedURLException e ) {
	            url = null;
	        }
	    }
	    log.debug(" A wsdl URL is " + url);
	    return url;
	}
	
	/**
	 * serves this connection.
	 */
	public void run() {
		boolean isTimeOut = false;
		while( isRunning ) {
			try {
				String hostName = this.httpServer.getHostName();
				int port = this.httpServer.getServerSocket().getLocalPort();
				wsdlTargetNamespace = "http://"+hostName+":"+port+WSDL_LOCATION;
				accessPoint = "http://"+hostName+":"+port+BASE_LOCATION;
				
				InputStream is = new BufferedInputStream(
					socket.getInputStream() );
				Properties prop = readHeader( is );
				String path = createAbsolutePath(prop);
				log.debug( "A request for path: " + path + "." );
				if ( WSDL_LOCATION.equalsIgnoreCase( path )) {
					// request for a WSDL
					try {
						//doRequest( msg, socket.getOutputStream());
						ClassLoader cl = this.getClass().getClassLoader();
						InputStream is2 = cl.getResourceAsStream( "com/whitestein/wsig/test/GoogleSearch.wsdl" ) ;
						BufferedInputStream bis = new BufferedInputStream(is2);
						byte[] content = readAllBytes( bis );
						Connection.sendBackSOAPContent( content, socket.getOutputStream() );
						setIsRunning(false);
						socket.close();
						
						// print out
						//log.debug( "A response is: " + new String( content, "UTF-8" ));
					}catch (IOException ioe) {
						ioe.printStackTrace();
						setIsRunning(false);
						return;
					}
				}else if ( BASE_LOCATION.equalsIgnoreCase( path )) {
					// request for a service's operation
				}

			}catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	private void doRequest( WSMessage wsMsg, OutputStream os ) throws IOException {

		String answer = ""; 
		String opName = wsMsg.getTheFirstUDDIOperationId().getWSDLOperation();
		if ( OP_1.equals( opName )) {
			answer =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
				"	<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  " +
				"	 <soapenv:Body> " +
				"<tns:String xmlns:tns=\""+ wsdlTargetNamespace +"\" >" +
				"Echo string" +
				"</tns:String>   " +
				"	 </soapenv:Body>   " +
				"	</soapenv:Envelope>\r\n";
		}else {
			answer =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
				"	<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  " +
				"	 <soapenv:Body> " +
				"<soapenv:Fault>" +
				"<soapenv:faultcode>soapenv:Client</soapenv:faultcode>" +
				"<soapenv:faultstring>Unknown operation.</soapenv:faultstring>" +
				"<soapenv:faultactor></soapenv:faultactor>" +
				// "<soapenv:detail>" +
				// "<tns:myFaultDetail xmlns:tns=\"http://"+hostName+":"+port+WSDL_LOCATION+"\" >" +
				// "<tns:operation>" + opName + "</tns:operation>" +
				// "<tns:message>Operation is unknown.</tns:message>" +
				// "</tns:myFaultDetail>   " +
				// "</soapenv:detail>" +
				"</soapenv:Fault>" +
				"	 </soapenv:Body>   " +
				"	</soapenv:Envelope>\r\n";
		}
		byte[] content = answer.getBytes("UTF-8");
		Connection.sendBackSOAPContent( content, os );
		
		// print out
		log.debug( "A response is: " + answer );
	}
	
	public WSDLDefinition createWSDLDefinition() {
		WSDLDefinition wsdlDef = null;
		String soapNS="http://schemas.xmlsoap.org/wsdl/soap/"; 
		String xsdNS="http://www.w3.org/2001/XMLSchema";
		String HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http";

		try {
			Definition def = WSDLFactory.newInstance().newDefinition();
			def.setTargetNamespace(wsdlTargetNamespace);
			def.setDocumentBaseURI(wsdlTargetNamespace);
			def.addNamespace( "tns", wsdlTargetNamespace );
			def.addNamespace( "xsd", xsdNS );
			def.addNamespace( "soap", soapNS );
			def.setQName( new QName( def.getNamespace("tns"), "TestingDefinition"));
			
			Message msg1 = def.createMessage();
			msg1.setQName( new QName( def.getNamespace("tns"),"echoInputMsg"));
			msg1.setUndefined( false );
			def.addMessage( msg1 );

			Part part2 = def.createPart();
			part2.setName("content");
			part2.setTypeName( new QName(def.getNamespace("xsd"), "string" ));
			Message msg2 = def.createMessage();
			msg2.setQName( new QName( def.getNamespace("tns"),"echoOutputMsg"));
			msg2.addPart( part2 );
			msg2.setUndefined( false );
			def.addMessage( msg2 );
			
			Input input = def.createInput();
			input.setMessage( msg1 );
			Output output = def.createOutput();
			output.setMessage( msg2 );
			
			Operation op = def.createOperation();
			op.setName(OP_1);
			op.setInput( input );
			op.setOutput( output );
			op.setUndefined( false );
			
			PortType portType = def.createPortType();
			portType.setQName( new QName( def.getNamespace("tns"),"echoPortType") );
			portType.addOperation( op );
			portType.setUndefined( false );
			def.addPortType( portType );
			
			// SOAP binding
			ExtensionRegistry reg = WSDLFactory.newInstance().newPopulatedExtensionRegistry();

			SOAPBody soapBody1 = (SOAPBody) reg.createExtension(
					BindingInput.class,
					new QName( def.getNamespace("soap"), "body") );
			soapBody1.setUse("literal");
			
			BindingInput binIn = def.createBindingInput();
			binIn.addExtensibilityElement(soapBody1);
			BindingOutput binOut = def.createBindingOutput();
			BindingFault binFault = def.createBindingFault();
			
			SOAPOperation soapOperation = (SOAPOperation) reg.createExtension(
					BindingOperation.class,
					new QName( def.getNamespace("soap"), "operation") );
			soapOperation.setSoapActionURI(""); // empty
			
			BindingOperation bindingOperation = def.createBindingOperation();
			bindingOperation.setName(OP_1);
			bindingOperation.addExtensibilityElement( soapOperation );
			bindingOperation.setBindingInput(binIn);
			bindingOperation.setBindingOutput(binOut);
			bindingOperation.addBindingFault( binFault );
			
			SOAPBinding soapBinding = (SOAPBinding) reg.createExtension(
					Binding.class,
					new QName( def.getNamespace("soap"), "binding") );
			soapBinding.setTransportURI(HTTP_TRANSPORT);
			
			Binding binding = def.createBinding();
			binding.setQName( new QName( def.getNamespace("tns"), "echoBinding") );
			binding.setPortType( portType );
			binding.addExtensibilityElement( soapBinding );
			binding.addBindingOperation( bindingOperation );
			binding.setUndefined(false);
			def.addBinding( binding );
			
			// a service
			
			SOAPAddress soapAddress = (SOAPAddress) reg.createExtension(
					Port.class,
					new QName( def.getNamespace("soap"), "address") );
			soapAddress.setLocationURI(accessPoint);

			Port port = def.createPort();
			port.setName("echoPort");
			port.setBinding(binding);
			port.addExtensibilityElement( soapAddress );
			Service service = def.createService();
			service.setQName( new QName( def.getNamespace("tns"),"echoService"));
			service.addPort(port);
			def.addService( service );

			// print it out
			//StringWriter swr = new StringWriter();
			//WSDLWriter wsdlWr = WSDLFactory.newInstance().newWSDLWriter();
			//wsdlWr.writeWSDL( def, swr );
			//log.debug( swr.getBuffer().toString());
			
			// set a WSDLDefinition
			wsdlDef = new WSDLDefinition();
			wsdlDef.setDefinition( def );
			try{
			    wsdlDef.setURL( new URL(wsdlTargetNamespace) );
			}catch ( MalformedURLException e ) {
			    log.error( e );
			}
		}catch (WSDLException wsdlEx) {
			log.error( wsdlEx );
		}
		
		return wsdlDef;
	}

	private byte[] readAllBytes( InputStream is ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		try {
			while ( -1 < (b = is.read()) ) {
				baos.write( b );
			}
		} catch ( IOException ioe ) {
			log.debug("There are problems to load file from .jar");
		}
		return baos.toByteArray();
	}

	/*	
	<?xml version="1.0" encoding="UTF-8"?>
			<definitions name="MyWSDL" 
			targetNamespace="http://T20java:8080/test/mywsdl"
			xmlns="http://schemas.xmlsoap.org/wsdl/" 
			xmlns:tns="http://T20java:8080/test/mywsdl"
			xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" 
			xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
			>

			  <message name="InMessageRequest">
			    <part name="numberToConvert" type="xsd:string"/>
			    <part name="encodedlocale" type="xsd:string"/>
			  </message>

			  <message name="OutMessageResponse">
			    <part name="outMsgText" type="xsd:string"/>
			  </message>

			  <portType name="NumberSpeller">
			    <operation name="getSpelledForm">
			      <input message="tns:InMessageRequest"/>
			      <output message="tns:OutMessageResponse"/>
			    </operation>
			  </portType>

			  <binding name="NumberSpellerBinding" type="tns:NumberSpeller">
			    <soap:binding style="rpc" transport="http://schemas.xmlsoap.org/soap/http"/>
			    <operation name="getSpelledForm">
			      <soap:operation soapAction="http://www.NumberSpeller.com/getSpelledForm"/>
			      <input>
			        <soap:body encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" namespace="urn:numberspeller" use="encoded"/>
			      </input>
			      <output>
			        <soap:body encodingStyle="http://schemas.xmlsoap.org/soap/encoding/" namespace="urn:numberspeller" use="encoded"/>
			      </output>
			    </operation>
			  </binding>

			  <service name="NumberSpeller">
			    <documentation>The NumberSpeller web service can be used to transform an integer number to a text string containing its spelled form (for example, "1" is transformed to "one"). </documentation>

			    <port name="WebSphere" binding="tns:NumberSpellerBinding">
			      <soap:address location="http://dwdemos.alphaworks.ibm.com/numspell/servlet/rpcrouter"/>
			<documentation>SOAP Port for the NumberSpeller web service</documentation>
			    </port>
			    
			  </service>
			</definitions>
	
*/	
	


}
