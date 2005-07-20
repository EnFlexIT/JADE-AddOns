package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.SymbolNode;

public class SymbolNodeOperations extends DefaultNodeOperations {
	public int compare(Node node1, Node node2)
	{
		return ((SymbolNode)node1).lx_value().compareTo(((SymbolNode)node2).lx_value());
	}
}
