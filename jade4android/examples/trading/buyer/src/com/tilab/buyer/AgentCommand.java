package com.tilab.buyer;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AgentCommand {
	
	private Map<String,Object> parametersMap;
	private final String commandName;
	
	public AgentCommand(String name){
		commandName = name;
		parametersMap = new HashMap<String, Object>();
	}
	
	public String getCommandName(){
		return commandName;
	}
	
	public void addCommandParam(String param, Object value){
		parametersMap.put(param, value);
	}
	
	public Object getCommandParam(String param){
		return parametersMap.get(param);
	}
	
	public Map<String,Object> getCommandParams() {
		return Collections.unmodifiableMap(parametersMap);
	}
	
	public void execute(Agent a, Behaviour b){
		a.addBehaviour(b);
	}
}
