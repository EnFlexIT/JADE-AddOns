/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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
package it.pisa.jade.data.activePlatform;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
public enum ConstantRequestAction {
	REQUESTACTIVEPLATFORM("Request");
	private ConstantRequestAction(String value) {
		this.value = value;
	}

	private String value;

	public String getValue() {
		return value;
	}

	public AttributeRequestAction[] getAttribute() {
		return AttributeRequestAction.values();
	}
}

enum AttributeRequestAction {
	REQUESTACTIVEPLATFORM("RecordPlatform", null, 0);
	private AttributeRequestAction(String value, String schema, int type) {
		this.schema = schema;
		this.value = value;
		this.type = type;
	}

	private String schema;

	private String value;

	private int type;

	public String getValue() {
		return value;
	}

	public int getType() {
		return type;
	}

	public String getSchema() {
		return schema;
	}
}
