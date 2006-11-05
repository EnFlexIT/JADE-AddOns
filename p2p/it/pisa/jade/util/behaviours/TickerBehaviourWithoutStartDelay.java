package it.pisa.jade.util.behaviours;




import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;


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