package com.tilab.wsig;

import jade.content.onto.*;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PrimitiveSchema;
import jade.content.schema.ObjectSchema;
import jade.util.leap.List;

import java.util.Hashtable;

public class MathOntology extends Ontology implements MathVocabulary{


	private final static Ontology theInstance = new MathOntology();


	public final static Ontology getInstance() {
		return theInstance;
	}


	/**
	 * Constructor
	 */
	public MathOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());

		try {
			add(new AgentActionSchema(SUM), Sum.class);
		    	add(new AgentActionSchema(DIFF), Diff.class);
			add(new AgentActionSchema(ABS), Abs.class);
			add(new ConceptSchema(COMPLEX), Complex.class);



			AgentActionSchema as = (AgentActionSchema) getSchema(ABS);
			as.add(COMPLEX, (ConceptSchema) getSchema(COMPLEX));
			as.setResult((PrimitiveSchema)getSchema(BasicOntology.FLOAT));                                                              			

			ConceptSchema cs = (ConceptSchema) getSchema(COMPLEX);
			cs.add(REAL, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			cs.add(IMMAGINARY, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));


			as = (AgentActionSchema) getSchema(SUM);
			as.add(FIRST_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			as.add(SECOND_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT), ObjectSchema.MANDATORY);
			as.setResult((PrimitiveSchema)getSchema(BasicOntology.FLOAT));


			as = (AgentActionSchema) getSchema(DIFF);
			as.add(FIRST_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			as.add(SECOND_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
			as.setResult((PrimitiveSchema)getSchema(BasicOntology.FLOAT));

			
			System.out.println("Action list --> " + getActionNames().toString());
			System.out.println("Concept list --> " + getConceptNames().toString());
			System.out.println("Predicate list --> " + getPredicateNames().toString());



		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
