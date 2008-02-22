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

import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.Facet;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.facets.TypedAggregateFacet;
import jade.util.leap.ArrayList;

import java.lang.reflect.Field;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.log4j.Logger;

import com.tilab.wsig.store.ActionBuilder;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.wsdl.WSDLConstants;
import com.tilab.wsig.wsdl.WSDLGeneratorUtils;

public class JadeToSoap {

	private static Logger log = Logger.getLogger(JadeToSoap.class.getName());
	
	private static final String PREFIX_Q0 = "q0";
	private static final String PREFIX_Q1 = "q1";
	
	private Ontology onto;
	private SOAPEnvelope envelope;
	private String tns;
	
	public JadeToSoap() {
	}

	/**
	 * convert
	 * @param resultObject
	 * @param wsigService
	 * @param operationName
	 * @return
	 * @throws Exception
	 */
	public SOAPMessage convert(Object resultObject, WSIGService wsigService, String operationName) throws Exception {
	
		// Get tns
		tns = "urn:" + wsigService.getServicePrefix() + wsigService.getServiceName();
			
		// Get ontology
		onto = wsigService.getOnto();
		
		// Create soap message
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapResponse = messageFactory.createMessage();
        
        // Create objects for the message parts            
        SOAPPart soapPart = soapResponse.getSOAPPart();
        envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(PREFIX_Q0, WSDLConstants.xsd);
        envelope.addNamespaceDeclaration(PREFIX_Q1, tns);
        
        SOAPBody body = envelope.getBody();

        String responseElementName = operationName + "Response";
        SOAPElement responseElement = addSoapElement(body, responseElementName, null, null);

        // Get action builder
        log.debug("Operation name: "+operationName);
        ActionBuilder actionBuilder = wsigService.getActionBuilder(operationName);
        if (actionBuilder == null) {
			throw new Exception("Action builder not found for operation "+operationName+" in WSIG");
        }
        
        // Get action schema
        AgentActionSchema actionSchema;
        try {
	        String ontoActionName = actionBuilder.getOntoActionName();
	        actionSchema = (AgentActionSchema)onto.getSchema(ontoActionName);
        } catch (OntologyException oe) {
        	throw new Exception("Operation schema not found for operation "+operationName+" in "+onto.getName()+" ontology", oe);
        }

		// Get result schema
        ObjectSchema resultSchema = actionSchema.getResultSchema();
        log.debug("Ontology result type: "+resultSchema.getTypeName());
        
        // Create soap message
        convertObjectToSoapElement(actionSchema, resultSchema, resultObject, WSDLGeneratorUtils.getResultName(operationName), responseElement);

        // Save all modifies of soap message
        soapResponse.saveChanges();
        
		return soapResponse;
	}

	/**
	 * convertObjectToSoapElement
	 * @param envelope
	 * @param resultSchema
	 * @param resultObj
	 * @param elementName
	 * @param rootSoapElement
	 * @return
	 * @throws Exception
	 */
	private SOAPElement convertObjectToSoapElement(ObjectSchema containerSchema, ObjectSchema resultSchema, Object resultObj, String elementName, SOAPElement rootSoapElement) throws Exception {
		
		SOAPElement soapElement = null;
		String soapType = null;
		ObjectSchema newContainerSchema = resultSchema;
		
		if (resultSchema instanceof PrimitiveSchema) {
			
			// PrimitiveSchema
			log.debug("Elaborate primitive schema: "+elementName+" of type: "+resultSchema.getTypeName());

			// Get type and cretae soap element
	        soapType = (String) WSDLGeneratorUtils.types.get(resultSchema.getTypeName());
			soapElement = addSoapElement(rootSoapElement, elementName, WSDLConstants.xsd, soapType);

			// Add value
	        soapElement.addTextNode(resultObj.toString());
			
		} else if (resultSchema instanceof ConceptSchema) {
			
			// ConceptSchema
			log.debug("Elaborate concept schema: "+elementName+" of type: "+resultSchema.getTypeName());

			// Get type and create soap element
	        soapType = resultSchema.getTypeName();
			soapElement = addSoapElement(rootSoapElement, elementName, tns, soapType);
			
			// Elaborate all sub-schema of current complex schema 
			for (String conceptSlotName : resultSchema.getNames()) {
				ObjectSchema slotSchema = resultSchema.getSchema(conceptSlotName);
			
				// Get sub-object value 
				Object subObject = getFieldValue(resultObj, conceptSlotName);
				
				// Do ricorsive call
				convertObjectToSoapElement(newContainerSchema, slotSchema, subObject, conceptSlotName, soapElement);
			}
		} else if (resultSchema instanceof AggregateSchema) {
			
			// AggregateSchema
			log.debug("Elaborate aggregate schema: "+elementName);

			// Get facets 
			Facet[] facets;
			if (containerSchema instanceof AgentActionSchema) {
				// first level
				facets = ((AgentActionSchema)containerSchema).getResultFacets();
			} else {
				// next level 
				facets = containerSchema.getFacets(elementName);
			}
			
			// Get aggregate type
			ObjectSchema aggrSchema = null;
			for (Facet facet : facets) {
				if (facet instanceof TypedAggregateFacet) {
					aggrSchema = ((TypedAggregateFacet) facet).getType();
					break;
				}
			}
			
			// Get slot type
			soapType = aggrSchema.getTypeName();
			if (aggrSchema instanceof PrimitiveSchema) {
				soapType = WSDLGeneratorUtils.types.get(soapType);
			}
			String itemName = soapType;
			soapType = WSDLGeneratorUtils.getArrayType(soapType);
			
			// Create element
			soapElement = addSoapElement(rootSoapElement, elementName, tns, soapType);
			
			// Elaborate all item of current aggregate schema 
			ArrayList array = (ArrayList)resultObj;
			for (int i=0; i<array.size(); i++) {
				
				//Get object value of index i
				Object itemObject = array.get(i);

				// Do ricorsive call
				convertObjectToSoapElement(newContainerSchema, aggrSchema, itemObject, itemName, soapElement);
			}
		}
					
		return soapElement;
	}


	/**
	 * addSoapElement
	 * @param rootSoapElement
	 * @param elementName
	 * @param uri
	 * @param soapType
	 * @return
	 * @throws Exception
	 */
	private SOAPElement addSoapElement(SOAPElement rootSoapElement, String elementName, String uri, String soapType) throws Exception {
		
		String prefix = PREFIX_Q1;
		if (uri != null && uri.equals(WSDLConstants.xsd)) {
			prefix = PREFIX_Q0;
		}
			
		// Create Name and Element
	    Name soapName = envelope.createName(elementName, "", "");
	    SOAPElement soapElement = rootSoapElement.addChildElement(soapName);
	    
	    // Add encoding style only to first element
	    if (rootSoapElement instanceof SOAPBody) {
	    	soapElement.setEncodingStyle(WSDLConstants.encodingStyle);
	    }
	    
	    // Add type
	    if (soapType != null) {
		    Name typeName = envelope.createName("type", "xsi", WSDLConstants.xsi);
		    soapElement.addAttribute(typeName, prefix+":"+soapType);
	    }
	    
	    return soapElement;
	}
	
	
	/**
	 * getFieldValue
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	private Object getFieldValue(Object obj, String fieldName) {
		
		Object fieldValue = null;
		try {
			// Get class
			Class clazz = obj.getClass();
			
			// Get fields of object
			Field field = clazz.getDeclaredField(fieldName);
			
			// Set reflection accessiable method
			field.setAccessible(true);
				
			// Get field value
			fieldValue = field.get(obj);
			
		} catch(Exception e) {
			log.error("Error accessing to field "+fieldName+" in object "+obj.getClass().getCanonicalName(), e);
		}

		return fieldValue;
	}
	
}
