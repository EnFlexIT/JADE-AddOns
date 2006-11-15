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

import java.util.*;

import org.apache.log4j.Category;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.BusinessList;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;

import com.whitestein.wsig.Configuration;


/**
 * @author jna
 *
 * Tests an exchange of UDDI data structures. 
 */
public class TestUDDI4j {

	private Category cat = Category.getInstance(TestUDDI4j.class.getName());
	private UDDIProxy uddiProxy;
	private AuthToken authToken;

	/**
	 * saves a business into a UDDI
	 *
	 */
	public void saveBusiness() {
		if (uddiProxy == null) {
			throw new RuntimeException("uddiProxy not initialized");
		}
		cat.debug("Enter into a saveBusines method.");
		cat.info(" debug is : " + cat.isDebugEnabled());
		BusinessDetail busDetail = null;
	    try {
	      BusinessEntity bEntity = new BusinessEntity();
	      bEntity.setDefaultName(new Name("ABC"));
	      Vector busVector = new Vector();
	      busVector.add(bEntity);

	      bEntity = new BusinessEntity();
	      bEntity.setDefaultName(new Name("CDE"));
	      busVector.add(bEntity);

	      busDetail = uddiProxy.save_business(authToken.getAuthInfoString(), busVector);
	      Vector v = busDetail.getBusinessEntityVector();
	      //assertEquals(victor.size(),2);
	    }
	    catch (UDDIException ex) {
	      //fail(ex.toString());
	    	ex.printStackTrace();
	    }
	    catch (TransportException ex) {
	      //fail(ex.toString());
	    	ex.printStackTrace();
	    }
	    finally {
	      //cleanupBusinessDetail(busDetail);
	    }

	}
	
	/**
	 * tests UDDI's searching activity
	 *
	 */
	public void searchTest() {
		if (uddiProxy == null) {
			throw new RuntimeException("uddiProxy not initialized");
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
			BusinessList businessList = uddiProxy.find_business(names, null, null, null,null,findQualifiers,5);
			Vector businessInfoVector  = businessList.getBusinessInfos().getBusinessInfoVector();
			for( int i = 0; i < businessInfoVector.size(); i++ )
			{
				BusinessInfo businessInfo = (BusinessInfo)businessInfoVector.elementAt(i);

				// Print name for each business
				cat.debug(" A name has been found: " + businessInfo.getDefaultNameString());
			}			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * initializes a UDDI repository.
	 * This method must be called before any test's methods from this class.
	 */
	public void initializeUDDI() {
		Configuration c = Configuration.getInstance();
		String userName, password;
		synchronized ( c ) {
			// synchronized on main Configuration instance
			// to prevent changes in configuration
			
			System.setProperty( Configuration.KEY_UDDI4J_LOG_ENABLED,
					c.getUDDI4jLogEnabled());
			System.setProperty( Configuration.KEY_UDDI4J_TRANSPORT_CLASS,
					c.getUDDI4jTransportClass());
			userName = c.getUserName();
			password = c.getUserPassword();

			uddiProxy = new UDDIProxy();
			// Select the desired UDDI server node
			try {
				uddiProxy.setInquiryURL(c.getQueryManagerURL());
				uddiProxy.setPublishURL(c.getLifeCycleManagerURL());

				// Get an authorization token
				cat.debug("An authToken is asked for.");

				// Pass in userid and password registered at the UDDI site
				authToken = uddiProxy.get_authToken( userName,password);
				cat.debug("Returned authToken from a UDDI:" + authToken.getAuthInfoString());


			}catch( Exception e ) {
				cat.error(e);
			}
		}
	}

	/**
	 * adds new business for the WSIGS.
	 * A businessKey is stored into the Configuration. 
	 *
	 */
	public void createNewBusiness() {
		if (uddiProxy == null) {
			throw new RuntimeException("uddiProxy not initialized");
		}
		// new business for gateway is created
		//BusinessEntity be = new BusinessEntity("", Configuration.getInstance().getGatewayName());
		BusinessEntity be = new BusinessEntity("","WSIGS");

		Vector entities = new Vector();
		entities.addElement(be);

		BusinessDetail bd = null;
		try {
			// Save business into UDDI
			bd = uddiProxy.save_business(authToken.getAuthInfoString(),entities);
		}catch ( Exception e ) {
			cat.error( e );
			return;
		}

		// Process returned BusinessDetail object to get the
		// busines key.
		Vector businessEntities = bd.getBusinessEntityVector();
		BusinessEntity returnedBusinessEntity = (BusinessEntity)(businessEntities.elementAt(0));
		String businessKey = returnedBusinessEntity.getBusinessKey();

		// to store a businessKey generated
		Configuration.getInstance().setBusinessKey( businessKey );
		Configuration.store();

		cat.info("New gateway's business key is " + businessKey + " .");
	}

	
	public static void main(String[] args) {
		TestUDDI4j test = new TestUDDI4j();
		test.initializeUDDI();
		//test.createNewBusiness();
		//test.saveBusiness();
		test.searchTest();
	}
}
