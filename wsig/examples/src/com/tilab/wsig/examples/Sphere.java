package com.tilab.wsig.examples;

import jade.content.onto.annotations.Slot;

public class Sphere extends Shape {
	
	private int radius;

	public Sphere() {
		this(0);
	}

	public Sphere(int radius) {
		this.radius = radius;
	}

	@Slot(mandatory=true)
	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public void calculateArea() {
		setArea(4*Math.PI*Math.pow(radius, 2)); 
	}

	@Override
	public String toString() {
		return "Sphere [radius=" + radius + ", area=" + getArea() + "]";
	}
}
