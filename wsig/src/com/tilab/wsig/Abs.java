package com.tilab.wsig;

import jade.content.AgentAction;

@Result(success=int.class,refuse=String.class,failure=String.class)
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
