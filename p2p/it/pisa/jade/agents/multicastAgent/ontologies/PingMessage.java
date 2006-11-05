/**
 * 
 */
package it.pisa.jade.agents.multicastAgent.ontologies;

import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


/**
 * @author domenico
 * 
 */
@SuppressWarnings("serial")
class PingMessage implements MulticastMessage {

	private String ip;

	private RecordPlatform infoSender;

	/**
	 * 
	 * @param r
	 */
	public PingMessage(RecordPlatform r) {
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

	public PingMessage(String msg) {
		String s = msg.substring(ConstantTypeMsg.PING.value().length() + 1, msg
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
		return ConstantTypeMsg.PING + "(" + ip + MessageCostant.SEPARATOR
				+ infoSender.getPlatformName() + MessageCostant.SEPARATOR
				+ infoSender.getPlatformAddress() + ")";
	}

	public static boolean isPingMessage(String msg) {
		if (msg.indexOf(ConstantTypeMsg.PING.value()) != -1) {
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
		return ConstantTypeMsg.PING;
	}

	public static void main(String[] args) {
		PingMessage m = new PingMessage(ConstantTypeMsg.PING
				+ "(squall;squall:1099/JADE;http://squall:7778/acc)");
		System.out.println(m);
	}

	public void setSender(String string) {
		ip = string;
	}
}
