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
package com.whitestein.wsig.ws;

import com.whitestein.wsig.*;
import com.whitestein.wsig.fipa.*;
import com.whitestein.wsig.struct.*;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.List;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Search;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;

import org.uddi4j.client.UDDIProxy;
//import org.uddi4j.response.BusinessInfo;
import java.util.Vector;
//import org.uddi4j.datatype.Name;
import org.uddi4j.util.*;
import org.uddi4j.*;
import org.uddi4j.datatype.*;
import org.uddi4j.datatype.binding.*;
import org.uddi4j.datatype.tmodel.*;
import org.uddi4j.transport.*;
import org.uddi4j.response.*;
//import org.uddi4j.datatype.business.*;
import org.uddi4j.datatype.service.*;

import org.apache.log4j.Category;

/**
 * This class provides a UDDI connection for an agent.
 * The agent communicates by FIPA messages to the WSIGS's DF. 
 *
 * @author jna
 *
 */
public class DFToUDDI4j implements DFMethodListener {

	private static DFToUDDI4j anInstance;
	private Category cat = Category.getInstance(DFToUDDI4j.class.getName());
	
	private Hashtable aidToService = new Hashtable(); 
	private Hashtable aidToConcept = new Hashtable();
	private ServedOperationStore operationStore = ServedOperationStore.getInstance();
	private UDDIProxy uddiProxy;
	private String businessKey;
	private String userName;
	private String password;
	
	
	/**
	 * creates new DFToUDDI4j instance
	 *
	 */
	public DFToUDDI4j() {
		try {
			setupUDDI4j();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * gives an instance.
	 * The singleton instance is provided.
	 * 
	 * @return an instance
	 */
	public static synchronized DFToUDDI4j getInstance() {
		if ( anInstance == null ) {
			anInstance = new DFToUDDI4j();
		}
		return anInstance;
	}
	
	/**
	 * creates served operations.
	 * For each FIPA operation new ServedOperation instance is created.
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
			op = createServedOperation( agentId, sd );
			if ( null != op ) {
				coll.add( op );
			}
		}
		// return all new operations
		return coll;
	}
	
	/**
	 * creates a new servedOperation.
	 * 
	 * @param fipaSId an end point identification
	 * @return servedOperation created or null
	 */
	private ServedOperation createServedOperation( AID agentId, ServiceDescription sd ) {
		UDDIOperationIdentificator uddiOId;
		OperationID opId;
		FIPAServiceIdentificator fipaSId;
		FIPAEndPoint fipaEP;
		ServedOperation op;
		
		if( ! Configuration.WEB_SERVICE.equalsIgnoreCase(sd.getType()) ) {
				// test for web service type in properties
				String strValue = null;
				Property p = null;
				Iterator it = sd.getAllProperties();
				while( it.hasNext() ) {
					p = (Property) it.next();
					if ( "type".equalsIgnoreCase(p.getName()) ) {
						strValue = p.getValue().toString();  // the value is a String
						// cat.debug( " property Object is an instance of " + p.getValue().getClass().getName() );
						break;
					}
				}
				
				if ( null == strValue
							|| strValue.indexOf( Configuration.WEB_SERVICE ) == -1 ) {
					// a FIPA service is not a web service
					return null;
				}
		}

		fipaSId = new FIPAServiceIdentificator( agentId, sd );
		fipaEP = new FIPAEndPoint( fipaSId );
		uddiOId = ServedOperationStore.generateUDDIOperationId();
		opId = new OperationID( fipaSId, uddiOId );
		op = new ServedOperation( opId, fipaEP, true);
		
		// add structure into a WSDL
		// input data:  sd, uddiOId
		WSDLDefinition wsdl = new WSDLDefinition();
		try {
			wsdl.setURL( new URL( Configuration.getInstance().getHostURI()
					+ "/mywsdl/" + uddiOId.getWSDLOperation() + ".wsdl"));
			op.setWSDL( wsdl );
		}catch (Exception e) {
			cat.debug(e); // a url does not exist
		}
		
		cat.debug("   -> new operation: fipa_service=" + fipaSId.getServiceName()
				+ ", WSoperation=" + uddiOId.getWSDLOperation() );
		return op;
	}
	
	/**
	 * removes served operations.
	 * For each FIPA service a corresponding ServedOperation instance
	 *  is removed from ServedOperationStore.
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
		cat.debug("A wsigs's registration from an agent: " + dfad.getName() + ".");
		
		// test an existence
		if (aidToService.containsKey(dfad.getName()) ) {
			//already registered
			//original registerAction gives a responce
			cat.debug("Already registered " + dfad.getName());
			return;
		}
			
		// create and store ServedOperations into a ServedOperationStore
		Collection col = createServedOperations( dfad );
		if ( col.isEmpty() ) {
			// nothing to do
			cat.debug("No operations are created.");
			return;
		}
		// store a ServedOperation generated
		operationStore.put( col );
		
		try {
			// create identification names
			String tName = "WSIGS's tModel for " + dfad.getName().getLocalName();
			String sName = "WSIGS's businessService for " + dfad.getName().getLocalName();
			
			// create a new tModel
			TModel tModel;
			tModel = createTModel( extractWsdlUrl(col), tName);
			
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
			ServiceDetail serviceDetail = uddiProxy.save_service(getAuthToken().getAuthInfoString(),services);

			// get a service key returned
			Vector businessServices = serviceDetail.getBusinessServiceVector();
			BusinessService businessServiceReturned = (BusinessService)(businessServices.elementAt(0));
			String serviceKey = businessServiceReturned.getServiceKey();

			// create bindingTemplate
			createBindingTemplate(
					extractAccessPoint( col ),
					new ServiceKey( serviceKey ),
					new TModelKey(tModel.getTModelKey()) );

			//store reference for AID and new service for removing the service
			aidToService.put( dfad.getName(), new ServiceKey(serviceKey));
			cat.debug("An agent services registration done.");

		}catch ( UDDIException e ) {
			cat.error(e);
		}catch ( TransportException e ) {
			cat.error(e);
		}
		
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
		cat.debug("A wsigs's deregistration from an agent: " + dfad + ".");
		
		// test an existence
		if (!aidToService.containsKey(dfad.getName()) ) {
			// is not registered
			//original deregisterAction gives a responce
			return;
		}

		// remove all operations owned by an agent from the ServedOperationStore
		removeServedOperations( dfad );
		
		//delete service identified by the Key stored
		ServiceKey k = (ServiceKey) aidToService.get( dfad.getName());
		aidToService.remove( dfad.getName());
		cat.debug("A serviceKey removed " + k.getText());

		DispositionReport dr;
		try {
			// delete tModel
			BusinessService bs = takeService( k );
			BindingTemplate bt = bs.getBindingTemplates().get(0);
			TModelKey tModelKey = new TModelKey(
				bt.getTModelInstanceDetails().get(0).getTModelKey() );
			dr = uddiProxy.delete_tModel(getAuthToken().getAuthInfoString(), tModelKey.getText() );
			if( ! dr.success() ) {
				cat.error("Error during deletion of TModel\n"+
						"\n operator:" + dr.getOperator() +
						"\n generic:"  + dr.getGeneric() );
			}

			// delete a service
			dr = uddiProxy.delete_service(getAuthToken().getAuthInfoString(), k.getText());
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
	 * performs modification
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * 
	 * @param modify
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void modifyAction( Modify modify, AID aid ) throws FIPAException {
		DFAgentDescription dfad = (DFAgentDescription) modify.getDescription();
		cat.debug("A wsigs's modification from an agent: " + dfad + ".");
		// modification of the agent's tModel
		//  - a coresponding wsdl structure may be afected
		//  - identificators and categories may be changed
		//       (a management is not proposed still)
		//  - a servedOperation structure may be changed
		
		// update ServedOperationStore
		//  find ServedOperations for an agent in a ServedOperationStore
		//  create a ServedOperation if it is not found
		//  delete one if it is not registered again
		ServiceDescription sd;
		FIPAServiceIdentificator fipaSId;
		ServedOperation op;
		Iterator i = dfad.getAllServices();
		Collection col = new HashSet();
		while ( i.hasNext() ) {
			sd = (ServiceDescription) i.next();
			fipaSId = new FIPAServiceIdentificator( aid, sd );
			op = operationStore.find( fipaSId );
			if ( null == op ) {
				// create a new servedOperation
				op = createServedOperation( aid, sd );
				if ( null == op ) {
					// is not operation for web service
					continue;
				}
				operationStore.put( op );
			}
			col.add(op);
		}
		i = operationStore.findAllServedOperations( aid ).iterator();
		while ( i.hasNext() ) {
			op = (ServedOperation) i.next();
			if ( ! col.contains(op) ) {
				// delete a servedOperation
				operationStore.remove( op );
			}
		}
	}
	
	/**
	 * performs searching.
	 * The list exists after superclasse's call and is passed by the generatedList parameter. 
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * An implementation is empty now, nothing special is required for a searching.
	 * 
	 * @param search searching request
	 * @param aid requester
	 * @param generatedList already generated list by suprclass
	 * @return generatedList afected
	 * @throws FIPAException
	 */
	public List searchAction( Search search, AID aid, List generatedList ) throws FIPAException {
		// nothing advanced, generatedList is only returned
		return generatedList;
	}
	

	/**
	 * sets up the DFToUDDI4j. It starts components required.
	 * Class fields authToken and uddiProxy are set properly as main result.  
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

			// Select the desired UDDI server node
			try {
				uddiProxy.setInquiryURL(c.getQueryManagerURL());
				uddiProxy.setPublishURL(c.getLifeCycleManagerURL());
			}catch( Exception e ) {
				cat.error(e);
			}
		}
	}
	
	/**
	 * asks for an authentification.
	 * Configuration's name and password are used.
	 * 
	 * @return an authentification
	 * @throws TransportException if transport problems are occured
	 * @throws UDDIException if UDDI problems are occured
	 */
	private AuthToken getAuthToken() throws TransportException, UDDIException{
		// Get an authorization token
		cat.debug("Ask for authToken.");

		// Pass in userid and password registered at the UDDI site
		AuthToken authToken = uddiProxy.get_authToken( userName,password);
		cat.debug("Returned authToken from a UDDI:" + authToken.getAuthInfoString());
		return authToken;
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
			BindingDetail bindingDetail = uddiProxy.save_binding(
					getAuthToken().getAuthInfoString(),
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
			TModelDetail tModelDetail = uddiProxy.save_tModel(
					getAuthToken().getAuthInfoString(),
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
	
	/**
	 * extract a wsdl's URL from servedOperations.
	 * A wsdl's document is the same for all FIPA services provided by an agent.
	 * 
	 * @param col a collection of servedOperations
	 * @return a wsdl's URL as String
	 */
	private String extractWsdlUrl( Collection col ) {
		if ( col.isEmpty() ) {
			return "";
		}
		Iterator i = col.iterator();
		ServedOperation op = (ServedOperation) i.next();
		return op.getWSDL().getURL().toString();
	}
	
	/**
	 * extract an accessPoint from servedOperations.
	 * An accessPoint is the same for all FIPA services provided by an agent.
	 * 
	 * @param col a collection of servedOperations
	 * @return an accessPoint
	 */
	private AccessPoint extractAccessPoint( Collection col ) {
		if ( col.isEmpty() ) {
			return new AccessPoint( Configuration.getInstance().getHostURI(), "http");
		}
		Iterator i = col.iterator();
		ServedOperation op = (ServedOperation) i.next();
		return new AccessPoint(
				op.getOperationID().getUDDIOperationIdentificator().getAccessPoint(),
				"http");
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
}
