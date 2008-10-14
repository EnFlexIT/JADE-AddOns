package test.bob.tests;

import jade.content.Concept;
import jade.content.onto.Ontology;
import jade.content.schema.ObjectSchema;

import java.util.Set;

import content.onto.bob.beans.TestBean;

public class TestBeanFlatTest extends AbstractCheckSendAndReceiveTest {
	private static final long serialVersionUID = 1L;

	private static final String S1_VALUE = "1";
	private static final String S2_VALUE = "2";
	private static final String S3_VALUE = "3";
	private static final String S4_VALUE = "4";

	@Override
	protected Concept getConcept() {
		TestBean tb = new TestBean();
		tb.setStringOne(S1_VALUE);
		tb.setStringTwo(S2_VALUE);
		tb.setStringThree(S3_VALUE);
		tb.setStringFour(S4_VALUE);
		return tb;
	}

	@Override
	protected Ontology getOntology() {
		return testerAgent.getFlatOntology();
	}

	@Override
	protected boolean isConceptCorrectlyFilled(Concept c) {
		TestBean tb = (TestBean)c;
		boolean result =
			S1_VALUE.equals(tb.getStringOne()) &&
			S2_VALUE.equals(tb.getStringTwo()) &&
			S3_VALUE.equals(tb.getStringThree()) &&
			S4_VALUE.equals(tb.getStringFour());
		return result;
	}

	@Override
	protected boolean isSchemaCorrect(ObjectSchema os) {
		boolean result = true;

		String expectedSchemaName = TestBean.class.getSimpleName();
		String realSchemaName = os.getTypeName(); 
		if (!expectedSchemaName.equals(realSchemaName)) {
			result = false; 
			log("wrong schema name \""+realSchemaName+"\" (expected \""+expectedSchemaName+"\")");
		}
		Set<String> slotNames = getSlotNameSet(os);
		String[] expectedSlotNames = new String[] {"slotForStringOne", "stringTwo", "stringThree", "stringFour"};
		for (String expectedSlotName: expectedSlotNames) {
			if (!slotNames.remove(expectedSlotName)) {
				log("missing field "+expectedSlotName);
				result = false;
			}
		}
		if (slotNames.size() > 0) {
			result = false;
			for (String name: slotNames) {
				log("unexpected field "+name);
			}
		}
		return result;
	}

}
