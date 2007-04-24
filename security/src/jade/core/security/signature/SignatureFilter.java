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

package jade.core.security.signature;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.Filter;
import jade.core.VerticalCommand;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingService;
import jade.core.messaging.MessagingSlice;
import jade.core.security.SecurityHelper;
import jade.core.security.SecurityService;
import jade.core.security.SecuritySlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.InternalError;
import jade.domain.FIPAAgentManagement.SecurityObject;
import jade.lang.acl.ACLMessage;
import jade.security.JADESecurityException;
import jade.security.util.SecurityData;
import jade.util.Logger;

/**
   This class implements the filter responsible for signing
   ACL message on behalf of an agent.

   @author Nicolas Lhuillier - Motorola Labs
   @author Jerome Picault - Motorola Labs

   @see jade.core.security.SecurityHelper
   @see jade.core.CommandProcessor
*/
public class SignatureFilter {
  
  // Logger to print out information and warnings
  private Logger myLogger = Logger.getMyLogger(this.getClass().getName());
  
  private AgentContainer myContainer;
  private SecurityService ss;
  private MessagingService ms;
  Filter in;
  Filter out;
  
  // init the SignatureFilter
  public void init(AgentContainer myContainer) {
    // the container
    this.myContainer = myContainer;
    in = new In();
    out = new Out();
  }
  
  // Method for lazy retrieval of the SecurityService
  private SecurityService getSecurityService() {
	  if (ss == null) {
		try {
	      ss = (SecurityService) myContainer.getServiceFinder().findService(SecuritySlice.NAME);
	    }
	    catch(Exception e) {
	      myLogger.log(Logger.SEVERE, "Unable to find Security services.", e);
	    }
	  }
	  return ss;
  }
  
  // Method for lazy retrieval of the MessagingService
  private MessagingService getMessagingService() {
	  if (ms == null) {
		try {
		   ms = (MessagingService)myContainer.getServiceFinder().findService(MessagingSlice.NAME);
	    }
	    catch(Exception e) {
	      myLogger.log(Logger.SEVERE, "Unable to find Messaging services.", e);
	    }
	  }
	  return ms;
  }
  
  /**
   * Outgoing filter for signature service
   */
  class Out extends Filter {

    public Out() {
      setPreferredPosition(50);
    }
    
    /**
     * Sign the message contains in the command and update envelope info
     */
    public boolean accept(VerticalCommand cmd) {
      String name = cmd.getName();
      
      // Only process if it is a SEND_MESSAGE CMD
      if (name.equals(MessagingSlice.SEND_MESSAGE)) {
        GenericMessage msg = null;
        AID sender = null;
        myLogger.log(Logger.FINEST,"Processing Outgoing Command: "+cmd.getName());
        try {
          // params[0] = sender (AID)
          // params[1] = generic message (acl + envelope + payload)
          // params[2] = receiver (AID)
          Object[] params = cmd.getParams();
          msg = (GenericMessage)params[1];
          SecurityObject so;
          Envelope env = msg.getEnvelope();
    	  System.out.println("Signature filter: Processing message from "+msg.getACLMessage().getSender().getName()+": Envelope is "+env);
          if (env != null) {
            if ((so=SecurityService.getSecurityObject(env,SecurityObject.SIGN)) != null) {
            sender = (AID)params[0];
            SecurityData sd = (SecurityData)so.getEncoded();
            Agent agt = myContainer.acquireLocalAgent(sender);
            SecurityHelper sh = (SecurityHelper)agt.getHelper(SecurityService.NAME);
            myContainer.releaseLocalAgent(sender);
            sd = sh.getAuthority().sign(sd.algorithm,msg.getPayload());
            so.setEncoded(sd);
            ACLMessage acl = msg.getACLMessage();
            if (acl!=null) so.setConversationId(acl.getConversationId());
            getSecurityService().encode(so);   
            myLogger.log(Logger.FINEST,"ACLMessage has been signed");
            }
          }
          else {
            // If envelope is null, ACLMessage can't be null
            env = msg.getACLMessage().getEnvelope();
            if ((env != null)&&((so=SecurityService.getSecurityObject(env,SecurityObject.SIGN)) != null)) {
              // This is a local message, the trick is that it is not signed
              // However we need to put the principal into the envelope
              sender = (AID)params[0];
              Agent agt = myContainer.acquireLocalAgent(sender);
              SecurityHelper sh = (SecurityHelper)agt.getHelper(SecurityService.NAME);
              ((SecurityData)so.getEncoded()).key = sh.getPrincipal();
              myContainer.releaseLocalAgent(sender); 
            }
          }
        }
        catch(Exception e) {
          // This can be a ClassCastException, IndexOutOfBounds, SecurityException, NullPointer, etc.
          myLogger.log(Logger.WARNING,e.toString());
          e.printStackTrace();
          try {
            // Reports the exception to the sender
            getSecurityService().reconstructACLMessage(msg);
            getMessagingService().notifyFailureToSender(msg, sender, new InternalError(e.getMessage()));
          }
          catch(Exception ne) {
            cmd.setReturnValue(ne);
          }
          return false;
        }
      }
      return true;
    }
    
  } // End of SignatureFilter.Out class


  /**
   * Incoming filter for signature service
   */
  class In extends Filter {

    public In() {
      setPreferredPosition(10);
    }

    /**
     * Verifies the signature of the received message
     */
    public boolean accept(VerticalCommand cmd) {
      String name = cmd.getName();

      // Only process if it is a SEND_MESSAGE CMD
      if (name.equals(MessagingSlice.SEND_MESSAGE)) {
        GenericMessage msg = null;
        Object[] params = null;
        
        myLogger.log(Logger.FINEST,"Processing Incoming Command: "+cmd.getName());
        
        try {
          // params[0] = sender (AID)
          // params[1] = generic message (acl + envelope + payload)
          // params[2] = receiver (AID)
          params = cmd.getParams();
          msg = (GenericMessage)params[1];
          SecurityObject so;
          Envelope env = msg.getEnvelope();
          if ((env != null)&&((so=SecurityService.getSecurityObject(env,SecurityObject.SIGN)) != null)) {
            AID receiver = (AID)params[2];
            getSecurityService().decode(so);
            SecurityData sd = (SecurityData)so.getEncoded();
            Agent agt = myContainer.acquireLocalAgent(receiver);
            SecurityHelper sh = (SecurityHelper)agt.getHelper(SecurityService.NAME);
            myContainer.releaseLocalAgent(receiver);
            if (! sh.getAuthority().verifySignature(sd,msg.getPayload())) {
              throw new JADESecurityException("Invalid signature");
            }
            myLogger.log(Logger.FINEST,"ACLMessage signature is valid");
          }
        }
        catch(Exception e) {
          // This can be a ClassCastException, IndexOutOfBounds, SecurityException, NullPointer, etc.
          e.printStackTrace();
          myLogger.log(Logger.WARNING,e.toString());
          try {
            // Reports the exception to the sender
        	getSecurityService().reconstructACLMessage(msg);
            getMessagingService().notifyFailureToSender(msg, (AID)params[0], new InternalError(e.getMessage()));
          }
          catch(Exception ne) {
            cmd.setReturnValue(ne);
          }
          return false;
        }
      }
      return true;
    }

  } // End of SignatureFilter.In class

}




