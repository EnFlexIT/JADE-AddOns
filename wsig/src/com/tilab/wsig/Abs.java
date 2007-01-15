package com.tilab.wsig;

@Result(success=int.class,refuse=String.class,failure=String.class)
public class Abs {
	private Complex complex;

	public Complex getComplex() {
		return complex;
	}

	public void setComplex(Complex complex) {
		this.complex = complex;
	}
	
}
