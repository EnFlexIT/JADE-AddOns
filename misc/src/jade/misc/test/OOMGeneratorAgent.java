package jade.misc.test;

import java.util.ArrayList;
import java.util.List;

import jade.core.behaviours.TickerBehaviour;
import jade.core.Agent;

public class OOMGeneratorAgent extends Agent {

	private List<byte[]> list = new ArrayList<byte[]>();
	
	public void setup() {
		addBehaviour(new TickerBehaviour(this, 50) {
			public void onTick() {
				try {
					// Allocate 50K
					for (int i = 0; i < 10; ++i) {
						list.add(new byte[5000]);
					}
					if (getTickCount() % 200 == 0) {
						System.out.println("10 MB successfully allocated");
					}
				}
				catch (Throwable t) {
					t.printStackTrace();
					stop();
				}
			}
		});
	}
	
}
