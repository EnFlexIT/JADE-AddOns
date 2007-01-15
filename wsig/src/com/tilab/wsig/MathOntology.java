package com.tilab.wsig;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.ReflectiveIntrospector;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PrimitiveSchema;

public class MathOntology extends Ontology implements MathVocabulary{

	
	private final static Ontology theInstance = new MathOntology();
	
	
    public final static Ontology getInstance() {
        return theInstance;
    }


    /**
     * Constructor
     */
    private MathOntology() {
        super(ONTOLOGY_NAME, new ReflectiveIntrospector());

        try {
        	add(new AgentActionSchema(SUM), Sum.class);
        	add(new AgentActionSchema(DIFF), Diff.class);
        	add(new AgentActionSchema(ABS), Abs.class);
        	
        	add(new ConceptSchema(FIRST_ELEMENT), Float.class);
        	add(new ConceptSchema(SECOND_ELEMENT), Float.class);
        	add(new ConceptSchema(COMPLEX), Complex.class);
        	add(new ConceptSchema(RESULT), Float.class);
            
        	ConceptSchema cs = (ConceptSchema) getSchema(COMPLEX);          
            cs.add(REAL, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            cs.add(IMMAGINARY, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));

            AgentActionSchema as = (AgentActionSchema) getSchema(SUM);
            as.add(FIRST_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            as.add(SECOND_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            
            as = (AgentActionSchema) getSchema(DIFF);
            as.add(FIRST_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            as.add(SECOND_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));
            
            as = (AgentActionSchema) getSchema(ABS);
            as.add(COMPLEX_ELEMENT, (PrimitiveSchema) getSchema(BasicOntology.FLOAT));

        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
