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

package examples.security.delegation;

import java.io.*;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import jade.security.*;
import jade.security.impl.*;


/**
 *
 *
 *
@author Giosuè Vitaglione - TILAB S.p.A.
@version  $Date$ $Revision$  
 */


public class Anja extends Agent {

  protected void setup() {

	System.out.println("\n This is Anja. ");
	System.out.println("   First I'll try to move to Container-1, but this will fail :-(");
	System.out.println("   So, I'll wait for a DelegationCertificate from Barbara.");
	System.out.println("   Then I'll try again, and it will work !");
	System.out.println("\n\n Step 1: \n\n Press enter, I'll try to go to 'Container-1'");
	try { 
		System.in.read();
	} catch (java.io.IOException e) {}

	ContainerID loc = new ContainerID();
	loc.setName("Container-1");
	
	addBehaviour(new GoThere( loc ));
	addBehaviour(new GetDelegationBehaviour());
	addBehaviour(new GoThere( loc ));
	addBehaviour(new SayHi());
	
  } // end setup



public class GoThere extends SimpleBehaviour {
	Location loc=null;
	public GoThere (Location loc) {
		this.loc=loc;
	}
	public void action() {
		System.out.println("\nAjna: I'm trying to go to Container-1: ");
		doMove( loc );
	} // end action
	public boolean done() {
		return ( true );
	}
}


public class GetDelegationBehaviour extends SimpleBehaviour {

	public void action() {

		// Wait for a delegation certificate from Barbara
		System.out.println("\nWaiting for the Delegation Certificate...");
		ACLMessage delegMsg = blockingReceive();
		System.out.println("Delegation Certificate received.");

		try{
		  // get the DelegationCertificate
		  System.out.println("  extracting Certificate from ACLMessage...");
		  DelegationCertificate deleg1 = (DelegationCertificate) delegMsg.getContentObject();

		  // add the receive delegation certificate to Ajna's Certificate older
		  getCertificateFolder().addDelegationCertificate( deleg1 );
		  System.out.println("  adding Delegation Certificate to my Certificate Folder");

		} catch(UnreadableException e) {
			System.out.println("\n Unreadable Certificate. " );
		}

	} // end action
	
	public boolean done() {
		return ( true );
	}


}; // end Behaviour


public class SayHi extends SimpleBehaviour {

	public void action() {
		System.out.println("\n Anja: I'm here !!" );
	}
	public boolean done() {
		return ( true );
	}

} // end sayHi Behaviour



}//end class Anja
