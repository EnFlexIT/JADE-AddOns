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
package it.pisa.jade.util.behaviours;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
/**
 * 
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
@SuppressWarnings("serial")
public class MyReceiver extends SimpleBehaviour
{
	
	private MessageTemplate template;
	private long    timeOut, 
	                wakeupTime;
  private boolean finished;
  
  private ACLMessage msg;
  
  public ACLMessage getMessage() { return msg; }
  
  
  public MyReceiver(Agent a, int millis, MessageTemplate mt) {
    super(a);
    timeOut = millis;
    template = mt;
  }
  
	public void onStart() {
		wakeupTime = (timeOut<0 ? Long.MAX_VALUE
		              :System.currentTimeMillis() + timeOut);
	}
		
	public boolean done () {
		return finished;
	}
	
	public void action() 
	{
		if(template == null)
      	msg = myAgent.receive();
		else
			msg = myAgent.receive(template);

		if( msg != null) {
			finished = true;
			handle( msg );
			return;
		}
      long dt = wakeupTime - System.currentTimeMillis();
      if ( dt > 0 ) 
      	block(dt);
      else {
			finished = true;
			handle( msg );
      }
	}

	public void handle( ACLMessage m) { /* can be redefined in sub_class */ }
	
	public void reset() {
		msg = null;
		finished = false;
		super.reset();
  	}
  	
	public void reset(int dt) {
		timeOut= dt;
		reset();
  	}

}
