package com.tilab.wsig.examples;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;

public class MathBeanOntology extends BeanOntology {

	public static final String ONTOLOGY_NAME = "math-bean-ontology";
	
    private final static Ontology theInstance = new MathBeanOntology();

    public final static Ontology getInstance() {
        return theInstance;
    }

	private MathBeanOntology() {
		super(ONTOLOGY_NAME);
	
		try {
			add(Complex.class);
			add(AgentInfo.class);
			
			add(Sum.class);
		    add(Diff.class);
			add(Abs.class);
			add(Multiplication.class);
			add(SumComplex.class);
			add(GetComponents.class);
			add(GetRandom.class);
			add(PrintComplex.class);
			add(GetAgentInfo.class);
			add(ConvertDate.class);
			add(PrintTime.class);
			add(CompareNumbers.class);
	    }
	    catch (Exception e){
	        e.printStackTrace();
	    }
	}
}
