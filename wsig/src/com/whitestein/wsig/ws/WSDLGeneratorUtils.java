package com.whitestein.wsig.ws;

import org.w3c.dom.Element;

import javax.wsdl.Definition;
import javax.wsdl.Types;
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

}
