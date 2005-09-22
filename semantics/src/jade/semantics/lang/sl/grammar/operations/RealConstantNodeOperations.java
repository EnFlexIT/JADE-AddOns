package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.RealConstantNode;

public class RealConstantNodeOperations 
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return new Long (((RealConstantNode)node).lx_value().longValue());
	}
	
    public Double realValue(Constant node)
	{
		return ((RealConstantNode)node).lx_value();
	}
	
    public String stringValue(Constant node)
	{
		return ((RealConstantNode)node).lx_value().toString();
	}
	
	public int compare(Node node1, Node node2)
	{
		return ((RealConstantNode)node1).lx_value().compareTo(((RealConstantNode)node2).lx_value());
	}
}
