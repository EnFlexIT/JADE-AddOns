package jade.osgi.test;

import jade.core.Agent;

public class MyBundleAgent extends Agent {

	@Override
	protected void setup() {
		System.out.println("MyBundleAgent#setup: Agent " +this.getName() +" started!");
	}

	@Override
	protected void takeDown() {
		System.out.println("MyBundleAgent#setup: Agent " +this.getName() +" stopped!");
	}


}
