/**
 * 
 */
package it.pisa.jade.agents.multicastAgent;

import it.pisa.jade.agents.multicastAgent.behaviours.ActivePlatformService;
import it.pisa.jade.agents.multicastAgent.behaviours.DiscoverBehavior;
import it.pisa.jade.agents.multicastAgent.ontologies.ConstantTypeMsg;
import it.pisa.jade.agents.multicastAgent.ontologies.FactoryMessage;
import it.pisa.jade.agents.multicastAgent.ontologies.MulticastMessage;
import it.pisa.jade.data.activePlatform.ActivePlatformLinkedList;
import it.pisa.jade.data.activePlatform.ActivePlatformStub;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;
import jade.core.Agent;
import jade.core.behaviours.ThreadedBehaviourFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial")
public class MulticastManagerAgent extends Agent {
	private static final int BUFFER_LENGTH = 128;

	private ActivePlatformStub activePlatform;

	// private SimpleDateFormat format = new SimpleDateFormat(
	// "dd/MMM/yyyy:HH:mm:ss");

	private DiscoverBehavior discover;

	private RecordPlatform localPlatform;

	private String multicastIP;

	private int multicastPort;

	private MulticastSocket multicastSocket;
	
	private byte multicastTTL;

	private boolean promptActivityPlatform = (Parameter
			.get(Values.promptConsoleMessage) != null && Parameter.get(
			Values.promptConsoleMessage).equals(Parameter.YES)) ? true : false;

	public DatagramPacket receiveMulticastMessage() throws IOException {
		byte[] buf = new byte[BUFFER_LENGTH];
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(packet);
		return packet;
	}

	public boolean sendMessage(MulticastMessage msg) {
		try {
			String strBuf = msg.toString();
			byte[] buf = new byte[128];
			byte[] strByte = strBuf.getBytes();
			System.arraycopy(strByte, 0, buf, 0, strByte.length);
			buf[127] = (byte) strByte.length;
			InetAddress mcastAddress = InetAddress.getByName(multicastIP);
			DatagramPacket dp = new DatagramPacket(buf, buf.length,
					mcastAddress, multicastPort);
			multicastSocket.setTimeToLive(multicastTTL);
			multicastSocket.send(dp);
			return true;
		} catch (UnknownHostException e) {
			WrapperErrori.wrap("", e);
		} catch (IOException e) {
			WrapperErrori.wrap("", e);
		}
		return false;
	}

	@Override
	protected void setup() {
		activePlatform = new ActivePlatformLinkedList();
		multicastPort = Integer.parseInt(Parameter.get(Values.multicastPort));
		multicastIP = Parameter.get(Values.multicastIP);
		multicastTTL=(byte)Parameter.getInt(Values.multicastTTL);
		try {
			multicastSocket = new MulticastSocket(multicastPort);
			multicastSocket.setBroadcast(true);
			multicastSocket.setTimeToLive(multicastTTL);
			InetAddress group = InetAddress.getByName(multicastIP);
			if (!multicastIP.equals("127.0.0.1"))
				multicastSocket.joinGroup(group);
			System.out.println("\n" + getLocalName()
					+ ">Iscritto al gruppo multicast(" + multicastIP + ":"
					+ multicastPort + ")");
		} catch (IOException e) {
			WrapperErrori.wrap("Inizializzazione multicast socket fallita", e);
			doDelete();
		}

		ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
		discover = new DiscoverBehavior(this, activePlatform);
		localPlatform = discover.getLocalPlatform();
		addBehaviour(tbf.wrap(discover));
		ActivePlatformService activePlatformService = new ActivePlatformService(
				this, activePlatform);
		discover.addObserver(activePlatformService);
		addBehaviour(activePlatformService);
	}

	@Override
	protected void takeDown() {
		sendMessage(FactoryMessage.createMessage(ConstantTypeMsg.EXIT,
				localPlatform));
		if (promptActivityPlatform)
			System.out.println("Exit message sended!");
		InetAddress group;
		try {
			group = InetAddress.getByName(multicastIP);
			if (!multicastIP.equals("127.0.0.1"))
				multicastSocket.leaveGroup(group);
		} catch (UnknownHostException e) {
			WrapperErrori.wrap("", e);
		} catch (IOException e) {
			WrapperErrori.wrap("", e);
		}
		multicastSocket.close();
		discover.setTerminate(true);
	}

}
