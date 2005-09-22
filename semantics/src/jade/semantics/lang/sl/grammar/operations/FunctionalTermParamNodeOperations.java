package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.ListOfParameter;
import jade.semantics.lang.sl.grammar.Parameter;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.Term;

import java.util.Comparator;

public class FunctionalTermParamNodeOperations 
	extends TermNodeOperations 
	implements FunctionalTermParamNode.Operations {
	
    public Term getParameter(FunctionalTermParamNode node, String name) {
		ListOfParameter result = new ListOfParameter();
		if ( node.as_parameters().find(ParameterNode.class, "lx_name", name, result, false)) {
			return result.element(0).as_value();
		}
		else {
			return null;
		}
    }
	
    public void setParameter(FunctionalTermParamNode node, String name, Term term) {
		ListOfParameter result = new ListOfParameter();
		if ( node.as_parameters().find(ParameterNode.class, "lx_name", name, result, false)) {
			result.element(0).as_value(term);
		}
		else {
			node.as_parameters().add(new ParameterNode(term, name, new Boolean(false)));
		}
	}
	
	public void simplify(Term node)
	{
		if ( ((FunctionalTermParamNode)node).as_parameters().size() == 0 ) {
			super.simplify(node);
		}
		else {
			FunctionalTermParamNode original = (FunctionalTermParamNode)node;
			FunctionalTermParamNode simplified = (FunctionalTermParamNode)original.getClone();
			for (int i=0; i<simplified.as_parameters().size(); i++) {
				Parameter p = simplified.as_parameters().element(i);
				Term value = original.as_parameters().element(i).as_value().sm_simplified_term();
				p.as_value(value);
			}
			node.sm_simplified_term(simplified);
			simplified.sm_simplified_term(simplified);
			simplified.as_parameters().sort(new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((Parameter)o1).compare((Parameter)o2);
				}
				public boolean equals(Object obj) {
					return super.equals(obj);
				}
			});
		}
	}
}
