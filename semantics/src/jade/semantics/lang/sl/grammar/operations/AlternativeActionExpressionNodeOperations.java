package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;

 
public class AlternativeActionExpressionNodeOperations
    extends ActionExpressionNodeOperations
{
	public void simplify(Term node)
	{
		AlternativeActionExpressionNode action = (AlternativeActionExpressionNode)node;
		Term left = action.as_left_action().sm_simplified_term();
		Term right = action.as_right_action().sm_simplified_term();
		
        if (left instanceof AlternativeActionExpressionNode) {
			Term leftLeft = ((AlternativeActionExpressionNode) left).as_left_action().sm_simplified_term();
			Term leftRight = ((AlternativeActionExpressionNode) left).as_right_action().sm_simplified_term();
			node.sm_simplified_term(new AlternativeActionExpressionNode(leftLeft, 
							        new AlternativeActionExpressionNode(leftRight, right)).getSimplifiedTerm());
        }
        else {
			node.sm_simplified_term(orderAlternativeLeaves(left, right));
        }
    }

    private AlternativeActionExpressionNode orderAlternativeLeaves(Term left, Term right) {
        // left is supposed to be a leaf (not a AlternativeActionExpressionNode)
        // right is supposed to be either a leaf (terminal case) or an already
        // ordered AlternativeActionExpressionNode
		AlternativeActionExpressionNode altNode;
		if (right instanceof AlternativeActionExpressionNode) {
			Term middle = ((AlternativeActionExpressionNode) right).as_left_action();
			if (left.compare(middle) <= 0) {
				altNode = new AlternativeActionExpressionNode(left, right);
				altNode.sm_simplified_term(altNode);
			}
			else {
				altNode = (AlternativeActionExpressionNode)new AlternativeActionExpressionNode(middle, 
						orderAlternativeLeaves(left, 
										       ((AlternativeActionExpressionNode) right).as_right_action())).getSimplifiedTerm();
			}
		}
		else if (left.compare(right) <= 0) {
			altNode =  new AlternativeActionExpressionNode(left, right);
			altNode.sm_simplified_term(altNode);			
		}
		else {
			altNode =  new AlternativeActionExpressionNode(right, left);
			altNode.sm_simplified_term(altNode);
		}
		return altNode;
    }
 }
