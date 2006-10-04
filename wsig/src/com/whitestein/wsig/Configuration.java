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
package com.whitestein.wsig;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.log4j.Category;

import com.whitestein.wsig.fipa.DFMethodListener;

import jade.core.AID;

/**
 * @author jna
 *
 * keeps and makes accessible configuration for gateway.
 * Only one instance is reasonable and is accessible by getInstance() method.
 * The properties are loaded and stored from the CONFIG_FILE_NAME;
 * 
 * To prevent accidential changes in configuration's properties the synchronization
 * on getInstance() returned object must be used in your code.
 */
public class Configuration extends Properties {

	private static Configuration anInstance;
	private static Category cat = Category.getInstance(Configuration.class.getName());
	private final static boolean isDeveloping = false; //true;
	private final static String VERSION = "beta.0.6";
	public static final String WEB_SERVICE = "web-service";
	public final static String GATEWAY_NAME = "wsig";
	private final static String GATEWAY_UPPER = "WSIG";
	private final static String CONFIG_HEADER = GATEWAY_UPPER + "'s configuration file, " + VERSION;
	/**
	 * the file responsible for the configuration storage
	 */
	public final static String CONFIG_FILE_NAME = GATEWAY_NAME + ".properties";
	private final static File CONFIG_FILE = new File(CONFIG_FILE_NAME);
	
	//there are configuration properties
	public final static String KEY_GATEWAY_AID = GATEWAY_NAME + ".agent_id";
	//public final static String KEY_HOST_URI = "host_uri";
	public final static String KEY_HOST_NAME = GATEWAY_NAME + ".host.name";
	public final static String KEY_HOST_PORT = GATEWAY_NAME + ".host.port";
	public final static String KEY_ACCESS_POINT_PATH = GATEWAY_NAME + ".accessPoint.path";
	public final static String KEY_QUERY_MANAGER_PATH = GATEWAY_NAME + ".uddi.queryManagerPath";
	public final static String KEY_LIFE_CYCLE_MANAGER_PATH = GATEWAY_NAME + ".uddi.lifeCycleManagerPath";
	public final static String KEY_BUSINESS_KEY = "uddi." + GATEWAY_NAME + "_businessKey";
	public final static String KEY_LOCAL_NAMESPACE_PREFIX = GATEWAY_NAME + ".soap.localNamespacePrefix";
	public final static String KEY_XML_ATTRIBUTES = GATEWAY_NAME + ".fipa.SL0.AttributeForXmlAttributes";
	public final static String KEY_XML_ELEMENT = GATEWAY_NAME + ".fipa.SL0.AttributeForXmlElement";
	public final static String KEY_XML_CONTENT = GATEWAY_NAME + ".fipa.SL0.AttributeForXmlContent";
	public final static String KEY_XML_TAG = GATEWAY_NAME + ".fipa.SL0.PrefixForXmlTag";
	public final static String KEY_DF_TO_UDDI_CLASS = GATEWAY_NAME + ".fipa.DFToUDDIClass";
	//
	public final static String KEY_QUERY_MANAGER_URL = "uddi.queryManagerURL";
	public final static String KEY_LIFE_CYCLE_MANAGER_URL = "uddi.lifeCycleManagerURL";
//	public final static String KEY_FACTORY_CLASS = "javax.xml.registry.ConnectionFactoryClass";
	public final static String KEY_USER_NAME = "uddi.userName";
	public final static String KEY_USER_PASSWORD = "uddi.userPassword";
	public final static String KEY_UDDI4J_LOG_ENABLED = "org.uddi4j.logEnabled";
	public final static String KEY_UDDI4J_TRANSPORT_CLASS = "org.uddi4j.TransportClassName";
	public final static String KEY_TEST_GOOGLE_ACCESS_KEY = "test.Google.accessKey";
	//public final static String KEY_xyz = "xYz";
	//public final static String KEY_xyz = "xYz";
	//public final static String KEY_xyz = "xYz";

	private AID gatewayAID;
	

	public synchronized String getTestAmazonAccessKey(){
		return getProperty( KEY_TEST_GOOGLE_ACCESS_KEY );
	}
	
	public synchronized String getQueryManagerURL(){
		return getProperty( KEY_QUERY_MANAGER_URL );
	}
	
	public synchronized String getLifeCycleManagerURL(){
		return getProperty( KEY_LIFE_CYCLE_MANAGER_URL );
	}

/*
	public synchronized String getFactoryClass(){
		return getProperty( KEY_FACTORY_CLASS );
	}
*/	
	public synchronized String getUserName() {
		return getProperty( KEY_USER_NAME );
	}
	
	public synchronized String getUserPassword() {
		return getProperty( KEY_USER_PASSWORD );
	}

	public synchronized String getBusinessKey() {
		return getProperty( KEY_BUSINESS_KEY );
	}
	
	public synchronized void setBusinessKey( String businessKey ) {
		setProperty( Configuration.KEY_BUSINESS_KEY, businessKey );
	}

	public synchronized String getLocalNamespacePrefix() {
		return getProperty( KEY_LOCAL_NAMESPACE_PREFIX );
	}
	
	public synchronized int getHostPort() {
		try {
			return Integer.parseInt( getProperty( KEY_HOST_PORT ));
		} catch ( NumberFormatException e ) {
			cat.debug(e);
			return 0;
		}
	}
	
	public synchronized String getAccessPoint() {
		return getHostURI() + getAccessPointPath();
	}

	public synchronized String getURIPathForWSDLs() {
		return getHostURI() + "/WSDLs/";
	}
	
	public synchronized String getAccessPointPath() {
		return getProperty( KEY_ACCESS_POINT_PATH );
	}
	
	public synchronized String getHostURI() {
		return "http://" + getProperty( KEY_HOST_NAME ).toLowerCase() + ":" + getProperty( KEY_HOST_PORT );
	}
	
	public synchronized String getFIPAAttrForXMLAttributes() {
		return getProperty( KEY_XML_ATTRIBUTES );
	}
	
	public synchronized String getFIPAAttrForXMLElement() {
		return getProperty( KEY_XML_ELEMENT );
	}
	
	public synchronized String getFIPAAttrForXMLContent() {
		return getProperty( KEY_XML_CONTENT );
	}
	
	public synchronized String getFIPAPrefixForXMLTag() {
		return getProperty( KEY_XML_TAG );
	}
	
	public synchronized AID getGatewayAID() {
		if ( null == gatewayAID ) {
			if ( "".equals(getProperty( KEY_GATEWAY_AID, "" )) ) {
				gatewayAID = new AID(GATEWAY_NAME, AID.ISLOCALNAME );
			}else {
				gatewayAID = new AID( getProperty( KEY_GATEWAY_AID ), AID.ISGUID );
			}
		}
		return gatewayAID;
	}
	
	public synchronized String getUDDI4jLogEnabled() {
		return getProperty( KEY_UDDI4J_LOG_ENABLED );
	}
	
	public synchronized String getUDDI4jTransportClass() {
		return getProperty( KEY_UDDI4J_TRANSPORT_CLASS );
	}
	
	public synchronized String getQueryManagerPath(){
		return getProperty( KEY_QUERY_MANAGER_PATH );
	}
	
	public synchronized String getLifeCycleManagerPath(){
		return getProperty( KEY_LIFE_CYCLE_MANAGER_PATH );
	}
	

	/**
	 * gives an instance of jade.domain.GatewayAgentMethodListener.
	 * The instance translates and sends DF management messages into UDDI messages.
	 * @return an instance
	 */
	public synchronized DFMethodListener getDFMethodListener() {
		try {
			return (DFMethodListener) Class.forName( getProperty( KEY_DF_TO_UDDI_CLASS )).newInstance();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 *  adds a property missed.
	 *  It creates a property not already stored in this configuration.
	 */
	private void addEmptyProperty( String key, String value ) {
		if ( getProperty( key ) == null ) {
			setProperty( key, value );
		}
	}

	/**
	 * adds properties missed.
	 *
	 */
	private void addEmptyProperties() {
//		addEmptyProperty( Configuration.KEY_FACTORY_CLASS, "" );
		addEmptyProperty( Configuration.KEY_LIFE_CYCLE_MANAGER_URL, "" );
		addEmptyProperty( Configuration.KEY_QUERY_MANAGER_URL, "" );
		addEmptyProperty( Configuration.KEY_USER_NAME, "" );
		addEmptyProperty( Configuration.KEY_USER_PASSWORD, "" );
		addEmptyProperty( Configuration.KEY_BUSINESS_KEY, "" );
		addEmptyProperty( Configuration.KEY_LOCAL_NAMESPACE_PREFIX, "tns" );
		addEmptyProperty( Configuration.KEY_GATEWAY_AID, GATEWAY_NAME + "@localhost:1099/JADE" );
		//addEmptyProperty( Configuration.KEY_HOST_URI, "http://localhost:1233" );
		addEmptyProperty( Configuration.KEY_HOST_NAME, "localhost" );
		addEmptyProperty( Configuration.KEY_HOST_PORT, "2222" );
		addEmptyProperty( Configuration.KEY_XML_ATTRIBUTES, "xml-attributes" );
		addEmptyProperty( Configuration.KEY_XML_ELEMENT, "xml-element" );
		addEmptyProperty( Configuration.KEY_XML_CONTENT, "xml-content" );
		addEmptyProperty( Configuration.KEY_XML_TAG, "xml-tag-" );
		addEmptyProperty( Configuration.KEY_DF_TO_UDDI_CLASS, "com.whitestein.wsig.ws.DFToUDDI4j" );
		addEmptyProperty( Configuration.KEY_UDDI4J_LOG_ENABLED, "false" );
		addEmptyProperty( Configuration.KEY_UDDI4J_TRANSPORT_CLASS, "org.uddi4j.transport.ApacheAxisTransport" );
		addEmptyProperty( Configuration.KEY_QUERY_MANAGER_PATH, "/" + GATEWAY_NAME + "/inquiry" );
		addEmptyProperty( Configuration.KEY_LIFE_CYCLE_MANAGER_PATH, "/" + GATEWAY_NAME + "/publish" );
		addEmptyProperty( Configuration.KEY_ACCESS_POINT_PATH, "/" + GATEWAY_NAME + "" );
		addEmptyProperty( Configuration.KEY_TEST_GOOGLE_ACCESS_KEY, "" );
	}
	
	/**
	 * returns an instance of the Configuration class.
	 * Only one instance is reasonable to use.
	 * 
	 * @return a configuration instance
	 */
	public static synchronized Configuration getInstance() {
		if( anInstance == null ) {
			anInstance = new Configuration();
			load();
		}
		return anInstance;
	}
	
	/**
	 * retrieves configuration.
	 * An internal instance is loaded.
	 *
	 */
	public static void load() {
		Configuration c = getInstance();
		InputStream is;
		synchronized( c ) {
			try {
				if ( ! CONFIG_FILE.exists() ) {
					// empty configuration
					c.addEmptyProperties();
					return;
				}
				is = new BufferedInputStream( new FileInputStream( CONFIG_FILE ));
			}catch (FileNotFoundException e) {
				cat.error(e);
				return;
			}
			try {
				c.load( is );
				is.close();
				cat.debug("WSIG configuration is loaded from a file: " +  CONFIG_FILE.getCanonicalPath() +  " .");
			}catch (IOException ioe) {
				cat.error(ioe);
			}
			c.addEmptyProperties();
		}
	}
	
	/**
	 * saves configuration.
	 * An internal instance is stored.
	 *
	 */
	public static void store() {
		if ( ! isDeveloping ) {
			// avoid comments' changing when not in developing proccess
			return;
		}
		Configuration c = getInstance();
		OutputStream os;
		synchronized( c ) {
			try {
				os = new BufferedOutputStream( new FileOutputStream( CONFIG_FILE ));
			}catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			try {
				c.store( os, CONFIG_HEADER );
				os.close();
			}catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
