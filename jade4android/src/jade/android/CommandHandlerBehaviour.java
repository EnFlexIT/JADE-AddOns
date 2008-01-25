package jade.android;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.wrapper.gateway.GatewayBehaviour;
import android.util.Log;

public class CommandHandlerBehaviour extends GatewayBehaviour {

	@Override
	protected void processCommand(final Object command) {
		if (command instanceof Behaviour) {
			SequentialBehaviour sb = new SequentialBehaviour(myAgent);
			sb.addSubBehaviour((Behaviour) command);
			sb.addSubBehaviour(new OneShotBehaviour(myAgent) {
				public void action() {
					releaseCommand(command);
				}
			});
			myAgent.addBehaviour(sb);
		}
		else {
			Log.v(null, "Unknown command "+command);
		}

	}

}
