package test.bob.tests;

import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;
import content.onto.bob.beans.VeryComplexBean;

public class AggregateContentHierarchicalOntoTest extends AbstractBaseTest {
	private static final long serialVersionUID = 1L;

	@Override
	protected Ontology getOntology() {
		return testerAgent.getHierarchicalOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		VeryComplexBean vcb = (VeryComplexBean)c;
		VeryComplexBean originalVcb = (VeryComplexBean)getConcept();
		return originalVcb.equals(vcb);
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		return true;
	}
}
