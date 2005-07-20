package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.Node;

public class IntegerConstantNodeOperations
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return ((IntegerConstantNode)node).lx_value();
	}
	
    public Double realValue(Constant node)
	{
		return ((IntegerConstantNode)node).lx_value().doubleValue();
	}
	
    public String stringValue(Constant node)
	{
		return ((IntegerConstantNode)node).lx_value().toString();
	}
	
	public int compare(Node node1, Node node2)
	{
		return ((IntegerConstantNode)node1).lx_value().compareTo(((IntegerConstantNode)node2).lx_value());
	}
}
