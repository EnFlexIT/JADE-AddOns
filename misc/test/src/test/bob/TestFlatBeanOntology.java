package test.bob;

import jade.content.onto.BasicOntology;
import jade.content.onto.BeanIntrospector;
import jade.content.onto.BeanOntologyBuilder;
import jade.content.onto.BeanOntologyBuilderException;
import jade.content.onto.Ontology;

import java.util.List;

import content.onto.bob.beans.ClassOne;
import content.onto.bob.beans.ClassThree;
import content.onto.bob.beans.ClassTwo;
import content.onto.bob.beans.ExtendedAction;
import content.onto.bob.beans.SimpleAction;
import content.onto.bob.beans.TestBean;
import content.onto.bob.beans.TestBeanEx;
import content.onto.bob.beans.TestBeanOther;
import content.onto.bob.beans.VeryComplexBean;

public class TestFlatBeanOntology extends Ontology {
	private static final long serialVersionUID = 1L;

	private static final String ONTOLOGY_NAME = "Flat Test Ontology";
	private static TestFlatBeanOntology INSTANCE;

	public final static TestFlatBeanOntology getInstance() throws BeanOntologyBuilderException {
		if (INSTANCE == null) {
			INSTANCE = new TestFlatBeanOntology();
		}
		return INSTANCE;
	}

	private TestFlatBeanOntology() throws BeanOntologyBuilderException {
        super(ONTOLOGY_NAME, new Ontology[]{BasicOntology.getInstance()}, new BeanIntrospector());

		BeanOntologyBuilder bob = new BeanOntologyBuilder(this);

		bob.add(SimpleAction.class);
		bob.add(ExtendedAction.class);

		bob.addSchemas(ClassThree.class, false);

		bob.add(TestBean.class);
		bob.add(TestBeanEx.class);
		bob.add(TestBeanOther.class);

		bob.add(ClassTwo.class);
		bob.add(ClassOne.class);

		bob.add(VeryComplexBean.class);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TestFlatBeanOntology {");
		List names = getActionNames();
		sb.append("actions=");
		sb.append(names);
		sb.append(' ');
		names = getConceptNames();
		sb.append("concepts=");
		sb.append(names);
		sb.append(' ');
		names = getPredicateNames();
		sb.append("predicates=");
		sb.append(names);
		sb.append('}');
		return sb.toString();
	}
}
