/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2003 TILAB S.p.A. 

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

package examples.misc.FSMMessageExchange;

import jade.core.Agent;
import jade.core.behaviours.*; 
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

/**
 * <br> SampleReceiverTask waits until a message arrives, prints the
 * received message, and then returns the integer representing the
 * performative of the received message. This integer can then be
 * used as a driver of the transition in the FSM.
 * @author Fabio Bellifemine - TILAB
 * @version $Date$ $Revision$
 **/
public class SampleReceiverTask extends Behaviour {
	
	private static Logger logger = Logger.getMyLogger(SampleReceiverTask.class.getName());

    ACLMessage msg, msgReceived;
    boolean done=false;
    
    public boolean done() { return done; }
    public int onEnd() { return msgReceived.getPerformative(); }

    public void action() {
	msg = myAgent.receive();
	if (msg == null)
	    block();
	else {
	    if(logger.isLoggable(Logger.INFO))
	    	logger.log(Logger.INFO,myAgent.getLocalName()+" executed SampleReceiverTask and returning value "+msg.getPerformative());
	    msgReceived = msg;
	    done=true;
	}
    }
}
