package com.whitestein.wsig.ws;

import org.eclipse.xsd.*;
import org.eclipse.xsd.impl.XSDComplexTypeDefinitionImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import java.util.Map;
import java.util.List;
import javax.wsdl.Types;
import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.extensions.schema.Schema;
import javax.xml.namespace.QName;

import com.ibm.wsdl.TypesImpl;
import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.extensions.schema.SchemaImpl;

/**
 * Created by IntelliJ IDEA.
 * User: ue_marchetti
 * Date: 19-gen-2007
 * Time: 15.10.06
 * To change this template use File | Settings | File Templates.
 */
public class SchemaGeneratorUtils {

	public static XSDSchema createSchema() {

		XSDFactory xsdFactory = XSDFactory.eINSTANCE;
		//Schema Initialization
		XSDSchema xsd = xsdFactory.createXSDSchema();
		xsd.setSchemaForSchemaQNamePrefix("xsd");
		xsd.setTargetNamespace("http://www.w3.org/2001/XMLSchema");


		Map qNamePrefixToNamespaceMap = xsd.getQNamePrefixToNamespaceMap();
		qNamePrefixToNamespaceMap.put("xsd", xsd.getTargetNamespace());
		qNamePrefixToNamespaceMap.put(xsd.getSchemaForSchemaQNamePrefix(), XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
		XSDAnnotation xsdAnnotation = xsdFactory.createXSDAnnotation();
		xsd.getContents().add(xsdAnnotation);

		Element documentation = xsdAnnotation.createUserInformation(null);

		return xsd;
	}

	public static XSDTypeDefinition getTypeDefinition(XSDSchema schema, String targetNameSpace, String localName) {
		XSDTypeDefinition result = null;
		for (XSDTypeDefinition type : schema.getTypeDefinitions()) {
			if (type.hasNameAndTargetNamespace(localName, targetNameSpace)) {
				result = type;
				break;
			}
		}
		return result;
	}

	public static XSDComplexTypeDefinition addComplexTypeToSchema(XSDSchema schema, String complexTypeName) {
		XSDComplexTypeDefinitionImpl simpleRecursiveComplexTypeDefinition = (XSDComplexTypeDefinitionImpl) XSDFactory.eINSTANCE.createXSDComplexTypeDefinition();
		simpleRecursiveComplexTypeDefinition.setName(complexTypeName);
		schema.getContents().add(simpleRecursiveComplexTypeDefinition);

		return simpleRecursiveComplexTypeDefinition;

	}

	public static XSDModelGroup addSequenceToComplexType(XSDComplexTypeDefinition complexTypeDefinition) {

		XSDParticle contentParticle = XSDFactory.eINSTANCE.createXSDParticle();
		XSDModelGroup contentSequence = XSDFactory.eINSTANCE.createXSDModelGroup();
		contentSequence.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
		contentParticle.setContent(contentSequence);
		complexTypeDefinition.setContent(contentParticle);
		return contentSequence;

	}

	public static void addElementToSequence(XSDSchema schema, String elementName, String elementType, XSDModelGroup sequence) {
		XSDElementDeclaration elementAdded = XSDFactory.eINSTANCE.createXSDElementDeclaration();
		elementAdded.setName(elementName);
		elementAdded.setTypeDefinition(schema.resolveSimpleTypeDefinition(elementType));
		XSDParticle elementParticle = XSDFactory.eINSTANCE.createXSDParticle();
		elementParticle.setContent(elementAdded);
		sequence.getContents().add(elementParticle);

	}


	public static void addElementToSequence(XSDSchema schema, String elementName, String elementType, XSDModelGroup sequence, int minOcc, int maxOcc) {
		XSDElementDeclaration elementAdded = XSDFactory.eINSTANCE.createXSDElementDeclaration();
		elementAdded.setName(elementName);
		elementAdded.setTypeDefinition(schema.resolveSimpleTypeDefinition(elementType));
		XSDParticle elementParticle = XSDFactory.eINSTANCE.createXSDParticle();
		if (minOcc != -1) {
			elementParticle.setMaxOccurs(maxOcc);
			elementParticle.setMinOccurs(minOcc);
		}
		elementParticle.setContent(elementAdded);
		sequence.getContents().add(elementParticle);

	}
}
