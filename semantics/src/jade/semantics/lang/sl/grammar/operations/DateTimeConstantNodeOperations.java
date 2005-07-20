package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Node;

public class DateTimeConstantNodeOperations 
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return null;
	}
	
    public Double realValue(Constant node)
	{
		return null;
	}
	
    public String stringValue(Constant node)
	{
		return null;
	}
	
	public int compare(Node node1, Node node2)
	{
		return ((DateTimeConstantNode)node1).lx_value().compareTo(((DateTimeConstantNode)node2).lx_value());
	}
}
