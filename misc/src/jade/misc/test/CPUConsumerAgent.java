package jade.misc.test;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

public class CPUConsumerAgent extends Agent {
	
	public void setup() {
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
			}
		});
	}

}
