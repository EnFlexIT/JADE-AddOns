/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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
package jade.distribution;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WrapperBehaviour;
import jade.domain.DFSubscriber;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Callback;
import jade.util.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility class that allows distributing generic items across a set of available agents
 * @author Giovanni Caire
 *
 * @param <Item> The class of the items to be assigned
 */
public class DistributionManager<Item> implements Serializable {

	public static final int IDLE_STATUS = 0;
	public static final int READY_STATUS = 1;
	public static final int BLOCKED_STATUS = -1;
	
	protected Agent myAgent;
	protected DFAgentDescription targetAgentsTemplate;
	protected AgentSelectionPolicy<Item> selectionPolicy;
	
	private int status = IDLE_STATUS;
	
	private List<PendingItem> pendingItems = new LinkedList<PendingItem>();
	private long pendingTimeoutMs = 120000;
	
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
	
	public AgentSelectionPolicy<Item> getAgentSelectionPolicy() {
		return selectionPolicy;
	}
	
	/** 
	 * Defines the maximum time in ms that this DistributionManager maintains an item while not yet READY
	 * (first notification from DF not yet received) before notifying a failure to the caller.
	 * Default 2 min. 
	 * 0 --> INFINITE
	 * NOTE: This method must be invoked before calling the start() method
	 * @param timeout
	 */
	public void setPendingTimeout(long timeout) {
		pendingTimeoutMs = timeout;
	}
	
	public long getPendingTimeout() {
		return pendingTimeoutMs;
	}
	
	/**
	 * 
	 */
	protected void targetAgentsInitialized() {
	}
	
	protected void onRegister(DFAgentDescription dfad) {	
	}
	
	protected void onDeregister(DFAgentDescription dfad) {	
	}
	
	public int getStatus() {
		return status;
	}
	
	public void block() {
		if (status == READY_STATUS) {
			status = BLOCKED_STATUS;
		}
		else {
			throw new IllegalStateException("DistributionManager can be blocked only when READY");
		}
	}
	
	public void unblock() {
		if (status == BLOCKED_STATUS) {
			status = READY_STATUS;
			flushPendingItems();
		}
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
				status = READY_STATUS;
				targetAgentsInitialized();
	
				// Subclasses may need to perform further initializations before pending items can be 
				// flushed. In that case they redefine targetAgentsInitialized() and can call block() inside it.
				// Therefore status can be BLOCKED at this stage.
				if (status != BLOCKED_STATUS) {
					flushPendingItems();
				}
			}
		});
		
		// Add the behaviour managing pending items that are to old
		if (pendingTimeoutMs > 0) {
			TickerBehaviour pendingPurger = new TickerBehaviour(myAgent, 30000) {
				@Override
				protected void onTick() {
					long now = System.currentTimeMillis();
					Iterator<PendingItem> it = pendingItems.iterator(); 
					while (it.hasNext()) {
						PendingItem pi = it.next();
						if (now - pi.timestamp > pendingTimeoutMs) {
							// Timeout for this pending item expired. No agent to give it
							if (pi.callback != null) {pi.callback.onFailure(new Exception("No suitable agent found"));}
							it.remove();
						}
						else {
							// Pending items are ordered by timestamp. If this pending item has not expired yet, for sure next ones won't
							break;
						}
					}
				}
			};
			pendingPurger.setBehaviourName("DM-Pending-Items-Purger");
			myAgent.addBehaviour(pendingPurger);
		}
	}

	private void flushPendingItems() {
		for (PendingItem pi : pendingItems) {
			DistributionManager.this.getAgent(pi.item, pi.callback);
		}
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
		if (status == READY_STATUS) {
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
								if (callback != null) {callback.onSuccess(targetAgent);}
							}
							else {
								if (callback != null) {callback.onFailure(new Exception("Selection behaviour class "+b.getClass().getName()+" does not implement the AgentSelector interface"));}
							}
							return ret;
						}
					});
					return;
				}
				catch (Exception e) {
					myLogger.log(Logger.WARNING, "Agent "+myAgent.getLocalName()+" - Unexpected error selecting agent for item "+item, e);
					if (callback != null) {callback.onFailure(e);}
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
			if (callback != null) {callback.onSuccess(targetAgent);}
		}
		else {
			// We couldn't find any agent to assign the item to
			if (callback != null) {callback.onFailure(new Exception("No suitable agent found"));}
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
		private long timestamp;
		
		public PendingItem(Item item, Callback<AID> callback) {
			this.item = item;
			this.callback = callback;
			this.timestamp = System.currentTimeMillis();
		}
	}
}
