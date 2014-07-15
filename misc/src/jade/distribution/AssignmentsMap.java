package jade.distribution;

import jade.core.AID;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface AssignmentsMap<Item> extends Serializable {
	void prepareAssignment(Object key, Item item);
	AssignmentManager.Assignment<Item> completeAssignment(Item item, Object key, AID owner);
	
	AssignmentManager.Assignment<Item> get(Object key);
	AssignmentManager.Assignment<Item> remove(Object key);
	Iterator<AssignmentManager.Assignment<Item>> iterator();
	int size();

	Set<Object> getAllKeys();
	
	List<Item> getAssignedItems(AID owner);
	int getAssignedCnt(AID owner);
}
