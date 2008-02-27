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
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.util.leap.Properties;
import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axis.Message;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.apache.soap.rpc.SOAPContext;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.agent.WSIGBehaviour;
import com.tilab.wsig.soap.JadeToSoap;
import com.tilab.wsig.soap.SoapToJade;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.store.WSIGStore;

public class WSIGServlet extends HttpServlet {
	
	private static Logger log = Logger.getLogger(WSIGServlet.class.getName());

	private WSIGStore wsigStore = new WSIGStore();
	private SoapToJade soapToJade = new SoapToJade();
	private JadeToSoap jadeToSoap = new JadeToSoap();
	private int executionTimeout = 0;
	private ServletContext servletContext = null;
	private String consoleUri;

	/**
	 * Init wsig servlet
	 */
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		
		log.info("Starting WSIG Servlet...");
		servletContext = servletConfig.getServletContext();
		String wsigPropertyPath = servletContext.getRealPath(WSIGConfiguration.WSIG_DEFAULT_CONFIGURATION_FILE);
		log.info("Configuration file= " + wsigPropertyPath);

		// Read WSIG property
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(wsigPropertyPath));
		} catch (IOException e) {
			log.info("Error reading wsig configuration");
			throw new ServletException(e);
		}
		
		// Get properties
		consoleUri = props.getProperty(WSIGConfiguration.KEY_WSIG_CONSOLE_URI);
		String gatewayClassName = props.getProperty(WSIGConfiguration.KEY_WSIG_AGENT_CLASS_NAME);
		String wsdlDirectory = props.getProperty(WSIGConfiguration.KEY_WSDL_DIRECTORY);
		String wsdlPath = servletContext.getRealPath(wsdlDirectory);
		String timeout = props.getProperty(WSIGConfiguration.KEY_WSIG_TIMEOUT);
		executionTimeout = Integer.parseInt(timeout);
		
		// Create a wsig store
		wsigStore = new WSIGStore();
		servletContext.setAttribute("WSIGStore", wsigStore);
		
		// Init configuration
		WSIGConfiguration.init(wsigPropertyPath);
		servletContext.setAttribute("WSIGConfiguration", WSIGConfiguration.getInstance());
		
		// Init Jade Gateway
		log.info("Init Jade Gateway...");
		Object [] wsigArguments = new Object[]{wsigPropertyPath, wsdlPath, wsigStore};
		props.setProperty(jade.core.Profile.MAIN, "false");
		
		JadeGateway.init(gatewayClassName, wsigArguments, props);
		log.info("Jade Gateway initialized");

		// Start WSIGAgent
		startupWSIGAgent();
		
		log.info("WSIG Servlet started");
	}

	/**
	 * close wsig servlet
	 */
	public void destroy() {

		// Close WSIGAgent
		shutdownWSIGAgent();

		super.destroy();
		log.info("WSIG Servlet destroied");
	}

	/**
	 * Manage get request
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);
	}
	
	/**
	 * Manage post request
	 */
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {

		// Check if the request is a WSIG agent command
		String wsigAgentCommand = httpRequest.getParameter("WSIGAgentCommand");
		if (wsigAgentCommand != null && !wsigAgentCommand.equals("")) {

			// Elaborate WSIG agent command
			elaborateWSIGAgentCommand(wsigAgentCommand, httpResponse);
			return;
		}
		
		// Check if the request is WSDL request
		if (httpRequest.getParameterMap().containsKey("WSDL")) {
			// Elaborate WSDL request
			elaborateWSDLRequest(httpRequest.getRequestURL().toString(), httpResponse);
			return;
			
		}
		
		// SOAP message elaboration
		log.info("WSIG SOAP request arrived, start elaboration...");

		// Extract soap messge from http
		Message soapRequest = null;
		try {
			soapRequest = extractSOAPMessage(httpRequest);
			log.debug("SOAP request:");
			log.debug(soapRequest.getSOAPPartAsString());
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		// Get wsig service & operation name
		String serviceName = null;
		String operationName = null;
		try {
			SOAPBody body = soapRequest.getSOAPPart().getEnvelope().getBody();
			
			serviceName = getServiceName(body);
			log.info("Request service: "+serviceName);
			
			operationName = getOperationName(body);
			log.info("Request operation: "+operationName);
		} catch (SOAPException ex) {
			log.error("Error extracting service and operation name", ex);
			httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
			return;
		}
		
		// Get WSIGService 
		WSIGService wsigService = wsigStore.getService(serviceName);
		if (wsigService == null) {
			log.error("Service "+serviceName+" not present in wsig");
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Service "+serviceName+" not present in wsig");
			return;
		}

		// Convert soap to jade
		AgentAction agentAction = null;
		try {
			agentAction = (AgentAction)soapToJade.convert(soapRequest, wsigService, operationName);
			log.info("Jade Action: "+agentAction.toString());
		} catch (Exception e) {
			log.error("Error in soap to jade conversion", e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in soap to jade conversion. "+e.getMessage());
			return;
		}

		// Execute operation
		Object operationResult = null;
		try {
			operationResult = executeOperation(agentAction, wsigService);
			if (operationResult != null) {
				log.info("operationResult: "+operationResult+", type "+operationResult.getClass().getName());
			} else {
				log.info("operation without result");
			}
		} catch (Exception e) {
			log.error("Error executing operation "+serviceName+"."+operationName);
			int errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			if (WSIGBehaviour.TIMEOUT.equals(e.getMessage())) {
				errorCode = HttpServletResponse.SC_REQUEST_TIMEOUT;
			}
			httpResponse.sendError(errorCode, "Error executing operation "+serviceName+"."+operationName+", error: "+e.getMessage());
			return;
		}
		
		// Convert jade to soap
		SOAPMessage soapResponse = null;
		try {
			soapResponse = jadeToSoap.convert(operationResult, wsigService, operationName);
			
			log.debug("SOAP response:");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			soapResponse.writeTo(baos);
			log.debug(baos.toString());

		} catch(Exception e) {
			log.error("Error in jade to soap conversion", e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error in jade to soap conversion");
			return;
		}
		
		// Fill http response
		try {
			fillHttpResponse(soapResponse, httpResponse);
			
		} catch(Exception e) {
			log.error("Error filling http response", e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error filling http response");
			return;
		}
		
		log.info("WSIGServlet doPost elaboration terminated");
	}

	/**
	 * execute wsig operation
	 * @param agentAction
	 * @param wsigService
	 * @return
	 * @throws Exception
	 */
	private Object executeOperation(AgentAction agentAction, WSIGService wsigService) throws Exception{
		
		AID agentReceiver = wsigService.getAid();
		Ontology onto = wsigService.getOnto(); 
		Object result = null;
		
		WSIGBehaviour wsigBh = new WSIGBehaviour(agentReceiver, agentAction, onto, executionTimeout);

		log.debug("Execute action "+agentAction+" on "+agentReceiver.getLocalName());
		
		JadeGateway.execute(wsigBh, executionTimeout);
		if (wsigBh.getStatus() == WSIGBehaviour.EXECUTED_STATUS) {
			log.debug("Behaviour executed");
			result = wsigBh.getResult();
		} else {
			throw new Exception(wsigBh.getError());
		}
		
		return result;
	}

	/**
	 * Elaborate WSIG Agent Command
	 * @param wsigAgentCommand
	 * @param httpResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void elaborateWSIGAgentCommand(String wsigAgentCommand, HttpServletResponse httpResponse) throws ServletException, IOException {
	
		log.info("WSIG agent command arrived ("+wsigAgentCommand+")");

		if (wsigAgentCommand.equalsIgnoreCase("start")) {
			// Start WSIGAgent
			startupWSIGAgent();
		} else if (wsigAgentCommand.equalsIgnoreCase("stop")) {
			// Stop WSIGAgent
			shutdownWSIGAgent();				
		} else {
			log.warn("WSIG agent command not implementated");
		}
		
		log.info("WSIG agent command elaborated");

		// Redirect to console home page
		httpResponse.sendRedirect(consoleUri);
	}

	/**
	 * Elaborate WSDL request
	 * @param requestURL
	 * @param httpResponse
	 */
	private void elaborateWSDLRequest(String requestURL, HttpServletResponse httpResponse) throws ServletException, IOException {
		
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

		// Redirect to wsdl
		String wsdlUrl = wsigService.getWsdl().toString();
		log.info("Redirect to " + wsdlUrl);
		httpResponse.sendRedirect(wsdlUrl);
	}

	/**
	 * get operation name from soap body
	 * @param body
	 * @return
	 */
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

	/**
	 * get service name from soap body
	 * @param body
	 * @return
	 */
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
	
	/**
	 * extract SOAP Message from http request
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Message extractSOAPMessage(HttpServletRequest request) throws Exception {
		
		// Get http header
		String contentLocation = request.getHeader(HTTPConstants.HEADER_CONTENT_LOCATION);
		log.debug("contentLocation: "+contentLocation);

		String contentType = request.getHeader(HTTPConstants.HEADER_CONTENT_TYPE);
		log.debug("contentType: "+contentType);

		// Get soap message
		Message soapRequest = null;
		try {
			soapRequest = new Message(request.getInputStream(),
				false,
				contentType,
				contentLocation);
		} catch(IOException e) {
			throw new Exception("Error extracting soap message", e);
		}
		
		// Transfer HTTP headers to MIME headers for request message
		MimeHeaders requestMimeHeaders = soapRequest.getMimeHeaders();
		SOAPContext soapContext = new SOAPContext();
		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
			String headerName = (String) e.nextElement();
			for (Enumeration f = request.getHeaders(headerName);
				 f.hasMoreElements();) {
				String headerValue = (String) f.nextElement();
				requestMimeHeaders.addHeader(headerName, headerValue);
				MimeBodyPart p = new MimeBodyPart();
				try {
					p.addHeader(headerName, headerValue);
					log.debug("headerName: "+headerName+", headerValue: "+headerValue);
					soapContext.addBodyPart(p);
				} catch (MessagingException ex) {
					throw new Exception("Error building soap context", ex);				}
			}
		}
		
		return soapRequest;
	}
	
	/**
	 * Fill http response
	 * @param soapResponse
	 * @param httpResponse
	 * @return
	 */
	private void fillHttpResponse(SOAPMessage soapResponse, HttpServletResponse httpResponse) throws Exception {
	
		byte[] content = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		soapResponse.writeTo(baos);
		content = baos.toByteArray();

		// Set http response
	    httpResponse.setHeader("Cache-Control", "no-store");
	    httpResponse.setHeader("Pragma", "no-cache");
	    httpResponse.setDateHeader("Expires", 0);
	    httpResponse.setContentType("soap+xml; charset=utf-8");
        ServletOutputStream responseOutputStream = httpResponse.getOutputStream();
        responseOutputStream.write(content);
        responseOutputStream.flush();
        responseOutputStream.close();
	}
	
	/**
	 * Start WSIG Agent
	 */
	private void startupWSIGAgent() {
		try {
			log.info("Starting WSIG agent...");
			JadeGateway.checkJADE();
			log.info("WSIG agent started");
		} catch (ControllerException e) {
			log.warn("Jade platform not present...WSIG agent not started");
		}
		setWSIGStatus();
	}

	/**
	 * Close WSIG Agent
	 */
	private void shutdownWSIGAgent() {
	
		JadeGateway.shutdown();
		setWSIGStatus();
		log.info("WSIG agent closed");
	}
	
	/**
	 * Set WSIG agent status
	 * @param wsigAgentUp
	 */
	private void setWSIGStatus() {
		servletContext.setAttribute("WSIGActive", new Boolean(JadeGateway.isGatewayActive()));
	}
}
