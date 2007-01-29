package com.whitestein.wsig.ws;

import java.util.ArrayList;
import java.util.Iterator;

import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import org.w3c.dom.Element;

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
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;

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
import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.schema.SchemaImpl;
import com.whitestein.wsig.WSIGConstants;

/**
 * Created by IntelliJ IDEA.
 * User: ue_marchetti
 * Date: 19-gen-2007
 * Time: 16.05.24
 * To change this template use File | Settings | File Templates.
 */
public class WSDLGeneratorUtils {

	public static void addTypeToDefinition(Definition definition, Element element) {
		Types ts = new TypesImpl();
		definition.setTypes(ts);
		ExtensionRegistry reg = new PopulatedExtensionRegistry();
		definition.setExtensionRegistry(reg);
		Schema schema = new SchemaImpl();
		schema.setElement(element);
		schema.setElementType(new QName("http://www.w3.org/2001/XMLSchema", "schema"));
		ts.addExtensibilityElement(schema);
	}
	
	public static Class getMapperClass(ServiceDescription sd) throws Exception{
		Property p = null;
		Iterator it = sd.getAllProperties();
		
		boolean mapperFound = false;
		String mapperClassName = null;
		while (it.hasNext() && !mapperFound) {
			p = (Property) it.next();
			if (WSIGConstants.WSIG_MAPPER.equalsIgnoreCase(p.getName())) {
				mapperClassName = (String)p.getValue();
				mapperFound = true;
			}
		}
		if(!mapperFound)
			return null;
		
		Class mapperClass = null;
		try {
			mapperClass = Class.forName(mapperClassName);
		} catch (ClassNotFoundException e2) {
			throw new Exception("Class "+mapperClassName+" not found!");
		}
		return mapperClass;
	}

	public static Definition createWSDLDefinition(WSDLFactory factory,String tns) {
		Definition definition = null;
		
		definition = factory.newDefinition();
		definition.setQName(new QName(tns, tns.substring(tns.indexOf(":")+1)));
		definition.setTargetNamespace(tns);
		definition.addNamespace("tns", tns);
		definition.addNamespace("xsd", WSDLConstants.xsd);
		definition.addNamespace("wsdlsoap", WSDLConstants.wsdlsoap);
		
		return definition;
	}
	
	public static PortType createPortType(ServiceDescription sd) {
		PortType portType = new PortTypeImpl();
		portType.setUndefined(false);
		portType.setQName(new QName(sd.getName()));
		return portType;
	}
	
	public static Binding createBinding(String tns) {
		Binding binding = new BindingImpl();
		PortType portTypeB = new PortTypeImpl();
		portTypeB.setUndefined(false);
		portTypeB.setQName(new QName(tns, tns.substring(tns.indexOf(":")+1)));
		binding.setPortType(portTypeB);
		binding.setUndefined(false);
		binding.setQName(new QName(WSDLConstants.publishSoapBinding));
		return binding;
	}

	public static SOAPBinding createSOAPBinding(ExtensionRegistry registry) throws WSDLException {
		SOAPBinding soapBinding = (SOAPBinding) registry.createExtension(
				Binding.class, new QName(WSDLConstants.wsdlsoap, "binding"));
		soapBinding.setStyle(WSDLConstants.soapStyle);
		soapBinding.setTransportURI(WSDLConstants.transportURI);
		return soapBinding;
	}
	
	public static Port createPort(String tns) {
		Binding bindingP = new BindingImpl();
		bindingP.setQName(new QName(tns, WSDLConstants.publishSoapBinding));
		bindingP.setUndefined(false);
		Port port = new PortImpl();
		port.setName(WSDLConstants.portPublish); // BOH!!
		port.setBinding(bindingP);
		return port;
	}
	
	public static SOAPAddress createSOAPAddress(ExtensionRegistry registry) throws WSDLException {
		SOAPAddress soapAddress = null;
		soapAddress = (SOAPAddress)registry.createExtension(Port.class,new QName(WSDLConstants.wsdlsoap,"address"));		
		soapAddress.setLocationURI(WSDLConstants.locationURI);
		return soapAddress;
	}

	public static Service createService(String name) {
		Service service = new ServiceImpl();
		service.setQName(new QName(name));
		return service;
	}

	public static Operation createOperation(String actionName) {
		Operation operation = new OperationImpl();
		operation.setName(actionName);
		operation.setUndefined(false);
		return operation;
	}

	public static Message createMessage(String tns,String name) {
		Message messageOut = new MessageImpl();
		messageOut.setQName(new QName(tns, name));
		messageOut.setUndefined(false);
		return messageOut;
	}

	public static Output createOutput(String name) {
		Output output = new OutputImpl();
		output.setName(name);
		return output;
	}
	
	public static BindingOperation createBindingOperation(ExtensionRegistry registry, String actionName) throws WSDLException {
		// Operation for binding
		BindingOperation operationB = new BindingOperationImpl();
		operationB.setName(actionName);
		
		// SOAP Operation
		SOAPOperation soapOperation = (SOAPOperation) registry
				.createExtension(BindingOperation.class,
						new QName(WSDLConstants.wsdlsoap, WSDLConstants.QNameOperation));
		soapOperation.setSoapActionURI(WSDLConstants.soapActionURI);
		operationB.addExtensibilityElement(soapOperation);
		return operationB;
	}
	
	public static BindingInput createBindingInput(ExtensionRegistry registry, String tns, String name) throws Exception{
		
		BindingInput inputB = new BindingInputImpl();
		inputB.setName(name);
		// SOAP BODY INPUT
		SOAPBody soapBodyInput;
		try {
			soapBodyInput = (SOAPBody) registry.createExtension(BindingInput.class,
							new QName(WSDLConstants.wsdlsoap, WSDLConstants.QNamebody));
		} catch (WSDLException e) {
			throw new Exception("Error in SOAPBodyInput Handling "+e.getMessage());
		}
		soapBodyInput.setUse(WSDLConstants.bodyInputUse);
		ArrayList encodingStylesInput = new ArrayList();
		encodingStylesInput.add(WSDLConstants.bodyEncodingStyle);
		soapBodyInput.setEncodingStyles(encodingStylesInput);
		soapBodyInput.setNamespaceURI(tns);
		inputB.addExtensibilityElement(soapBodyInput);
		return inputB;
	}
	

	public static BindingOutput createBindingOutput(ExtensionRegistry registry,
			String tns, String name) throws Exception  {

		BindingOutput outputB = new BindingOutputImpl();
		outputB.setName(name);
		// SOAP BODY OUTPUT
		SOAPBody soapBodyOutput;
		try {
			soapBodyOutput = (SOAPBody) registry.createExtension(
					BindingOutput.class, new QName(WSDLConstants.wsdlsoap, WSDLConstants.QNamebody));
		} catch (WSDLException e) {
			throw new Exception("Error in SOAPBodyOutput Handling "+e.getMessage());
		}
		soapBodyOutput.setUse(WSDLConstants.bodyInputUse);
		ArrayList encodingStylesOutput = new ArrayList();
		encodingStylesOutput.add(WSDLConstants.bodyEncodingStyle);
		soapBodyOutput.setEncodingStyles(encodingStylesOutput);
		soapBodyOutput.setNamespaceURI(tns);
		outputB.addExtensibilityElement(soapBodyOutput);
		return outputB;
	}

	public static Message createMessageIn(String tns, String name) {
		Message messageIn = new MessageImpl();
		messageIn.setQName(new QName(tns, name));
		messageIn.setUndefined(false);
		return messageIn;
	}

	public static Input createInput(Message messageIn, String name) {
		Input input = new InputImpl();
		input.setMessage(messageIn);
		input.setName(name);
		return input;
	}

	public static Part createPart(String name, String className) {
		Part part = new PartImpl();
		QName qNameType = new QName(WSDLConstants.xsd, className);
		part.setTypeName(qNameType);
		part.setName(name);
		return part;
	}
	

}
