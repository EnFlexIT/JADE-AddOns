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
import jade.lang.acl.ACLMessage;
import jade.onto.basic.Action;
import jade.onto.*;
import jade.lang.Codec;
import jade.lang.sl.SL0Codec;
import jade.domain.JADEAgentManagement.*;
import jade.proto.FIPAProtocolNames;

import test.common.agentConfigurationOntology.*;

import java.util.Date;

/**
   Class including static methods useful during test setup/takedown
   to launch/kill responder agents as needed during the test 
   @author Giovanni Caire - TILAB
 */
public class TestUtility {
	public static final String RESPONDER_CLASS_NAME = "test.common.ConfigurableAgent";
	
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
     Create an agent (that will act as a responder) in the local platform
	 */
  public static AID createResponder(Agent a, String respName) throws TestException {
  	return createResponder(a, respName, Agent.getAMS());
  }
  
  /**
     Create an agent (that will act as a responder) in the platform
     administrated by the indicated AMS
	 */
  public static AID createResponder(Agent a, String respName, AID amsAID) throws TestException {
    try {
  		ACLMessage request = createRequestMessage(a, amsAID, SL0Codec.NAME, JADEAgentManagementOntology.NAME);

  		CreateAgent ca = new CreateAgent();
  		ca.setAgentName(respName);
  		ca.setClassName(RESPONDER_CLASS_NAME);
  		if (amsAID.equals(a.getAMS())) { 
	  		ca.setContainer((ContainerID) a.here());
  		}
  		else {
  			ca.setContainer(new ContainerID("Main-Container", null));
  		}
  		
  		Action act = new Action();
  		act.setActor(amsAID);
  		act.setAction(ca);
  		
    	synchronized(c) { //must be synchronized because this is a static method
      	request.setContent(encode(act,c,o));
    	}
    	
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    	
    	return createAID(respName, amsAID);
    }
    catch (Exception e) {
    	throw new TestException("Error creating ResponderAgent "+respName, e);
    }
  }

  /**
     Kill an agent that as acted as a responder
	 */
  public static void killResponder(Agent a, AID resp) throws TestException {
  	try {
  		ACLMessage request = createRequestMessage(a, resp, codec.getName(), onto.getName());
  		
  		Quit q = new Quit();
  		synchronized (cm) {
	  		cm.fillContent(request, q);
  		}
  		
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    	// Send message and don't collect reply as the responder will 
  		// quit and therefore will not reply
    	//a.send(request);
    }
    catch (Exception e) {
    	throw new TestException("Error killing agent "+resp.getName(), e);
    }
  }
  
  /**
	 */
  /*public static void killResponder(Agent a, String respName, AID amsAID) throws TestException {
    try {
  		ACLMessage request = createRequestMessage(a, amsAID, SL0Codec.NAME, JADEAgentManagementOntology.NAME);

  		KillAgent ka = new KillAgent();
  		ka.setAgent(createAID(respName, amsAID));
  		
  		Action act = new Action();
  		act.setActor(amsAID);
  		act.setAction(ka);
  		
    	synchronized(c) { //must be synchronized because this is a static method
      	request.setContent(encode(act,c,o));
    	}
    	
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
    }
    catch (Exception e) {
    	throw new TestException("Error killing ResponderAgent "+respName, e);
    }
  }*/

  /**
   */
  public static void addBehaviour(Agent a, AID resp, String behaviourClassName) throws TestException { 
  	try {
  		ACLMessage request = createRequestMessage(a, resp, codec.getName(), onto.getName());
  		
  		AddBehaviour ab = new AddBehaviour(null, behaviourClassName);
  		synchronized (cm) {
	  		cm.fillContent(request, ab);
  		}
  		
    	// Send message and collect reply
    	FIPAServiceCommunicator.doFipaRequestClient(a, request);
      }
    catch (Exception e) {
    	throw new TestException("Error adding behaviour "+behaviourClassName+" to agent "+resp.getName(), e);
    }
  }
  
  /**
     Launch a new instance of JADE in a separate process
   */
  public static RemoteController launchJadeInstance(String instanceName, String classpath, String jadeArgs, String[] protoNames) throws TestException { 
		RemoteController rc = new RemoteController(instanceName, new String("java -cp "+classpath+" jade.Boot "+jadeArgs), protoNames);
		return rc;
  }
  
  /**
   */
  static ACLMessage createRequestMessage(Agent sender, AID receiver, String language, String ontology) {
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
  static String encode(Action act, Codec c, Ontology o) throws FIPAException {
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
  
  static AID createAID(String localName, AID amsAID) {
  	String amsName = amsAID.getName();
  	String amsHap = null;
    int atPos = amsName.lastIndexOf('@');
    if(atPos == -1)
      amsHap = amsName;
    else
      amsHap = amsName.substring(atPos + 1);
    AID id = new AID(localName+"@"+amsHap, AID.ISGUID);
    Iterator it = amsAID.getAllAddresses();
    while (it.hasNext()) {
    	id.addAddresses((String) it.next());
    }
    return id;
  }
}
  
  