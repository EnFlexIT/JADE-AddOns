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

import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.Facet;
import jade.content.schema.ObjectSchema;
import jade.content.schema.facets.CardinalityFacet;
import jade.content.schema.facets.JavaTypeFacet;
import jade.content.schema.facets.PermittedValuesFacet;
import jade.content.schema.facets.TypedAggregateFacet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ElementExtensible;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLWriter;
import javax.xml.namespace.QName;

import org.apache.axis.utils.bytecode.ParamReader;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDEnumerationFacet;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import com.ibm.wsdl.BindingImpl;
import com.ibm.wsdl.BindingInputImpl;
import com.ibm.wsdl.BindingOperationImpl;
import com.ibm.wsdl.BindingOutputImpl;
import com.ibm.wsdl.InputImpl;
import com.ibm.wsdl.MessageImpl;
import com.ibm.wsdl.OperationImpl;
import com.ibm.wsdl.OutputImpl;
import com.ibm.wsdl.PartImpl;
import com.ibm.wsdl.PortImpl;
import com.ibm.wsdl.PortTypeImpl;
import com.ibm.wsdl.ServiceImpl;
import com.ibm.wsdl.TypesImpl;
import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.store.TypedAggregateSchema;

public class WSDLUtils {
	
	private static final String INPUT = "INPUT";
	private static final String OUTPUT = "OUTPUT";
	
	// -----------------------------------------------------------------------------
	// Package-scope methods

	static XSDSchema createSchema(String tns) {

		XSDFactory xsdFactory = XSDFactory.eINSTANCE;
		XSDSchema xsd = xsdFactory.createXSDSchema();
		xsd.setSchemaForSchemaQNamePrefix(WSDLConstants.XSD);
		xsd.setTargetNamespace(tns);

		Map qNamePrefixToNamespaceMap = xsd.getQNamePrefixToNamespaceMap();
		qNamePrefixToNamespaceMap.put(WSDLConstants.XSD, xsd.getTargetNamespace());
		qNamePrefixToNamespaceMap.put(xsd.getSchemaForSchemaQNamePrefix(), WSDLConstants.XSD_URL);
		qNamePrefixToNamespaceMap.put(WSIGConfiguration.getInstance().getLocalNamespacePrefix(), tns);

		// Add annotation (without this xsd.getElement() is null)
		XSDAnnotation xsdAnnotation = xsdFactory.createXSDAnnotation();
		xsd.getContents().add(xsdAnnotation);
		xsdAnnotation.createUserInformation(null);
		
		return xsd;
	}

	static XSDTypeDefinition getSimpleOrComplexType(XSDSchema schema, String targetNameSpace, String typeName) {
		XSDTypeDefinition result = null;
		for (XSDTypeDefinition type : schema.getTypeDefinitions()) {
			if (type.hasNameAndTargetNamespace(typeName, targetNameSpace)) {
				result = type;
				break;
			}
		}
		return result;
	}

	static XSDComplexTypeDefinition createComplexType(String tns, String name) {
		XSDComplexTypeDefinition complexType = XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
		if (name != null) {
			complexType.setName(name);
		}
		if (tns != null) {
			complexType.setTargetNamespace(tns);
		}

		return complexType;
	}

	static XSDSimpleTypeDefinition createSimpleType(String tns, String name, XSDSimpleTypeDefinition simpleTypeDefinition) {
		XSDSimpleTypeDefinition simpleType = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
		
		if (simpleTypeDefinition != null) {
			simpleType.setBaseTypeDefinition(simpleTypeDefinition);
		}

		if (name != null) {
			simpleType.setName(name);
		}
		if (tns != null) {
			simpleType.setTargetNamespace(tns);
		}
		
		return simpleType;
	}
	
	static XSDElementDeclaration createElement(String tns, String name) {
		XSDElementDeclaration element = XSDFactory.eINSTANCE.createXSDElementDeclaration();
		if (name != null) {
			element.setName(name);
		}
		if (tns != null) {
			element.setTargetNamespace(tns);
		}
		
		return element;
	}

	static XSDSimpleTypeDefinition addSimpleTypeToSchema(String tns, XSDSchema schema, String simpleTypeName, XSDSimpleTypeDefinition simpleTypeDefinition) {
		XSDSimpleTypeDefinition simpleType = createSimpleType(tns, simpleTypeName, simpleTypeDefinition);
		schema.getContents().add(simpleType);

		return simpleType;
	}
	
	static XSDSimpleTypeDefinition addRestrictionToSimpleType(XSDSimpleTypeDefinition simpleType, Object[] permittedValues) {
		for(int i=0; i<permittedValues.length; i++) {
			XSDEnumerationFacet xsdEnumerationFacet = XSDFactory.eINSTANCE.createXSDEnumerationFacet();
			xsdEnumerationFacet.setLexicalValue((permittedValues[i]).toString());
			simpleType.getFacetContents().add(xsdEnumerationFacet);
		}
			
		return simpleType;
	}
	
	static XSDComplexTypeDefinition addComplexTypeToSchema(String tns, XSDSchema schema, String complexTypeName) {
		XSDComplexTypeDefinition complexType = createComplexType(tns, complexTypeName);
		schema.getContents().add(complexType);

		return complexType;
	}

	static XSDComplexTypeDefinition addComplexTypeToElement(XSDElementDeclaration element) {
		XSDComplexTypeDefinition complexType = createComplexType(null, null);
		element.setAnonymousTypeDefinition(complexType);

		return complexType;
	}
	
	static XSDElementDeclaration addElementToSchema(String tns, XSDSchema schema, String elementName) {
		
		XSDElementDeclaration element = createElement(null, elementName);
		schema.getContents().add(element);
		
		return element;
	}
	
	static XSDModelGroup addSequenceToComplexType(XSDComplexTypeDefinition complexTypeDefinition) {

		XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
		XSDModelGroup contentSequence = XSDFactory.eINSTANCE.createXSDModelGroup();
		contentSequence.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
		particle.setContent(contentSequence);
		complexTypeDefinition.setContent(particle);
		
		return contentSequence;
	}

	static XSDParticle addElementToSequence(String tns, XSDSchema schema, String elementName, String elementType, XSDModelGroup sequence) {
		XSDElementDeclaration element = createElement(null, elementName);
		
		XSDTypeDefinition complexType;
		if (isPrimitiveType(elementType)) {
			complexType = schema.resolveSimpleTypeDefinition(WSDLConstants.XSD_URL, elementType);
		} else {
			complexType = getSimpleOrComplexType(schema, tns, elementType);
			
			if (complexType == null) {
				// Not already present in definition type
				// Create a temp definition in my tns
				complexType = createComplexType(tns, elementType);
			}
		}
		element.setTypeDefinition(complexType);
		
		XSDParticle particle = XSDFactory.eINSTANCE.createXSDParticle();
		particle.setContent(element);
		sequence.getContents().add(particle);
		
		return particle;
	}

	static XSDParticle addElementToSequence(String tns, XSDSchema schema, String elementName, String elementType, XSDModelGroup sequence, Integer minOcc, Integer maxOcc) {
		XSDParticle particle = addElementToSequence(tns, schema, elementName, elementType, sequence);
		if (minOcc != null) {
			particle.setMinOccurs(minOcc);
		}
		if (maxOcc != null) {
			particle.setMaxOccurs(maxOcc);
		}
		return particle;
	}
	
	static Types createTypes(ExtensionRegistry registry, XSDSchema wsdlTypeSchema) throws WSDLException {
		Types types = new TypesImpl();

		Schema schema = (Schema) registry.createExtension(
							Types.class, new QName(WSDLConstants.XSD_URL,
							WSDLConstants.SCHEMA));
		schema.setElement(wsdlTypeSchema.getElement());
		types.addExtensibilityElement(schema);
		
		return types;
	}
	
	static String getLocalPart(String tns) {
		return tns.substring(tns.indexOf(":")+1);
	}
	
	static Definition createWSDLDefinition(WSDLFactory factory, String tns) {
		Definition definition = factory.newDefinition();
		definition.setQName(new QName(tns, getLocalPart(tns)));
		definition.setTargetNamespace(tns);
		definition.addNamespace(WSIGConfiguration.getInstance().getLocalNamespacePrefix(), tns);
		definition.addNamespace(WSDLConstants.XSD, WSDLConstants.XSD_URL);
		definition.addNamespace(WSDLConstants.XSI, WSDLConstants.XSI_URL);
		definition.addNamespace(WSDLConstants.WSDL_SOAP, WSDLConstants.WSDL_SOAP_URL);
		
		return definition;
	}
	
	static PortType createPortType(String tns) {
		PortType portType = new PortTypeImpl();
		portType.setUndefined(false);
		portType.setQName(new QName(getPortName(tns)));
		return portType;
	}
	
	static Binding createBinding(String tns) {
		Binding binding = new BindingImpl();
		PortType portType = new PortTypeImpl();
		portType.setUndefined(false);
		portType.setQName(new QName(tns, getPortName(tns)));
		binding.setPortType(portType);
		binding.setUndefined(false);
		binding.setQName(new QName(getBindingName(tns)));
		return binding;
	}

	static SOAPBinding createSOAPBinding(ExtensionRegistry registry, String soapStyle) throws WSDLException {
		SOAPBinding soapBinding = (SOAPBinding) registry.createExtension(
				Binding.class, new QName(WSDLConstants.WSDL_SOAP_URL, WSDLConstants.BINDING));
		soapBinding.setStyle(soapStyle);
		soapBinding.setTransportURI(WSDLConstants.TRANSPORT_URL);
		return soapBinding;
	}
	
	static Port createPort(String tns) {
		Binding bindingP = new BindingImpl();
		bindingP.setQName(new QName(tns, tns.substring(4)+WSDLConstants.BINDING_SUFFIX));
		bindingP.setUndefined(false);
		Port port = new PortImpl();
		port.setName(getPortName(tns));
		port.setBinding(bindingP);
		return port;
	}
	
	static SOAPAddress createSOAPAddress(ExtensionRegistry registry, String serviceName) throws WSDLException, MalformedURLException {
		SOAPAddress soapAddress = null;
		soapAddress = (SOAPAddress)registry.createExtension(Port.class,new QName(WSDLConstants.WSDL_SOAP_URL, "address"));		
		soapAddress.setLocationURI(WSIGConfiguration.getInstance().getServicesUrl(null)+"/"+serviceName);
		return soapAddress;
	}
	
	static Service createService(String tns) {
		Service service = new ServiceImpl();
		service.setQName(new QName(getServiceName(tns)));
		return service;
	}

	static Operation createOperation(String actionName) {
		Operation operation = new OperationImpl();
		operation.setName(actionName);
		operation.setUndefined(false);
		return operation;
	}

	static BindingOperation createBindingOperation(ExtensionRegistry registry, String tns, String actionName) throws WSDLException {

		BindingOperation operationB = new BindingOperationImpl();
		operationB.setName(actionName);
		SOAPOperation soapOperation = (SOAPOperation) registry
				.createExtension(BindingOperation.class,
						new QName(WSDLConstants.WSDL_SOAP_URL, WSDLConstants.OPERATION));
		soapOperation.setSoapActionURI(getActionName(tns));
		operationB.addExtensibilityElement(soapOperation);
		return operationB;
	}

	static BindingInput createBindingInput(ExtensionRegistry registry, String tns, String soapUse) throws Exception{
		
		return (BindingInput)createBinding(registry, tns, soapUse, INPUT);
	}
	
	static BindingOutput createBindingOutput(ExtensionRegistry registry, String tns, String soapUse) throws Exception  {

		return (BindingOutput)createBinding(registry, tns, soapUse, OUTPUT);
	}

	static Message createMessage(String tns, String name) {
		Message messageIn = new MessageImpl();
		messageIn.setQName(new QName(tns, name));
		messageIn.setUndefined(false);
		return messageIn;
	}

	static Input createInput(Message messageIn) {
		Input input = new InputImpl();
		input.setMessage(messageIn);
		return input;
	}

	static Output createOutput(Message messageOut) {
		Output output = new OutputImpl();
		output.setMessage(messageOut);
		return output;
	}
	
	static Part createTypePart(String name, String type, String tns) {
		Part part = new PartImpl();
		String namespaceURI;
		if (isPrimitiveType(type)) {
			namespaceURI = WSDLConstants.XSD_URL;
		} else {
			namespaceURI = tns;
		}
		
		QName qNameType = new QName(namespaceURI, type);
		part.setTypeName(qNameType);
		part.setName(name);
		return part;
	}
	
	static Part createElementPart(String name, String elementName, String tns) {
		QName qNameElement = new QName(tns, elementName);
		Part part = new PartImpl();
		part.setElementName(qNameElement);
		part.setName(name);
		return part;
	}
	
	static String getResponseName(String operationName) {
		return operationName+WSDLConstants.RESPONSE_SUFFIX;
	}

	static String getRequestName(String operationName) {
		return operationName+WSDLConstants.REQUEST_SUFFIX;
	}

	static Object[] getPermittedValues(ObjectSchema containerSchema, String slotName) {
		
		Object[] permittedValues = null;
		Facet[] facets = getSlotFacets(containerSchema, slotName);
		if (facets != null) {
			for (Facet facet : facets) {
				if (facet instanceof PermittedValuesFacet) {
					permittedValues = ((PermittedValuesFacet) facet).getPermittedValues();
				} 
			}
		}
		
		return permittedValues;
	}
	
	public static int getAggregateCardMin(ObjectSchema containerSchema, String slotName) {
		
		int cardMin = 0;
		Facet[] facets = getSlotFacets(containerSchema, slotName);
		if (facets != null) {
			for (Facet facet : facets) {
				if (facet instanceof CardinalityFacet) {
					cardMin = ((CardinalityFacet) facet).getCardMin();
				} 
			}
		}
		
		return cardMin;
	}

	public static int getAggregateCardMax(ObjectSchema containerSchema, String slotName) {
		
		int cardMax = -1;
		Facet[] facets = getSlotFacets(containerSchema, slotName);
		if (facets != null) {
			for (Facet facet : facets) {
				if (facet instanceof CardinalityFacet) {
					cardMax = ((CardinalityFacet) facet).getCardMax();
				} 
			}
		}
		
		return cardMax;
	}

	static String getJavaType(ObjectSchema containerSchema, String slotName) {
		
		String javaType = null;
		Facet[] facets = getSlotFacets(containerSchema, slotName);
		if (facets != null) {
			for (Facet facet : facets) {
				if (facet instanceof JavaTypeFacet) {
					javaType = ((JavaTypeFacet) facet).getJavaType();
				} 
			}
		}
		
		return javaType;
	}
	
	static void writeWSDL(WSDLFactory factory, Definition definition, String serviceName) throws Exception {
		String fileName = WSIGConfiguration.getInstance().getWsdlDirectory() + File.separator + serviceName + ".wsdl";
		WSDLWriter writer = factory.newWSDLWriter();
		File file = new File(fileName);
		PrintWriter output = new PrintWriter(file);
		writer.writeWSDL(definition, output);
	}
	
	
	
	// -----------------------------------------------------------------------------
	// Public methods
	
	public static Slot getSlotAnnotation(Annotation[] annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof Slot) {
					return (Slot)annotation;
				}
			}
		}
		return null;
	}

	public static SuppressSlot getSuppressSlotAnnotation(Annotation[] annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof SuppressSlot) {
					return (SuppressSlot)annotation;
				}
			}
		}
		return null;
	}
	
	public static AggregateSlot getAggregateSlotAnnotation(Annotation[] annotations) {
		if (annotations != null) {
			for (Annotation annotation : annotations) {
				if (annotation instanceof AggregateSlot) {
					return (AggregateSlot)annotation;
				}
			}
		}
		return null;
	}
	
    public static String[] getParameterNames(Method method) {
        // Don't worry about it if there are no params.
        int numParams = method.getParameterTypes().length;
        if (numParams == 0)
            return null;

        // Get declaring class
        Class c = method.getDeclaringClass();
        
        // Don't worry about it if the class is a Java dynamic proxy 
        if(Proxy.isProxyClass(c)) {
            return null;
        }
        
        try {
            // Get a parameter reader
            ParamReader pr = new ParamReader(c);

            // Get the parameter names
            return pr.getParameterNames(method);
            
        } catch (IOException e) {
        	return null;
        }
    }
	
	public static String getResultName(String operationName) { 
		return operationName+WSDLConstants.RETURN_SUFFIX;
	}

	public static TypedAggregateSchema getTypedAggregateSchema(ObjectSchema containerSchema, String slotName) throws OntologyException  {
		String typeName = containerSchema.getSchema(slotName).getTypeName();
		ObjectSchema elementType = getAggregateElementSchema(containerSchema, slotName);
		return new TypedAggregateSchema(typeName, elementType);
	}	

	public static ObjectSchema getAggregateElementSchema(ObjectSchema containerSchema, String slotName) {
		
		ObjectSchema elementSchema = null;
		Facet[] facets = getSlotFacets(containerSchema, slotName);
		if (facets != null) {
			for (Facet facet : facets) {
				if (facet instanceof TypedAggregateFacet) {
					elementSchema = ((TypedAggregateFacet) facet).getType();
				}
			}
		}
		
		return elementSchema;
	}

	public static String getAggregateType(String elementType, Integer cardMin, Integer cardMax) {
		char[] uppercaseElementType = elementType.toCharArray();
		uppercaseElementType[0] = Character.toUpperCase(uppercaseElementType[0]);

		String min = "0";
		if (cardMin != null) {
			min = cardMin.toString(); 
		}
		String max = "U";
		if (cardMax != null && cardMax.intValue() != -1) {
			max = cardMax.toString(); 
		}
		String cardinality = "";
		if (!min.equals("0") || !max.equals("U")) {
			cardinality = "_"+min+"_"+max;
		}
		
		return WSDLConstants.ARRAY_OF + String.valueOf(uppercaseElementType) + cardinality;
	}

	public static String getWsdlUrl(String serviceName, HttpServletRequest request) {
		String wsdlUrl = null;
		try {
			wsdlUrl = WSIGConfiguration.getInstance().getServicesUrl(request)+"/"+serviceName+"?WSDL";
		} catch (MalformedURLException e) {}
		return wsdlUrl;
	}

	public static String getPrimitiveType(ObjectSchema objSchema, ObjectSchema containerSchema, String slotName) {
		String slotType = WSDLConstants.jade2xsd.get(objSchema.getTypeName());
		if (slotType == null) {
			slotType = objSchema.getTypeName();
		}
	
		if (containerSchema != null) {
			// Check java-type to preserve the type
			String javaType = WSDLUtils.getJavaType(containerSchema, slotName);
			if (javaType != null && "true".equalsIgnoreCase(System.getProperty(SLCodec.PRESERVE_JAVA_TYPES))) {
				if (long.class.getName().equals(javaType) || Long.class.getName().equals(javaType)) {
					slotType = WSDLConstants.XSD_LONG;
				}
				else if (double.class.getName().equals(javaType) || Double.class.getName().equals(javaType)) {
					slotType = WSDLConstants.XSD_DOUBLE;
				}
			}
		}
		
		return slotType;
	}	

	// -----------------------------------------------------------------------------
	// Private methods

	private static boolean isPrimitiveType(String type) {
		return 	WSDLConstants.jade2xsd.values().contains(type) || 
				WSDLConstants.XSD_LONG.equals(type) ||
				WSDLConstants.XSD_DOUBLE.equals(type);
	}
	
	private static ElementExtensible createBinding(ExtensionRegistry registry, String tns, String soapUse, String type) throws Exception{

		SOAPBody soapBody;
		ElementExtensible binding;
		if (INPUT.equals(type)) {
			binding = new BindingInputImpl();
		} else {
			binding = new BindingOutputImpl();
		}
		
		try {
			soapBody = (SOAPBody) registry.createExtension(BindingInput.class,
							new QName(WSDLConstants.WSDL_SOAP_URL, WSDLConstants.BODY));
		} catch (WSDLException e) {
			throw new Exception("Error in SOAPBodyInput Handling", e);
		}
		soapBody.setUse(soapUse);
		if (WSDLConstants.USE_ENCODED.equals(soapUse)) {
			ArrayList encodingStylesInput = new ArrayList();
			encodingStylesInput.add(WSDLConstants.ENCODING_URL);
			soapBody.setEncodingStyles(encodingStylesInput);
			soapBody.setNamespaceURI(tns);
		}
		binding.addExtensibilityElement(soapBody);
		return binding;
	}
	
	private static String getActionName(String tns) {
		return tns+WSDLConstants.ACTION_SUFFIX;
	}
	
	private static String getBindingName(String tns) {
		return getLocalPart(tns)+WSDLConstants.BINDING_SUFFIX;
	}

	private static String getServiceName(String tns) {
		return getLocalPart(tns)+WSDLConstants.SERVICE_SUFFIX;
	}
	
	private static String getPortName(String tns) {
		return getLocalPart(tns)+WSDLConstants.PORT_TYPE_SUFFIX;
	}

	private static Facet[] getSlotFacets(ObjectSchema containerSchema, String slotName) {
		
		Facet[] facets = containerSchema.getFacets(slotName);
		
		// If there are no facets get result facets
		if ((facets == null || facets.length == 0) && containerSchema instanceof AgentActionSchema) {
			facets = ((AgentActionSchema)containerSchema).getResultFacets();
		}
		
		return facets;
	}
}
