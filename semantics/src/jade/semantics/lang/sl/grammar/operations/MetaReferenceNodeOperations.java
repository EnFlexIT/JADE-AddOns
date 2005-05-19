package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Node;
 
public class MetaReferenceNodeOperations 
    extends DefaultNodeOperations
{
    public boolean equals(Node node1, Node node2)
    {
	return (node1.getClass().equals(node2.getClass())) 
	    && (node1.toString() == node2.toString());
// 	return ((node1 instanceof MetaFormulaReferenceNode &&
// 		 node2 instanceof MetaFormulaReferenceNode &&
// 		 ((MetaFormulaReferenceNode)node1).lx_name().equals(((MetaFormulaReferenceNode)node2).lx_name())) ||
// 		(node1 instanceof MetaVariableReferenceNode &&
// 		 node2 instanceof MetaVariableReferenceNode &&
// 		 ((MetaVariableReferenceNode)node1).lx_name().equals(((MetaVariableReferenceNode)node2).lx_name())) ||
// 		(node1 instanceof MetaSymbolReferenceNode &&
// 		 node2 instanceof MetaSymbolReferenceNode &&
// 		 ((MetaSymbolReferenceNode)node1).lx_name().equals(((MetaSymbolReferenceNode)node2).lx_name())) ||
// 		(node1 instanceof MetaActionExpressionReferenceNode &&
// 		 node2 instanceof MetaActionExpressionReferenceNode &&
// 		 ((MetaActionExpressionReferenceNode)node1).lx_name().equals(((MetaActionExpressionReferenceNode)node2).lx_name())) ||
// 		(node1 instanceof MetaContentExpressionReferenceNode &&
// 		 node2 instanceof MetaContentExpressionReferenceNode &&
// 		 ((MetaContentExpressionReferenceNode)node1).lx_name().equals(((MetaContentExpressionReferenceNode)node2).lx_name())) ||
// 		(node1 instanceof MetaTermReferenceNode &&
// 		 node2 instanceof MetaTermReferenceNode &&
// 		 ((MetaTermReferenceNode)node1).lx_name().equals(((MetaTermReferenceNode)node2).lx_name())));
    }
}
