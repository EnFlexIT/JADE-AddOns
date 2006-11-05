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
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial")
class ExitMessage implements MulticastMessage {

	private String ip;

	private RecordPlatform infoSender;

	/**
	 * 
	 */
	public ExitMessage(RecordPlatform r) {
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

	public ExitMessage(String msg) {
		String s = msg.substring(ConstantTypeMsg.EXIT.value().length() + 1, msg
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
		return ConstantTypeMsg.EXIT + "(" + ip + MessageCostant.SEPARATOR
				+ infoSender.getPlatformName() + MessageCostant.SEPARATOR
				+ infoSender.getPlatformAddress() + ")";
	}

	public static boolean isExitMessage(String msg) {
		if (msg.indexOf(ConstantTypeMsg.EXIT.value()) != -1) {
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
		return ConstantTypeMsg.EXIT;
	}

}
