package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Content.Operations;
import jade.semantics.lang.sl.parser.SLUnparser;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

public class ContentNodeOperations 
	extends DefaultNodeOperations
	implements Operations {

	public String toSLString(Content node) {
		String result = null;
		try {
			java.io.StringWriter writer = new java.io.StringWriter();
			(new SLUnparser(writer)).unparseTrueSL(node);
			result = writer.toString();
		}
		catch (Exception e) {}
		return result;
	}
	
    public Node getContentElement(Content node, int i) {
        return node.as_expressions().element(i).getElement();		
	}
	
    public void setContentElement(Content node, int i, Node element) {
        if (element instanceof ActionExpression) {
			node.as_expressions().replace(i, new ActionContentExpressionNode((ActionExpression)element));
        }
        else if (element instanceof Formula) {
			node.as_expressions().replace(i, new FormulaContentExpressionNode((Formula)element));            
        }
        else if (element instanceof IdentifyingExpression) {
			node.as_expressions().replace(i, new IdentifyingContentExpressionNode((IdentifyingExpression)element));            
        }		
	}
	
    public void addContentElement(Content node, Node element) {
		if ( node.as_expressions() == null ) {
			node.as_expressions(new ListOfContentExpression());			
		}
        if (element instanceof ActionExpression) {
			node.as_expressions().append(new ActionContentExpressionNode((ActionExpression)element));
        }
        else if (element instanceof Formula) {
			node.as_expressions().append(new FormulaContentExpressionNode((Formula)element));            
        }
        else if (element instanceof IdentifyingExpression) {
			node.as_expressions().append(new IdentifyingContentExpressionNode((IdentifyingExpression)element));            
        }		
	}
	
    public void setContentElements(Content node, int number){
		if ( node.as_expressions() != null ) {
			node.as_expressions().removeAll();
		}
		else {
			node.as_expressions(new ListOfContentExpression());
		}
        for (int i = 0 ; i < number ; i++) {
			node.as_expressions().add(null); // prepare a dummy content element
        }		
    }
	
	public int contentElementNumber(Content node) {
		return node.as_expressions().size();
	}
	
	public jade.semantics.lang.sl.tools.MatchResult match(Content node, Node expression)
	{
		return SLPatternManip.match(node, expression);
	}
	
	public Node instantiate(Content node, String varname, Node expression)
	{
		try {
			return SLPatternManip.instantiate(node, varname, expression);
		} catch (WrongTypeException e) {
			e.printStackTrace();
			return null;
		}
	}

}
