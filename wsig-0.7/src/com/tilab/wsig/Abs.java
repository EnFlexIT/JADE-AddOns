package com.tilab.wsig;

import jade.content.AgentAction;


public class Abs implements AgentAction {
	private Complex complex;

	public Abs() {
	}

	public Complex getComplex() {
		return complex;
	}

	public void setComplex(Complex complex) {
		this.complex = complex;
	}

}
