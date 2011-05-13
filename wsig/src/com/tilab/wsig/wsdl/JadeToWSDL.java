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
import jade.content.onto.OntologyException;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;

import org.apache.log4j.Logger;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.store.ActionBuilder;
import com.tilab.wsig.store.MapperBasedActionBuilder;
import com.tilab.wsig.store.OntologyBasedActionBuilder;
import com.tilab.wsig.store.OperationName;
import com.tilab.wsig.store.SuppressOperation;
import com.tilab.wsig.store.TypedAggregateSchema;
import com.tilab.wsig.store.WSIGService;

public class JadeToWSDL {
	
	private static Logger log = Logger.getLogger(JadeToWSDL.class.getName());
	
	private static Integer MANDATORY = null;
	private static Integer OPTIONAL = Integer.valueOf(0);
	
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
		String serviceName = wsigService.getServicePrefix() + sd.getName();
		WSDLFactory factory = WSDLFactory.newInstance();
		String tns = WSDLConstants.URN+":" + serviceName;
		
		Definition definition = WSDLUtils.createWSDLDefinition(factory, tns);
		XSDSchema wsdlTypeSchema = WSDLUtils.createSchema(tns);

		// Create Extension Registry
		ExtensionRegistry registry = null;
		registry = factory.newPopulatedExtensionRegistry();
		definition.setExtensionRegistry(registry);

		// Create Port Type
		PortType portType = WSDLUtils.createPortType(tns);
		definition.addPortType(portType);

		// Create Binding
		Binding binding = WSDLUtils.createBinding(tns);
		try {
			binding.addExtensibilityElement(WSDLUtils.createSOAPBinding(registry, soapStyle));
		} catch (WSDLException e) {
			throw new Exception("Error in SOAPBinding Handling", e);
		}
		definition.addBinding(binding);

		Port port = WSDLUtils.createPort(tns);
		try {
			SOAPAddress soapAddress = WSDLUtils.createSOAPAddress(registry, serviceName);
			wsigService.setSOAPAddress(soapAddress);
			port.addExtensibilityElement(soapAddress);
		} catch (Exception e) {
			throw new Exception("Error in SOAPAddress Handling", e);
		}

		// Create Service
		Service service = WSDLUtils.createService(tns);
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
						
						// Prepare default operation name
						String operationName = actionName;

						// Create appropriate ActionBuilder
						ActionBuilder actionBuilder = null;
						if (operationDefinitedInMapper) {
							
							// Mapper
							Method method = mapperMethodsForAction.get(j);

							// Check is the operation has a specific name
							OperationName annotationOperationName = method.getAnnotation(OperationName.class);
							if (annotationOperationName != null) {

								// Set specific operation name from annotation
								operationName = annotationOperationName.name();
							} else {
								
								// No specific name, check operation overloading
								if (subOperationNumber > 1) {
									
									// Use parameter class type to define operation name
									Class[] parameterTypes = method.getParameterTypes();
									StringBuffer parameterStrings = new StringBuffer();
									for (int paramIndex=0; paramIndex<parameterTypes.length; paramIndex++) {
										parameterStrings.append(parameterTypes[paramIndex].getSimpleName());
										if (paramIndex<(parameterTypes.length-1)) {
											parameterStrings.append(WSDLConstants.SEPARATOR);
										}
									}
									
									operationName = operationName+WSDLConstants.SEPARATOR+parameterStrings.toString(); 
								}
							}
							
							// Create MapperBased builder
							actionBuilder = new MapperBasedActionBuilder(mapperObject, method, onto, actionName);
						} else {
							
							// Create OntologyBased builder
							actionBuilder = new OntologyBasedActionBuilder(onto, actionName);
						}

						// Add ActionBuilder to wsigService 
						wsigService.addActionBuilder(operationName, actionBuilder);

						// Operation
						Operation operation = WSDLUtils.createOperation(operationName);
						portType.addOperation(operation);

						// Operation binding
						BindingOperation operationBinding = WSDLUtils.createBindingOperation(registry, tns, operationName);
						binding.addBindingOperation(operationBinding);
						
						// Input parameters
						Message inputMessage = WSDLUtils.createMessage(tns, WSDLUtils.getRequestName(operationName));
						Input input = WSDLUtils.createInput(inputMessage);
						operation.setInput(input);
						definition.addMessage(inputMessage);

						BindingInput inputBinding = WSDLUtils.createBindingInput(registry, tns, soapUse);
						operationBinding.setBindingInput(inputBinding);
						
						Map<String, ObjectSchema> inputParametersMap;
						if (operationDefinitedInMapper) {
							// Mapper
							Method mapperMethod = mapperMethodsForAction.get(j);
							inputParametersMap = manageInputParameters(onto, tns, operationName, soapStyle, wsdlTypeSchema, inputMessage, actionName, mapperMethod);

						} else {
							// Ontology
							inputParametersMap = manageInputParameters(onto, tns, operationName, soapStyle, wsdlTypeSchema, inputMessage, actionName, null);
						}
						
						// Set input parameters map to action builder
						actionBuilder.setParametersMap(inputParametersMap);
						
						// Output parameters		
						Message outputMessage = WSDLUtils.createMessage(tns, WSDLUtils.getResponseName(operationName));
						Output output = WSDLUtils.createOutput(outputMessage);
						operation.setOutput(output);
						definition.addMessage(outputMessage);

						BindingOutput outputBinding = WSDLUtils.createBindingOutput(registry, tns, soapUse);
						operationBinding.setBindingOutput(outputBinding);

						manageOutputParameter(onto, tns, operationName, soapStyle, wsdlTypeSchema, outputMessage, actionName);
						
					}
				} catch (Exception e) {
					throw new Exception("Error in Agent Action Handling", e);
				}
			}
			
			// Log ontology
			if (log.isDebugEnabled()) {
				onto.dump();
			}
			
			// Add complex type to wsdl definition
			try {
				definition.setTypes(WSDLUtils.createTypes(registry, wsdlTypeSchema));
			} catch (WSDLException e) {
				throw new Exception("Error adding type to definition", e);
			}

			// Set wsdl definition in wsigService
			wsigService.setWsdlDefinition(definition);
			
			// Write wsdl on file system
			if (WSIGConfiguration.getInstance().isWsdlWriteEnable()) {
				try {
					log.info("Write WSDL for service: "+serviceName);
					WSDLUtils.writeWSDL(factory, definition, serviceName);
					
				} catch (Exception e) {
					log.error("Error writing WSDL file", e);
				}
			}
		}
		
		return definition;
	}
	
	private static Map<String, ObjectSchema> manageInputParameters(Ontology onto, String tns, String operationName, String soapStyle, XSDSchema wsdlTypeSchema, Message inputMessage, String actionName, Method mapperMethod) throws Exception {

		AgentActionSchema actionSchema = (AgentActionSchema) onto.getSchema(actionName);

		// In document style there is only a message part named parameters and an element in types definition
		XSDModelGroup elementSequence = null;
		if (WSDLConstants.STYLE_DOCUMENT.equals(soapStyle)) {
			Part partMessage = WSDLUtils.createElementPart(WSDLConstants.PARAMETERS, operationName, tns);
			inputMessage.addPart(partMessage);

			XSDElementDeclaration element = WSDLUtils.addElementToSchema(tns, wsdlTypeSchema, operationName);
			XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToElement(element);
			elementSequence = WSDLUtils.addSequenceToComplexType(complexType);
		}

		Map<String, ObjectSchema> inputParametersMap;
		if (mapperMethod != null) {
			// Mapper
			inputParametersMap = manageMapperInputParameters(onto, tns, soapStyle, wsdlTypeSchema, elementSequence, inputMessage, actionSchema, mapperMethod);
			
		} else {
			// Ontology
			inputParametersMap = manageOntologyInputParameters(onto, tns, soapStyle, wsdlTypeSchema, elementSequence, inputMessage, actionSchema);
		}
		
		return inputParametersMap;
	}
	
	private static Map<String, ObjectSchema> manageOntologyInputParameters(Ontology onto, String tns, String soapStyle, XSDSchema wsdlTypeSchema, XSDModelGroup elementSequence, Message inputMessage, AgentActionSchema actionSchema) throws Exception {

		String[] slotNames = actionSchema.getNames();
		Map<String, ObjectSchema> inputParametersMap = new HashMap<String, ObjectSchema>();

		// Loop for all slot of action schema
		for (String slotName : slotNames) {
			ObjectSchema slotSchema = actionSchema.getSchema(slotName);
			String slotType = createComplexTypeFromSchema(onto, tns, actionSchema, slotSchema, wsdlTypeSchema, slotName, null, null, null);
			log.debug("--ontology input slot: "+slotName+" ("+slotType+")");

			// For aggregate create the relative TypedAggregateSchema
			if (slotSchema instanceof AggregateSchema) {
				slotSchema = WSDLUtils.getTypedAggregateSchema(actionSchema, slotName);
			}
			
			// Add parameter to map
			inputParametersMap.put(slotName, slotSchema);
			
			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {

				// Add a part message for all parameters
				Part partMessage = WSDLUtils.createTypePart(slotName, slotType, tns);
				inputMessage.addPart(partMessage);
			} else {
				
				// Add a element in complex type definition for all parameters
				Integer cardMin = actionSchema.isMandatory(slotName) ? MANDATORY : OPTIONAL;
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, elementSequence, cardMin, null);
			}
		}
		
		return inputParametersMap;
	}
	
	private static Map<String, ObjectSchema> manageMapperInputParameters(Ontology onto, String tns, String soapStyle, XSDSchema wsdlTypeSchema, XSDModelGroup elementSequence, Message inputMessage, AgentActionSchema actionSchema, Method mapperMethod) throws Exception {
		Annotation[][] parameterAnnotations = mapperMethod.getParameterAnnotations();
		Class[] parameterClasses = mapperMethod.getParameterTypes();
		String[] parameterNames = WSDLUtils.getParameterNames(mapperMethod);
		Map<String, ObjectSchema> inputParametersMap = new HashMap<String, ObjectSchema>();
		
		// Loop for all parameters of mapper method
		for (int k = 0; k < parameterClasses.length; k++) {
			Class parameterClass = parameterClasses[k];
			Annotation[] annotations = parameterAnnotations[k];

			// Get parameter name
			String parameterName;
			if (parameterNames != null) {
				parameterName = parameterNames[k];
			} else {
				parameterName = parameterClass.getSimpleName() + WSDLConstants.SEPARATOR + k;
			}

			// Default cardMin: primitive parameters -> MANDATORY, others OPTIONAL
			Integer cardMin = parameterClass.isPrimitive() ? MANDATORY : OPTIONAL;
			Integer mandatory = cardMin; 
			Integer cardMax = null;

			// Try to get @Slot annotation
			Slot slotAnnotation = WSDLUtils.getSlotAnnotation(annotations);
			if (slotAnnotation != null) {
				if (!Slot.USE_METHOD_NAME.equals(slotAnnotation.name())) {
					parameterName = slotAnnotation.name();
				}
				mandatory = slotAnnotation.mandatory() ? MANDATORY : OPTIONAL;
			}

			// If parameter is a primitive OPTIONALITY is not permitted
			if (parameterClass.isPrimitive() && OPTIONAL.equals(cardMin)) {
				throw new Exception("Optionality not permitted in primitive parameter "+parameterName);
			}

			// Try to get @AggregateSlot annotation
			Class aggregateElementClass = null;
			AggregateSlot aggregateSlotAnnotation = WSDLUtils.getAggregateSlotAnnotation(annotations);
			if (aggregateSlotAnnotation != null) {
				cardMin = aggregateSlotAnnotation.cardMin();
				if (cardMin > 0) {
					mandatory = MANDATORY;
				}
				cardMax = aggregateSlotAnnotation.cardMax();
				aggregateElementClass = aggregateSlotAnnotation.type();
			}
			
			String parameterComplexType = createComplexTypeFromClass(tns, onto, actionSchema, parameterClass, wsdlTypeSchema, parameterName, null, cardMin, cardMax, aggregateElementClass);
			log.debug("--mapper input parameter: "+parameterName+" ("+parameterComplexType+")");

			// Create virtual schema of java parameter
			ObjectSchema parameterSchema = getParameterSchema(onto, parameterClass, aggregateElementClass);
			
			// Add parameter to map
			inputParametersMap.put(parameterName, parameterSchema);
			
			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {

				// Add a part message for all parameters
				Part partMessage = WSDLUtils.createTypePart(parameterName, parameterComplexType, tns);
				inputMessage.addPart(partMessage);
			} else {
				
				// Add a element in complex type definition for all parameters
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, parameterName, parameterComplexType, elementSequence, mandatory, null);
			}
		}
		
		return inputParametersMap;
	}
	
	private static void manageOutputParameter(Ontology onto, String tns, String operationName, String soapStyle, XSDSchema wsdlTypeSchema, Message outputMessage, String actionName) throws Exception {
		
		AgentActionSchema actionSchema = (AgentActionSchema) onto.getSchema(actionName);
		ObjectSchema resultSchema = actionSchema.getResultSchema();

		// In document style there is only a message part named parameters and an element in types definition
		XSDModelGroup elementSequence = null;
		if (WSDLConstants.STYLE_DOCUMENT.equals(soapStyle)) {
			
			String responseName = WSDLUtils.getResponseName(operationName); 
			Part partMessage = WSDLUtils.createElementPart(WSDLConstants.PARAMETERS, responseName, tns);
			outputMessage.addPart(partMessage);
			
			// Add element to type schema
			String elementName = WSDLUtils.getResponseName(operationName);
			XSDElementDeclaration element = WSDLUtils.addElementToSchema(tns, wsdlTypeSchema, elementName);
			XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToElement(element);
			elementSequence = WSDLUtils.addSequenceToComplexType(complexType);
		}
		
		// Check result schema
		if (resultSchema != null) {
			String resultName = WSDLUtils.getResultName(operationName);
			String resultType = createComplexTypeFromSchema(onto, tns, actionSchema, resultSchema, wsdlTypeSchema, resultSchema.getTypeName(), null, null, null);
			log.debug("--ontology output result: "+resultName+" ("+resultType+")");

			Part partMessage;
			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {
				partMessage = WSDLUtils.createTypePart(resultName, resultType, tns);
				outputMessage.addPart(partMessage);
				
			} else {
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, resultName, resultType, elementSequence);
			}
		}
	}
	
	public static ObjectSchema getParameterSchema(Ontology onto, Class parameterClass, Class aggregateElementClass) throws OntologyException {

		ObjectSchema parameterSchema;
		
		if (parameterClass.isPrimitive() || WSDLConstants.java2xsd.get(parameterClass) != null) {

			// Primitive java-type or xsd-type
			String typeName;
			if (parameterClass.isPrimitive()) {
				typeName = parameterClass.getName();
			} else {
				typeName = (String) WSDLConstants.java2xsd.get(parameterClass);
			}
			parameterSchema = new PrimitiveSchema(typeName);
		} 
		else if (parameterClass.isArray() ||
				 Collection.class.isAssignableFrom(parameterClass) ||
				 jade.util.leap.Collection.class.isAssignableFrom(parameterClass)) {

			if (parameterClass.isArray()) {
				// Overwrite aggregateType from Java Array
				aggregateElementClass = parameterClass.getComponentType();
			}
			
			String typeName;
			if (parameterClass.isArray() || 
				List.class.isAssignableFrom(parameterClass) ||
				jade.util.leap.List.class.isAssignableFrom(parameterClass)) {
				typeName = BasicOntology.SEQUENCE;
			} else {
				typeName = BasicOntology.SET;
			}
			
			ObjectSchema elementSchema = getParameterSchema(onto, aggregateElementClass, null);
			parameterSchema = new TypedAggregateSchema(typeName, elementSchema);
		} 
		else {
			
			// Search a schema of this parameterClass
			String conceptSchemaName = null;
			List conceptNames = onto.getConceptNames();
			for (int i=0; i<conceptNames.size(); i++) {
				String conceptName = (String)conceptNames.get(i);
				if (parameterClass.equals(onto.getClassForElement(conceptName))) {
					conceptSchemaName = conceptName;
					break;
				}
			}
			parameterSchema = onto.getSchema(conceptSchemaName);
		}

		return parameterSchema;
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
	
	private static String createComplexTypeFromClass(String tns, Ontology onto, ConceptSchema containerSchema, Class parameterClass, XSDSchema wsdlTypeSchema, String paramName, XSDComponent parentComponent, Integer cardMin, Integer cardMax, Class aggregateElementClass) throws Exception {
		
		String slotType = null;
		if (parameterClass.isPrimitive() || WSDLConstants.java2xsd.get(parameterClass) != null) {

			// Primitive java-type or xsd-type
			if (parameterClass.isPrimitive()) {
				slotType = parameterClass.getName();
			} else {
				slotType = (String) WSDLConstants.java2xsd.get(parameterClass);
			}
			if (parentComponent != null) {
				log.debug("------add primitive-type "+paramName+" ("+slotType+")");
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, paramName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
		} 
		else if (parameterClass.isEnum()) {
			
			// Enum
			slotType = parameterClass.getSimpleName();
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				log.debug("----create simple-type "+slotType);
				XSDSimpleTypeDefinition simpleTypeDefinition = wsdlTypeSchema.resolveSimpleTypeDefinition(WSDLConstants.XSD_URL, WSDLConstants.XSD_STRING);
				Object[] enumValues = parameterClass.getEnumConstants();
				Object[] permittedValues = new Object[enumValues.length]; 
				for (int i=0; i<enumValues.length; i++) {
					permittedValues[i] = enumValues[i].toString(); 
				}
				XSDSimpleTypeDefinition enumType = WSDLUtils.addSimpleTypeToSchema(tns, wsdlTypeSchema, slotType, simpleTypeDefinition);
				WSDLUtils.addRestrictionToSimpleType(enumType, permittedValues);
			}
		}
		else if (parameterClass.isArray() || 
				 Collection.class.isAssignableFrom(parameterClass) ||
				 jade.util.leap.Collection.class.isAssignableFrom(parameterClass)) {

			if (parameterClass.isArray()) {
				// Overwrite aggregateType from Java Array
				aggregateElementClass = parameterClass.getComponentType();
			}
			
			if (aggregateElementClass == null) {
				throw new Exception("Collection class "+parameterClass.getSimpleName()+" without specified element type");
			}
			
			paramName = aggregateElementClass.getSimpleName().toLowerCase();
			slotType = WSDLUtils.getAggregateType(paramName, cardMin, cardMax);
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				log.debug("----create array-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
				
				if (cardMax == null) {
					// If not specified, default card-max is UNBOUNDED
					cardMax = Integer.valueOf(ObjectSchema.UNLIMITED);
				}
				createComplexTypeFromClass(tns, onto, containerSchema, aggregateElementClass, wsdlTypeSchema, paramName, sequence, cardMin, cardMax, null);
			}
		} 
		else {
			// Java custom type (work with concept schema of type paramType)
			// Search a schema of this type
			ObjectSchema conceptSchema = onto.getSchema(parameterClass);
			if (conceptSchema == null) {
				throw new Exception("ConceptSchema of type "+parameterClass.getSimpleName()+" doesn't exist in "+ onto.getName());
			}
			
			String conceptSchemaName = conceptSchema.getTypeName();
			slotType = createComplexTypeFromSchema(onto, tns, containerSchema, conceptSchema, wsdlTypeSchema, conceptSchemaName, parentComponent, null, null);
		}
		
		return slotType;
	}

	private static String createComplexTypeFromSchema(Ontology onto, String tns, ConceptSchema containerSchema, ObjectSchema objSchema, XSDSchema wsdlTypeSchema, String slotName, XSDComponent parentComponent, Integer cardMin, Integer cardMax) throws Exception {
		
		String slotType = null;
		if (objSchema instanceof PrimitiveSchema) {
			// Get type from PrimitiveSchema
			slotType = WSDLUtils.getPrimitiveType(objSchema, containerSchema, slotName);
			
			if (parentComponent != null) {
				if (cardMin == MANDATORY && !containerSchema.isMandatory(slotName)) {
					cardMin = OPTIONAL;
				}
				
				log.debug("------add primitive-type "+slotName+" ("+slotType+") "+(OPTIONAL.equals(cardMin)?"OPTIONAL":""));
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
		} 
		else if (objSchema instanceof ConceptSchema) {
			// Get type from ConceptSchema (if not found in wsdlTypeSchema create it)
			slotType = objSchema.getTypeName();
			if (parentComponent != null) {
				if (cardMin == MANDATORY && !containerSchema.isMandatory(slotName)) {
					cardMin = OPTIONAL;
				}
				log.debug("------add defined-type "+slotName+" ("+slotType+") "+(OPTIONAL.equals(cardMin)?"OPTIONAL":""));
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
			
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				Class slotClass = onto.getClassForElement(slotType);
				if (slotClass != null && slotClass.isEnum()) {
					log.debug("----create simple-type "+slotType);
					XSDSimpleTypeDefinition simpleTypeDefinition = wsdlTypeSchema.resolveSimpleTypeDefinition(WSDLConstants.XSD_URL, WSDLConstants.XSD_STRING);
					Object[] permittedValues = WSDLUtils.getPermittedValues(objSchema, WSDLConstants.ENUM_SLOT_NAME);
					XSDSimpleTypeDefinition enumType = WSDLUtils.addSimpleTypeToSchema(tns, wsdlTypeSchema, slotType, simpleTypeDefinition);
					WSDLUtils.addRestrictionToSimpleType(enumType, permittedValues);
				}
				else {
					log.debug("----create complex-type "+slotType);
					XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
					XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
					for (String conceptSlotName : objSchema.getNames()) {
						ObjectSchema slotSchema = objSchema.getSchema(conceptSlotName);
						createComplexTypeFromSchema(onto, tns, (ConceptSchema) objSchema, slotSchema, wsdlTypeSchema, conceptSlotName, sequence, null, null);
					}
				}
			}
		} 
		else if (objSchema instanceof AggregateSchema) {

			// Get type from AggregateSchema (if array type not present in wsdlTypeSchema create it)
			// Get cardinality and aggregate type
			cardMin = WSDLUtils.getAggregateCardMin(containerSchema, slotName);
			cardMax = WSDLUtils.getAggregateCardMax(containerSchema, slotName);
			ObjectSchema aggregateSchema = WSDLUtils.getAggregateElementSchema(containerSchema, slotName);
			
			// Get array type 
			slotType = aggregateSchema.getTypeName();
			if (aggregateSchema instanceof PrimitiveSchema) {
				slotType = WSDLUtils.getPrimitiveType(aggregateSchema, containerSchema, slotName);
			}
			String itemName = slotType;
			String aggregateType = objSchema.getTypeName();
			slotType = WSDLUtils.getAggregateType(slotType, cardMin, cardMax);
			
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				log.debug("----create array-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
				if (parentComponent != null) {
					log.debug("------add array-type "+slotName+" ("+slotType+") ["+cardMin+","+cardMax+"]");
					WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, OPTIONAL.equals(cardMin)?cardMin:null, null);
				}
				createComplexTypeFromSchema(onto, tns, containerSchema, aggregateSchema, wsdlTypeSchema, itemName, sequence, cardMin, cardMax);
			}
		}
		
		return slotType;
	}
}