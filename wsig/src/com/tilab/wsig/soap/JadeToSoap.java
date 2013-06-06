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

package com.tilab.wsig.soap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;
import jade.content.abs.AbsTerm;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.TermSchema;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.axis.utils.XMLUtils;
import org.apache.log4j.Logger;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.WSIGConstants;
import com.tilab.wsig.store.FaultBuilder;
import com.tilab.wsig.store.OperationResult;
import com.tilab.wsig.store.ParameterInfo;
import com.tilab.wsig.store.ResultBuilder;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.wsdl.WSDLConstants;
import com.tilab.wsig.wsdl.WSDLUtils;

public class JadeToSoap {

	private static Logger log = Logger.getLogger(JadeToSoap.class.getName());
	
	private String localNamespacePrefix;
	private String soapStyle;
	private Ontology onto;
	private boolean hierarchicalComplexType;
	private SOAPMessage soapMessage;
	private SOAPPart soapPart;
	private SOAPEnvelope soapEnvelope;
	private SOAPBody soapBody;
	private String tns;
	
	public JadeToSoap() {
		localNamespacePrefix = WSIGConfiguration.getInstance().getLocalNamespacePrefix(); 
		soapStyle = WSIGConfiguration.getInstance().getWsdlStyle();
	}
	
	public synchronized SOAPMessage convert(OperationResult opResult, WSIGService wsigService, String operationName) throws Exception {
		onto = wsigService.getServiceOntology();
		hierarchicalComplexType = wsigService.isHierarchicalComplexType();
		tns = WSDLConstants.URN + ":" + wsigService.getServicePrefix() + wsigService.getServiceName();
		
        MessageFactory messageFactory = MessageFactory.newInstance();
        soapMessage = messageFactory.createMessage();
        soapPart = soapMessage.getSOAPPart();
        soapEnvelope = soapPart.getEnvelope();
        soapEnvelope.setPrefix(WSDLConstants.SOAPENVELOP_PREFIX);
        soapEnvelope.addNamespaceDeclaration(WSDLConstants.XSD, WSDLConstants.XSD_URL);
        soapEnvelope.addNamespaceDeclaration(localNamespacePrefix, tns);
        soapBody = soapEnvelope.getBody();
		
		if (opResult.getResult() == OperationResult.Result.OK) {
			fillResult(opResult, wsigService, operationName);
		} else {
			fillFault(opResult, wsigService, operationName);
		}
		
		soapMessage.saveChanges();
		return soapMessage;
	}
	
	public static SOAPMessage convert(SOAPException e) throws Exception {
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

        return soapFaultMessage;
	}
	
	private void fillFault(OperationResult opResult, WSIGService wsigService, String operationName) throws Exception {
		FaultBuilder faultBuilder = wsigService.getFaultBuilder(operationName);
        if (faultBuilder == null) {
			throw new Exception("Fault builder not found for operation "+operationName+" in WSIG");
        }
		
		faultBuilder.prepare(opResult.getMessage());
		
		SOAPFault soapFault = soapBody.addFault();
		soapFault.setFaultCode(faultBuilder.getFaultCode());
		soapFault.setFaultString(faultBuilder.getFaultString());
		soapFault.setFaultActor(faultBuilder.getFaultActor());
	}
	
	private void fillResult(OperationResult opResult, WSIGService wsigService, String operationName) throws Exception {
        // Create soap element
        String responseElementName = operationName + WSDLConstants.RESPONSE_SUFFIX;
        SOAPElement responseElement = addSoapElement(soapBody, responseElementName, localNamespacePrefix, null, tns, false);
        
        // Get action builder
        log.debug("Operation name: "+operationName);
        ResultBuilder resultBuilder = wsigService.getResultBuilder(operationName);
        if (resultBuilder == null) {
			throw new Exception("Result builder not found for operation "+operationName+" in WSIG");
        }
        
        // Get response schema
    	ObjectSchema responseSchema = resultBuilder.getResponseSchema();
    	log.debug("Ontology result type: "+responseSchema.getTypeName());
    	
    	// Loop all element parameters
    	List<ParameterInfo> operationResultValues = resultBuilder.getOperationResultValues(opResult);
    	for (ParameterInfo parameterInfo : operationResultValues) {
    		
    		String elementName = parameterInfo.getName();
    		AbsObject elementValue = parameterInfo.getValue();
    		ObjectSchema elementSchema = parameterInfo.getSchema();

    		// Create soap element
    		log.debug("Add element type: "+elementSchema.getTypeName());
            convertObjectToSoapElement(responseSchema, elementSchema, elementValue, elementName, responseElement);
		}
	}
	
	private SOAPElement xmlToSOAPElement(String text) {
		try {
			InputStream is = new ByteArrayInputStream(text.getBytes());
			org.w3c.dom.Document doc = XMLUtils.newDocument(is);
			MessageFactory factory = MessageFactory.newInstance();
			SOAPMessage message = factory.createMessage();
			return message.getSOAPBody().addDocument(doc); 
		} catch (Exception e) {
			// Throw exception if the text is not a valid xml
			return null; 
		}
	} 
	
	private SOAPElement convertObjectToSoapElement(ObjectSchema containerSchema, ObjectSchema resultSchema, AbsObject resultAbsObj, String elementName, SOAPElement rootSoapElement) throws Exception {
		
		SOAPElement soapElement = null;
		if (resultAbsObj != null) {
			String soapType = null;
			
			if (resultSchema instanceof PrimitiveSchema || resultSchema.getClass()==TermSchema.class) {
				
				// PrimitiveSchema
				log.debug("Elaborate primitive schema: "+elementName+" of type: "+resultSchema.getTypeName());
	
				// Get type and create soap element
		        soapType = WSDLUtils.getPrimitiveType(resultSchema, containerSchema, elementName);
				soapElement = addSoapElement(rootSoapElement, elementName, WSDLConstants.XSD, soapType, "", false);
	
				AbsPrimitive primitiveAbsObj = (AbsPrimitive)resultAbsObj;
				
		        // Create a text node which contains the value of the object.
		        // If the value is a DATE Format it in ISO8601;
				// if the slot is a TermSchema try to manage the value as a xml;
		        // for every other kind of object, just call toString.
		        if (BasicOntology.DATE.equals(primitiveAbsObj.getTypeName())) {
		        	soapElement.addTextNode(WSIGConstants.ISO8601_DATE_FORMAT.format(primitiveAbsObj.getDate()));
		        } else if (resultSchema.getClass() == TermSchema.class) {
		        	String text = primitiveAbsObj.toString();
		        	SOAPElement element = xmlToSOAPElement(text);
		        	if (element != null) {
		        		soapElement.addChildElement(element);
		        	} else {
			        	soapElement.addTextNode(text);
		        	}
		        } else {
		        	soapElement.addTextNode(primitiveAbsObj.toString());
		        }
			} else if (resultSchema instanceof ConceptSchema) {
				
				// ConceptSchema
				boolean forceType = false;
				if (hierarchicalComplexType) {
					// Get schema of resultAbsObj (to manage inheritance)
					String parameterType = resultAbsObj.getTypeName();
					if (!parameterType.equalsIgnoreCase(resultSchema.getTypeName())) {
						ObjectSchema soapSchema = onto.getSchema(parameterType);
						if (soapSchema == null || !resultSchema.isAssignableFrom(soapSchema)) {
							throw new Exception("Schema "+soapSchema.getTypeName()+" not assignable from "+resultSchema.getTypeName());
						}
						
						resultSchema = (TermSchema)soapSchema;
						forceType = true;
					}
				}
				log.debug("Elaborate concept schema: "+elementName+" of type: "+resultSchema.getTypeName());

				// Get type and create soap element
		        soapType = resultSchema.getTypeName();
				soapElement = addSoapElement(rootSoapElement, elementName, localNamespacePrefix, soapType, "", forceType);
				
				Class parameterClass = onto.getClassForElement(resultSchema.getTypeName());
				if (parameterClass != null && parameterClass.isEnum()) {
					// Enum type

					// Get enum-object value and set into soap message 
					AbsPrimitive enumAbsObject = (AbsPrimitive)resultAbsObj.getAbsObject(WSDLConstants.ENUM_SLOT_NAME);
					soapElement.addTextNode(enumAbsObject.toString());
				}
				else {
					// Elaborate all sub-schema of current complex schema 
					for (String conceptSlotName : resultSchema.getNames()) {
						ObjectSchema slotSchema = resultSchema.getSchema(conceptSlotName);
					
						// Get sub-object value 
						AbsTerm subAbsObject = (AbsTerm)resultAbsObj.getAbsObject(conceptSlotName);
						
						// Do recursive call
						convertObjectToSoapElement(resultSchema, slotSchema, subAbsObject, conceptSlotName, soapElement);
					}
				}
			} else if (resultSchema instanceof AggregateSchema) {
				
				// AggregateSchema
				log.debug("Elaborate aggregate schema: "+elementName);
	
				// Get aggregate type and cardinality
				ObjectSchema aggrSchema = WSDLUtils.getAggregateElementSchema(containerSchema, elementName);
				Integer cardMin = WSDLUtils.getAggregateCardMin(containerSchema, elementName);
				Integer cardMax = WSDLUtils.getAggregateCardMax(containerSchema, elementName);
				
				// Get slot type
				soapType = aggrSchema.getTypeName();
				if (aggrSchema instanceof PrimitiveSchema) {
					soapType = WSDLUtils.getPrimitiveType(aggrSchema, containerSchema, elementName);
				}
				String itemName = soapType;
				String aggrType = resultSchema.getTypeName();

				soapType = WSDLUtils.getAggregateType(soapType, cardMin, cardMax);
				
				// Create element
				soapElement = addSoapElement(rootSoapElement, elementName, localNamespacePrefix, soapType, "", false);
				
				// Elaborate all item of current aggregate schema 
				AbsAggregate aggregateAbsObj = (AbsAggregate)resultAbsObj;
				if (aggregateAbsObj != null) {
					for (int i=0; i<aggregateAbsObj.size(); i++) {
						
						//Get object value of index i
						AbsTerm itemObject = aggregateAbsObj.get(i);
		
						// Do recursive call
						convertObjectToSoapElement(resultSchema, aggrSchema, itemObject, itemName, soapElement);
					}
				}
			}
		}
		
		return soapElement;
	}

	private SOAPElement addSoapElement(SOAPElement rootSoapElement, String elementName, String prefix, String soapType, String tns, boolean forceType) throws Exception {

		// Create Name and Element
		String elementPrefix = "";
		if (WSDLConstants.STYLE_RPC.equals(soapStyle) && rootSoapElement instanceof SOAPBody) {
			elementPrefix = prefix;
		}
		Name soapName = soapEnvelope.createName(elementName, elementPrefix, tns);
	    SOAPElement soapElement = rootSoapElement.addChildElement(soapName);
	    
	    // Add encoding style only in result tag and for style rpc
        if (WSDLConstants.STYLE_RPC.equals(soapStyle) && rootSoapElement instanceof SOAPBody) {
        	soapElement.setEncodingStyle(WSDLConstants.ENCODING_URL);
        }

	    // Add type to element
	    if ((WSDLConstants.STYLE_RPC.equals(soapStyle) || forceType) && prefix != null && soapType != null) {
		    Name typeName = soapEnvelope.createName(WSDLConstants.TYPE, WSDLConstants.XSI, WSDLConstants.XSI_URL);
		    soapElement.addAttribute(typeName, prefix+":"+soapType);
	    }
	    
	    return soapElement;
	}
}
