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

package benchmark.roundTripTime;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Sender agent for Round Trip benchmark. 
 * This works into "Parallel Mode", that is:
 * a sender sends in sequence all the messages without waiting for the replies.
 *
 * For other tests, is used the "serial mode", where each sender sends a message 
 * and wait for a reply before sending the next one. 
 *
 * For both "serial" and "parallel" mode the receiver agent is: benchmark.RoundTripReceiver
 *
 * For intra-container, just run:<br>
 *  <code>java jade.Boot -conf ParalleTestSingleContainer.conf</code><br>
 * or, for inter-container:
 *  <code>java jade.Boot -conf ParalleTest2Containers_RX.conf</code><br>
 *  <code>java jade.Boot -conf ParalleTest2Containers_TX.conf</code><br>
 *
 * @author Fabio Bellifemine - TiLab
 * @author Elisabetta Cortese - TiLab
 */

public class ParallelRoundTripSender extends Agent {

    /**
     * The start time.
     */
    public long startTime = 0;
    /**
     * The end time.
     */
    public long stopTime = 0;
    /**
     * Number of message to send.
     */
    static long iterations = 0;


    static int THR_UP = 95; // percentage of agents that must have been created before starting to count roundtrip
    static int THR_LOW = 0;
    private static int agents = 0;
    private static int terminatedAgents = 0;
    private static boolean resultPrinted = false;

    synchronized static void increaseNumAgents(int couples) {
	if (agents == 0) {
	    THR_UP = Math.round(THR_UP * couples / 100.0f);
	    System.out.println("The roundtrippers will measure time when at least "+THR_UP+" agents have been created and "+THR_LOW+" agents are still working.");
	}
	agents++;
    }

    synchronized static void decreaseNumAgents() {
        terminatedAgents++;
    }

    static Vector times = new Vector();

    synchronized static void updateResults(long startTime, long stopTime, long iterations) {
	long tempoTotale = stopTime - startTime;
	double avg = (double)tempoTotale/(double)iterations;
	times.add(new Double(avg));
    }

    public double printResults() {
        double totalTime = 0;
        double tot1 = 0;
        double tot2 = 0;
        double currentValue;
        double rtt;
        double standardDev;
        int n = times.size();

        for (int i=0; i < n; i++) {
            currentValue = ( (Double)times.elementAt(i) ).doubleValue();
            totalTime += currentValue;
            tot1 = tot1 + currentValue * currentValue;
            tot2 = tot2 + currentValue;
        }
        rtt = totalTime / (double)n;
        standardDev = Math.sqrt( ( n * tot1 - tot2 * tot2 ) / ( n * (n-1) ) );
        System.out.println( "Average RTT=" + rtt + " msec Dev.Std=" + standardDev );
        return rtt;
	//System.exit(0);
    }

    synchronized static boolean startMeasuring() {
	return ( agents >= THR_UP);
    }

    synchronized static boolean stopTripping(int couples) {
	return ((couples - terminatedAgents) <= THR_LOW);
    }

    AID receiver;
    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    String ior = "";
    int couples = 0;
		AID controller = null;

   /**
    * This agent get parameter from command line.
    */
    public void setup() {
	Object[] args = getArguments();
	String receiverName = (String)(args[0]);
        iterations = (new Long((String)(args[1]))).longValue();
        ior = (String)args[2];
	couples = Integer.parseInt(((String)(args[3])));
	receiver = new AID(receiverName, ((ior.length() > 9) ? AID.ISGUID : AID.ISLOCALNAME));
        if(ior.length() > 9){ //is GUID
            receiver.addAddresses(ior);
	}
	msg.setContent("CONTENT");
        msg.addReceiver(receiver);

  if (args.length == 5) {
  	controller = new AID((String) args[4], AID.ISLOCALNAME);
  }
        
	increaseNumAgents(couples);

        addBehaviour( new CyclicBehaviour(this) {
		int counter = 0;
		int measuring = 0;
		public void action() {
			//System.out.println(measuring+" "+THR_LOW+" "+THR_UP);
		    switch (measuring) {
		      case 0:
			  if ( startMeasuring() ) {
			      measuring = 1;
			      startTime = System.currentTimeMillis();
			      counter = 0;
			  }
			  break;
		      case 1: // in this state I send iterations number of messages
		      	send(msg);
			counter++;
			if (counter == iterations) 
				measuring = 2;
			break;
		      case 2: // in this state I receive iterations number of messages
		      	msg=blockingReceive();
			counter--;
			if (counter == 0)  {
				// take the result	
			      stopTime = System.currentTimeMillis();
			      decreaseNumAgents();
			      updateResults(startTime, stopTime, iterations);
			      if ( stopTripping(couples) ){
				    	if (!resultPrinted) {
				    		resultPrinted = true;
					    	double rtt = printResults();
					    	if (controller != null) {
					    		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					    		msg.addReceiver(controller);
				  	  		msg.setContent(String.valueOf(rtt));
				    			myAgent.send(msg);
				    		}
				    		else {
				    			System.exit(0);
				    		}
				    	}
			      }
			  }
			  break;
		      default:
		    }
		}
            });
    }

}
