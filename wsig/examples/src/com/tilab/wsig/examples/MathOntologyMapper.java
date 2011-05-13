/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package com.tilab.wsig.examples;

import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

import java.util.List;
import java.util.Set;

import com.tilab.wsig.store.OperationName;
import com.tilab.wsig.store.SuppressOperation;

public class MathOntologyMapper {
	
	public Abs toAbs(float real,float immaginary){
		Abs abs = new Abs();
		Complex complex = new Complex();
		complex.setImmaginary(immaginary);
		complex.setReal(real);
		abs.setComplex(complex);
		return abs;
	}
	
	public Abs toAbs(Complex complex){
		Abs abs = new Abs();
		abs.setComplex(complex);
		return abs;
	}
	
	public Abs toAbs(String real,String immaginary){
		Abs abs = new Abs();
		Complex complex = new Complex();
		complex.setImmaginary(Float.parseFloat(immaginary));
		complex.setReal(Float.parseFloat(real));
		abs.setComplex(complex);
		return abs;
	}

	private class AbsResultConverter {
		private String stringAbs;

		public AbsResultConverter(float abs) {
			stringAbs = Float.toString(abs);
		}
		
		@Slot(mandatory=true)
		public String getStringAbs() {
			return stringAbs;
		}  
	}
	
	// DO not expose a web service operation corresponding to the Diff ontology action
	@SuppressOperation
	public Abs toDiff(){
		return null;
	}

	// The web service operation corresponding to the sum ontology action will be called add
	@OperationName(name="Add")
	public Sum toSum(float firstElement, float secondElement){
		Sum sum = new Sum();
		sum.setFirstElement(firstElement);
		sum.setSecondElement(secondElement);
		return sum;
	}

	@OperationName(name="Add2")
	public Sum toSum(
			@Slot(mandatory=true) float firstElement, 
			@Slot(mandatory=true) float secondElement, 
			@Slot(mandatory=false) Float thirdElement){
		Sum sum = new Sum();
		sum.setFirstElement(firstElement);
		sum.setSecondElement(secondElement+((thirdElement!=null)?thirdElement.floatValue():0));
		return sum;
	}
	
	public PrintTime toPrintTime(){
		return new PrintTime();
	}
	
	public Multiplication toMultiplication(
			@AggregateSlot(cardMin=1, cardMax=5, type=Float.class) List numbers1,
			@AggregateSlot(cardMin=2, cardMax=3, type=String.class) jade.util.leap.List numbers2,
			float[] numbers3,
			@AggregateSlot(cardMin=0, cardMax=5, type=float.class) Set numbers4) {

		jade.util.leap.List numbers = new jade.util.leap.ArrayList();
		
		for (Object number1 : numbers1) {
			numbers.add((Float)number1);
		}
		
		for (Object number2 : ((jade.util.leap.ArrayList)numbers2).toList()) {
			numbers.add(Float.parseFloat((String)number2));
		}

		for (Object number3 : numbers3) {
			numbers.add((Float)number3);
		}

		for (Object number4 : numbers4) {
			numbers.add((Float)number4);
		}
		
		Multiplication mul = new Multiplication();
		mul.setNumbers(numbers);
		return mul;
	}
}
