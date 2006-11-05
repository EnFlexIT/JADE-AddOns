/**
 * 
 */
package it.pisa.jade.agents.multicastAgent.behaviours;

import it.pisa.jade.agents.multicastAgent.MulticastManagerAgent;
import it.pisa.jade.agents.multicastAgent.ontologies.ConstantTypeMsg;
import it.pisa.jade.agents.multicastAgent.ontologies.FactoryMessage;
import it.pisa.jade.agents.multicastAgent.ontologies.MulticastMessage;
import it.pisa.jade.data.activePlatform.ActivePlatformStub;
import it.pisa.jade.data.activePlatform.RecordPlatform;
import it.pisa.jade.util.JadeObserver;
import it.pisa.jade.util.Parameter;
import it.pisa.jade.util.Values;
import it.pisa.jade.util.WrapperErrori;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Observable;

/**
 * @author Domenico Trimboli
 * 
 */
@SuppressWarnings("serial")
public class DiscoverBehavior extends MulticastBehaviour {
	private class ObservablePlatformEvent extends Observable {

		public void notifyObserversBehaviours(Object o) {
			setChanged();
			notifyObservers(o);
		}

	}

	class RefreshActivePlatform extends WakerBehaviour {
		private boolean promptActivityPlatform = (Parameter
				.get(Values.promptConsoleMessage) != null && Parameter.get(
						Values.promptConsoleMessage).equals(Parameter.YES)) ? true : false;

		public RefreshActivePlatform(Agent a, long period) {
			super(a, period);

		}

		protected void onTick() {
			synchronized (activePlatform) {
				SimpleDateFormat format = new SimpleDateFormat(
						"dd/MMM/yyyy:HH:mm:ss");
				if (promptActivityPlatform)
					System.out.println(format.format(new Date()) + " "
							+ localPlatform.getPlatformName()
							+ ">Piattaforme attive in questo momento");
				for (Iterator<RecordPlatform> it = activePlatform.getIterator(); it
						.hasNext();) {
					RecordPlatform r = it.next();
					if (r.getTTL() == 0) {
						it.remove();
						notifyObservers(r);
						if (promptActivityPlatform)
							System.out.println("La piattaforma:" + r
									+ " è stata eliminata");
					} else {
						if (promptActivityPlatform)System.out.println(">attiva :" + r.getPlatformName());
					}
				}
				activePlatform.tick();
			}
			// this test reduced the message send on the net
			if (!reducedMessageSending
					|| (reducedMessageSending && !justSendPong))
				((MulticastManagerAgent) myAgent).sendMessage(FactoryMessage
						.createMessage(ConstantTypeMsg.PING, localPlatform));
			myAgent.addBehaviour(new RefreshActivePlatform(myAgent, random()));
		}

		@Override
		protected void onWake() {
			onTick();
		}

	}

	private boolean done = false;

	boolean justSendPong = false;

	private String localIP;

	private RecordPlatform localPlatform;

	private ObservablePlatformEvent observerByDelegation = new ObservablePlatformEvent();

	boolean reducedMessageSending = (Parameter.get(Values.reducedMessageSending)!=null&&Parameter.get(Values.reducedMessageSending)
			.equals(Parameter.YES)) ? true : false;

	private int state;

	private boolean terminate = false;

	long timeRefreshMax =Parameter
			.getLong(Values.timeRefreshPlatformMax);

	long timeRefreshMin = Parameter
			.getLong(Values.timeRefreshPlatformMin);

	public DiscoverBehavior(Agent a, ActivePlatformStub activePlatform) {
		super(a, activePlatform);
		state = 1;
		this.activePlatform = activePlatform;
		localPlatform = prepareRecordLocalPlatform();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		switch (state) {
		case 1: {

			myAgent.addBehaviour(new RefreshActivePlatform(myAgent, random()));
			state++;
		}
		case 2: {
			((MulticastManagerAgent) myAgent).sendMessage(FactoryMessage
					.createMessage(ConstantTypeMsg.PING, localPlatform));
			state++;
		}
		case 3: {
			receiveMessage();
		}
		}
	}

	public void addObserver(JadeObserver observer) {
		observerByDelegation.addObserver(observer);
	}

	public void countObservers() {
		observerByDelegation.countObservers();
	}

	public void deleteObserver(JadeObserver observer) {
		observerByDelegation.deleteObserver(observer);
	}

	public void deleteObservers() {
		observerByDelegation.deleteObservers();
	}

	@Override
	public boolean done() {
		if (done) {
			myAgent.addBehaviour(new ErrorBehavior(myAgent));
		}
		return done || terminate;
	}

	public RecordPlatform getLocalPlatform() {
		return localPlatform;
	}

	public boolean isTerminate() {
		return terminate;
	}

	private void notifyObservers(Object object) {
		observerByDelegation.notifyObserversBehaviours(object);
	}

	private RecordPlatform prepareRecordLocalPlatform() {
		localIP = Parameter.get(Values.localIP);
		if (localIP == null) {
			try {
				localIP = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				WrapperErrori.wrap("impossible research local ip", e);
			}
		}
		String[] arr = myAgent.getAID().getAddressesArray();
		String jadePortNumber = arr[0].substring(("http://").length());
		jadePortNumber = jadePortNumber.substring(
				jadePortNumber.indexOf(":") + 1, jadePortNumber.indexOf("/"));
		String namePlatform = myAgent.getHap();
		String addresPlatform = "http://" + localIP + ":" + jadePortNumber
				+ "/acc";
		RecordPlatform r = new RecordPlatform(namePlatform, addresPlatform);
		return r;
	}

	private long random() {

		return (long) (timeRefreshMin + Math.random()
				* (timeRefreshMax - timeRefreshMin));
	}

	private void receiveMessage() {
		try {
			// Ricevo datagramma multicast
			DatagramPacket packet = ((MulticastManagerAgent) myAgent)
					.receiveMulticastMessage();
			MulticastMessage msg = FactoryMessage
					.parseMessage(packet.getData());

			if (msg != null /* && !msg.getSender().equals(localIP) */) {
				RecordPlatform record = msg.getPlatform();
				if (!record.getPlatformAddress().equals(
						localPlatform.getPlatformAddress())) {
					if (msg.getType().equals(ConstantTypeMsg.PING)) {
						if (!activePlatform.refreshRecord(record, Parameter
								.getInt(Values.ttlPlatformRecord))) {
							record
									.setTTL(Parameter
											.getInt(Values.ttlPlatformRecord));
							activePlatform.addPlatform(record);
							notifyObservers(msg);
						}
						justSendPong = true;
						((MulticastManagerAgent) myAgent)
								.sendMessage(FactoryMessage.createMessage(
										ConstantTypeMsg.PONG, localPlatform));

					} else if (msg.getType().equals(ConstantTypeMsg.PONG)) {
						if (!activePlatform.refreshRecord(record, Parameter
								.getInt(Values.ttlPlatformRecord))) {
							record
									.setTTL(Parameter
											.getInt(Values.ttlPlatformRecord));
							activePlatform.addPlatform(record);
							notifyObservers(msg);
						}
					} else {// sicuramente ConstantTypeMsg.EXIT
						activePlatform.removePlatform(record);
						notifyObservers(msg);
					}

				}
			}
		} catch (IOException e) {
			if (terminate == true) {

			} else {
				WrapperErrori.wrap("", e);
			}
		}

	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}

}
