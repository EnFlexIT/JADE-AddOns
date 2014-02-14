package jade.distribution;

import jade.core.behaviours.Behaviour;

public class AsynchSelection extends Exception {
	private Behaviour selector;
	
	public AsynchSelection(Behaviour selector) {
		super();
		this.selector = selector;
	}
	
	public Behaviour getSelector() {
		return selector;
	}
}
