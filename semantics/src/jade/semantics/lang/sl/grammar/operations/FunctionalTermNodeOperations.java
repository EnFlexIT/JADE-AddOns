package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;

public class FunctionalTermNodeOperations 
	extends TermNodeOperations {
		
	public void simplify(Term node)
	{
		if ( ((FunctionalTermNode)node).as_terms().size() == 0 ) {
			super.simplify(node);
		}
		else {
			FunctionalTermNode original = (FunctionalTermNode)node;
			FunctionalTermNode simplified = new FunctionalTermNode(original.as_symbol(), new ListOfTerm());
			for (int i=0; i<original.as_terms().size(); i++) {
				simplified.as_terms().add(original.as_terms().element(i).sm_simplified_term());
			}
			node.sm_simplified_term(simplified);
			simplified.sm_simplified_term(simplified);
		}
	}
}
