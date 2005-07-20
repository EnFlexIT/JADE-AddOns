package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;

public class DoneNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {
        Term action = ((DoneNode) node).as_action().sm_simplified_term();
        Formula formula = ((DoneNode) node).as_formula().sm_simplified_formula();
        if (formula instanceof FalseNode) {
            node.sm_simplified_formula(formula);
        }
        else {
			DoneNode doneNode = new DoneNode(action, formula);
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
            return ((DoneNode) node).as_action().equals(((DoneNode) formula).as_action())
                    && ((DoneNode) node).as_formula().isSubsumedBy(((DoneNode) formula).as_formula());
        }
        return false;
    }
}
