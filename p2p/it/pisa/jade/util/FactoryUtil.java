/**
 * 
 */
package it.pisa.jade.util;

import jade.core.AID;

import java.io.File;
import java.io.IOException;

/**
 * @author LunaAdmin
 *
 */
public class FactoryUtil {
	
	/**
	 * Parses the passed string containing an agent name and an agent address
	 * separated by a comma and creates an AID for that agent.
	 * 
	 * @param value
	 *            a String in the form <name>,<value> For instance
	 *            df@sharon.cselt.it:1099/JADE , IOR:000002233
	 * @return the agent AID
	 */
	 public static AID createAID(String value) {
		AID aid;
		// before the comma is the AID.name, after the comma is the AID.address
		int ind = value.indexOf(',');
		if (ind < 0) {
			// if no comma was found, then assume is a local agent
			aid = new AID(value, (value.indexOf('@') < 0 ? AID.ISLOCALNAME
					: AID.ISGUID));
		} else {
			String aidName = value.substring(0, ind).trim();
			String aidAddress = value.substring(ind + 1).trim();
			aid = new AID(aidName, AID.ISGUID);
			aid.addAddresses(aidAddress);
		}
		return aid;
	}

	public static boolean checkName(String platformName) {
		return true;
	}

	public static boolean checkAddress(String platformAddress) {
		return true;
		
	}
	
	public static String getApplicationPath(){
		String path="";
		File f;
		try {
			f = File.createTempFile("temp","tmp");
			path=f.getCanonicalPath();
		} catch (IOException e) {
		}
		return path;
	}

}
