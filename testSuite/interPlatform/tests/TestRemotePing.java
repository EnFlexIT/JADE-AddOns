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

package test.interPlatform.tests;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import test.common.*;

/**
   @author Giovanni Caire - TILAB
 */
public class TestRemotePing extends Test {
	public  static final String TEST_NAME = "Remote Ping";
	private static final String RESPONDER_NAME = "responder";
	private final String CONV_ID = "conv_ID"+hashCode();
	
	private AID resp = null;
	
  public String getName() {
  	return TEST_NAME;
  }
  
  public String getDescription() {
  	StringBuffer sb = new StringBuffer("Tests sending and receiving a message to/from an agent on a remote platform using the default IIOP MTP");
  	return sb.toString();
  }
  
  public Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException { 
  	try {
  		final DataStore store = ds;
  		final String key = resultKey;
  		
	  	Object[] args = getGroupArguments();
  		AID remoteAMS = (AID) args[0];
  		resp = TestUtility.createTarget(a, RESPONDER_NAME, remoteAMS);
  		TestUtility.addBehaviour(a, resp, "test.common.behaviours.NotUnderstoodResponder");
  		
  		Behaviour b1 = new SimpleBehaviour() {
  			private boolean finished = false;
  			public void onStart() {
  				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
  				msg.addReceiver(resp);
  				msg.setConversationId(CONV_ID);
  				myAgent.send(msg);
  			}
  			
  			public void action() {
  				ACLMessage msg = myAgent.receive(MessageTemplate.MatchConversationId(CONV_ID)); 
					if (msg != null) { 
						AID sender = msg.getSender();
						if (sender.equals(resp)) {
							store.put(key, new Integer(Test.TEST_PASSED));
						}
						else {
							store.put(key, new Integer(Test.TEST_FAILED));
						}
						finished = true;
  				}
  				else {
  					block();
  				}
  			}
  			
  			public boolean done() {
  				return finished;
  			}
  		};
  	
  		// If we don't receive any answer in 10 sec --> TEST_FAILED
  		Behaviour b2 = new WakerBehaviour(a, 10000) {
				protected void handleElapsedTimeout() {
					store.put(key, new Integer(Test.TEST_FAILED));
				}
  		};
  		
  		ParallelBehaviour b = new ParallelBehaviour(a, ParallelBehaviour.WHEN_ANY); 
  		b.addSubBehaviour(b1);
  		b.addSubBehaviour(b2);
  		
  		return b;
  	}
  	catch (TestException te) {
  		throw te;
  	}
  	catch (Exception e) {
  		throw new TestException("Error loading test", e);
  	}
  }
					
  public void clean(Agent a) {
  	try {
  		TestUtility.killTarget(a, resp);
  	}
  	catch (Exception e) {
  		e.printStackTrace();
  	}
  }
  	
}

