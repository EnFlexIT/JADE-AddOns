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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.core.behaviours.WrapperBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.util.Callback;
import jade.util.Logger;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Utility class that allows assigning generic "items" to a set of agents taking into
 * account all issues related to agents fault
 * 
 * @author Giovanni Caire
 *
 * @param <Item> The class of the items to be assigned
 */
public class AssignmentManager<Item> extends DistributionManager<Item> {
	
	private static final long serialVersionUID = 8757454234438L;
	
	/** Assignment context specifying that an new item is being assigned */
	public static final int ASSIGN_CONTEXT = 0;
	/** Assignment context specifying that a modified version of an already assigned item is being reassigned */
	public static final int REASSIGN_MODIFIED_CONTEXT = 1;
	/** Assignment context specifying that an item previously assigned to target agent A is being reassigned to A following A's restart */
	public static final int REASSIGN_RESTARTED_OWNER_CONTEXT = 2;
	/** Assignment context specifying that an item previously assigned to target agent A is being reassigned to another agent B following A's termination */
	public static final int REASSIGN_DEAD_OWNER_CONTEXT = 3;
	
	/**
	 * The DataStore key where retrieve-assignments behaviours must insert the list of
	 * items currently owned by a given target agent.
	 * @see createRetrieveAssignmentsBehaviour(AID) 
	 */
	public static final String OWNED_ITEMS = "OWNED-ITEMS";

	private AssignmentsMap<Item> assignmentsMap = new DefaultAssignmentsMap<Item>();	
	// Map an agent with the items that are going to be assigned to it: agent selection already done, but assignment not completed yet
	private Map<AID, Set<Item>> pendingItems = new HashMap<AID, Set<Item>>();	
	
	private Map<AID, DeadAgentInfo> itemsByDeadAgent = new HashMap<AID, DeadAgentInfo>();
	private long deadAgentsRestartTimeout = 30000;
	
	// Maps an item-key to a list of pending assignment callbacks. This is only used 
	// When the same item is assigned a second time (or more) while the first assignment has not completed yet.
	// As soon as the first assignment completes all pending callbacks are notified and flushed
	private Map<Object, List<Callback<AID>>> pendingAssignmentCallbacks = new HashMap<Object, List<Callback<AID>>>();
	
	private boolean needReconstruct = false;
	private Callback<Void> reconstructCallback;
	
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
	
	public long getDeadAgentsRestartTimeout() {
		return deadAgentsRestartTimeout;
	}
		
	/**
	 * This method is invoked when an item is assigned to a given assignee agent. This typically 
	 * occurs following a call to the <code>assign()</code> method, but can also occur in other situations
	 * e.g. following the termination of a target agent to reassign its items.
	 * Subclasses may redefine it to provide a behaviour responsible to 
	 * interact with the agent selected as assignee to make it aware of
	 * the assignment. If and how this interaction takes place is application-specific: for instance 
	 * it could be done by means of a one-shot notification or a request-reply protocol. 
	 * In any case the created behaviour, if any, must return a value > 0 in its onEnd() method to 
	 * indicate that the assignment interaction was successful or <= 0 to indicate an error.
	 * The default implementation returns null indicating that no interaction with the assignee
	 * agent is needed.    
	 * @param item The item to be assigned
	 * @param target The agent selected as assignee for the given item 
	 * @param context An indication of the context in which the assignment takes place. Possible values 
	 * are <code>ASSIGN_CONTEXT</code>, <code>REASSIGN_MODIFIED_CONTEXT</code>, <code>REASSIGN_RESTARTED_OWNER_CONTEXT</code> and
	 * <code>REASSIGN_DEAD_OWNER_CONTEXT</code>
	 * @return The assignment behaviour or null if no assignment interaction is needed.
	 * @see assign(Item, Callback)
	 */
	protected Behaviour createAssignmentBehaviour(Item item, AID target, int context) {
		return null;
	}
	
	/**
	 * This method is invoked (following a call to the <code>unassign()</code> or <code>unassignItem()</code> methods) 
	 * just after a given item has been un-assigned to a given target agent.
	 * Subclasses may redefine it to provide a behaviour responsible to 
	 * interact with the owner agent to make it aware of
	 * the un-assignment. If and how this interaction takes place is application-specific: for instance 
	 * it could be done by means of a one-shot notification or a request-reply protocol. 
	 * In any case the created behaviour, if any, must return a value > 0 in its onEnd() method to 
	 * indicate that the un-assignment interaction was successful or <= 0 to indicate an error.
	 * The default implementation returns null indicating that no interaction with the owner
	 * agent is needed.    
	 * @param item The item to be un-assigned
	 * @param owner The agent the given item is currently assigned to 
	 * @return The un-assignment behaviour or null if no un-assignment interaction is needed.
	 * @see unassign(Item, Callback)
	 * @see unassignItem(Object, Callback)
	 */
	protected Behaviour createUnassignmentBehaviour(Item item, AID owner) {
		return null;
	}
	
	
	/**
	 * This method is invoked (following a call to the <code>reconstructAssignments()</code> method) 
	 * once for each target agent.
	 * Subclasses may redefine it to provide a behaviour responsible to 
	 * interact with the indicated target agent to retrieve the items it is currently owning.
	 * If and how this interaction takes place is application-specific. 
	 * In any case the created behaviour, if any, must insert the retrieved list of items
	 * in its DataStore at the <code>OWNED_ITEMS</code> key.
	 * The default implementation returns null indicating that no interaction with the given target
	 * agent has to be performed. This corresponds to the situation where assignee agents are not
	 * aware of the items they have been assigned to and therefore the whole reconstruct assignments 
	 * process is not relevant.
	 * @param item The item to be un-assigned
	 * @param owner The agent the given item is currently assigned to 
	 * @return The un-assignment behaviour or null if no un-assignment interaction is needed.
	 * @see reconstructAssignments(Callback)
	 */
	protected Behaviour createRetrieveAssignmentsBehaviour(AID owner) {
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
		Assignment<Item> a = assignmentsMap.get(key);
		if (a != null) {
			return a.getAid();
		}
		return null;
	}
	
	/**
	 * Returns an unmodifiable view of the keys of all currently assigned items
	 * @return A Set representing an unmodifiable view of the keys of all currently assigned items
	 */
	public Set<Object> getAssignedKeys() {
		return assignmentsMap.getAllKeys();
	}
	
	/**
	 * Returns an unmodifiable view of all currently assigned items
	 * @return A Set representing an unmodifiable view of all currently assigned items
	 */
	public Set<Item> getAssignedItems() {
		return new ItemsSet();
	}
	
	private class ItemsSet extends AbstractCollection<Item> implements Set<Item> {
		public boolean add(Item item) {throw new UnsupportedOperationException();}
		public boolean addAll(Collection<? extends Item> arg0) {throw new UnsupportedOperationException();}
		public void clear() {throw new UnsupportedOperationException();}
		public boolean remove(Object arg0) {throw new UnsupportedOperationException();}
		public boolean removeAll(Collection<?> arg0) {throw new UnsupportedOperationException();}
		public boolean retainAll(Collection<?> arg0) {throw new UnsupportedOperationException();}

		public Iterator<Item> iterator() {
			return new Iterator<Item>() {
				private Iterator<Assignment<Item>> inner = assignmentsMap.iterator();
				
				public void remove() {throw new UnsupportedOperationException();}

				public boolean hasNext() {
					return inner.hasNext();
				}

				public Item next() {
					Assignment<Item> a = inner.next();
					return a.getItem();
				}
			};
		}

		public int size() {
			return assignmentsMap.size();
		}
	}
	
	/**
	 * Retrieve the number of items currently assigned to a given owner agent
	 * @param owner The owner agent
	 * @return The number of items currently assigned to the owner agent
	 */
	public int getAssignedCnt(AID owner) {
		return assignmentsMap.getAssignedCnt(owner);	
	}
	
	/**
	 * Retrieve an unmodifiable collection of the items currently assigned to a given agent
	 * @param owner The owner agent
	 * @return An unmodifiable collection of the items currently assigned to the owner agent
	 */
	public Collection<Item> getAssignedItems(AID owner) {
		List<Item> l = assignmentsMap.getAssignedItems(owner);
		if (l != null) {
			return Collections.unmodifiableCollection(assignmentsMap.getAssignedItems(owner));
		}
		else {
			return null;
		}
	}
	
	/**
	 * Return the current load of a given agent taking into account that assigned items may 
	 * have different weights. 
	 * @param aid The agent whose current load is requested
	 * @return The current load of the agent 
	 * @see getWeight(Item)
	 */
	public int getCurrentLoad(AID aid) {
		int load = 0;
		// Consider load of items already assigned to aid
		List<Item> l = assignmentsMap.getAssignedItems(aid);
		if (l != null) {
			for (Item item : l) {
				load += getWeight(item);
			}
		}
		
		// Add load of items that are going to be assigned to aid
		Set<Item> s = pendingItems.get(aid);
		if (s != null) {
			for (Item item : s) {
				load += getWeight(item);
			}
		}
		return load;
	}
	
	public int getWeight(Item item) {
		return 1;
	}
	
	/**
	 * Assign a given item to a suitable agent
	 * @param item The item to be assigned
	 * @param callback A Callback object whose onSuccess() method will be invoked (with 
	 * the assignee agent as parameter) when the assignment will be completed.
	 */
	public void assign(final Item item, final Callback<AID> callback) {
		assign(item, ASSIGN_CONTEXT, callback);
	}
	
	private void assign(final Item item, final int context, final Callback<AID> callback) {
		final Object key = getIdentifyingKey(item);
		Assignment<Item> a = assignmentsMap.get(key);
		if (a == null) {
			// New item
			assignmentsMap.prepareAssignment(key, item);
			getAgent(item, new Callback<AID>() {
				@Override
				public void onSuccess(AID targetAgent) {
					// Agent selection completed
					// Item is going to be assigned to targetAgent, but assignment is not completed yet
					addPendingItem(targetAgent, item);
					manageSelectionDone(item, key, targetAgent, context, callback);
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
			if (a.getItem().equals(item)) {
				// This is the very same version of an already assigned item --> 
				// If the original assignment is completed just invoke the callback onSuccess() method.
				// Otherwise store the callback: it will be notified as soon as the original assignment completes
				if (a.getAid() != null) {
					handleSuccess(item, key, a.getAid(), callback);
				}
				else {
					storePendingCallback(key, callback);
				}
			}
			else {
				// This is a modified version of an already assigned item --> Substitute it and go on specifying the MODIFIED context 
				a.setItem(item);
				manageSelectionDone(item, key, a.getAid(), REASSIGN_MODIFIED_CONTEXT, callback);
			}
		}
	}
	
	private void manageSelectionDone(final Item item, final Object identifyingKey, final AID targetAgent, int context, final Callback<AID> callback) {
		try {
			Behaviour b = createAssignmentBehaviour(item, targetAgent, context);
			if (b != null) {
				myAgent.addBehaviour(new WrapperBehaviour(b) {
					public int onEnd() {
						removePendingItem(targetAgent, item);
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
				removePendingItem(targetAgent, item);
				handleSuccess(item, identifyingKey, targetAgent, callback);
			}
		}
		catch (Exception e) {
			// Unexpected error creating the assignment behaviour
			handleError(item, identifyingKey, "Error creating assignment behaviour: "+e, callback);
		}
	}
	
	private void handleSuccess(Item item, Object identifyingKey, AID targetAgent, Callback<AID> callback) {
		Assignment<Item> a = assignmentsMap.completeAssignment(item, identifyingKey, targetAgent);
		if (a != null) {
			// Assignment completed successfully
			if (callback != null) {
				callback.onSuccess(targetAgent);
			}
			notifyPendingCallbacksSuccess(identifyingKey, targetAgent);
		}
		else {
			handleError(item, identifyingKey, getNature(item)+" "+identifyingKey+" unassigned in the meanwhile", callback);
		}
	}
	
	private void handleError(Item item, Object identifyingKey, String errorMessage, Callback<AID> callback) {
		assignmentsMap.remove(identifyingKey);
		if (callback != null) {
			callback.onFailure(new Exception(errorMessage));
		}
		notifyPendingCallbacksError(identifyingKey, errorMessage);
	}
	
	private void storePendingCallback(Object key, Callback<AID> callback) {
		if (callback != null) {
			List<Callback<AID>> l = pendingAssignmentCallbacks.get(key);
			if (l == null) {
				l = new ArrayList<Callback<AID>>();
				pendingAssignmentCallbacks.put(key, l);
			}
			l.add(callback);
		}
	}
	
	private void notifyPendingCallbacksSuccess(Object key, AID owner) {
		List<Callback<AID>> l = pendingAssignmentCallbacks.remove(key);
		if (l != null) {
			for (Callback<AID> callback : l) {
				callback.onSuccess(owner);
			}
		}
	}
	
	private void notifyPendingCallbacksError(Object key, String errorMessage) {
		List<Callback<AID>> l = pendingAssignmentCallbacks.remove(key);
		if (l != null) {
			for (Callback<AID> callback : l) {
				callback.onFailure(new Exception(errorMessage));
			}
		}
	}
	
	/**
	 * Un-assign the item identified by a given key
	 * @param key The key identifying the item to be un-assigned as would be returned by getIdentifyingKey()
	 * @param callback A Callback object whose onSuccess() method will be invoked (with 
	 * the previous assignee agent as parameter) when the un-assignment will be completed.
	 */
	public void unassignItem(Object key, Callback<AID> callback) {
		Assignment<Item> a = assignmentsMap.remove(key);
		if (a != null) {
			AID owner = a.getAid();
			if (owner != null) {
				unassign(a.getItem(), key, owner, callback);
				return;
			}
		}
		if (callback != null) {
			callback.onSuccess(null);
		}
	}
	
	/**
	 * Un-assign a given item 
	 * @param item The item to be un-assigned
	 * @param callback A Callback object whose onSuccess() method will be invoked (with 
	 * the previous assignee agent as parameter) when the un-assignment will be completed.
	 */
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
	

	@Override
	protected void onDeregister(DFAgentDescription dfad) {	
		// A given target agent is no longer there. Manage items that were assigned to it 
		AID aid = dfad.getName();
		List<Item> items = assignmentsMap.getAssignedItems(aid);
		if (items != null && items.size() > 0) {
			if (deadAgentsRestartTimeout > 0) {
				// Item re-assignment enabled --> Store the items assigned to the dead agent
				// so that as soon as it restarts we can re-assign them.
				// Also start a watchDog to reassign these items to other agents in case the dead agent does 
				// not restart in due time. 
				myLogger.log(Logger.WARNING, "Agent "+myAgent.getName()+" - Target agent "+aid.getLocalName()+" no longer available. Activate restore procedure to manage the items ("+items.size()+") that were assigned to it");
				WatchDog watchDog = new WatchDog(aid);
				myAgent.addBehaviour(watchDog);
				itemsByDeadAgent.put(aid, new DeadAgentInfo(items, watchDog));				
			}
			else {
				// Item re-assignment disabled --> just clean the current assignments
				for (Item item : items) {
					Object key = getIdentifyingKey(item);
					assignmentsMap.remove(key);
				}
			}
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
				manageSelectionDone(item, key, aid, REASSIGN_RESTARTED_OWNER_CONTEXT, new Callback<AID>() {
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
	
	@Override
	protected void targetAgentsInitialized() {
		myLogger.log(Logger.INFO, "Agent "+myAgent.getLocalName()+" - Target agents initialized");
		if (needReconstruct) {
			reconstruct(reconstructCallback);
		}
	}
	
	/**
	 * This method triggers the assignment reconstruction process that allows restoring the 
	 * current assignments after a restart of the agent using this AssignmentManager instance.
	 * This process implies interacting with all assignee agents to retrieve their current assignments.
	 * Such interactions are implemented by behaviours returned by the createRetrieveAssignmentsBehaviour()
	 * method. 
	 * @param callback
	 */
	public void reconstructAssignments(Callback<Void> callback) {
		switch (getStatus()) {
		case READY_STATUS:
			reconstruct(callback);
			break;
		case IDLE_STATUS:
			// We are still waiting for the retrieval of available target agents. Just set a flag: the
			// assignment reconstruction process will start as soon as target agents retrieval completes
			// See targetAgentsInitialized()
			needReconstruct = true;
			reconstructCallback = callback;
			break;
		default:
			throw new IllegalStateException("Assignment reconstruction process cannot be started in status "+getStatus());
		}
	}
	
	private void reconstruct(final Callback<Void> callback) {
		myLogger.log(Logger.INFO, "Agent "+myAgent.getLocalName()+" - Activating assignments reconstruction process");
		// Block whatever assignment that can occur during the reconstruction process. Such assignments will 
		// be managed as soon as the reconstruction process completes
		block();
		
		AID[] targetAgents = selectionPolicy.getAllAgents();
		ParallelBehaviour pb = new ParallelBehaviour() {
			public int onEnd() {
				// When the process is fully completed notify the callback if any and unblock assignments
				myLogger.log(Logger.INFO, "Agent "+myAgent.getLocalName()+" - Reconstruction completed: "+dump(null));
				if (callback != null) {
					callback.onSuccess(null);
				}
				unblock();
				return super.onEnd();
			}
		};
		pb.setBehaviourName("Assignment-Reconstructor");
		for (final AID aid : targetAgents) {
			Behaviour b = createRetrieveAssignmentsBehaviour(aid);
			if (b != null) {
				pb.addSubBehaviour(new WrapperBehaviour(b) {
					public int onEnd() {
						int ret = super.onEnd();
						fillReconstructedAssignemtns(aid, (Collection<Item>) getDataStore().get(OWNED_ITEMS));
						return ret;
					}
				});
			}
		}
		myAgent.addBehaviour(pb);
	}
	
	private void fillReconstructedAssignemtns(AID owner, Collection<Item> items) {
		if (items != null) {
			for (Item item : items) {
				Object key = getIdentifyingKey(item);
				assignmentsMap.prepareAssignment(key, item);
				assignmentsMap.completeAssignment(item, key, owner);
				myLogger.log(Logger.INFO, "Agent "+myAgent.getLocalName()+" - "+getNature(item)+" "+key+" re-associated to agent "+owner.getLocalName());
			}
		}
	}
	
	
	/**
	 * Inner class Assignment
	 */
	public static interface Assignment<Item> {
		
		public Item getItem();
		public void setItem(Item item);
		public AID getAid();
		
	} // END of inner class Assignment
	

	private void addPendingItem(AID selectedAgent, Item item) {
		Set<Item> ii = pendingItems.get(selectedAgent);
		if (ii == null) {
			ii = new HashSet<Item>();
			pendingItems.put(selectedAgent, ii);
		}
		ii.add(item);
	}
	
	private void removePendingItem(AID selectedAgent, Item item) {
		if (selectedAgent != null) { 
			Set<Item> ii = pendingItems.get(selectedAgent);
			if (ii != null) {
				ii.remove(item);
				if (ii.isEmpty()) {
					pendingItems.remove(selectedAgent);
				}
			}
		}
		else {
			for (AID aid : pendingItems.keySet()) {
				Set<Item> ii = pendingItems.get(aid);
				if (ii.remove(item)) {
					if (ii.isEmpty()) {
						pendingItems.remove(selectedAgent);
					}
					break;
				}
			}
		}
	}
	
	
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
				// We need to scan a copy of the items list since assignmentMap.remove() will modify the original one
				List<Item> items = new ArrayList<Item>(dai.items);
				for (final Item item : items) {
					final Object key = getIdentifyingKey(item);
					assignmentsMap.remove(key);
					assign(item, REASSIGN_DEAD_OWNER_CONTEXT, new Callback<AID>() {
						@Override
						public void onSuccess(AID result) {
							myLogger.log(Logger.INFO, "Agent "+myAgent.getName()+" - "+getNature(item)+" "+key+" reassigned to agent "+result.getLocalName());
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
		for (Object key : assignmentsMap.getAllKeys()) {
			Assignment<Item> a = assignmentsMap.get(key);
			String keyStr = key.toString();
			if (key != a.getItem()) {
				keyStr = keyStr+"["+a.getItem()+"]";
			}
			String agentStr = a.getAid() != null ? a.getAid().getLocalName() : "null";
			sb.append(prefix+"- "+keyStr+" --> "+agentStr+"\n");
			String assignmentInfo = getDumpAssignmentInfo(prefix, key, a.getItem());
			if (assignmentInfo != null) {
				sb.append(assignmentInfo);
			}
		}
		return sb.toString();
	}
	
	public String getDumpAssignmentInfo(String prefix, Object key, Item item) {
		return null;
	}
}
