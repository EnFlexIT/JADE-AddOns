package examples.jsp;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

/**
 * This agent is to be used in a JSP page. It just sends
 * messages to a buffer agent.
 */
public class Snooper extends Agent {
    private ACLMessage msg;

    public Snooper() {
	// Create the message to send to the client
        msg = new ACLMessage(ACLMessage.INFORM);
	System.out.println("Snooper created");
    }

    public void setup() {
      // Accept objects through the object-to-agent communication
      // channel, with a maximum size of 10 queued objects
      setEnabledO2ACommunication(true, 10);


      // Add a suitable cyclic behaviour...
      addBehaviour(new jade.core.behaviours.CyclicBehaviour() {
	      
	      public void action() {
		  System.out.println(" Trying... ");
		  Object obj = getO2AObject();
		  if(obj != null) {
		      System.out.println(" Snooping "+obj);
		      snoop(obj.toString());
		  } else { 
		      block();
		  }
	      }
      });
    }

    public void takeDown() {
      // Disables the object-to-agent communication channel, thus
      // waking up all waiting threads
      setEnabledO2ACommunication(false, 0);
    }


    /**
     * The method that will be invoked in the JSP page.
     * @param str the message to send to the client
     */
    public void snoop(String str) {
	// JADE 2.3
	msg.clearAllReceiver();
	msg.addReceiver(new AID("buffer",AID.ISLOCALNAME));
        // JADE 2.0: 
        // getHap() cannot be moved in the constructor because it would not 
	// work! each time the previous entry must be removed.
	// msg.clearAllReceiver();
	// msg.addReceiver(new AID("buffer@"+getHap()));
	// JADE 1.4:
	// msg.removeAllDests();
	//msg.addDest("buffer");
	msg.setContent(str);
	send(msg);
    }
}




