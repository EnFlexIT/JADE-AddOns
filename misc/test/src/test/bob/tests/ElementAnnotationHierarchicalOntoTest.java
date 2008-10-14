package test.bob.tests;

import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

public class ElementAnnotationHierarchicalOntoTest extends AbstractBaseTest {
	private static final long serialVersionUID = 1L;

	private static final String ELEMENT_NAME = "beanComplicato";

	@Override
	protected Ontology getOntology() {
		return testerAgent.getHierarchicalOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		return true;
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		return ELEMENT_NAME.equals(os.getTypeName());
	}

}
