// -------------------------------------------------------------------------------

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.*;

/**
 * Directory Servlet
 *
 * Servlet linking the multi-agent system to the Web interface.
 *
 * @author <A HREF="mailto:Fabien.Gandon@cs.cmu.edu">Fabien GANDON</A>, <A href="http://www-2.cs.cmu.edu/~sadeh/Mobile%20Commerce%20Lab.htm">Mobile Lab.</A>, <A href="http://www.cmu.edu/">CMU</A>, 11th June 2003
 * @version 1.0
 * @since JDK1.4
 */


public class DirectoryServlet extends HttpServlet
{ 
  private static AgentController theProxyAgent = null;
  
  /**
    Synchronized static method called at the initialization of the servlet. It ensures that a
    non-main container is created once and only once, with a proxy agent associated to this servlet.
  */
  synchronized private static void SyncInit()
  {
    // Have the proxy agent and its container already been initialized?
    if (theProxyAgent==null)
    {
      // Get a hold on JADE runtime
      Runtime l_JADERunTime = Runtime.instance();
      
      // Create a default profile
      Profile l_JADEProfile = new ProfileImpl();    
      
      // Create a new non-main container, connecting to the default main container (i.e. on this host, port 1099)
      AgentContainer l_NewContainer = l_JADERunTime.createAgentContainer(l_JADEProfile);
      try
      {
        // Create the object used to synchronize the starting of the Servlet and the Proxy agent
        Object l_Arg[] = new Object[1];
        Synchronizer l_Sync = new Synchronizer();
        l_Arg[0] = l_Sync;
        
        // Create the Proxy Agent and pass it the synchronizing object as argument
        theProxyAgent = l_NewContainer.createNewAgent("ProxyAgent", ProxyAgent.class.getName(),l_Arg);

        // Start the Proxy Agent
        theProxyAgent.start();
        
        // Wait for synchronization signal
        l_Sync.waitOn();
      }
      catch(Exception l_Exception) { l_Exception.printStackTrace(); }
    }
  } // End of SyncInit

  /**
    Just call the synchronized initialization.
  */
  public void init() throws ServletException 
  {
    SyncInit();
  }
     
  /**
    Respond to an HTTP Request by passing an Interaction object to the proxy agent using
    the object2agent channel and waiting for the signal that the Response of the Interaction was updated.
   * @param p_Request incoming HTTP request.
   * @param p_Response outgoing HTTP Response.
   */ 
  public void doGet (HttpServletRequest p_Request, HttpServletResponse p_Response) throws ServletException, IOException
  {
   // Create an interaction object wrapping the HTTP request and response 
   Interaction l_Interac = new Interaction(p_Request,p_Response);
   
   // Pass the interaction to the Proxy Agent and wait for the signal that the interaction was updated
   try
   {
     theProxyAgent.putO2AObject(l_Interac, AgentController.ASYNC);
     l_Interac.waitChangedResponse();
   }
   catch(Exception l_Exception) { l_Exception.printStackTrace(); }
  } // End of doGet
  
} // End of Servlet
