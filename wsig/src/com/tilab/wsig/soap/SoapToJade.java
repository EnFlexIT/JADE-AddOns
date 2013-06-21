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
import jade.util.Logger;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.Message;
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
	
	private static Logger logger = Logger.getMyLogger(SoapToJade.class.getName());

	private XMLReader xmlParser;
	private int level = 0;
	private Ontology onto;
	private boolean hierarchicalComplexType;
	private StringBuffer elementValue = new StringBuffer();
	private Vector<Vector<ParameterInfo>> parametersByLevel = new Vector<Vector<ParameterInfo>>();
	private Vector<ObjectSchema> schemaByLevel = new Vector<ObjectSchema>();
	private Map<String, ParameterInfo> parametersInfo;
	private String anyTypeParameterName = null;
	
	public SoapToJade() {
		
		// Get xml parser
	    try { 
	    	String parserName = getSaxParserName();
	    	
			xmlParser = (XMLReader)Class.forName(parserName).newInstance();
			xmlParser.setContentHandler(this);
			xmlParser.setErrorHandler(this);
		}
	    catch(Exception e) {
			logger.log(Level.SEVERE, "Unable to create XML parser", e);
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
	
	public synchronized Object convert(Message soapRequest, WSIGService wsigService, String operationName) throws Exception {

		Object actionObj = null;
		String soapBodyMessage = soapRequest.getSOAPBody().toString();
		hierarchicalComplexType = wsigService.isHierarchicalComplexType();
		
		// Verify if parser is ready
		if (xmlParser == null) {
			logger.log(Level.SEVERE, "XML parser not initialized");
			throw new Exception("XML parser not initialized");
		}

		// Get action builder
		ActionBuilder actionBuilder = wsigService.getActionBuilder(operationName);
		if (actionBuilder == null) {
			logger.log(Level.SEVERE, "Operation "+operationName+" not present in service "+wsigService.getServiceName());
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
		logger.log(Level.FINE, "Begin parameters list");

		LinkedHashMap<String, ParameterInfo> params = new LinkedHashMap<String, ParameterInfo>();
		if (parametersByLevel.size() >= 1) {
			Vector<ParameterInfo> soapParams = parametersByLevel.get(0);
			for (ParameterInfo soapParam : soapParams) {
				params.put(soapParam.getName(), soapParam);
				logger.log(Level.FINE, "   "+soapParam.getName()+"= "+soapParam.getValue());
			}
		} else {
			logger.log(Level.FINE, "   No parameters");
		}
		logger.log(Level.FINE, "End parameters list");
		
		return params;
	}

	private TermSchema getParameterSchema(String elementName, int level, Attributes attrs) throws Exception {

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
			
			if (hierarchicalComplexType) {
				// Get parameter schema declared in attribute type of SOAP 
				String parameterType = getAttributeType(attrs);
				if (parameterType != null) {
					ObjectSchema soapSchema = onto.getSchema(parameterType);
					if (soapSchema == null || !schema.isAssignableFrom(soapSchema)) {
						throw new Exception("Schema "+soapSchema.getTypeName()+" not assignable from "+schema.getTypeName());
					}
					
					schema = (TermSchema)soapSchema;
				}
			}
			
			// Add schema to stack 
			schemaByLevel.add(level, schema);
			
			return schema;
			
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Schema not found for element "+elementName, e);
			throw e;
		}
	}

	private Vector<ParameterInfo> getParametersByLevel(int level, boolean addIfNotExist) throws Exception {
		if (!addIfNotExist && parametersByLevel.size() <= level) {
			return null;
		}
		
		Vector<ParameterInfo> parameters;
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
	
	private String getAttributeType(Attributes attrs) {
		String type = null;
		if (attrs != null) {
			for (int i=0; i<attrs.getLength(); i++) {
				if (attrs.getLocalName(i).equalsIgnoreCase("type")) {
					String value = attrs.getValue(i);
					int sepPos = value.indexOf(":");
					if (sepPos == -1) {
						type = value;
					} else {
						type = value.substring(sepPos+1);
					}
					break;
				}
			}
		}		
		return type;
	}
	
	
	//-------- PARSER EVENT HANDLERS -----------------//

	@Override
	public void startElement (String uri, String parameterName, String qName, Attributes attrs) {
		// Manage anyType parameter
		if (anyTypeParameterName != null) {
			// anyType managing active -> collect data and skip to next tag
			elementValue.append("<");
			elementValue.append(qName);
			for (int i=0; i<attrs.getLength(); i++) {
				elementValue.append(" ");
				elementValue.append(attrs.getLocalName(i));
				elementValue.append("=\"");
				elementValue.append(attrs.getValue(i));
				elementValue.append("\"");
			}
			elementValue.append(">");
			return;
		}
		
		try {
			elementValue.setLength(0);
			++level;

			// Manage only parameters levels
			if (level >= PARAMETERS_LEVEL) {

				// Get  parameter level
				int parameterLevel = level - PARAMETERS_LEVEL; 

				// Get parameter schema
				TermSchema parameterSchema = getParameterSchema(parameterName, parameterLevel, attrs);
				logger.log(Level.FINE, "Start managing parameter "+parameterName+" of type "+parameterSchema.getTypeName());
				
				// If the slot is of type TermSchema start to collect all the following tags
				if (parameterSchema.getClass() == TermSchema.class) {
					anyTypeParameterName = parameterName;
				}

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

	@Override
	public void endElement (String uri, String parameterName, String qName) {
		// Manage anyType parameter
		if (anyTypeParameterName != null) {
			if (anyTypeParameterName.equals(parameterName)) {
				// anyType tag closed -> resume normal tag management
				anyTypeParameterName = null;
			} else {
				// anyType managing active -> collect data and skip to next tag
				elementValue.append("</");
				elementValue.append(qName);
				elementValue.append(">");
				return;
			}
		}
		
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
					logger.log(Level.FINE, "Set "+parameterName+" with " + parameterValue);
					
				} else if (parameterSchema.getClass() == TermSchema.class) {
					// Term type (anyType)
					// Assigned as a string
					pi.setValue(getPrimitiveAbsValue(new PrimitiveSchema(WSDLConstants.XSD_STRING), parameterValue));
					logger.log(Level.FINE, "Set "+parameterName+" with " + parameterValue);
					
				} else {
					// Complex type -> create abs object from schema
					AbsObject absObj = parameterSchema.newInstance();
					
					Class parameterClass = onto.getClassForElement(parameterSchema.getTypeName());
					if (parameterClass != null && parameterClass.isEnum()) {
						// Enum type
						AbsPrimitive enumValue = getPrimitiveAbsValue(new PrimitiveSchema(WSDLConstants.XSD_STRING), parameterValue);
						AbsHelper.setAttribute(absObj, WSDLConstants.ENUM_SLOT_NAME, enumValue);
						pi.setValue(absObj);
						logger.log(Level.FINE, "Set "+parameterName+" with " + parameterValue);
						
					}
					else {
						// Get parameters for complex/aggregate type 
						Vector<ParameterInfo> fieldsParameter = getParametersByLevel(parameterLevel+1, false);
						if (fieldsParameter != null) {
							
							if (absObj instanceof AbsAggregate) {
		
								// Type is aggregate
								for (int arrayIndex = 0; arrayIndex < fieldsParameter.size(); arrayIndex++) {
									
									// Add parameters to aggregate
									ParameterInfo fieldPi = fieldsParameter.get(arrayIndex);
									((AbsAggregate)absObj).add((AbsTerm)fieldPi.getValue());
									logger.log(Level.FINE, "Add element "+arrayIndex+" to "+parameterName+" with "+fieldPi.getValue());
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
						}
	
						// Set value in parameter info object
						pi.setValue(absObj);
					
						logger.log(Level.FINE, "End managing parameter "+parameterName);
					}
				}
			}
		} catch(Exception e) {
			level = 0;
			throw new RuntimeException("Error parsing element "+parameterName+". "+e.getMessage(), e);
		}

		--level;
	}

	@Override
	public void characters (char ch[], int start, int length) {
		elementValue.append(ch, start, length);
	}

	@Override
	public void startDocument () {
	}

	@Override
	public void endDocument () {
	}

	@Override
	public void startPrefixMapping (String prefix, String uri) {
	}

	@Override
	public void endPrefixMapping (String prefix) {
	}
}

