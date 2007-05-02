package com.tilab.wsig;

import jade.content.AgentAction;


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
