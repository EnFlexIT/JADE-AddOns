package com.tilab.wsig;

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
	
	

}
