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

import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.TermSchema;
import jade.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

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

import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.WSIGConstants;
import com.tilab.wsig.store.ActionBuilder;
import com.tilab.wsig.store.ApplyTo;
import com.tilab.wsig.store.Builder;
import com.tilab.wsig.store.FaultBuilder;
import com.tilab.wsig.store.FaultConverter;
import com.tilab.wsig.store.MapperBasedActionBuilder;
import com.tilab.wsig.store.MapperBasedFaultConverter;
import com.tilab.wsig.store.MapperBasedResultConverter;
import com.tilab.wsig.store.OntologyBasedActionBuilder;
import com.tilab.wsig.store.OntologyBasedFaultConverter;
import com.tilab.wsig.store.OntologyBasedResultConverter;
import com.tilab.wsig.store.OperationName;
import com.tilab.wsig.store.ParameterInfo;
import com.tilab.wsig.store.ResultBuilder;
import com.tilab.wsig.store.ResultConverter;
import com.tilab.wsig.store.SuppressOperation;
import com.tilab.wsig.store.TypedAggregateSchema;
import com.tilab.wsig.store.WSIGService;

public class JadeToWSDL {
	
	private static Logger logger = Logger.getMyLogger(JadeToWSDL.class.getName());
	
	public static Integer MANDATORY = null;
	public static Integer OPTIONAL = Integer.valueOf(0);
	
	private String soapStyle;
	private String soapUse;
	private String tns;
	private WSIGService wsigService;
	private Ontology ontoService;
	private Ontology ontoAgent;
	private Class mapperClass;
	private XSDSchema wsdlTypeSchema;
	
	
	public Definition createWSDL(WSIGService wsigService) throws Exception {
		this.wsigService = wsigService;
		
		// Get soap style & use
		soapStyle = WSIGConfiguration.getInstance().getWsdlStyle();
		if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {
			soapUse = WSDLConstants.USE_ENCODED;
		} else {
			soapUse = WSDLConstants.USE_LITERAL;
		}
		
		// Create wsdl definition and type schema
		String serviceName = wsigService.getServiceName();
		WSDLFactory factory = WSDLFactory.newInstance();
		tns = WSDLConstants.URN+":" + serviceName;
		
		Definition definition = WSDLUtils.createWSDLDefinition(factory, tns);
		wsdlTypeSchema = WSDLUtils.createSchema(tns);

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

		// Create mapper object (if any)
		Object mapperObject = null;
		mapperClass = wsigService.getMapperClass();
		if (mapperClass != null) {
			try {
				mapperObject = mapperClass.newInstance();
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Mapper class "+mapperClass.getName()+" can not be instantiated", e);
				throw e;
			}
		}

		// Get mapper actions and converters
		Map<String, Map<String, Method>> mapperActions = getMapperActions();
		Map<String, Map<String, Class>> mapperResultConverters = getMapperConverters(ResultConverter.class, WSIGConstants.RESULT_CONVERTER_SUFFIX);
		Map<String, Map<String, Class>> mapperFaultConverters = getMapperConverters(FaultConverter.class, WSIGConstants.FAULT_CONVERTER_SUFFIX);
		
		// Manage ontology
		ontoService = wsigService.getServiceOntology();
		ontoAgent = wsigService.getAgentOntology();
		logger.log(Level.FINE, "Elaborate ontology: "+ontoService.getName());		
		
		// Manage actions
		List actionNames = ontoService.getActionNames();
		actionNames.addAll(ontoService.getPredicateNames());
		for (int i = 0; i < actionNames.size(); i++) {
			try {
				String actionName = (String) actionNames.get(i);
				logger.log(Level.FINE, "Elaborate operation: "+ actionName);		

				// Check if action is suppressed (valid only if mapper is present)
				if (isActionSuppressed(actionName)) {
					logger.log(Level.FINE, "--operation "+actionName+" suppressed");
					continue;
				}
				
				int operationsForAction = 1;
				
				// Get action methods declared in mapper (if any)
				Map<String, Method> mapperMethodsForAction = mapperActions.get(actionName.toLowerCase());
				Iterator<Entry<String, Method>> mapperMethodsForActionIt = null;
				if (mapperMethodsForAction != null) {
					operationsForAction = mapperMethodsForAction.size();
					mapperMethodsForActionIt = mapperMethodsForAction.entrySet().iterator();
				}
				
				// Loop all operations
				for (int j = 0; j < operationsForAction; j++) {
					
					// Prepare default operation name
					String operationName = actionName;

					// Create appropriate ActionBuilder
					ActionBuilder actionBuilder = null;
					Method mapperActionMethod = null;
					if (mapperMethodsForAction != null) {
						Entry<String, Method> mapperMethodForAction = mapperMethodsForActionIt.next();
						operationName = mapperMethodForAction.getKey();
						mapperActionMethod = mapperMethodForAction.getValue();
						
						actionBuilder = new MapperBasedActionBuilder(ontoService, ontoAgent, actionName, mapperObject, mapperActionMethod);
					} else {
						
						actionBuilder = new OntologyBasedActionBuilder(ontoService, ontoAgent, actionName);
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
					
					Map<String, ParameterInfo> inputParameters = manageInputParameters(operationName, inputMessage, actionName, mapperActionMethod);
					
					// Set input parameters map to action builder
					actionBuilder.setParameters(inputParameters);
					
					// Get result converter class declared in mapper (if any)
					Class mapperResultConverterClass = null;
					Map<String, Class> mapperResultConvertersForAction = mapperResultConverters.get(actionName.toLowerCase());
					if (mapperResultConvertersForAction != null) {
						mapperResultConverterClass = mapperResultConvertersForAction.get(operationName.toLowerCase());
					}
					
					// Create appropriate ResultBuilder
					ResultBuilder resultBuilder;
					if (mapperResultConverterClass != null) {
						
						resultBuilder = new MapperBasedResultConverter(mapperObject, mapperResultConverterClass, ontoService, ontoAgent, actionName);
					} else {
						
						resultBuilder = new OntologyBasedResultConverter(ontoService, ontoAgent, actionName);
					}

					// Add ResultBuilder to wsigService 
					wsigService.addResultBuilder(operationName, resultBuilder);

					// Get fault converter class declared in mapper (if any)
					Class mapperFaultConverterClass = null;
					Map<String, Class> mapperFaultConvertersForAction = mapperFaultConverters.get(actionName.toLowerCase());
					if (mapperFaultConvertersForAction != null) {
						mapperFaultConverterClass = mapperFaultConvertersForAction.get(operationName.toLowerCase());
					}

					// Create appropriate FaultBuilder
					FaultBuilder faultBuilder;
					if (mapperFaultConverterClass != null) {
						
						faultBuilder = new MapperBasedFaultConverter(mapperObject, mapperFaultConverterClass, ontoService, ontoAgent, actionName);
					} else {
						
						faultBuilder = new OntologyBasedFaultConverter(ontoService, ontoAgent, actionName);
					}

					// Add FaultBuilder to wsigService 
					wsigService.addFaultBuilder(operationName, faultBuilder);
					
					// Output parameters		
					Message outputMessage = WSDLUtils.createMessage(tns, WSDLUtils.getResponseName(operationName));
					Output output = WSDLUtils.createOutput(outputMessage);
					operation.setOutput(output);
					definition.addMessage(outputMessage);

					BindingOutput outputBinding = WSDLUtils.createBindingOutput(registry, tns, soapUse);
					operationBinding.setBindingOutput(outputBinding);

					Map<String, ParameterInfo> outputParameters = manageOutputParameters(operationName, outputMessage, actionName, mapperResultConverterClass);
					resultBuilder.setParameters(outputParameters);
				}
			} catch (Exception e) {
				throw new Exception("Error in Agent Action Handling", e);
			}
		}
		
		// Log ontology
		if (logger.isLoggable(Level.FINE)) {
			ontoService.dump();
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
				logger.log(Level.INFO, "Write WSDL for service: "+serviceName);
				WSDLUtils.writeWSDL(factory, definition, serviceName);
				
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error writing WSDL file", e);
			}
		}
		
		return definition;
	}
	
	private Map<String, ParameterInfo> manageInputParameters(String operationName, Message inputMessage, String actionName, Method mapperMethod) throws Exception {

		ObjectSchema actionSchema = (ObjectSchema) ontoService.getSchema(actionName);

		// In document style there is only a message part named parameters and an element in types definition
		XSDModelGroup elementSequence = null;
		if (WSDLConstants.STYLE_DOCUMENT.equals(soapStyle)) {
			Part partMessage = WSDLUtils.createElementPart(WSDLConstants.PARAMETERS, operationName, tns);
			inputMessage.addPart(partMessage);

			XSDElementDeclaration element = WSDLUtils.addElementToSchema(tns, wsdlTypeSchema, operationName);
			XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToElement(element);
			elementSequence = WSDLUtils.addSequenceToComplexType(complexType);
		}

		Map<String, ParameterInfo> inputParametersMap;
		if (mapperMethod != null) {
			// Mapper
			inputParametersMap = manageMapperInputParameters(elementSequence, inputMessage, actionSchema, mapperMethod);
			
		} else {
			// Ontology
			inputParametersMap = manageOntologyInputParameters(elementSequence, inputMessage, actionSchema);
		}
		
		return inputParametersMap;
	}
	
	private Map<String, ParameterInfo> manageOntologyInputParameters(XSDModelGroup elementSequence, Message inputMessage, ObjectSchema actionSchema) throws Exception {

		String[] slotNames = actionSchema.getNames();
		Map<String, ParameterInfo> inputParametersMap = new HashMap<String, ParameterInfo>();

		// Loop for all slot of action schema
		for (String slotName : slotNames) {
			TermSchema slotSchema = (TermSchema)actionSchema.getSchema(slotName);
			String slotType = createComplexTypeFromSchema(slotSchema, actionSchema, slotName, null, null, null);
			logger.log(Level.FINE, "--ontology input slot: "+slotName+" ("+slotType+")");

			// For aggregate create the relative TypedAggregateSchema
			if (slotSchema instanceof AggregateSchema) {
				slotSchema = WSDLUtils.getTypedAggregateSchema(actionSchema, slotName);
			}
			
			// Add parameter to map
			inputParametersMap.put(slotName, new ParameterInfo(slotName, slotSchema));
			
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

	private Map<String, ParameterInfo> manageMapperInputParameters(XSDModelGroup elementSequence, Message inputMessage, ObjectSchema actionSchema, Method mapperMethod) throws Exception {
		String[] parameterNames = WSDLUtils.getParameterNames(mapperMethod);
		Class[] parameterClasses = mapperMethod.getParameterTypes();
		Annotation[][] parameterAnnotations = mapperMethod.getParameterAnnotations();
		
		return manageMapperParameters(elementSequence, inputMessage, actionSchema, parameterNames, parameterClasses, parameterAnnotations, null);
	}

	private Map<String, ParameterInfo> manageMapperParameters(XSDModelGroup elementSequence, Message message, ObjectSchema actionSchema, String[] parameterNames, Class[] parameterClasses, Annotation[][] parameterAnnotations, Method[] parameterMethods) throws Exception {
		Map<String, ParameterInfo> parametersMap = new LinkedHashMap<String, ParameterInfo>();
		
		// Loop for all parameters of mapper method
		for (int k = 0; k < parameterClasses.length; k++) {
			Method parameterMethod = null;
			if (parameterMethods != null) {
				parameterMethod = parameterMethods[k];
			}
			Class parameterClass = parameterClasses[k];
			Annotation[] annotations = parameterAnnotations[k];

			// Skip all suppressed methods
			if (WSDLUtils.getSuppressSlotAnnotation(annotations) != null) {
				continue;
			}
			
			// Get parameter name
			String parameterName;
			if (parameterNames != null) {
				parameterName = parameterNames[k];
			} else {
		        // If the mapper class is a Java dynamic proxy is not possible to have 
				// the name of the parameters of the methods (methodParameterNames == null)
				// Use SOAP parameters as master and apply it in the order of vector   
				// See: WSDLUtils.getParameterNames(method)
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
			
			String parameterComplexType = createComplexTypeFromClass(parameterClass, parameterName, null, cardMin, cardMax, aggregateElementClass);
			logger.log(Level.FINE, "--mapper input parameter: "+parameterName+" ("+parameterComplexType+")");

			// Create virtual schema of java parameter
			TermSchema parameterSchema = getParameterSchema(parameterClass, aggregateElementClass);
			
			// Add parameter to map
			ParameterInfo pi = new ParameterInfo(parameterName, parameterSchema);
			pi.setParameterClass(parameterClass);
			pi.setMapperMethod(parameterMethod);
			pi.setMinCard(cardMin);
			if (parameterSchema instanceof TypedAggregateSchema) {
				pi.setMaxCard(cardMax);
				
				TypedAggregateSchema aggregateSchema = (TypedAggregateSchema)parameterSchema; 
				pi.setElementsSchema((TermSchema)aggregateSchema.getElementSchema());
			}
			
			parametersMap.put(parameterName, pi);
			
			if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {

				// Add a part message for all parameters
				Part partMessage = WSDLUtils.createTypePart(parameterName, parameterComplexType, tns);
				message.addPart(partMessage);
			} else {
				
				// Add a element in complex type definition for all parameters
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, parameterName, parameterComplexType, elementSequence, mandatory, null);
			}
		}
		
		return parametersMap;
	}
	
	private Map<String, ParameterInfo> manageOutputParameters(String operationName, Message outputMessage, String actionName, Class mapperResultConverterClass) throws Exception {
		
		ObjectSchema actionSchema = (ObjectSchema) ontoService.getSchema(actionName);

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
		
		Map<String, ParameterInfo> outputParametersMap;
		if (mapperResultConverterClass != null) {
			// Mapper
			outputParametersMap = manageMapperOutputParameters(elementSequence, outputMessage, actionSchema, operationName, mapperResultConverterClass);
			
		} else {
			// Ontology
			outputParametersMap = manageOntologyOutputParameters(elementSequence, outputMessage, actionSchema, operationName);
		}
		
		return outputParametersMap;
	}
	
	private Map<String, ParameterInfo> manageMapperOutputParameters(XSDModelGroup elementSequence, Message outputMessage, ObjectSchema actionSchema, String operationName, Class resultConverterClass) throws Exception {
		
		ArrayList<Method> parameterMethods = new ArrayList<Method>();
		ArrayList<String> parameterNames = new ArrayList<String>();
		ArrayList<Class> parameterClasses = new ArrayList<Class>();
		ArrayList<Annotation[]> parameterAnnotations = new ArrayList<Annotation[]>();
		Method[] classMethods = resultConverterClass.getDeclaredMethods();
		for (Method method : classMethods) {
			if (WSDLUtils.isGetter(method)) {
				parameterMethods.add(method);
				parameterNames.add(WSDLUtils.buildNameFromGetterMethod(method));
				parameterClasses.add(method.getReturnType());
				parameterAnnotations.add(method.getAnnotations());
			} 
		}
		
		Method[] methods = parameterMethods.toArray(new Method[parameterMethods.size()]);
		String[] names = parameterNames.toArray(new String[parameterNames.size()]);
		Class[] classes = parameterClasses.toArray(new Class[parameterClasses.size()]);
		Annotation[][] annotations = parameterAnnotations.toArray(new Annotation[parameterAnnotations.size()][]);
		 
		return manageMapperParameters(elementSequence, outputMessage, actionSchema, names, classes, annotations, methods);
	}

	private Map<String, ParameterInfo> manageOntologyOutputParameters(XSDModelGroup elementSequence, Message outputMessage, ObjectSchema actionSchema, String operationName) throws Exception {
		
		Map<String, ParameterInfo> outputParametersMap = new HashMap<String, ParameterInfo>();
		
		if (actionSchema instanceof AgentActionSchema) {
			TermSchema resultSchema = ((AgentActionSchema) actionSchema).getResultSchema();
			if (resultSchema != null) {
				String resultName = WSDLUtils.getResultName(operationName);
				String resultType = createComplexTypeFromSchema(resultSchema, actionSchema, resultSchema.getTypeName(), null, null, null);
				logger.log(Level.FINE, "--ontology output result: "+resultName+" ("+resultType+")");
	
				outputParametersMap.put(resultName, new ParameterInfo(resultName, resultSchema));
				
				Part partMessage;
				if (WSDLConstants.STYLE_RPC.equals(soapStyle)) {
					partMessage = WSDLUtils.createTypePart(resultName, resultType, tns);
					outputMessage.addPart(partMessage);
					
				} else {
					WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, resultName, resultType, elementSequence);
				}
			}
		}
		
		return outputParametersMap;
	}

	public TermSchema getParameterSchema(Class parameterClass, Class aggregateElementClass) throws OntologyException {

		TermSchema parameterSchema;
		
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
		else if (Builder.isCollection(parameterClass)) {

			if (parameterClass.isArray()) {
				// Overwrite aggregateType from Java Array
				aggregateElementClass = parameterClass.getComponentType();
			}
			
			String typeName;
			if (Builder.isSequence(parameterClass)) {
				typeName = BasicOntology.SEQUENCE;
			} else {
				typeName = BasicOntology.SET;
			}
			
			ObjectSchema elementSchema = getParameterSchema(aggregateElementClass, null);
			parameterSchema = new TypedAggregateSchema(typeName, elementSchema);
		} 
		else {
			
			// Search a schema of this parameterClass
			String conceptSchemaName = null;
			List conceptNames = ontoService.getConceptNames();
			for (int i=0; i<conceptNames.size(); i++) {
				String conceptName = (String)conceptNames.get(i);
				if (parameterClass.equals(ontoService.getClassForElement(conceptName))) {
					conceptSchemaName = conceptName;
					break;
				}
			}
			parameterSchema = (TermSchema)ontoService.getSchema(conceptSchemaName);
		}

		return parameterSchema;
	}
	
	private Map<String, Map<String, Method>> getMapperActions() {
		Map<String, Map<String, Method>> mapperActions = new HashMap<String, Map<String, Method>>();

		if (mapperClass != null) {
			Method[] mapperMethods = mapperClass.getDeclaredMethods();
			for (Method mapperMethod : mapperMethods) {
				String mapperMethodName = mapperMethod.getName();
				if (mapperMethodName.startsWith(WSDLConstants.MAPPER_METHOD_PREFIX)) {
					// Remove prefix to from method name toXXX()
					String actionName = mapperMethodName.substring(2);
					
					Map<String,Method> operationMethods = mapperActions.get(actionName.toLowerCase());
					if (operationMethods == null) {
						operationMethods = new HashMap<String, Method>();
						mapperActions.put(actionName.toLowerCase(), operationMethods);
					}
					
					String operationName = actionName;

					// Check is the operation has a specific name specified with annotation @OperationName
					OperationName annotationOperationName = mapperMethod.getAnnotation(OperationName.class);
					if (annotationOperationName != null) {
						operationName = annotationOperationName.name();
					}
					else {
						// If there are more operations (without annotation) modify the operationName that must be unique 
						if (operationMethods.size() > 0) {
							// Use parameter class type to define operation name
							Class[] parameterTypes = mapperMethod.getParameterTypes();
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
					
					operationMethods.put(operationName, mapperMethod);
				} 
			}
		}
		
		return mapperActions;
	}

	private <A> Map<String, Map<String, Class>> getMapperConverters(A annotationClass, String classSuffix) {
		Map<String, Map<String, Class>> mapperResultConverters = new HashMap<String, Map<String, Class>>();

		if (mapperClass != null) {
			Class[] mapperInnerClasses = mapperClass.getDeclaredClasses();
			for (Class mapperInnerClass : mapperInnerClasses) {
				
				A annotationConverter = (A)mapperInnerClass.getAnnotation((Class)annotationClass);
				if (annotationConverter != null) {
					ApplyTo[] appliesTo = null;
					if (annotationConverter instanceof ResultConverter) {
						appliesTo = ((ResultConverter)annotationConverter).value();
					} else if (annotationConverter instanceof FaultConverter) {
						appliesTo = ((FaultConverter)annotationConverter).value();
					}
					if (appliesTo != null) {
						for (ApplyTo applyTo : appliesTo) {

							String actionName = applyTo.action();
							String operationName = applyTo.operation();
							if (operationName.equals(ApplyTo.NULL)) {
								operationName = actionName;
							}
							
							addMapperConverters(mapperResultConverters, actionName, operationName, mapperInnerClass);
						}
					}
				} else {
					// Check is the inner-class name end with ResultConverter
					String className = mapperInnerClass.getSimpleName();
					if (className.endsWith(classSuffix)) {
						
						int pos = className.indexOf(classSuffix);
						String actionName = className.substring(0, pos).toLowerCase();

						
						addMapperConverters(mapperResultConverters, actionName, actionName, mapperInnerClass);
					}
				}
			}
		}
		
		return mapperResultConverters;
	}
	
	private void addMapperConverters(Map<String, Map<String, Class>> mapperConverters, String actionName, String operationName, Class mapperInnerClass) {
		Map<String,Class> operationClasses = mapperConverters.get(actionName.toLowerCase());
		if (operationClasses == null) {
			operationClasses = new HashMap<String, Class>();
			mapperConverters.put(actionName.toLowerCase(), operationClasses);
		}

		if (!operationClasses.containsKey(operationName.toLowerCase())) {
			operationClasses.put(operationName.toLowerCase(), mapperInnerClass);	
		} else {
			logger.log(Level.WARNING, "Skipped converter class <"+mapperInnerClass.getName()+"> map because the operation <"+operationName+"> of action <"+actionName+"> is already mapped!");
		}
	}
	
	private boolean isActionSuppressed(String actionName) {
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
	
	private String createComplexTypeFromClass(Class parameterClass, String paramName, XSDComponent parentComponent, Integer cardMin, Integer cardMax, Class aggregateElementClass) throws Exception {
		
		String slotType = null;
		if (parameterClass.isPrimitive() || WSDLConstants.java2xsd.get(parameterClass) != null) {

			// Primitive java-type or xsd-type
			if (parameterClass.isPrimitive()) {
				slotType = parameterClass.getName();
			} else {
				slotType = (String) WSDLConstants.java2xsd.get(parameterClass);
			}
			if (parentComponent != null) {
				logger.log(Level.FINE, "------add primitive-type "+paramName+" ("+slotType+")");
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, paramName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
		} 
		else if (parameterClass.isEnum()) {
			
			// Enum
			slotType = parameterClass.getSimpleName();
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				logger.log(Level.FINE, "----create simple-type "+slotType);
				XSDSimpleTypeDefinition simpleTypeDefinition = wsdlTypeSchema.resolveSimpleTypeDefinition(WSDLConstants.XSD_URL, WSDLConstants.XSD_STRING);
				Object[] enumValues = parameterClass.getEnumConstants();
				Object[] permittedValues = new Object[enumValues.length]; 
				for (int i=0; i<enumValues.length; i++) {
					permittedValues[i] = enumValues[i].toString(); 
				}
				XSDSimpleTypeDefinition enumType = WSDLUtils.addSimpleTypeToSchema(tns, wsdlTypeSchema, slotType, simpleTypeDefinition);
				WSDLUtils.addRestrictionToSimpleType(enumType, permittedValues);
			}
			
			ObjectSchema enumSchema = ontoService.getSchema(parameterClass);
			if (enumSchema == null) {
				// Schema not present -> add the class to ontology
				logger.log(Level.FINE, "----add class "+parameterClass+" to ontology");
				((BeanOntology)ontoService).add(parameterClass);
			}
			
			if (parentComponent != null) {
				logger.log(Level.FINE, "------add enum-type "+paramName+" ("+slotType+")");
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, paramName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
		}
		else if (Builder.isCollection(parameterClass)) {

			if (parameterClass.isArray()) {
				// Overwrite aggregateType from Java Array
				aggregateElementClass = parameterClass.getComponentType();
			}
			
			// If is not a java array is not possible get the type of aggregate element. 
			// It would be possible get it from the return type of a method (such in BOB) 
			// but not directly from a class (parameterClass)
			if (aggregateElementClass == null) {
				throw new Exception("Collection class "+parameterClass.getSimpleName()+" without specified element type");
			}
			
			paramName = aggregateElementClass.getSimpleName().toLowerCase();
			slotType = WSDLUtils.getAggregateType(paramName, cardMin, cardMax);
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				logger.log(Level.FINE, "----create array-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
				
				if (cardMax == null) {
					// If not specified, default card-max is UNBOUNDED
					cardMax = Integer.valueOf(ObjectSchema.UNLIMITED);
				}
				createComplexTypeFromClass(aggregateElementClass, paramName, sequence, cardMin, cardMax, null);
			}
		} 
		else {
			// Java custom type, search in the ontology for a schema of this type
			ObjectSchema conceptSchema = ontoService.getSchema(parameterClass);
			if (conceptSchema == null) {
				// Schema not present -> add the class to ontology
				logger.log(Level.FINE, "----add class "+parameterClass+" to ontology");
				((BeanOntology)ontoService).add(parameterClass);
				
				// Retry to get schema -> if not found throw an exception
				conceptSchema = ontoService.getSchema(parameterClass);
				if (conceptSchema == null) {
					throw new Exception("ConceptSchema of type "+parameterClass.getSimpleName()+" doesn't exist in "+ ontoService.getName());
				}
			}
			
			String conceptSchemaName = conceptSchema.getTypeName();
			slotType = createComplexTypeFromSchema(conceptSchema, null, conceptSchemaName, parentComponent, cardMin, cardMax);
		}
		
		return slotType;
	}

	private String createComplexTypeFromSchema(ObjectSchema objSchema) throws Exception {
		return createComplexTypeFromSchema(objSchema, null, null, null, null, null);
	}
	
	private String createComplexTypeFromSchema(ObjectSchema objSchema, ObjectSchema containerSchema, String slotName, XSDComponent parentComponent, Integer cardMin, Integer cardMax) throws Exception {
		
		String slotType = null;
		if (objSchema instanceof PrimitiveSchema || objSchema.getClass()==TermSchema.class) {
			// Get type from PrimitiveSchema
			slotType = WSDLUtils.getPrimitiveType(objSchema, containerSchema, slotName);
			
			if (parentComponent != null) {
				if (cardMin == MANDATORY && !containerSchema.isMandatory(slotName)) {
					cardMin = OPTIONAL;
				}
				
				logger.log(Level.FINE, "------add primitive-type "+slotName+" ("+slotType+") "+(OPTIONAL.equals(cardMin)?"OPTIONAL":""));
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
		} 
		else if (objSchema instanceof ConceptSchema) {
			// Get type from ConceptSchema (if not found in wsdlTypeSchema create it)
			slotType = objSchema.getTypeName();
			if (parentComponent != null) {
				// Nota: containerSchema può essere nullo solo nel caso di mapper dove la ActionSchema 
				// non corrisponde al contenitore dei parametri definiti nei metodi toXXX o nelle classi 
				// ResultConverter.
				// Qui arrivo nei casi di parametri di tipo List<T> dove T e' un tipo complesso al 
				// momento di gestire T. In questo caso parentComponent e' una sequence ma il containerSchema
				// non puo' essere usato per determinare la mandatorieta', quindi solo la cardinalita'
				// definita nella annotation viene presa in considerazione.
				if (cardMin == MANDATORY && containerSchema != null && !containerSchema.isMandatory(slotName)) {
					cardMin = OPTIONAL;
				}
				logger.log(Level.FINE, "------add defined-type "+slotName+" ("+slotType+") "+(OPTIONAL.equals(cardMin)?"OPTIONAL":""));
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, cardMin, cardMax);
			}
			
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, tns, slotType) == null) {
				Class slotClass = ontoService.getClassForElement(slotType);
				if (slotClass != null && slotClass.isEnum()) {
					logger.log(Level.FINE, "----create simple-type "+slotType);
					XSDSimpleTypeDefinition simpleTypeDefinition = wsdlTypeSchema.resolveSimpleTypeDefinition(WSDLConstants.XSD_URL, WSDLConstants.XSD_STRING);
					Object[] permittedValues = WSDLUtils.getPermittedValues(objSchema, WSDLConstants.ENUM_SLOT_NAME);
					XSDSimpleTypeDefinition enumType = WSDLUtils.addSimpleTypeToSchema(tns, wsdlTypeSchema, slotType, simpleTypeDefinition);
					WSDLUtils.addRestrictionToSimpleType(enumType, permittedValues);
				}
				else {
					if (wsigService.isHierarchicalComplexType()) {
						
						// Manage super schema 
						// (WARNING only first ontology super-schema is managed because in xsd the multiple inheritance is not supported)
						XSDTypeDefinition xsdBaseTypeDef = null;
						ObjectSchema[] superSchemas = objSchema.getSuperSchemas();
						if (superSchemas != null && superSchemas.length > 0) {
							ObjectSchema superSchema = superSchemas[0];
							
							String xsdTypeName = createComplexTypeFromSchema(superSchema);
							xsdBaseTypeDef = WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, tns, xsdTypeName);
						}
						
						if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, tns, slotType) == null) {
							// Create xsd type
							logger.log(Level.FINE, "----create complex-type "+slotType);
							XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType, xsdBaseTypeDef);
							XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
							
							// Manage only own slots
							for (String conceptSlotName : objSchema.getOwnNames()) {
								ObjectSchema slotSchema = objSchema.getSchema(conceptSlotName);
								createComplexTypeFromSchema(slotSchema, (ConceptSchema) objSchema, conceptSlotName, sequence, null, null);
							}
						}

						// Manage schemas that extends this
						List<String> conceptNames = ontoService.getConceptNames();
						for (String conceptName : conceptNames) {
							ObjectSchema schema = ontoService.getSchema(conceptName);
							superSchemas = schema.getSuperSchemas();
							for (ObjectSchema superSchema : superSchemas) {
								if (superSchema.getTypeName().equalsIgnoreCase(objSchema.getTypeName())) {
									createComplexTypeFromSchema(schema);
									break;
								}
							}
						}
					}
					else {
						// Create xsd type
						logger.log(Level.FINE, "----create complex-type "+slotType);
						XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
						XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
						
						// Manage all slots in flat mode
						for (String conceptSlotName : objSchema.getNames()) {
							ObjectSchema slotSchema = objSchema.getSchema(conceptSlotName);
							createComplexTypeFromSchema(slotSchema, (ConceptSchema) objSchema, conceptSlotName, sequence, null, null);
						}
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

			if (parentComponent != null) {
				logger.log(Level.FINE, "------add array-type "+slotName+" ("+slotType+") ["+cardMin+","+cardMax+"]");
				WSDLUtils.addElementToSequence(tns, wsdlTypeSchema, slotName, slotType, (XSDModelGroup) parentComponent, OPTIONAL.equals(cardMin)?cardMin:null, null);
			}
			
			if (WSDLUtils.getSimpleOrComplexType(wsdlTypeSchema, wsdlTypeSchema.getTargetNamespace(), slotType) == null) {
				logger.log(Level.FINE, "----create array-type "+slotType);
				XSDComplexTypeDefinition complexType = WSDLUtils.addComplexTypeToSchema(tns, wsdlTypeSchema, slotType);
				XSDModelGroup sequence = WSDLUtils.addSequenceToComplexType(complexType);
				createComplexTypeFromSchema(aggregateSchema, containerSchema, itemName, sequence, cardMin, cardMax);
			}
		}
		
		return slotType;
	}
}