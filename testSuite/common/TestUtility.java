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
import jade.onto.basic.Action;
import jade.onto.*;
import jade.lang.Codec;
import jade.lang.sl.SL0Codec;
import jade.domain.JADEAgentManagement.*;
import jade.proto.FIPAProtocolNames;
import jade.content.AgentAction;

import test.common.agentConfigurationOntology.*;

import java.util.Date;

/**
   Class including static methods useful during test setup/takedown
   to launch/kill responder agents as needed during the test 
   @author Giovanni Caire - TILAB
 */
public class TestUtility {
	private static boolean verbose = true;
	
	public static final String TARGET_CLASS_NAME = "test.common.ConfigurableAgent";
	
  private static Codec c = new SL0Codec();
  private static Ontology o = JADEAgentManagementOntology.instance();

  private static jade.content.ContentManager cm = new jade.content.ContentManager();
 	private static jade.content.lang.Codec codec = new jade.content.lang.leap.LEAPCodec();
  private static jade.content.onto.Ontology onto = AgentConfigurationOntology.getInstance();
  
  static {
  	cm.registerLanguage(codec);
  	cm.registerOntology(onto);
  }
  
  /**
     Create a target agent in the local platform
	 */
  public static AID createTarget(Agent a, String respName) throws TestException {
  	return createTarget(a, respName, Agent.getAMS());
  }
    
  /**
     Create a target agent in the platform administrated by the indicated AMS
	 */
  public static AID createTarget(Agent a, String targetName, AID amsAID) throws TestException {
		return createAgent(a, targetName, TARGET_CLASS_NAME, null, amsAID, null);
  }

  /**
     Create a generic agent in the platform administrated by the indicated AMS
	 */
  public static AID createAgent(Agent a, String agentName, String agentClass, String[] agentArgs, AID amsAID, String containerName) throws TestException {
    try {
  		ACLMessage request = createRequestMessage(a, amsAID, SL0Codec.NAME, JADEAgentManagementOntology.NAME);

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
  		
  		Action act = new Action();
  		act.setActor(amsAID);
  		act.setAction(ca);
  		
    	synchronized(c) { //must be synchronized because this is a static method
      	request.setContent(encode(act, c, o));
    	}
    	
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    	
    	return createNewAID(agentName, amsAID);
    }
    catch (Exception e) {
    	throw new TestException("Error creating ResponderAgent "+agentName, e);
    }
  }

  /**
     Kill a target agent (whereever it is)
   */
  public static void killTarget(Agent a, AID targetAID) throws TestException {
  	AID amsAID = createNewAID("ams", targetAID);
  	killTarget(a, targetAID, amsAID);
  }
  
  /**
     Kill a target agent living in the platform administrated by the 
     indicated AMS
	 */
  public static void killTarget(Agent a, AID targetAID, AID amsAID) throws TestException {
    try {
  		ACLMessage request = createRequestMessage(a, amsAID, SL0Codec.NAME, JADEAgentManagementOntology.NAME);

  		KillAgent ka = new KillAgent();
  		ka.setAgent(targetAID);
  		
  		Action act = new Action();
  		act.setActor(amsAID);
  		act.setAction(ka);
  		
    	synchronized(c) { //must be synchronized because this is a static method
      	request.setContent(encode(act, c, o));
    	}
    	
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    }
    catch (Exception e) {
    	throw new TestException("Error killing TargetAgent "+targetAID.getName(), e);
    }
  }

  /**
     Add a behaviour of the indicated class to the indicated target agent
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
     Launch a new instance of JADE in a separate process
   */
  public static RemoteController launchJadeInstance(String instanceName, String classpath, String jadeArgs, String[] protoNames) throws TestException { 
		if (classpath == null) {
  		classpath = System.getProperty("java.class.path");
		}
		RemoteController rc = new RemoteController(instanceName, new String("java -cp "+classpath+" jade.Boot "+jadeArgs), protoNames);
		return rc;
  }
  
  /**
     Make the indicated target agent perform the indicated action
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
  
  /**
   */
  private static ACLMessage createRequestMessage(Agent sender, AID receiver, String language, String ontology) {
    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
    request.setSender(sender.getAID());
    request.addReceiver(receiver);
    request.setProtocol(FIPAProtocolNames.FIPA_REQUEST);
    request.setLanguage(language);
    request.setOntology(ontology);
    request.setReplyWith("rw"+sender.getName()+(new Date()).getTime());
    request.setConversationId("conv"+sender.getName()+(new Date()).getTime());
    return request;
  }

  /**
   */
  private static String encode(Action act, Codec c, Ontology o) throws FIPAException {
    // Write the action in the :content slot of the request
    List l = new ArrayList();
    try {
      Frame f = o.createFrame(act, o.getRoleName(act.getClass()));
      l.add(f);
    } catch (OntologyException oe) {
      throw new FIPAException(oe.getMessage());
    }
    return c.encode(l,o);
  }
  
  private static AID createNewAID(String newName, AID anAID) {
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
  
  