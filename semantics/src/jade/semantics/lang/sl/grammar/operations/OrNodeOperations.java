package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;

public class OrNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {
        Formula left = (Formula)((OrNode) node).as_left_formula().sm_simplified_formula();
        Formula right = (Formula)((OrNode) node).as_right_formula().sm_simplified_formula();
        if (left.isSubsumedBy(right)) {
            node.sm_simplified_formula(left);
        }
        else if (right.isSubsumedBy(left)) {
			node.sm_simplified_formula(right);
        }
        else if ((right.isSubsumedBy(new NotNode(left))) || (left.isSubsumedBy(new NotNode(right)))) {
			TrueNode trueNode = new TrueNode();
			trueNode.sm_simplified_formula(trueNode);
			node.sm_simplified_formula(trueNode);
        }
        else if (left instanceof AndNode) {
			Formula leftLeft = (Formula)((AndNode) left).as_left_formula().sm_simplified_formula();
			Formula leftRight = (Formula)((AndNode) left).as_right_formula().sm_simplified_formula();
			node.sm_simplified_formula((new AndNode(new OrNode(leftLeft, right), new OrNode(leftRight, right))).getSimplifiedFormula());
        }
        else if (right instanceof AndNode) {
			Formula rightLeft = (Formula)((AndNode) right).as_left_formula().sm_simplified_formula();
			Formula rightRight = (Formula)((AndNode) right).as_right_formula().sm_simplified_formula();
			node.sm_simplified_formula((new AndNode(new OrNode(rightLeft, left), new OrNode(rightRight, left))).getSimplifiedFormula());
        }
        else if (left instanceof OrNode) {
			Formula leftLeft = (Formula)((OrNode) left).as_left_formula().sm_simplified_formula();
			Formula leftRight = (Formula)((OrNode) left).as_right_formula().sm_simplified_formula();
			node.sm_simplified_formula(new OrNode(leftLeft, new OrNode(leftRight, right)).getSimplifiedFormula());
        }
        else {
			node.sm_simplified_formula(orderOrLeaves(left, right));
        }
    }

    private OrNode orderOrLeaves(Formula left, Formula right) {
		OrNode orNode;
        // left is supposed to be a leaf (not a OrNode)
        // right is supposed to be either a leaf (terminal case) or an already
        // ordered OrNode
        if (right instanceof OrNode) {
            Formula middle = ((OrNode) right).as_left_formula();
            if (left.toString().compareTo(middle.toString()) <= 0) {
                orNode = new OrNode(left, right);
				orNode.sm_simplified_formula(orNode);
            }
            else {
                orNode = (OrNode) new OrNode(middle, orderOrLeaves(left, ((OrNode) right).as_right_formula())).getSimplifiedFormula();
            }
        }
        else if (left.toString().compareTo(right.toString()) <= 0) {
			orNode = new OrNode(left, right);
			orNode.sm_simplified_formula(orNode);
			        }
        else {
			orNode = new OrNode(right, left);
			orNode.sm_simplified_formula(orNode);
		}
		return orNode;
    }

    public boolean isMentalAttitude(Formula node, Term term) {
        return (((OrNode) node).as_left_formula().isMentalAttitude(term) && ((OrNode) node).as_right_formula()
                .isMentalAttitude(term));
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (super.isSubsumedBy(node, formula)) {
            return true;
        }
        else {
            Formula left = ((OrNode) node).as_left_formula().getSimplifiedFormula();
            Formula right = ((OrNode) node).as_right_formula().getSimplifiedFormula();
            if (left.isSubsumedBy(formula) || right.isSubsumedBy(formula)) {
                return true;
            }
            else if (formula instanceof OrNode) {
                Formula left_form = ((OrNode) formula).as_left_formula().getSimplifiedFormula();
                Formula right_form = ((OrNode) formula).as_right_formula().getSimplifiedFormula();
                return (left.isSubsumedBy(left_form) && left.isSubsumedBy(right_form))
                        || (right.isSubsumedBy(left_form) && right.isSubsumedBy(right_form));
            }
            else {
                return false;
            }
        }
    }

    public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true) {
        return (new OrNode(((OrNode) node).as_left_formula().getDoubleMirror(i, j, true), ((OrNode) node)
                .as_right_formula().getDoubleMirror(i, j, true))).getSimplifiedFormula();
    }
}
