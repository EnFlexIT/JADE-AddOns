package jade.core;

import jade.core.behaviours.Behaviour;
import jade.core.remoteAgents.PlatformConnector;
import jade.lang.acl.ACLMessage;
import jade.security.Credentials;
import jade.security.JADEPrincipal;
import jade.security.JADESecurityException;
import jade.util.Logger;
import jade.util.leap.Iterator;
import jade.util.leap.Properties;


/**
 * Minimal runtime for a single "remote agent"
 */
public class AgentRuntime {
	private static final int IDLE = 0;
	private static final int STAND_ALONE = 1;
	private static final int JOINED = 2;
	
	private static AgentRuntime theInstance = new AgentRuntime();

	/**
	 * Retrieve the singleton instance of the AgentRuntime
	 * @return the singleton instance of the AgentRuntime
	 */
	public static AgentRuntime getInstance() {
		return theInstance;
	}
	
	private Agent myAgent;
	private PlatformConnector myConnector;
	private ContainerID containerId;
	// Contains the the platform-name, the container-name, the default-df-name and all platform addresses
	private String[] platformInfo;
	private boolean explicitPlatformName = false;
	private Runnable terminator;
	
	private AID amsAID;
	private AID dfAID;
	
	private int status = IDLE;
	
	private Logger myLogger = Logger.getMyLogger(this.getClass().getName());
	
	/**
	 * Start the agent running on top of this AgentRuntime. Note that at that point the agent is in STAND-ALONE mode,
	 * i.e. it is not part of the platform yet.
	 * In order to join the platform the agent will have to call the joinPlatform() method explicitly.
	 * @param agentName The local name of the agent to start
	 * @param agent The agent instance
	 * @param args The agent arguments 
	 * @param platformName The name of the platform. This name, if specified, must match the actual name of the platform that the agent will join.
	 * If the platformName argument is not specified, AMS and DF AID as well as the <code>new AID(name, AID.ISLOCALNAME)</code> constructor will
	 * not be available when this AgentRuntime is not joined to a platform. 
	 * @param PlatformConnector The PlatformConnector to be used to join the platform and exchange messages with remote agents 
	 * @param terminator A Runnable object that will be activated when the agent will terminate.  
	 * @throws Exception If an error occurs.
	 */
	public synchronized void start(String agentName, Agent agent, Object[] args, String platformName, PlatformConnector connector, Runnable terminator) throws Exception {
		if (status == IDLE) {
			if (platformName != null) {
				AID.setPlatformID(platformName);
				explicitPlatformName = true;
			}
			AID agentAID = null;
			if (AID.getPlatformID() != null) {
				amsAID = new AID("ams", AID.ISLOCALNAME);
				dfAID = new AID("df", AID.ISLOCALNAME);
				agentAID = new AID(agentName, AID.ISLOCALNAME);
			}
			else {
				agentAID = new AID(agentName+"@UNKNOWN", AID.ISGUID);
			}
			
			this.terminator = terminator;
			myConnector = connector;
			if (myConnector != null) {
				myConnector.initialize(new PlatformConnector.Listener() {
					public void msgIn(ACLMessage msg) {
						// Incoming message for the local agent
						if (myAgent != null) {
							myAgent.postMessage(msg);
						}
					}
					
					public void handleDead() {
						// Local agent's counterpart in the platform was killed
						synchronized (AgentRuntime.this) {
							if (status == JOINED) {
								myLogger.log(Logger.INFO, "Local agent counterpart terminated --> Enter stand-alone mode");
								clearPlatformInfo();
								status = STAND_ALONE;
							}
						}
					}
				});
			}
			
			myAgent = agent;
			myAgent.setToolkit(new Toolkit());
			myAgent.setArguments(args);
			//#MIDP_EXCLUDE_BEGIN
			myAgent.initMessageQueue();
			//#MIDP_EXCLUDE_END
			status = STAND_ALONE;
			myAgent.powerUp(agentAID, new Thread(myAgent));
		}
	}

	/**
	 * Make the agent living on this AgentRuntime become part of a remote platform
	 * @throws Exception If an error occurs joining the platform
	 */
	public synchronized void joinPlatform() throws Exception {
		if (status == STAND_ALONE) {
			if (myConnector != null) {
				myLogger.log(Logger.INFO, "Joining platform ...");
				platformInfo = myConnector.joinPlatform(myAgent.getLocalName(), AID.getPlatformID());
				myLogger.log(Logger.INFO, "Platform successfully joined");
				if (AID.getPlatformID() == null) {
					// First element is the platform name
					AID.setPlatformID(platformInfo[0]);
					amsAID = new AID("ams", AID.ISLOCALNAME);
					dfAID = new AID("df", AID.ISLOCALNAME);
					myAgent.setAID(new AID(myAgent.getLocalName(), AID.ISLOCALNAME));
				}
				containerId = new ContainerID(platformInfo[1], null);
				dfAID.setLocalName(platformInfo[2]);
				setPlatformAddresses(myAgent.getAID());
				setPlatformAddresses(amsAID);
				setPlatformAddresses(dfAID);
				status = JOINED;
			}
			else {
				throw new Exception("NULL PlatformConnector");
			}
		}
	}
	
	/**
	 * Remove the agent living on this AgentRuntime from the platform it was part of and turn it back to the STAN-ALONE mode 
	 */
	public synchronized void leavePlatform() {
		if (status == JOINED) {
			try {
				myLogger.log(Logger.INFO, "Leaving platform ...");
				myConnector.leavePlatform();
				myLogger.log(Logger.INFO, "Platform succesfully left");
			}
			catch (Exception e) {
				myLogger.log(Logger.WARNING, "Unexpected error leaving platform", e);
			}
			clearPlatformInfo();
			status = STAND_ALONE;
		}
	}
	
	/** 
	 * Check if the agent living on this AgentRuntime is currently part of a platform
	 * @return <code>true</code> if the agent living on this AgentRuntime is currently part of a platform
	 */
	public boolean isJoined() {
		return status == JOINED;
	}
	
	/**
	 * Inner class Toolkit
	 * This is the AgentToolkit for the agent living on this AgentRuntime
	 */
	private class Toolkit implements AgentToolkit {
		//#MIDP_EXCLUDE_BEGIN
		public jade.wrapper.AgentContainer getContainerController(JADEPrincipal principal, Credentials credentials){
			return null;
		}
		//#MIDP_EXCLUDE_END
		
		public final Location here() {
			return containerId;
		}
		
		public final void handleEnd(AID agentID) {
			synchronized (AgentRuntime.this) {
				leavePlatform();
				myAgent.resetToolkit();
				myAgent = null;
				if (myConnector != null) {
					myConnector.shutdown();
					myConnector = null;
				}
				status = IDLE;
			}
			if (terminator != null) {
				terminator.run();
			}
		}
		
		public final void handleChangedAgentState(AID agentID, int from, int to) {
		}
		
		public final void handleSend(ACLMessage msg, AID sender, boolean needClone) {
			try {
				joinPlatform();
				myConnector.msgOut(msg);
			}
			catch (Exception e) {
				e.printStackTrace();
				// An exception here means that we could not transfer the message to the platform -->
				// We need to send back a FAILURE message for each receiver
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.FAILURE);
				reply.setSender(amsAID);
				Iterator it = msg.getAllReceiver();
				if (it.hasNext()) {
					reply.setContent(buildFailureMessageContent((AID) it.next(), e.getMessage()));
					myAgent.postMessage(reply);
					while (it.hasNext()) {
						reply.setContent(null);
						reply = (ACLMessage) reply.clone();
						reply.setContent(buildFailureMessageContent((AID) it.next(), e.getMessage()));
						myAgent.postMessage(reply);
					}
				}
			}
		}
		
		public final void setPlatformAddresses(AID id) {
			AgentRuntime.this.setPlatformAddresses(id);
		}
		
		public final AID getAMS() {
			return amsAID;
		}
		
		public final AID getDefaultDF() {
			return dfAID;
		}
		
		public String getProperty(String key, String aDefault) {
			return null;
		}
		
		//#MIDP_EXCLUDE_BEGIN
		public Properties getBootProperties(){
			return null;
		}
		
		public void handleMove(AID agentID, Location where) throws JADESecurityException, IMTPException, NotFoundException {
		}
		
		public void handleClone(AID agentID, Location where, String newName) throws JADESecurityException, IMTPException, NotFoundException {
		}
		
		public void handlePosted(AID agentID, ACLMessage msg) {
		}
		
		public void handleReceived(AID agentID, ACLMessage msg) {
		}
		
		public void handleBehaviourAdded(AID agentID, Behaviour b) {
		}
		
		public void handleBehaviourRemoved(AID agentID, Behaviour b) {
		}
		
		public void handleChangeBehaviourState(AID agentID, Behaviour b, String from, String to) {
		}
		//#MIDP_EXCLUDE_END
		
		public ServiceHelper getHelper(Agent a, String serviceName) throws ServiceException {
			throw new ServiceNotActiveException(serviceName);
		}
	}  // END of inner class Toolkit
	
	private void setPlatformAddresses(AID id) {
 		id.clearAllAddresses();
		if (platformInfo != null) {
			for (int i = 3; i < platformInfo.length; ++i) {
				id.addAddresses(platformInfo[i]);
			}
		}
	}
	
	private void clearPlatformInfo() {
		containerId = null;
		platformInfo = null;
		if (explicitPlatformName) {
			// An explicit platform name was specified --> keep it and just reset platform addresses from relevant AIDs 
			setPlatformAddresses(myAgent.getAID());
			setPlatformAddresses(amsAID);
			setPlatformAddresses(dfAID);
		}
		else {
			// Reset the platform name and all related stuff
			AID.setPlatformID(null);
			amsAID = null;
			dfAID = null;
			myAgent.setAID(new AID(myAgent.getLocalName()+"@UNKNOWN", AID.ISGUID));
		}
	}
	
	private String buildFailureMessageContent(AID id, String reason) {
		// FIXME: TBD
		return null;
	}
}
