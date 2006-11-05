/**
 * 
 */
package it.pisa.jade.util;

/**
 * @author Domenico Trimboli
 * 
 */
public enum ConstantForACL { 
	MANAGE_PLATFORM("managePlatform"),
	MANAGE_AGENT("manageAgent"),
	MANAGE_FEDERATION("manageFederation");
	ConstantForACL(String s) {
		value = s;
	}

	private String value;

	public String value() {
		return value;
	}
}
