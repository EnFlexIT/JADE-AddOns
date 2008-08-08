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

package com.tilab.wsig.wsdl;

import jade.content.ContentManager;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.Facet;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.facets.CardinalityFacet;
import jade.content.schema.facets.TypedAggregateFacet;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;

import org.apache.log4j.Logger;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDSchema;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.store.ActionBuilder;
import com.tilab.wsig.store.MapperBasedActionBuilder;
import com.tilab.wsig.store.OperationName;
import com.tilab.wsig.store.OntologyBasedActionBuilder;
import com.tilab.wsig.store.SuppressOperation;
import com.tilab.wsig.store.WSIGService;

public class SDToWSDL {
	
	private static Logger log = Logger.getLogger(SDToWSDL.class.getName());

	public static Definition createWSDLFromSD(Agent agent, ServiceDescription sd, WSIGService wsigService) throws Exception {

		// Create mapper object
		Object mapperObject = null;
		Class mapperClass = wsigService.getMapperClass();
		if (mapperClass != null) {
			try {
				mapperObject = mapperClass.newInstance();
			} catch (Exception e) {
				log.error("Mapper class "+mapperClass.getName()+" not found", e);
				throw e;
			}
		}
		
		// Get soap style & use
		String soapStyle = WSIGConfiguration.getInstance().getWsdlStyle();
		String soapUse;
		if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {
			soapUse = WSDLConstants.USE_ENCODED;
		} else {
			soapUse = WSDLConstants.USE_LITERAL;
		}
		
		// Create wsdl definition and type schema
		WSDLFactory factory = WSDLFactory.newInstance();
		String tns = "urn:" + wsigService.getServicePrefix() + sd.getName();
		
		Definition definition = WSDLGeneratorUtils.createWSDLDefinition(factory, tns);
		XSDSchema wsdlTypeSchema = WSDLGeneratorUtils.createSchema(tns);

		// Create Extension Registry
		ExtensionRegistry registry = null;
		registry = factory.newPopulatedExtensionRegistry();
		definition.setExtensionRegistry(registry);

		// Create Port Type
		PortType portType = WSDLGeneratorUtils.createPortType(tns);
		definition.addPortType(portType);

		// Create Binding
		Binding binding = WSDLGeneratorUtils.createBinding(tns);
		try {
			binding.addExtensibilityElement(WSDLGeneratorUtils.createSOAPBinding(registry, soapStyle));
		} catch (WSDLException e) {
			throw new Exception("Error in SOAPBinding Handling", e);
		}
		definition.addBinding(binding);

		Port port = WSDLGeneratorUtils.createPort(tns);
		try {
			port.addExtensibilityElement(WSDLGeneratorUtils.createSOAPAddress(registry));
		} catch (WSDLException e) {
			throw new Exception("Error in SOAPAddress Handling", e);
		}

		// Create Service
		Service service = WSDLGeneratorUtils.createService(tns);
		service.addPort(port);
		definition.addService(service);

		// Manage Ontologies
		Iterator ontologies = sd.getAllOntologies();
		ContentManager cntManager = agent.getContentManager();
		
		// TODO Manage only FIRST ontology for ServiceDescription   
		if (ontologies.hasNext()) {
			String ontoName = (String) ontologies.next();
			log.debug("Elaborate ontology: "+ontoName);		
			
			Ontology onto = cntManager.lookupOntology(ontoName);
			java.util.List actionNames = onto.getActionNames();

			// Manage Actions
			for (int i = 0; i < actionNames.size(); i++) {
				try {
					String actionName = (String) actionNames.get(i);
					log.debug("Elaborate operation: "+ actionName);		

					// Check if action is suppressed (only for mapper)
					if (mapperClass != null && isActionSuppressed(mapperClass, actionName)) {
						log.debug("--operation "+actionName+" suppressed");
						continue;
					}
					
					// Sub operation (operation with same name, >1 only for mapper) 
					int subOperationNumber = 1;
					boolean operationDefinitedInMapper = false;
					
					// Get action methods of mapper
					Vector<Method> mapperMethodsForAction = getMapperMethodsForAction(mapperClass, actionName);
					if (mapperMethodsForAction.size() > 0) {
						subOperationNumber = mapperMethodsForAction.size();
						operationDefinitedInMapper = true;
					}
					
					// Loop all sub operations
					for (int j = 0; j < subOperationNumber; j++) {
						
						// Prepare operation name (add index if there are more operation with same name)
						String operationName = wsigService.getServicePrefix()+actionName;
						if (subOperationNumber > 1) {
							operationName = operationName + WSDLConstants.SEPARATOR + j;
						}

						// Create appropriate ActionBuilder
						ActionBuilder actionBuilder = null;
						if (operationDefinitedInMapper) {
							// Mapper
							Method method = mapperMethodsForAction.get(j);
							
							// Check is the operation has a specific name
							OperationName annotationOperationName = method.getAnnotation(OperationName.class);
							if (annotationOperationName != null) {

								// Set specific operation name
								operationName = annotationOperationName.name();
							}
							
							actionBuilder = new MapperBasedActionBuilder(mapperObject, method, onto, actionName);
						} else {
							// Ontology
							actionBuilder = new OntologyBasedActionBuilder(onto, actionName);
						}

						// Add ActionBuilder to wsigService 
						wsigService.addActionBuilder(operationName, actionBuilder);

						// Operation
						Operation operation = WSDLGeneratorUtils.createOperation(operationName);
						portType.addOperation(operation);

						// Operation binding
						BindingOperation operationBinding = WSDLGeneratorUtils.createBindingOperation(registry, tns, operationName);
						binding.addBindingOperation(operationBinding);
						
						// Input parameters
						Message inputMessage = WSDLGeneratorUtils.createMessage(tns, WSDLGeneratorUtils.getRequestName(operationName));
						Input input = WSDLGeneratorUtils.createInput(inputMessage);
						operation.setInput(input);
						definition.addMessage(inputMessage);

						BindingInput inputBinding = WSDLGeneratorUtils.createBindingInput(registry, tns, soapUse);
						operationBinding.setBindingInput(inputBinding);
						
						if (operationDefinitedInMapper) {
							// Mapper
							Method mapperMethod = mapperMethodsForAction.get(j);
							manageInputParameters(tns, operationName, soapStyle, wsdlTypeSchema, inputMessage, actionName, onto, mapperMethod);

						} else {
							// Ontology
							manageInputParameters(tns, operationName, soapStyle, wsdlTypeSchema, inputMessage, actionName, onto, null);
						}
						
						// Output parameters		
						Message outputMessage = WSDLGeneratorUtils.createMessage(tns, WSDLGeneratorUtils.getResponseName(operationName));
						Output output = WSDLGeneratorUtils.createOutput(outputMessage);
						operation.setOutput(output);
						definition.addMessage(outputMessage);

						BindingOutput outputBinding = WSDLGeneratorUtils.createBindingOutput(registry, tns, soapUse);
						operationBinding.setBindingOutput(outputBinding);

						manageOutputParameter(tns, operationName, soapStyle, wsdlTypeSchema, outputMessage, actionName, onto);
						
					}
				} catch (Exception e) {
					throw new Exception("Error in Agent Action Handling", e);
				}
			}
			
			// Add complex type to wsdl definition
			try {
				definition.setTypes(WSDLGeneratorUtils.createTypes(registry, wsdlTypeSchema));
			} catch (WSDLException e) {
				throw new Exception("Error adding type to definition", e);
			}

			// Write wsdl file
			try {
				String filename = WSDLGeneratorUtils.getWSDLFilename(wsigService.getServicePrefix() + sd.getName());
				log.info("Write WSDL file: "+filename);

				WSDLGeneratorUtils.writeWSDL(factory, definition, filename);
				
			} catch (Exception e) {
				log.error("Error in WSDL file writing", e);
			}
		}
		
		return definition;
	}
	
	private static void manageInputParameters(String tns, String operationName, String soapStyle, XSDSchema wsdlTypeSchema, Message inputMessage, String actionName, Ontology onto, Method mapperMethod) throws Exception {

		AgentActionSchema actionSchema = (AgentActionSchema) onto.getSchema(actionName);
		String[] slotNames = actionSchema.getNames();

		// In document style there is only a message part named parameters and an element in types definition
		XSDModelGroup elementSequence = null;
		if (slotNames.length > 0 && WSDLConstants.STYLE_DOCUMENT.equals(soapStyle)) {
			Part partMessage = WSDLGeneratorUtils.createElementPart(WSDLConstants.PARAMETERS, operationName, tns);
			inputMessage.addPart(partMessage);

			XSDElementDeclaration element = WSDLGeneratorUtils.addElementToSchema(tns, wsdlTypeSchema, operationName);
			XSDComplexTypeDefinition complexType = WSDLGeneratorUtils.addComplexTypeToElement(element);
			elementSequence = WSDLGeneratorUtils.addSequenceToComplexType(complexType);
		}

		if (mapperMethod != null) {
			// Mapper
			manageMapperInputParameters(tns, soapStyle, wsdlTypeSchema, elementSequence, inputMessage, actionSchema, onto, mapperMethod);
			
		} else {
			// Ontology
			manageOntologyInputParameters(tns, soapStyle, wsdlTypeSchema, elementSequence, inputMessage, actionSchema);
		}
	}
	
	private static void manageOntologyInputParameters(String tns, String soapStyle, XSDSchema wsdlTypeSchema, XSDModelGroup elementSequence, Message inputMessage, AgentActionSchema actionSchema) throws Exception {

		String[] slotNames = actionSchema.getNames();

		// Loop for all slot of action schema
		for (String slotName : slotNames) {
			ObjectSchema slotSchema = actionSchema.getSchema(slotName);
			String slotType = createComplexTypeFromSchema(tns, false, actionSchema, slotSchema, wsdlTypeSchema, slotName, null, null, null);
			log.debug("--ontology input slot: "+slotName+" ("+slotType+")");
			
			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {

				// Add a part message for all parameters
				Part partMessage = WSDLGeneratorUtils.createTypePart(slotName, slotType, tns);
				inputMessage.addPart(partMessage);
			} else {
				
				// Add a element in complex type definition for all parameters
				Integer cardMin = actionSchema.isMandatory(slotName) ? null : 0;
				WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, elementSequence, cardMin, null);
			}
		}
	}
	
	private static void manageMapperInputParameters(String tns, String soapStyle, XSDSchema wsdlTypeSchema, XSDModelGroup elementSequence, Message inputMessage, AgentActionSchema actionSchema, Ontology onto, Method mapperMethod) throws Exception {
		
		Class[] parameterTypes = mapperMethod.getParameterTypes();
		String[] parameterNames = WSDLGeneratorUtils.getParameterNames(mapperMethod);
		
		// Loop for all parameters of mapper method
		for (int k = 0; k < parameterTypes.length; k++) {
			Class parameterClass = parameterTypes[k];
			String parameterName;
			if (parameterNames != null) {
				parameterName = parameterNames[k];
			} else {
				parameterName = parameterClass.getSimpleName() + WSDLConstants.SEPARATOR + k;
			}
			String parameterType = createComplexTypeFromClass(tns, onto, actionSchema, parameterClass, wsdlTypeSchema, parameterName, null);
			log.debug("--mapper input parameter: "+parameterName+" ("+parameterType+")");

			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {

				// Add a part message for all parameters
				Part partMessage = WSDLGeneratorUtils.createTypePart(parameterName, parameterType, tns);
				inputMessage.addPart(partMessage);
			} else {
				
				// Add a element in complex type definition for all parameters
				WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, parameterName, parameterType, elementSequence);
			}
		}
	}
	
	private static void manageOutputParameter(String tns, String operationName, String soapStyle, XSDSchema wsdlTypeSchema, Message outputMessage, String actionName, Ontology onto) throws Exception {
		
		AgentActionSchema actionSchema = (AgentActionSchema) onto.getSchema(actionName);
		ObjectSchema resultSchema = actionSchema.getResultSchema();
		
		if (resultSchema != null) {
			String resultName = WSDLGeneratorUtils.getResultName(operationName);
			String resultType = createComplexTypeFromSchema(tns, true, actionSchema, resultSchema, wsdlTypeSchema, resultSchema.getTypeName(), null, null, null);
			log.debug("--ontology output result: "+resultName+" ("+resultType+")");

			Part partMessage;
			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {
				partMessage = WSDLGeneratorUtils.createTypePart(resultName, resultType, tns);
				
			} else {
				String responseName = WSDLGeneratorUtils.getResponseName(operationName); 
				partMessage = WSDLGeneratorUtils.createElementPart(WSDLConstants.PARAMETERS, responseName, tns);
				
				// Add element to type schema
				String elementName = WSDLGeneratorUtils.getResponseName(operationName);
				XSDElementDeclaration element = WSDLGeneratorUtils.addElementToSchema(tns, wsdlTypeSchema, elementName);
				XSDComplexTypeDefinition complexType = WSDLGeneratorUtils.addComplexTypeToElement(element);
				XSDModelGroup sequence = WSDLGeneratorUtils.addSequenceToComplexType(complexType);

				WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, resultName, resultType, sequence);
			}

			outputMessage.addPart(partMessage);
		}
	}

	private static Vector<Method> getMapperMethodsForAction(Class mapperClass, String actionName) {

		Vector<Method> methodsAction = new Vector<Method>();

		if (mapperClass != null) {
			Method[] methods = mapperClass.getDeclaredMethods();
		
			Method method = null;
			String methodNameToCheck = WSDLConstants.MAPPER_METHOD_PREFIX + actionName;
			for (int j = 0; j < methods.length; j++) {
				method = methods[j];
				if (method.getName().equalsIgnoreCase(methodNameToCheck)) {
					methodsAction.add(method);
				} 
			}
		}
		return methodsAction;
	}

	private static boolean isActionSuppressed(Class mapperClass, String actionName) {

		boolean isSuppressed = false;
		if (mapperClass != null) {
			Method[] methods = mapperClass.getDeclaredMethods();
		
			Method method = null;
			String methodNameToCheck = WSDLConstants.MAPPER_METHOD_PREFIX + actionName;
			for (int j = 0; j < methods.length; j++) {
				method = methods[j];
				
				SuppressOperation annotationSuppressOperation = method.getAnnotation(SuppressOperation.class);
				if ((method.getName().equalsIgnoreCase(methodNameToCheck) && annotationSuppressOperation != null)) {
					isSuppressed = true;
					break;
				} 
			}
		}
		return isSuppressed;
	}
	
	private static String createComplexTypeFromClass(String tns, Ontology onto, ConceptSchema containerSchema, Class paramType, XSDSchema wsdlTypeSchema, String paramName, XSDComponent parentComponent) throws Exception {
		
		String slotType = null;
		if (paramType.isPrimitive() || WSDLConstants.java2xsd.get(paramType) != null) {

			// Primitive java-type or xsd-type
			if (paramType.isPrimitive()) {
				slotType = paramType.getName();
			} else {
				slotType = (String) WSDLConstants.java2xsd.get(paramType);
			}
			if (parentComponent != null) {
				log.debug("------add primitive-type "+paramName+" ("+slotType+")");
				WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, paramName, slotType, (XSDModelGroup) parentComponent, null, null);
			}

		} 
		else if (paramType.isArray()) {

			// Java array
			Class aggrType = paramType.getComponentType();
			paramName = aggrType.getSimpleName().toLowerCase();
			slotType = WSDLGeneratorUtils.getAggregateType(paramName, BasicOntology.SEQUENCE);
			if (WSDLGeneratorUtils.getComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				log.debug("----create array-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLGeneratorUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLGeneratorUtils.addSequenceToComplexType(complexType);
				createComplexTypeFromClass(tns, onto, containerSchema, aggrType, wsdlTypeSchema, paramName, sequence);
			}
		} 
		else if (	Collection.class.isAssignableFrom(paramType) ||
					jade.util.leap.Collection.class.isAssignableFrom(paramType)) {
			// TODO Java collection
			// Manage collection element type with a specif annotation associated to mapper method
			// es. @CollectionElementType (parameter=xxx, type=java.util.String)
			throw new Exception("Collection NOT supported");

		} else {
			
			// Java custom type (work with concept schema of type paramType)
			// Search a schema of this type
			String[] conceptSchemaNames = containerSchema.getNames();
			String conceptSchemaName = null;
			for (String name : conceptSchemaNames) {
				if (paramType.equals(onto.getClassForElement(name))) {
					conceptSchemaName = name;
					break;
				}
			}
			if (conceptSchemaName == null) {
				throw new Exception("ConceptSchema of type "+paramType.getSimpleName()+" doesn't exist in "+ onto.getName());
			}
			ObjectSchema conceptSchema = containerSchema.getSchema(conceptSchemaName);
			slotType = createComplexTypeFromSchema(tns, false, containerSchema, conceptSchema, wsdlTypeSchema, conceptSchemaName, parentComponent, null, null);
		}
		
		return slotType;
	}

	private static String createComplexTypeFromSchema(String tns, boolean firstLevelResult, ConceptSchema containerSchema, ObjectSchema objSchema, XSDSchema wsdlTypeSchema, String slotName, XSDComponent parentComponent, Integer cardMin, Integer cardMax) throws Exception {
		
		String slotType = null;
		if (objSchema instanceof PrimitiveSchema) {
			
			// Get type from PrimitiveSchema
			slotType = WSDLConstants.jade2xsd.get(objSchema.getTypeName());
			if (parentComponent != null) {
				if (cardMin == null && !containerSchema.isMandatory(slotName)) {
					cardMin = new Integer(0);
				}
				log.debug("------add primitive-type "+slotName+" ("+slotType+") "+((cardMin!=null && cardMin==0)?"OPTIONAL":""));
				WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}

		} 
		else if (objSchema instanceof ConceptSchema) {
			
			// Get type from ConceptSchema (if not found in wsdlTypeSchema create it)
			slotType = objSchema.getTypeName();
			if (parentComponent != null) {
				if (cardMin == null && !containerSchema.isMandatory(slotName)) {
					cardMin = new Integer(0);
				}
				log.debug("------add complex-type "+slotName+" ("+slotType+") "+((cardMin!=null && cardMin==0)?"OPTIONAL":""));
				WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
			
			if (WSDLGeneratorUtils.getComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				log.debug("----create complex-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLGeneratorUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLGeneratorUtils.addSequenceToComplexType(complexType);
				for (String conceptSlotName : objSchema.getNames()) {
					ObjectSchema slotSchema = objSchema.getSchema(conceptSlotName);
					createComplexTypeFromSchema(tns, false, (ConceptSchema) objSchema, slotSchema, wsdlTypeSchema, conceptSlotName, sequence, null, null);
				}
			}
		} 
		else if (objSchema instanceof AggregateSchema) {

			// Get type from AggregateSchema (if array type not present in wsdlTypeSchema create it)
			// Get cardinality and aggregate type
			Facet[] facets;
			if (firstLevelResult) {
				// output first level
				facets = ((AgentActionSchema)containerSchema).getResultFacets();
			} else {
				// input or output complex type 
				facets = containerSchema.getFacets(slotName);
			}
			ObjectSchema aggregateSchema = null;
			for (Facet facet : facets) {
				if (facet instanceof CardinalityFacet) {
					cardMax = ((CardinalityFacet) facet).getCardMax();
					cardMin = ((CardinalityFacet) facet).getCardMin();
				} else if (facet instanceof TypedAggregateFacet) {
					aggregateSchema = ((TypedAggregateFacet) facet).getType();
				}
			}
			// Get array type 
			slotType = aggregateSchema.getTypeName();
			if (aggregateSchema instanceof PrimitiveSchema) {
				slotType = WSDLConstants.jade2xsd.get(slotType);
			}
			String itemName = slotType;
			String aggregateType = objSchema.getTypeName();
			slotType = WSDLGeneratorUtils.getAggregateType(slotType, aggregateType);
			
			if (WSDLGeneratorUtils.getComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				log.debug("----create array-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLGeneratorUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLGeneratorUtils.addSequenceToComplexType(complexType);
				if (parentComponent != null) {
					log.debug("------add array-type "+slotName+" ("+slotType+") ["+cardMin+","+cardMax+"]");
					WSDLGeneratorUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent);
				}
				createComplexTypeFromSchema(tns, false, containerSchema, aggregateSchema, wsdlTypeSchema, itemName, sequence, cardMin, cardMax);
			}
		}
		
		return slotType;
	}
}