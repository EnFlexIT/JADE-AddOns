package it.pisa.jade.agents;

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



import it.pisa.jade.util.Logger;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


/**
This agent implements a simple Ping Agent for the AgentCities project.
First of all the agent registers itself with the DF of the platform and 
then waits for ACLMessages.
If  a QUERY_REF message arrives that contains the string "ping" within the content 
then it replies with an INFORM message whose content will be the string "alive". 
If it receives a NOT_UNDERSTOOD message no reply is sent. 
For any other message received it replies with a NOT_UNDERSTOOD message.
The exchanged message are written in a log file whose name is the local name of the agent.

@author Tiziana Trucco - CSELT S.p.A.
@version  $Date: 2002-08-02 10:10:01 +0200 (Fri, 02 Aug 2002) $ $Revision: 3320 $  
*/


@SuppressWarnings("serial")
public class PingAgent extends Agent {

    Logger logFile;
    
    @SuppressWarnings("serial") 
    class WaitPingAndReplyBehaviour extends SimpleBehaviour {
	
  	private boolean finished = false;
	
	public WaitPingAndReplyBehaviour(Agent a) {
	    super(a);
	}
	
	public void action() {
	    
	    ACLMessage  msg = blockingReceive();
	    
	    if(msg != null){
		if(msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD){
			//received a NOT-UNDERSTOOD message
			logFile.log(msg.getSender().getName(),Logger.RECEIVED,ACLMessage.getPerformative(msg.getPerformative()));
			logFile.log("No reply message sent");
		    }
		else{
		    
		    ACLMessage reply = msg.createReply();
		    
		    //if((msg.getPerformative()== ACLMessage.QUERY_REF)||(msg.getPerformative()== ACLMessage.QUERY_IF))
		    if(msg.getPerformative()== ACLMessage.QUERY_REF){
		
			String content = msg.getContent();
			if ((content != null) && (content.indexOf("ping") != -1)){
			    //received a QUERY_REF with correct content.
			    logFile.log(msg.getSender().getName(),Logger.RECEIVED,ACLMessage.getPerformative(msg.getPerformative()));
			    reply.setPerformative(ACLMessage.INFORM);
			    //reply.setContent("(pong)");
			    reply.setContent("alive");
			    logFile.log(((AID)reply.getAllReceiver().next()).getName(), Logger.TRANSMITTED,ACLMessage.getPerformative(reply.getPerformative()));
			}
			else{
			    //received a QUERY_REF with uncorrect content.
			    logFile.log(msg.getSender().getName(),Logger.RECEIVED,ACLMessage.getPerformative(msg.getPerformative()),msg.toString());
			    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			    reply.setContent("( UnexpectedContent (expected ping))");
			    //write the full message.
			    logFile.log(((AID)reply.getAllReceiver().next()).getName(),Logger.TRANSMITTED,ACLMessage.getPerformative(reply.getPerformative()),reply.toString());
			}
			
		    }
		    else {
			//received a wrong performative.
			logFile.log(msg.getSender().getName(),Logger.RECEIVED,ACLMessage.getPerformative(msg.getPerformative()),msg.toString());
			reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			reply.setContent("( (Unexpected-act "+ACLMessage.getPerformative(msg.getPerformative())+") ( expected (query-ref :content ping)))");   
			logFile.log(((AID)(reply.getAllReceiver().next())).getName(),Logger.TRANSMITTED,ACLMessage.getPerformative(reply.getPerformative()),reply.toString());
		    }
		    
		  
		    send(reply);
		}
	    }else{
		//System.out.println("No message received");
	    }
	}
	
	public boolean done() {
	    return finished;
	}
    } //End class WaitPingAndReplyBehaviour
    
    
    protected void setup() {
  	
	/** Registration with the DF */
	DFAgentDescription dfd = new DFAgentDescription();
	ServiceDescription sd = new ServiceDescription();   
	sd.setType("AgentcitiesPingAgent"); 
	sd.setName(getName());
	sd.setOwnership("TILAB");
	//sd.addOntologies("PingAgent");
	dfd.setName(getAID());
	dfd.addServices(sd);
	try {
	    DFService.register(this,dfd);
	} catch (FIPAException e) {
	    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
	    doDelete();
	}
	

	logFile = new Logger(getLocalName());
	logFile.log("Agent: " + getName() + " born");
	WaitPingAndReplyBehaviour PingBehaviour = new  WaitPingAndReplyBehaviour(this);
	addBehaviour(PingBehaviour);
	
    }
    
   
    
}//end class PingAgent
