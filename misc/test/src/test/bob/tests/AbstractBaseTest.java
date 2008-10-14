package test.bob.tests;

import jade.content.Concept;

import java.util.LinkedList;

import content.onto.bob.beans.ClassOne;
import content.onto.bob.beans.ClassZero;
import content.onto.bob.beans.VeryComplexBean;

public abstract class AbstractBaseTest extends AbstractCheckSendAndReceiveTest {
	private static final long serialVersionUID = 1L;

	protected static final String NORMALSLOT_VALUE = "aValue";
	protected static final String SUPPRESSEDSTRING_VALUE = "aSuppressedString";
	protected java.util.List<ClassZero> javaList_model;
	protected java.util.List<ClassZero> jadeList_model;
	protected java.util.Set<ClassOne> javaSet_model;
	protected java.util.Set<ClassZero> jadeSet_model;
	protected jade.util.leap.List jadeList_value;
	protected java.util.List<ClassZero> javaList_value;
	protected jade.util.leap.Set jadeSet_value;
	protected java.util.Set<ClassOne> javaSet_value;

	private int counter;

	private ClassZero generateClassZero() {
		ClassZero cz = new ClassZero();
		cz.setFieldZeroZero(counter++);
		cz.setFieldZeroOne(counter++);
		return cz;
	}

	private ClassOne generateClassOne() {
		ClassOne co = new ClassOne();
		co.setFieldZeroZero(counter++);
		co.setFieldZeroOne(counter++);
		co.setFieldOneZero("s10-"+counter++);
		co.setFieldOneOne("s11-"+counter++);
		co.setFieldOneTwo("s12-"+counter++);
		return co;
	}

	@Override
	protected Concept getConcept() {
		counter = 0;
		javaList_model = new LinkedList<ClassZero>();
		javaList_model.add(generateClassZero());
		javaList_model.add(generateClassZero());

		jadeList_model = new LinkedList<ClassZero>();
		jadeList_model.add(generateClassZero());
		jadeList_model.add(generateClassZero());
		jadeList_model.add(generateClassZero());

		javaSet_model = new java.util.HashSet<ClassOne>();
		javaSet_model.add(generateClassOne());
		javaSet_model.add(generateClassOne());
		javaSet_model.add(generateClassOne());
		javaSet_model.add(generateClassOne());

		jadeSet_model = new java.util.HashSet<ClassZero>();
		jadeSet_model.add(generateClassZero());

		VeryComplexBean vcb = new VeryComplexBean();

		jadeList_value = new jade.util.leap.ArrayList();
		for (ClassZero cz: jadeList_model) {
			jadeList_value.add(cz);
		}

		javaList_value = new java.util.ArrayList();
		for (ClassZero cz: javaList_model) {
			javaList_value.add(cz);
		}

		jadeSet_value = new jade.util.leap.SortedSetImpl();
		for (ClassZero cz: jadeSet_model) {
			jadeSet_value.add(cz);
		}

		javaSet_value = new java.util.HashSet<ClassOne>();
		for (ClassOne co: javaSet_model) {
			javaSet_value.add(co);
		}

		vcb.setAJadeList(jadeList_value);
		vcb.setAJavaList(javaList_value);
		vcb.setAJadeSet(jadeSet_value);
		vcb.setAJavaSet(javaSet_value);
		vcb.setNormalSlot(NORMALSLOT_VALUE);
		vcb.setSuppressedString(SUPPRESSEDSTRING_VALUE);
		return vcb;
	}
}
