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
public class Barbara extends Agent {


  protected void setup() {
	System.out.println("\n This is Barbara.");
	System.out.println("\n Let Anja start on the main container.\n");

	addBehaviour(new SimpleBehaviour(this) {

		int count=10;

		public void action() {
			ACLMessage msg = blockingReceive();
		    System.out.println( "\n		Barbara:  Received message from "+msg.getSender().getName() );
		    System.out.println( "				  "+msg.getContent() );
		} // end action
		
		public boolean done() {
			return false;
		}
		
	}); // end Behaviour

  } // end setup
  
  
}//end class Anja
