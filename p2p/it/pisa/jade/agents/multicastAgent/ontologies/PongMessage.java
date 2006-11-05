package it.pisa.jade.agents.multicastAgent.ontologies;

import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;



/**
 * Questa classe mantiene un'ontologia del messaggio di pong che può avere due
 * funzioni.
 * <ul>
 * <li>E' un messaggio del tipo sono vivo, identificato dalla costante
 * <b>ALIVE_TYPE</b></li>
 * <li>E' un messaggio del tipo sto per terminare la mia esecuzione,
 * identificato dalla costante <b>LEAVE_TYPE</b></li>
 * </ul>
 * 
 * @author domenico
 * 
 */
@SuppressWarnings("serial")
class PongMessage implements MulticastMessage {

	private RecordPlatform infoSender;

	private String ip;

	/**
	 * L'indirizzo ip è impostato automaticamente ma si può cambiare tramite i
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
					"La lunghezza del nome della piattaforma è troppo elevato");
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
					"La lunghezza del nome della piattaforma è troppo elevato");
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
