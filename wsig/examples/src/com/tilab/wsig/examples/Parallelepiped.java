package com.tilab.wsig.examples;

import jade.content.onto.annotations.Slot;

public class Parallelepiped extends Rectangle {
	
	private int height;

	public Parallelepiped() {
		super();
		height = 0;
	}

	public Parallelepiped(int x, int y, int z) {
		super(x, y);
		height = z;
	}

	@Slot(mandatory=true)
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void calculateArea() {
		setArea(2*(getLength()*getBreadth() +getBreadth()*height + getLength()*height));
	}

	@Override
	public String toString() {
		return "Parallelepiped [height=" + height + ", length=" + getLength() + ", breadth=" + getBreadth() + ", area=" + getArea() + "]";
	}
}
