// -------------------------------------------------------------------------------

import java.io.*;
import javax.servlet.http.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Proxy Agent
 *
 * Agent linking the multi-agent system to the servlet providing HTML interfaces.
 *
 * @author <A HREF="mailto:Fabien.Gandon@cs.cmu.edu">Fabien GANDON</A>, <A href="http://www-2.cs.cmu.edu/~sadeh/Mobile%20Commerce%20Lab.htm">Mobile Lab.</A>, <A href="http://www.cmu.edu/">CMU</A>, 11th June 2003
 * @version 1.0
 * @since JDK1.4
 */
public class ProxyAgent extends Agent
{
  /**
   * Starting method of the agent: enable object2agent and set a cyclic behavior to check the queue.
   */
  public void setup() 
  {
    // Accept objects through the object-to-agent communication channel, with a maximum size of 100 queued objects
    this.setEnabledO2ACommunication(true, 100);
    
    // Add a cyclic behaviour checking the queue of objects
    this.addBehaviour(new CyclicBehaviour() 
    {
      public void action()
      {
        Object l_Obj = getO2AObject();
        if(l_Obj != null) 
        {
          // We have an object and we know for sure it is an Interaction
          // here the only interaction handle is the request for the directory
          // thus one behavior is created to fulfil this request 
          myAgent.addBehaviour(new GetDFList(myAgent,(Interaction)l_Obj));
        }
        else block();
      }
    });
    
    // Register the Proxy Agent with the DF so that there is at least one registered agent
    DFAgentDescription l_AgentDescription = new DFAgentDescription();
    l_AgentDescription.setName(this.getAID());
    ServiceDescription l_ServiceDescription = new ServiceDescription();
    l_ServiceDescription.setName("HTTP-proxy-agent");
    l_ServiceDescription.setType("proxy");
    l_ServiceDescription.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
    l_ServiceDescription.addOntologies("HTTP-ontology");
    l_ServiceDescription.setOwnership("Carnegie Mellon University");
    l_AgentDescription.addServices(l_ServiceDescription);
    try { DFService.register(this,l_AgentDescription); }
    catch(FIPAException l_ex) {l_ex.printStackTrace(); }
     
    // Notify the Servlet that the Proxy Agent is ready
    Object[] l_Args = getArguments();
    Synchronizer l_Sync = (Synchronizer) (l_Args[0]);
    l_Sync.Started();
  }
   
   
  /**
   * Disables the object-to-agent communication channel, thus waking up all waiting threads
   */ 
  public void takeDown()
  {
    this.setEnabledO2ACommunication(false, 0);
  }

  /**
   * Simple inner class providing a behavior that deals with a request
   */
  class GetDFList extends OneShotBehaviour
  {
    Interaction myInteraction = null;
    
    /**
     * Constructor of the behavior
     * @param p_Agent agent handling the request
     * @param p_Interaction interaction to be handled
     */
    public GetDFList(Agent p_Agent, Interaction p_Interaction)
    {
      super(p_Agent);
      myInteraction = p_Interaction;
    }

    /**
     * Method handling the retrieval of the list of agent and its conversion into a Web page.
     */    
    public void action()
    {
      try
      {
        //retrieve list of all registered agents
        DFAgentDescription[] l_Result = DFService.search(myAgent, new DFAgentDescription());
        
        // write the list of AID in the HTTP response
        PrintWriter l_Out = myInteraction.getResponse().getWriter();
        l_Out.println("<HTML><BODY>");
        if (l_Result!=null && l_Result.length>0)
        {
          l_Out.println("List of registered agents:<BR />");
          for(int l_count=0; l_count< l_Result.length ; l_count++)
            l_Out.println("<LI>"+l_Result[l_count].getName().toString()+"</LI>");
        }
        else l_Out.println("No agent found.");
        l_Out.println("</BODY></HTML>");
        l_Out.close();
      }
      catch(Exception l_ex) {l_ex.printStackTrace(); }
      
      // Notify the Servlet that the response is ready
      myInteraction.setResponseChanged();
    }  
  }// End of GetDFList inner class
  
} // End of ProxyAgent class