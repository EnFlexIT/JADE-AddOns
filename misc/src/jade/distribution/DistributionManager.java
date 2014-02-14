package jade.distribution;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WrapperBehaviour;
import jade.domain.DFSubscriber;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Callback;
import jade.util.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility class that allows distributing generic items across a set of available agents
 * @author Giovanni Caire
 *
 * @param <Item> The class of the items to be assigned
 */
public class DistributionManager<Item> {

	protected Agent myAgent;
	private DFAgentDescription targetAgentsTemplate;
	private AgentSelectionPolicy<Item> selectionPolicy;
	private boolean ready = false;
	
	private List<PendingItem> pendingItems = new LinkedList<PendingItem>();
	
	private Map<Object, AID> targetAgentsByCorrelationKey = new HashMap<Object, AID>();
	
	protected Logger myLogger = Logger.getJADELogger(getClass().getName());
	
	public DistributionManager(String type) {
		this(createServiceTypeTemplate(type));
	}
	
	public DistributionManager(DFAgentDescription template) {
		targetAgentsTemplate = template;
	}
	
	public void setAgentSelectionPolicy(AgentSelectionPolicy<Item> selectionPolicy) {
		this.selectionPolicy = selectionPolicy;
	}
	
	/**
	 * 
	 */
	protected void onReady() {
	}
	
	protected void onRegister(DFAgentDescription dfad) {	
	}
	
	protected void onDeregister(DFAgentDescription dfad) {	
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public void start(Agent agent) {
		myAgent = agent;
		if (selectionPolicy == null) {
			// Selection policy not set --> Use the default one: round-robin
			selectionPolicy = new RoundRobinAgentSelectionPolicy<Item>();
		}
		myAgent.addBehaviour(new DFSubscriber(myAgent, targetAgentsTemplate){
			@Override
			public void onRegister(DFAgentDescription dfad) {
				selectionPolicy.handleAgentRegistered(dfad);
				DistributionManager.this.onRegister(dfad);
			}

			@Override
			public void onDeregister(DFAgentDescription dfad) {
				selectionPolicy.handleAgentDeregistered(dfad);
				DistributionManager.this.onDeregister(dfad);
			}
			
			@Override
			public void afterFirstNotification(DFAgentDescription[] dfds) {
				ready = true;
				onReady();
				for (PendingItem pi : pendingItems) {
					DistributionManager.this.getAgent(pi.item, pi.callback);
				}
			}
		});
	}
	
	
	/**
	 * Items with the same correlation key will be associated to the same target agent
	 * The default implementation returns null, i.e. this item is not correlated to any other item
	 */
	protected CorrelationInfo getCorrelationInfo(Item item) {
		return null;
	}
	
	protected String getNature(Item item) {
		return item.getClass().getSimpleName();
	}
	
	public void getAgent(final Item item, final Callback<AID> callback) {
		if (ready) {
			AID targetAgent = null;
			final CorrelationInfo ci = getCorrelationInfo(item);
			if (ci != null) {
				// If other items with the same correlationKey have already been managed, associate this item to the same agent 
				Object correlationKey = ci.getKey();
				targetAgent = targetAgentsByCorrelationKey.get(correlationKey);
				if (ci.isLast()) {
					targetAgentsByCorrelationKey.remove(correlationKey);
				}
			}
			if (targetAgent == null) {
				// Use the selectionPolicy to select a suitable agent to assign the new item to
				try {
					targetAgent = selectionPolicy.select(item);
				}
				catch (AsynchSelection as) {
					// Selection is done by means of a behaviour. Such behaviour must implement the AgentSelector interface 
					myAgent.addBehaviour(new WrapperBehaviour(as.getSelector()) {
						public int onEnd() {
							int ret = super.onEnd();
							Behaviour b = getWrappedBehaviour();
							if (b instanceof AgentSelectionPolicy.AgentSelector) {
								AID targetAgent = ((AgentSelectionPolicy.AgentSelector) b).getAgent();
								manageSelection(targetAgent, ci, callback);
								callback.onSuccess(targetAgent);
							}
							else {
								callback.onFailure(new Exception("Selection behaviour class "+b.getClass().getName()+" does not implement the AgentSelector interface"));
							}
							return ret;
						}
					});
					return;
				}
			}
			manageSelection(targetAgent, ci, callback);
		}
		else {
			// Store the item and manage it as soon as we are ready (see DFSubscriber.afterFirstNotification())
			pendingItems.add(new PendingItem(item, callback));
		}
	}
	
	private void manageSelection(AID targetAgent, CorrelationInfo ci, Callback<AID> callback) {
		if (targetAgent != null) {
			if (ci != null && !ci.isLast()) {
				targetAgentsByCorrelationKey.put(ci.getKey(), targetAgent);
			}
			callback.onSuccess(targetAgent);
		}
		else {
			// We couldn't find any agent to assign the item to
			callback.onFailure(new Exception("No suitable agent found"));
		}
	}
	
	
	private static DFAgentDescription createServiceTypeTemplate(String serviceType) {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(serviceType);
		template.addServices(sd);
		return template;
	}

	/**
	 * Inner class PendingItem
	 */
	private class PendingItem {
		private Item item;
		private Callback<AID> callback;
		
		public PendingItem(Item item, Callback<AID> callback) {
			this.item = item;
			this.callback = callback;
		}
	}
}
