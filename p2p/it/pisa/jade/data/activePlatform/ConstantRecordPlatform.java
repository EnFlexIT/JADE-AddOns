/*
 * Created on 8-ott-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.pisa.jade.data.activePlatform;

import jade.content.onto.BasicOntology;
import jade.content.schema.ObjectSchema;

/**
 * @author Domenico Trimboli
 * 
 */
public enum ConstantRecordPlatform {

	RECORDPLATFORM("RecordPlatform");

	private ConstantRecordPlatform(String value) {
		this.value = value;
	}

	public AttributeRecordPlatform[] getAttribute() {
		return AttributeRecordPlatform.values();
	}

	private String value;
	
	public String getValue(){
		return value;
	}

	enum AttributeRecordPlatform {

		RECORDPLATFORM_PLATFORM_NAME("platformName", BasicOntology.STRING,
				ObjectSchema.MANDATORY), RECORDPLATFORM_PLATFORM_ADDRESS(
				"platformAddress", BasicOntology.STRING, ObjectSchema.MANDATORY), RECORDPLATFORM_TTL(
				"ttl", BasicOntology.INTEGER, ObjectSchema.MANDATORY);
		private AttributeRecordPlatform(String value, String schema, int type) {
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
}
