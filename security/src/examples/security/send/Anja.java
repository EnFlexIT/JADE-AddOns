/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB S.p.A. 

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

package examples.security.send;


import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;


/**
 *
 *
 *
@author Giosuè Vitaglione - TILAB S.p.A.
@version  $Date$ $Revision$  
 */
public class Anja extends Agent {



  protected void setup() {

	System.out.println("\n This is Anja. Now I'm going to send messages to Barbara.");
	System.out.println("\n Press enter to start");
	try { 
		System.in.read();
	} catch (java.io.IOException e) {}
	
	addBehaviour(new SimpleBehaviour(this) {

		int count=10;

		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		    msg.setContent(" count is now "+count);
		    msg.addReceiver(  new AID("Barbara", AID.ISLOCALNAME)  );
		    System.out.println("Ajna:  Sending message "+count+" to Barbara");
		    send(msg);
		} // end action
		
		public boolean done() {
			System.out.println("\n Anja has done.");
			return ( (--count)==0 );
		}
		
	}); // end Behaviour

  } // end setup



}//end class Anja
