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

package jade.core.security;

//#MIDP_EXCLUDE_FILE

import java.io.*;

//import javax.security.auth.Subject;
//import javax.security.auth.kerberos.KerberosPrincipal;
//import javax.security.auth.login.LoginContext;
//import javax.security.auth.login.LoginException;

import jade.core.*;
import jade.core.security.util.*;
import jade.core.security.authentication.UserAuthenticator;
import jade.core.security.authentication.UserPassCredential;
import jade.core.messaging.GenericMessage;
import jade.core.messaging.MessagingSlice;
import jade.domain.FIPAAgentManagement.Envelope;
import jade.domain.FIPAAgentManagement.SecurityObject;
import jade.domain.FIPAAgentManagement.Property;
import jade.security.Credentials;
import jade.security.JADEPrincipal;
import jade.security.impl.JADEPrincipalImpl;
import jade.security.SecurityFactory;
import jade.security.JADEAuthority;
import jade.util.Logger;
import jade.util.leap.Iterator;
import jade.lang.acl.ACLMessage;

import java.util.Hashtable;
import jade.security.JADESecurityException;

/**
   ServiceService does not have filters, it provides 
   the authentication mechanism 
   and the SecurityHelper for performing common security operations.

   @author Giosue Vitaglione - Telecom Italia LAB
   @author Ivan Trenkansky - Whitestein Technologies 
   @author Dominic Greenwood - Whitestein Technologies
   @author Jerome Picault - Motorola Labs
   @version  $Date$ $Revision$
   
   @see jade.core.security.SecurityHelper
   @see jade.core.security.permission.PermissionHelper
   @see jade.core.security.signature.SignatureHelper
   @see jade.core.security.encryption.EncryptionHelper
 */
public class SecurityService extends BaseService {

  public static final String AUTHENTICATION_LOGINMODULE_KEY = "jade_security_authentication_loginmodule";
  public static final String AUTHENTICATION_LOGINCALLBACK_KEY = "jade_security_authentication_logincallback";
  public static final String AUTHENTICATION_OWNER_KEY = "owner";
  public static final String AUTHENTICATE_USER = "AUTH_USR";
  public static final String CODECS = "jade_core_security_SecurityService_CODEC";
  public static final String BASIC_CODEC_CLASS = "jade.core.security.basic.BasicSOCodec";

  private Logger myLogger = Logger.getMyLogger( this.getClass().getName() );

	// The concrete agent container, providing access to LADT, etc.
	private AgentContainer myContainer;
	private Profile myProfile;
  
  // Table of SecurityObject codecs
  private Hashtable codecs;

  // Constant for default encoding format
  public static String SO_FORMAT;

	public static final String NAME = "jade.core.security.Security";
	public String getName() {
		return SecurityService.NAME;
	}

  // authority for this container
  private JADEAuthority authority = null;

  public void init(AgentContainer ac, Profile profile) throws ProfileException {

    super.init(ac, profile);
    myContainer = ac;
    myProfile = profile;

    // set properly various profile parameters and system properties
    setPropertiesValue();

    // ask for user/pass, authenticate, 
    // and pass principal/credentials to myContainer
    authenticatUser();

  } // end init()


  private void setPropertiesValue() {
    // initialize the SecurityFactory
    myProfile.setParameter( SecurityFactory.SECURITY_FACTORY_CLASS_KEY,
                          "jade.security.impl.JADESecurityFactory" );
    SecurityFactory.getSecurityFactory( myProfile );

    // if present, a jade configuration entry 
    // overwrites java system parameter value 
    copyProp( "java.security.auth.login.config" );
    copyProp( "java.security.krb5.realm" );
    copyProp( "java.security.krb5.kdc" );
    copyProp( "java.security.krb5.conf" );

    // simple login crdentials file
    copyProp( "jade_security_authentication_loginsimplecredfile");
    
    // container's policy file
    copyProp( "java.security.policy");

  } // end setPropertiesFromProfile
  private void copyProp(String key){
    String val =  myProfile.getParameter( key, null );
    if (( val!=null) && (val.length()>0)) System.setProperty(  key, val );
  } // end copyProp


  public void boot(Profile p) {
    // load
    loadContainerAuthority();
    try {
      // register codecs (FIXME: currently only the default one)
      codecs = new Hashtable();
      SOCodec soc = (SOCodec)Class.forName(BASIC_CODEC_CLASS).newInstance();
      codecs.put(soc.getName(),soc);
      SO_FORMAT = soc.getName();
    }
    catch(Exception e) {
      myLogger.log( Logger.SEVERE,e.toString());
    }
  }
  
	public Filter getCommandFilter(boolean direction) {
		return null;
	}
  
	public Class getHorizontalInterface() {
    return null;
	}

	// no horiz commands => no slice
	public Service.Slice getLocalSlice() {
		return null;
	}

	private static final String[] OWNED_COMMANDS = {AUTHENTICATE_USER};

	public String[] getOwnedCommands() {
		//return null;
		return OWNED_COMMANDS;
	}

	private final SecuritySink sink = new SecuritySink();

	public Sink getCommandSink(boolean side) {
		return sink;
	}

	public ServiceHelper getHelper(Agent a) {
		SecurityHelper se = new SecurityHelper(myProfile);
		return se;
	}

  /**
   * Returns the SecurityObject of the given type contained in the envelope,
   * @param e a non-null envelope
   * @param type the type of the searched SecurityObject (e.g. SIGN, ENCRYPT)
   */
  public static SecurityObject getSecurityObject(Envelope e, int type) {
    // Check the SecurityObject(s) in the envelope
    // TODO: Currently at the X-security user defined property
    Property p;
    SecurityObject[] so;
    for (Iterator it = e.getAllProperties(); it.hasNext();) {
      p = (Property) it.next();
      if (p.getName().equals(SecurityObject.NAME)) {
        so = (SecurityObject[])p.getValue();
        for (int i=so.length-1;i>=0;i--) {
          if (so[i].getType() == type) {
            return so[i];
          }
        }
      }
    }
    return null;
  }
  
   /**
   * Decodes the encoded security information contained in a SecurityObject
   * @return a SecurityData object containing the security information
   * @throw an exception is encoding format is not supported
   * TODO: Should throw a JADESecurityException
   */
  public void decode(SecurityObject so) throws Exception {
    SOCodec soc;  
    if ((so.getEncoded() != null) && 
        (so.getEncoded() instanceof byte[])) {
      if ((soc = (SOCodec)codecs.get(so.getFormat())) == null) {
        throw new Exception("Unknown Securing Object Encoding"); // Should be a JADESecurityException
      } 
      so.setEncoded(soc.decode((byte[])so.getEncoded()));
    }
  }
  
  /** 
   * Encode the security information contained in the SecurityData object within the 
   * given SecurityObject.
   * If no encoding format is specified in the SecurityObject, then
   * the default one will be used.
   * @throws an Exception if the encoding format is not supported
   * TODO: Should throw a JADESecurityException
   */
  public void encode(SecurityObject so) throws Exception {
    if ((so.getEncoded() != null) && 
        (so.getEncoded() instanceof SecurityData)) {
      if (so.getFormat() == null) {
        so.setFormat(SO_FORMAT);
      }
      SOCodec soc = (SOCodec)codecs.get(so.getFormat());
      so.setEncoded(soc.encode((SecurityData)so.getEncoded()));
    }
  }

	/**
	 * Implementation of the abstract jade.core.security.SecurityHelper.
   private class SecurityHelperImpl extends SecurityHelper {

   // (non-Javadoc)
   // @see jade.core.security.SecurityHelper#authenticateUser()
   //
   public LoginContext authenticateUser() throws ServiceException {
   myLogger.log( Logger.FINEST, 
   "SecurityHelperImpl -> authenticateUser() method called.");
   GenericCommand cmd =
   new GenericCommand(
   SecurityHelper.AUTHENTICATE_USER,
   getName(),
   null);
   Object result = submit(cmd);
   if (result == null) {
   myLogger.log( Logger.WARNING, 
   "SecurityHelperImpl -> User authentication has failed due to null result.\n"
   + "Exiting JVM...");
   System.exit(0);
   } else if ((result instanceof Throwable)) {
   myLogger.log( Logger.WARNING, 
   "SecurityHelperImpl -> User authentication has failed due to the following exception:\n"
   + ((Throwable) result).getMessage()
   + "\nExiting JVM...");
   //((Throwable) result).printStackTrace();
   System.exit(0);
   } else if (!(result instanceof LoginContext)) {
   myLogger.log( Logger.SEVERE, 
   "SecurityHelperImpl -> User authentication has failed due to unexpected type of result:\n"
   + result.getClass()
   + "\nExiting JVM...");
   System.exit(0);
   }
   // Successfull Authentication.
   myLogger.log( Logger.INFO, 
   "\n\nSecurityHelperImpl -> User authentication has succeeded."
   + "\nThe login context's subject is:\n"
   + ((Subject) ((LoginContext) result).getSubject()).toString()
   + "\n ");
   return (LoginContext) result;
   };
   }
  */




  // called from init(), loads the container's keypair
  private void loadContainerAuthority(){
    // open the related SecurityStore
    // get the key pair, and create the JADEPrincipal
    authority = SecurityFactory.getSecurityFactory().newJADEAuthority();
    String name = myContainer.getID().getName();
    try {
      Credentials mycreds = new UserPassCredential(name, new byte[]{}); // TOFIX: use right credentials
      authority.init(name, myProfile, mycreds); 
    } catch (Exception e) { e.printStackTrace(); } // TOFIX: handle this properly 
  }

  // returns the JADEPrincipal assigned for this container
  public JADEPrincipal getContainerJADEPrincipal(){
    return getContainerJADEAuthority().getJADEPrincipal();
  }
  // returns the JADEPrincipal assigned for this container
  public JADEAuthority getContainerJADEAuthority(){
    return authority;
  }

 
  /* 
     Authenticate against the login module (that includes asking for user/pass)
     get or create the user JADEPrincipal and its initial Credentials and 
     pass them to the starting container (via the method passUserPrincipalToTheContainer()
   */
  private void authenticatUser() {

          String authLogMod = myProfile.getParameter(AUTHENTICATION_LOGINMODULE_KEY, null);
          if ( (authLogMod!=null) && (authLogMod.equals("SingleUser")) ) {
            authenticateSingleUserMode();
            return;
          }

          // Set LOGINMODULE and LOGINCALLBACK to default if not set
          if (myProfile.getParameter(AUTHENTICATION_LOGINMODULE_KEY, null) == null) 
            myProfile.setParameter(AUTHENTICATION_LOGINMODULE_KEY, "Simple"); 

          if (myProfile.getParameter(AUTHENTICATION_LOGINCALLBACK_KEY, null) == null) 
            myProfile.setParameter(AUTHENTICATION_LOGINCALLBACK_KEY, "Dialog"); 

          if (myContainer.getMain() != null) {
            // This is a main container, so process the user authentication
            UserAuthenticator ua = new UserAuthenticator(
                                    (myProfile.getParameter(AUTHENTICATION_LOGINMODULE_KEY, null)),
                                    (myProfile.getParameter(AUTHENTICATION_LOGINCALLBACK_KEY, null)),
                                    (myProfile.getParameter(AUTHENTICATION_OWNER_KEY, null)),false);

            try {
                ua.login();

                passUserPrincipalToTheContainer(ua.getPrincipal(), ua.getCredentials());

              } catch (Exception e) { // Should be a JADESecurityException
                  goAway(e);
              }

          } else {

            // This is not a main container so gather username and password
            // and pass them to a NodeDescriptor
            UserAuthenticator ua = new UserAuthenticator(
                                    (myProfile.getParameter(AUTHENTICATION_LOGINMODULE_KEY, null)),
                                    (myProfile.getParameter(AUTHENTICATION_LOGINCALLBACK_KEY, null)),
                                    (myProfile.getParameter(AUTHENTICATION_OWNER_KEY, null)),true);

            try {
              ua.login();

              //  Send user and pass to main container using NodeDescriptor
              myContainer.getNodeDescriptor().setUsername(ua.getUsername());
              myContainer.getNodeDescriptor().setPassword(ua.getPassword());

              passUserPrincipalToTheContainer(new JADEPrincipalImpl(ua.getUsername()), null);
            } catch (Exception e) {
                goAway(e);
            }
          }
  }

  /*  this is used instead of authenticatUser() 
   *  when the 'SingleUser' mode is selected as login module
   */
  private void authenticateSingleUserMode(){
    try {
      myLogger.log(Logger.INFO, "'SingleUser' mode.  The platform has a single user named: 'jade'.");
      passUserPrincipalToTheContainer(new JADEPrincipalImpl("jade"), null);
    }
    catch (JADESecurityException ex) {
    }
  }


  // set into the NodeDescriptor of the local Container the owner's Principal and InitialCredentials
  // this is important, agents creation at startup is done by using this principal
  private void passUserPrincipalToTheContainer(
           JADEPrincipal userPrincipal, Credentials userInitialCredentials) 
       throws JADESecurityException {
    if (userPrincipal != null) {
      myContainer.getNodeDescriptor().setOwnerPrincipal(userPrincipal);
    } else {
      myLogger.log(Logger.WARNING,
          "SecurityService -> Unable to set Container Principal from User Authenticator");
      throw new JADESecurityException("Authentication failed. Unable to set Container Principal from User Authenticator.");
    }
    if (userInitialCredentials != null) {
      myContainer.getNodeDescriptor().setOwnerCredentials(userInitialCredentials);
    }
    /*
      } else {
      myLogger.log (Logger.WARNING,
      "SecurityService -> Unable to set Container Crendentials from User Authenticator");
      }
    */
  } // end passUserPrincipalToTheContainer

  // when authentication fails, prints out message and exit the JVM !
  private void goAway(Exception e) {
     myLogger.log(Logger.SEVERE, 
                  "\n ---USER NOT AUTHENTICATED---   Go away! \n");
     myLogger.log(Logger.FINER, e.getMessage());
     //e.printStackTrace();
     System.exit( -1);
  }

  /**
   */
  public void reconstructACLMessage(GenericMessage gmsg){
    if (gmsg.getACLMessage()==null){
      Envelope env = gmsg.getEnvelope();
      ACLMessage acl = new ACLMessage(ACLMessage.INFORM);
      acl.setSender(env.getFrom());
      // try to retrieve conv-id from the security objects
      SecurityObject so=SecurityService.getSecurityObject(env,SecurityObject.SIGN);
      if (so==null) so=SecurityService.getSecurityObject(env,SecurityObject.ENCRYPT);
      if (so!=null) acl.setConversationId(so.getConversationId());
      gmsg.setACLMessage(acl);
    }
  }

} // end class SecurityService
