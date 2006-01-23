package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.Term;

public class IdentifyingExpressionNodeOperations 
	extends TermNodeOperations {
		
	public void simplify(Term node)
	{
		Term term = ((IdentifyingExpression)node).as_term().sm_simplified_term();
		Formula formula = ((IdentifyingExpression)node).as_formula().sm_simplified_formula();
		IdentifyingExpression ire = null;
		if ( node instanceof IotaNode ) {
			ire = new IotaNode(term, formula);
		}
		else if ( node instanceof AllNode ) {
			ire = new AllNode(term, formula);
		}
		else if ( node instanceof SomeNode ) {
			ire = new SomeNode(term, formula);
		}
		else {
			ire = new AnyNode(term, formula);			
		}
		ire.sm_simplified_term(ire);
		node.sm_simplified_term(ire);
	}
}
