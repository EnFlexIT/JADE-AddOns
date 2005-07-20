package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Node;

public class ByteConstantNodeOperations 
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
		return node1.toString().compareTo(node2.toString());
	}
}
