package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.EquivNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaActionExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.PropositionSymbolNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.ResultNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.grammar.VariableNode;
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
		
		Node[] children = node.children();
		for (int i=0; i<children.length; i++) {
			Node child = children[i];
            if ( child != null ) {
				if ( child instanceof ListOfNodes ) {
					Node[] childrenOfList = child.children();
					for (int j=0; j<childrenOfList.length; j++) {
						Node childOfList = childrenOfList[j];
			            if ( childOfList != null ) {
							if ( childOfList instanceof Formula || childOfList instanceof Term ) {
								nodesToSimplify(childOfList, nodes);	
								simplify = simplify 
								    || (childOfList instanceof Formula && ((Formula)childOfList).sm_simplified_formula() == null ) 
						            || (childOfList instanceof Term && ((Term)childOfList).sm_simplified_term() == null );
							}
						}
					}
				}
				else {
		            if ( child != null ) {
						if ( child instanceof Formula || child instanceof Term ) {
							nodesToSimplify(child, nodes);	
							simplify = simplify 
							    || (child instanceof Formula && ((Formula)child).sm_simplified_formula() == null ) 
					            || (child instanceof Term && ((Term)child).sm_simplified_term() == null );
						}
		            }
				}
           }
        }
		
		if ( simplify ) {
			if ( node instanceof Formula ) {
				((Formula)node).sm_simplified_formula(null);
			}
			else {
				((Term)node).sm_simplified_term(null);
			}
			nodes.add(node);
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

    static public void installOperations() 
    {
	DefaultNodeOperations DEFAULT_OPERATIONS                        = new DefaultNodeOperations();
	MetaReferenceNodeOperations META_REFERENCE_OPERATIONS           = new MetaReferenceNodeOperations();
	FormulaNodeOperations FORMULA_OPERATIONS                        = new FormulaNodeOperations();
	TermNodeOperations TERM_OPERATIONS                              = new TermNodeOperations();
	ActionExpressionNodeOperations ACTION_EXPRESSION_OPERATIONS     = new ActionExpressionNodeOperations();

	Node.installOperations(new Object[] {ContentNode.class,          DEFAULT_OPERATIONS,
					     ActionContentExpressionNode.class,          DEFAULT_OPERATIONS,
					     IdentifyingContentExpressionNode.class,     DEFAULT_OPERATIONS,
					     FormulaContentExpressionNode.class,         DEFAULT_OPERATIONS,
					     MetaFormulaReferenceNode.class,             DEFAULT_OPERATIONS,
					     MetaTermReferenceNode.class,                DEFAULT_OPERATIONS,
					     MetaVariableReferenceNode.class,            DEFAULT_OPERATIONS,
					     MetaSymbolReferenceNode.class,              DEFAULT_OPERATIONS,
					     MetaActionExpressionReferenceNode.class,    DEFAULT_OPERATIONS,
					     MetaContentExpressionReferenceNode.class,   DEFAULT_OPERATIONS,
					     BelieveNode.class,                          new BelieveNodeOperations(),
					     IntentionNode.class,                        new IntentionNodeOperations(),
					     UncertaintyNode.class,                      new UncertaintyNodeOperations(),
					     PersistentGoalNode.class,                   new PersistentGoalNodeOperations(),
					     FeasibleNode.class,                         new FeasibleNodeOperations(),
					     DoneNode.class,                             new DoneNodeOperations(),
					     PropositionSymbolNode.class,                FORMULA_OPERATIONS,
					     PredicateNode.class,                        FORMULA_OPERATIONS,
					     EqualsNode.class,                           new EqualsNodeOperations(),
					     ResultNode.class,                           FORMULA_OPERATIONS,
					     FalseNode.class,                            FORMULA_OPERATIONS,
					     TrueNode.class,                             new TrueNodeOperations(),
					     ForallNode.class,                           new ForallNodeOperations(),
					     ExistsNode.class,                           new ExistsNodeOperations(),
					     NotNode.class,                              new NotNodeOperations(),
					     AndNode.class,                              new AndNodeOperations(),
					     OrNode.class,                               new OrNodeOperations(),
					     EquivNode.class,                            new EquivNodeOperations(),
					     ImpliesNode.class,                          new ImpliesNodeOperations(),
					     ActionExpressionNode.class,                 ACTION_EXPRESSION_OPERATIONS,
					     AlternativeActionExpressionNode.class,      ACTION_EXPRESSION_OPERATIONS,
					     SequenceActionExpressionNode.class,         ACTION_EXPRESSION_OPERATIONS,
					     FunctionalTermNode.class,                   TERM_OPERATIONS,
					     FunctionalTermParamNode.class,              TERM_OPERATIONS,
					     StringConstantNode.class,                   TERM_OPERATIONS,
					     WordConstantNode.class,                     TERM_OPERATIONS,
					     ByteConstantNode.class,                     TERM_OPERATIONS,
					     DateTimeConstantNode.class,                 TERM_OPERATIONS,
					     RealConstantNode.class,                     TERM_OPERATIONS,
					     IntegerConstantNode.class,                  TERM_OPERATIONS,
					     SymbolNode.class,                           DEFAULT_OPERATIONS,
					     IotaNode.class,                             TERM_OPERATIONS,
					     AllNode.class,                              TERM_OPERATIONS,
					     AnyNode.class,                              TERM_OPERATIONS,
					     VariableNode.class,                         TERM_OPERATIONS,
	});
    }
}
