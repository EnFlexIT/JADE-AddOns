/*
 * Created on Dec 22, 2004
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
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.Register;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Category;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.OverviewDoc;
import org.uddi4j.datatype.OverviewURL;
import org.uddi4j.datatype.binding.AccessPoint;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BindingDetail;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.Result;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.ServiceKey;
import org.uddi4j.util.TModelKey;
import org.uddi4j.datatype.binding.AccessPoint;


import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.net.Connection;
import com.whitestein.wsig.net.HTTPServer;
import com.whitestein.wsig.net.Redirector;
import com.whitestein.wsig.net.SOAPHandler;
import com.whitestein.wsig.net.UDDIRegistrationHandler;
import com.whitestein.wsig.net.WSClient;
import com.whitestein.wsig.ws.DFToUDDI4j;
import com.whitestein.wsig.ws.WSMessage;

/**
 * @author jna
 *
 * tests a SOAP server to use a gateway
 */
public class TestSOAPServer extends HTTPServer{

	public static Category cat = Category.getInstance( TestSOAPServer.class.getName());
	private UDDIProxy uddiProxy;
	private String businessKey;
	private String userName;
	private String password;
	private ServiceKey myServiceKey = null;
	private static final int DEFAULT_PORT = 3333;
	
	public String uddiInquiry = "";
	public String uddiPublish = "";

	/**
	 * starts a new TestSOAPServer on a socket given
	 * 
	 * @param socket a server socket used
	 */
	public TestSOAPServer( ServerSocket socket ) {
		super( socket, "com.whitestein.wsig.test.TestSOAPServerConnection" );
		try {
			String host = socket.getInetAddress().getLocalHost().getHostName();
			cat.debug(" a local host's name = " + host );
			setHostName( host );
		}catch ( java.net.UnknownHostException he ) {
			cat.error( he );
		}
	}

	/**
	 * finds a handler.
	 * A new instance is returned.
	 *
	 * @param absolutePath a searching criteria
	 * @return a new instance of the handler
	 */
	public SOAPHandler findHandler( String absolutePath ) {
		SOAPHandler h = null;
		
		// an empty handler is provided
		h = new SOAPHandler() {
			public SOAPMessage doRequest( Properties header, SOAPMessage soap ) {
				return null;
			}
			public void close() {
			}
		};
		return h;
	}
	
	/**
	 * sets up the UDDI4j. It starts components required.
	 * Class fields authToken and uddiProxy are set properly as main result.
	 * A .ws.DFToUDDI4j class is used also.  
	 *
	 */
	private void setupUDDI4j() {
		// to register into UDDI
		// structures used for a communication with UDDI is retrieved
		Configuration c = Configuration.getInstance();

		//System.setProperty("hpsoap.logDirectory","/tmp");
		//System.setProperty("hpsoap.logFileName","uddi4j.log");
		
		uddiProxy = new UDDIProxy();
		synchronized ( c ) {
			// synchronized on main Configuration instance
			// to prevent changes in configuration

			System.setProperty( Configuration.KEY_UDDI4J_LOG_ENABLED,
					c.getUDDI4jLogEnabled());
			System.setProperty( Configuration.KEY_UDDI4J_TRANSPORT_CLASS,
					c.getUDDI4jTransportClass());
			
			businessKey = c.getBusinessKey();
			userName = c.getUserName();
			password = c.getUserPassword();
			
			// cat.debug(" bk="+ businessKey + ", user=" + userName + ", p=" + password );

			// Select the desired UDDI server node
			try {
				uddiProxy.setInquiryURL(uddiInquiry);
				uddiProxy.setPublishURL(uddiPublish);
			}catch( Exception e ) {
				cat.error(e);
			}
		}
	}
	
	/**
	 * performs a registration into a UDDI.
	 */
	public synchronized void register() {

		try {
			// create identification names
			String tName = "Testing SOAP's tModel.";
			String sName = "Testing SOAP's businessService.";
			
			TestSOAPServerConnection conn = new TestSOAPServerConnection();
			conn.setHTTPServer( this );
			URL wsdlURL = conn.getWSDLLocation();
			
			// create a new tModel
			TModel tModel;
			tModel = createTModel( wsdlURL.toString(), tName);
			
			// Add new service into an organization running WSIGS.
			// This is only functional in UDDI v3.0 by accessPoint's useType attribute.
			// It is left on hostingRedirector in UDDI v2.0.

			// create a new businessService
			BusinessService businessService = new BusinessService("");
			businessService.setDefaultNameString(sName,null);
			businessService.setBusinessKey(businessKey);
			
			// save the Service
			Vector services = new Vector();
			services.addElement(businessService);
			AuthToken authToken = uddiProxy.get_authToken( userName,password);
			ServiceDetail serviceDetail = uddiProxy.save_service(authToken.getAuthInfoString(),services);

			// get a service key returned
			Vector businessServices = serviceDetail.getBusinessServiceVector();
			BusinessService businessServiceReturned = (BusinessService)(businessServices.elementAt(0));
			String serviceKey = businessServiceReturned.getServiceKey();
			myServiceKey = new ServiceKey(serviceKey);

			// create bindingTemplate
			AccessPoint ap = new AccessPoint( conn.getAccessPoint().toASCIIString(), "http");
			createBindingTemplate(
			        ap,
					new ServiceKey( serviceKey ),
					new TModelKey(tModel.getTModelKey()) );

		}catch ( UDDIException e ) {
			cat.error(e);
		}catch ( TransportException e ) {
			cat.error(e);
		}
		
	}
	
	
	/**
	 * gets a service identified by a key
	 * 
	 * @param serviceKey key of service requested
	 * @return service requested
	 * @throws UDDIException
	 * @throws TransportException
	 */
	public BusinessService takeService( ServiceKey serviceKey ) throws UDDIException, TransportException {
		ServiceDetail serviceDetail = uddiProxy.get_serviceDetail(serviceKey.getText());
		Vector businessServices = serviceDetail.getBusinessServiceVector();
		return (BusinessService)(businessServices.elementAt(0));
	}

	/**
	 * performs a deregistration into a UDDI.
	 */
	public synchronized void deregister() {
		DispositionReport dr;
		try {
			// delete tModel
			BusinessService bs = takeService( myServiceKey );
			BindingTemplate bt = bs.getBindingTemplates().get(0);
			TModelKey tModelKey = new TModelKey(
				bt.getTModelInstanceDetails().get(0).getTModelKey() );
			AuthToken authToken = uddiProxy.get_authToken( userName,password);
			dr = uddiProxy.delete_tModel(authToken.getAuthInfoString(), tModelKey.getText() );
			if( ! dr.success() ) {
				cat.error("Error during deletion of TModel\n"+
						"\n operator:" + dr.getOperator() +
						"\n generic:"  + dr.getGeneric() );
			}

			// delete a service
			dr = uddiProxy.delete_service(authToken.getAuthInfoString(), myServiceKey.getText());
			if( ! dr.success() ) {
				cat.error("Error during deletion of Service\n"+
						"\n operator:" + dr.getOperator() +
						"\n generic:"  + dr.getGeneric() );

				Vector results = dr.getResultVector();
				for( int j=0; j<results.size(); j++ )
				{
					Result r = (Result)results.elementAt(j);
					cat.error(" errno:"    + r.getErrno() );
					if( r.getErrInfo()!=null )
					{
						cat.error("\n errCode:"  + r.getErrInfo().getErrCode() +
								"\n errInfoText:" + r.getErrInfo().getText());
					}
				}
			}
		}catch ( UDDIException e ) {
			cat.error(" Deregistration " + e);
		}catch ( TransportException e ) {
			cat.error(" Deregistration " + e);
		}
	}
	
	/**
	 * creates and registers a bindingTemplate
	 * 
	 * @param accessPoint an accessPoint for a service
	 * @param serviceKey service key for a bindingTemplate
	 * @param tModelKey tModel key to be reffered
	 * @return a new bindingTemplate registered in a UDDI
	 */
	public BindingTemplate createBindingTemplate( AccessPoint accessPoint, ServiceKey serviceKey, TModelKey tModelKey ) {
		cat.debug("A bindingTemplate is going to be created.");
		BindingTemplate bindingTemplateReturned = null;
		try {
			// create TModelInstanceDetails
			Vector tModelInstanceInfoVector = new Vector();
			TModelInstanceInfo tModelInstanceInfo = new TModelInstanceInfo(tModelKey.getText());
			tModelInstanceInfoVector.add(tModelInstanceInfo);
			TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();
			tModelInstanceDetails.setTModelInstanceInfoVector(tModelInstanceInfoVector);

			// create a new bindingTemplate
			BindingTemplate bindingTemplate =
				new BindingTemplate("",
									tModelInstanceDetails,
									accessPoint );
			bindingTemplate.setServiceKey(serviceKey.getText());
			Vector bindingTemplatesVector = new Vector();
			bindingTemplatesVector.addElement(bindingTemplate);

			// save bindingTemplate
			AuthToken authToken = uddiProxy.get_authToken( userName,password);
			BindingDetail bindingDetail = uddiProxy.save_binding(
					authToken.getAuthInfoString(),
					bindingTemplatesVector);

			// BindingDetail returned is given as a result
			Vector bindingTemplateVector = bindingDetail.getBindingTemplateVector();
			bindingTemplateReturned = (BindingTemplate)(bindingTemplateVector.elementAt(0));
			
		}catch ( UDDIException e ) {
			cat.error(e);
		}catch ( TransportException e ) {
			cat.error(e);
		}

		cat.debug("New BindingKey: " + bindingTemplateReturned.getBindingKey());
		return bindingTemplateReturned;
	}
	

	/**
	 * creates and registers a new TModel
	 * 
	 * @param wsdlURL url for a wsdl's description
	 * @param name name for a tModel
	 * @return tModel created
	 */
	public TModel createTModel( String wsdlURL, String name ) {
		cat.debug("A tModel is going to be created.");
		TModel tModelReturned = null;
		try {
			// to point into a WSDL
			OverviewURL overviewURL = new OverviewURL( wsdlURL );
			OverviewDoc overviewDoc = new OverviewDoc();
			overviewDoc.setOverviewURL( overviewURL );
			
			// create a new TModel
			TModel tModel = new TModel("", name);
			tModel.setOverviewDoc( overviewDoc );
			Vector tModelsVector = new Vector();
			tModelsVector.addElement( tModel );

			// save bindingTemplate
			AuthToken authToken = uddiProxy.get_authToken( userName,password);
			TModelDetail tModelDetail = uddiProxy.save_tModel(
					authToken.getAuthInfoString(),
					tModelsVector);

			// tModelDetail returned is given as a result
			tModelsVector = tModelDetail.getTModelVector();
			tModelReturned = (TModel)(tModelsVector.elementAt(0));
			
		}catch ( UDDIException e ) {
			cat.error(e);
		}catch ( TransportException e ) {
			cat.error(e);
		}

		cat.debug("New tModelKey: " + tModelReturned.getTModelKey());
		return tModelReturned;
	}
	

	public static int parsePort( String[] args ) {
		if ( args.length < 1 ) {
			return DEFAULT_PORT;
		}
		try {
			return Integer.parseInt( args[0] );
		}catch (NumberFormatException ne ) {
			return DEFAULT_PORT;
		}
		
	}
	
	public static String parseGatewayURI( String[] args ) {
		if ( args.length > 2 ) {
			return args[1];
		}else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		//TestSOAPServerConnection con = new TestSOAPServerConnection();
		//con.createWSDLDefinition();
		//System.exit(0);
		
		ServerSocket socket = null;
		try {
			socket = new ServerSocket( parsePort( args ));
		}catch( IOException ioe ) {
			cat.error(ioe);
			System.exit(0);
		}
		com.whitestein.wsig.Configuration c = com.whitestein.wsig.Configuration.getInstance();
		String host;
		if ( null == parseGatewayURI( args ) ) {
			host = c.getHostURI();
		}else {
			host = parseGatewayURI( args );
		}
		TestSOAPServer t = new TestSOAPServer( socket );
		t.uddiInquiry = host + c.getQueryManagerPath();
		cat.debug(" uddi query accsess " + t.uddiInquiry);
		t.uddiPublish = host + c.getLifeCycleManagerPath();
		t.setupUDDI4j();
		t.register();
		try {
			int m = 5;
		    Thread.sleep( m * 60000 ); // 5 minutes
		    cat.warn( "A TestSOAPServer will be terminated after " +
		    		m + "minutes");
		}catch (InterruptedException ie) {
		    cat.debug( "an interruption, " + ie);
			t.deregister();
			t.close();
		}
		
	}
}
