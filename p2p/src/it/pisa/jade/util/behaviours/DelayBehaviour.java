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

package it.pisa.jade.util.behaviours;


import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
/**
 * 
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 *
 */
@SuppressWarnings("serial")
public class DelayBehaviour extends SimpleBehaviour 
{
	private long    timeout, 
	                wakeupTime;
	private boolean finished;
	
	public DelayBehaviour(Agent a, long timeout) 
	{
		super(a);
		this.timeout = timeout;
		finished = false;
	}
	
	public void onStart() {
		wakeupTime = System.currentTimeMillis() + timeout;
	}
		
	public void action() 
	{
		long dt = wakeupTime - System.currentTimeMillis();
		if (dt <= 0) {
			finished = true;
			handleElapsedTimeout();
		} else 
			block(dt);
			
	} //end of action
	
	protected void handleElapsedTimeout() {}
	
	public void reset(long timeout) {
	  wakeupTime = System.currentTimeMillis() + timeout ;
	  finished = false;
	}
	
	public boolean done() {
	  return finished;
	}
}
