package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.ParameterNode;

public class ParameterNodeOperations extends DefaultNodeOperations {
	public int compare(Node node1, Node node2)
	{
		return ((ParameterNode)node1).lx_name().compareTo(((ParameterNode)node2).lx_name());
	}
}
