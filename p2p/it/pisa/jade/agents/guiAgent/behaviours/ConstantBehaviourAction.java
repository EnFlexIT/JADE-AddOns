/**
 * 
 */
package it.pisa.jade.agents.guiAgent.behaviours;

/**
 * @author Domenico Trimboli
 *
 */
public enum ConstantBehaviourAction {
	LOAD_ACTIVITY_PLATFORM(1),
	SUBSCRIBE(2),
	DESUBSCRIBE(3),
	FEDERATE(4),
	DEFEDERATE(5),
	KILLAGENT(6);//,CATCH_EVENT(4);
	
	private ConstantBehaviourAction (int num){
	this.value=num;	
	}
	
	private int value;
	
	public int value(){
		return value;
	}

}
