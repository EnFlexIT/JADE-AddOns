package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;

public class BelieveNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {
        Term agent = ((BelieveNode) node).as_agent().sm_simplified_term();
        Formula formula = ((BelieveNode) node).as_formula().sm_simplified_formula();
        if (formula instanceof TrueNode || formula instanceof FalseNode) {
            node.sm_simplified_formula(formula);
        }
        else if (formula instanceof AndNode) {
			Formula formulaLeft = ((AndNode) formula).as_left_formula().sm_simplified_formula();
			Formula formulaRight = ((AndNode) formula).as_right_formula().sm_simplified_formula();
			node.sm_simplified_formula((new AndNode(new BelieveNode(agent, formulaLeft), new BelieveNode(agent,
                    formulaRight))).getSimplifiedFormula());
        }
        else if (formula.isMentalAttitude(agent)) {
			node.sm_simplified_formula(formula);
        }
        else {
			BelieveNode believeNode = new BelieveNode(agent, formula);
			believeNode.sm_simplified_formula(believeNode);
			node.sm_simplified_formula(believeNode);
        }
    }

    public boolean isMentalAttitude(Formula node, Term term) {
        return ((BelieveNode) node).as_agent().equals(term);
    }

    public Formula isBeliefFrom(Formula node, Term agent) {
        if (((BelieveNode) node).as_agent().equals(agent)) {
            return ((BelieveNode) node).as_formula();
        }
        return null;
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (super.isSubsumedBy(node, formula)) {
            return true;
        }
        else if (formula instanceof BelieveNode) {
            return ((BelieveNode) node).as_agent().equals(((BelieveNode) formula).as_agent())
                    && ((BelieveNode) node).as_formula().isSubsumedBy(((BelieveNode) formula).as_formula());
        }
//        else if (formula instanceof IntentionNode) {
//            return ((BelieveNode) node).as_agent().equals(((IntentionNode) formula).as_agent())
//                    && ((BelieveNode) node).isSubsumedBy(
//                            new BelieveNode(((IntentionNode) formula).as_agent(), (new NotNode(((IntentionNode) formula).as_formula())).getSimplifiedFormula()));
//        }
        // tests if (B ??A (exists ??X ??PSI)) is subsumed by (exists ??Y (B ??A
        // ??PHI))
        else if (((BelieveNode) node).as_formula() instanceof ExistsNode && formula instanceof ExistsNode
                && ((ExistsNode) formula).as_formula() instanceof BelieveNode) {
            return ((BelieveNode) node).as_agent().equals(
                    ((BelieveNode) ((ExistsNode) formula).as_formula()).as_agent())
                    && ((ExistsNode) ((BelieveNode) node).as_formula()).as_formula().isSubsumedBy(
                            ((BelieveNode) ((ExistsNode) formula).as_formula()).as_formula());
        }
        return false;
    }

    public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true) {
        if (((BelieveNode) node).as_agent().equals(j) && ((BelieveNode) node).as_formula().isMentalAttitude(i)) {
            return ((BelieveNode) node).as_formula();
        }
        else {
            return super.getDoubleMirror(node, i, j, default_result_is_true);
        }
    }
}
