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

import jade.core.AID;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.List;

import org.apache.log4j.Logger;
import com.whitestein.wsig.fipa.DFMethodListener;

/**
 * @author jna
 *         <p/>
 *         keeps and makes accessible configuration for gateway.
 *         Only one instance is reasonable and is accessible by getInstance() method.
 *         The properties are loaded and stored from the CONFIG_FILE_NAME;
 *         <p/>
 *         To prevent accidential changes in configuration's properties the synchronization
 *         on getInstance() returned object must be used in your code.
 */
public class Configuration extends Properties {

	private static Configuration anInstance;
	private static Logger log = Logger.getLogger(Configuration.class.getName());
	private final static boolean isDeveloping = false; //true;
	private final static String VERSION = "0.7beta";
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
	public final static String KEY_UDDI_TMODEL = "uddi.tmodel.key";

	//public final static String KEY_UDDI4J_TRANSPORT_CLASS = "org.uddi4j.transport.ApacheAxisTransport";
	public final static String KEY_UDDI4J_TRANSPORT_CLASS = "org.uddi4j.TransportClassName";


	public final static String KEY_ONTOLOGIES = "ontologies";
	public final static String KEY_LANGUAGES = "languages";
	public final static String KEY_WSDL_DIRECTORY = "wsdl.directory";

	public static final String VOID_TYPE = "";


	private AID gatewayAID;


	public synchronized String getTestAmazonAccessKey() {
		return "";
	}

	public synchronized String getQueryManagerURL() {
		return getProperty(KEY_QUERY_MANAGER_URL);
	}

	public synchronized String getLifeCycleManagerURL() {
		return getProperty(KEY_LIFE_CYCLE_MANAGER_URL);
	}

/*
	public synchronized String getFactoryClass(){
		return getProperty( KEY_FACTORY_CLASS );
	}
*/

	public synchronized String getUserName() {
		return getProperty(KEY_USER_NAME);
	}

	public synchronized String getUserPassword() {
		return getProperty(KEY_USER_PASSWORD);
	}

	public synchronized String getBusinessKey() {
		return getProperty(KEY_BUSINESS_KEY);
	}

	public synchronized void setBusinessKey(String businessKey) {
		setProperty(Configuration.KEY_BUSINESS_KEY, businessKey);
	}

	public synchronized String getLocalNamespacePrefix() {
		return getProperty(KEY_LOCAL_NAMESPACE_PREFIX);
	}

	public synchronized int getHostPort() {
		try {
			return Integer.parseInt(getProperty(KEY_HOST_PORT));
		} catch (NumberFormatException e) {
			log.debug(e);
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
		return getProperty(KEY_ACCESS_POINT_PATH);
	}

	public synchronized String getHostURI() {
		return "http://" + getProperty(KEY_HOST_NAME).toLowerCase() + ":" + getProperty(KEY_HOST_PORT);
	}

	public synchronized String getFIPAAttrForXMLAttributes() {
		return getProperty(KEY_XML_ATTRIBUTES);
	}

	public synchronized String getFIPAAttrForXMLElement() {
		return getProperty(KEY_XML_ELEMENT);
	}

	public synchronized String getFIPAAttrForXMLContent() {
		return getProperty(KEY_XML_CONTENT);
	}

	public synchronized String getFIPAPrefixForXMLTag() {
		return getProperty(KEY_XML_TAG);
	}

	public synchronized AID getGatewayAID() {
		if (null == gatewayAID) {
			if ("".equals(getProperty(KEY_GATEWAY_AID, ""))) {
				gatewayAID = new AID(GATEWAY_NAME, AID.ISLOCALNAME);
			} else {
				gatewayAID = new AID(getProperty(KEY_GATEWAY_AID), AID.ISGUID);
			}
		}
		return gatewayAID;
	}

	public synchronized String getUDDI4jLogEnabled() {
		return getProperty(KEY_UDDI4J_LOG_ENABLED);
	}

	public synchronized String getUDDI4jTransportClass() {
		log.info("In getUDDI4j... " + KEY_UDDI4J_TRANSPORT_CLASS);
		return getProperty(KEY_UDDI4J_TRANSPORT_CLASS);
	}

	public synchronized String getQueryManagerPath() {
		return getProperty(KEY_QUERY_MANAGER_PATH);
	}

	public synchronized String getLifeCycleManagerPath() {
		return getProperty(KEY_LIFE_CYCLE_MANAGER_PATH);
	}


	/**
	 * gives an instance of jade.domain.GatewayAgentMethodListener.
	 * The instance translates and sends DF management messages into UDDI messages.
	 *
	 * @return an instance
	 */
	public synchronized DFMethodListener getDFMethodListener() {
		try {
			return (DFMethodListener) Class.forName(getProperty(KEY_DF_TO_UDDI_CLASS)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public String[] getOntologies() {
		String onto = getProperty(KEY_ONTOLOGIES).trim();
		if (onto.equals("")) {
			return new String[0];
		}
		return onto.split(",");


	}

	public String[] getLanguages() {
		String languages = getProperty(KEY_LANGUAGES).trim();

		if (languages.equals("")) {
			return new String[0];
		}
		return languages.split(",");
	}

	public String getWsdlDirectory() {
		return getProperty(KEY_WSDL_DIRECTORY);
	}


	/**
	 * adds properties missed.
	 */
	private void setDefaultProperties() {
//		setProperty( Configuration.KEY_FACTORY_CLASS, "" );
		setProperty(Configuration.KEY_LIFE_CYCLE_MANAGER_URL, "");
		setProperty(Configuration.KEY_QUERY_MANAGER_URL, "");
		setProperty(Configuration.KEY_USER_NAME, "");
		setProperty(Configuration.KEY_USER_PASSWORD, "");
		setProperty(Configuration.KEY_BUSINESS_KEY, "");
		setProperty(Configuration.KEY_LOCAL_NAMESPACE_PREFIX, "tns");
		setProperty(Configuration.KEY_GATEWAY_AID, GATEWAY_NAME + "@localhost:1099/JADE");
		//setProperty( Configuration.KEY_HOST_URI, "http://localhost:1233" );
		setProperty(Configuration.KEY_HOST_NAME, "localhost");
		setProperty(Configuration.KEY_HOST_PORT, "2222");
		setProperty(Configuration.KEY_XML_ATTRIBUTES, "xml-attributes");
		setProperty(Configuration.KEY_XML_ELEMENT, "xml-element");
		setProperty(Configuration.KEY_XML_CONTENT, "xml-content");
		setProperty(Configuration.KEY_XML_TAG, "xml-tag-");
		setProperty(Configuration.KEY_DF_TO_UDDI_CLASS, "com.whitestein.wsig.ws.DFToUDDI4j");
		setProperty(Configuration.KEY_UDDI4J_LOG_ENABLED, "false");
		setProperty(Configuration.KEY_UDDI4J_TRANSPORT_CLASS, "org.uddi4j.transport.ApacheAxisTransport");
		setProperty(Configuration.KEY_UDDI_TMODEL, "uuid:A035A07C-F362-44dd-8F95-E2B134BF43B4");
		setProperty(Configuration.KEY_QUERY_MANAGER_PATH, "/" + GATEWAY_NAME + "/inquiry");
		setProperty(Configuration.KEY_LIFE_CYCLE_MANAGER_PATH, "/" + GATEWAY_NAME + "/publish");
		setProperty(Configuration.KEY_ACCESS_POINT_PATH, "/" + GATEWAY_NAME + "");
		setProperty(Configuration.KEY_ONTOLOGIES, "");
		setProperty(Configuration.KEY_LANGUAGES, "");
		setProperty(Configuration.KEY_WSDL_DIRECTORY, ".");

	}

	/**
	 * returns an instance of the Configuration class.
	 * Only one instance is reasonable to use.
	 *
	 * @return a configuration instance
	 */
	public static synchronized Configuration getInstance() {
		if (anInstance == null) {
			anInstance = new Configuration();
			load();
		}
		return anInstance;
	}

	/**
	 * retrieves configuration.
	 * An internal instance is loaded.
	 */
	public static void load() {
		Configuration c = getInstance();
		InputStream is;
		synchronized (c) {
			c.setDefaultProperties();
			InputStream input = ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME);
			try {
				if (input != null) {
					is = new BufferedInputStream(input);
					c.load(is);
					is.close();
					log.debug("WSIG configuration is loaded from a file: " + CONFIG_FILE.getCanonicalPath() + " .");
				}
			} catch (IOException ioe) {
				log.error(ioe);
			}

		}
	}

	/**
	 * saves configuration.
	 * An internal instance is stored.
	 */
	public static void store() {
		if (! isDeveloping) {
			// avoid comments' changing when not in developing proccess
			return;
		}
		Configuration c = getInstance();
		OutputStream os;
		synchronized (c) {
			try {
				os = new BufferedOutputStream(new FileOutputStream(CONFIG_FILE));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			try {
				c.store(os, CONFIG_HEADER);
				os.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Configuration conf = Configuration.getInstance();
		System.out.println(conf.toString());
	}
}
