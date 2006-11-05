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
 * @author Fabrizio Marozzo
 * @author Domenico Trimboli
 *
 */
public abstract class TickerBehaviourWithoutStartDelay extends SimpleBehaviour {
	private long wakeupTime, period;
	private boolean finished;
	private int tickCount = 0;

	
	public TickerBehaviourWithoutStartDelay(Agent a, long period) {
		super(a);
		if (period <= 0) {
			throw new IllegalArgumentException("Period must be greater than 0");
		}
		this.period = period;
	}
	
	public void onStart() {
		onTick();
		wakeupTime = System.currentTimeMillis() + period;
	}
	
	public final void action() {
		// Someone else may have stopped us in the meanwhile
		if (!finished) {
			long blockTime = wakeupTime - System.currentTimeMillis();
			if (blockTime <= 0) {
				// Timeout is expired --> execute the user defined action and
				// re-initialize wakeupTime
				tickCount++;
				onTick();
				wakeupTime = System.currentTimeMillis() + period;
				blockTime = period;
			}
			// Maybe this behaviour has been removed within the onTick() method
			if (myAgent != null) {
				block(blockTime);
			}
		}
	} 
	
	public final boolean done() {
		return finished;
	}
	
	/**
	 This method is invoked periodically with the period defined in the
	 constructor.
	 Subclasses are expected to define this method specifying the action
	 that must be performed at every tick.
	 */
	protected abstract void onTick();
	
	/**
	 * This method must be called to reset the behaviour and starts again
	 * @param period the new tick time
	 */
	public void reset(long period) {
		this.reset();
		if (period <= 0) {
			throw new IllegalArgumentException("Period must be greater than 0");
		}
		this.period = period;
	}
	
	/**
	 * This method must be called to reset the behaviour and starts again
	 * @param timeout indicates in how many milliseconds from now the behaviour
	 * must be waken up again. 
	 */
	public void reset() {
		super.reset();
		finished = false;
		tickCount = 0;
	}
	
	/**
	 * Make this <code>TickerBehaviour</code> terminate.
	 * Calling stop() has the same effect as removing this TickerBehaviour, but is Thread safe
	 */
	public void stop() {
		finished = true;
	}
	
	/**
	 * Retrieve how many ticks were done (i.e. how many times this
	 * behaviour was executed) since the last reset.
	 * @return The number of ticks since the last reset
	 */
	public final int getTickCount() {
		return tickCount;
	}
	
	

	
	
}