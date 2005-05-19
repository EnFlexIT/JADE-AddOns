package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
 
public class ImpliesNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
    
		Formula left = (Formula)((ImpliesNode)node).as_left_formula().sm_simplified_formula();
		Formula right = (Formula)((ImpliesNode)node).as_right_formula().sm_simplified_formula();
		node.sm_simplified_formula((new OrNode(new NotNode(left), right)).getSimplifiedFormula());
    }
}
