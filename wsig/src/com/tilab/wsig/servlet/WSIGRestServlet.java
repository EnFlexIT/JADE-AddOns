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

import jade.content.ContentElement;
import jade.wrapper.gateway.DynamicJadeGateway;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONObject;
import org.json.XML;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.rest.JadeToRest;
import com.tilab.wsig.rest.RestException;
import com.tilab.wsig.rest.RestToJade;
import com.tilab.wsig.store.OperationResult;
import com.tilab.wsig.store.WSIGService;


public class WSIGRestServlet extends WSIGServletBase {
	private static final long serialVersionUID = -3447051223821710511L;


	public WSIGRestServlet() {
		super();
	}

	public WSIGRestServlet(DynamicJadeGateway jadeGateway) {
		super(jadeGateway);
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		logger.log(Level.INFO, "Starting WSIG REST Servlet...");

		super.init(servletConfig);

		String endpointPath = servletConfig.getInitParameter(WSIGConfiguration.WSIG_ENDPOINT_PATH_KEY);
		if (endpointPath != null && !endpointPath.isEmpty()) {
			WSIGConfiguration.getInstance().setRESTServicesPath(endpointPath);
		}

		logger.log(Level.INFO, "WSIG REST Servlet started");
	}

	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		//getting the Accept Content Type. It could be :application/json or application/xml
		String accept =  httpRequest.getHeader("Accept");
		//set default Accept to application/xml
		if (accept == null || accept.equals("*/*")) {
			accept = MediaType.APPLICATION_XML;
		}
		//getting the Body request content type. It could be :application/json or application/xml
		String contentType = httpRequest.getContentType();

		// Rest elaboration 
		try {
			if (!(accept.equals(MediaType.APPLICATION_XML) || accept.equals(MediaType.APPLICATION_JSON))) {
				throw new RestException(RestException.FAULT_CODE_CLIENT, "Unsupported Accept Media type: "+accept+". ", RestException.FAULT_ACTOR_WSIG);
			}

			if (contentType == null || !(contentType.equals(MediaType.APPLICATION_XML) || contentType.equals(MediaType.APPLICATION_JSON))) {
				throw new RestException(RestException.FAULT_CODE_CLIENT, "Unsupported Content Type for request body. Check if you have specified the appropriate Content Type", RestException.FAULT_ACTOR_WSIG);
			}

			logger.log(Level.INFO, "WSIG REST request arrived, start elaboration...");
			StringBuilder stringBuilder = new StringBuilder();  
			BufferedReader bufferedReader = null;  

			try {  
				//getting the request body from the httpRequest	
				InputStream inputStream = httpRequest.getInputStream(); 

				if (inputStream != null) {  
					bufferedReader = new BufferedReader(new InputStreamReader(inputStream));  

					char[] charBuffer = new char[128];  
					int bytesRead = -1;  

					while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {  
						stringBuilder.append(charBuffer, 0, bytesRead);  
					}  
				} else {  
					stringBuilder.append("");  
				}  
			} catch (IOException ex) {  

			} finally {  
				if (bufferedReader != null) {  
					try {  
						bufferedReader.close();  
					} catch (IOException ex) {  

					}  
				}  
			}  

			String requestBodyString = stringBuilder.toString(); 
			if (logger.isLoggable(Level.CONFIG)) {
				StringBuilder sb = new StringBuilder();
				sb.append("---- REST request ----");
				sb.append(System.getProperty("line.separator"));
				sb.append(requestBodyString);
				sb.append(System.getProperty("line.separator"));
				sb.append("----");
				logger.log(Level.CONFIG, sb.toString());
			}

			// Get wsig service and operation name
			String serviceName = null;

			//getting the Service Name from the path
			String pathInfo = httpRequest.getPathInfo();
			String[] paths = null;
			if (pathInfo != null) {
				paths = pathInfo.split("/");
			} else {
				logger.log(Level.SEVERE, "resource path not specified");
				throw new RestException(RestException.FAULT_CODE_CLIENT, "resource path not specified. ", RestException.FAULT_ACTOR_WSIG);
			}

			if (paths.length != 2) {
				logger.log(Level.SEVERE, "the resource path is not correct");
				throw new RestException(RestException.FAULT_CODE_CLIENT, "the resource path is not correct. ", RestException.FAULT_ACTOR_WSIG);
			} else {
				serviceName= paths[1];
				logger.log(Level.INFO, "Request service: "+serviceName);
			}	

			//if content type is equal to application/xml, the body request remains the same type,
			//and it is parsed to get the Operation Name
			String xml = null;
			if (contentType.equals(MediaType.APPLICATION_XML)) {
				xml = removeXmlHeader(requestBodyString);
			}
			//if content type is equal to application/json the body request is converted from json to xml,
			//and the Operation Name is obtained as well			
			else if (contentType.equals(MediaType.APPLICATION_JSON)){
				try {
					JSONObject jsonObj = new JSONObject(requestBodyString);
					xml = removeXmlHeader(XML.toString(jsonObj));
				} catch (Exception e) {
					throw new RestException(RestException.FAULT_CODE_CLIENT, "Error with Content Type  "+e, RestException.FAULT_ACTOR_WSIG);
				}
			}		

			// Check if WSIG is up, in the case of down status and user status is active 
			// and automatic startup is true try to activate it
			checkAutomaticStartupWSIGAgent();
			
			// Get WSIGService 
			WSIGService wsigService = wsigStore.getService(serviceName);
			if (wsigService == null) {
				logger.log(Level.SEVERE, "Service "+serviceName+" not present in wsig");
				throw new RestException(RestException.FAULT_CODE_CLIENT, "Service name "+serviceName+" not present in wsig. ", RestException.FAULT_ACTOR_WSIG);
			}

			// Try to convert all the xml attributes to xml tags
			try {
				xml = convertAttributesToTags(xml);
				// The xsl transformation add the xml header -> remove again 
				xml = removeXmlHeader(xml);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error converting xml attributes to xml tags", e);
				throw new RestException(RestException.FAULT_CODE_CLIENT, "Error converting xml attributes to xml tags", RestException.FAULT_ACTOR_WSIG);
			}

			// Extract operation name
			String operationName = getOperationNameFromXML(xml);
			
			// Extract HTTP headers and put in threadLocal HTTPInfo 
			try {
				handleInputHeaders(httpRequest);
			} catch(Exception e) {
				logger.log(Level.SEVERE, "Error extracting headers from http request", e);
				throw new RestException(RestException.FAULT_CODE_CLIENT, "Error extracting headers from http request. "+e.getMessage(), RestException.FAULT_ACTOR_WSIG);
			} 
			
			// Convert REST to jade
			ContentElement agentAction = null;
			try {
				RestToJade restToJade = new RestToJade();
				agentAction = (ContentElement) restToJade.convert(xml, wsigService, operationName);
				logger.log(Level.INFO, "Jade Action: "+agentAction.toString());
			}
			catch(InvocationTargetException ite) {
				logger.log(Level.WARNING, "Error mapper invocation method", ite);
				throw new RestException(RestException.FAULT_CODE_SERVER, ite.getTargetException().getMessage(), RestException.FAULT_ACTOR_WSIG);
			}
			catch (Exception e) {
				logger.log(Level.SEVERE, "Error in REST to jade conversion", e);
				throw new RestException(RestException.FAULT_CODE_SERVER, e.getMessage(), RestException.FAULT_ACTOR_WSIG);
			}

			// Execute operation
			OperationResult opResult = null;
			try {
				opResult = executeOperation(agentAction, HTTPInfo.getInputHeaders(), wsigService);
				if (opResult.getResult() == OperationResult.Result.OK) {
					if (opResult.getValue() != null) {
						logger.log(Level.INFO, "operationResult: "+opResult.getValue()+", type "+opResult.getValue().getTypeName());
					} else {
						logger.log(Level.INFO, "operation without result");
					}
				} else {
					logger.log(Level.INFO, "operation failed: "+opResult.getMessage().getContent());
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error executing operation "+operationName, e);
			}

			// Convert jade to REST
			String bodyResponse = null;
			try {
				JadeToRest jadeToRest = new JadeToRest();
				bodyResponse = jadeToRest.convert(opResult, wsigService, operationName);

			}
			catch(InvocationTargetException ite) {
				logger.log(Level.WARNING, "Error mapper invocation method", ite);
				throw new RestException(RestException.FAULT_CODE_SERVER, ite.getTargetException().getMessage(), RestException.FAULT_ACTOR_WSIG);
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Error in jade to REST conversion", e);
				throw new RestException(RestException.FAULT_CODE_SERVER, e.getMessage(), RestException.FAULT_ACTOR_WSIG);
			}

			// Send http response
			try {
				if (opResult.getResult() == OperationResult.Result.OK) {
					// Fill HTTP headers with the threadLocal HTTPInfo 
					handleOutputHeaders(httpResponse);
					
					sendRESTHttpResponse(bodyResponse, httpResponse, accept);
				} else {
					sendRESTHttpErrorResponse(bodyResponse, httpResponse, accept);
				}
			} catch(Exception e) {
				logger.log(Level.SEVERE, "Error sending http response", e);
				throw new RestException(RestException.FAULT_CODE_SERVER, "Error sending http response. "+e.getMessage(), RestException.FAULT_ACTOR_WSIG);
			}
		} catch (RestException e) {
			try {
				//Manage generic fault
				String faultBodyResponse = JadeToRest.convert(e);

				// Send http response				
				sendRESTHttpErrorResponse(faultBodyResponse, httpResponse, accept);
			} catch (Exception e1) {
				logger.log(Level.SEVERE, "Error sending http error response", e1);
				httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage());
			}
		}
	}

	private String removeXmlHeader(String xml) {
		if (xml.contains("<?xml")) {
			int index = xml.indexOf(">");
			xml = xml.substring(index+1);
			index = xml.indexOf("<");
			xml = xml.substring(index);
		}
		return xml;
	}
		
	private static final String xslString = 
	"<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>"+
	"  <xsl:output method='xml' indent='no'/>"+
	"  <xsl:template match='node()'>"+
	"    <xsl:copy>"+
	"      <xsl:apply-templates select='@*|node()'/>"+
	"    </xsl:copy>"+
	"  </xsl:template>"+
	"  <xsl:template match='@*'>"+
	"    <xsl:element name='{name()}'><xsl:value-of select='.'/></xsl:element>"+
	"  </xsl:template>"+
	"</xsl:stylesheet>";

	private String convertAttributesToTags(String xmlString) throws Exception {
		StreamSource xsl = new StreamSource(new ByteArrayInputStream(xslString.getBytes()));
		Transformer transformer = TransformerFactory.newInstance().newTransformer(xsl); 
		StreamSource xml = new StreamSource(new ByteArrayInputStream(xmlString.getBytes())); 
		StringWriter writer = new StringWriter();
		transformer.transform(xml, new StreamResult(writer));
		return writer.toString();
	}

	private void sendRESTHttpResponse(String bodyResponse, HttpServletResponse httpResponse, String accept) throws Exception  {
		// Set http header
		httpResponse.setHeader("Cache-Control", "no-store");
		httpResponse.setHeader("Pragma", "no-cache");
		httpResponse.setDateHeader("Expires", 0);

		if (accept != null) {
			if (bodyResponse.contains("<soapenv:Body")) {
				bodyResponse = bodyResponse.replace("<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">","");
				bodyResponse = bodyResponse.replace("</soapenv:Body>", "");
				bodyResponse = bodyResponse.replaceAll(" xmlns=\"\"", "");	
				bodyResponse = bodyResponse.replaceAll(" xmlns=\".*\"", "");	
				bodyResponse = bodyResponse.replaceAll("soapenv:", "");
				
				if (accept.equals(MediaType.APPLICATION_JSON)) {
					JSONObject jsonObj = XML.toJSONObject(bodyResponse);
					bodyResponse = jsonObj.toString();
				}
			}
		}

		// Write response
		ServletOutputStream responseOutputStream = httpResponse.getOutputStream();
		if (bodyResponse != null) {
			responseOutputStream.write(bodyResponse.getBytes(Charset.forName("UTF-8")));
		}
		responseOutputStream.flush();
		responseOutputStream.close();
	}

	private void sendRESTHttpErrorResponse(String faultBodyRequest, HttpServletResponse httpResponse, String accept) throws Exception {
		// Set http error (500)
		httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		// Send response 
		sendRESTHttpResponse(faultBodyRequest, httpResponse, accept);
	}

	private String getOperationNameFromXML(String xml) throws RestException{
		String operationName = "";
		int index = xml.indexOf("<");
		int index2 = xml.indexOf(">");
		if (index != -1 && index < index2) {
			String temp1 = xml.substring(index+1);
			index = temp1.indexOf(">");
			operationName = temp1.substring(0,index);
			if (operationName.endsWith("/")) {
				operationName = operationName.replace("/", "");
			}
			logger.log(Level.INFO, "Operation Name: "+operationName);		
		} else{
			throw new RestException(RestException.FAULT_CODE_CLIENT, "The Body request Content Type does not correspond to a Well-Formed XML, Check if you specified the correct Content Type ", RestException.FAULT_ACTOR_WSIG);
		}					

		return operationName;
	}
}
