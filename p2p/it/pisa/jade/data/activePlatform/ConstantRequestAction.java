/*
 * Created on 8-ott-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.pisa.jade.data.activePlatform;

/**
 * @author Domenico Trimboli
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
