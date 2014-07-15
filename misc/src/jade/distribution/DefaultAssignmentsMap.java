package jade.distribution;

import jade.core.AID;
import jade.distribution.AssignmentManager.Assignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultAssignmentsMap<Item> implements AssignmentsMap<Item> {

	private static final long serialVersionUID = 254466899753L;
	
	private Map<Object, Assignment<Item>> assignments = new HashMap<Object, Assignment<Item>>();
	private Map<AID, List<Item>> itemsByAgent = new HashMap<AID, List<Item>>();
	
	@Override
	public void prepareAssignment(Object key, Item item) {
		assignments.put(key, new DefaultAssignment(item));
	}
	
	@Override
	public Assignment<Item> completeAssignment(Item item, Object key, AID owner) {
		Assignment<Item> a = assignments.get(key);
		if (a != null) {
			((DefaultAssignment) a).setAid(owner);
			List<Item> ii = itemsByAgent.get(owner);
			if (ii == null) {
				ii = new ArrayList<Item>();
				itemsByAgent.put(owner, ii);
			}
			if (!ii.contains(item)) {
				ii.add(item);
			}
		}
		return a;
	}
	
	@Override
	public Assignment<Item> get(Object key) {
		return assignments.get(key);
	}

	@Override
	public Assignment<Item> remove(Object key) {
		Assignment<Item> a = assignments.remove(key);
		if (a != null) {
			AID owner = a.getAid();
			if (owner != null) {
				List<Item> ii = itemsByAgent.get(owner);
				if (ii != null) {
					ii.remove(a.getItem());
					if (ii.isEmpty()) {
						itemsByAgent.remove(owner);
					}
				}
			}
		}
		return a;
	}

	@Override
	public Iterator<Assignment<Item>> iterator() {
		return assignments.values().iterator();
	}

	@Override
	public int size() {
		return assignments.size();
	}

	@Override
	public Set<Object> getAllKeys() {
		return Collections.unmodifiableSet(assignments.keySet());
	}

	@Override
	public List<Item> getAssignedItems(AID owner) {
		return itemsByAgent.get(owner);
	}

	@Override
	public int getAssignedCnt(AID owner) {
		List<Item> items = itemsByAgent.get(owner);
		return items != null ? items.size() : 0;
	}
	
	private class DefaultAssignment implements Assignment<Item> {
		private Item item;
		private AID aid;
		
		DefaultAssignment(Item item) {
			this.item = item;
		}
		
		@Override
		public Item getItem() {
			return item;
		}
		@Override
		public void setItem(Item item) {
			this.item = item;
		}
		@Override
		public AID getAid() {
			return aid;
		}
		void setAid(AID aid) {
			this.aid = aid;
		}
	}
}
