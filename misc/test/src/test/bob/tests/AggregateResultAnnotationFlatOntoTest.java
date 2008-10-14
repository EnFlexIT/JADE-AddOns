package test.bob.tests;

import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ObjectSchema;
import content.onto.bob.beans.ExtendedAction;

public class AggregateResultAnnotationFlatOntoTest extends AbstractCheckSendAndReceiveTest {
	private static final long serialVersionUID = 1L;
	private static final String SOMETHING = "something";
	private static final String SOMETHING_ELSE = "something-else";

	@Override
	protected Concept getConcept() {
		ExtendedAction ea = new ExtendedAction();
		ea.setSomething(SOMETHING);
		ea.setSomethingElse(SOMETHING_ELSE);
		return ea;
	}

	@Override
	protected Ontology getOntology() {
		return testerAgent.getFlatOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		ExtendedAction ea = (ExtendedAction)c;
		return SOMETHING.equals(ea.getSomething()) && SOMETHING_ELSE.equals(ea.getSomethingElse());
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		AgentActionSchema aas = (AgentActionSchema)os;
		return verifyResultFacets(aas, 1, 2, "ClassOne");
	}
}
