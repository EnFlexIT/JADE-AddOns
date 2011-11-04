package com.tilab.wsig.examples;

import jade.content.onto.annotations.Slot;

public class Rectangle extends Shape {

	private int length;
	private int breadth;
	
	public Rectangle() {
		this(0, 0);
	}

	public Rectangle(int x, int y) {
		this.length = x;
		this.breadth = y;
	}

	@Slot(mandatory=true)
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Slot(mandatory=true)
	public int getBreadth() {
		return breadth;
	}

	public void setBreadth(int breadth) {
		this.breadth = breadth;
	}

	public void calculateArea() {
		setArea(length * breadth); 
	}
	
	@Override
	public String toString() {
		return "Rectangle [length=" + length + ", breadth=" + breadth + ", area=" + getArea() + "]";
	}

}
