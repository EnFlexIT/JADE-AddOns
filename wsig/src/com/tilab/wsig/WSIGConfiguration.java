/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package com.tilab.wsig;

import jade.content.lang.sl.SLCodec;
import jade.util.leap.Properties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.tilab.wsig.wsdl.WSDLConstants;

public class WSIGConfiguration extends Properties {

	private static WSIGConfiguration anInstance;
	private static Logger log = Logger.getLogger(WSIGConfiguration.class.getName());

	public static final String WSIG_DEFAULT_CONFIGURATION_FILE = "conf/wsig.properties";
	private static String wsigConfPath;
	private static String wsigVersion;
	private ServletContext servletContext;
	
	// WSIG configuration
	public static final String KEY_WSIG_AGENT_CLASS_NAME = "wsig.agent";
	public static final String KEY_WSIG_SERVICES_URL = "wsig.servicesURL";
	public static final String KEY_WSIG_TIMEOUT = "wsig.timeout";
	public static final String KEY_WSIG_PRESERVE_JAVA_TYPE = SLCodec.PRESERVE_JAVA_TYPES;
	public static final String KEY_WSIG_TRACE_CLIENT_IP = "wsig.traceClientIP";
	public static final String KEY_WSIG_TRACE_HTTP_HEADERS = "wsig.traceHttpHeaders";
	
	// WSS security
	public final static String KEY_WSS_USERNAME = "wss.username";
	public final static String KEY_WSS_PASSWORD = "wss.password";
	public final static String KEY_WSS_TIME_TO_LIVE = "wss.timeToLive";
	
	// WSDL generation
	public final static String KEY_LOCAL_NAMESPACE_PREFIX = "wsdl.localNamespacePrefix";
	public static final String KEY_WSDL_WRITE_ENABLE = "wsdl.writeEnable";
	public static final String KEY_WSDL_DIRECTORY = "wsdl.directory";
	public static final String KEY_WSDL_STYLE = "wsdl.style";
	public static final String KEY_HIERARCHICAL_COMPLEX_TYPE = "wsdl.hierarchicalComplexType";
	
	// UDDI repository configuration
	public final static String KEY_UDDI_ENABLE = "uddi.enable";
	public final static String KEY_QUERY_MANAGER_URL = "uddi.queryManagerURL";
	public final static String KEY_LIFE_CYCLE_MANAGER_URL = "uddi.lifeCycleManagerURL";
	public final static String KEY_BUSINESS_KEY = "uddi.businessKey";
	public final static String KEY_USER_NAME = "uddi.userName";
	public final static String KEY_USER_PASSWORD = "uddi.userPassword";
	public final static String KEY_UDDI_TMODEL = "uddi.tmodel.key";

	// UDDI4j configuration
	public final static String KEY_UDDI4J_LOG_ENABLED = "org.uddi4j.logEnabled";
	public final static String KEY_UDDI4J_TRANSPORT_CLASS = "org.uddi4j.TransportClassName";

	// Ontology configuration
	public final static String KEY_ONTO_PREFIX = "onto";
	
	// Log manager configuration
	public final static String KEY_ENABLE_LOG_MANAGER = "enable-log-manager";
	public final static String KEY_LOG_MANAGER_NAME = "log-manager-name";
	public final static String KEY_LOG_MANAGER_ROOT = "log-manager-root";
	public final static String KEY_LOG_MANAGER_DOWNLOAD_BLOCK_SIZE = "log-manager-download-block-size";
	public final static String LOG_MANAGER_ROOT_DEFAULT = "../../logs";
	
	
	/**
	 * Return an instance of the class.
	 * Only one instance is reasonable to use.
	 *
	 * @return a instance
	 */
	public static synchronized WSIGConfiguration getInstance() {
		if (anInstance == null) {
			anInstance = new WSIGConfiguration();
			load();
		}
		return anInstance;
	}

   	public static void init(String _wsigConfPath){
		wsigConfPath = _wsigConfPath;
   	}

   	// WSIG CONFIGURATION
	public synchronized String getWsigVersion() {
		if (wsigVersion == null) {
			try {
				Object versionManager = Class.forName("com.tilab.wsig.VersionManager").newInstance();
				Class c = versionManager.getClass();
				java.lang.reflect.Method m = c.getMethod("getVersion", new Class[0]);
				String version = (String) m.invoke(versionManager, new Object[0]);
				m = c.getMethod("getRevision", new Class[0]);
				String revision = (String) m.invoke(versionManager, new Object[0]);
				m = c.getMethod("getDate", new Class[0]);
				String date = (String) m.invoke(versionManager, new Object[0]);
				
				wsigVersion = version+" - revision "+revision+" of "+date;
			}
			catch (Exception e) {
				// VersionManager not available
				wsigVersion = "Not available";
			}
		}
		return wsigVersion;
	}
   	
	// AGENT CONFIGURATION FOR SERVLET
	public synchronized String getMainHost() {
		return getProperty(jade.core.Profile.MAIN_HOST);
	}
	
	public void setMainHost(String mainHost) {
		 setProperty(jade.core.Profile.MAIN_HOST, mainHost);
	}
	
	public synchronized String getMainPort() {
		return getProperty(jade.core.Profile.MAIN_PORT);
	}
	
	public void setMainPort(String mainPort) {
		 setProperty(jade.core.Profile.MAIN_PORT, mainPort);
	}
	
	public synchronized String getContainerName() {
		return getProperty(jade.core.Profile.CONTAINER_NAME);
	}
	
	public void setContainerName(String containerName) {
		 setProperty(jade.core.Profile.CONTAINER_NAME, containerName);
	}
	
	public synchronized String getLocalPort() {
		return getProperty(jade.core.Profile.LOCAL_PORT);
	}
	
	public void setLocalPort(String localPort) {
		 setProperty(jade.core.Profile.LOCAL_PORT, localPort);
	}
	
	public synchronized String getPreserveJavaType() {
		return getProperty(KEY_WSIG_PRESERVE_JAVA_TYPE);
	}
	
	public void setPreserveJavaType(String preserveJavaType) {
		 setProperty(KEY_WSIG_PRESERVE_JAVA_TYPE,preserveJavaType);
	}
	
	public synchronized String getAgentClassName() {
		return getProperty(KEY_WSIG_AGENT_CLASS_NAME);
	}
	
	public void setAgentClassName(String agentClassName) {
		 setProperty(KEY_WSIG_AGENT_CLASS_NAME,agentClassName);
	}
	
	public synchronized boolean isTraceClientIP() {
		String traceClientIP = getProperty(KEY_WSIG_TRACE_CLIENT_IP);
		return "true".equalsIgnoreCase(traceClientIP);
	}
	
		
	public synchronized boolean isTraceHttpHeaders() {
		String traceHttpHeaders = getProperty(KEY_WSIG_TRACE_HTTP_HEADERS);
		return "true".equalsIgnoreCase(traceHttpHeaders);
	}
	

	public synchronized String getServicesUrl(HttpServletRequest request) throws MalformedURLException {
		// Try to read from configuration file 
		String servicesUrl = getProperty(KEY_WSIG_SERVICES_URL);
		if (servicesUrl == null) {
			// Try to get from request
			if (request != null) {
				String webappUrl = getWebappUrl(request).toString();
				servicesUrl = webappUrl + "/ws";
			} else {
				servicesUrl = "$ENDPOINT$";
			}
		}
		return servicesUrl;
	}
	
	public void setServicesUrl(String servicesUrl) {
		 setProperty(KEY_WSIG_SERVICES_URL,servicesUrl);
	}
		
	public synchronized int getWsigTimeout() {
		String timeout = getProperty(KEY_WSIG_TIMEOUT);
		return Integer.parseInt(timeout);
	}
	
	public void setWsigTimeout(int wsigTimeout) {
		 String timeout = Integer.toString(wsigTimeout);
		 setProperty(KEY_WSIG_TIMEOUT,timeout);
	}
	
	// WSS security
	public synchronized String getWssUsername() {
		return getProperty(KEY_WSS_USERNAME);
	}
	
	public void setWssUsername(String wssUsername) {
		 setProperty(KEY_WSS_USERNAME,wssUsername);
	}

	public synchronized String getWssPassword() {
		return getProperty(KEY_WSS_PASSWORD);
	}
	
	public void setWssPassword(String wssPassword) {
		 setProperty(KEY_WSS_PASSWORD,wssPassword);
	}
	
	public synchronized String getWssTimeToLive() {
		return getProperty(KEY_WSS_TIME_TO_LIVE);
	}
	
   	// WSDL generation
	public synchronized String getLocalNamespacePrefix() {
		return getProperty(KEY_LOCAL_NAMESPACE_PREFIX);
	}
	
	public synchronized void setLocalNamespacePrefix(String localNamespacePrefix) {
		setProperty(KEY_LOCAL_NAMESPACE_PREFIX,localNamespacePrefix);
	}

	public synchronized String getWsdlDirectory() {
		return getProperty(KEY_WSDL_DIRECTORY);
	}
	public synchronized void setWsdlDirectory(String wsdlDirectory) {
		setProperty(KEY_WSDL_DIRECTORY, wsdlDirectory);
	}
	
	public synchronized boolean isHierarchicalComplexTypeEnable() {
		String hierarchicalComplexTypeEnable = getProperty(KEY_HIERARCHICAL_COMPLEX_TYPE);
		return "true".equalsIgnoreCase(hierarchicalComplexTypeEnable);
	}
	
	public synchronized void setHierarchicalComplexTypeEnable(Boolean hierarchicalComplexTypeEnable) {
		String hComplexTypeEnable = Boolean.toString(hierarchicalComplexTypeEnable);
		setProperty(KEY_HIERARCHICAL_COMPLEX_TYPE, hComplexTypeEnable);
	}
	
	
	public synchronized boolean isWsdlWriteEnable() {
		String wsdlWriteEnable = getProperty(KEY_WSDL_WRITE_ENABLE);
		return "true".equalsIgnoreCase(wsdlWriteEnable);
	}
	public synchronized void setWsdlWriteEnable(boolean wsdlWriteEnable) {
		setProperty(KEY_WSDL_WRITE_ENABLE, Boolean.toString(wsdlWriteEnable));
	}

	public synchronized String getWsdlStyle() {
		return getProperty(KEY_WSDL_STYLE);
	}
	
	public synchronized void setWsdlStyle(String wsdlStyle) {
		setProperty(KEY_WSDL_STYLE,wsdlStyle);
	}
	
	// UDDI repository configuration
	public synchronized boolean isUddiEnable() {
		String uddiEnable = getProperty(KEY_UDDI_ENABLE);
		return "true".equalsIgnoreCase(uddiEnable);
	}
	
	public synchronized void setUddiEnable(Boolean uddiEnable) {
		String enable = Boolean.toString(uddiEnable);
		setProperty(KEY_UDDI_ENABLE,enable);
	}

	public synchronized String getQueryManagerURL() {
		return getProperty(KEY_QUERY_MANAGER_URL);
	}
	
	public synchronized void setQueryManagerURL(String queryManagerURL) {
		setProperty(KEY_QUERY_MANAGER_URL,queryManagerURL);
	}

	public synchronized String getLifeCycleManagerURL() {
		return getProperty(KEY_LIFE_CYCLE_MANAGER_URL);
	}
	
	public synchronized void setLifeCycleManagerURL(String lifeCycleManagerURL) {
		setProperty(KEY_LIFE_CYCLE_MANAGER_URL,lifeCycleManagerURL);
	}
	
	public synchronized String getBusinessKey() {
		return getProperty(KEY_BUSINESS_KEY);
	}
	
	public synchronized void setBusinessKey(String businessKey) {
		setProperty(KEY_BUSINESS_KEY,businessKey);
	}
   	
	public synchronized String getUserName() {
		return getProperty(KEY_USER_NAME);
	}
	
	public synchronized void setUserName(String userName) {
		setProperty(KEY_USER_NAME,userName);
	}

	public synchronized String getUserPassword() {
		return getProperty(KEY_USER_PASSWORD);
	}
	
	public synchronized void setUserPassword(String userPassword) {
		setProperty(KEY_USER_PASSWORD,userPassword);
	}
	
	public synchronized String getTModel() {
		return getProperty(KEY_UDDI_TMODEL);
	}
	
	public synchronized void setTModel(String tModel) {
		setProperty(KEY_UDDI_TMODEL,tModel);
	}
	
	// UDDI4j configuration
	public synchronized String getUDDI4jLogEnabled() {
		return getProperty(KEY_UDDI4J_LOG_ENABLED);
	}

	public synchronized String getUDDI4jTransportClass() {
		return getProperty(KEY_UDDI4J_TRANSPORT_CLASS);
	}

	// Ontology configuration
	public synchronized String getOntoClassname(String ontoName) {
		
		String ontoKey = KEY_ONTO_PREFIX + "." + ontoName;
		String propertyKey;
		Iterator it =keySet().iterator();
		while(it.hasNext()) {
			propertyKey = (String)it.next();
			if (propertyKey.equals(ontoKey))
				return getProperty(propertyKey);
		}
		return null;
	}
	
	private static URL getWebappUrl(HttpServletRequest request) throws MalformedURLException {
		String protocol = request.getScheme();
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		String contextPath = request.getContextPath();
		return new URL(protocol, serverName, serverPort, contextPath);
	}
			
	
	public static URL getAdminUrl(HttpServletRequest request) throws MalformedURLException {
		return getWebappUrl(request);
	}
	
	
	
	public synchronized boolean isJadeMiscPresent() {
		try {
			Class.forName("jade.misc.CreateFileManagerAgentBehaviour");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	public synchronized boolean isLogManagerEnable() {
		String logManagerEnable = getProperty(KEY_ENABLE_LOG_MANAGER);
		return "true".equalsIgnoreCase(logManagerEnable);
	}
	
	public synchronized String getLogManagerName() {
		return getProperty(KEY_LOG_MANAGER_NAME);
	}
	
	public synchronized String getLogManagerRoot() {
		String fileManagerRoot = getProperty(KEY_LOG_MANAGER_ROOT, LOG_MANAGER_ROOT_DEFAULT);
		File f = new File(fileManagerRoot);
		if (!f.isAbsolute()) {
			fileManagerRoot = servletContext.getRealPath(fileManagerRoot);
		}
		return fileManagerRoot;
	}

	public synchronized Integer getLogManagerDownloadBlockSize() {
		String fileManagerDownloadBlockSizeStr = getProperty(WSIGConfiguration.KEY_LOG_MANAGER_DOWNLOAD_BLOCK_SIZE);
		Integer fileManagerDownloadBlockSize = null;
		if (fileManagerDownloadBlockSizeStr != null) {
			fileManagerDownloadBlockSize = Integer.valueOf(fileManagerDownloadBlockSizeStr);
		}
		return fileManagerDownloadBlockSize;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	/**
	 * adds properties missed.
	 */
	private void setDefaultProperties() {
		setProperty(jade.core.Profile.MAIN, "false");
		
		setProperty(WSIGConfiguration.KEY_WSIG_AGENT_CLASS_NAME, "com.tilab.wsig.agent.WSIGAgent");
		setProperty(WSIGConfiguration.KEY_WSIG_TIMEOUT, "30000");
		setProperty(WSIGConfiguration.KEY_WSIG_TRACE_CLIENT_IP, "true");
		setProperty(WSIGConfiguration.KEY_WSIG_TRACE_HTTP_HEADERS, "true");
		setProperty(WSIGConfiguration.KEY_WSDL_DIRECTORY, "wsdl");
		setProperty(WSIGConfiguration.KEY_WSDL_WRITE_ENABLE, "false");
		setProperty(WSIGConfiguration.KEY_WSDL_STYLE, WSDLConstants.STYLE_DOCUMENT);
		setProperty(WSIGConfiguration.KEY_UDDI_ENABLE, "false");
		setProperty(WSIGConfiguration.KEY_LIFE_CYCLE_MANAGER_URL, "");
		setProperty(WSIGConfiguration.KEY_QUERY_MANAGER_URL, "");
		setProperty(WSIGConfiguration.KEY_USER_NAME, "");
		setProperty(WSIGConfiguration.KEY_USER_PASSWORD, "");
		setProperty(WSIGConfiguration.KEY_BUSINESS_KEY, "");
		setProperty(WSIGConfiguration.KEY_LOCAL_NAMESPACE_PREFIX, "impl");
		setProperty(WSIGConfiguration.KEY_UDDI4J_LOG_ENABLED, "false");
		setProperty(WSIGConfiguration.KEY_UDDI4J_TRANSPORT_CLASS, "org.uddi4j.transport.ApacheAxisTransport");
		setProperty(WSIGConfiguration.KEY_UDDI_TMODEL, "");
		setProperty(WSIGConfiguration.KEY_HIERARCHICAL_COMPLEX_TYPE, "false");
		setProperty(WSIGConfiguration.KEY_ENABLE_LOG_MANAGER, "false");
		setProperty(WSIGConfiguration.KEY_LOG_MANAGER_NAME, "WSIGLogManager");
	}

	
	public void store() {
		try {
			if (wsigConfPath != null) {
				String propertyKey;
				Iterator it =keySet().iterator();
				while(it.hasNext()) {
					propertyKey = (String)it.next();					
					if (getProperty(propertyKey) == null) {
						this.setProperty(propertyKey, "");
					}
				}
				store(wsigConfPath);
			}
		} catch (IOException e) {
			log.error("WSIG error writing configuration", e);
		}                  
	}
	/**
	 * Retrieves configuration.
	 * An internal instance is loaded.
	 */
	private static void load() {
		
		log.info("Loading WSIG configuration file...");
		WSIGConfiguration c = getInstance();
		InputStream is;
		synchronized (c) {
			c.setDefaultProperties();
			
			InputStream input = null;
			if (wsigConfPath != null) {
				try {
					input = new FileInputStream(wsigConfPath);
				} catch (FileNotFoundException e) {
					log.error("WSIG configuration file <<" + wsigConfPath + ">> not found, wsig agent will use default configuration", e);
					return;
				}
			} else {
				input = ClassLoader.getSystemResourceAsStream(WSIG_DEFAULT_CONFIGURATION_FILE);
				if (input == null) {
					log.error("WSIG configuration file <<" + WSIG_DEFAULT_CONFIGURATION_FILE + ">> not found, wsig agent will use default configuration");
					return;
				}
			}
			try {
				is = new BufferedInputStream(input);
				c.load(is);
				is.close();
				log.debug("WSIG configuration file is loaded");

			} catch (IOException e) {
				log.error("WSIG configuration file error reading", e);
			}
		}
	}
}
