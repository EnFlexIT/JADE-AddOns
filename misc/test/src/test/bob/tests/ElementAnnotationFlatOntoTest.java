package test.bob.tests;

import content.onto.bob.beans.VeryComplexBean;
import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

public class ElementAnnotationFlatOntoTest extends AbstractBaseTest {
	private static final long serialVersionUID = 1L;

	@Override
	protected Ontology getOntology() {
		return testerAgent.getFlatOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		return true;
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		return VeryComplexBean.EXPECTED_ELEMENT_NAME.equals(os.getTypeName());
	}

}
