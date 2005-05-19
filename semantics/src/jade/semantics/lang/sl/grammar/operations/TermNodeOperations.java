package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Term;

public class TermNodeOperations extends DefaultNodeOperations implements Term.Operations {
	
	public void initNode(Node node)
	{		
		((Term)node).sm_simplified_term(null);
	}

	public void simplify(Term node)
	{
		node.sm_simplified_term(node);
	}
	
    public Term getSimplifiedTerm(Term node) {
		 doSimplifyNode(node);
		 return node.sm_simplified_term();
    }
}
