package jade.core.remoteAgents.impl;

import jade.core.remoteAgents.AgentConnector;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Implementation of the AgentConnector interface
 * 
 * @author Telefonica
 */
public class AgentConnectorImp extends RafConnection implements AgentConnector {

	// Listener to forward messages from remote agent
	protected Listener listener;

	// ConnectionReader to listen for incoming packets
	ConnectionReader connectionReader;

	// Socket to send/received messages
	protected Socket socketClient;

	protected boolean joined = false;
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());

	/**
	 * Constructor
	 * 
	 * @param s	: socket
	 * @throws Exception
	 */
	public AgentConnectorImp(Socket s) throws Exception {
		this.socketClient = s;
		this.os = s.getOutputStream();
		this.is = s.getInputStream();

	}

	/**
	 * Serializes an ACLMessage and send it in a packet
	 * 
	 * @param msn: ACLMessage
	 */
	public void msgIn(ACLMessage msg) throws Exception {
		
		if (joined){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			LEAPACLCodec.serializeACL(msg, dos);
			byte[] bb = baos.toByteArray();
			
			RafPacket send = new RafPacket(RafPacket.MESSAGE_IN, bb);
			sendPacket(send);
		}else{
			myLogger.log(Logger.WARNING, "No connection between AgentConnector and PlatformConnector");
		}		
	}

	/**
	 * When remoteAgentProxy dead a notification message
	 * 
	 * @exception
	 */
	synchronized public void notifyDead() throws Exception {
		RafPacket send = new RafPacket(
		RafPacket.MESSAGE_REMOTE_AGENT_PROXY_DEAD);
		sendPacket(send);
		close();
	}

	/**
	 * Initialize the listener
	 * 
	 * @param l : Listener we use to listen
	 */
	
	public void setListener(Listener l) {
		this.listener = l;
	}

	/**
	 * Send the result of the JOIN
	 * 
	 * @param establishedConnection : true if the type of packet is JOIN_OK
	 * @param info : Information received in the packet
	 * @throws Exception
	 */
	public void sendResult(boolean establishedConnection, String[] info)
			throws Exception {
		RafPacket send = new RafPacket();
		if (establishedConnection) {
			joined = true;
			send.setPacketType(RafPacket.JOIN_OK);
			send.setInfo(info);
		} else {
			send.setPacketType(RafPacket.JOIN_NOK);
		}
		sendPacket(send);
	}

	/**
	 * Close connection
	 */
	synchronized public void close() {
		try {
			super.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (socketClient != null) {
			try {
				myLogger.log(Logger.INFO, "Closing sockets");
				socketClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socketClient = null;
		}
		joined = false;
	}

	/**
	 * Starts the listener thread
	 */
	public void startConnectionReader() {
		connectionReader = new ConnectionReader(this);
		connectionReader.start();
	}

	/**
	 * This variable is only used within the ConnectionReader class, but is
	 * declared externally since it must "survive" when a ConnectionReader is
	 * replaced
	 */
	private int cnt = 0;

	/**
	 * Inner class ConnectionReader. This class is responsible for reading
	 * incoming packets (incoming commands and responses to outgoing commands)
	 */
	private class ConnectionReader extends Thread {
		private int myId;
		private AgentConnectorImp myConnection = null;

		public ConnectionReader(AgentConnectorImp c) {
			super();
			myConnection = c;
			// #MIDP_EXCLUDE_BEGIN
			setName("ConnectionReader-" + myId);
			// #MIDP_EXCLUDE_END
		}

		/**
		 * Listener thread. when a packet is received, look at the type of model
		 * and perform an action or another depending on this
		 */
		public void run() {
			myId = cnt++;
			myLogger.log(Logger.INFO, "CR-" + myId + " started");
			try {
				while (isConnected() && joined) {
					RafPacket pkt = myConnection.receivePacket();
					if (pkt != null) {
						if (pkt.getPacketType() == RafPacket.MESSAGE_OUT) {
							myLogger.log(Logger.INFO, "Received message OUT");
							ACLMessage msg = null;
							byte[] bb = pkt.getACLMessage();
							DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bb));
							msg = LEAPACLCodec.deserializeACL(dis);
							listener.msgOut(msg);
						} else if (pkt.getPacketType() == RafPacket.MESSAGE_LEAVE) {
							myLogger.log(Logger.INFO, "Received message LEFT");
							listener.handleLeft();
							close();
						}
					}
				}
			} catch (IOException ioe) {
				myLogger.log(Logger.WARNING,"Connection failure");
				if (isConnected()){
					ioe.printStackTrace();
					handleDisconnection();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			myLogger.log(Logger.INFO, "CR-" + myId + " terminated");
		}

		/**
		 * Disconnected
		 */
		private void handleDisconnection() {
			myLogger.log(Logger.INFO, "CR-" + myId + "handle disconnection");
			listener.handleLeft();
			close();
		}
	} // END of inner class ConnectionReader

	/**
	 * Is the Agent Connector connected?
	 * 
	 * @return true if is Connected
	 */
	synchronized private final boolean isConnected() {
		if (socketClient == null) {
			return false;
		} else {
			return true;
		}
	}
}
