package test.bob.tests;

import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

import java.util.Set;

import content.onto.bob.beans.VeryComplexBean;

public class SuppressSlotAnnotationHierarchicalOntoTest extends AbstractBaseTest {
	private static final long serialVersionUID = 1L;

	@Override
	protected Ontology getOntology() {
		return testerAgent.getHierarchicalOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		VeryComplexBean vcb = (VeryComplexBean)c;
		String s = VeryComplexBean.getSuppressedStringDefaultValue();
		return s.equals(vcb.getSuppressedString());
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		Set<String> effectiveSlotNames = getSlotNameSet(os);
		return !effectiveSlotNames.contains("suppressedString");
	}
}
