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

package jade.core.security.encryption;

import jade.core.Agent;
import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.Filter;
import jade.core.VerticalCommand;
import jade.core.messaging.MessagingSlice;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingService;
import jade.core.security.*;
import jade.core.security.basic.*;
import jade.core.security.util.*;
import jade.lang.acl.ACLMessage;
//import jade.security.SDSIName;
import jade.security.JADEPrincipal;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SecurityObject;
import jade.domain.FIPAAgentManagement.InternalError;
import jade.util.leap.Iterator;
import jade.util.Logger;

import java.util.Hashtable;


/**
 * This class implements the filter responsible for signing
 * ACL message on behalf of an agent.
 *
 * @author Jerome Picault - Motorola Labs
 * @author Nicolas Lhuillier - Motorola Labs
 *
 * @see jade.core.security.SecurityServiceExecutor
 * @see jade.core.CommandProcessor
 */
public class EncryptionFilter {
  
  // Logger to print out information and warnings
  private Logger myLogger = Logger.getMyLogger(this.getClass().getName());
  
  private AgentContainer myContainer;
  private MessagingService ms;
  private SecurityService ss;
  Filter in;
  Filter out;

  // init the AuthorizationFilter
  public void init(AgentContainer myContainer ) {
    // the container
    this.myContainer = myContainer;
    try {
      ss = (SecurityService)myContainer.getServiceFinder().findService(SecuritySlice.NAME);
      ms = (MessagingService)myContainer.getServiceFinder().findService(MessagingSlice.NAME);
    }
    catch(Exception e) {
      myLogger.log(Logger.SEVERE,"Unable to find Security or Messaging services");
    }
    in = new In();
    out = new Out();
  }
  

  /**
   * Outgoing filter for encryption service
   */
  class Out extends Filter {

    public Out(){
      // sets the relative position of the filter in the filter chain.
      setPreferredPosition(30);
    }

    /**
     * Encrypt the message contained in the command and update envelope info
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
          if ((env != null)&&((so=SecurityService.getSecurityObject(env,SecurityObject.ENCRYPT)) != null)) {
            sender = (AID)params[0];
            Agent agt = myContainer.acquireLocalAgent(sender);
            SecurityHelper sh = (SecurityHelper)agt.getHelper(SecurityService.NAME);
            myContainer.releaseLocalAgent(sender);
            
            // encrypts the message payload and update the security object
            JADEPrincipal principal = sh.getPrincipal(((AID)params[2]).getName());
            byte[] enc = sh.getAuthority().encrypt(so,msg.getPayload(),principal);
            // Obfuscate some fields in the ACLMessage
            ACLMessage acl = msg.getACLMessage();
            if (acl != null) { 
              acl.setContent(null);
              so.setConversationId(acl.getConversationId());
            }
            ss.encode(so);
            // update the Generic Message in the command
            msg.update(acl, env, enc);
            myLogger.log(Logger.FINEST,"ACLMessage encrypted");
          }
        }
        catch(Exception e) {
          // This can be a ClassCastException, IndexOutOfBounds, SecurityException, NullPointer, etc.
          myLogger.log(Logger.WARNING,e.toString());
          //e.printStackTrace();
          try {
            // Reports the exception to the sender
            ss.reconstructACLMessage(msg);
            ms.notifyFailureToSender(msg, sender, new InternalError(e.getMessage()));
          }
          catch(Exception ne) {
            cmd.setReturnValue(ne);
          }
          return false;
        }
      }
      return true;
    }

  } // End of EncryptionFilter.In class


  /**
   * Incoming filter for encryption service
   */
  class In extends Filter {

    public In(){
      // sets the relative position of the filter in the filter chain.
      setPreferredPosition(30);
    }

    /**
     * Verifies the signature of the received message
     */
    public boolean accept(VerticalCommand cmd) {
      String name = cmd.getName();

      // Only process if it is a SEND_MESSAGE CMD
      if (name.equals(MessagingSlice.SEND_MESSAGE)) {
        GenericMessage msg = null;
        Object[] params = cmd.getParams();
        
        myLogger.log(Logger.FINEST,"Processing Incoming Command: "+cmd.getName());
        
        try {
          // params[0] = sender (AID)
          // params[1] = generic message (acl + envelope + payload)
          // params[2] = receiver (AID)
          msg = (GenericMessage)params[1];
          SecurityObject so;
          Envelope env = msg.getEnvelope();

          if ((env != null)&&((so=SecurityService.getSecurityObject(env,SecurityObject.ENCRYPT)) != null)) {
            AID receiver = (AID)params[2];
            Agent agt = myContainer.acquireLocalAgent(receiver);
            SecurityHelper sh = (SecurityHelper)agt.getHelper(SecurityService.NAME);
            myContainer.releaseLocalAgent(receiver);
            
            // decrypt payload
            ss.decode(so);
            byte[] dec = sh.getAuthority().decrypt(so,msg.getPayload());
            // update the command
            msg.update(msg.getACLMessage(), env, dec);
            
            myLogger.log(Logger.FINEST,"ACL payload decrypted");
          }
        }
        catch(Exception e) {
          // This can be a ClassCastException, IndexOutOfBounds, SecurityException, NullPointer, etc.
          myLogger.log(Logger.WARNING,e.toString());
          //e.printStackTrace();
          try {
            // Reports the exception to the sender
            ss.reconstructACLMessage(msg);
            ms.notifyFailureToSender(msg, (AID)params[0], new InternalError(e.getMessage()));
          }
          catch(Exception ne) {
            cmd.setReturnValue(ne);
          }
          return false;
        }
      }
      return true;
    }

  } // End of EncryptionFilter.In class

}




