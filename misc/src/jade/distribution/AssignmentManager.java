package jade.distribution;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.core.behaviours.WrapperBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.util.Callback;
import jade.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class that allows assigning generic "items" to a set of agents taking into
 * account all issues related to agents fault
 * 
 * @author Giovanni Caire
 *
 * @param <Item> The class of the items to be assigned
 */
public class AssignmentManager<Item> extends DistributionManager<Item>{

	private Map<Object, AssignedItem> assignments = new HashMap<Object, AssignedItem>();
	private Map<AID, List<Item>> itemsByAgent = new HashMap<AID, List<Item>>();
	
	private Map<AID, DeadAgentInfo> itemsByDeadAgent = new HashMap<AID, DeadAgentInfo>();
	private long deadAgentsRestartTimeout = 30000;
	
	/**
	 * Create an AssignmentManager that assigns items to agents that provides a DF service
	 * of a given type.
	 * @param type The type of the DF service identifying agents items will be assigned to  
	 */
	public AssignmentManager(String type) {
		super(type);
	}
	
	/**
	 * Create an AssignmentManager that assigns items to agents whose DF registration matches 
	 * a given DF search template.
	 * @param type The DF search template used to identify agents items will be assigned to  
	 */
	public AssignmentManager(DFAgentDescription template) {
		super(template);
	}
	
	public void setDeadAgentsRestartTimeout(long timeout) {
		deadAgentsRestartTimeout = timeout;
	}
			
	/**
	 * Subclasses may redefine this method to provide a behaviour responsible to 
	 * interact with the agent selected as assignee of a given item to make it aware of
	 * the assignment. If and how this interaction takes place is application-specific: for instance 
	 * it could be done by means of a one-shot notification or a request-reply protocol. 
	 * In any case the created behaviour, if any, must return a value > 0 in its onEnd() method to 
	 * indicate that the assignment interaction was successful or <= 0 to indicate an error.
	 * The default implementation returns null indicating that no interaction with the assignee
	 * agent is needed.    
	 * @param item The item to be assigned
	 * @param target The agent selected as assignee for the given item 
	 * @return The assignment behaviour or null if no assignment interaction is needed.
	 */
	protected Behaviour createAssignmentBehaviour(Item item, AID target) {
		return null;
	}
	
	/**
	 * Subclasses may redefine this method to provide a behaviour responsible to 
	 * interact with the owner agent of a given item to make it aware of
	 * the un-assignment. If and how this interaction takes place is application-specific: for instance 
	 * it could be done by means of a one-shot notification or a request-reply protocol. 
	 * In any case the created behaviour, if any, must return a value > 0 in its onEnd() method to 
	 * indicate that the un-assignment interaction was successful or <= 0 to indicate an error.
	 * The default implementation returns null indicating that no interaction with the owner
	 * agent is needed.    
	 * @param item The item to be un-assigned
	 * @param owner The agent the given item is currently assigned to 
	 * @return The un-assignment behaviour or null if no un-assignment interaction is needed.
	 */
	protected Behaviour createUnassignmentBehaviour(Item item, AID owner) {
		return null;
	}
	
	/**
	 * Return a key uniquely identifying an item. This default implementation returns the 
	 * item itself. Subclasses can redefine this method to provide application-specific 
	 * key extraction methods. 
	 * @param item The item whose identifying key must be returned
	 * @return The key identifying the hive item
	 */
	protected Object getIdentifyingKey(Item item) {
		// By default the whole item is used as identifying key
		return item;
	}

	/**
	 * Retrieve the owner of a given item i.e. the agent the given item was assigned to.
	 * @param item The item whose owner must be retrieved.
	 * @return The agent the given item was assigned to
	 */
	public AID getOwner(Item item) {
		return getItemOwner(getIdentifyingKey(item));
	}
	
	/**
	 * Retrieve the owner of the item identified by a given key i.e. the agent that item was assigned to.
	 * @param item The item identifying key.
	 * @return The agent the item identified by the given key was assigned to
	 */
	public AID getItemOwner(Object key) {
		AssignedItem ai = assignments.get(key);
		if (ai != null) {
			return ai.aid;
		}
		return null;
	}
	
	/**
	 * Assign a given item to a suitable agent
	 * @param item The item to be assigned
	 * @param callback A Callback object whose onSuccess() method will be invoked (with 
	 * the assignee agent as parameter) when the assignment will be completed.
	 */
	public void assign(final Item item, final Callback<AID> callback) {
		final Object key = getIdentifyingKey(item);
		AssignedItem ai = assignments.get(key);
		if (ai == null) {
			// New item
			assignments.put(key, new AssignedItem(item));
			getAgent(item, new Callback<AID>() {
				@Override
				public void onSuccess(AID targetAgent) {
					// Agent selection completed
					manageSelectionDone(item, key, targetAgent, callback);
				}

				@Override
				public void onFailure(Throwable t) {
					// Agent selection failure 
					handleError(item, key, t.getMessage(), callback);
				}
			});
		}
		else {
			// Item already assigned
			handleSuccess(item, key, ai.aid, callback);
		}
	}
	
	private void manageSelectionDone(final Item item, final Object identifyingKey, final AID targetAgent, final Callback<AID> callback) {
		try {
			Behaviour b = createAssignmentBehaviour(item, targetAgent);
			if (b != null) {
				myAgent.addBehaviour(new WrapperBehaviour(b) {
					public int onEnd() {
						int ret = super.onEnd();
						if (ret > 0) {
							// Success
							handleSuccess(item, identifyingKey, targetAgent, callback);
						}
						else {
							// Failure
							handleError(item, identifyingKey, "Error assigning "+getNature(item)+" "+identifyingKey+" to selected agent "+targetAgent.getLocalName(), callback);
						}
						return ret;
					}
				});
			}
			else {
				// Implicit assignment (target agents need not to be aware they have been assigned a given item)
				handleSuccess(item, identifyingKey, targetAgent, callback);
			}
		}
		catch (Exception e) {
			// Unexpected error creating the assignment behaviour
			handleError(item, identifyingKey, "Error creating assignment behaviour: "+e, callback);
		}
	}
	
	private void handleSuccess(Item item, Object identifyingKey, AID targetAgent, Callback<AID> callback) {
		AssignedItem ai = assignments.get(identifyingKey);
		if (ai != null) {
			ai.aid = targetAgent;
			List<Item> ii = itemsByAgent.get(targetAgent);
			if (ii == null) {
				ii = new ArrayList<Item>();
				itemsByAgent.put(targetAgent, ii);
			}
			ii.add(item);
			if (callback != null) {
				callback.onSuccess(targetAgent);
			}
		}
		else {
			handleError(item, identifyingKey, getNature(item)+" "+identifyingKey+" unassigned in the meanwhile", callback);
		}
	}
	
	private void handleError(Item item, Object identifyingKey, String errorMessage, Callback<AID> callback) {
		assignments.remove(identifyingKey);
		if (callback != null) {
			callback.onFailure(new Exception(errorMessage));
		}
	}
	
	public void unassignItem(Object key, Callback<AID> callback) {
		AssignedItem ai = assignments.remove(key);
		if (ai != null) {
			AID owner = ai.aid;
			if (owner != null) {
				List<Item> ii = itemsByAgent.get(owner);
				if (ii != null) {
					ii.remove(ai.item);
					if (ii.isEmpty()) {
						itemsByAgent.remove(owner);
					}
				}
				unassign(ai.item, key, owner, callback);
				return;
			}
		}
		if (callback != null) {
			callback.onSuccess(null);
		}
	}
	
	public void unassign(Item item, Callback<AID> callback) {
		Object key = getIdentifyingKey(item);
		unassignItem(key, callback);
	}
	
	private void unassign(final Item item, final Object key, final AID owner, final Callback<AID> callback) {
		try {
			Behaviour b = createUnassignmentBehaviour(item, owner);
			if (b != null) {
				myAgent.addBehaviour(new WrapperBehaviour(b) {
					public int onEnd() {
						int ret = super.onEnd();
						if (ret > 0) {
							// Success
							if (callback != null) {
								callback.onSuccess(owner);
							}
						}
						else {
							// Failure
							if (callback != null) {
								callback.onFailure(new Exception("Error un-assigning "+getNature(item)+" "+key+" from agent "+owner.getLocalName()));
							}
						}
						return ret;
					}
				});
			}
			else {
				// Implicit unassignment (target agents are not aware they own a given item --> they need not to be informed)
				if (callback != null) {
					callback.onSuccess(owner);
				}
			}
		}
		catch (Exception e) {
			// Unexpected error creating the un-assignment behaviour
			if (callback != null) {
				callback.onFailure(new Exception("Error creating un-assignment behaviour: "+e));
			}
		}
	}
	
	/**
	 * 
	 */
	public void reconstructAssignments() {
		if (isReady()) {
			// FIXME: To be implemented
		}
		else {
			throw new IllegalStateException("Not ready");
		}
	}

	@Override
	protected void onDeregister(DFAgentDescription dfad) {	
		// A given target agent is no longer there. Store the items assigned to that agent
		// so that as soon as it restarts we can re-assign them.
		// Also start a watchDog to reassign these items to other agents in case the dead agent does 
		// not restart in due time 
		AID aid = dfad.getName();
		List<Item> items = itemsByAgent.remove(aid);
		if (items != null) {
			WatchDog watchDog = new WatchDog(aid);
			myAgent.addBehaviour(watchDog);
			itemsByDeadAgent.put(aid, new DeadAgentInfo(items, watchDog));				
		}
	}
	
	@Override
	protected void onRegister(DFAgentDescription dfad) {	
		// A new target agent is available. Check if this is a restarting agent and, in case,
		// reassign the items it had before the restart
		final AID aid = dfad.getName();
		DeadAgentInfo dai = itemsByDeadAgent.get(aid);
		if (dai != null) {
			// Restarted agent!
			myAgent.removeBehaviour(dai.watchDog);
			for (final Item item : dai.items) {
				final Object key = getIdentifyingKey(item);
				manageSelectionDone(item, key, aid, new Callback<AID>() {
					@Override
					public void onSuccess(AID targetAgent) {
						myLogger.log(Logger.INFO, "Agent "+myAgent.getName()+" - "+getNature(item)+" "+key+" reassigned to restarted agent "+aid.getLocalName());
					}

					@Override
					public void onFailure(Throwable t) {
						myLogger.log(Logger.WARNING, "Agent "+myAgent.getName()+" - Cannot reassign "+getNature(item)+" "+key+" to restarted agent "+aid.getLocalName()+". "+t.getMessage());
					}
				});
			}
		}
	}
	
	/**
	 * Inner class AssignedItem
	 */
	private class AssignedItem {
		private Item item;
		private AID aid;
		
		public AssignedItem(Item item) {
			this.item = item;
		}
	} // END of inner class AssignedItem
	

	/**
	 * Inner class DeadAgentInfo
	 */
	private class DeadAgentInfo {
		private List<Item> items;
		private WakerBehaviour watchDog;
		
		public DeadAgentInfo(List<Item> items, WakerBehaviour watchDog) {
			this.items = items;
			this.watchDog = watchDog;
		}
	}
	
	private class WatchDog extends WakerBehaviour {
		private AID targetAgent;
		
		public WatchDog(AID targetAgent) {
			super(null, deadAgentsRestartTimeout);
			setBehaviourName("WatchDog-"+targetAgent.getLocalName());
			this.targetAgent = targetAgent;
		}
		
		public void onWake() {
			// The dead agent did not restart in due time --> Redistribute its items among the available agents 
			myLogger.log(Logger.WARNING, "Agent "+myAgent.getName()+" - Target-Agent "+targetAgent.getLocalName()+" did not restart in due time --> redistribute its items");
			DeadAgentInfo dai = itemsByDeadAgent.remove(targetAgent);
			if (dai != null) {
				for (final Item item : dai.items) {
					final Object key = getIdentifyingKey(item);
					assignments.remove(key);
					assign(item, new Callback<AID>() {
						@Override
						public void onSuccess(AID result) {
							myLogger.log(Logger.INFO, "Agent "+myAgent.getName()+" - "+getNature(item)+" "+key+" reassigned to agent "+targetAgent.getLocalName());
						}

						@Override
						public void onFailure(Throwable t) {
							myLogger.log(Logger.WARNING, "Agent "+myAgent.getName()+" - Cannot reassign "+getNature(item)+" "+key+". "+t.getMessage());
						}
					});
				}
			}
		}
	}
	
	//////////////////////////////////////////////////////////
	// Debugging
	//////////////////////////////////////////////////////////
	public String dump(String prefix) {
		if (prefix == null) {
			prefix = "";
		}
		StringBuffer sb = new StringBuffer(prefix+"ASSIGNMENTS\n");
		for (Object key : assignments.keySet()) {
			AssignedItem ai = assignments.get(key);
			String keyStr = key.toString();
			if (key != ai.item) {
				keyStr = keyStr+"["+ai.item+"]";
			}
			String agentStr = ai.aid != null ? ai.aid.getLocalName() : "null";
			sb.append(prefix+"- "+keyStr+" --> "+agentStr+"\n");
		}
		return sb.toString();
	}
}
