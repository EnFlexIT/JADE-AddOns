package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.SLPatternManip;
 
public class EqualsNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
 
		if ( ((EqualsNode)node).as_left_term() instanceof AnyNode 
				&& ((AnyNode)((EqualsNode)node).as_left_term()).as_term() instanceof Variable ) {
			AnyNode any = (AnyNode)((EqualsNode)node).as_left_term();
			Variable variable = (Variable)any.as_term();
			Formula formula = any.as_formula().sm_simplified_formula();
			try {
				Formula formPattern = (Formula)(SLPatternManip.toPattern(formula, variable));
				SLPatternManip.set(formPattern, "??X",((EqualsNode)node).as_right_term());
				Formula formulaPrim = (Formula)SLPatternManip.instantiate(formPattern);
				node.sm_simplified_formula(formulaPrim);
			}
			catch(Exception e) {
				e.printStackTrace();
				super.simplify(node);
			}
		}
		else {
			super.simplify(node);
		}
    }
}
