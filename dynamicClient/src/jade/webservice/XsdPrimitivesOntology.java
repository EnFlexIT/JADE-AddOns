/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package jade.webservice;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.message.MessageElement;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import jade.content.abs.AbsExtendedPrimitive;
import jade.content.abs.AbsObject;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.UnknownSchemaException;
import jade.content.schema.PrimitiveSchema;

//#APIDOC_EXCLUDE_FILE

/**
 * This class implements an ontology containing schemas for 
 * ExtendedPrimitive types 
 */
public class XsdPrimitivesOntology extends Ontology {

	// The singleton instance of this ontology
	private static final XsdPrimitivesOntology theInstance = new XsdPrimitivesOntology();
	static {
		theInstance.initialize();
	}

	/**
	 * Constructor
	 */
	private XsdPrimitivesOntology() {
		super("XSD_PRIMITIVES_ONTOLOGY", (Ontology) null, null);
	}

	private void add(Class c) throws OntologyException {
		add(new PrimitiveSchema(c.getName()), c);
	}
	
	private void initialize() {
		try {
			// Schemas for extended-primitives
			add(java.math.BigInteger.class);
			add(java.math.BigDecimal.class);
			add(java.lang.Short.class);
			add(java.lang.Byte.class);
			add(short.class);
			add(byte.class);
			add(java.sql.Date.class);
			
			// Schema for wsdl <Any/> tag 
			add(org.apache.axis.message.MessageElement.class);
		} 
		catch (OntologyException oe) {
			oe.printStackTrace();
		} 
	}
	
	/**
	 * Returns the singleton instance of the <code>XsdPrimitivesOntology</code>.
	 * @return the singleton instance of the <code>XsdPrimitivesOntology</code>
	 */
	public static Ontology getInstance() {
		return theInstance;
	}

	/**
	 * This method is redefined as XsdPrimitivesOntology does not use an
	 * Introspector for performance reason
	 * @see Ontology#toObject(AbsObject)
	 */
	protected Object toObject(AbsObject abs, String lcType, Ontology referenceOnto) throws UngroundedException, OntologyException {
		if (abs == null) {
			return null;
		} 

		if (abs.getAbsType() == AbsExtendedPrimitive.ABS_EXTENDED_PRIMITIVE) {
			return ((AbsExtendedPrimitive) abs).get();
		} 

		throw new UnknownSchemaException();
	}

	/**
	 * This method is redefined as XsdPrimitivesOntology does not use an
	 * Introspector for performance reason
	 * @see Ontology#toObject(AbsObject)
	 */
	protected AbsObject fromObject(Object obj, Ontology referenceOnto) throws OntologyException{
		if (obj == null) {
			return null;
		} 
		
		if (getSchema(obj.getClass()) != null) {
			return AbsExtendedPrimitive.wrap(obj);
		} 

		throw new UnknownSchemaException();
	}
	
	/**
	 * Convert a string containing a xml into a org.apache.axis.message.MessageElement
	 * Used to manage the wsdl tag <Any/>  
	 */
	public static MessageElement xml2MessageElement(String xml) throws Exception {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document XMLDoc = documentBuilder.parse(new InputSource(new StringReader(xml)));
		return new MessageElement(XMLDoc.getDocumentElement());
	}

	/**
	 * Convert a org.apache.axis.message.MessageElement into string containing an xml 
	 * Used to manage the wsdl tag <Any/>  
	 */
	public static String messageElement2Xml(MessageElement messageElement) throws Exception {
		return messageElement.getAsString();
	}
}
