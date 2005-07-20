package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.ListOfParameter;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Parameter;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;

import java.util.Comparator;

public class PredicateNodeOperations 
	extends FormulaNodeOperations {
		
	public void simplify(Formula node)
	{
		if ( ((PredicateNode)node).as_terms().size() == 0 ) {
			super.simplify(node);
		}
		else {
			PredicateNode original = (PredicateNode)node;
			PredicateNode simplified = new PredicateNode(original.as_symbol(), new ListOfTerm());
			for (int i=0; i<original.as_terms().size(); i++) {
				simplified.as_terms().add(original.as_terms().element(i).sm_simplified_term());
			}
			node.sm_simplified_formula(simplified);
			simplified.sm_simplified_formula(simplified);
		}
	}
}
