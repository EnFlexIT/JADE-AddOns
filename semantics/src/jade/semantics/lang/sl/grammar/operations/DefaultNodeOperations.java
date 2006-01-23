package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.EquivNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.parser.SLUnparser;
import jade.semantics.lang.sl.tools.SLEqualizer;
 
public class DefaultNodeOperations implements Node.Operations 
{
    public void initNode(Node node)
	{		
	}
	 
	protected ListOfNodes nodesToSimplify(Node node, ListOfNodes nodes)
	// Should only be called on nodes holding sm_simplified_node operations.
	{
		boolean simplify = (node instanceof Formula && ((Formula)node).sm_simplified_formula() == null ) 
			            || (node instanceof Term && ((Term)node).sm_simplified_term() == null );
		
		int currentSize = nodes.size();
		
		Node[] children = node.children();
		for (int i=0; i<children.length; i++) {
			Node child = children[i];
            if ( child != null ) {
				nodesToSimplify(child, nodes);	
			}
		}
      
		if ( simplify || nodes.size() != currentSize ) {
			if ( node instanceof Formula ) {
				((Formula)node).sm_simplified_formula(null);
				nodes.add(node);
			}
			else if ( node instanceof Term ){
				((Term)node).sm_simplified_term(null);
				nodes.add(node);
			}
		} 

		return nodes;
	}
	
	protected void doSimplifyNode(Node node) {
//		System.err.println("!!!!!!!!!!!!!!!!!doSimplifyNode("+node+")!!!!!!!!!!!!!!!!!!!!!!");
		ListOfNodes nodes = nodesToSimplify(node, new ListOfNodes());
		for (int i=0; i<nodes.size(); i++) {
//			System.err.println("simplify("+nodes.get(i)+")");
			if ( nodes.get(i) instanceof Formula ) {
				((Formula)nodes.get(i)).simplify();
			}
			else {
				((Term)nodes.get(i)).simplify();
			}
		}
  }
	
	public String toString(Node node)
    {
	java.io.StringWriter writer = new java.io.StringWriter();
	node.accept(new SLUnparser(writer));
	return writer.toString();
    }

    public boolean equals(Node node1, Node node2)
    {
	if ( node1 == node2) {
	    return true;
	}
	else {
		return new SLEqualizer().equals(node1, node2);
	}
    }

	public int compare(Node node1, Node node2)
	{
		return 0;
	}

	static public void installOperations() 
    {
		Node.installOperations(new Object[] {
			Node.ID,                          		new DefaultNodeOperations(),
			ContentNode.ID,                         new ContentNodeOperations(),
			ContentExpression.ID,          			new ContentExpressionNodeOperations(),
			Formula.ID,             				new FormulaNodeOperations(),
			Term.ID,                				new TermNodeOperations(),
			BelieveNode.ID,                         new BelieveNodeOperations(),
			IntentionNode.ID,                       new IntentionNodeOperations(),
			UncertaintyNode.ID,                     new UncertaintyNodeOperations(),
			PersistentGoalNode.ID,                  new PersistentGoalNodeOperations(),
			FeasibleNode.ID,                        new FeasibleNodeOperations(),
			DoneNode.ID,                            new DoneNodeOperations(),
			EqualsNode.ID,                          new EqualsNodeOperations(),
			TrueNode.ID,                            new TrueNodeOperations(),
			ForallNode.ID,                          new ForallNodeOperations(),
			ExistsNode.ID,                          new ExistsNodeOperations(),
			NotNode.ID,                             new NotNodeOperations(),
			AndNode.ID,                             new AndNodeOperations(),
			OrNode.ID,                              new OrNodeOperations(),
			EquivNode.ID,                           new EquivNodeOperations(),
			ImpliesNode.ID,                         new ImpliesNodeOperations(),
			PredicateNode.ID,                       new PredicateNodeOperations(),
			ActionExpressionNode.ID,                new ActionExpressionNodeOperations(),
			AlternativeActionExpressionNode.ID, 	new AlternativeActionExpressionNodeOperations(),
			SequenceActionExpressionNode.ID, 		new SequenceActionExpressionNodeOperations(),
			FunctionalTermNode.ID,                  new FunctionalTermNodeOperations(),
			FunctionalTermParamNode.ID,             new FunctionalTermParamNodeOperations(),
			SymbolNode.ID,                          new SymbolNodeOperations(),
			IntegerConstantNode.ID,					new IntegerConstantNodeOperations(),
			RealConstantNode.ID, 					new RealConstantNodeOperations(),
			StringConstantNode.ID,					new StringConstantNodeOperations(),
			WordConstantNode.ID,					new WordConstantNodeOperations(),
			ByteConstantNode.ID,					new ByteConstantNodeOperations(),
			DateTimeConstantNode.ID,				new DateTimeConstantNodeOperations(),
			ParameterNode.ID,	                    new ParameterNodeOperations(),
			IdentifyingExpression.ID,               new IdentifyingExpressionNodeOperations()
		});
    }
}
