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

import java.util.Date;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import jade.security.*;
import jade.security.impl.*;


/**
 *
 *
 *
@author Giosuè Vitaglione - TILAB S.p.A.
@version  $Date$ $Revision$  
 */


public class Barbara extends Agent {


  protected void setup() {

	addBehaviour(new SimpleBehaviour(this) {

	public void action() {

		System.out.println("\n This is Barbara. ");
		System.out.println("   Now I'm going to create a Delegation Certificate for Anja, ");
		System.out.println("   and send it to her. She will be able to come to Container-1.");
	    System.out.println("\n\n Step 2: \n\n Press enter, I delegate Anja, and Anja will come here.");

		try { 
			System.in.read();
		} catch (java.io.IOException e) { e.printStackTrace(); }


		// create the delegation certificate and sign it
	    System.out.println( "\n		Barbara:  Creating Delegation Certificate for Anja" );
	    DelegationCertificate cert = getAuthority().createDelegationCertificate();

		cert.setNotBefore( new Date(System.currentTimeMillis()-1000) );
		cert.setNotAfter( new Date(System.currentTimeMillis() + 15*60*1000) ); // 15 minutes from now
		cert.setSubject( getAuthority().createAgentPrincipal( new AID("Anja", AID.ISLOCALNAME)  , "alice" ) );

		System.out.println( "\n		Barbara:     adding permissions..." );
		
		cert.addPermission(new jade.security.impl.AgentPermission("alice", "move"));
		cert.addPermission(new jade.security.impl.ContainerPermission("alice", "move-from"));
		cert.addPermission(new jade.security.impl.ContainerPermission("bob", "move-to"));

		try {
		System.out.println( "\n		Barbara:     signing..." );
		getAuthority().sign( cert, getCertificateFolder() );
 		} catch (jade.security.AuthException e) { 
 			System.out.println( "AuthException during Delegation Certificate signing: "+ e.getMessage() );
 		}

		System.out.println( "\n		Barbara:  Preparing message..." );
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
	    msg.addReceiver(  new AID("Anja", AID.ISLOCALNAME)  );
	    
	    try {
	    	msg.setByteSequenceContent( cert.getEncoded() );
	 	} catch ( jade.security.CertificateEncodingException e) {
	 		System.out.println("CertificateEncodingException."); 
	 	}
	 	
		System.out.println( "\n		Barbara:  Sending Delegation Certificate to Anja" );
		send(msg);


	} // end action
		
		public boolean done() {
			return ( true );
		}


	}); // end Behaviour

  } // end agent setup
  
  



}//end class Anja
