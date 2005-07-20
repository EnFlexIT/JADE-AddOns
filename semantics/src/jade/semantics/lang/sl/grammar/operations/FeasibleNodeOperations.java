package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;

public class FeasibleNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {
        Term action = ((FeasibleNode) node).as_action().sm_simplified_term();
        Formula formula = (Formula)((FeasibleNode) node).as_formula().sm_simplified_formula();
        if (formula instanceof FalseNode) {
			node.sm_simplified_formula(formula);
        }
        else {
			FeasibleNode feasibleNode = new FeasibleNode(action, formula);
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
            return ((FeasibleNode) node).as_action().equals(((FeasibleNode) formula).as_action())
                    && ((FeasibleNode) node).as_formula().isSubsumedBy(((FeasibleNode) formula).as_formula());
        }
        return false;
    }
}
