package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.WordConstantNode;

public class WordConstantNodeOperations 
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return new Long(((WordConstantNode)node).lx_value());
	}
	
    public Double realValue(Constant node)
	{
		return new Double(((WordConstantNode)node).lx_value());
	}
	
    public String stringValue(Constant node)
	{
		return ((WordConstantNode)node).lx_value();
	}
	
	public int compare(Node node1, Node node2)
	{
		return ((WordConstantNode)node1).lx_value().compareTo(((WordConstantNode)node2).lx_value());
	}
}
