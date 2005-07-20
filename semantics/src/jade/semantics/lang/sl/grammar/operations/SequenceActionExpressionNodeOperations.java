package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;

 
public class SequenceActionExpressionNodeOperations
    extends ActionExpressionNodeOperations
{
	public void simplify(Term node)
	{
		SequenceActionExpressionNode action = (SequenceActionExpressionNode)node;
		Term left = action.as_left_action().sm_simplified_term();
		Term right = action.as_right_action().sm_simplified_term();
		SequenceActionExpressionNode simplified = new SequenceActionExpressionNode(left, right);
		node.sm_simplified_term(simplified);
		simplified.sm_simplified_term(simplified);
	}
 }
