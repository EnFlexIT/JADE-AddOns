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
package it.pisa.jade.agents.multicastAgent.ontologies;

import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;



/**
 * Questa classe mantiene un'ontologia del messaggio di pong che pu� avere due
 * funzioni.
 * <ul>
 * <li>E' un messaggio del tipo sono vivo, identificato dalla costante
 * <b>ALIVE_TYPE</b></li>
 * <li>E' un messaggio del tipo sto per terminare la mia esecuzione,
 * identificato dalla costante <b>LEAVE_TYPE</b></li>
 * </ul>
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
class PongMessage implements MulticastMessage {

	private RecordPlatform infoSender;

	private String ip;

	/**
	 * L'indirizzo ip � impostato automaticamente ma si pu� cambiare tramite i
	 * setter
	 * 
	 * @param type
	 */
	public PongMessage(RecordPlatform r) {
		if (r == null)
			throw new NullPointerException();
		infoSender = r;
		ip = Parameter.get(Values.localIP);
		if (ip == null) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				WrapperErrori.wrap("impossible research local ip", e);
			}
		}
		if (this.toString().length() > 127) {
			throw new IllegalArgumentException(
					"La lunghezza del nome della piattaforma � troppo elevato");
		}
	}

	/**
	 * riceve una stringa come rappresentazione del messaggio
	 * 
	 * @param msg
	 */
	public PongMessage(String msg) {
		String s = msg.substring(ConstantTypeMsg.PONG.value().length() + 1, msg
				.indexOf(")"));
		StringTokenizer st = new StringTokenizer(s, MessageCostant.SEPARATOR
				.value());
		ip = st.nextToken();
		String platformName = st.nextToken();
		String platformAddress = st.nextToken();
		infoSender = new RecordPlatform(platformName, platformAddress);
		if (this.toString().length() > 127) {
			throw new IllegalArgumentException(
					"La lunghezza del nome della piattaforma � troppo elevato");
		}
	}

	public String toString() {
		return ConstantTypeMsg.PONG + "(" + ip + MessageCostant.SEPARATOR
				+ infoSender.getPlatformName() + MessageCostant.SEPARATOR
				+ infoSender.getPlatformAddress() + ")";
	}

	public boolean equals(Object o) {
		PongMessage p = (PongMessage) o;
		return ip.equals(p.ip) && infoSender.equals(p.getPlatform());
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static void main(String[] arg) {
		String msg = ConstantTypeMsg.PONG
				+ "(127.0.0.1;squall:1099/JADE;http://squall:7778/acc)";
		PongMessage p = new PongMessage(msg);
		System.out.println(p);
	}

	public static boolean isPongMessage(String msg) {
		if (msg.indexOf(ConstantTypeMsg.PONG.value()) != -1) {
			return true;
		} else
			return false;
	}

	public String getSender() {
		return ip;
	}

	public RecordPlatform getPlatform() {
		return infoSender;
	}

	public ConstantTypeMsg getType() {
		return ConstantTypeMsg.PONG;
	}

}
