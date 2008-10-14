package test.bob.tests;

import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

public class AggregateSlotAnnotationFlatOntoTest extends AbstractBaseTest {
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

		//@AggregateSlot(type=ClassZero.class,cardMin=2,cardMax=4)
		boolean result = verifySlotFacets(os, "slot ", "aJavaListSlot", 2, 4, "ClassZero");

		//@AggregateSlot(type=ClassOne.class,cardMin=2)
		result = result && verifySlotFacets(os, "slot ", "aJavaSetSlot", 2, ObjectSchema.UNLIMITED, "ClassOne");

		//@AggregateSlot(type=ClassZero.class,cardMax=4)
		result = result && verifySlotFacets(os, "slot ", "aJadeListSlot", 0, 4, "ClassZero");

		//@AggregateSlot(type=ClassZero.class)
		result = result && verifySlotFacets(os, "slot ", "aJadeSetSlot", 0, ObjectSchema.UNLIMITED, "ClassZero");

		return result;
	}
}
