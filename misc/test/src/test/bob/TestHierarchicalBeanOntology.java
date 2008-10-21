package test.bob;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;

import java.util.List;

import content.onto.bob.beans.ClassOne;
import content.onto.bob.beans.ClassThree;
import content.onto.bob.beans.ClassTwo;
import content.onto.bob.beans.ClassZero;
import content.onto.bob.beans.ExtendedAction;
import content.onto.bob.beans.SimpleAction;
import content.onto.bob.beans.SimplePredicate;
import content.onto.bob.beans.TestBean;
import content.onto.bob.beans.TestBeanEx;
import content.onto.bob.beans.TestBeanOther;
import content.onto.bob.beans.TestSubBean;
import content.onto.bob.beans.VeryComplexBean;

public class TestHierarchicalBeanOntology extends BeanOntology {
	private static final long serialVersionUID = 1L;

	private static final String ONTOLOGY_NAME = "Hierarchical Test Ontology";
	private static TestHierarchicalBeanOntology INSTANCE;

	public final static TestHierarchicalBeanOntology getInstance() throws BeanOntologyException {
		if (INSTANCE == null) {
			INSTANCE = new TestHierarchicalBeanOntology();
		}
		return INSTANCE;
	}

	private TestHierarchicalBeanOntology() throws BeanOntologyException {
        super(ONTOLOGY_NAME);

		add(ClassZero.class, true);
		add(ClassOne.class, true);
		add(ClassTwo.class, true);

		add(TestSubBean.class, true);

		add(SimpleAction.class, true);
		add(ExtendedAction.class, true);

		add(ClassThree.class, true);

		add(TestBean.class, true);
		add(TestBeanEx.class, true);
		add(TestBeanOther.class, true);

		add(VeryComplexBean.class, true);

		add(SimplePredicate.class, true);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TestHierarchicalBeanOntology {");
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
