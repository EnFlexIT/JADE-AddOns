/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/*
 * $Id: ToolRequester.java,v 1.11 2005/08/24 08:28:44 medha Exp $
 * Created on 14.07.2004
 * TODO Copyright notice
 *
 */
package jade.tools.ascml.launcher;

import jade.content.AgentAction;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Iterator;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.DebugOn;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.SniffOn;
import jade.domain.toolagent.ToolAgentParameter;
import jade.domain.toolagent.ToolAgentParameterSet;
import jade.domain.toolagent.ToolAgentParameterSetOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.tools.ascml.model.runnable.RunnableAgentInstance;
import jade.wrapper.ControllerException;
import java.util.HashMap;
import java.util.Vector;


/**
 * @author Tim Niemueller & Sven Lilienthal (ascml@sven-lilienthal.de)
 *
 */
public class ToolRequester {
    
    private String toolClass;
    private String toolPrefix;
    private boolean waitForToolCreate = false;
    private Hashtable<String,StringBuffer> waitingAgents;
    private Hashtable<String,StringBuffer> requestAgents;
    private Hashtable<String,HashMap<String,Vector<String>>> agentTooloptionProperties;
    private int toolCount = 0;
    private AgentLauncher launcher;
    private boolean isSniffer;
    private String tooloptionConfigType;
	
    
    /**
     * @deprecated
     */
    public ToolRequester(AgentLauncher launcher, String toolClass, String tooloptionConfigType, String toolPrefix) {
        // This equals stuff is crap! Do not rely on it, use the longer constructor instead!
        this(launcher, toolClass, toolPrefix, tooloptionConfigType, toolClass.equals("jade.tools.sniffer.Sniffer"));
        System.out.println("Using CrappyConstructor(TM) of ToolRequester. DO NOT DO THAT AND FIX YOUR CODE!");
    }
    
    public ToolRequester(AgentLauncher launcher, String toolClass, String toolPrefix, String tooloptionConfigType, boolean isSniffer) {
        this.toolClass=toolClass;
        this.toolPrefix=toolPrefix;
        this.launcher=launcher;
        this.isSniffer = isSniffer;
        this.tooloptionConfigType = tooloptionConfigType;
        waitingAgents = new Hashtable<String,StringBuffer>();
        requestAgents = new Hashtable<String,StringBuffer>();
        agentTooloptionProperties = new Hashtable<String,HashMap<String,Vector<String>>>();
        launcher.getContentManager().registerOntology(ToolAgentParameterSetOntology.getInstance());
    }
    
    private void newToolAgent(String arg, StringBuffer result) throws jade.content.lang.Codec.CodecException, OntologyException {
        CreateAgent ca = new CreateAgent();
        ca.setAgentName(toolPrefix+toolCount);
        ca.setClassName(toolClass);
        ca.addArguments(arg);
        try {
            ca.setContainer(new ContainerID(launcher.getContainerController().getContainerName(), null));
        } catch (ControllerException e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setLanguage(launcher.codec.getName());
        msg.setOntology(JADEManagementOntology.NAME);
        msg.addReceiver(launcher.getAMS());
        msg.setSender(launcher.getAID());
        msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
        Action contentAction = new Action();
        contentAction.setAction(ca);
        contentAction.setActor(launcher.getAID());
        ContentManager manager = launcher.getContentManager();
        manager.setValidationMode(false);
        manager.fillContent(msg, (ContentElement)contentAction);
        launcher.addAMSBehaviour(new ToolCreateInitiator(msg, result, toolPrefix, launcher));
    }
    
    private class ToolCreateInitiator extends AchieveREInitiator {
        protected StringBuffer result;
        protected String aName;
        protected AgentLauncher launcher;
        
        public ToolCreateInitiator(ACLMessage request, StringBuffer result, String aName, AgentLauncher launcher) {
            super(launcher, request);
            this.result = result;
            this.aName = aName;
            this.launcher = launcher;
        }
        
        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("NOT-UNDERSTOOD received" + reply);
        }
        
        protected void handleRefuse(ACLMessage reply) {
            System.err.println("REFUSE received" + reply);
        }
        
        protected void handleAgree(ACLMessage reply) {
        }
        
        protected void handleFailure(ACLMessage reply) {
            // System.err.println("Failure: "+reply);
            String s = new String(reply.getContent());
            if (s.indexOf("nested") > 0) {
                s = s.substring(s.indexOf("nested"), s.length() - 1);
            } else if (s.indexOf("already-register") > 0) {
                s = aName + " is already registered";
            } else if (s.indexOf("internal-error") > 0) {
                s = s.substring(s.indexOf("internal-error")+"internal-error".length(), s.length() - 2);
            } else {
                System.err.println("\n"+s+"\n");
            }
            synchronized (result) {
                if (s != null) {
                    result.setLength(0);
                    result.append(s);
                }
                result.notifyAll();
            }
            launcher.removeBehaviour(this);
        }
        
        protected void handleInform(ACLMessage reply) {
            synchronized (result) {
                result.setLength(0);
                result.notifyAll();
            }
            launcher.removeBehaviour(this);
            waitForToolCreate = false;
        }
    }
    
    private class ToolRequestInitiator extends AchieveREInitiator {
        
        private String agents;
        private StringBuffer o;
        
        public ToolRequestInitiator(String agents, ACLMessage request, StringBuffer o) {
            super(launcher, request);
            this.agents = agents;
            this.o = o;
        }
        
        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("NOT-UNDERSTOOD received" + reply);
        }
        
        protected void handleRefuse(ACLMessage reply) {
            try {
                newToolAgent(agents, o);
            } catch (Exception e) {
                System.err.println(e);
            }
            toolCount++;
            launcher.removeBehaviour(this);
        }
        
        protected void handleFailure(ACLMessage reply) {
            try {
                newToolAgent(agents,o);
            } catch (Exception e) {
                System.err.println(e);
            }
            toolCount++;
            launcher.removeBehaviour(this);
        }
        
        protected void handleInform(ACLMessage reply) {
            synchronized (o) {
                o.notifyAll();
            }
            launcher.removeBehaviour(this);
            try {
                Done done = (Done)launcher.getContentManager().extractContent(reply);
                Action a = (Action)done.getAction();
                AgentAction aa = (AgentAction)a.getAction();
                if (aa instanceof SniffOn) {
                    SniffOn so = (SniffOn)aa;
                    for (Iterator i = so.getAllSniffedAgents(); i.hasNext();) {
                        AID aid = (AID)i.next();
                        System.out.println(aid.getLocalName());
                        
                        if ( agentTooloptionProperties.containsKey(aid.getLocalName()) ) {
                            sendToolAgentParameters(aid, o);
                        } else {
                            waitingAgents.remove(aid.getLocalName());
                        }
                    }
                }
            } catch (CodecException e) {
                System.err.println("CodecException: "+e);
            } catch (UngroundedException e) {
                System.err.println(e);
            } catch (OntologyException e) {
                System.err.println(e);
            }
        }
    }
    
    private class ToolParameterInitiator extends AchieveREInitiator {
        
        private StringBuffer o;
        
        public ToolParameterInitiator(ACLMessage request, StringBuffer o) {
            super(launcher, request);
            this.o = o;
        }
        
        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("NOT-UNDERSTOOD received" + reply);
            launcher.removeBehaviour(this);
        }
        
        protected void handleRefuse(ACLMessage reply) {
            System.err.println("ToolAgent "+toolClass+" refused parameter set.");
            launcher.removeBehaviour(this);
        }
        
        protected void handleFailure(ACLMessage reply) {
            System.err.println("ToolAgent "+toolClass+" failed on parameter set.");
            launcher.removeBehaviour(this);
        }
        
        protected void handleInform(ACLMessage reply) {
            synchronized (o) {
                o.notifyAll();
            }
            launcher.removeBehaviour(this);
            try {
                Done done = (Done)launcher.getContentManager().extractContent(reply);
                Action a = (Action)done.getAction();
                AgentAction aa = (AgentAction)a.getAction();
                if (aa instanceof ToolAgentParameterSet) {
                    ToolAgentParameterSet ps = (ToolAgentParameterSet)aa;
                    AID aid = ps.getAgentID();
                    agentTooloptionProperties.remove(aid.getLocalName());
                    waitingAgents.remove(aid.getLocalName());
                }
            } catch (CodecException e) {
                System.err.println("CodecException: "+e);
            } catch (UngroundedException e) {
                System.err.println(e);
            } catch (OntologyException e) {
                System.err.println(e);
            }
        }        
    }

    private void sendToolAgentParameters(AID aid, StringBuffer o) {
        HashMap<String,Vector<String>> toolOptionProperties = agentTooloptionProperties.get(aid.getLocalName());
        if (toolOptionProperties == null) {
            return;
        }

        AID toolAID = new AID(toolPrefix+(toolCount-1), AID.ISLOCALNAME);
        Action a = new Action();
        ToolAgentParameterSet ps = new ToolAgentParameterSet();
        ps.setAgentID(aid);
        for (String type: toolOptionProperties.keySet()) {
            for (String value: toolOptionProperties.get(type)) {
                ps.addParameter(new ToolAgentParameter(type, value));
            }
        }
        a.setAction(ps);
        a.setActor(toolAID);
        ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
        requestMsg.setOntology(ToolAgentParameterSetOntology.ONTOLOGY_NAME);
        requestMsg.setLanguage(launcher.codec.getName());
        requestMsg.addReceiver(toolAID);
        requestMsg.setSender(launcher.getAID());
        try {
            launcher.getContentManager().fillContent(requestMsg, a);
            launcher.addBehaviour(new ToolParameterInitiator(requestMsg, o));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void toolReady(){
        while (! (requestAgents.isEmpty() && waitingAgents.isEmpty())) {
            try {
                Iterator<String> iterator = requestAgents.keySet().iterator();
                while(iterator.hasNext()) {
                    String key = iterator.next();
                    StringBuffer synchobject = requestAgents.get(key);
                    internalRequestTool(key, synchobject);
                }
            } catch(ConcurrentModificationException cme) {
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
    }
    
    public void reset() {
        waitingAgents.clear();
        requestAgents.clear();
        agentTooloptionProperties.clear();
        waitForToolCreate = false;
    }
    
    private void sendRequest(String agent, StringBuffer synchobject) {
        AID toolAID = new AID(toolPrefix+(toolCount-1), AID.ISLOCALNAME);
        Action a = new Action();
        Concept so;
        if (isSniffer) {
            so = new SniffOn();
            ((SniffOn)so).setSniffer(toolAID);
            ((SniffOn)so).addSniffedAgents(new AID(agent, AID.ISLOCALNAME));
        } else {
            so = new DebugOn();
            ((DebugOn)so).setDebugger(toolAID);
            ((DebugOn)so).addDebuggedAgents(new AID(agent,AID.ISLOCALNAME));
        }
        a.setAction(so);
        a.setActor(toolAID);
        ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
        requestMsg.setOntology(JADEManagementOntology.NAME);
        requestMsg.setLanguage(launcher.codec.getName());
        requestMsg.addReceiver(toolAID);
        requestMsg.setSender(launcher.getAID());
        try {
            launcher.getContentManager().fillContent(requestMsg, a);
            launcher.addBehaviour(new ToolRequestInitiator(agent, requestMsg, synchobject));
            waitingAgents.put(agent, synchobject);
            requestAgents.remove(agent);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    private void internalRequestTool(String agent, StringBuffer synchobject) {
        if (waitingAgents.containsKey(agent) || waitForToolCreate) {
            return;
        }
        sendRequest(agent, synchobject);
    }
    
    public void requestTool(RunnableAgentInstance agentModel, StringBuffer synchobject ) {
        if (waitingAgents.containsKey(agentModel.getName())) {
            return;
        }
		System.err.println("ACHTUNG, ToolRequester muss an neue ToolOption-Klasse angepasst werden !!!");
        /* HashMap<String,Vector<String>> toolOptionProperties = agentModel.getToolOptionProperties(tooloptionConfigType);
        if ((toolOptionProperties != null) && (toolOptionProperties.size() > 0)) {
            agentTooloptionProperties.put(agentModel.getName(), toolOptionProperties);
        }
		*/
        if( !waitForToolCreate ) {
            if(toolCount == 0) {
                waitForToolCreate = true;
                try {
                    newToolAgent(agentModel.getName(),synchobject);
                    requestAgents.put(agentModel.getName(), synchobject);
                    toolCount += 1;
                } catch(Exception e) {
                    System.err.println(e);
                }
            } else {
                sendRequest(agentModel.getName(), synchobject);
            }
        } else {
            requestAgents.put(agentModel.getName(), synchobject);
        }
    }
}
