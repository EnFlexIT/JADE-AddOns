package jade.core.remoteAgents.impl;

import jade.core.Agent;
import jade.core.remoteAgents.RemoteAgentManager;
import jade.util.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Implementation of the RemoteAgentManager interface
 * 
 * @author Telefónica Implementation of the RemoteAgentManager interface
 */
public class RemoteAgentManagerImp extends RemoteAgentManager {

	private static RemoteAgentManagerImp theInstance;

	public static RemoteAgentManagerImp getInstance() {
		return theInstance;
	}

	private final Logger logger = Logger.getMyLogger(getClass().getName());
	
	public static int DEFAULT_PORT = 2500;
	public static int DEFAULT_UDP_PORT = 2501;

	private JoinPlatformListener joinListener; // Waiting for join requests
	private UDPListener udpListener; // Waiting for RemoteAgentManager search requests
	
	private int portNumber = DEFAULT_PORT;
	private int udpPortNumber = DEFAULT_UDP_PORT;
	
	private HashMap<String, AgentConnectorImp> connectors = new HashMap<String, AgentConnectorImp>();

	private static final long serialVersionUID = 1L;

	private static final int ONE_SEC_AS_MS = 1000;;

	/**
	 * 
	 * agent initializations
	 */
	protected void setup() {

		super.setup();

		theInstance = this;

		// Printout a welcome message
		logger.log(Logger.INFO, "RemoteAgentManagerImp startup");
		
		assignedParameters((String[])getArguments());
		
		try {
			// Start Server Listener
			udpListener  = new UDPListener(udpPortNumber, this);
		} catch (Exception e) {
			logger.log(Logger.SEVERE, "Failed to start UDPListener "+ e);
			e.printStackTrace();
			doDelete();
		}

		try {
			// Start Server Listener
			joinListener = new JoinPlatformListener(portNumber, this);
		} catch (Exception e) {
			logger.log(Logger.SEVERE, "Failed to start JoinPlatformListener"
					+ e);
			e.printStackTrace();
			doDelete();
		}
		
	}

	/**
	 * takeDown: stops the server threads
	 */
	protected void takeDown() {
		try {
			logger
					.log(Logger.INFO,
							" takeDown(). Closing JoinPlatformListener");

			if (joinListener != null) {
				joinListener.interrupt();
				joinListener.closeDown();
				joinListener.join(ONE_SEC_AS_MS);
				joinListener = null;
			}
			
			if (udpListener != null) {
				udpListener.interrupt();
				udpListener.closeDown();
				udpListener.join(ONE_SEC_AS_MS);
				udpListener = null;
			}
		} catch (Exception e) {
			logger.log(Logger.WARNING, "Error closing JoinPlatformListener "
					+ e.toString());
		}
	}

	/**
	 * Private thread to wait for JOIN REQUESTS
	 */
	private class JoinPlatformListener extends Thread {

		/** my logger */
		private Logger logger = Logger.getMyLogger(JoinPlatformListener.class
				.getName());

		protected ServerSocket serverSocket;

		private Agent myAgent;
		private boolean done = false;

		/**
		 * Constructor of the class. It creates a ServerSocket to listen for
		 * connections on a given port
		 * 
		 * @param port Is the port number to listen for. If 0, then it uses the
		 *            default port number.
		 * @param user Agent to be used to process the incoming requests
		 */
		JoinPlatformListener(int port, Agent a) {
			myAgent = a;
			setName(myAgent.getLocalName() + "-SocketListener");
			if (port == 0) {
				port = DEFAULT_PORT;
			}
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				logger.log(Logger.SEVERE,
					"Failed to create the ServerSocket. Another server listening on that port");
				e.printStackTrace();
				myAgent.doDelete();
				return;
			}
			logger.log(Logger.INFO, "Agent " + getLocalName()
					+ " waiting for JOIN requests on port " + port);
			start();
		}

		/**
		 * The body of the server thread. It is executed when the start() method
		 * of the server object is called. Loops forever, listening for and
		 * accepting connections from clients.
		 */
		public void run() {
			done = false;
			while (!done) {
				RafPacket received;
				String[] info = null;
				Socket clientSocket = null;
				boolean establishedConnection = false;
				AgentConnectorImp connector = null;
				try {
					logger.log(Logger.INFO, "Waiting for request");
					clientSocket = serverSocket.accept();
					InetAddress clientAddres = clientSocket.getInetAddress();
					logger.log(Logger.INFO, "Received request. Processing it");
					connector = new AgentConnectorImp(clientSocket);
					received = connector.receivePacket();
					logger.log(Logger.INFO, "Received  "+ received.getStringPacketType()
							+ "\n Client: "+ clientAddres.getHostAddress() 
							+ "\n Port: "	+ clientSocket.getPort());
					if (received.getPacketType() == RafPacket.MESSAGE_JOIN) {
						logger.log(Logger.INFO,	"Received JOIN. HANDLE NEW AGENT");
						establishedConnection = true;
						try {
							info = received.getInfo();
							String agentName = info[0];
							String platformName = info[1];
							logger.log(Logger.INFO,	"Received request to join: " +
									"AgentName = "+ agentName + 
									", platformName " + platformName);
							info = handleNewAgent(agentName, platformName,connector);
						
							connectors.put(agentName, connector);
						} catch (Exception e) {
							logger.log(Logger.WARNING,"Error handlingNewAgent " + e.getMessage());
							establishedConnection = false;
						}
						connector.sendResult(establishedConnection, info);
						if (establishedConnection) {
							connector.startConnectionReader();
							
						}
					}else if (received.getPacketType() == RafPacket.MESSAGE_RECONNECT) {
						logger.log(Logger.INFO,	"Received RECONNECT message.");
						establishedConnection = true;
						try {
							info = received.getInfo();
							String agentName = info[0];
							String platformName = info[1];
							logger.log(Logger.INFO,	"Received request to reconnect: " +
									"AgentName = "+ agentName + 
									", platformName " + platformName);
							
							AgentConnectorImp myConnector = connectors.get(agentName);
							if(myConnector != null){
								myConnector.listener.handleLeft();
							}
							connectors.remove(agentName);
							
							info = handleNewAgent(agentName, platformName,connector);
							connectors.put(agentName, connector);
						} catch (Exception e) {
							logger.log(Logger.WARNING,"Error handlingNewAgent " + e.getMessage());
							establishedConnection = false;
						}
						connector.sendResult(establishedConnection, info);
						if (establishedConnection) {
							connector.startConnectionReader();
							
						}
					}
				} catch (IOException con) {
					logger.log(Logger.WARNING, "Connection problems");
					con.printStackTrace();
				} catch (Throwable any) {
					try {
						if (connector != null) {
							connector.close();
						} else if (clientSocket != null) {
							clientSocket.close();
						}
						logger.log(Logger.WARNING, any.getMessage());
						any.printStackTrace();
					} catch (Exception e) {
						logger.log(Logger.WARNING,
								"Problems to close the connection");
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * stop listening
		 */
		protected void closeDown() {
			logger.log(Logger.INFO, "closeDown Server");
			done = true;
			try {
				if (serverSocket != null) {
					serverSocket.close();
					serverSocket = null;
				}
			} catch (IOException e) {
				logger.log(Logger.WARNING, "Server closeDown error: "
						+ e.toString());
			}
		}

		/**
		 * try to clean up the server thread
		 */
		protected void finalize() {
			closeDown();
		}
	}// end of JoinPlatformListener

	private class UDPListener extends Thread {

		private static final int DEFAULT_PORT = 2501;
		
		private Agent myAgent;
		protected DatagramSocket socketUDP;
		private boolean done = false;

		/** my logger */
		private Logger logger = Logger.getMyLogger(UDPListener.class.getName());

		public UDPListener(int port, Agent a) {
			logger.log(Logger.INFO, "Starting a new UDPListerner");
			myAgent = a;
			setName(myAgent.getLocalName() + "-UDPSocketListener");
			
			if(port == 0) {
				port = DEFAULT_PORT;
			}
			try {
				socketUDP = new DatagramSocket(port);
			} catch (SocketException e) {
				logger.log(Logger.SEVERE, "Error while opening socket.");
				myAgent.doDelete();
				e.printStackTrace();
				return;
			}
			start();
		}

		public void run() {
			logger.log(Logger.INFO,"UDP socket open");
			try {
				byte[] buffer = new byte[1024];
				done = false;
				//Construct the DatagramPacket to receive petitions
				while (!done) {
					logger.log(Logger.INFO,"Waiting UDP  request in Port: " + udpPortNumber+ "...");
					DatagramPacket data = new DatagramPacket(buffer, buffer.length);
					socketUDP.receive(data);
					String msg =  (String) new String(data.getData()).subSequence(0,data.getLength());
					logger.log(Logger.INFO,"Datagram received from the host: "
							+ data.getAddress().getHostAddress()+ " from the remote port "
							+ data.getPort());
					if (msg.equals("LOOKING_FOR_A_REMOTE_AGENT_MANAGER")){
						msg = "CONFIRM:"+portNumber;
						byte[] info = msg.getBytes();
						DatagramPacket answer = new DatagramPacket(info,msg.length(), data.getAddress(), data.getPort());
						socketUDP.send(answer);
					}
				}

			} catch (IOException e) {
				System.out.println("IO: " + e.getMessage());
			}
		}
		
		/**
		 * stop listening
		 */
		protected void closeDown() {
			logger.log(Logger.INFO, "closeDown Server");
			done = true;

			if (socketUDP != null) {
				socketUDP.close();
				socketUDP = null;
			}
		}
		/**
		 * try to clean up the server thread
		 */
		protected void finalize() {
			closeDown();
		}

	}// end of UDPListener
	
	private void assignedParameters(String[] args) {
		if (args != null&& args.length!= 0){
			for(String argu : args){
				try{
					String[] par = argu.split(":");
					if(par.length==2){
						if(par[0].compareTo("tcpport") == 0){
							try{
								portNumber = Integer.parseInt(par[1]);
							}catch(Exception e){
								// Do nothing
								logger.log(Logger.WARNING, "Invalid parameter tcpport");
							}
						}else if(par[0].compareTo("udpport") == 0){
							try{
								udpPortNumber = Integer.parseInt(par[1]);
							}catch(Exception e){
								// Do nothing
								logger.log(Logger.WARNING, "Invalid parameter udpport");
							}
						}
					}
				}catch(Exception e){}
			}
		}
	}
}