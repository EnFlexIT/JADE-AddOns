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

/**
 * The Authors
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra </a>
 * @author <a href="mailto:Jordi.Cucurull@uab.es">Jordi Cucurull Juan</a>
 *  
 */
package samples;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.AID;
import jade.core.PlatformID;
import java.util.Properties;
import java.io.FileInputStream;
import java.util.Vector;

public class MobileAgent extends Agent {

    public void setup(){
		// Get the file with the list of destinations and the message to print as agent arguments
		Object[] args = getArguments();
		if (args!=null) {
			if (args.length==1) {
				String fproperties = args[0].toString(); 

				//Add agent behaviour.
				addBehaviour(new MyB(this,fproperties));
			} 
			else {
				help();
			}
		} 
		else {
			help();
		}
    }

    private class MyB extends SimpleBehaviour {
		public MyB(Agent a, String fproperties) {
			super(a);

			_state = 0;
			_done = false;
			_hopcounter = 0;
			_itinerari = new Vector();
			try {
				Properties p = new Properties();
				p.load(new FileInputStream(fproperties));
				parseItinerary(p);
			} 
			catch(Exception e){
				System.out.println("Error reading agent file properties.");
			}
		}
	
		public void action(){
			switch(_state){
			case 0:
				System.out.println("\n\nAgent "+myAgent.getName()+" - Hello. I'm here...I'm about to perform hop "+(_hopcounter+1)+" of "+_itinerari.size());
				String destination = (String) _itinerari.get(_hopcounter);
				String[] ss = destination.split(" ");
				if (ss.length != 2) {
					System.out.println("WARNING: Agent "+myAgent.getName()+" - hop "+_hopcounter+" \""+destination+"\" does not have the right form <platform-name> <platform-address>.");
				}
				else {
					System.out.println("Agent "+myAgent.getName()+" - Moving to " + destination);
					String destinationName = ss[0];
					String destinationAddress = ss[1];
					AID remoteAms = new AID("ams@" + destinationName, AID.ISGUID);
					remoteAms.addAddresses(destinationAddress);
					PlatformID dest = new PlatformID(remoteAms);
					doMove(dest);
				}
				
				_hopcounter++;
				if (_itinerari.size() == _hopcounter) {
					_state++;
				}
				break;
			case 1:
				_done = true;
				System.out.println("\n\nAgent " + myAgent.getName() + " - Here I am. Itinerary terminated.");
			}
		}
	
		public boolean done(){
			return _done;
		}

		private void parseItinerary(Properties p){
			String prop = "";
			int i = 0;
			while(!(prop = p.getProperty("hop" + i, "")).equals("")) {
				_itinerari.add(prop);
				i++;
			}
		}

        private String _msg;
		private boolean _done;
		private int _state;
		private int _hopcounter;
		private Vector _itinerari;
    }

    private void help() {
        System.out.println("\n\nUse: MobileAgent(\"itinerary-file-name\")\n");
        System.out.println("This is a mobile agent that travels across different agent platforms.");
        System.out.println("The itinerary-file-name that must be provided as argument is a properties file of the form");
        System.out.println("  hop1=<platform-name> <platform-address>");
        System.out.println("  hop2=<platform-name> <platform-address>");
        System.out.println("  ...\n");
        System.out.println("For instance");
        System.out.println("  hop1=P1 http://localhost:7771");
        System.out.println("  hop2=P2 http://localhost:7772");
        System.out.println("  hop3=P1 http://localhost:7771");
    }
}
