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

import jade.content.onto.Ontology;
import jade.util.leap.ArrayList;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.Message;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.tilab.wsig.store.ActionBuilder;
import com.tilab.wsig.store.ParameterInfo;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.wsdl.WSDLConstants;

public class SoapToJade extends DefaultHandler {

	private static Logger log = Logger.getLogger(SoapToJade.class.getName());

	private int level = 0;
	private StringBuffer elContent;
	private XMLReader xr = null;
	private Ontology onto;
	private Vector<Vector<ParameterInfo>> params4Level;

	/**
	 * SoapToJade2
	 */
	public SoapToJade() {
	    try { 
	    	String parserName = getSaxParserName();
	    	
			xr = (XMLReader)Class.forName(parserName).newInstance();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
		}
	    catch(Exception e) {
			log.error("Unable to create XML reader", e);
		}
	}

	/**
	 * Get SAX parser class name
	 * @param s
	 * @return parser name
	 */
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
	
	/**
	 * convert
	 * @param soapMessage
	 * @param wsigService
	 * @param operationName
	 * @return
	 * @throws Exception
	 */
	public Object convert(Message soapRequest, WSIGService wsigService, String operationName) throws Exception {

		Object actionObj = null;
		try {
			String soapMessage = soapRequest.getSOAPPartAsString();
			
			// Verify if parser is ready
			if (xr == null) {
				throw new Exception("Parser not initialized");
			}
	
			// Set ontology
			onto = wsigService.getOnto();
	
			// Parse xml to extract parameters value
			xr.parse(new InputSource(new StringReader(soapMessage)));
	
			// Get parameters
			Vector<ParameterInfo> params = getParameters();
			
			// Get action builder
			ActionBuilder actionBuilder = wsigService.getActionBuilder(operationName);
			
			// Prepare jade action
			actionObj = actionBuilder.getAgentAction(params);

		} catch(Exception e) {
			log.error("Error parsing soap message", e);
		}
		
		return actionObj;
	}
	
	/**
	 * getParameters
	 * @return
	 */
	private Vector<ParameterInfo> getParameters() {
	
		Vector<ParameterInfo> params = null;
		
		log.debug("Begin parameters list");
		if (params4Level.size() >= 1) {
			params = params4Level.get(0);
			
			for (ParameterInfo param : params) {
				log.debug("   "+param.getName()+"= "+param.getValue());
			}
		} else {
			log.debug("   No parameters");
		}
		log.debug("End parameters list");
		
		return params;
	}

	
	
	//-------- SAX2 EVENT HANDLERS -----------------//

	/**
	 * startDocument
	 */
	public void startDocument () {
		elContent = new StringBuffer();
		params4Level = new Vector<Vector<ParameterInfo>>();
	}

	/**
	 * endDocument
	 */
	public void endDocument () {
	}

	/**
	 * startPrefixMapping
	 */
	public void startPrefixMapping (String prefix, String uri) {
	}

	/**
	 * endPrefixMapping
	 */
	public void endPrefixMapping (String prefix) {
	}

	/**
	 * startElement
	 *
	 * Level:
	 * 1: Envelope
	 * 2: Body
	 * 3: Operation
	 * >=4: Parameters
	 */
	public void startElement (String uri, String name, String qName, Attributes attrs) {

		try {
			elContent.setLength(0);
			++level;

			// Manage only parameters levels
			if (level >= 4) {

				// Get parameter type
				String attrValue = attrs.getValue(WSDLConstants.xsi, "type");
				int pos = attrValue.indexOf(':');
				String valueType = attrValue.substring(pos+1);
				log.debug("Start managing parameter "+name+" of type "+valueType);

				// Prepare vector store for this level
				int params4LevelIndex = level - 4; 
				Vector<ParameterInfo> params = null;
				if (params4Level.size() <= params4LevelIndex) {
					params = new Vector<ParameterInfo>();
					params4Level.add(params4LevelIndex, params);
				} else {
					params = params4Level.get(params4LevelIndex);
				}

				// Create new ElementInfo for this parameter
				ParameterInfo ei = new ParameterInfo();
				ei.setName(name);
				ei.setType(valueType);
				params.add(ei);
			}

		} catch(Exception e) {
			level = 0;
			throw new RuntimeException("Error parsing element "+name, e);
		}
	}

	/**
	 * endElement
	 * 
	 * Level:
	 * 1: Envelope
	 * 2: Body
	 * 3: Operation
	 * >=4: Parameters
	 */
	public void endElement (String uri, String name, String qName) {
		
		try {
			// Manage only parameters levels
			if (level >= 4) {

				// Get parameter value
				String fieldValue = elContent.toString();

				// Get vector store for this level
				int params4LevelIndex = level - 4; 
				Vector<ParameterInfo> params = params4Level.get(params4LevelIndex);
				
				// Get parameter infos & verify...
				ParameterInfo paramEi = params.lastElement();
				if (!paramEi.getName().equals(name)) {
					throw new RuntimeException("Parameter "+name+" not match with parameter in store ("+paramEi.getName()+")");
				}

				// Get class type
				Class clazz = SoapUtils.getClassByType(onto, paramEi.getType());

				// Fill value
				if (SoapUtils.isPrimitiveJadeType(clazz)) {
					// Primitive type
					paramEi.setValue(SoapUtils.getPrimitiveValue(paramEi.getType(), fieldValue));
					log.debug("Set "+name+" with "+paramEi.getValue());

				} else {
					// Not primitive type
					
					// Create parameter object
					Object obj = clazz.newInstance();
					
					// Get store of object parameters
					Vector<ParameterInfo> objParams = params4Level.get(params4LevelIndex+1);

					if (obj instanceof ArrayList) {

						// Object is an array
						for (int count = 0; count < objParams.size(); ++count) {
							
							// Add param to array
							ParameterInfo objParamEi = objParams.get(count);
							((ArrayList)obj).add(objParamEi.getValue());
							log.debug("Add element "+count+" to "+name+" with "+objParamEi.getValue());
						}
					} else {

						// Object is a custom type

						// Get fields of object
						Field[] dfs = clazz.getDeclaredFields();

						// Set value to every fields
						for (int count = 0; count < dfs.length; ++count) {
							
							// Set reflection accessiable method
							dfs[count].setAccessible(true);

							// Get parameter and verify...
							String paramName = dfs[count].getName();
							ParameterInfo paramEi1 = objParams.get(count);
							if (paramEi1.getName() != paramName) {
								throw new RuntimeException("Parameter "+paramName+" not match with parameter in store ("+paramEi1.getName()+")");
							}

							// Set parameter value
							dfs[count].set(obj, paramEi1.getValue());
							log.debug("Set "+name+"."+paramName+" with "+paramEi1.getValue());
						}
					}

					// Remove params level from store
					params4Level.remove(objParams);
					
					// Set value in param object
					paramEi.setValue(obj);
				
					log.debug("End managing parameter "+name);
				}
			}
		} catch(Exception e) {
			level = 0;
			throw new RuntimeException("Error parsing element "+name, e);
		}

		--level;
	}

	/**
	 * characters
	 */
	public void characters (char ch[], int start, int length) {
		elContent.append(ch, start, length);
	}
}

