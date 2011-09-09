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

import jade.content.AgentAction;
import jade.content.abs.AbsTerm;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.GatewayListener;
import jade.wrapper.gateway.JadeGateway;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.apache.soap.rpc.SOAPContext;
import org.apache.ws.axis.security.WSDoAllReceiver;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.handler.WSHandlerConstants;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.WSIGException;
import com.tilab.wsig.agent.WSIGBehaviour;
import com.tilab.wsig.soap.JadeToSoap;
import com.tilab.wsig.soap.SoapToJade;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.store.WSIGStore;
import com.tilab.wsig.wsdl.WSDLConstants;

public class WSIGServlet extends HttpServlet implements GatewayListener {
	
	private static final long serialVersionUID = -3447051223821710511L;

	private static Logger log = Logger.getLogger(WSIGServlet.class.getName());

	private WSIGStore wsigStore;
	private ServletContext servletContext;
	private AxisEngine axisEngine = new AxisClient(new NullProvider());
	private UsernameTokenCallback usernameTokenCallback;

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		
		log.info("Starting WSIG Servlet...");

		// Get configuration file path
		servletContext = servletConfig.getServletContext();
		String wsigPropertyPath = servletContext.getRealPath(WSIGConfiguration.WSIG_DEFAULT_CONFIGURATION_FILE);
		log.info("Configuration file= " + wsigPropertyPath);

		// Init configuration
		WSIGConfiguration.init(wsigPropertyPath);
		WSIGConfiguration wsigConfiguration = WSIGConfiguration.getInstance();
		
		// Set WSDL directory (convert relative to absolute path)
		String wsdlRelativeDirectory = wsigConfiguration.getWsdlDirectory();
		String wsdlAbsolutePath = servletContext.getRealPath(wsdlRelativeDirectory);
		wsigConfiguration.setWsdlDirectory(wsdlAbsolutePath);
		
		// Set WSIGConfiguration into servlet context 
		servletContext.setAttribute("WSIGConfiguration", wsigConfiguration);
		
		// Create a wsig store
		wsigStore = new WSIGStore();
		servletContext.setAttribute("WSIGStore", wsigStore);
		
		// Java type preservation
		String preserveJavaType = wsigConfiguration.getPreserveJavaType();
		if (preserveJavaType != null) {
			System.setProperty(SLCodec.PRESERVE_JAVA_TYPES, preserveJavaType);
		}
		
		// Init Jade Gateway
		log.info("Init Jade Gateway...");
		Object [] wsigAgentArguments = new Object[]{wsigStore};
		JadeGateway.init(wsigConfiguration.getAgentClassName(), wsigAgentArguments, wsigConfiguration);
		JadeGateway.addListener(this);
		log.info("Jade Gateway initialized");

		// Start WSIGAgent
		startupWSIGAgent();
		
		log.info("WSIG Servlet started");
	}

	public void destroy() {
		// Close WSIGAgent
		shutdownWSIGAgent();

		// Close servlet
		super.destroy();
		
		log.info("WSIG Servlet destroied");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {

		// Check if the request is a WSIG agent command
		String wsigAgentCommand = httpRequest.getParameter("WSIGAgentCommand");
		if (wsigAgentCommand != null && !wsigAgentCommand.equals("")) {

			// Elaborate WSIG agent command
			elaborateWSIGAgentCommand(wsigAgentCommand, httpRequest, httpResponse);
			return;
		}
		
		// A typical Web Service convention is that a request of the form 
		// http://<wsig-url>/<service-name>?WSDL (elements following the '?' are HTTP 
		// request parameters), e.g. http://localhost:8080/wsig/ws/MatchService?WSDL, 
		// is intended to retrieve the WSDL of the specified service.
		if (httpRequest.getParameterMap().containsKey("WSDL") ||
			httpRequest.getParameterMap().containsKey("wsdl")) {
			// Elaborate WSDL request
			elaborateWSDLRequest(httpRequest, httpResponse);
			return;
		}
		
		// SOAP message elaboration
		try {
			log.info("WSIG SOAP request arrived, start elaboration...");
	
			// Extract soap message from http
			Message soapRequest = null;
			try {
				soapRequest = extractSOAPMessage(httpRequest);
				
				log.debug("SOAP request:");
				log.debug(soapRequest.getSOAPPartAsString());
			} catch(Exception e) {
				log.error("Error extracting SOAP message from http request", e);
				throw new WSIGException(WSIGException.CLIENT, "Error extracting SOAP message from http request. "+e.getMessage());
			} 

			// Manage WSS security 
			try {
				manageRequestWSS(soapRequest);
			} catch(AxisFault e) {
				log.error("Error managing request WSS security credential", e);
				throw new WSIGException(WSIGException.SERVER, "Error managing request WSS security credential. "+e.getMessage());
			} 
			
			// Get wsig service and operation name
			String serviceName;
			String operationName;
			try {
				SOAPBody soapBody = soapRequest.getSOAPBody();
	
				serviceName = getServiceName(soapBody);
				log.info("Request service: "+serviceName);
				
				operationName = getOperationName(soapBody);
				log.info("Request operation: "+operationName);
			} catch (SOAPException e) {
				log.error("Error extracting SOAP body message from request", e);
				throw new WSIGException(WSIGException.CLIENT, "Error extracting SOAP body message from request. "+e.getMessage());
			}
			
			// Get WSIGService 
			WSIGService wsigService = wsigStore.getService(serviceName);
			if (wsigService == null) {
				log.error("Service "+serviceName+" not present in wsig");
				throw new WSIGException(WSIGException.SERVER, "Service "+serviceName+" not present in wsig");
			}
	
			// Convert soap to jade
			AgentAction agentAction = null;
			try {
				SoapToJade soapToJade = new SoapToJade();
				agentAction = (AgentAction)soapToJade.convert(soapRequest, wsigService, operationName);
				log.info("Jade Action: "+agentAction.toString());
			} catch (Exception e) {
				log.error("Error in soap to jade conversion", e);
				throw new WSIGException(WSIGException.SERVER, e.getMessage());
			}
	
			// Execute operation
			AbsTerm operationAbsResult = null;
			try {
				operationAbsResult = executeOperation(agentAction, wsigService);
				if (operationAbsResult != null) {
					log.info("operationResult: "+operationAbsResult+", type "+operationAbsResult.getTypeName());
				} else {
					log.info("operation without result");
				}
			} catch (WSIGException e) {
				log.error("Error executing operation "+operationName, e);
				throw e;
			}
			
			// Convert jade to soap
			SOAPMessage soapResponse = null;
			try {
				JadeToSoap jadeToSoap = new JadeToSoap();
				soapResponse = jadeToSoap.convert(operationAbsResult, wsigService, operationName);
			} catch(Exception e) {
				log.error("Error in jade to soap conversion", e);
				throw new WSIGException(WSIGException.SERVER, e.getMessage());
			}

			// Manage WSS security 
			try {
				manageResponseWSS(soapResponse);
			} catch(AxisFault e) {
				log.error("Error managing response WSS security credential", e);
				throw new WSIGException(WSIGException.SERVER, "Error managing response WSS security credential. "+e.getMessage());
			} 
			
			// Send http response
			try {
				sendHttpResponse(soapResponse, httpResponse);
			} catch(Exception e) {
				log.error("Error sending http response", e);
				throw new WSIGException(WSIGException.SERVER, "Error sending http response. "+e.getMessage());
			}
			
			log.info("WSIG SOAP response sended, stop elaboration.");
			
		} catch (WSIGException e) {
			// Manage fault
			try {
				sendHttpErrorResponse(e, httpResponse);
			} catch (SOAPException e1) {
				log.error("Error sending http error response", e1);
				httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}

	private void manageRequestWSS(Message soapRequest) throws AxisFault {
		String wssUsername = WSIGConfiguration.getInstance().getWssUsername();
		String wssPassword = WSIGConfiguration.getInstance().getWssPassword();
		String wssTimeToLive = WSIGConfiguration.getInstance().getWssTimeToLive();

		if (wssUsername != null || wssTimeToLive != null) {
			WSDoAllReceiver ws = new WSDoAllReceiver();
			String action = "";

			MessageContext mc = new MessageContext(axisEngine);
			mc.setMessage(soapRequest);
			
			if (wssUsername != null) {
				if (usernameTokenCallback == null) {
					usernameTokenCallback = new UsernameTokenCallback(wssUsername, wssPassword);
				}
				mc.setProperty(WSHandlerConstants.PW_CALLBACK_REF, usernameTokenCallback);
				
				action = WSHandlerConstants.USERNAME_TOKEN;
			}
			
			if (wssTimeToLive != null) {
				action = action + (action.length()!=0?" ":"") + WSHandlerConstants.TIMESTAMP;
			}
			 
			ws.setOption(WSHandlerConstants.ACTION, action);

			ws.invoke(mc);
		}
	}

	private void manageResponseWSS(SOAPMessage soapResponse) throws AxisFault {
		String wssTimeToLive = WSIGConfiguration.getInstance().getWssTimeToLive();
		
		if (wssTimeToLive != null) {
			WSDoAllSender ws = new WSDoAllSender(); 
			ws.setOption(WSHandlerConstants.ACTION, WSHandlerConstants.TIMESTAMP);

			MessageContext mc = new MessageContext(axisEngine);
			mc.setMessage(soapResponse);
			mc.setProperty(WSHandlerConstants.TTL_TIMESTAMP, wssTimeToLive);

			ws.invoke(mc);
		}
	}

	private AbsTerm executeOperation(AgentAction agentAction, WSIGService wsigService) throws WSIGException {
		
		AbsTerm absResult;
		int timeout = WSIGConfiguration.getInstance().getWsigTimeout();
		AID agentExecutor = wsigService.getAid();
		Ontology onto = wsigService.getAgentOntology(); 
		WSIGBehaviour wsigBehaviour = new WSIGBehaviour(agentExecutor, agentAction, onto, timeout);

		// Execute operation
		try {
			log.debug("Execute action "+agentAction+" on agent "+agentExecutor.getLocalName());
			JadeGateway.execute(wsigBehaviour, timeout);
		} catch (InterruptedException ie) {
			// Timeout
			log.error("Timeout executing action "+agentAction);
			throw new WSIGException(WSIGException.SERVER, "TIMEOUT");
		} catch (Exception e) {
			// Unexpected error
			log.error("Unexpected error executing action "+agentAction, e);
			throw new WSIGException(WSIGException.SERVER, e.getMessage());
		} 
		
		// Check result
		if (wsigBehaviour.getStatus() == WSIGBehaviour.SUCCESS_STATUS) {
			log.debug("Action "+agentAction+" successfully executed");
			absResult = wsigBehaviour.getAbsResult();
		} else {
			// Agent error
			log.error("Error executing action "+agentAction+": "+wsigBehaviour.getError());
			throw new WSIGException(WSIGException.SERVER, wsigBehaviour.getError());
		}
		
		return absResult;
	}

	private void elaborateWSIGAgentCommand(String wsigAgentCommand, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException  {
	
		log.info("WSIG agent command arrived ("+wsigAgentCommand+")");

		if (wsigAgentCommand.equalsIgnoreCase("start")) {
			// Start WSIGAgent
			startupWSIGAgent();
			try {
				// Wait a bit for the registration of services before refreshing the page
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		} else if (wsigAgentCommand.equalsIgnoreCase("stop")) {
			// Stop WSIGAgent
			shutdownWSIGAgent();				
		} else {
			log.warn("WSIG agent command not implementated");
		}
		
		log.info("WSIG agent command elaborated");

		// Redirect to console home page
		URL consoleUrl = WSIGConfiguration.getAdminUrl(httpRequest);
		httpResponse.sendRedirect(consoleUrl.toString());
	}

	private void elaborateWSDLRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {

		String requestURL = httpRequest.getRequestURL().toString();
		log.info("WSDL request arrived ("+requestURL+")");
		
		int pos = requestURL.lastIndexOf('/');
		if (pos == -1) {
			httpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "WSDL request " + requestURL + " not correct");
			return;
		}

		String serviceName = requestURL.substring(pos+1);
		log.info("WSDL request for service "+serviceName);
		
		// Get WSIGService 
		WSIGService wsigService = wsigStore.getService(serviceName);
		if (wsigService == null) {
			log.error("Service "+serviceName+" not present in wsig");
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service "+serviceName+" not present in wsig");
			return;
		}
		
		// Get wsdl definition (and update soap endpoint)
		Definition wsdlDefinition = wsigService.getWsdlDefinition(httpRequest);

		// Send wsdl over http
		try {
			WSDLFactory.newInstance().newWSDLWriter().writeWSDL(wsdlDefinition, httpResponse.getOutputStream());
		} catch (WSDLException e) {
			log.error("Error sending wsdl of service "+serviceName);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error sending wsdl of service "+serviceName);
		}
	}

	private String getOperationName(SOAPBody body) {
		SOAPElement el;
		String operationName = null;
		Iterator it = body.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			operationName = el.getElementName().getLocalName();
		}
		
		return operationName;
	}

	private String getServiceName(SOAPBody body) {
		SOAPElement el;
		String serviceName = null;
		Iterator it = body.getChildElements();
		while ( it.hasNext() ) {
			el = (SOAPElement) it.next();
			String nsUri = el.getNamespaceURI();
			int pos = nsUri.indexOf(':');
			serviceName = nsUri.substring(pos+1);
		}
		return serviceName;
	}
	
	private Message extractSOAPMessage(HttpServletRequest request) throws IOException, MessagingException {
		// Get http header
		String contentLocation = request.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION);
		log.debug("contentLocation: "+contentLocation);

		String contentType = request.getHeader(HTTPConstants.HEADER_CONTENT_TYPE);
		log.debug("contentType: "+contentType);

		// Get soap message
		Message soapRequest = new Message(request.getInputStream(), false, contentType, contentLocation);
		
		// Transfer HTTP headers to MIME headers for request message
		MimeHeaders requestMimeHeaders = soapRequest.getMimeHeaders();
		SOAPContext soapContext = new SOAPContext();
		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			for (Enumeration f = request.getHeaders(headerName); f.hasMoreElements();) {
				String headerValue = (String) f.nextElement();
				requestMimeHeaders.addHeader(headerName, headerValue);
				MimeBodyPart p = new MimeBodyPart();
				p.addHeader(headerName, headerValue);
				log.debug("headerName: "+headerName+", headerValue: "+headerValue);
				
				soapContext.addBodyPart(p);
			}
		}
		
		return soapRequest;
	}
	
	private void sendHttpResponse(SOAPMessage soapMessage, HttpServletResponse httpResponse) throws SOAPException, IOException  {
		// Set http header
		httpResponse.setHeader("Cache-Control", "no-store");
	    httpResponse.setHeader("Pragma", "no-cache");
	    httpResponse.setDateHeader("Expires", 0);
	    httpResponse.setContentType("text/xml; charset=utf-8");

	    // Convert soap message in byte-array
		byte[] content = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		soapMessage.writeTo(baos);
		content = baos.toByteArray();
	    
		// Write response
	    ServletOutputStream responseOutputStream = httpResponse.getOutputStream();
        responseOutputStream.write(content);
        responseOutputStream.flush();
        responseOutputStream.close();
        
		log.debug("SOAP response:");
		log.debug(baos.toString());
	}
	
	private void sendHttpErrorResponse(SOAPMessage soapFaultMessage, HttpServletResponse httpResponse) throws SOAPException, IOException {
		// Set http error (500)
		httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		
		// Send response 
		sendHttpResponse(soapFaultMessage, httpResponse);
	}

	private void sendHttpErrorResponse(WSIGException e, HttpServletResponse httpResponse) throws SOAPException, IOException {
		// Create soap message
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapFaultMessage = messageFactory.createMessage();
        
        // Create soap part and body            
        SOAPPart soapPart = soapFaultMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix(WSDLConstants.SOAPENVELOP_PREFIX);
        envelope.addNamespaceDeclaration(WSDLConstants.XSD, WSDLConstants.XSD_URL);
        SOAPBody body = envelope.getBody();
		
        // Create soap fault
        SOAPFault fault = body.addFault();
        fault.setFaultActor(e.getFaultActor());
        fault.setFaultCode(e.getFaultCode());
        fault.setFaultString(e.getFaultString());
        
        // Send response
        sendHttpErrorResponse(soapFaultMessage, httpResponse);
	}
	
	private void startupWSIGAgent() {
		try {
			log.info("Starting WSIG agent...");
			JadeGateway.checkJADE();
		} catch (ControllerException e) {
			log.warn("Jade platform not present...WSIG agent not started");
		}
	}

	private void shutdownWSIGAgent() {
		log.info("Stopping WSIG agent...");
		JadeGateway.shutdown();
	}

	@Override
	public void handleGatewayConnected() {
		servletContext.setAttribute("WSIGActive", true);
		log.info("WSIG agent started");
	}

	@Override
	public void handleGatewayDisconnected() {
		servletContext.setAttribute("WSIGActive", false);
		log.info("WSIG agent stopped");
	}
	
	
	// Inner class to check WSS UsernameToken credential
	private class UsernameTokenCallback implements CallbackHandler {
		
		private String username;
		private String password;

		public UsernameTokenCallback(String username, String password) {
			this.username = username;
			this.password = password;
		}
		
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof WSPasswordCallback) {
					WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
					
					if (!username.equals(pc.getIdentifer()) ||
						!password.equals(pc.getPassword())) {
						throw new IOException("Wrong WSS username-token credential");
					}
				} else {
					throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
				}
			}
		}
	}

}
