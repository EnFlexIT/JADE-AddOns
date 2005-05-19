package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.Formula;

public class FeasibleNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {
        ActionExpression action_expression = ((FeasibleNode) node).as_expression();
        Formula formula = (Formula)((FeasibleNode) node).as_formula().sm_simplified_formula();
        if (formula instanceof FalseNode) {
			node.sm_simplified_formula(formula);
        }
        else {
			FeasibleNode feasibleNode = new FeasibleNode(action_expression, formula);
			feasibleNode.sm_simplified_formula(feasibleNode);
			node.sm_simplified_formula(feasibleNode);
        }
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (super.isSubsumedBy(node, formula)) {
            return true;
        }
        // tests if (feasible ??A ??PHI) is subsumed by (feasible ??A ??PSI)
        else if (formula instanceof FeasibleNode) {
            return ((FeasibleNode) node).as_expression().equals(((FeasibleNode) formula).as_expression())
                    && ((FeasibleNode) node).as_formula().isSubsumedBy(((FeasibleNode) formula).as_formula());
        }
        return false;
    }
}
