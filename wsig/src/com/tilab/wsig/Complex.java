package com.tilab.wsig;

import jade.content.Concept;

public class Complex implements Concept {
	private float real;
	private float immaginary;

	public Complex(){}

	public float getReal() {
		return real;
	}
	public void setReal(float real) {
		this.real = real;
	}
	public float getImmaginary() {
		return immaginary;
	}
	public void setImmaginary(float immaginary) {
		this.immaginary = immaginary;
	}
}
