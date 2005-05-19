package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;

public class DoneNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {
        ActionExpression action_expression = ((DoneNode) node).as_expression();
        Formula formula = ((DoneNode) node).as_formula().sm_simplified_formula();
        if (formula instanceof FalseNode) {
            node.sm_simplified_formula(formula);
        }
        else {
			DoneNode doneNode = new DoneNode(action_expression, formula);
			doneNode.sm_simplified_formula(doneNode);
			node.sm_simplified_formula(doneNode);
        }
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (super.isSubsumedBy(node, formula)) {
            return true;
        }
        // tests if (done ??A ??PHI) is subsumed by (done ??A ??PSI)
        else if (formula instanceof DoneNode) {
            return ((DoneNode) node).as_expression().equals(((DoneNode) formula).as_expression())
                    && ((DoneNode) node).as_formula().isSubsumedBy(((DoneNode) formula).as_formula());
        }
        return false;
    }
}
