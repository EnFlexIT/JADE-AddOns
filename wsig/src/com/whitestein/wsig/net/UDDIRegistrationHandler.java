/*
 * Created on Nov 24, 2004
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
package com.whitestein.wsig.net;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Hashtable;
import java.util.Collection;

import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Name;

import org.apache.log4j.Category;

import com.whitestein.wsig.fipa.FIPAServiceIdentificator;
import com.whitestein.wsig.fipa.GatewayAgent;
import com.whitestein.wsig.struct.OperationID;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.whitestein.wsig.ws.UDDIOperationIdentificator;
import com.whitestein.wsig.ws.WSDLDefinition;
import com.whitestein.wsig.ws.WSEndPoint;

/**
 * @author jna
 *
 * Handles a request for a registration's activity.
 * 
	 According an operation name do an action.
	 Actions marked as a publish (from jUDDI, UDDIProxy.java):
	 <pre>
	   add_publisherAssertions
	   get_assertionStatusReport
	   get_publisherAssertions
	   delete_binding  
	   delete_business 
	   delete_service  
	   delete_tModel   
	   delete_publisherAssertions
	   discard_authToken 
	   get_authToken 
	   get_registeredInfo
	   save_binding
	   save_business
	   save_service
	   save_tModel
	   set_publisherAssertions
	  </pre> 
 */
public class UDDIRegistrationHandler extends Redirector {
	
	private static Category cat = Category.getInstance(UDDIRegistrationHandler.class.getName());
	private static final HashSet saveOperations = new HashSet();
	private static final HashSet deleteOperations = new HashSet();
	
	private static Hashtable tModelKeyToWSDL = new Hashtable();
	
	// according a operation name do actions
	// actions as a publish (from jUDDI, UDDIProxy.java):
	//   add_publisherAssertions
	//   get_assertionStatusReport
	//   get_publisherAssertions
	//   delete_binding  
	//   delete_business 
	//   delete_service  
	//   delete_tModel   
	//   delete_publisherAssertions
	//   discard_authToken 
	//   get_authToken 
	//   get_registeredInfo
	//   save_binding
	//   save_business
	//   save_service
	//   save_tModel
	//   set_publisherAssertions 
	//   ============================

	static {
		deleteOperations.add( "delete_binding" );
		deleteOperations.add( "delete_business" );
		deleteOperations.add( "delete_service" );
		deleteOperations.add( "delete_tModel" );
		saveOperations.add( "save_binding" );
		saveOperations.add( "save_business" );
		saveOperations.add( "save_service" );
		saveOperations.add( "save_tModel" );

	}

	/**
	 * stores a wsdl for a tModelKey
	 * 
	 * @param tModelKey
	 * @param wsdl
	 */
	public static synchronized void putTModelKeyAndWSDL( String tModelKey, WSDLDefinition wsdl ) {
		tModelKeyToWSDL.put( tModelKey, wsdl );
		//cat.debug("A tModel: " + tModelKey + " uses a wsdl " + wsdl.getURL());
	}
	
	/**
	 * gives a wsdl for a tModelKey
	 * 
	 * @param tModelKey
	 * @return a WSDL Definition
	 */
	public static synchronized WSDLDefinition getWsdlForTModelKey( String tModelKey ) {
	    WSDLDefinition wsdl = (WSDLDefinition) tModelKeyToWSDL.get( tModelKey );
		// cat.debug("A tModel: " + tModelKey + " reffers into wsdl " + wsdl.getURL());
		return wsdl;
	}
	
	/**
	 * removes a tModelKey
	 * 
	 * @param tModelKey
	 */
	public static synchronized void removeTModelKey( String tModelKey ) {
		tModelKeyToWSDL.remove( tModelKey );
	}
	
	/**
	 * performs a request.
	 * The close method is used to close a call blocked.
	 * 
	 * @param header HTTP request's header
	 * @param soap  HTTP request's body
	 * @return a response
	 */
	public SOAPMessage doRequest( Properties header, SOAPMessage soap ) {
		SOAPMessage retSOAP = null;
		HttpURLConnection c = null;
		try {

			// do a transaction's behaviour from a Redirector
			if ( ! ( isSave( soap ) || isDelete( soap ))) {
				return super.doRequest( header, soap );
			}
			
			Collection operations = null;
			Collection tModels = null;
			boolean isDelete = isDelete(soap);
			if ( isDelete ) {
				// retrieve and save internaly informations
			    try {
			        operations = findOperationsDeleted(soap);
			    }catch ( Exception e ) {
			        // can not find any usable informations
			        operations = new ArrayList();
			    }
			    try {
				    tModels = findTModelsDeleted(soap);
			    }catch ( Exception e ) {
			        // can not find any usable informations
			        tModels = new ArrayList();
			    }
			}
			
			// send a request to UDDI
			c = WSEndPoint.sendHTTPRequest( target, soap );
			
			// read a response
			// receive a response
			retSOAP = WSEndPoint.receiveHTTPResponse( c );
			
			// release resources
			//c.getOutputStream().close(); // exception: Cannot write output after reading input
			c.getInputStream().close();
			c.disconnect();
	
			// also send a request into a gateway's DF
			//cat.debug(" A request is sending into gateway's DF too.");
			
			try {
			    if ( ! isDelete ) {
			        informDF( soap, retSOAP );
			// construct anACL from a SOAP UDDI's request
			//   operations names are needed
			//   otologies' names are needed
			//   an UDDI businessKey as an owner
			//   some informations may be presented by properties
			//   languages = SL0
			//   protocols = fipa-request
			
			// send the request by GatewayAgent
			//GatewayAgent.getInstance().send(
			//		anACL, listener);
			    }else {
					SOAPBody body = retSOAP.getSOAPPart().getEnvelope().getBody();
					if ( body.getFault() != null ) {
						// soap is a fault
					}else {
					    // delete operations and/or tModels
					    deleteTModels( tModels );
					    deleteOperations( operations );
					}
			    }
			}catch ( Exception e ) {
				// if something goes wrong then undo a request in UDDI
				// roll back
			}
			
		}catch (MalformedURLException mfe) {
			cat.error( mfe );
		}catch (SOAPException se) {
			cat.error(se);
		}catch (IOException ioe) {
			cat.error(ioe);
		}

		return retSOAP;
	}

	/**
	 * checks if a soap is a save
	 * 
	 * @param soap tested SOAP
	 * @return true, if it is a save
	 */
	public boolean isSave( SOAPMessage soap ) {
		try{
		if ( soap != null && soap.getSOAPPart() != null
				&& soap.getSOAPPart().getEnvelope() != null
				&& soap.getSOAPPart().getEnvelope().getBody() != null ) {
			Iterator it = soap.getSOAPPart().getEnvelope().getBody().getChildElements();
			if( ! it.hasNext() ) {
				return false;
			}
			SOAPBodyElement bEl =  (SOAPBodyElement) it.next();
			String name = bEl.getElementName().getLocalName();
			return saveOperations.contains( name );
		}
		}catch (SOAPException se) {
			cat.error( se );
		}
		return false;
	}
	
	/**
	 * checks if a soap is a delete
	 * 
	 * @param soap tested SOAP
	 * @return true, if it is a delete
	 */
	public boolean isDelete( SOAPMessage soap ) {
		try{
		if ( soap != null && soap.getSOAPPart() != null
				&& soap.getSOAPPart().getEnvelope() != null
				&& soap.getSOAPPart().getEnvelope().getBody() != null ) {
			Iterator it = soap.getSOAPPart().getEnvelope().getBody().getChildElements();
			if( ! it.hasNext() ) {
				return false;
			}
			SOAPBodyElement bEl =  (SOAPBodyElement) it.next();
			String name = bEl.getElementName().getLocalName();
			return deleteOperations.contains( name );
		}
		}catch (SOAPException se) {
			cat.error( se );
		}
		return false;
	}
	
	/**
	 * creates a new ServedOperation
	 * 
	 * @param wsId an end point identifier
	 * @param wsdl a wsdl definition
	 * @return operation created
	 */
	private ServedOperation createServedOperation( UDDIOperationIdentificator wsId, WSDLDefinition wsdl ) {
		OperationID opId;
		FIPAServiceIdentificator fipaSId;
		WSEndPoint wsEP;
		ServedOperation op;

		op = ServedOperationStore.getInstance().find( wsId );
		if ( null == op ) {
			wsEP = new WSEndPoint( wsId );
			fipaSId = ServedOperationStore.generateFIPAServiceId();
			opId = new OperationID( fipaSId, wsId );
			
			op = new ServedOperation( opId, wsEP, true);
			// op = new ServedOperation( opId, wsEP,
			//				! wsdl.isOneWayOperation( wsId.getWSDLOperation()) );
			op.setWSDL( wsdl );
			ServedOperationStore.getInstance().put( op );
			cat.debug("   -> new operation: fipa_service=" + fipaSId.getServiceName()
					+ ", WSoperation=" + wsId.getWSDLOperation() );
			GatewayAgent.getInstance().addToDFAgentDescription( op );
		}else {
			// already is served
			op = null;
		}
		
		return op;
	}
	
	/**
	 * creates operations reffered
	 * 
	 * @param accessPoint a point to call an operation
	 * @param tModelKeys refference into a wsdl
	 * @return a collection of new operations created
	 */
	private synchronized Collection createServedOperations( String accessPoint, Collection tModelKeys ) {
	    cat.debug("Enter into createServedOperations.");
		Iterator it;
		Iterator itOps;
		Collection col = new ArrayList();
		String tModelKey, opName;
		WSDLDefinition wsdl;
		UDDIOperationIdentificator uddiId;
		ServedOperation so;
		
		it = tModelKeys.iterator();
		while ( it.hasNext()) {
			tModelKey = (String) it.next();
			wsdl = getWsdlForTModelKey( tModelKey );
			itOps = wsdl.getAllOperations().iterator();
			while ( itOps.hasNext()) {
				opName = (String) itOps.next();
				uddiId = new UDDIOperationIdentificator(accessPoint, opName);
				so = createServedOperation( uddiId, wsdl);
				if ( so != null ) {
					col.add( so );
					cat.debug("A new operation added." );
				}
			}
		}
		return col;
	}
	
	private synchronized void deleteOperations( Collection col ) {
		OperationID opId;
		FIPAServiceIdentificator fipaSId;
		WSEndPoint wsEP;
		ServedOperation op;

		Iterator it = col.iterator();
		while ( it.hasNext() ) {
		    // must be treated differently an indirect or refferenced operation 
			
		    //op = ServedOperationStore.getInstance().find( wsId );
		    op = (ServedOperation) it.next();
			if ( null != op ) {
				ServedOperationStore.getInstance().remove( op );
				GatewayAgent.getInstance().removeFromDFAgentDescription( op );
			}
		}
	}

	private Collection findBusinessOperations( SOAPElement actual ) throws Exception {
		Collection col = new ArrayList();
		return col;
	}
	
	private Collection findServiceOperations( SOAPElement actual ) throws Exception {
		Collection col = new ArrayList();
		return col;
	}
	
	private Collection findBindingOperations( SOAPElement actual ) throws Exception {
		Collection col = new ArrayList();
		return col;
	}
	
	private Collection findOperationsDeleted( SOAPMessage request ) throws Exception {
	    cat.debug("Enter into findOperationsDeleted.");
		SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
		SOAPElement el;
		Iterator it = body.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String name = el.getElementName().getLocalName();
			if ( "delete_business".equals( name ) ) {
				return findBusinessOperations( el );
			}else if ( "delete_service".equals( name ) ) {
				return findServiceOperations( el );
			}else if ( "delete_binding".equals( name ) ) {
				return findBindingOperations( el );
			}else if ( "delete_tModel".equals( name ) ) {
			    return new ArrayList();
			}
		}
	    return new ArrayList();
	}
	
	private Collection findTModelsDeleted( SOAPMessage request ) throws Exception {
	    cat.debug("Enter into findTModelsDeleted.");
		SOAPBody body = request.getSOAPPart().getEnvelope().getBody();
		SOAPElement el, subEl;
		Collection col = new ArrayList();
		Iterator subIt, it = body.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String name = el.getElementName().getLocalName();
			if ( "delete_tModel".equals( name ) ) {
				subIt = el.getChildElements();
				while ( subIt.hasNext() ) {
					subEl = (SOAPElement) subIt.next();
					name = subEl.getElementName().getLocalName();
					if( "tModelKey".equals(name)) {
					    col.add( subEl.getValue() );
					}
				}
				return col;
			}
		}
	    return new ArrayList();
	}
	
	
	
	private void informDF( SOAPMessage request, SOAPMessage response ) throws Exception {
	    cat.debug("Enter into informDF.");
		SOAPBody body = response.getSOAPPart().getEnvelope().getBody();
		if ( body.getFault() != null ) {
			// soap is a fault
			throw new Exception( "UDDI's fault. Reasons: " + body.getFault().getFaultCode() + ", " + body.getFault().getFaultString());
		}else {
			SOAPElement el;
			Iterator it = body.getChildElements();
			while ( it.hasNext() ) {
				el = (SOAPElement) it.next();
				String name = el.getElementName().getLocalName();
				if ( "businessDetail".equals( name ) ) {
					doBusinessDetail( el );
				}else if ( "serviceDetail".equals( name ) ) {
					doServiceDetail( el );
				}else if ( "bindingDetail".equals( name ) ) {
					doBindingDetail( el );
				}else if ( "tModelDetail".equals( name ) ) {
					doTModelDetail( el );
				}else if ( "dispositionReport".equals( name ) ) {
					// a request is used for informations
					body = request.getSOAPPart().getEnvelope().getBody();
					// keys are stored in a body
					//   related informations must be retrieved before a delete
				}
			}
		}
	}
	
	private void doBusinessDetail( SOAPElement actual ) throws Exception {
		SOAPElement el;
		Iterator it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String name = el.getElementName().getLocalName();
			if ( "businessEntity".equals( name ) ) {
				// doBusinessEntity( el );
			}
		}
	}

	private void doServiceDetail( SOAPElement actual ) throws Exception {
		SOAPElement el;
		Iterator it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String name = el.getElementName().getLocalName();
			if ( "businessService".equals( name ) ) {
				doBusinessService( null, el );
			}
		}
	}

	private void doBindingDetail( SOAPElement actual ) throws Exception {
		SOAPElement el;
		Iterator it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String name = el.getElementName().getLocalName();
			if ( "bindingTemplate".equals( name ) ) {
				doBindingTemplate( null, null, el );
			}
		}
	}
	
	private void doTModelDetail( SOAPElement actual ) throws Exception {
		SOAPElement el;
		Iterator it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String name = el.getElementName().getLocalName();
			if ( "tModel".equals( name ) ) {
				doTModel( el );
			}
		}
	}
	
	private void doBusinessEntity( SOAPElement actual ) throws Exception {
		SOAPElement el, subEl;
		Name attr;
		String businessKey = null, name;
		Iterator subIt;
		Iterator it = actual.getAllAttributes();
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "businessKey".equals( attr.getLocalName()) ) {
				businessKey = actual.getAttributeValue( attr );
			}
		}
		
		// go through all elements
		it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			name = el.getElementName().getLocalName();
			if ( "businessServices".equals( name ) ) {
				subIt = el.getChildElements();
				
				// process bindingTemplates
				while ( subIt.hasNext() ) {
					subEl = (SOAPElement) it.next();
					name = subEl.getElementName().getLocalName();
					if ( "businessService".equals( name ) ) {
						doBusinessService( businessKey, subEl );
					}
				}
			}else if ( "description".equals( name ) ) {
			}else if ( "categoryBag".equals( name ) ) {
			}else if ( "name".equals( name ) ) {
			}else if ( "discoveryURLs".equals( name ) ) {
			}else if ( "contacts".equals( name ) ) {
			}else if ( "identifierBag".equals( name ) ) {
			}else if ( "Signature".equals( name ) ) {
			}
		}
	}

	private void doBusinessService( String businessKey, SOAPElement actual ) throws Exception {
		SOAPElement el, subEl;
		Name attr;
		String serviceKey = null, name;
		Iterator subIt;
		Iterator it = actual.getAllAttributes();
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "serviceKey".equals( attr.getLocalName()) ) {
				serviceKey = actual.getAttributeValue( attr );
			}else if ( "businessKey".equals( attr.getLocalName())) {
				if ( null == businessKey ) {
					businessKey = actual.getAttributeValue( attr );
				}else if ( ! businessKey.equals(actual.getAttributeValue( attr ))) {
					// a service projection
					//   is already served 
					return;
				}
			}
		}
		if ( null == serviceKey || null == businessKey ) {
			// gateway can not serve
			return;
		}
		
		// go through all elements
		it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			name = el.getElementName().getLocalName();
			if ( "bindingTemplates".equals( name ) ) {
				subIt = el.getChildElements();
				
				// process bindingTemplates
				while ( subIt.hasNext() ) {
					subEl = (SOAPElement) it.next();
					name = subEl.getElementName().getLocalName();
					if ( "bindingTemplate".equals( name ) ) {
						doBindingTemplate( businessKey, serviceKey, subEl );
					}
				}
			}else if ( "description".equals( name ) ) {
			}else if ( "categoryBag".equals( name ) ) {
			}else if ( "name".equals( name ) ) {
			}else if ( "Signature".equals( name ) ) {
			}
		}
	}

	private String getAttribute( String attribute, SOAPElement actual ) {
		String val = null;
		Name attr;
		Iterator it = actual.getAllAttributes();
		
		if ( null == attribute ) {
		    return null;
		}
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( attribute.equals( attr.getLocalName()) ) {
				val = actual.getAttributeValue( attr );
			}
		}
		return val;
	}
	
	private String getUseType( SOAPElement actual ) {
		String useType = null;
		Name attr;
		Iterator it = actual.getAllAttributes();
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "useType".equals( attr.getLocalName()) ) {
				useType = actual.getAttributeValue( attr );
			}
		}
		return useType;
	}
	
	private void doBindingTemplate( String businessKey, String serviceKey, SOAPElement actual ) throws Exception {
	    // cat.debug("Enter into doBindingTemplate.");
		SOAPElement el, subEl, accessPoint = null;
		Name attr;
		String name, bindingKey = null;
		String accessPointURLType = null;
		String accessPointContent = null, accessPointUseType = null;
			// only one accessPointContent/UseType is determinated per a bindingTemplate
		Collection tModelKeys = new HashSet();
		Iterator subIt;
		Iterator it = actual.getAllAttributes();
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "bindingKey".equals( attr.getLocalName()) ) {
				bindingKey = actual.getAttributeValue( attr );
			}else if ( "serviceKey".equals( attr.getLocalName())) {
				if ( null == serviceKey ) {
					serviceKey = actual.getAttributeValue( attr );
				}
			}
		}
		if ( null == businessKey ) {
			// retrieve a businessKey by serviceKey
		}
		if ( null == serviceKey || null == bindingKey ) {
			// gateway can not serve
			return;
		}
		
		// go through all elements
		it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			name = el.getElementName().getLocalName();
			if ( "accessPoint".equals( name ) ) {
				accessPoint = el;
				accessPointContent = el.getValue();
				accessPointUseType = getUseType(el); // UDDI v3.0
				if (null == accessPointUseType ) {
				    accessPointUseType = "";  // to avoid a null value
				}
				accessPointURLType = getAttribute( "URLType", el );
			}else if ( "hostingRedirector".equals( name ) ) {
				// it contains only a bindingKey attribute
				subIt = el.getAllAttributes();
				while( subIt.hasNext() ) {
					attr = (Name)subIt.next(); 
					if ( "bindingKey".equals( attr.getLocalName()) ) {
						bindingKey = el.getAttributeValue( attr );
					}
				}
				if ( bindingKey != null ) {
					// use the new bindingKey to get bindingTemplate
					// a bindingTemplate redirected is already served by gateway,
					//   it may be a usable information
					cat.debug( "A hostingRedirector has a new bindingKey " + bindingKey );
					//return;
					accessPointContent = bindingKey;
					accessPointUseType = "bidingTemplate"; // a UDDI v3.0 equivalent
				}else {
					throw new Exception("A hostingRedirector has a null's bindingKey.");
				}
			}else if ( "tModelInstanceDetails".equals( name ) ) {
				subIt = el.getChildElements();
				while( subIt.hasNext() ) {
					subEl = (SOAPElement) subIt.next();
					name = subEl.getElementName().getLocalName();
					if ( "tModelInstanceInfo".equals( name ) ) {
						tModelKeys.add( doTModelInstanceInfo( subEl ));
					}
				}
			}else if ( "description".equals( name ) ) {
			}
		}
		
		if ( tModelKeys.isEmpty() ) {
			// gateway can not serve
		}else if ( "http".equals(accessPointURLType) || accessPointUseType.equals("endPoint")) {
			// collect all operations
			Collection col = createServedOperations( accessPointContent, tModelKeys); //, bindingKey);
			// create a refference for a latter removing
			//    col, bindingKey, 
		}else if ( accessPointUseType.equals("bindingTemplate")) {
			// load another bindingTemplate
			// check a newer ending or long loop
			//doBindingTemplate(1,accessPointContent);
		}else if ( accessPointUseType.equals("hostingRedirector")) {
			// another UDDI repository must be contacted
			//   another password may be needed
			//   a special operation for agent must be introduced
			//     to obtain a security token for a password
			// accessPoint are stored in it
		}else if ( accessPointUseType.equals("wsdlDeployment")) {
			// read a wsdl document and accessPoint can be found there
			//  also the necessary binding information is there
			//wsdlDeployment( accessPointContent );
		}
	}

	private String doTModelInstanceInfo( SOAPElement actual ) throws Exception {
		SOAPElement el, subEl;
		Name attr;
		String name, tModelKey = null;
		Iterator subIt;
		Iterator it;
		WSDLDefinition subWSDL;
		
		/*
		if ( null == businessKey || null == serviceKey || null == bindingKey ) {
			// gateway can not serve
			return null;
		}
		*/

		// go through all attributes
		it = actual.getAllAttributes();
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "tModelKey".equals( attr.getLocalName()) ) {
				tModelKey = actual.getAttributeValue( attr );
				// process tModelKey
				// cat.debug("A tModelKey is occured : " + tModelKey);
			}
		}
		
		// go through all elements
		it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			name = el.getElementName().getLocalName();
			if ( "description".equals( name ) ) {
			}else if ( "instanceDetails".equals( name ) ) {
				subIt = el.getChildElements();
				while( subIt.hasNext() ) {
					subEl = (SOAPElement) subIt.next();
					name = subEl.getElementName().getLocalName();
					if ( "description".equals( name ) ) {
					}else if ( "instanceParms".equals( name ) ) {
						//  content may be a URL
					}else if ( "overviewDoc".equals( name ) ) {
						// only a subset of a functionality to compare with TModel occurence
						try {
							subWSDL = doOverviewDocInTModel( subEl );
						}catch (Exception e) {
							// the gateway can not serve
						}
					}
				}
			}
		}
		
		// the tModelKey is known now
		//  and all specific settings may be collected for a TModel's instance
		if ( null == tModelKey ) {
			throw new Exception( "A tModelKey is required." );
		}
		return tModelKey;
	}

	/**
	 * handles a OverviewDoc element.
	 * A wsdl document and more descriptions may be stored in the UDDI's element.
	 * 
	 * @param actual a OverviewDoc UDDI's element to be processed
	 * @return a wsdl documentation reffered
	 * @throws Exception if a problem is occured
	 */
	private WSDLDefinition doOverviewDocInTModel( SOAPElement actual ) throws Exception {
		SOAPElement el, subEl;
		Name attr;
		String name, tModelKey;
		WSDLDefinition wsdl = null;
		Iterator subIt;
		Iterator it;
		
		// go through all elements
		it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			name = el.getElementName().getLocalName();
			if ( "description".equals( name ) ) {
			}else if ( "overviewURL".equals( name ) ) {
				// wsdl may be here
				// only one time
				wsdl = doOverviewURL( el );
			}
		}
		
		if ( wsdl != null ) {
			// store and use the wsdl
		}
		
		return wsdl;
	}

	private WSDLDefinition doOverviewURL( SOAPElement actual ) throws Exception {
		Name attr;
		String val;
		URI uri = null;
		Definition definition;
		WSDLDefinition wsdl = null;
		boolean isWSDLinterface = false;
		Iterator it = actual.getAllAttributes();
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "useType".equals( attr.getLocalName()) ) {
				val = actual.getAttributeValue( attr );
				if ( "wsdlIterface".equals(val) ) {
					uri = new URI( actual.getValue());
					// retrieve a wsdl document
					isWSDLinterface = true;
				}else if ( "text".equals(val) ) {
					// gateway can not serve
					return null;
				}
			}
		}

		if ( null == uri ) {
			// try to use a document reffered as a wsdl document
			try {
				uri = new URI( actual.getValue());
			}catch ( NullPointerException ne ) {
				cat.debug( "No content." );
				return null;
			}catch ( URISyntaxException uriEx ) {
				cat.debug( "A bad URI : " + uri.toString());
				return null;
			}
		}

		// create a WSDLDefinition
		try {
			WSDLReader reader =	WSDLFactory.newInstance().newWSDLReader();
			definition = reader.readWSDL( uri.toString() );
			wsdl = new WSDLDefinition();
			wsdl.setURL( uri.toURL());
			wsdl.setDefinition( definition );
		}catch ( WSDLException wsdlEx ) {
			cat.debug( "No WSDL file at " + uri.toString() );
			if ( isWSDLinterface ) {
				// there must be a good WSDL file at the URI
				throw new Exception("It is not a WSDL file.");
			}else {
				return null;
			}
		}
		return wsdl;
	}

	/**
	 * stores a TModel.
	 * Important parts are stored and used.
	 * 
	 * @param actual a TModel UDDI's structure
	 * @throws Exception
	 */
	private void doTModel( SOAPElement actual ) throws Exception {
		SOAPElement el, subEl;
		Name attr;
		String name, tModelKey = null;
		WSDLDefinition wsdl = null;
		Iterator subIt;
		Iterator it = actual.getAllAttributes();
		
		// go through all attributes
		while( it.hasNext() ) {
			attr = (Name) it.next();
			if ( "tModelKey".equals( attr.getLocalName()) ) {
				tModelKey = actual.getAttributeValue( attr );
			}else if ( "operator".equals( attr.getLocalName())) {
			}else if ( "authorizedName".equals( attr.getLocalName())) {
			}
		}

		if ( null == tModelKey ) {
			// gateway can not serve
			throw new Exception("A tModelKey is null in a TModel.");
		}
		
		// go through all elements
		it = actual.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			name = el.getElementName().getLocalName();
			if ( "name".equals( name ) ) {
				// is URI
			}else if ( "description".equals( name ) ) {
			}else if ( "overviewDoc".equals( name ) ) {
				wsdl = doOverviewDocInTModel( el );
				// UDDI v3.0, it may be repeating 
			}else if ( "identifierBag".equals( name ) ) {
			}else if ( "categoryBag".equals( name ) ) {
			}
		}

		// create and register a new Otology for a TModel
		putTModelKeyAndWSDL( tModelKey, wsdl);
	}

	private void deleteTModels( Collection col ) {
	    String key;
		Iterator it = col.iterator();
	    
		// go through all
		while ( it.hasNext() ) {
			key = (String) it.next();
			removeTModelKey( key );
		}
	}

}
