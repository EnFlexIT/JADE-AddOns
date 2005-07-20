package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.EquivNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ImpliesNode;
 
public class EquivNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {	
		Formula left = (Formula)((EquivNode)node).as_left_formula().sm_simplified_formula();
		Formula right = (Formula)((EquivNode)node).as_right_formula().sm_simplified_formula();
		node.sm_simplified_formula((new AndNode(new ImpliesNode(left, right), 
											    new ImpliesNode(right, left))).getSimplifiedFormula());
    }
}
