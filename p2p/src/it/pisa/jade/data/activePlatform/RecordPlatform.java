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

import jade.content.Concept;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Coppia di valori nome piattaforma indirizzo
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial") 
public class RecordPlatform implements
		Serializable, Comparable, Concept {

	private String platformAddress;
	private String platformName;

	private int TTL;
	/**
	 * 
	 */
	public RecordPlatform() {
	}

	public RecordPlatform(String msg) {
		StringTokenizer st = new StringTokenizer(msg, "()");
		this.platformName = st.nextToken();
		this.platformAddress = st.nextToken();
		TTL = 1;
	}

	/**
	 * Il costruttore è l'unico che permette di settare il nome e l'indirizzo
	 * della piattaforma, questo per garantire che nessun agente può modificare
	 * il valori.
	 * 
	 * @param nomePiattaforma
	 *            nome della piattaforma
	 * @param indirizzoPiattaforma
	 *            indirizzo della piattaforma
	 */
	public RecordPlatform(String platformName, String platformAddress) {
		this.platformName = platformName;
		this.platformAddress = platformAddress;
		TTL = 1;
	}

	public int compareTo(Object o) {
		RecordPlatform record = (RecordPlatform) o;

		return this.platformName.compareTo(record.platformName);
	}

	public void decTTL() {
		TTL--;
	}

	public boolean equals(Object o) {
		RecordPlatform record = (RecordPlatform) o;
		return this.platformName.equals(record.platformName);
	}

	public String getPlatformAddress() {
		return platformAddress;
	}

	public String getPlatformName() {
		return platformName;
	}

	public int getTTL() {
		return TTL;
	}

	/**
	 * @param platformAddress The platformAddress to set.
	 */
	public void setPlatformAddress(String platformAddress) {
		this.platformAddress = platformAddress;
	}
	/**
	 * @param platformName The platformName to set.
	 */
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public void setTTL(int ttl) {
		TTL = ttl;
	}

	public String toString() {
		return platformName + "(" + platformAddress + ")";
	}

}
