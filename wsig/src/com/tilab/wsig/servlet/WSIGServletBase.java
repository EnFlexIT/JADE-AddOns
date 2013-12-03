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
package com.tilab.wsig.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.WSIGConstants;
import com.tilab.wsig.agent.WSIGBehaviour;
import com.tilab.wsig.soap.SOAPException;
import com.tilab.wsig.store.OperationResult;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.store.WSIGStore;

import jade.content.AgentAction;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Profile;
import jade.util.Logger;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.DynamicJadeGateway;
import jade.wrapper.gateway.GatewayListener;
import jade.wrapper.gateway.JadeGateway;
import jade.wrapper.gateway.LocalJadeGateway;

public abstract class WSIGServletBase extends HttpServlet implements GatewayListener {
	private static final long serialVersionUID = 1471617048324302610L;
	
	public static final String WEBAPP_CONFIGURATION_KEY = "WSIGConfiguration";
	public static final String WEBAPP_GATEWAY_KEY = "JADEGateway";
	public static final String WEBAPP_STORE_KEY = "WSIGStore";
	public static final String WEBAPP_ACTIVE_KEY = "WSIGActive";
	
	protected Logger logger = Logger.getMyLogger(getClass().getName());
	
	private DynamicJadeGateway jadeGateway;
	protected WSIGStore wsigStore;
	private ServletContext webappContext;

	
	public WSIGServletBase() {
		this(null);
	}

	public WSIGServletBase(DynamicJadeGateway jadeGateway) {
		this.jadeGateway = jadeGateway;
	}
	
	// Methods init() of the servlet (SOAP and REST) are called sequentially 
	// with the order declared in the web.xml.
	// Only the first servlet initialize the WSIG configuration, the WSIG storage 
	// and the JADE gateway 
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		// Get webapp context (commons to SOAP and REST servlets)
		webappContext = servletConfig.getServletContext();
		
		// Check WSIG configuration
		WSIGConfiguration wsigConfiguration = (WSIGConfiguration) webappContext.getAttribute(WEBAPP_CONFIGURATION_KEY);
		if (wsigConfiguration == null) {

			// Get init parameters
			String resourcesBase = servletConfig.getInitParameter(WSIGConfiguration.WSIG_RESOURCES_BASE_KEY);
			String configurationFile = servletConfig.getInitParameter(WSIGConfiguration.WSIG_CONFIGURATION_KEY);
			if (configurationFile == null) {
				configurationFile = WSIGConfiguration.WSIG_DEFAULT_CONFIGURATION;
			}
			logger.log(Level.INFO, "Configuration file= " + configurationFile);

			// Init configuration
			WSIGConfiguration.init(configurationFile, webappContext, resourcesBase);
			wsigConfiguration = WSIGConfiguration.getInstance();
			
			// Java type preservation
			String preserveJavaType = wsigConfiguration.getPreserveJavaType();
			if (preserveJavaType != null) {
				System.setProperty(SLCodec.PRESERVE_JAVA_TYPES, preserveJavaType);
			}

			// Set WSIGConfiguration into context 
			webappContext.setAttribute(WEBAPP_CONFIGURATION_KEY, wsigConfiguration);
		}
		
		// Check the WSIG store
		wsigStore = (WSIGStore) webappContext.getAttribute(WEBAPP_STORE_KEY); 
		if (wsigStore == null) {
			
			// Create new WSIG store ant put it into context
			wsigStore = new WSIGStore();
			webappContext.setAttribute(WEBAPP_STORE_KEY, wsigStore);
		}		
		
		// Check the JADE gateway
		DynamicJadeGateway djg = (DynamicJadeGateway) webappContext.getAttribute(WEBAPP_GATEWAY_KEY);
		if (djg == null) {
			
			// Check if is present a custom gateway 
			if (jadeGateway == null) {
				// Use default gateway
				jadeGateway = JadeGateway.getDefaultGateway();
			}
			
			// Init Jade Gateway
			logger.log(Level.INFO, "Init Jade Gateway...");
			Object [] wsigAgentArguments = new Object[]{wsigStore};
			jadeGateway.init(wsigConfiguration.getAgentName(), wsigConfiguration.getAgentClassName(), wsigAgentArguments, wsigConfiguration);
			jadeGateway.addListener(this);
			logger.log(Level.INFO, "Jade Gateway initialized");
			
			// If the jade gateway is local replace platform and container informations 
			if (jadeGateway instanceof LocalJadeGateway) {
				String mainHost = jadeGateway.getProfileProperty(Profile.MAIN_HOST, wsigConfiguration.getMainHost());
				wsigConfiguration.setMainHost(mainHost);

				String mainPort = jadeGateway.getProfileProperty(Profile.MAIN_PORT, wsigConfiguration.getMainPort());
				wsigConfiguration.setMainPort(mainPort);

				String localPort = jadeGateway.getProfileProperty(Profile.LOCAL_PORT, "");
				wsigConfiguration.setLocalPort(localPort);

				String containerName = jadeGateway.getProfileProperty(Profile.CONTAINER_NAME, wsigConfiguration.getContainerName());
				wsigConfiguration.setContainerName(containerName);
			}
			
			// Set JADE gateway into context 
			webappContext.setAttribute(WEBAPP_GATEWAY_KEY, jadeGateway);
			
			// Start WSIG agent
			startupWSIGAgent();
			
		} else {
			jadeGateway = djg;
		}
	}

	@Override
	public void destroy() {
		// Close WSIGAgent
		shutdownWSIGAgent();

		// Close servlet
		super.destroy();

		logger.log(Level.INFO, "WSIG Servlet destroied");
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected OperationResult executeOperation(AgentAction agentAction, Map<String, String> headers, WSIGService wsigService) throws SOAPException {
		int timeout = WSIGConfiguration.getInstance().getWsigTimeout();
		AID agentExecutor = wsigService.getAid();
		Ontology onto = wsigService.getAgentOntology(); 
		WSIGBehaviour wsigBehaviour = new WSIGBehaviour(agentExecutor, agentAction, onto, timeout, headers);

		// Execute operation
		try {
			logger.log(Level.FINE, "Execute action "+agentAction+" on agent "+agentExecutor.getLocalName());
			jadeGateway.execute(wsigBehaviour, timeout);
		} catch (InterruptedException ie) {
			// Timeout
			logger.log(Level.SEVERE, "Timeout executing action "+agentAction);
			throw new SOAPException(SOAPException.FAULT_CODE_SERVER, SOAPException.FAULT_STRING_TIMEOUT, SOAPException.FAULT_ACTOR_WSIG);
		} catch (Exception e) {
			// Unexpected error
			logger.log(Level.SEVERE, "Unexpected error executing action "+agentAction, e);
			throw new SOAPException(SOAPException.FAULT_CODE_SERVER, e.getMessage(), SOAPException.FAULT_ACTOR_WSIG);
		} 

		// Check result
		if (wsigBehaviour.getStatus() == WSIGBehaviour.SUCCESS_STATUS) {
			// Success
			logger.log(Level.FINE, "Action "+agentAction+" successfully executed");
			return wsigBehaviour.getOperationResult();
		} else if (wsigBehaviour.getStatus() == WSIGBehaviour.APPLICATIVE_FAILURE_STATUS) {
			// Application failure
			logger.log(Level.FINE, "Action "+agentAction+" applicatically failed");
			return wsigBehaviour.getOperationResult();
		} else {
			// Other failure
			logger.log(Level.SEVERE, "Error executing action "+agentAction+": "+wsigBehaviour.getError());
			throw new SOAPException(SOAPException.FAULT_CODE_SERVER, wsigBehaviour.getError(), agentExecutor.getName());
		}
	}
	
	protected Map<String, String> extractHeaders(HttpServletRequest request) {
		Map<String, String> headers = new HashMap<String, String>();

		// Get HTTP headers
		if (WSIGConfiguration.getInstance().isTraceHttpHeaders()) {
			Enumeration headerEnum = request.getHeaderNames();
			while (headerEnum.hasMoreElements()) {
				String headerName = (String) headerEnum.nextElement();
				if (headerName != null) {
					String headerValue = request.getHeader(headerName);
					headers.put(WSIGConstants.HTTP_HEADER_PREFIX+"."+headerName, headerValue);
					logger.log(Level.FINE, "HTTP headerName: "+headerName+", headerValue: "+headerValue);
				}
			}
		}

		// Get client-ip
		if (WSIGConfiguration.getInstance().isTraceClientIP()) {
			String clientIP = request.getRemoteAddr();
			headers.put(WSIGConstants.WSIG_HEADER_PREFIX+"."+WSIGConstants.WSIG_HEADER_CLIENT_IP, clientIP);
			logger.log(Level.FINE, "Client IP: "+clientIP);
		}

		return headers;
	}
	
	private void startupWSIGAgent() {
		try {
			logger.log(Level.INFO, "Starting WSIG agent...");
			jadeGateway.checkJADE();
		} catch (ControllerException e) {
			logger.log(Level.WARNING, "Jade platform not present...WSIG agent not started", e);
		}
	}

	private void shutdownWSIGAgent() {
		logger.log(Level.INFO, "Stopping WSIG agent...");
		jadeGateway.shutdown();
	}

	public void handleGatewayConnected() {
		webappContext.setAttribute(WEBAPP_ACTIVE_KEY, true);
		logger.log(Level.INFO, "WSIG agent started");
	}

	public void handleGatewayDisconnected() {
		webappContext.setAttribute(WEBAPP_ACTIVE_KEY, false);
		logger.log(Level.INFO, "WSIG agent stopped");
	}
}		