/**
 * 
 */
package jade.core.remoteAgents.impl;

import jade.core.AgentRuntime;
import jade.core.remoteAgents.PlatformConnector;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.LEAPACLCodec;
import jade.util.Logger;
import jade.util.leap.Serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Simple socket based implementation of the PlatformConnector interface
 * 
 * @author Telefónica I+D
 */
public class PlatformConnectorImpl extends RafConnection implements PlatformConnector, Serializable {
	

	private static final long serialVersionUID = -32828532695493030L;
	
	private final int DEFAULT_RAM_PORT = 2500;
	private final String DEFAULT_RAM_HOST = "localhost";
	
	private int timeout = 5000;
	private int timeReconnect = 2000;
	
	private Listener listener = null;
	private String agentName = null;
	private String platformName = null;
		
	private String remoteAgentManagerHost = DEFAULT_RAM_HOST;
	private int remoteAgentManagerPort = DEFAULT_RAM_PORT;
	
	private boolean constructorParameters= false;
	private boolean connectedBefore = false;
	private boolean joined = false;
	
	protected Socket sc;
	
	private ConnectionReader connectionReader= null;
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	
	/**
	 * Constructor of the class.
	 * looking for the address and port to connect a remoteAgentManager
	 */
	public PlatformConnectorImpl() {
	}

	/**
	 * Constructor of the class. 
	 * 
	 * @param port is the port number to listen for. 
	 * @param host is the host that is going to connect
	 */
	public PlatformConnectorImpl(String host, int port) {
		this.remoteAgentManagerHost = host;
		this.remoteAgentManagerPort = port;
		this.constructorParameters = true;
	}
	
	/**
	 * Set the time will wait to try to reconnect to remoteAgentManager
	 * @param timeout
	 */
	public void setTimeReconnect(int time){
		this.timeReconnect = time;
	}
	
	/**
	 * Get the time will wait to try to reconnect to remoteAgentManager
	 * @return timeout
	 */
	public int getTimeReconnect(){
		return this.timeReconnect;
	}
	/**
	 * Set the time will wait to receive data from a remoteAgentManager
	 * @param timeout
	 */
	public void setTimeOut(int timeout){
		this.timeout=timeout;
	}
	
	/**
	 * Get the time will wait to receive data from a remoteAgentManager
	 * @return timeout
	 */
	public int getTimeOut(){
		return this.timeout;
	}
	
	/**
	 *  Initialize the listener
	 * @param l is the listener we used to listen to
	 */
	public void initialize(Listener l) throws Exception {
		listener = l;
	}
	
	/**	
	 *	Sends a JOIN request and awaits a response. If that answer is JOIN_OK returns the 
	 *   information that comes with the packet. If an exception raises JOIN_NOK.
	 *   It creates a listening thread
	 *   If we do not have the data from remoteAgentManager, look for them
	 * @param agentName	name of the agent
	 * @param platformName 	name of the platform
	 * @exception 
	 * @return Information packet
	 *
	 */
	public String[] joinPlatform(String agentName, String platformName) throws Exception {
		
		if(!constructorParameters && !connectedBefore){
			myLogger.log(Logger.INFO, "Looking for a host to connect");
			lookForAHost();
			myLogger.log(Logger.INFO, "HOST: " + this.remoteAgentManagerHost + " PORT: " + this.remoteAgentManagerPort);
		}
		
		if (! joined){
			this.agentName = agentName;
			this.platformName = platformName;
			String[] dev=null;
			
			try{
				 dev = join(this.agentName, this.platformName);
			}catch(Exception e){
				
				if (e instanceof ClassNotFoundException){
					myLogger.log(Logger.SEVERE,	"Failure to receive a package");
				}else if (e instanceof IOException){
					myLogger.log(Logger.SEVERE,"Connection failure in joinPlatform");
				}
				
				try{ 
					Thread.sleep(timeReconnect);
					dev = join(this.agentName, this.platformName);
				}catch(Exception ee){
					myLogger.log(Logger.INFO,"Failure in the second attempt to join");
				}

			}
			
			if (dev != null) {
				joined = true;
				connectionReader = new ConnectionReader(this);
				connectionReader.start();
				connectedBefore = true;
			}else{
				myLogger.log(Logger.SEVERE,"Can't join the platform.");
				throw new Exception("Can't join the platform.");
			}
			
			return dev;
			
		}else{
			myLogger.log(Logger.WARNING,"Already connected to the platform");
			throw new Exception("Already connected to the platform");	
		}
	}

	/**
	 * 	The Agent  wants to disassociate from AgentManagerConnector. 
	 * 	@exception if not connected, throws Exception
	 */
	public void leavePlatform() throws Exception {
		if (!isAlive()) {
			throw new Exception();
		} else{
			leave();
		}
	}

	/** 	
	 * Serializes and sends an ACLmessage
	 * @param ACLMessage
	 */
	public void msgOut(ACLMessage msg) throws Exception {
		if (joined){
			byte[] bb = serializeACLMessage(msg);
			RafPacket send = new RafPacket(RafPacket.MESSAGE_OUT,bb);
			sendPacket(send);
		}else{
			myLogger.log(Logger.WARNING, "No connection between AgentConnector and PlatformConnector");
		}
	}

	/**
	 * the agent in the device in the HAN dies
	 */
	public void shutdown() {
		try {
			leavePlatform();
		} catch (Exception e) {}
	}
	
	/**
	 * sends a packet with type MESSAGE_JOIN and information agenteName 
	 * and platformName and expects to receive a JOIN_OK or JOIN_NOK
	 * 
	 * @param agentName Name of the agent
	 * @param platformName 	Name of the platform
	 * @return the information package received
	 * @throws Exception 
	 */
	public String[] join(String agentName, String platformName) throws Exception {
		
		String[] dev = null;
		
		String[] info = {agentName,platformName};
		
		RafPacket send = null;
		
		if(!connectedBefore)
			send = new RafPacket(RafPacket.MESSAGE_JOIN,info);
		else
			send = new RafPacket(RafPacket.MESSAGE_RECONNECT,info);
		
		RafPacket received;
		open();
		sendPacket(send);
		do{
			sc.setSoTimeout(5000);
			received = receivePacket();
		}while (received.getPacketType() != RafPacket.JOIN_OK &&
			   received.getPacketType() != RafPacket.JOIN_NOK);
		if (received.getPacketType()== RafPacket.JOIN_OK){
			dev = received.getInfo();
			sc.setSoTimeout(0); // infinite timeout
		}
		
		return dev;
	}
	
	/**
	 * open the connection
	 * @throws Exception
	 */
	public void open ()throws IOException{
		open(remoteAgentManagerHost,remoteAgentManagerPort);
	}
	
	/**
	 * open the connection
	 * @param host address of the host we want to connect
	 * @param port port of the host we want to connect
	 * @throws IOException
	 */
	public void open(String host,int port)throws IOException{
		myLogger.log(Logger.INFO,"Opening connection with Host: "+ host + " Port: " +port);
		sc = new Socket(host,port);
		os = sc.getOutputStream();
		is = sc.getInputStream();
	}
	
	/**
	 * Send a packet with type MESSAGE_LEAVE and close the connection
	 * @throws Exception
	 */
	synchronized public void leave ()throws Exception{
		RafPacket msg = new RafPacket(RafPacket.MESSAGE_LEAVE);
		sendPacket(msg);
		joined = false;
		close();
	}
	
	/**
	 * Close the connection
	 */
	synchronized public void close() throws Exception{
		super.close();
		if (sc != null) {
			sc.close();
			sc = null;
		}
		joined = false;
		myLogger.log(Logger.INFO, "Connection closed");
	}
	
	/**
	 * is the connection open?
	 * @return  true if the connection is open
	 */
	public boolean isAlive() {
		if (sc==null){
			myLogger.log(Logger.INFO,"Not alive cause sc=null");
			return false;
		}else {
			return (!sc.isClosed());
		}
	}

	/** 
	* This variable is only used within the ConnectionReader class,
	* but is declared externally since it must "survive" when 
	* a ConnectionReader is replaced
	*/
	private int cnt = 0;
	
	/**
	 * Inner class ConnectionReader.
	 * This class is responsible for reading incoming packets (incoming commands and responses
	 * to outgoing commands)
	 */
	private class ConnectionReader extends Thread {
		private int myId;
		private PlatformConnectorImpl myConnection = null;
		
		/**
		 * Constructor 
		 * 			
		 * @param c 
		 */
		public ConnectionReader(PlatformConnectorImpl c) {
			super();
			myConnection = c;
			//#MIDP_EXCLUDE_BEGIN
			setName("ConnectionReader-"+myId);
			myLogger.log(Logger.INFO,"Starting " + this.getName());
			//#MIDP_EXCLUDE_END
		}
		
		/**
		 * Listener thread. when a packet is received, look at the 
		 * type of model and perform an action or another depending on this
		 */
		public void run() {
			myId = cnt++;
			myLogger.log(Logger.INFO, this.getName()+ " started");
			
			try {
				while (isConnected()) {
					RafPacket pkt= myConnection.receivePacket();
									
					if (pkt != null) {					
						if (pkt.getPacketType() == RafPacket.MESSAGE_IN){
							myLogger.log(Logger.INFO, "Received MESSAGE_IN");
							ACLMessage msg; 
							byte[] bb = pkt.getACLMessage();
							DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bb));
							msg = LEAPACLCodec.deserializeACL(dis);
							listener.msgIn(msg);
						}
						else if (pkt.getPacketType() == RafPacket.MESSAGE_REMOTE_AGENT_PROXY_DEAD){
							myLogger.log(Logger.INFO, "Received MESSAGE_REMOTE_AGENT_PROXY_DEAD");
							listener.handleDead();
							close();
						}
					}
				}
			}catch (IOException ioe) {
				if(isConnected()){
					ioe.printStackTrace();
					myLogger.log(Logger.WARNING,"Connection failure");
					handleDisconnection();
					myLogger.log(Logger.INFO,"Trying to reconnect ...");
					while(!joined){
						try {
							sleep(timeReconnect);
							AgentRuntime.getInstance().joinPlatform();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					myLogger.log(Logger.INFO,"Connection restored");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			myLogger.log(Logger.INFO, "CR-"+myId+" terminated");
		}
		
		private void handleDisconnection() {
			myLogger.log(Logger.INFO, "CR-" +myId+" handle disconnection");
			listener.handleDead();
			try {
				close();
			} catch (Exception e) {
				myLogger.log(Logger.WARNING,"Failed to close the connection");
				e.printStackTrace();
			}
			
		}
	} // END of inner class ConnectionReader
	
	/**
	 *  
	 * @return isAlive()
	 */
	synchronized private final boolean isConnected() {
		return isAlive();
	}
	
	/**
	 * Uses UDP protocol to send a message to all computers on your subnet.
	 * When receive a confirmation message updates its connection parameters
	 * as included in the received message.
	 * The listening port of remoteAgentManager requests for data is 2501
	 * @return 	Address and port of a remoteAgentManager
	 */
	private void lookForAHost(){
		try {
			int serverPort = RemoteAgentManagerImp.udpPortNumber;
			DatagramSocket socketUDP = new DatagramSocket();
								
			InetAddress subnet = InetAddress.getByName("255.255.255.255");
			byte[] buffer = new byte[1024];
			String msg ="LOOKING_FOR_A_REMOTE_AGENT_MANAGER";
			buffer = msg.getBytes();
			DatagramPacket dataSent = new DatagramPacket(buffer, msg.length(), subnet,serverPort);
		
			socketUDP.send(dataSent);

			byte[] buffer2 = new byte[1024];
			DatagramPacket dataRecieved = new DatagramPacket(buffer2,buffer2.length);
			String[] info;
			do{
				socketUDP.setSoTimeout(5000);
				socketUDP.receive(dataRecieved);
				msg = (String) new String(dataRecieved.getData()).subSequence(0,dataRecieved.getLength());
				info=msg.split(":");
			}while(! info[0].equals("CONFIRM"));
				this.remoteAgentManagerHost = dataRecieved.getAddress().getHostAddress();
				this.remoteAgentManagerPort = Integer.parseInt(info[1]);
				socketUDP.close();
		} catch (IOException e) {
			myLogger.log(Logger.WARNING,"Too much time waiting for a response from a remoteAgentManager");
		} catch (Exception e) {
		}
	}

	/**
	 * Serializes an ACLMessage buscar
	 * @param msg ACLMessage to serialize
	 * @return ACLMessage serialized
	 * @throws IOException
	 */
	private byte[] serializeACLMessage(ACLMessage msg) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		LEAPACLCodec.serializeACL(msg, dos);
		byte[] bb = baos.toByteArray();
		return bb;
	}
}
