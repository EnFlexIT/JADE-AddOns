/**
 * Proxy Synchronizer
 *
 * Object synchronizing the starting of the Servlet and the Proxy Agent
 *
 * @author <A HREF="mailto:Fabien.Gandon@cs.cmu.edu">Fabien GANDON</A>, <A href="http://www-2.cs.cmu.edu/~sadeh/Mobile%20Commerce%20Lab.htm">Mobile Lab.</A>, <A href="http://www.cmu.edu/">CMU</A>, 11th June 2003
 * @version 1.0
 * @since JDK1.4
 */
public class Synchronizer
{
  private boolean Started = false;

  /**
    Wait for synchronization signal
  */
  synchronized void waitOn()
  {
    try { while(!Started) wait();}
    catch (InterruptedException l_InterruptedException) { l_InterruptedException.printStackTrace(); }
  }

  /**
    Send synchronization signal
  */
  synchronized void Started()
  {
    Started = true;
    notifyAll();
  }

} // End of Synchronizer class