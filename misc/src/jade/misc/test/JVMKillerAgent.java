package jade.misc.test;

import jade.core.behaviours.WakerBehaviour;
import jade.core.Agent;

public class JVMKillerAgent extends Agent {
	public void setup() {
		addBehaviour(new WakerBehaviour(this, 10000) {
			public void onWake() {
				System.out.println("JVMKillerAgent "+getLocalName()+" - Killing the JVM now!!!");
				System.exit(0);
			}
		});
	}

}
