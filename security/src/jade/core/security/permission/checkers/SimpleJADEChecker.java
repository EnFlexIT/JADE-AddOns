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

package jade.core.security.permission.checkers;

import jade.core.Command;
import jade.core.Agent;
import jade.core.AID;
import jade.core.Location;
import jade.security.JADEPrincipal;
import jade.security.JADESecurityException;
import jade.security.JADEAccessController;

import jade.core.AgentContainer;
import jade.core.Profile;

import java.util.Hashtable;
import jade.security.Credentials;
import jade.core.management.AgentManagementSlice;
import jade.security.*;
import jade.core.Service;
import jade.core.ContainerID;
import jade.core.security.permission.PermissionFilter;
import jade.core.NodeDescriptor;
import jade.core.security.authentication.OwnershipCertificate;
import jade.core.security.SecurityHelper;
import jade.core.security.SecurityService;
import jade.core.*;
import jade.core.security.authentication.UserAuthenticator;
import jade.core.security.authentication.UserPassCredential;
import javax.security.auth.login.*;
import jade.security.impl.NameAuthority;
import jade.util.Logger;
import jade.core.security.util.SecurityData;
import jade.security.CertificateEncodingException;
import java.security.Permission;

/**
 *
 *
 *  A <code>SimpleCommandChecker </code>
 *  object maps Command types to couples: (Permission, action)
 *  where Permission is the name of the Permission class
 *  and action is the action to be checked against the policy.
 *
 *   @author Giosue Vitaglione - Telecom Italia LAB
 *   @version  $Date$ $Revision$
 * 
 *   @see jade.core.security.permission.PermissionService
 */
public class SimpleJADEChecker extends BaseJADEChecker {


  /**
   *
   * Implement the check(Command) so that:
   *  takes the cmd, gets the required info from the cmd parameters
   *  calls the method: check(subject, permission, target, credential)
   *
   */
  public void check(Command cmd) throws JADESecurityException {


    String name = cmd.getName();

    //--- prepare parameters for the check ---

    // principal who gets responsability for this command
    JADEPrincipal requester = cmd.getPrincipal();
    // 
    Credentials creds = cmd.getCredentials();

    // permission to be checked for passing this command
    Permission permission = null;
    // 
    JADEPrincipal target = null;
    // 
    Object[] params = cmd.getParams(); 



//----------
    if (name.equals(Service.NEW_NODE)) {

      // get prioncipal/creds from NodeDescriptor 
      // and not from the cmd
      PermissionFilter.log( direction, cmd );
      NodeDescriptor nd = (NodeDescriptor) params[0];
      String container_name = nd.getName();
      requester = nd.getOwnerPrincipal();
      creds = nd.getOwnerCredentials();

      boolean startingMain = nd.getNode().hasPlatformManager();

      if ( startingMain ) {
        // check if that user/principal is authorized, according to the policy
        myLogger.log(Logger.FINE, "  NEW_NODE ("+nd.getName()+")  p="+requester+" c="+creds);
        permission = new PlatformPermission( "", "create");
        // check if that user/principal is authorized, according to the policy
        checkAction( requester, permission, target, creds );
      } else {
        // if this comes from a remote container
        // authenticate here (on the main) the remote user
        authenticateRemoteUser(nd.getUsername(), nd.getPassword());        
      }

      // check if that user/principal is authorized, according to the policy
      myLogger.log(Logger.FINE, "  NEW_NODE ("+nd.getName()+")  p="+requester+" c="+creds);
      permission = new ContainerPermission( "", "create");
      checkAction( requester, permission, target, creds );
    }
    else
//----------
    if (name.equals(Service.DEAD_NODE)) {

// ToFix: Look at PlatformManagerImpl, line 356
//  something is changed before ssuing the vcmd

/*
      // get prioncipal/creds from NodeDescriptor 
      // and not from the cmd
      PermissionFilter.log( direction, cmd );
      NodeDescriptor nd = (NodeDescriptor) params[0];
      String container_name = nd.getName();
      requester = nd.getOwnerPrincipal();
      creds = nd.getOwnerCredentials();

      // if this comes from a remote container, 
      // and this is the main container
      if ((direction==Filter.OUTGOING) && (myContainer.getMain()!=null)) {
        authenticateRemoteUser(nd.getUsername(), nd.getPassword());
      }

      // check if that user/principal is authorized, according to the policy
      myLogger.log(Logger.FINE, "  NEW_NODE   p="+requester+" c="+creds);
      permission = new ContainerPermission( "", "create");

      checkAction( requester, permission, target, creds );
*/
    }
    else
//----------
    if (name.equals(AgentManagementSlice.REQUEST_CREATE)) {
      jade.core.security.permission.PermissionFilter.log( direction, cmd );

      String agentName="";
      if (direction==Filter.OUTGOING) { // cmd 'DOWN'
        ContainerID cid = (ContainerID) params[3];
        JADEPrincipal owner = (JADEPrincipal) params[4];
        Credentials initialCredentials = (Credentials) params[5];
      } else { // cmd 'UP'
        AID agentAID = (AID) params[0];
        agentName=agentAID.getName();
        String arguments[] =  (String[]) params[2];
        JADEPrincipal owner = (JADEPrincipal) params[3];
        Credentials initialCredentials = (Credentials) params[4];
        Boolean startIt = (Boolean) params[5];
      }

      String className = (String) params[1];
      JADEPrincipal owner;
      Credentials initialCredentials;

      if (direction==Filter.OUTGOING) { // cmd 'DOWN'
        owner = (JADEPrincipal) params[4];
        initialCredentials = (Credentials) params[5];
      } else { // cmd 'UP'
        owner = (JADEPrincipal) params[3];
        initialCredentials = (Credentials) params[4];
      }


      if ((owner==null) && (direction==Filter.OUTGOING))  {
        Object ret; // return value to be passed to the cmd
        ret = fixOwner(cmd);
        cmd.setReturnValue( ret );
        // veto this cmd
        JADESecurityException jse = new JADESecurityException("vetoed");
        throw jse;
      }

      if (creds==null) {
        // if the requester does not show any his creds, pass his ownership cert
        OwnershipCertificate oc = (OwnershipCertificate) service.getOwnershipCertificate(requester);
        if (oc!=null) owner=oc.getOwner();
        creds = (OwnershipCertificate) oc;
      }

      String ownerName="";
      if (owner!=null) ownerName=owner.getName();

      // permission to create a new agent whose class is className
      permission = new AgentPermission( AuthPermission.AGENT_CLASS+"="+className+","+
                                        AuthPermission.AGENT_NAME+"="+agentName+","+
                                        AuthPermission.AGENT_OWNER+"="+ownerName 
                                        , "create");

      checkAction( requester, permission, target, creds );
    }
    else
//----------
    if (name.equals(AgentManagementSlice.INFORM_CREATED)) {
      jade.core.security.permission.PermissionFilter.log( direction,  cmd );

      AID createdAgentID = (AID) params[0];

      Agent createdAgent;
      ContainerID createdContainer;
      if (params[1] instanceof ContainerID) {
        createdContainer = (ContainerID) params[1];
        // cmd incoming (up)

      } else {
        // cmd outgoing (down)
        // ok, agent was created, and is about to be registered 
        createdAgent = (Agent) params[1]; 
        String className = (String) createdAgent.getClass().getName();

        // princ/creds of the guy who requested the agent creation
        JADEPrincipal owner = (JADEPrincipal) params[2];
        Credentials initialCreds = (Credentials) params[3];

        // baptize the agent and get his SecurityHelper
        SecurityHelper sh = baptism( createdAgent, owner ); 

        JADEPrincipal agentPrincipal = sh.getPrincipal();
        Credentials agentCreds = sh.getCredentials();

        // insert into the vertical command with the agent principal/credentials
        cmd.setPrincipal( agentPrincipal );
        cmd.setCredentials( agentCreds );
        myLogger.log(Logger.FINER, "\n    agent=("+agentPrincipal+")"+ 
                                   "\n    owner=("+agentCreds.getOwner()+")");

        String ownerName = (owner!=null) ? owner.getName() : "";

        // permission to register the agent
        permission = new AMSPermission( AuthPermission.AGENT_CLASS+"="+className+","+
                                        AuthPermission.AGENT_OWNER+"="+ownerName 
                                        , "register");

        // check if the agent (and not the requestor) has got the permission
        if (agentPrincipal==null) {
          String msg = " Internal error,  null agentPrincipal after agent creation.";
          myLogger.log(Logger.WARNING, msg );
          throw new JADESecurityException( msg );
        } else {
          checkAction(agentPrincipal, permission, target, agentCreds);
        }
      }


    } else 
//----------
    if (name.equals(AgentManagementSlice.REQUEST_KILL)) {
      jade.core.security.permission.PermissionFilter.log( direction, cmd );

      AID killedAgentID = (AID) params[0];
      Agent agent = null; // agent to be killed
      String className = "";
      JADEPrincipal requester_owner = null;
      JADEPrincipal victim = null;
      String victim_owner_name = "";
      String victim_name = victim_name = killedAgentID.getName();


      // if the requester does not show any his creds, pass his ownership cert
      OwnershipCertificate oc=null;
      if (creds==null) {
        oc = (OwnershipCertificate) service.getOwnershipCertificate(requester);
        creds = (OwnershipCertificate) oc;
      }
      // get the owner of the requester
      if (creds!=null) requester_owner = oc.getOwner();

    
      // get the owner of the agent to kill
      OwnershipCertificate oc_victim = (OwnershipCertificate) service.getOwnershipCertificate( victim_name );
      if (oc_victim!=null) victim_owner_name = oc_victim.getOwner().getName();

      agent = myContainer.acquireLocalAgent(killedAgentID);
      if (agent!=null) { className = (String) agent.getClass().getName();  }
      myContainer.releaseLocalAgent(killedAgentID);

      if (agent!=null) {
        // agent is on this container
        permission = new AgentPermission( AuthPermission.AGENT_CLASS+"="+className+","+
                                          AuthPermission.AGENT_OWNER+"="+victim_owner_name+","+
                                          AuthPermission.AGENT_NAME+ "="+victim_name 
                                          , "kill");
      } else {
        // agent is not on this container
        permission = new AgentPermission( AuthPermission.AGENT_OWNER+"="+victim_owner_name+","+ 
                                          AuthPermission.AGENT_NAME+ "="+victim_name 
                                          , "kill");
      }
      checkAction( requester, permission, target, creds );


    } else 
//----------
    if (name.equals(AgentManagementSlice.INFORM_KILLED)) {
      jade.core.security.permission.PermissionFilter.log( direction, cmd );

      AID killedAgentID = (AID) params[0];
      Agent agent = myContainer.acquireLocalAgent(killedAgentID);
      myContainer.releaseLocalAgent(killedAgentID);
      String className ="";
      if (agent!=null) {
        className = (String) agent.getClass().getName();
      }
      // permission to deregister an agent 
      permission = new AMSPermission( AuthPermission.AGENT_CLASS+"="+className 
                                      , "deregister");
      // TOFIX:  //checkAction( requester, permission, target, creds );
      }


  }
//----------
//----------
  
  
  
  private SecurityHelper amsAutoBaptism (Agent createdAgent, JADEPrincipal owner) 
          throws JADESecurityException {
    // The AMS agent baptize itself, with this special procedure

    SecurityHelper ams_sh=null;
    try { 
      // retrieve ams principal
      ams_sh = (SecurityHelper) createdAgent.getHelper(jade.core.security.SecurityService.NAME);
      ams_sh.init(createdAgent);
    } catch (ServiceException ex) {
      throw new JADESecurityException (" Service problems in creating Agent's OwnershipCertificate. ");
    }

    // create the OwnershipCertificate
    OwnershipCertificate amsOwnCert = new OwnershipCertificate( 
        ams_sh.getPrincipal(), // ams principal
        owner );           // ams owner principal

    // self-sign the cert
    // calculates the signature
    SecurityData sd = new SecurityData();
    byte[] encCert = null;
    try {
      encCert = amsOwnCert.getEncoded();
    } catch(CertificateEncodingException cee) {
      throw new JADESecurityException("CertificateEncodingException when trying to sign (by the AMS) a certificate. ");
    }
    
    sd = ams_sh.getAuthority().sign(ams_sh.getSignatureAlgorithm(), encCert);
    
    // put the signature into the NameCertificate
    amsOwnCert.setSignature(sd);

    // keep the AMS OwnershipCertificate
    service.setAMSOwnershipCertificate( amsOwnCert );
    ams_sh.addCredentials( amsOwnCert ); // add the Ownership Certificate

    return ams_sh;
  }


  private SecurityHelper baptism (Agent createdAgent, JADEPrincipal owner) 
          throws JADESecurityException {

    if (createdAgent instanceof jade.domain.ams) {
      // the ams is special, it self-baptizes
      return amsAutoBaptism( createdAgent, owner);
    }

    // all normal agents are baptized by the platform's AMS

    // agent principal (got from the sh) and credentials (OwnershipCertificate)
      SecurityHelper sh=null;
    try { 
      // retrieve agent's principal
      sh = (SecurityHelper) createdAgent.getHelper(jade.core.security.SecurityService.NAME);
      sh.init(createdAgent);
    } catch (ServiceException ex) {
      throw new JADESecurityException (" Service problems in creating Agent's OwnershipCertificate. ");
    }

    // create the OwnershipCertificate
    OwnershipCertificate ownershipCert = 
        service.getOwnershipCertificate(
        sh.getPrincipal(), // agent principal
        owner );           // owner principal

    sh.addCredentials( ownershipCert ); // add the Ownership Certificate

    return sh;
  } // end baptism


  private void authenticateRemoteUser( String username, byte[] password ) 
          throws JADESecurityException {

    // authenticate user by using the Main container authentication module
    UserAuthenticator ua = new UserAuthenticator(
        (service.getProfile().getParameter(SecurityService.AUTHENTICATION_LOGINMODULE_KEY, null)),
        (service.getProfile().getParameter(SecurityService.AUTHENTICATION_LOGINCALLBACK_KEY, null)),
        (service.getProfile().getParameter(SecurityService.AUTHENTICATION_OWNER_KEY, null)), false);
    
    try {
      // authenticate the user who is requesting the join 
      // of a remote container
      ua.login(new UserPassCredential(username, password));
    } catch (Exception ex2) {
      throw new JADESecurityException("Username Authentication FAILED.");
    }
  }

  private Object fixOwner(Command cmd) {
    Object returnValue=null;
    // requester did not tell the owner => 
    // => assume the owner the same as the requester's owner
    //  requester's agentPrincipal -> owner
    JADEPrincipal requester = cmd.getPrincipal();
    Credentials creds = cmd.getCredentials();

    JADEPrincipal reqOwner=null;

    OwnershipCertificate oc = (OwnershipCertificate) service.getOwnershipCertificate(requester);
    if (oc!=null) reqOwner=oc.getOwner();

    if (reqOwner==null) { 
      // avoid loop
      returnValue = new JADESecurityException("Agent creation request had 'null' owner, and owner was not known.");
    } else {

      if (creds==null) {
        // if the requester does not show any his creds, pass his ownership cert (since we have it here...)
        creds = (OwnershipCertificate) oc;
      }
      // create a new cmd, that "replaces" the current cmd
      GenericCommand cmd2 = new GenericCommand(cmd.getName(),cmd.getService(),null);
      cmd2.setPrincipal(requester);
      cmd2.setCredentials(creds);
      Object[] params = cmd.getParams(); 
      cmd2.addParam( params[0] );
      cmd2.addParam( params[1] );
      cmd2.addParam( params[2] );
      cmd2.addParam( params[3] );
      cmd2.addParam( (JADEPrincipal) reqOwner );
      cmd2.addParam( (Credentials) null ); // initial creds
    
      // feed the new cmd into the service chain
      try {
        Service amserv = myContainer.getServiceFinder().findService(
            jade.core.management.AgentManagementSlice.NAME);
        
        returnValue = amserv.submit(cmd2);
      } catch (ServiceException ex) {
        returnValue = new JADESecurityException("Agent creation request had 'null' owner, service exception while trying to retrieve it.");
      } catch (IMTPException ex1) {
        returnValue = new JADESecurityException("Agent creation request had 'null' owner, IMTP exception while trying to retrieve it.");
      }

    } // end if-else reqOwner==null

    return returnValue;
  }// end fixOwner

} // end checker class
