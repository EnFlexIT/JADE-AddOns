/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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

package jade.cli;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.WrapperBehaviour;
import jade.util.Logger;

public class CLIAdminExecutorAgent extends Agent {
	private static final long serialVersionUID = -5324872230110030797L;
	
	private Logger myLogger = Logger.getMyLogger(getClass().getName());

	public CLIAdminExecutorAgent() {
		// Enable object2agent communication with queue of infinite length
		setEnabledO2ACommunication(true, 0);
	}
	
	protected void setup() {
		Behaviour b = new CyclicBehaviour(this) {
			private static final long serialVersionUID = -8186998548310024382L;

			public void action() {
				Object obj = myAgent.getO2AObject();
				if (obj != null) {
					if (obj instanceof Behaviour) {
						process((Behaviour) obj);
					}
					else {
						myLogger.log(Logger.WARNING, "Unexpected Object received: "+obj);
						// No behaviour to execute --> terminate immediately
						doDelete();
					}
				}
				else {
					block();
				}
			}
		};		
		setO2AManager(b);
		addBehaviour(b);
	}
	
	private void process(Behaviour b) {
		addBehaviour(new WrapperBehaviour(b) {
			private static final long serialVersionUID = 8791614876400432467L;

			public void onStart() {
				myLogger.log(Logger.INFO, "Starting execution of behaviour "+getWrappedBehaviour().getBehaviourName());
				super.onStart();
			}
			
			public int onEnd() {
				int result = super.onEnd();
				myLogger.log(Logger.INFO, "Execution of behaviour "+getWrappedBehaviour().getBehaviourName()+" terminated");

				// Terminate after behaviour execution
				myAgent.doDelete();
				return result;
			}
		});
	}
}
