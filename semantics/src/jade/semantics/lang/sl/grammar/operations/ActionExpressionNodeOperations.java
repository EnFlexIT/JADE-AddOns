package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;

 
public class ActionExpressionNodeOperations
    extends TermNodeOperations
    implements ActionExpression.Operations 
{
	public void simplify(Term node)
	{
		ActionExpressionNode action = (ActionExpressionNode)node;
		Term agent = action.as_agent().sm_simplified_term();
		Term term = action.as_term().sm_simplified_term();
		ActionExpressionNode simplified = new ActionExpressionNode(agent, term);
		simplified.sm_simplified_term(simplified);
		node.sm_simplified_term(simplified);
	}
	
	void addAgents2Result(ListOfTerm result, ListOfTerm agents)
    {
	for (int i=0; i<agents.size(); i++) {
	    if ( !result.contains(agents.element(i)) ) {
		result.add(agents.element(i));
	    }
	}
    }

    public ListOfTerm getAgents(ActionExpression node)
    {
	ListOfTerm result = new ListOfTerm();
	if ( node instanceof ActionExpressionNode ) {
	    result.add(((ActionExpressionNode)node).as_agent());
	}
	else if ( node instanceof AlternativeActionExpressionNode ) {
	    addAgents2Result(result,((ActionExpression)((AlternativeActionExpressionNode)node).as_left_action()).getAgents());
	    addAgents2Result(result,((ActionExpression)((AlternativeActionExpressionNode)node).as_right_action()).getAgents());
	}
	else if ( node instanceof SequenceActionExpressionNode ) {
	    addAgents2Result(result,((ActionExpression)((SequenceActionExpressionNode)node).as_left_action()).getAgents());
	    addAgents2Result(result,((ActionExpression)((SequenceActionExpressionNode)node).as_right_action()).getAgents());
	}
	return result;
    }
}
