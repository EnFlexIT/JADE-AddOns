/*****************************************************************
"DistilledStateChartBehaviour" is a work based on the library "HSMBehaviour"
(authors: G. Caire, R. Delucchi, M. Griss, R. Kessler, B. Remick).
Changed files: "HSMBehaviour.java", "HSMEvent.java", "HSMPerformativeTransition.java",
"HSMTemplateTransition.java", "HSMTransition.java".
Last change date: 18/06/2010
Copyright (C) 2010 G. Fortino, F. Rango

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation;
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*****************************************************************/

package examples.meeting_dsc_example;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import java.util.*;

/**
 * @author G. Fortino, F. Rango
 */
public class MeetingBrokerDSCBehaviour extends DistilledStateChartBehaviour {
	int contResponses;
	int contRequestsToMeetingRequester;
	WakerBehaviour timer;
	public DistilledStateChartTransition t1;
	public DistilledStateChartTransition t2;
	public DistilledStateChartTransition t3;
	public DistilledStateChartTransition t4;
	public DistilledStateChartTransition t5;
	public DistilledStateChartTransition t6;
	public DistilledStateChartTransition t7;
	public DistilledStateChartTransition t8;
	public DistilledStateChartTransition t9;
	public DistilledStateChartTransition t10;
	public DistilledStateChartTransition t11;
	public DistilledStateChartTransition t12;
	public DistilledStateChartTransition t13;
	public DistilledStateChartTransition t14;
	public DistilledStateChartTransition t15;
	public DistilledStateChartTransition t16;
	public NEGOTIATION negotiation;
	public ARRANGE arrange;

	public MeetingBrokerDSCBehaviour(Agent anAgent, String aName) {
		super(anAgent, aName, MessageTemplate
				.MatchConversationId("DistilledStateChartBehaviour"), false);
		negotiation = new NEGOTIATION(anAgent, "NEGOTIATION");
		arrange = new ARRANGE(anAgent, "ARRANGE");
		addInitialState(DistilledStateChartTransition.DEEP_HISTORY);
		addState(negotiation);
		addState(arrange);
		setDefaultDeepHistoryEntrance(negotiation);
		negotiation.createT1();
		negotiation.started.createT2();
		negotiation.proposesent.createT3();
		arrange.createT4();
		arrange.started2.createT5();
		arrange.started2.createT6();
		arrange.started2.createT7();
		arrange.coordinated.createT8();
		arrange.coordinated.createT9();
		arrange.coordinated.createT10();
		arrange.excluded.createT11();
		arrange.excluded.createT12();
		arrange.excluded.createT13();
		arrange.timeout.createT14();
		arrange.timeout.createT15();
		arrange.timeout.createT16();
		addTransition(t1, negotiation);
		addTransition(t2, negotiation.started);
		addTransition(t3, negotiation.proposesent);
		addTransition(t4, arrange);
		addTransition(t5, arrange.started2);
		addTransition(t6, arrange.started2);
		addTransition(t7, arrange.started2);
		addTransition(t8, arrange.coordinated);
		addTransition(t9, arrange.coordinated);
		addTransition(t10, arrange.coordinated);
		addTransition(t11, arrange.excluded);
		addTransition(t12, arrange.excluded);
		addTransition(t13, arrange.excluded);
		addTransition(t14, arrange.timeout);
		addTransition(t15, arrange.timeout);
		addTransition(t16, arrange.timeout);
	}

	private class NEGOTIATION extends DistilledStateChartBehaviour {
		public STARTED started;
		public PROPOSESENT proposesent;

		public NEGOTIATION(Agent anAgent, String aName) {
			super(anAgent, aName, MessageTemplate
					.MatchConversationId("DistilledStateChartBehaviour"), false);
			started = new STARTED(anAgent, "STARTED");
			proposesent = new PROPOSESENT(anAgent, "PROPOSESENT");
			addInitialState(started);
			addState(proposesent);
		}

		public void createT1() {
			t1 = new DistilledStateChartTransition("T1", arrange,
					DistilledStateChartTransition.SHALLOW_HISTORY) {
				public boolean trigger(Behaviour sourceState, ACLMessage event) {
					if (event instanceof Propose) {
						return true;
					}
					return false;
				}

				public void action(ACLMessage event) {
					initializeTimer(event);
				}
			};
		}

		private void initializeTimer(ACLMessage e) {
			if (timer != null) {
				myAgent.removeBehaviour(timer);
			}

			timer = new WakerBehaviour(myAgent, 30000) { // TIMEOUT = 30 s
				protected void onWake() {
					ArrayList<AID> target = new ArrayList<AID>();
					target.add(myAgent.getAID());
					TimeOut msg = new TimeOut(ACLMessage.UNKNOWN, myAgent
							.getAID(), target, null);
					myAgent.send(msg);
					System.out
							.println("MEETING BROKER: timer elapsed! timeout event sent successfully!");
				}
			};
			myAgent.addBehaviour(timer);

			System.out.println("MEETING BROKER: timer initialized");
		}

		private void sendPropose(ACLMessage e) {
			try {
				System.out.println("MEETING BROKER: request arrived...");
				Request r = (Request) e;
				Appointment app = (Appointment) r.getContentObject();
				contResponses = app.getParticipantsList().size();

				// send propose to himself and all the participants
				ArrayList<AID> target = new ArrayList<AID>();
				target.add(myAgent.getAID());
				for (int i = 0; i < app.getParticipantsList().size(); i++) {
					target.add(app.getParticipantsList().get(i));
				}
				Propose msg = new Propose(ACLMessage.PROPOSE, myAgent.getAID(),
						target, app);
				myAgent.send(msg);

				System.out
						.println("MEETING BROKER: propose sent successfully to himself and all the participants!");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private class STARTED extends Behaviour {
			public STARTED(Agent anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT2() {
				t2 = new DistilledStateChartTransition("T2",
						negotiation.proposesent) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof Request) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						sendPropose(event);
					}
				};
			}
		}

		private class PROPOSESENT extends Behaviour {
			public PROPOSESENT(Agent anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT3() {
				t3 = new DistilledStateChartTransition("T3",
						negotiation.proposesent) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof Request) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						sendPropose(event);
					}
				};
			}
		}
	}

	private class ARRANGE extends DistilledStateChartBehaviour {
		ArrayList<AID> acceptedParticipants;
		public STARTED2 started2;
		public COORDINATED coordinated;
		public EXCLUDED excluded;
		public TIMEOUT timeout;

		public ARRANGE(Agent anAgent, String aName) {
			super(anAgent, aName, MessageTemplate
					.MatchConversationId("DistilledStateChartBehaviour"), false);
			started2 = new STARTED2(anAgent, "STARTED2");
			coordinated = new COORDINATED(anAgent, "COORDINATED");
			excluded = new EXCLUDED(anAgent, "EXCLUDED");
			timeout = new TIMEOUT(anAgent, "TIMEOUT");
			addInitialState(started2);
			addState(coordinated);
			addState(excluded);
			addState(timeout);
			setDefaultShallowHistoryEntrance(started2);
		}

		public void defaultShallowHistoryEntranceAction() {
			init(null);
		}

		public void createT4() {
			t4 = new DistilledStateChartTransition("T4",
					negotiation.proposesent) {
				public boolean trigger(Behaviour sourceState, ACLMessage event) {
					if (event instanceof ArrangementDone) {
						return true;
					}
					return false;
				}

				public void action(ACLMessage event) {
					completeArrangement(event);
				}
			};
		}

		private void completeArrangement(ACLMessage e) {
			if (acceptedParticipants.size() >= 2) {

				// send CONFIRM to accepted participants
				Confirm msg = new Confirm(ACLMessage.CONFIRM, myAgent.getAID(),
						acceptedParticipants, null);
				myAgent.send(msg);
				System.out
						.println("MEETING BROKER: meeting completed! sent confirm to accepted participants...");

				contRequestsToMeetingRequester = 0;
				acceptedParticipants = new ArrayList<AID>();
			} else {
				if (contRequestsToMeetingRequester > 2) {

					// send CANCEL to accepted participants
					Cancel msg = new Cancel(ACLMessage.CANCEL,
							myAgent.getAID(), acceptedParticipants, null);
					myAgent.send(msg);
					System.out
							.println("MEETING BROKER: meeting cancelled! sent cancel to accepted participants...");

					contRequestsToMeetingRequester = 0;
					acceptedParticipants = new ArrayList<AID>();
				} else {

					// ask for request to MeetingRequester
					contRequestsToMeetingRequester++;
					ArrayList<AID> target = new ArrayList<AID>();
					target.add(new AID("MeetingRequester", AID.ISLOCALNAME));
					AskForRequest msg = new AskForRequest(ACLMessage.UNKNOWN,
							myAgent.getAID(), target, null);
					myAgent.send(msg);
					System.out
							.println("MEETING BROKER: ask for request to Meeting Requester...");
				}
			}
		}

		private void sendArrangementDone(ACLMessage e) {
			ArrayList<AID> target = new ArrayList<AID>();
			target.add(myAgent.getAID());
			ArrangementDone msg = new ArrangementDone(ACLMessage.UNKNOWN,
					myAgent.getAID(), target, null);
			myAgent.send(msg);
			System.out
					.println("MEETING BROKER: sent arrangement done event...");
		}

		private void excludeParticipant(ACLMessage e) {
			// exclude current participant
			System.out.println("MEETING BROKER: agent "
					+ e.getSender().getName() + " excluded");
			contResponses--;
			if (contResponses == 0) {
				ArrayList<AID> target = new ArrayList<AID>();
				target.add(myAgent.getAID());
				ArrangementDone msg = new ArrangementDone(ACLMessage.UNKNOWN,
						myAgent.getAID(), target, null);
				myAgent.send(msg);
				System.out
						.println("MEETING BROKER: sent arrangement done event...");
			}
		}

		private void init(ACLMessage e) {
			contRequestsToMeetingRequester = 0;
			acceptedParticipants = new ArrayList<AID>();
		}

		private void acceptParticipant(ACLMessage e) {
			// accept current participant
			acceptedParticipants.add(e.getSender());
			contResponses--;
			System.out.println("MEETING BROKER: agent "
					+ e.getSender().getName()
					+ " added successfully to participants!");
			if (contResponses == 0) {
				ArrayList<AID> target = new ArrayList<AID>();
				target.add(myAgent.getAID());
				ArrangementDone msg = new ArrangementDone(ACLMessage.UNKNOWN,
						myAgent.getAID(), target, null);
				myAgent.send(msg);
				System.out
						.println("MEETING BROKER: sent arrangement done event...");
			}
		}

		private class STARTED2 extends Behaviour {
			public STARTED2(Agent anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT5() {
				t5 = new DistilledStateChartTransition("T5",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof AcceptProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						acceptParticipant(event);
					}
				};
			}

			public void createT6() {
				t6 = new DistilledStateChartTransition("T6", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof RejectProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						excludeParticipant(event);
					}
				};
			}

			public void createT7() {
				t7 = new DistilledStateChartTransition("T7", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof TimeOut) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						sendArrangementDone(event);
					}
				};
			}
		}

		private class COORDINATED extends Behaviour {
			public COORDINATED(Agent anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT8() {
				t8 = new DistilledStateChartTransition("T8",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof AcceptProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						acceptParticipant(event);
					}
				};
			}

			public void createT9() {
				t9 = new DistilledStateChartTransition("T9", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof RejectProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						excludeParticipant(event);
					}
				};
			}

			public void createT10() {
				t10 = new DistilledStateChartTransition("T10", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof TimeOut) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						sendArrangementDone(event);
					}
				};
			}
		}

		private class EXCLUDED extends Behaviour {
			public EXCLUDED(Agent anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT11() {
				t11 = new DistilledStateChartTransition("T11", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof RejectProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						excludeParticipant(event);
					}
				};
			}

			public void createT12() {
				t12 = new DistilledStateChartTransition("T12",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof AcceptProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						acceptParticipant(event);
					}
				};
			}

			public void createT13() {
				t13 = new DistilledStateChartTransition("T13", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof TimeOut) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						sendArrangementDone(event);
					}
				};
			}
		}

		private class TIMEOUT extends Behaviour {
			public TIMEOUT(Agent anAgent, String aName) {
				super(anAgent);
				this.myAgent = anAgent;
				setBehaviourName(aName);
			}

			public void action() {
			}

			public boolean done() {
				return true;
			}

			public void createT14() {
				t14 = new DistilledStateChartTransition("T14",
						arrange.coordinated) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof AcceptProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						acceptParticipant(event);
					}
				};
			}

			public void createT15() {
				t15 = new DistilledStateChartTransition("T15", arrange.excluded) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof RejectProposal) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						excludeParticipant(event);
					}
				};
			}

			public void createT16() {
				t16 = new DistilledStateChartTransition("T16", arrange.timeout) {
					public boolean trigger(Behaviour sourceState,
							ACLMessage event) {
						if (event instanceof TimeOut) {
							return true;
						}
						return false;
					}

					public void action(ACLMessage event) {
						sendArrangementDone(event);
					}
				};
			}
		}
	}
}