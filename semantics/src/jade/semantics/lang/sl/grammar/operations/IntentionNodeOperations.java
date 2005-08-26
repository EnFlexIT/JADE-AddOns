package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
 
public class IntentionNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
    
		Term agent = ((IntentionNode)node).as_agent().sm_simplified_term();
		Formula formula = (Formula)((IntentionNode)node).as_formula().sm_simplified_formula();
		if ( formula instanceof TrueNode || formula instanceof FalseNode ) {
			FalseNode falseNode = new FalseNode();
			node.sm_simplified_formula(falseNode);
			falseNode.sm_simplified_formula(falseNode);
		}
		else {
			IntentionNode intentionNode = new IntentionNode(agent, formula);
			intentionNode.sm_simplified_formula(intentionNode);
			node.sm_simplified_formula(intentionNode);
		}
    }

    public boolean isMentalAttitude(Formula node, Term term)
    {
	return ((IntentionNode)node).as_agent().equals(term);
    }
}