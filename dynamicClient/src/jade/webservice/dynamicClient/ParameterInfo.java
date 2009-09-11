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
package jade.webservice.dynamicClient;

import javax.xml.rpc.holders.Holder;

import org.apache.axis.utils.JavaUtils;

import jade.content.schema.PrimitiveSchema;
import jade.content.schema.TermSchema;

/**
 * Descriptor of a parameter exposed by the web-service.
 * 
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class ParameterInfo {

	public static final int UNBOUNDED = -1;
	
	public static final int UNDEFINED = -1;
	public static final int IN = 0;
	public static final int OUT = 1;
	public static final int INOUT = 2;
	public static final int RETURN = 3;
	
	private String name;
	private String documentation;
	private Class typeClass;
	private int mode = UNDEFINED;
	private boolean mandatory;
	private TermSchema schema; 
	private Object defaultValue;
	private String regex;
	private Integer cardMin;
	private Integer cardMax;
	

	ParameterInfo(String parameterName) {
		this.name = parameterName;
		this.mandatory = true;
	}
	
	/**
	 * Return the name of the parameter
	 * 
	 * @return the name of parameter
	 */
	public String getName() {
		return name;
	}

	public String getDocumentation() {
		return documentation;
	}

	/**
	 * Return the wsdl documentation associated at this parameter concatenating the
	 * documentations contained in <code>message element</code> and <code>parameter element</code>.   
	 * <p>
	 * <code>
	 *  &lt;xsd:element name="PortName"&gt;<br>
	 *  	&lt;xsd:annotation&gt;<br>
	 *			&lt;wsdl:documentation&gt;parameter documentation&lt;/wsdl:documentation&gt;<br>
	 *		&lt;/xsd:annotation&gt;<br>
	 *	&lt;/xsd:element&gt;<br>
	 * </code>
	 *   
	 * @return wsdl documentation
	 */
	void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	
	/**
	 * If parameter is a primitive or an extended-primitive 
	 * return the java class associated, otherwise return null
	 *  
	 * @return parameter java class
	 */
	public Class getPrimitiveTypeClass() {
		Class primitiveTypeClass = null;
		if (schema instanceof PrimitiveSchema) {
			if (Holder.class.isAssignableFrom(typeClass)) {
				primitiveTypeClass = JavaUtils.getHolderValueType(typeClass);
			} else {
				// IN or RETURN mode
				primitiveTypeClass = typeClass;
			}
		}
		return primitiveTypeClass;
	}
	
	Class getTypeClass() {
		return typeClass;
	}

	void setTypeClass(Class typeClass) {
		this.typeClass = typeClass;
	}

	/**
	 * Return the mode of this parameter.
	 * <p>
	 * The possible values are (defined as class constants):
	 * <li> ParameterInfo.IN: input parameter
	 * <li> ParameterInfo.OUT: output parameter
	 * <li> ParameterInfo.INOUT: input/output parameter
	 * <li> ParameterInfo.RETURN: the same of OUT (used internally)  
	 * 
	 * @return parameter mode
	 */
	public int getMode() {
		return mode;
	}

	void setMode(int mode) {
		this.mode = mode;
	}

	/**
	 * Return the JADE schema of this parameter
	 * 
	 * @return parameter JADE schema
	 * 
	 * @see jade.content.schema.TermSchema
	 */
	public TermSchema getSchema() {
		return schema;
	}
	
	void setSchema(TermSchema schema) {
		this.schema = schema;
	}

	/**
	 * Tests if this parameter is mandatory for the operation
	 * 
	 * @return true if the parameter is mandatory, false otherwise
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * Get default value
	 * 
	 * @return default value
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}

	void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Get regular expression with permitted value of parameter
	 * 
	 * @return regular expression
	 */
	public String getRegex() {
		return regex;
	}

	void setRegex(String regex) {
		this.regex = regex;
	}

	/**
	 * Get min cardinality, valid only if parameter is an array
	 *    
	 * @param cardMin min cardinality
	 */
	public Integer getCardMin() {
		return cardMin;
	}

	void setCardMin(int cardMin) {
		this.cardMin = Integer.valueOf(cardMin);
	}

	/**
	 * Get max cardinality, valid only if parameter is an array
	 *    
	 * @param cardMin max cardinality (UNBOUNDED if there is no limits)
	 */
	public Integer getCardMax() {
		return cardMax;
	}

	void setCardMax(int cardMax) {
		this.cardMax = Integer.valueOf(cardMax);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name="+name);
		sb.append(", class="+typeClass.getCanonicalName());
		sb.append(", primitiveClass="+getPrimitiveTypeClass());
		sb.append(", mode="+getStringMode(mode));
		sb.append(", schema="+schema.getTypeName());
		sb.append(", mandatory="+mandatory);
		if (cardMin != null) {
			sb.append(", cardMin="+cardMin);
		}
		if (cardMax != null) {
			sb.append(", cardMax="+cardMax);
		}
		if (documentation != null && !documentation.equals("")) {
			sb.append(", doc="+documentation);
		}
		if (regex != null && !regex.equals("")) {
			sb.append(", regex="+regex);
		}
		return sb.toString();
	}
	
	private String getStringMode(int mode) {
		switch(mode) {
		case IN:
			return "IN";
		case OUT:
			return "OUT";
		case INOUT:
			return "INOUT";
		case RETURN:
			return "RETURN";
		}
		return "UNDEFINED";
	}

}
