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

package test.common;

import jade.util.leap.*;

import jade.domain.*;
import jade.core.Agent;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.AgentManager;
import jade.lang.acl.ACLMessage;

import jade.domain.JADEAgentManagement.*;
import jade.domain.FIPANames;
import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;
import jade.content.lang.sl.SLCodec;

import test.common.agentConfigurationOntology.*;
import test.common.remote.RemoteManager;

import java.util.Date;
import java.rmi.*;
import java.net.InetAddress;

/**
   Class including utility static methods for launching/killing
   agents, requesting generic AMS actions and creating remote JADE
   instances.
   @author Giovanni Caire - TILAB
 */
public class TestUtility {
	private static boolean verbose = true;
	
	public static final String CONFIGURABLE_AGENT = "test.common.ConfigurableAgent";
	
  
  private static jade.content.ContentManager cm = new jade.content.ContentManager();
  private static jade.content.lang.Codec codec = new jade.content.lang.leap.LEAPCodec();
  private static jade.content.onto.Ontology onto = AgentConfigurationOntology.getInstance();
      
  private static test.common.remote.RemoteManager defaultRm = null;
  
  static {
  	cm.registerLanguage(codec);
  	cm.registerOntology(onto);
  	cm.registerLanguage(new SLCodec(0), FIPANames.ContentLanguage.FIPA_SL0);
  	cm.registerOntology(JADEManagementOntology.getInstance());
  }
  
  /**
     Create a generic agent in the local container of the local platform
	 */
  public static AID createAgent(Agent a, String agentName, String agentClass, String[] agentArgs) throws TestException {
		return createAgent(a, agentName, agentClass, agentArgs, null, null);
  }

  /**
     Create a generic agent in the platform administrated by the indicated AMS
	 */
  public static AID createAgent(Agent a, String agentName, String agentClass, String[] agentArgs, AID amsAID, String containerName) throws TestException {
		if (amsAID == null) {
			amsAID = a.getAMS();
		}
		
		CreateAgent ca = new CreateAgent();
		// Agent name
		ca.setAgentName(agentName);
		// Agent class
		ca.setClassName(agentClass);
		// Agent args
		if (agentArgs != null) {
			for (int i = 0; i < agentArgs.length; ++i) {
				ca.addArguments(agentArgs[i]);
			}
		}
		// Container where to create the agent
		if (containerName != null) {
			ca.setContainer(new ContainerID(containerName, null));
		}
		else {
  		if (amsAID.equals(a.getAMS())) { 
	  		ca.setContainer((ContainerID) a.here());
			}
			else {
				ca.setContainer(new ContainerID(AgentManager.MAIN_CONTAINER_NAME, null));
			}
		}
  		
    try {
  		requestAMSAction(a, amsAID, ca);
    	return createNewAID(agentName, amsAID);
    }
    catch (TestException te) {
    	throw new TestException("Error creating Agent "+agentName, te.getNested());
    }
  }

  /**
     Kill an agent
   */
  public static void killAgent(Agent a, AID agentAID) throws TestException {
  	killAgent(a, agentAID, null);
  }
  
  /**
     Kill an agent living in the platform administrated by the 
     indicated AMS
	 */
  public static void killAgent(Agent a, AID agentAID, AID amsAID) throws TestException {
  	if (amsAID == null) {
			amsAID = createNewAID("ams", agentAID);
  	}
  	
		ACLMessage request = createRequestMessage(a, amsAID, FIPANames.ContentLanguage.FIPA_SL0, JADEManagementVocabulary.NAME);

		KillAgent ka = new KillAgent();
		ka.setAgent(agentAID);
		
    try {
  		requestAMSAction(a, amsAID, ka);
    }
    catch (TestException te) {
    	throw new TestException("Error killing TargetAgent "+agentAID.getName(), te.getNested());
    }
  }
  
  /**
     Request an AMS agent to perform a given action in the JADE
     management ontology.
   */
  public static Object requestAMSAction(Agent a, AID amsAID, AgentAction action) throws TestException {
    try {
    	// Prepare the request
  		ACLMessage request = createRequestMessage(a, amsAID, FIPANames.ContentLanguage.FIPA_SL0, JADEManagementVocabulary.NAME);

  		Action act = new Action();
  		act.setActor(amsAID);
  		act.setAction(action);
  		
  		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
  		request.setOntology(JADEManagementVocabulary.NAME);
  		cm.fillContent(request,act);
    	   	
    	// Send message and collect reply
    	ACLMessage inform = FIPAServiceCommunicator.doFipaRequestClient(a, request);
    	
    	// Extract the result from the reply (if any)
    	ContentElement ce = cm.extractContent(inform);
    	if (ce instanceof Done) {
    		// No result to return
    		return null;
    	}
    	else if (ce instanceof Result) {
    		return ((Result) ce).getValue();
    	}
    	else {
    		throw new TestException("Unknown notification received from "+amsAID);
    	}
    }
    catch (Exception e) {
    	throw new TestException("Error executing action "+action, e);
    }
  }
  	
  /////////////////////////////////////////////
  // Methods to configure a ConfigurableAgent 
  /////////////////////////////////////////////
  
  /**
     Add a behaviour of the indicated class to the indicated ConfigurableAgent
   */
  public static void addBehaviour(Agent a, AID targetAID, String behaviourClassName) throws TestException { 
  	try {
  		ACLMessage request = createRequestMessage(a, targetAID, codec.getName(), onto.getName());
  		
  		AddBehaviour ab = new AddBehaviour(null, behaviourClassName);
  		synchronized (cm) {
	  		cm.fillContent(request, ab);
  		}
  		
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    }
    catch (Exception e) {
    	throw new TestException("Error adding behaviour "+behaviourClassName+" to agent "+targetAID.getName(), e);
    }
  }
  
  /**
     Make the indicated ConfigurableAgent perform the indicated action
   */
  public static void forceAction(Agent a, AID targetAID, AgentAction action) throws TestException { 
  	try {
  		ACLMessage request = createRequestMessage(a, targetAID, codec.getName(), onto.getName());
  		
  		synchronized (cm) {
	  		cm.fillContent(request, action);
  		}
  		
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    }
    catch (Exception e) {
    	throw new TestException("Error forcing action "+action+" to agent "+targetAID.getName(), e);
    }
  }
  
  /////////////////////////////////////////////
  // Methods to launch remote JADE instances 
  // and to configure the RemoteManager 
  /////////////////////////////////////////////
  
  /**
     Launch a new instance of JADE in a separate process 
   */
  public static JadeController launchJadeInstance(String instanceName, String classpath, String jadeArgs, String[] protoNames) throws TestException { 
  	return launchJadeInstance(null, instanceName, classpath, jadeArgs, protoNames);
  }
  
  /**
     Launch a new instance of JADE in a separate process using the
     indicated RemoteManager
   */
  public static JadeController launchJadeInstance(RemoteManager rm, String instanceName, String classpath, String jadeArgs, String[] protoNames) throws TestException { 
		JadeController jc = null;
		if (rm == null) {
			rm = defaultRm;
		}
		
		if (rm != null) {
			// If a RemoteManager is set, use it and launch the JADE instance 
			// remotely
			try {
				int id = rm.launchJadeInstance(instanceName, classpath, jadeArgs, protoNames);
				jc = new RemoteJadeController(rm, id);
			}
			catch (RemoteException re) {
				throw new TestException("Communication error launching JADE instance remotely", re);
			}
		}
		else {
			// Otherwise launch the JADE instance locally
  		if (classpath == null || classpath.startsWith("+")) {
  			String currentCp = System.getProperty("java.class.path");
  			if (classpath == null) {
  				classpath = currentCp;
  			}
  			else {
  				classpath = classpath.substring(1)+";"+currentCp;
  			}
			}
			Logger.getLogger().log("Launching JADE. Classpath is "+classpath);
			jc = new LocalJadeController(instanceName, new String("java -cp "+classpath+" jade.Boot "+jadeArgs), protoNames);
		}
		return jc;
  }

  public static RemoteManager createRemoteManager(String hostName, int port, String managerName) throws TestException {
  	String remoteManagerRMI = new String("rmi://"+hostName+":"+port+"//"+managerName);
  	try {
  		return (RemoteManager) Naming.lookup(remoteManagerRMI);
  	}
  	catch (Exception e) {
  		throw new TestException("Error looking up remote manager "+remoteManagerRMI, e);
  	}
  }
  
  public static void setDefaultRemoteManager(RemoteManager rm) {
  	defaultRm = rm;
  }
  
  public static RemoteManager getDefaultRemoteManager() {
  	return defaultRm;
  }
  
  public static String getLocalHostName() throws TestException {
  	try {
			return InetAddress.getLocalHost().getHostName();
  	}
  	catch (Exception e) {
  		throw new TestException("Can't get local host name", e);
  	}
  }
  
  /**
   */
  private static ACLMessage createRequestMessage(Agent sender, AID receiver, String language, String ontology) {
    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
    request.setSender(sender.getAID());
    request.addReceiver(receiver);
    request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    request.setLanguage(language);
    request.setOntology(ontology);
    request.setReplyWith("rw"+sender.getName()+(new Date()).getTime());
    request.setConversationId("conv"+sender.getName()+(new Date()).getTime());
    return request;
  }

  
  public static AID createNewAID(String newName, AID anAID) {
  	String name = anAID.getName();
  	String hap = null;
    int atPos = name.lastIndexOf('@');
    if(atPos == -1)
      hap = name;
    else
      hap = name.substring(atPos + 1);
      
    AID id = new AID(newName+"@"+hap, AID.ISGUID);
    Iterator it = anAID.getAllAddresses();
    while (it.hasNext()) {
    	id.addAddresses((String) it.next());
    }
    return id;
  }
  
  /**
   */
  public static void log(String s) {
    if (verbose) {
      System.out.println(s);
    } 
  } 	
		
  /**
   */
  public static void log(Object o) {
  	log(o.toString());
  }
  
  /**
   */
  public static void log(Throwable t) {
    if (verbose) {
      t.printStackTrace();
    } 
  } 	
		
  /**
   */
  public static void setVerbose(boolean b) {
  	verbose = b;
  }
}
  
  