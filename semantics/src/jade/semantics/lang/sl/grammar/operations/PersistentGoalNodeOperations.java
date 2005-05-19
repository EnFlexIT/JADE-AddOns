package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.Term;
 
public class PersistentGoalNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
		node.sm_simplified_formula(new PersistentGoalNode(((PersistentGoalNode)node).as_agent(), 
														((PersistentGoalNode)node).as_formula()
														.getSimplifiedFormula()));
    }

    public boolean isMentalAttitude(Formula node, Term term)
    {
	return ((PersistentGoalNode)node).as_agent().equals(term);
    }
}
