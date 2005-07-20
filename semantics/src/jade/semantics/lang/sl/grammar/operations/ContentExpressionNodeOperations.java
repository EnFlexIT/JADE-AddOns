package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.ContentExpression.Operations;

public class ContentExpressionNodeOperations extends DefaultNodeOperations implements Operations {

	public Node getElement(ContentExpression node) {
		if ( node instanceof MetaContentExpressionReferenceNode ) {
			if ( ((MetaContentExpressionReferenceNode)node).sm_value() == null ) {
				return null;
			}
			else {
				return ((MetaContentExpressionReferenceNode)node).sm_value().getElement();
			}
		}
		else {
			return node.children()[0];
		}
	}
}
