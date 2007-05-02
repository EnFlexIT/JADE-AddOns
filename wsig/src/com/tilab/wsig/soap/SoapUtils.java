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

import org.apache.log4j.Logger;

import com.tilab.wsig.wsdl.WSDLGeneratorUtils;

public class SoapUtils {
	
	private static Logger log = Logger.getLogger(SoapUtils.class.getName());

	
	/**
	 * getClassByType
	 * @param onto
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static Class getClassByType(Ontology onto, String type) throws Exception {

		Class clazz = null;
		if("string".equals(type)) {
			clazz = String.class;
		} else if("boolean".equals(type)) {
			clazz = Boolean.TYPE;
		} else if("byte".equals(type)) {
			clazz = Byte.TYPE;
		} else if("char".equals(type)) {
			clazz = Character.TYPE;
		} else if("double".equals(type)) {
			clazz = Double.TYPE;
		} else if("float".equals(type)) {
			clazz = Float.TYPE;
		} else if("int".equals(type)) {
			clazz = Integer.TYPE;
		} else if("long".equals(type)) {
			clazz = Long.TYPE;
		} else if("short".equals(type)) {
			clazz = Short.TYPE;
		} else if(type.endsWith(WSDLGeneratorUtils.getArraySuffix())) {
			// Array
			clazz = ArrayList.class;
		} else {
			// Find in ontology type
			clazz = getClassFromOnto(onto, type);
		}
		return clazz;
	}

	/**
	 * getClassFromOnto
	 * @param onto
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private static Class getClassFromOnto(Ontology onto, String name) throws Exception {

		Class clazz = null;
		try {
			clazz = onto.getClassForElement(name);
		} catch(Exception e) {
			log.error("Element "+name+" not found in ontology "+onto.getName(), e);
			throw new Exception("Element "+name+" not found in ontology "+onto.getName(), e);
		}
		return clazz;
	}
	

	/**
	 * getPrimitiveValue
	 * @param type
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static Object getPrimitiveValue(String type, String value) throws Exception {

		Object obj = null;
		// Find in base type
		if("string".equals(type)) {
			obj = value;
		} else if("boolean".equals(type)) {
			obj = Boolean.parseBoolean(value);
		} else if("byte".equals(type)) {
			obj = Byte.parseByte(value);
		} else if("char".equals(type)) {
			obj = value.charAt(0);
		} else if("double".equals(type)) {
			obj = Double.parseDouble(value);
		} else if("float".equals(type)) {
			obj = Float.parseFloat(value);
		} else if("int".equals(type)) {
			obj = Integer.parseInt(value);
		} else if("long".equals(type)) {
			obj = Long.parseLong(value);
		} else if("short".equals(type)) {
			obj = Short.parseShort(value);
		} else {
			// No primitive type
			throw new Exception(type+" is not a primitive type");
		}
		return obj;
	}
	
}
