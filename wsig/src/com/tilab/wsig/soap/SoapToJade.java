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

import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;
import jade.content.abs.AbsTerm;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.schema.AggregateSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.TermSchema;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.Message;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.tilab.wsig.WSIGConstants;
import com.tilab.wsig.store.ActionBuilder;
import com.tilab.wsig.store.ParameterInfo;
import com.tilab.wsig.store.TypedAggregateSchema;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.wsdl.WSDLConstants;
import com.tilab.wsig.wsdl.WSDLUtils;

public class SoapToJade extends DefaultHandler {

	private static final int PARAMETERS_LEVEL = 3;
	
	private static Logger log = Logger.getLogger(SoapToJade.class.getName());

	private XMLReader xmlParser;
	private int level = 0;
	private Ontology onto;
	private StringBuffer elementValue = new StringBuffer();
	private Vector<Vector<ParameterInfo>> parametersByLevel = new Vector<Vector<ParameterInfo>>();
	private Vector<ObjectSchema> schemaByLevel = new Vector<ObjectSchema>();
	private Map<String, ParameterInfo> parametersInfo;
	
	public SoapToJade() {
		
		// Get xml parser
	    try { 
	    	String parserName = getSaxParserName();
	    	
			xmlParser = (XMLReader)Class.forName(parserName).newInstance();
			xmlParser.setContentHandler(this);
			xmlParser.setErrorHandler(this);
		}
	    catch(Exception e) {
			log.error("Unable to create XML parser", e);
		}
	}

	private static String getSaxParserName() throws Exception {
		
		String saxFactory = System.getProperty( "org.xml.sax.driver" );
		if( saxFactory != null ) {
			// SAXParser specified by means of the org.xml.sax.driver Java option
			return saxFactory;
		}
		else {
			// Use the JVM default SAX Parser
			SAXParserFactory newInstance = SAXParserFactory.newInstance();
			SAXParser newSAXParser = newInstance.newSAXParser();
			XMLReader reader = newSAXParser.getXMLReader();
			String name = reader.getClass().getName();
			return name;
		}
	}
	
	public Object convert(Message soapRequest, WSIGService wsigService, String operationName) throws Exception {

		Object actionObj = null;
		String soapBodyMessage = soapRequest.getSOAPBody().toString();
		
		// Verify if parser is ready
		if (xmlParser == null) {
			log.error("XML parser not initialized");
			throw new Exception("XML parser not initialized");
		}

		// Get action builder
		ActionBuilder actionBuilder = wsigService.getActionBuilder(operationName);
		if (actionBuilder == null) {
			log.error("Operation "+operationName+" not present in service "+wsigService.getServiceName());
			throw new Exception("Operation "+operationName+" not present in service "+wsigService.getServiceName()); 
		}
		
		// Get ontology
		onto = wsigService.getServiceOntology();
		
		// Get parameters schema map
		parametersInfo = actionBuilder.getParameters();
		
		// Parse soap to extract parameters value
		xmlParser.parse(new InputSource(new StringReader(soapBodyMessage)));

		// Prepare jade action
		actionObj = actionBuilder.getAgentAction(getParameterValues());
		
		return actionObj;
	}
	
	private LinkedHashMap<String, ParameterInfo> getParameterValues() {
		log.debug("Begin parameters list");

		LinkedHashMap<String, ParameterInfo> params = new LinkedHashMap<String, ParameterInfo>();
		if (parametersByLevel.size() >= 1) {
			Vector<ParameterInfo> soapParams = parametersByLevel.get(0);
			for (ParameterInfo soapParam : soapParams) {
				params.put(soapParam.getName(), soapParam);
				log.debug("   "+soapParam.getName()+"= "+soapParam.getValue());
			}
		} else {
			log.debug("   No parameters");
		}
		log.debug("End parameters list");
		
		return params;
	}

	private TermSchema getParameterSchema(String elementName, int level) throws Exception {

		try {
			TermSchema schema = null;
			if (level == 0) {
				
				// First level -> get schema from map (Primitive, Concept or TypedAggregate)
				schema = parametersInfo.get(elementName).getSchema();
			} else {
				
				// Other level -> get schema from parent
				ObjectSchema parentSchema = schemaByLevel.get(level-1);

				if (parentSchema instanceof TypedAggregateSchema) {
					// If is an aggregate get schema of content element
					schema = (TermSchema)((TypedAggregateSchema)parentSchema).getElementSchema();
				} else {
					// If is a Concept or a Primitive get schema of the slot
					schema = (TermSchema)parentSchema.getSchema(elementName);
				}
				
				// For aggregate wrap schema with TypedAggregateSchema  
				if (schema instanceof AggregateSchema) {
					schema = WSDLUtils.getTypedAggregateSchema(parentSchema, elementName);
				}
			}
			
			// Add schema to stack 
			schemaByLevel.add(level, schema);
			
			return schema;
			
		} catch(Exception e) {
			log.error("Schema not found for element "+elementName, e);
			throw e;
		}
	}

	private Vector<ParameterInfo> getParametersByLevel(int level, boolean addIfNotExist) throws Exception {
		
		if (!addIfNotExist && parametersByLevel.size() <= level) {
			throw new Exception("Parameters not present for level "+level);
		}
		
		Vector<ParameterInfo> parameters = null;
		if (parametersByLevel.size() <= level) {
			parameters = new Vector<ParameterInfo>();
			parametersByLevel.add(level, parameters);
		} else {
			parameters = parametersByLevel.get(level);
		}
		return parameters;
	}
	
	private ParameterInfo getLastParameterInfo(int level, String verifyName) throws Exception {
		
		Vector<ParameterInfo> parameters = parametersByLevel.get(level);
		
		// Get parameter info
		ParameterInfo pi = parameters.lastElement();

		// Verify...
		if (verifyName != null) {
			if (!pi.getName().equals(verifyName)) {
				throw new Exception("Parameter "+verifyName+" doesn't match with expected parameter ("+pi.getName()+")");
			}
		}
		return pi;
	}	
	
	public static AbsPrimitive getPrimitiveAbsValue(ObjectSchema schema, String value) throws Exception {
		
		String typeName = schema.getTypeName();
		AbsPrimitive absObj = null;
		
		// Get jade primitive
		if(BasicOntology.STRING.equals(typeName)) {
			absObj = AbsPrimitive.wrap(value);
		} else if(BasicOntology.BOOLEAN.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Boolean.parseBoolean(value));
		} else if(BasicOntology.FLOAT.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Float.parseFloat(value));
		} else if(BasicOntology.INTEGER.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Integer.parseInt(value));
		} else if(BasicOntology.DATE.equals (typeName)) {
			absObj = AbsPrimitive.wrap(WSIGConstants.ISO8601_DATE_FORMAT.parse(value));			
		} else if(BasicOntology.BYTE_SEQUENCE.equals (typeName)) {
			absObj = AbsPrimitive.wrap(value.getBytes());			
		}
		
		// Get java primitive
		  else if(WSDLConstants.XSD_STRING.equals(typeName)) {
			absObj = AbsPrimitive.wrap(value);
		} else if(WSDLConstants.XSD_BOOLEAN.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Boolean.parseBoolean(value));
		} else if(WSDLConstants.XSD_FLOAT.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Float.parseFloat(value));
		} else if(WSDLConstants.XSD_INT.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Integer.parseInt(value));
		} else if(WSDLConstants.XSD_DATETIME.equals (typeName)) {
			absObj = AbsPrimitive.wrap(WSIGConstants.ISO8601_DATE_FORMAT.parse(value));			
		} else if(WSDLConstants.XSD_DOUBLE.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Double.parseDouble(value));
		} else if(WSDLConstants.XSD_LONG.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Long.parseLong(value));
		} else if(WSDLConstants.XSD_SHORT.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Short.parseShort(value));
		} else if(WSDLConstants.XSD_BYTE.equals(typeName)) {
			absObj = AbsPrimitive.wrap(Byte.parseByte(value));
		} 
		
		// No primitive type
		  else {
			throw new Exception(typeName+" is not a primitive type");
		}
	
		return absObj;
	}
	
	
	
	//-------- PARSER EVENT HANDLERS -----------------//

	public void startElement (String uri, String parameterName, String qName, Attributes attrs) {

		try {
			elementValue.setLength(0);
			++level;

			// Manage only parameters levels
			if (level >= PARAMETERS_LEVEL) {

				// Get  parameter level
				int parameterLevel = level - PARAMETERS_LEVEL; 

				// Get parameter schema
				TermSchema parameterSchema = getParameterSchema(parameterName, parameterLevel);
				log.debug("Start managing parameter "+parameterName+" of type "+parameterSchema.getTypeName());

				// Get parameters vector for this level
				Vector<ParameterInfo> parameters = getParametersByLevel(parameterLevel, true);

				// Create new ParameterInfo for this soap parameter
				parameters.add(new ParameterInfo(parameterName, parameterSchema));
			}

		} catch(Exception e) {
			level = 0;
			throw new RuntimeException("Error parsing element "+parameterName+" - "+e.getMessage(), e);
		}
	}

	public void endElement (String uri, String parameterName, String qName) {
		
		try {
			// Manage only parameters levels
			if (level >= PARAMETERS_LEVEL) {

				// Get parameter value
				String parameterValue = elementValue.toString();

				// Get  parameter level
				int parameterLevel = level - PARAMETERS_LEVEL; 

				// Get parameter info & verify...
				ParameterInfo pi = getLastParameterInfo(parameterLevel, parameterName);

				// Get parameter schema 
				ObjectSchema parameterSchema = pi.getSchema();

				// Manage parameter
				if (parameterSchema instanceof PrimitiveSchema) {
					// Primitive type
					pi.setValue(getPrimitiveAbsValue(parameterSchema, parameterValue));
					log.debug("Set "+parameterName+" with " + parameterValue);
					
				} else {
					// Complex type -> create abs object from schema
					AbsObject absObj = parameterSchema.newInstance();
					
					Class parameterClass = onto.getClassForElement(parameterSchema.getTypeName());
					if (parameterClass != null && parameterClass.isEnum()) {
						// Enum type
						AbsPrimitive enumValue = getPrimitiveAbsValue(new PrimitiveSchema(WSDLConstants.XSD_STRING), parameterValue);
						AbsHelper.setAttribute(absObj, WSDLConstants.ENUM_SLOT_NAME, enumValue);
						pi.setValue(absObj);
						log.debug("Set "+parameterName+" with " + parameterValue);
						
					}
					else {
						// Get parameters for complex/aggregate type 
						Vector<ParameterInfo> fieldsParameter = getParametersByLevel(parameterLevel+1, false);
	
						if (absObj instanceof AbsAggregate) {
	
							// Type is aggregate
							for (int arrayIndex = 0; arrayIndex < fieldsParameter.size(); arrayIndex++) {
								
								// Add parameters to aggregate
								ParameterInfo fieldPi = fieldsParameter.get(arrayIndex);
								((AbsAggregate)absObj).add((AbsTerm)fieldPi.getValue());
								log.debug("Add element "+arrayIndex+" to "+parameterName+" with "+fieldPi.getValue());
							}
						} else {
							
							// Type is complex
							for (int fieldIndex = 0; fieldIndex < fieldsParameter.size(); fieldIndex++) {
								ParameterInfo fieldPi = fieldsParameter.get(fieldIndex);
							
								// Get field parameter info
								String fieldName = fieldPi.getName();
								AbsObject fieldValue = fieldPi.getValue();
								
								// Set value
								AbsHelper.setAttribute(absObj, fieldName, fieldValue);
							}
						}
	
						// Remove parameters of level parameterLevel+1
						parametersByLevel.remove(parameterLevel+1);
	
						// Set value in parameter info object
						pi.setValue(absObj);
					
						log.debug("End managing parameter "+parameterName);
					}
				}
			}
		} catch(Exception e) {
			level = 0;
			throw new RuntimeException("Error parsing element "+parameterName+". "+e.getMessage(), e);
		}

		--level;
	}

	public void characters (char ch[], int start, int length) {
		elementValue.append(ch, start, length);
	}

	public void startDocument () {
	}

	public void endDocument () {
	}

	public void startPrefixMapping (String prefix, String uri) {
	}

	public void endPrefixMapping (String prefix) {
	}
}

