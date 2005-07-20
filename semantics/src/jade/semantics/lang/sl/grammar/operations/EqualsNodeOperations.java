package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.SLPatternManip;
 
public class EqualsNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
 
		Term left = ((EqualsNode)node).as_left_term().sm_simplified_term();
		Term right = ((EqualsNode)node).as_right_term().sm_simplified_term();
		
		if ( left.compare(right) == 1 ) {
			Term t = left;
			left = right;
			right = t;
		}

		if ( left instanceof AnyNode && ((AnyNode)left).as_term() instanceof Variable ) {
			AnyNode any = (AnyNode)left;
			Variable variable = (Variable)any.as_term();
			Formula formula = any.as_formula().sm_simplified_formula();
			try {
				Formula formPattern = (Formula)(SLPatternManip.toPattern(formula, variable));
				Formula formulaPrim = (Formula)SLPatternManip.instantiate(formPattern, "X", right);
				formulaPrim.getSimplifiedFormula();
				node.sm_simplified_formula(formulaPrim);
				return;
			}
			catch(Exception e) {e.printStackTrace();}
		}
		
		Formula equalsNode = new EqualsNode(left, right);
		equalsNode.sm_simplified_formula(equalsNode);
		node.sm_simplified_formula(equalsNode); 
    }
}
