package com.tilab.wsig.examples;

import jade.content.AgentAction;
import jade.content.onto.annotations.AggregateResult;

@AggregateResult(cardMin=2, cardMax=2, type=Shape.class)
public class OrderShapesByArea implements AgentAction {

	private Shape shape;
	private Parallelepiped parallelepiped;
	
	public OrderShapesByArea() {
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Parallelepiped getParallelepiped() {
		return parallelepiped;
	}

	public void setParallelepiped(Parallelepiped parallelepiped) {
		this.parallelepiped = parallelepiped;
	}

	@Override
	public String toString() {
		return "OrderShapesByArea [shape=" + shape + ", parallelepiped=" + parallelepiped + "]";
	}
}
