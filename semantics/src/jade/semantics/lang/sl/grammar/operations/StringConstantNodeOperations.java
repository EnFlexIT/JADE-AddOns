package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;

public class StringConstantNodeOperations
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		try {
			return Long.parseLong(((StringConstantNode)node).lx_value());
		}
		catch(NumberFormatException nfe) {
			return new Double(((StringConstantNode)node).lx_value()).longValue();
			
		}
	}
	
    public Double realValue(Constant node)
	{
		return Double.parseDouble(((StringConstantNode)node).lx_value());
	}
	
    public String stringValue(Constant node)
	{
		return ((StringConstantNode)node).lx_value();
	}
	
	public int compare(Node node1, Node node2)
	{
		return ((StringConstantNode)node1).lx_value().compareTo(((StringConstantNode)node2).lx_value());
	}
}
