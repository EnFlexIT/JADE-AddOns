package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

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
	
	public jade.semantics.lang.sl.tools.MatchResult match(Term node, Node expression)
	{
		return SLPatternManip.match(node, expression);
	}
	
	public Node instantiate(Term node, String varname, Node expression)
	{
		try {
			return SLPatternManip.instantiate(node, varname, expression);
		} catch (WrongTypeException e) {
			e.printStackTrace();
			return null;
		}
	}
}
