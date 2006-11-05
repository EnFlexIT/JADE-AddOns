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
package it.pisa.jade.util;

import jade.core.AID;

import java.io.File;
import java.io.IOException;

/**
 * @author Fabrizio Marozzo
 *
 */
public class FactoryUtil {
	public static final String JAVASERIALIZATION_LANGUAGE = "JavaSerialization";

	public static final String STRING_LANGUAGE = "String";
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
