// -------------------------------------------------------------------------------

import javax.servlet.http.*;

/**
 * Interaction description
 *
 * Object used between the Servlet and the Proxy Agent to exchange an HTTP request and its response
 *
 * @author <A HREF="mailto:Fabien.Gandon@cs.cmu.edu">Fabien GANDON</A>, <A href="http://www-2.cs.cmu.edu/~sadeh/Mobile%20Commerce%20Lab.htm">Mobile Lab.</A>, <A href="http://www.cmu.edu/">CMU</A>, 11th June 2003
 * @version 1.0
 * @since JDK1.4
 */

public class Interaction
{
  /** incoming HTTP request */
  private HttpServletRequest theRequest = null;
  /** outgoing HTTP response */
  private HttpServletResponse theResponse = null;
  /** flag used to detect an update in the Response */
  private boolean theResponseChangeFlag = false;

  /**
   * Constructor of an interaction, requiring handlers of the HTTP request and its response.
   * @param p_Request incoming HTTP request.
   * @param p_Response outgoing HTTP Response.
   */ 
  public Interaction (HttpServletRequest p_Request, HttpServletResponse p_Response)
  {
    theRequest = p_Request;
    theResponse = p_Response;
  }
  
  /**
   * accessor to the HTTP response
   * @return the handler of the HTTP response. 
   */
  synchronized HttpServletResponse getResponse()
  {
    return (theResponse);
  }

  /**
   * wait for the update of the response 
   */
  synchronized void waitChangedResponse()
  {
    boolean l_Flag = theResponseChangeFlag;
    try { while(l_Flag==theResponseChangeFlag) wait();}
    catch (InterruptedException l_InterruptedException) { l_InterruptedException.printStackTrace(); }
  }

  /**
   * accessor to the HTTP request
   * @return the handler of the HTTP request. 
   */
  synchronized HttpServletRequest getRequest()
  {
    return (theRequest);
  }

  /**
   * notifies an update of the response.
   */
  synchronized void setResponseChanged()
  {
    theResponseChangeFlag = !theResponseChangeFlag;
    this.notifyAll();
  }

} // End of Interaction class