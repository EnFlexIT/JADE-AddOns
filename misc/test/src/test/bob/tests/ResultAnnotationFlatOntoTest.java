package test.bob.tests;

import content.onto.bob.beans.ClassZero;
import content.onto.bob.beans.SimpleAction;
import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.TermSchema;

public class ResultAnnotationFlatOntoTest extends AbstractBaseTest {
	private static final long serialVersionUID = 1L;
	private static final String SOMETHING = "something";

	@Override
	protected Concept getConcept() {
		SimpleAction sa = new SimpleAction();
		sa.setSomething(SOMETHING);
		return sa;
	}

	@Override
	protected Ontology getOntology() {
		return testerAgent.getFlatOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		SimpleAction sa = (SimpleAction)c;
		return SOMETHING.equals(sa.getSomething());
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		AgentActionSchema aas = (AgentActionSchema)os;
		TermSchema resultSchema = aas.getResultSchema();
		return ClassZero.class.getSimpleName().equals(resultSchema.getTypeName());
	}

}
