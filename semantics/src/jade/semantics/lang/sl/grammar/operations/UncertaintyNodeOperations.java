package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
 
public class UncertaintyNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
	
		Term agent = ((UncertaintyNode)node).as_agent().sm_simplified_term();
		Formula formula = (Formula)((UncertaintyNode)node).as_formula().sm_simplified_formula();
		if ( formula instanceof TrueNode || 
			formula instanceof FalseNode || 
			formula.isMentalAttitude(agent) ) {
			FalseNode falseNode = new FalseNode();
			node.sm_simplified_formula(falseNode);
			falseNode.sm_simplified_formula(falseNode);
		}
		else {
			UncertaintyNode uncertaintyNode = new UncertaintyNode(agent, formula);
			uncertaintyNode.sm_simplified_formula(uncertaintyNode);
			node.sm_simplified_formula(uncertaintyNode);
		}
    }

    public boolean isMentalAttitude(Formula node, Term term)
    {
	return ((UncertaintyNode)node).as_agent().equals(term);
    }
}