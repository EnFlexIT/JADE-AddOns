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

        String fproperties = null;
	String msg = null;

	//Get agent arguments
	Object[] args = getArguments();
	if (args!=null) {
	    if (args.length==2) {
                fproperties = args[0].toString(); 
                msg = args[1].toString(); 

		//Add agent behaviour.
		addBehaviour(new MyB(this,fproperties,msg));
            } else help();
	} else help();
    }

    private class MyB extends SimpleBehaviour{
	public MyB(Agent a, String fproperties, String msg){
	    super(a);

	    _msg = msg;
	    _a = a;
	    _state = 0;
	    _done = false;
	    _hopcounter = 0;
	    Properties p = new Properties();
	    _itinerari = new Vector();
	    try {
		p.load(new FileInputStream(fproperties));
		parseItinerary(p);
	    } catch(Exception e){
		System.out.println("Error reading agent file properties.");
	    }
	}
	
	public void action(){
	    switch(_state){
	    case 0:
		System.out.println(_msg);
                System.out.println("\n\nMoving to " + 
                   _itinerari.get(_hopcounter));
		AID a = new AID("ams@" + 
                  (String)_itinerari.get(_hopcounter) + ":1099/JADE",true);
		a.addAddresses("http://" + (String)_itinerari.get(_hopcounter)
			       + ":7778/acc");
		PlatformID dest = new PlatformID(a);
		_hopcounter++;
		if(_itinerari.size() == _hopcounter)
		    _state++;
		myAgent.doMove(dest);
		break;
	    case 1:
		_done = true;
		System.out.println(_msg);
		System.out.println("Agent " + _a.getName() + " has ended his itinerary.");
		myAgent.doDelete();
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
	private Agent _a;
    }

    private void help() {
        System.out.println("\n\nUse: MobileAgent(\"agent_properties_file_name\",\"msg_to_display\")\n");
        System.out.println("This is a mobile agent that travels between agencies. It follows the");
        System.out.println("itinerary written in the agent properties file and shows a");
        System.out.println("message in every platform. The arguments are: \n\n");
	System.out.println("   agent_properties_file_name      name of the file containing");
	System.out.println("                                   the agent itinerary.\n");
        System.out.println("   msg_to_display                  message that agent displays");
        System.out.println("                                   on every platform\n\n");
    }
}
