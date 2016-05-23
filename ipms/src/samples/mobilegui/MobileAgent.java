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

package samples.mobilegui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import java.util.Iterator;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.PlatformID;

public class MobileAgent extends Agent implements ActionListener{
	public void setup(){
		_itigui = new ItineraryGui(this);
		_itigui.display();
	}
	
	public void actionPerformed(ActionEvent e){
		addBehaviour(new MyB(this,_itigui.getTableContent()));
		_text = _itigui.getTextToDisplay();
	        _useGUItoDisp = _itigui.getUseGUI();
                _itigui.hide();
 	        _itigui = null;
	}

	    private class MyB extends SimpleBehaviour{

		public MyB(Agent a, Vector iti){
		    super(a);
		    _state = 0;
		    _done = false;
		    _firstTime = true;
		    _hopcounter = 0;
		    _itinerary = iti;
		    
		}
		
	        private void displayMsg() {
		    
		  if (_useGUItoDisp){
		    JOptionPane.showMessageDialog(null,_text,"Information",JOptionPane.INFORMATION_MESSAGE);
		  } else {
		    System.out.println(_text);
		  }
		}
	       
	       
		public void action(){
		    switch(_state){
		    case 0:
		    	if (_firstTime) _firstTime = false;
		        else displayMsg();
		        System.out.println("Moving to " + 
	                   ((String[])_itinerary.get(_hopcounter))[0]);
		      	AID a = new AID(((String[])_itinerary.get(_hopcounter))[1],true);
		    	a.addAddresses(((String[])_itinerary.get(_hopcounter))[2]);
		    	PlatformID dest = new PlatformID(a);
		    	_hopcounter++;
		    	if(_itinerary.size() == _hopcounter)
		    		_state++;
		    	myAgent.doMove(dest);
		    	break;
		    case 1:
		        displayMsg();
		    	_done = true;
		    	System.out.println("Its the end, dying...");
		    	myAgent.doDelete();
		    }
		}
		
		public boolean done(){
		    return _done;
		}

	        private boolean _firstTime;
		private boolean _done;
		private int _state;
		private int _hopcounter;
		private Vector _itinerary;
	    }
	    
	    private transient ItineraryGui _itigui;
            private boolean _useGUItoDisp = false;
	    private String _text= "";
}
