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
import content.onto.bob.beans.ClassZero;
import content.onto.bob.beans.ExtendedAction;
import content.onto.bob.beans.SimpleAction;
import content.onto.bob.beans.TestBean;
import content.onto.bob.beans.TestBeanEx;
import content.onto.bob.beans.TestBeanOther;
import content.onto.bob.beans.TestSubBean;
import content.onto.bob.beans.VeryComplexBean;

public class TestHierarchicalBeanOntology extends Ontology {
	private static final long serialVersionUID = 1L;

	private static final String ONTOLOGY_NAME = "Hierarchical Test Ontology";
	private static TestHierarchicalBeanOntology INSTANCE;

	public final static TestHierarchicalBeanOntology getInstance() throws BeanOntologyBuilderException {
		if (INSTANCE == null) {
			INSTANCE = new TestHierarchicalBeanOntology();
		}
		return INSTANCE;
	}

	private TestHierarchicalBeanOntology() throws BeanOntologyBuilderException {
        super(ONTOLOGY_NAME, new Ontology[]{BasicOntology.getInstance()}, new BeanIntrospector());

		BeanOntologyBuilder bob = new BeanOntologyBuilder(this);

		bob.addSchemas(ClassZero.class, true);
		bob.addSchemas(ClassOne.class, true);
		bob.addSchemas(ClassTwo.class, true);

		bob.addSchemas(TestSubBean.class, true);

		bob.addSchemas(SimpleAction.class, true);
		bob.addSchemas(ExtendedAction.class, true);

		bob.addSchemas(ClassThree.class, true);

		bob.addSchemas(TestBean.class, true);
		bob.addSchemas(TestBeanEx.class, true);
		bob.addSchemas(TestBeanOther.class, true);

		bob.addSchemas(VeryComplexBean.class, true);

	
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
