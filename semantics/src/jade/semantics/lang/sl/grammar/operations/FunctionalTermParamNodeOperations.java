package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Term;

public class FunctionalTermParamNodeOperations extends TermNodeOperations {
	
	public void simplify(Term node)
	{
		node.sm_simplified_term(node);
	}
}
