package com.tilab.wsig;

import jade.content.AgentAction;

@Result(success=float.class,refuse=String.class,failure=String.class)
public class Sum implements AgentAction {
	private float firstElement;
	private float secondElement;
	
	
	public float getFirstElement() {
		return firstElement;
	}
	public void setFirstElement(float firstElement) {
		this.firstElement = firstElement;
	}
	public float getSecondElement() {
		return secondElement;
	}
	public void setSecondElement(float secondElement) {
		this.secondElement = secondElement;
	}
	
	
}
