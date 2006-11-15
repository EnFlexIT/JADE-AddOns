/*
 * Created on Jun 9, 2004
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
package com.whitestein.wsig;

import com.whitestein.wsig.fipa.*;
import com.whitestein.wsig.net.*;
import com.whitestein.wsig.struct.*;
import com.whitestein.wsig.test.*;
import com.whitestein.wsig.translator.*;
import com.whitestein.wsig.ws.*;

import org.apache.axis.server.AxisServer;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.List;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Search;

/*
import javax.xml.registry.infomodel.Concept;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.RegistryObject;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.SpecificationLink;
import javax.xml.registry.infomodel.ServiceBinding;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.InternationalString;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.RegistryException;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.RegistryService;
import javax.xml.registry.Connection;
import javax.xml.registry.JAXRException;
*/

import javax.wsdl.WSDLException;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Set;
import java.util.Properties;

import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.URL;
import java.io.IOException;

//import org.apache.axis.transport.http.SimpleAxisServer;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.deployment.wsdd.WSDDService;
import org.apache.axis.deployment.wsdd.WSDDOperation;
import org.apache.axis.description.OperationDesc;  // by JAVAdoc it is in working progress
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.handlers.soap.SOAPService;

import javax.xml.namespace.QName;

import org.uddi4j.client.UDDIProxy;
import org.uddi4j.response.BusinessInfo;
import java.util.Vector;
import org.uddi4j.datatype.Name;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.response.BusinessList;
import org.uddi4j.response.AuthToken;
//import org.apache.soap.*;

/**
 * The main class to run the WSIGS gateway service.
 *
 * @author jna
 *
 */
public class Gateway {

	private static Gateway gw;
	
	//private BusinessLifeCycleManager businessLifeCycleManager;
	//private BusinessQueryManager businessQueryManager;
	//private Organization organization;
	//private Service service;
	private Hashtable aidToService = new Hashtable(); 
	private Hashtable aidToConcept = new Hashtable();
	// fipaServiceId to ServedOperation index
	private Hashtable fipaSIdToServedOperation = new Hashtable();
	// uddiOperationId to ServedOperation index
	private Hashtable uddiOIdToServedOperation = new Hashtable();
	private ServedOperationStore operationStore = ServedOperationStore.getInstance();
	
	private SimpleAxisServer sAxisServer;
	
	private UDDIProxy uddiProxy;
	
	private HTTPServer server;
	private Thread test1;

	
	/**
	 * creates new WSIGS 
	 *
	 */
	public Gateway() {
		try {
			int port = Configuration.getInstance().getHostPort();
			server = new HTTPServer( new ServerSocket(port));
			//test();
			
			//test1 = new Thread(new TestSOAPClient());
			//test1.start();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*private void test() {
		FIPAServiceIdentificator fipa = new FIPAServiceIdentificator(
				new AID("testAgent001@T20java:1099/JADE",AID.ISGUID), TestAgent001.OPERATION);
			//	new AID("da0",AID.ISLOCALNAME), "service01");
		UDDIOperationIdentificator uddi = new UDDIOperationIdentificator(
				TestSOAPClient.ACCESS_POINT, TestSOAPClient.OPERATION );
		OperationID id = new OperationID( fipa, uddi );
		FIPAEndPoint ep = new FIPAEndPoint(fipa);//,this);
		ServedOperation test = new ServedOperation( id, ep, true );
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
		operationStore.put(test);
		
		fipa = new FIPAServiceIdentificator(
				new AID("testAgent001@T20java:1099/JADE",AID.ISGUID), TestAgent001.OPERATION_2);
			//	new AID("da0",AID.ISLOCALNAME), "service01");
		uddi = new UDDIOperationIdentificator(
				TestSOAPClient.ACCESS_POINT, TestSOAPClient.OPERATION_2 );
		id = new OperationID( fipa, uddi );
		ep = new FIPAEndPoint(fipa);//,this);
		test = new ServedOperation( id, ep, true );
		test.setWSDL( wsdl );
		operationStore.put(test);
		
		fipa = new FIPAServiceIdentificator(
				Configuration.getInstance().getGatewayAID(), "getTestVersion");
		uddi = new UDDIOperationIdentificator(
				"http://localhost:1235/axis/services/Version", "getVersion");
		id = new OperationID( fipa, uddi );
		WSEndPoint wsep = new WSEndPoint(uddi);//,this);
		test = new ServedOperation( id, wsep, true );
		wsdl = new WSDLDefinition();
		try {
			wsdl.setURL( new URL("http://localhost:8080/axis/services/Version?wsdl"));
			wsdl.setDefinition( WSDLFactory.newInstance().newWSDLReader(
				).readWSDL( "file:///C:/Program%20Files/Apache%20Software%20Foundation/Tomcat%205.0/webapps/axis/Version.xml.wsdl" ) );
		}catch ( Exception e ) {
			e.printStackTrace();
		}
		test.setWSDL( wsdl );
		operationStore.put(test);

		// add also a XML-tagged version
		fipa = new FIPAServiceIdentificator(
				Configuration.getInstance().getGatewayAID(),
				SOAPToFIPASL0.XML_TAG_ + "getTestVersion");
		//uddi = new UDDIOperationIdentificator(
		//		"http://localhost:1235/axis/services/Version", "getVersion");
		id = new OperationID( fipa, uddi );
		//wsep = new WSEndPoint(uddi);//,this);
		test = new ServedOperation( id, wsep, true );

		//wsdl = new WSDLDefinition();
		//try {
		//	wsdl.setURL( new URL("http://localhost:8080/axis/services/Version?wsdl"));
		//	wsdl.setDefinition( WSDLFactory.newInstance().newWSDLReader(
		//		).readWSDL( "file:///C:/Program%20Files/Apache%20Software%20Foundation/Tomcat%205.0/webapps/axis/Version.xml.wsdl" ) );
		//}catch ( Exception e ) {
		//	e.printStackTrace();
		//}

		test.setWSDL( wsdl );
		operationStore.put(test);
	}*/

	public void addServedOperation() {
		FIPAServiceIdentificator fipa;
		UDDIOperationIdentificator uddi;
		OperationID id;
		FIPAEndPoint ep;
		ServedOperation test;
		WSDLDefinition wsdl;
		
		fipa = new FIPAServiceIdentificator(
				Configuration.getInstance().getGatewayAID(), "getTestVersion");
		uddi = new UDDIOperationIdentificator(
				"http://localhost:1235/axis/services/Version", "getVersion");
		id = new OperationID( fipa, uddi );
		WSEndPoint wsep = new WSEndPoint(uddi);//,this);
		test = new ServedOperation( id, wsep, true );
		wsdl = new WSDLDefinition();
		try {
			wsdl.setURL( new URL("http://localhost:8080/axis/services/Version?wsdl"));
			wsdl.setDefinition( WSDLFactory.newInstance().newWSDLReader(
				).readWSDL( "file:///C:/Program%20Files/Apache%20Software%20Foundation/Tomcat%205.0/webapps/axis/Version.xml.wsdl" ) );
		}catch ( Exception e ) {
			e.printStackTrace();
		}
		test.setWSDL( wsdl );
		operationStore.put(test);
	
	}
	
	/**
	 * gives an instance of gateway.
	 * The singleton instance is provided.
	 * 
	 * @return instance of gateway
	 */
	public static synchronized Gateway getInstance() {
		if ( gw == null ) {
			gw = new Gateway();
			// gw.setupUDDI4j();
			//gw.setupJAXR();
		}
		return gw;
	}
	
	/**
	 * gives gateway's businessLifeCycleManager
	 * @return businessLifeCycleManager
	 */
//	public BusinessLifeCycleManager getBusinessLifeCycleManager( ) {
//		return businessLifeCycleManager;
//	}
	
	/**
	 * gives gateway's businessQueryManager
	 * @return businessQueryManager
	 */
//	public BusinessQueryManager getBusinessQueryManager( ) {
//		return businessQueryManager;
//	}
	
	public String getAccessPointURI( DFAgentDescription dfad ) {
		String uri = "http://T20Java:8080/jUDDI/"; // + dfad.getName().getLocalName();
		return uri;
	}
	
	/**
	 * produces and registers a JAXR Concept.
	 * A JAXR Concept as a UDDI tModel holder is produced for the DFAgentDescription.
	 * 
	 * @param dfd description of the agent
	 * @return the Concept of the agent
	 * @throws registries exception may be occured
	 */
/*	
	public Concept registerJAXRConceptFromDFAgentDescription( DFAgentDescription dfad ) throws Exception {
		// only name of tModel is mandatory,
		// in future other fields will be registered too ( overviewDoc, identifierBag, categoryBag )
		Iterator i;
		Concept c = getBusinessLifeCycleManager().createConcept(
			null,
			"WSIGS tModel for agent " + dfad.getName(),
			null);
		Collection coll = new ArrayList();
		coll.add(c);
		BulkResponse r = getBusinessLifeCycleManager().saveConcepts(coll);
		if( r.getExceptions() != null ) {
			// exception has been occured
			for( i = r.getExceptions().iterator(); i.hasNext(); ) {
				RegistryException re =
					(RegistryException) i.next();
				System.out.println( re.toString());
			}
			i = r.getExceptions().iterator();
			if ( i.hasNext() ) {
				RegistryException re =
					(RegistryException) i.next();
				// inform caller of this methot
				throw new Exception(re);
			}
		}else {
			i = r.getCollection().iterator();
			if ( i.hasNext() ) {
				// only one tModel registered
				Object o = i.next();
				if ( ! (o instanceof Concept) ) {
					throw new Exception("Instance of Concept has been expected.");
				}
				c = (Concept) o;
			}else {
				// our registered tModel is missing
				throw new Exception(" Missing tModel registered.");
			}
		}
		return c;
	}
*/	
	/**
	 * saves service into UDDI
	 *  
	 * @param s service saved
	 * @return service returned by UDDI
	 * @throws Exception
	 */
/*
	private Service saveService( Service s ) throws Exception {
		BulkResponse r;
		Iterator i;
		// save services into UDDI
		Collection coll = new ArrayList();
		coll.add(s);
		r = businessLifeCycleManager.saveServices(coll);
		if( r.getExceptions() != null ) {
			// exception has been occured
			i = r.getExceptions().iterator();
			if ( i.hasNext() ) {
				RegistryException re =
					(RegistryException) i.next();
				// inform method's caller, first exception is only used
				throw new Exception(re);
			}
		}else {
			// check the UDDI response
			i = r.getCollection().iterator();
			if ( i.hasNext() ) {
				Object o = i.next();
				if ( ! (o instanceof Service) ) {
					throw new Exception("Instance of Service has been expected.");
				}
				s = (Service) o;
			}else {
				// WSIGS modified is missing
				throw new Exception(" Missing WSIGS service in the UDDI response.");
			}
		}
		return s;
	}
*/
	
	/**
	 * creates served operations.
	 * For each FIPA operation is created new ServedOperation instance.
	 * 
	 * @param dfad agent's registration structure
	 * @return collection of served operations
	 */
	private synchronized Collection createServedOperations( DFAgentDescription dfad ) {
		Collection coll = new ArrayList();
		ServiceDescription sd;
		FIPAServiceIdentificator fipaSId;
		UDDIOperationIdentificator uddiOId;
		Iterator it = dfad.getAllServices();
		AID agentId = dfad.getName();
		ServedOperation op;
		OperationID opId;
		FIPAEndPoint fipaEP;
		while( it.hasNext()) {
			sd = (ServiceDescription) it.next();
			fipaSId = new FIPAServiceIdentificator( agentId, sd );
			fipaEP = new FIPAEndPoint( fipaSId );//, Gateway.getInstance());
			
			uddiOId = ServedOperationStore.generateUDDIOperationId();
			
			opId = new OperationID( fipaSId, uddiOId );
			op = new ServedOperation( opId, fipaEP, true);
			coll.add( op );
			
			operationStore.put( op );
		}
		return coll;
	}
	
	/**
	 * removes served operations.
	 * Each FIPA operation is removed with corresponding ServedOperation instance.
	 * 
	 * @param dfad agent's registration structure
	 */
	private synchronized void removeServedOperations( DFAgentDescription dfad ) {
		ServiceDescription sd;
		FIPAServiceIdentificator fipaSId;
		Iterator it = dfad.getAllServices();
		ServedOperation op;
		while( it.hasNext()) {
			sd = (ServiceDescription) it.next();
			fipaSId = new FIPAServiceIdentificator( dfad.getName(), sd );
			op = operationStore.find( fipaSId );
			operationStore.remove(op);
			op.close();
		}
	}
	
	/**
	 * performs registration.
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * 
	 * @param register
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void registerAction( Register register, AID aid ) throws FIPAException {
		DFAgentDescription dfad = (DFAgentDescription) register.getDescription();
		System.out.println(" wsigs registration " + dfad);
		
		Collection col = createServedOperations( dfad );
		
		//uddi.registerServedOperations( col.getInfos() );
		
		/*
		try {
			System.out.println(" wsigs registration 1 ");
			
			// tModel (Concept) for an agent is registered in UDDI
			//Concept c = registerJAXRConceptFromDFAgentDescription( dfad );
			//aidToConcept.put( dfad.getName(), c.getKey());
			// to create new binding into WSIGS service
			System.out.println( " bLCM : " + businessLifeCycleManager );
			ServiceBinding binding = 
				businessLifeCycleManager.createServiceBinding();
			System.out.println(" wsigs registration 1b ");
			binding.setAccessURI( getAccessPointURI(dfad));
			System.out.println(" wsigs registration 2 ");
			//SpecificationLink specLink = 
			//	getBusinessLifeCycleManager().createSpecificationLink();
			//specLink.setSpecificationObject(c);
			//binding.addSpecificationLink( specLink );
			
			
			// to modify WSIGS service
			Service s = service;
			
			////  do not register in WSIGS's service record, use standalone services only
			//s.addServiceBinding(binding);
			//service = saveService(s);
			
			
			// add new service into an organization running WSIGS
			String name = "WSIGS gateway for" + dfad.getName();
			Service sn = businessLifeCycleManager.createService( name );
			sn.setProvidingOrganization( organization );
			System.out.println(" wsigs registration 3 ");
			// in the future change bindig reference from URI to bindingTemplate
			// see tergetBinding
			sn.addServiceBinding(binding);
			// save services into UDDI
			System.out.println(" wsigs registration 4 ");
			sn = saveService(sn);
			//store reference for AID and new service for removing the service
			aidToService.put( dfad.getName(), sn.getKey());
			System.out.println(" wsigs registration done ");
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new FIPAException(" An UDDI service registration exception.");
		}
		*/
	}
	
	/**
	 * performs deregistration
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * 
	 * @param deregister
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void deregisterAction( Deregister deregister, AID aid ) throws FIPAException {
		DFAgentDescription dfad = (DFAgentDescription) deregister.getDescription();
		removeServedOperations( dfad );
		
		/*
		//delete service identified by the Key stored
		Key k = (Key) aidToService.get( dfad.getName());
		BulkResponse r;
		Iterator i;
		Collection coll = new ArrayList();
		coll.add(k);
		try {
			r = getBusinessLifeCycleManager().deleteServices(coll);
			if( r.getExceptions() != null ) {
				// exception has been occured
				i = r.getExceptions().iterator();
				if ( i.hasNext() ) {
					RegistryException re =
						(RegistryException) i.next();
					// inform method's caller, first exception is only used
					throw new Exception(re);
				}
			}
		} catch ( Exception e ) {
			throw new FIPAException(" An UDDI service removing exception.");
		}
		aidToService.remove( dfad.getName());
		
		// delete also tModel
		k = (Key) aidToConcept.get( dfad.getName());
		coll = new ArrayList();
		coll.add(k);
		try {
			r = getBusinessLifeCycleManager().deleteConcepts(coll);
			if( r.getExceptions() != null ) {
				// exception has been occured
				i = r.getExceptions().iterator();
				if ( i.hasNext() ) {
					RegistryException re =
						(RegistryException) i.next();
					// inform method's caller, first exception is only used
					throw new Exception(re);
				}
			}
		} catch ( Exception e ) {
			throw new FIPAException(" An UDDI service removing exception.");
		}
		aidToConcept.remove( dfad.getName());
		*/
	}
	
	/**
	 * performs modification
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * 
	 * @param modify
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void modifyAction( Modify modify, AID aid ) throws FIPAException {
		DFAgentDescription dfd = (DFAgentDescription) modify.getDescription();
		// modification of the agent's tModel
	}
	
	/**
	 * performs searching.
	 * The list exists after superclasse's call and is passed by the generatedList parameter. 
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * 
	 * @param search searching request
	 * @param aid requester
	 * @param generatedList already generated list by suprclass
	 * @return
	 * @throws FIPAException
	 */
	public List searchAction( Search search, AID aid, List generatedList ) throws FIPAException {
		// nothing advanced, generatedList is only returned
		return generatedList;
	}
	

	/**
	 * setups the gateway. It starts components required. 
	 *
	 */
	private void setupUDDI4j_old() {
		// to register into UDDI
		// structures used for a communication with UDDI is retrieved
		Properties prop = new Properties();
		String userName;
		String password;
		String orgKey;
		Configuration c = Configuration.getInstance();
		System.setProperty("hpsoap.logDirectory","/tmp");
		System.setProperty("hpsoap.logFileName","uddi4j.log");
		System.setProperty("org.uddi4j.logEnabled","false");
		System.setProperty("org.uddi4j.TransportClassName","org.uddi4j.transport.ApacheAxisTransport");
		synchronized ( c ) {
			// synchronized on main Configuration instance
			// to prevent changes in configuration

//			System.setProperty(Configuration.KEY_FACTORY_CLASS, c.getFactoryClass());

			prop.setProperty(Configuration.KEY_QUERY_MANAGER_URL,c.getQueryManagerURL());
			prop.setProperty(Configuration.KEY_LIFE_CYCLE_MANAGER_URL, c.getLifeCycleManagerURL());
			
			orgKey = c.getBusinessKey();

			userName = c.getUserName();
			password = c.getUserPassword();

			uddiProxy = new UDDIProxy ();
			// Select the desired UDDI server node
			try {
//				uddiProxy.setInquiryURL(c.getQueryManagerURL());
//				uddiProxy.setPublishURL(c.getLifeCycleManagerURL());
			}catch( Exception e ) {
				e.printStackTrace();
			}
		}
			
		// Get an authorization token
		System.out.println("\nGet authtoken");

		// Pass in userid and password registered at the UDDI site
		AuthToken token;
		try {
			System.out.println("10");
			token = uddiProxy.get_authToken( userName,password);
			System.out.println("Returned authToken:" + token.getAuthInfoString());
		}catch (Exception e ){
			e.printStackTrace();
		}

		//creating vector of Name Object
		Vector names = new Vector();
		names.add(new Name("S"));

		// Setting FindQualifiers to 'caseSensitiveMatch'
		FindQualifiers findQualifiers = new FindQualifiers();
		Vector qualifier = new Vector();
		qualifier.add(new FindQualifier("caseSensitiveMatch"));
		findQualifiers.setFindQualifierVector(qualifier);

		// Find businesses by name
		// And setting the maximum rows to be returned as 5.
		try {
			System.out.println("0");
			BusinessList businessList = uddiProxy.find_business(names, null, null, null,null,findQualifiers,5);
			System.out.println("1");
			Vector businessInfoVector  = businessList.getBusinessInfos().getBusinessInfoVector();
			System.out.println("2");
			for( int i = 0; i < businessInfoVector.size(); i++ )
			{
				BusinessInfo businessInfo = (BusinessInfo)businessInfoVector.elementAt(i);

				// Print name for each business
				System.out.println(businessInfo.getDefaultNameString());
			}			
			System.out.println("3");
		}catch (Exception e) {
			e.printStackTrace();
		}


		/*
		try {
			//Provide your UDDI account user name and password.
			//You must create a real UDDI account for this work.
			PasswordAuthentication passwdAuth =
				new PasswordAuthentication(userName, password.toCharArray());
			Set creds = new HashSet();
			creds.add(passwdAuth);

			ConnectionFactory connFac = ConnectionFactory.newInstance();
			connFac.setProperties(prop);
			Connection connection = connFac.createConnection();
			
			connection.setCredentials(creds);
			
			RegistryService registryService = connection.getRegistryService();
			businessQueryManager = registryService.getBusinessQueryManager();
			businessLifeCycleManager = registryService.getBusinessLifeCycleManager();
			
			// get the organization for the businessKey orgKey
			if ( orgKey != null && !orgKey.matches("") ) {
				organization = (Organization) businessQueryManager.getRegistryObject(
						orgKey,
						BusinessLifeCycleManager.ORGANIZATION );
			}else{
				System.out.println("BusinessKey for an organization provided wsigs is required.");
				createNewOrganization();
			}
		}
		catch (JAXRException e) {
			System.out.println("A UDDI connection problems.");
			//e.printStackTrace();
		}
		*/
		
		//testAxis1();
	}
	
	
	/**
	 * setups the gateway. It starts components required. 
	 *
	 */
/*	
	private void setupJAXR() {
		// to register into UDDI
		// structures used for a communication with UDDI is retrieved
		try {
			Properties prop = new Properties();
			String userName;
			String password;
			String orgKey;
			Configuration c = Configuration.getInstance();
			synchronized ( c ) {
				// synchronized on main Configuration instance
				// to prevent changes in configuration

				System.setProperty(Configuration.KEY_FACTORY_CLASS, c.getFactoryClass());

				prop.setProperty(Configuration.KEY_QUERY_MANAGER_URL,c.getQueryManagerURL());
				prop.setProperty(Configuration.KEY_LIFE_CYCLE_MANAGER_URL, c.getLifeCycleManagerURL());
				
				orgKey = c.getBusinessKey();

				userName = c.getUserName();
				password = c.getUserPassword();
			}
			//Provide your UDDI account user name and password.
			//You must create a real UDDI account for this work.
			PasswordAuthentication passwdAuth =
				new PasswordAuthentication(userName, password.toCharArray());
			Set creds = new HashSet();
			creds.add(passwdAuth);

			ConnectionFactory connFac = ConnectionFactory.newInstance();
			connFac.setProperties(prop);
			Connection connection = connFac.createConnection();
			
			connection.setCredentials(creds);
			
			RegistryService registryService = connection.getRegistryService();
			businessQueryManager = registryService.getBusinessQueryManager();
			businessLifeCycleManager = registryService.getBusinessLifeCycleManager();
			if ( businessQueryManager == null || businessLifeCycleManager == null ) {
				System.out.println("business*Manager is/are null");
			}
			
			// get the organization for the businessKey orgKey
			if ( orgKey != null && !orgKey.matches("") ) {
				System.out.println("org. key is : " + orgKey );
				organization = (Organization) businessQueryManager.getRegistryObject(
						orgKey,
						BusinessLifeCycleManager.ORGANIZATION );
			}else{
				System.out.println("BusinessKey for an organization provided wsigs is required.");
				//createNewOrganization();
			}
		}
		catch (JAXRException e) {
			System.out.println("A UDDI connection problems.");
			e.printStackTrace();
		}
		
		testAxis1();
	}
*/		
	private void testAxis1() {
		// create SimpleAxisServer
		try {
			Configuration c = Configuration.getInstance();
			sAxisServer = new SimpleAxisServer();
			sAxisServer.setServerSocket( new ServerSocket( 3456 ));
			// add SOAP server - WSIGS SOAP server
			System.out.println(" Axis configuration is " + sAxisServer.getInternalAxisServer().getConfig().getClass() );
			FileProvider fp = (FileProvider) sAxisServer.getInternalAxisServer().getConfig();
			WSDDService wsddService = new WSDDService();
			/*
			OperationDesc opD = new OperationDesc("test", new ParameterDesc[0], new QName("qTest"));
			WSDDOperation wsddOp = new WSDDOperation( opD );
			wsddService.addOperation( wsddOp );
			fp.getDeployment().deployService( wsddService );
			 */
			
			SOAPService soapService = null; //new SOAPService( null, new OperationsHandler(), null );
			/*
			wsddService = new WSDDService();
			wsddService.setCachedService(soapService);
			fp.getDeployment().deployService( wsddService );
			 */
			SimpleProvider sp = new SimpleProvider( fp );
			sp.deployService( "test", soapService );
			sp.configureEngine( sAxisServer.getInternalAxisServer());
			sAxisServer.getInternalAxisServer().start();
			System.out.println(" New axis's configuration is " + sAxisServer.getInternalAxisServer().getConfig().getClass() );
			System.out.println("  . " + sAxisServer.getInternalAxisServer().getService("test") );
			System.out.println("  . " + sAxisServer.getInternalAxisServer().getService("wst") );
			// soapService = new SOAPService( null, new OperationsHandler(), null );
			// soapService.setEngine( sAxisServer.getInternalAxisServer() );
			
			soapService = null; // new SOAPService( null, new OperationsHandler(), null );
			sp = new SimpleProvider();
			// sp.deployService( new QName("test"), soapService );
			AxisServer as = new AxisServer( sp );
			as.start();
			//sp = new SimpleProvider( as.getConfig());
			sp.deployService( "test", soapService );
			//sp.configureEngine( as );
			System.out.println("  . " + as.getService( "test") );
		}catch (IOException ioe) {
			ioe.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * creates new organization in a UDDI.
	 * For testing purpose.
	 * 
	 * A problematic point is the Apache Scout project developed.
	 *
	 */
/*
	private void createNewOrganization() {
		try {
			Organization org = businessLifeCycleManager.createOrganization("Sample WSIGS Provider");
			InternationalString str =
				businessLifeCycleManager.createInternationalString(
						java.util.Locale.ENGLISH, "Sample WSIGS testing provider");
			org.setName(str);
			Collection request = new ArrayList();
			request.add(org);
			BulkResponse response = null;
			try {
				response = businessLifeCycleManager.saveOrganizations( request );
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("An organization creation failure.");
				return;
			}
			if( response == null ) {
				System.out.println("An organization key is empty.");
				return;
			}
			if ( response.getExceptions() == null ) {
				Collection keys = response.getCollection();
				Iterator i = keys.iterator();
				if ( i.hasNext() ) {
					Key k = (Key) i.next();
					synchronized (Configuration.getInstance()) {
						Configuration.getInstance().setBusinessKey( k.getId() );
					}
				}
			}
		}catch (JAXRException e) {
			e.printStackTrace();
		}
	}
*/
	
	/**
	 * Runs the gateway service.
	 * 
	 * @param args command line arguments.
	 */
	public static void main(String[] args) {
		// the new instance of Gateway is started
		Gateway gateway = Gateway.getInstance();
	}
}
