package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.Term;
 
public class PersistentGoalNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
		Term agent = ((PersistentGoalNode)node).as_agent().sm_simplified_term();
		Formula formula = ((PersistentGoalNode)node).as_formula().sm_simplified_formula();
		PersistentGoalNode pgGoal = new PersistentGoalNode(agent, formula);
		pgGoal.sm_simplified_formula(pgGoal);
		node.sm_simplified_formula(pgGoal);
    }

    public boolean isMentalAttitude(Formula node, Term term)
    {
		return ((PersistentGoalNode)node).as_agent().equals(term);
    }
}
