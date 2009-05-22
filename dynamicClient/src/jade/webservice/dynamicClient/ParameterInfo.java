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

import jade.content.schema.TermSchema;

public class ParameterInfo {

	public static final int UNDEFINED = -1;
	public static final int IN = 0;
	public static final int OUT = 1;
	public static final int INOUT = 2;
	public static final int RETURN = 3;
	
	private String name;
	private Class typeClass;
	private int mode = UNDEFINED;
	private boolean mandatory;

	private TermSchema schema; 

	public ParameterInfo(String parameterName) {
		this.name = parameterName;
		this.mandatory = true;
	}
	
	public String getName() {
		return name;
	}

	public Class getTypeClass() {
		return typeClass;
	}

	void setTypeClass(Class typeClass) {
		this.typeClass = typeClass;
	}

	public int getMode() {
		return mode;
	}

	void setMode(int mode) {
		this.mode = mode;
	}

	public TermSchema getSchema() {
		return schema;
	}
	
	void setSchema(TermSchema schema) {
		this.schema = schema;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name="+name);
		sb.append(", class="+typeClass.getCanonicalName());
		sb.append(", mode="+getStringMode(mode));
		sb.append(", schema="+schema.getTypeName());
		sb.append(", mandatory="+mandatory);
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
