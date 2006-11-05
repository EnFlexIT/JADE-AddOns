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

/**
 * Enumeration da uasare insieme alla classe Parameter per definire i parametri
 * ed i loro valori di dafault
 * 
 * @author Domenico Trimboli
 * 
 */
public enum Values {
	multicastPort("6030", Parameter.INTEGER), multicastIP("127.0.0.1",
			Parameter.IP), jadePortNumber("1099", Parameter.INTEGER), timeRefreshPlatformMax(
			"20000", Parameter.LONG), timeRefreshPlatformMin("5000",
			Parameter.LONG), ttlPlatformRecord("2", Parameter.INTEGER), federatorTimeSleep(
			"5000", Parameter.LONG), peerAgentPeriod("5000", Parameter.LONG), reducedMessageSending(
			Parameter.YES, Parameter.BOOLEAN), promptConsoleMessage(
			Parameter.NO, Parameter.BOOLEAN, false), sharedDirectory(" ",
			Parameter.STRING), mobileInterplatformService(Parameter.NO,
			Parameter.BOOLEAN, false), mobileInterplatformPath("C:\\",
			Parameter.STRING, false), localIP("127.0.0.1", Parameter.IP, false), multicastTTL(
			"127", Parameter.INTEGER, true), peerAddressPort("6000",
			Parameter.INTEGER);

	private Values(String s, int type, boolean mandatory) {
		this.value = s;
		this.type = type;
		this.mandatory = mandatory;
	}

	private Values(String s, int type) {
		this.value = s;
		this.type = type;
		this.mandatory = true;
	}

	private boolean mandatory;

	private String value;

	private int type;

	boolean isMandatory() {
		return mandatory;
	}

	int getType() {
		return type;
	}

	public String getDefaultValue() {
		return value;
	}
}
