package test.bob;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;

import java.util.List;

import content.onto.bob.beans.ClassOne;
import content.onto.bob.beans.ClassThree;
import content.onto.bob.beans.ClassTwo;
import content.onto.bob.beans.ExtendedAction;
import content.onto.bob.beans.SimpleAction;
import content.onto.bob.beans.SimplePredicate;
import content.onto.bob.beans.TestBean;
import content.onto.bob.beans.TestBeanEx;
import content.onto.bob.beans.TestBeanOther;
import content.onto.bob.beans.VeryComplexBean;

public class TestFlatBeanOntology extends BeanOntology {
	private static final long serialVersionUID = 1L;

	private static final String ONTOLOGY_NAME = "Flat Test Ontology";
	private static TestFlatBeanOntology INSTANCE;

	public final static TestFlatBeanOntology getInstance() throws BeanOntologyException {
		if (INSTANCE == null) {
			INSTANCE = new TestFlatBeanOntology();
		}
		return INSTANCE;
	}

	private TestFlatBeanOntology() throws BeanOntologyException {
        super(ONTOLOGY_NAME);

		add(SimpleAction.class);
		add(ExtendedAction.class);

		add(ClassThree.class, false);

		add(TestBean.class);
		add(TestBeanEx.class);
		add(TestBeanOther.class);

		add(ClassTwo.class);
		add(ClassOne.class);

		add(VeryComplexBean.class);

		add(SimplePredicate.class);
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
