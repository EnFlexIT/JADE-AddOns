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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.tilab.wsig.wsdl.WSDLConstants;

import jade.content.lang.sl.SLCodec;
import jade.util.Logger;
import jade.util.leap.Properties;

public class WSIGConfiguration extends Properties {

	private static WSIGConfiguration anInstance;
	private static Logger logger = Logger.getMyLogger(WSIGConfiguration.class.getName());

	public static final String WSIG_CONFIGURATION_FILE_NAME = "wsig.properties";
	public static final String WSIG_DEFAULT_CONFIGURATION = "conf/"+WSIG_CONFIGURATION_FILE_NAME;
	public static final String WSIG_ENDPOINT_PATH_KEY = "endpoint-path";
	public static final String WSIG_CONF_DIR = "conf";
	public static final String WSIG_RESOURCE_PATH = "WSIG_RESOURCE_PATH";
	
	private static String wsigConfFile;
	private static String wsigVersion;
	private static ServletContext servletContext;
	private static String resourcesBase;

	// WSIG configuration
	public static final String KEY_WSIG_INITIALIZER_CLASS_NAME = "wsig.initializer";
	public static final String KEY_WSIG_AGENT_NAME = "wsig.agentName";
	public static final String KEY_WSIG_AGENT_CLASS_NAME = "wsig.agent";
	public static final String KEY_WSIG_SERVICES_URL = "wsig.servicesURL";
	public static final String KEY_WSIG_SERVICES_PATH = "wsig.servicesPath";
	public static final String KEY_WSIG_REST_SERVICES_URL = "wsig.REST.servicesURL";
	public static final String KEY_WSIG_REST_SERVICES_PATH = "wsig.REST.servicesPath";
	public static final String KEY_WSIG_ADMIN_SERVICES_URL = "wsig.admin.servicesURL";
	public static final String KEY_WSIG_ADMIN_SERVICES_PATH = "wsig.admin.servicesPath";
	public static final String KEY_WSIG_TIMEOUT = "wsig.timeout";
	public static final String KEY_WSIG_AUTOMATIC_STARTUP_TIMEOUT = "wsig.automaticStartup.timeout";
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

	public static void init(String configurationFile, ServletContext _servletContext, String _resourcesBase) {
		servletContext = _servletContext;

		resourcesBase = _resourcesBase;
		if (resourcesBase == null) {
			// Try to read resource-base from environment variable WSIG_RESOURCE_PATH
			resourcesBase = System.getenv(WSIG_RESOURCE_PATH);
		}
		
		if (configurationFile == null) {
			configurationFile = WSIG_DEFAULT_CONFIGURATION;
		}
		wsigConfFile = resolvePath(servletContext, resourcesBase, configurationFile);
		logger.log(Level.INFO, "Configuration file= " + wsigConfFile);
	}

	public static void reset() {
		anInstance = null;
		wsigConfFile = null;
		wsigVersion = null;
		servletContext = null;
		resourcesBase = null;
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

	public synchronized String getWsigResourcePath() {
		return resolvePath(servletContext, resourcesBase, null);
	}

	public synchronized String getWsigConfPath() {
		return resolvePath(servletContext, resourcesBase, WSIG_CONF_DIR);
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

	public synchronized String getAgentName() {
		return getProperty(KEY_WSIG_AGENT_NAME);
	}

	public void setAgentName(String agentName) {
		setProperty(KEY_WSIG_AGENT_NAME,agentName);
	}

	public synchronized String getAgentClassName() {
		return getProperty(KEY_WSIG_AGENT_CLASS_NAME);
	}

	public void setAgentClassName(String agentClassName) {
		setProperty(KEY_WSIG_AGENT_CLASS_NAME,agentClassName);
	}

	public synchronized String getInitializerClassName() {
		return getProperty(KEY_WSIG_INITIALIZER_CLASS_NAME);
	}

	public void setInitializerClassName(String initializerClassName) {
		setProperty(KEY_WSIG_INITIALIZER_CLASS_NAME,initializerClassName);
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
				servicesUrl = webappUrl + "/" + getServicesPath();
			} else {
				servicesUrl = "$ENDPOINT$";
			}
		}
		return servicesUrl;
	}

	public void setServicesUrl(String servicesUrl) {
		setProperty(KEY_WSIG_SERVICES_URL,servicesUrl);
	}

	public String getServicesPath() {
		return getProperty(KEY_WSIG_SERVICES_PATH);
	}

	public void setServicesPath(String servicesPath) {
		setProperty(KEY_WSIG_SERVICES_PATH,servicesPath);
	}
	
	public synchronized String getRESTServicesUrl(HttpServletRequest request) throws MalformedURLException {
		// Try to read from configuration file 
		String servicesUrl = getProperty(KEY_WSIG_REST_SERVICES_URL);
		if (servicesUrl == null) {
			// Try to get from request
			if (request != null) {
				String webappUrl = getWebappUrl(request).toString();
				servicesUrl = webappUrl + "/" + getRESTServicesPath();
			} else {
				servicesUrl = "$ENDPOINT$";
			}
		}
		return servicesUrl;
	}

	public void setRESTServicesUrl(String servicesUrl) {
		setProperty(KEY_WSIG_REST_SERVICES_URL,servicesUrl);
	}

	public String getRESTServicesPath() {
		return getProperty(KEY_WSIG_REST_SERVICES_PATH);
	}

	public void setRESTServicesPath(String servicesPath) {
		setProperty(KEY_WSIG_REST_SERVICES_PATH,servicesPath);
	}
	
	public synchronized String getAdminServicesUrl(HttpServletRequest request) throws MalformedURLException {
		// Try to read from configuration file 
		String servicesUrl = getProperty(KEY_WSIG_ADMIN_SERVICES_URL);
		if (servicesUrl == null) {
			// Try to get from request
			if (request != null) {
				String webappUrl = getWebappUrl(request).toString();
				servicesUrl = webappUrl + "/" + getAdminServicesPath();
			}
		}
		return servicesUrl;
	}

	public void setAdminServicesUrl(String servicesUrl) {
		setProperty(KEY_WSIG_ADMIN_SERVICES_URL,servicesUrl);
	}

	public String getAdminServicesPath() {
		return getProperty(KEY_WSIG_ADMIN_SERVICES_PATH);
	}

	public void setAdminServicesPath(String servicesPath) {
		setProperty(KEY_WSIG_ADMIN_SERVICES_PATH,servicesPath);
	}
	
	public static String getConsoleUrl(HttpServletRequest request) throws MalformedURLException {
		return getWebappUrl(request).toString();
	}

	public synchronized int getWsigTimeout() {
		String timeout = getProperty(KEY_WSIG_TIMEOUT);
		return Integer.parseInt(timeout);
	}

	public void setWsigTimeout(int wsigTimeout) {
		String timeout = Integer.toString(wsigTimeout);
		setProperty(KEY_WSIG_TIMEOUT,timeout);
	}

	public synchronized int getWsigAutomaticStartupTimeout() {
		String timeout = getProperty(KEY_WSIG_AUTOMATIC_STARTUP_TIMEOUT);
		return Integer.parseInt(timeout);
	}

	public void setWsigAutomaticStartupTimeout(int timeout) {
		String strTimeout = Integer.toString(timeout);
		setProperty(KEY_WSIG_AUTOMATIC_STARTUP_TIMEOUT,strTimeout);
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
		String wsdlDirectory = getProperty(KEY_WSDL_DIRECTORY);
		return resolvePath(servletContext, resourcesBase, wsdlDirectory);
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
		return resolvePath(servletContext, resourcesBase, fileManagerRoot);
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

	/**
	 * adds properties missed.
	 */
	private void setDefaultProperties() {
		setProperty(jade.core.Profile.MAIN, "false");

		setProperty(WSIGConfiguration.KEY_WSIG_AGENT_NAME, "wsig");
		setProperty(WSIGConfiguration.KEY_WSIG_AGENT_CLASS_NAME, "com.tilab.wsig.agent.WSIGAgent");
		setProperty(WSIGConfiguration.KEY_WSIG_TIMEOUT, "30000");
		setProperty(WSIGConfiguration.KEY_WSIG_AUTOMATIC_STARTUP_TIMEOUT, "5000");
		setProperty(WSIGConfiguration.KEY_WSIG_TRACE_CLIENT_IP, "true");
		setProperty(WSIGConfiguration.KEY_WSIG_TRACE_HTTP_HEADERS, "true");
		setProperty(WSIGConfiguration.KEY_WSIG_SERVICES_PATH, "ws");
		setProperty(WSIGConfiguration.KEY_WSIG_REST_SERVICES_PATH, "wsRest");
		setProperty(WSIGConfiguration.KEY_WSIG_ADMIN_SERVICES_PATH, "admin");
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
			if (wsigConfFile != null) {
				String propertyKey;
				Iterator it =keySet().iterator();
				while(it.hasNext()) {
					propertyKey = (String)it.next();					
					if (getProperty(propertyKey) == null) {
						this.setProperty(propertyKey, "");
					}
				}
				store(wsigConfFile);
			} else {
				logger.log(Level.WARNING, "Default configuration or configuration loaded in classloader is not modifiable");
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "WSIG error writing configuration", e);
		}                  
	}
	/**
	 * Retrieves configuration.
	 * An internal instance is loaded.
	 */
	private static void load() {
		logger.log(Level.INFO, "Loading WSIG configuration file...");
		WSIGConfiguration c = getInstance();
		InputStream is;
		synchronized (c) {
			c.setDefaultProperties();

			InputStream input = null;
			if (wsigConfFile != null) {
				try {
					input = new FileInputStream(wsigConfFile);
				} catch (FileNotFoundException e) {
					logger.log(Level.SEVERE, "WSIG configuration <<" + wsigConfFile + ">> not found in file system, wsig agent will use default configuration", e);
					return;
				}
			} else {
				input = ClassLoader.getSystemResourceAsStream(WSIG_CONFIGURATION_FILE_NAME);
				if (input == null) {
					logger.log(Level.SEVERE, "WSIG configuration <<" + WSIG_CONFIGURATION_FILE_NAME + ">> not found in system class loader, wsig agent will use default configuration");
					return;
				}
			}
			try {
				is = new BufferedInputStream(input);
				c.load(is);
				is.close();
				logger.log(Level.FINE, "WSIG configuration file is loaded");

			} catch (IOException e) {
				logger.log(Level.SEVERE, "WSIG configuration file error reading", e);
			}
		}
	}
	
	private static String resolvePath(ServletContext servletContext, String resourcesBase, String path) {
		if (path != null) {
			// Check if path is absolute
			File f = new File(path);
			if (f.isAbsolute()) {
				return path;
			}
		}

		String absolutePath;
		if (resourcesBase != null) {
			// Use specified absolute base resources path
			absolutePath = resourcesBase;
			if (path != null) {
				absolutePath = absolutePath+File.separator+path;
			}
		} 
		else {
			// Resolve with webapp context
			if (path != null) {
				absolutePath = servletContext.getRealPath(path);
			}
			else {
				absolutePath = servletContext.getRealPath("");
			}
		}
		
		return absolutePath;
	}
}
