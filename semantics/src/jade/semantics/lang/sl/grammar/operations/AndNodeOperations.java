package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;


public class AndNodeOperations extends FormulaNodeOperations {
	
    public void simplify(Formula node) {
		
        Formula left = ((AndNode) node).as_left_formula().sm_simplified_formula();
        Formula right = ((AndNode) node).as_right_formula().sm_simplified_formula();
        if (left.isSubsumedBy(right)) {
            node.sm_simplified_formula(right);
        }
        else if (right.isSubsumedBy(left)) {
			node.sm_simplified_formula(left);
        }
        else if ((new NotNode(right)).isSubsumedBy(left) || (new NotNode(left)).isSubsumedBy(right)) {
			FalseNode falseNode = new FalseNode();
			falseNode.sm_simplified_formula(falseNode);
			node.sm_simplified_formula(falseNode);
        }
        else if (left instanceof AndNode) {
			Formula leftLeft = ((AndNode) left).as_left_formula().sm_simplified_formula();
			Formula leftRight = ((AndNode) left).as_right_formula().sm_simplified_formula();
			node.sm_simplified_formula(new AndNode(leftLeft, new AndNode(leftRight, right)).getSimplifiedFormula());
        }
        else {
			node.sm_simplified_formula(orderAndLeaves(left, right));
        }
    }

    private AndNode orderAndLeaves(Formula left, Formula right) {
        // left is supposed to be a leaf (not a AndNode)
        // right is supposed to be either a leaf (terminal case) or an already
        // ordered AndNode
		AndNode andNode;
		if (right instanceof AndNode) {
			Formula middle = ((AndNode) right).as_left_formula();
			if (left.compare(middle) <= 0) {
				andNode = new AndNode(left, right);
				andNode.sm_simplified_formula(andNode);
			}
			else {
				andNode = (AndNode)new AndNode(middle, orderAndLeaves(left, ((AndNode) right).as_right_formula())).getSimplifiedFormula();
			}
		}
		else if (left.compare(right) <= 0) {
			andNode =  new AndNode(left, right);
			andNode.sm_simplified_formula(andNode);			
		}
		else {
			andNode =  new AndNode(right, left);
			andNode.sm_simplified_formula(andNode);
		}
		return andNode;
    }
    
    public boolean isMentalAttitude(Formula node, Term term) {
        return (((AndNode) node).as_left_formula().isMentalAttitude(term) && ((AndNode) node).as_right_formula()
                .isMentalAttitude(term));
    }

    public Formula isBeliefFrom(Formula node, Term agent) {
        Formula leftBelief = ((AndNode) node).as_left_formula().isBeliefFrom(agent);
        if (leftBelief != null) {
            Formula rightBelief = ((AndNode) node).as_right_formula().isBeliefFrom(agent);
            if (rightBelief != null) {
                return new AndNode(leftBelief, rightBelief);
            }
        }
        return null;
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (super.isSubsumedBy(node, formula)) {
            return true;
        }
        else {
            Formula left = ((AndNode) node).as_left_formula().getSimplifiedFormula();
            Formula right = ((AndNode) node).as_right_formula().getSimplifiedFormula();
            if (left.isSubsumedBy(formula) && right.isSubsumedBy(formula)) {
                return true;
            }
            else if (formula instanceof AndNode) {
                Formula left_form = ((AndNode) formula).as_left_formula().getSimplifiedFormula();
                Formula right_form = ((AndNode) formula).as_right_formula().getSimplifiedFormula();
                return (left.isSubsumedBy(left_form) || left.isSubsumedBy(right_form))
                        && (right.isSubsumedBy(left_form) || right.isSubsumedBy(right_form));
            }
            else {
                return false;
            }
        }
    }

    public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true) {
        return (new AndNode(((AndNode) node).as_left_formula().getDoubleMirror(i, j, true), ((AndNode) node)
                .as_right_formula().getDoubleMirror(i, j, true))).getSimplifiedFormula();
    }
}
