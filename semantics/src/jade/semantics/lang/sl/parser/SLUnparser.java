package jade.semantics.lang.sl.parser;

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
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.MetaActionExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.PropositionSymbolNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.ResultNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.TermSequenceNode;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.grammar.VisitorBase;
import jade.semantics.lang.sl.grammar.WordConstantNode;

public class SLUnparser extends VisitorBase
{
    java.io.PrintWriter _out;

    static final String WHITE_CHAR = " ";
    static final String NULL_CHAR = "";

    String _nextChar = NULL_CHAR;

    boolean _metaExpanded = false;

    void _outputLiteralExp(Object exp)
    {
	// THIS SHOULD BE IMPROVED TO DISTINGUISH DIFFERENT KINDS OF LITERAL !!!!!!!  
	_out.print(_nextChar);
	_out.print(exp);
	_nextChar = WHITE_CHAR;
    }

    void _outputNoTaggedExp(Node node)
    {
	_out.print(_nextChar);
    	_out.print("(");
	_nextChar = NULL_CHAR;
        node.childrenAccept(this);
	_out.print(")");
	_nextChar = WHITE_CHAR;
    }

    void _outputTaggedExp(Node node, String tag)
    {
	_out.print(_nextChar);
    	_out.print("("+tag);
	_nextChar = WHITE_CHAR;
        node.childrenAccept(this);
	_out.print(")");
	_nextChar = WHITE_CHAR;
    }

    public void flush()
    {
	_out.flush();
    }

    public SLUnparser(java.io.Writer out)
    {
	_out = new java.io.PrintWriter(out, true);
    }

    public SLUnparser(java.io.OutputStream out)
    {
	_out = new java.io.PrintWriter(out, true);
    }

    public void unparse(Node node)
    {
	unparse(node, false);
    }

    public void unparse(Node node, boolean meta_expanded)
    {
	_metaExpanded = meta_expanded;
        node.accept(this);
	flush();
    }

    public void visitContentNode(ContentNode node) {
    	_out.print("(");
	_nextChar = NULL_CHAR;
        node.childrenAccept(this);
	_out.print(")");
    }

    public void visitActionContentExpressionNode(ActionContentExpressionNode node) {
        node.childrenAccept(this);
    }

    public void visitFormulaContentExpressionNode(FormulaContentExpressionNode node) {
	    node.childrenAccept(this);
   }

    public void visitIdentifyingContentExpressionNode(IdentifyingContentExpressionNode node) 
    {
        node.childrenAccept(this);
    }

    public void visitActionExpressionNode(ActionExpressionNode node) 
    {
	_outputTaggedExp(node, "action");
    }

    public void visitAlternativeActionExpressionNode(AlternativeActionExpressionNode node) 
    {
	_outputTaggedExp(node, "|");
    }

    public void visitSequenceActionExpressionNode(SequenceActionExpressionNode node) 
    {
	_outputTaggedExp(node, ";");
    }

    public void visitMetaFormulaReferenceNode(MetaFormulaReferenceNode node)
    {
	if ( _metaExpanded ) {
	    node.sm_value().accept(this);
	}
	else {
	    _outputLiteralExp(node.lx_name());
	}
    }

    public void visitMetaTermReferenceNode(MetaTermReferenceNode node)
    {
	if ( _metaExpanded ) {
	    node.sm_value().accept(this);
	}
	else {
	    _outputLiteralExp(node.lx_name());
	}
    }

    public void visitMetaSymbolReferenceNode(MetaSymbolReferenceNode node) 
    {
	if ( _metaExpanded ) {
	    node.sm_value().accept(this);
	}
	else {
	    _outputLiteralExp(node.lx_name());
	}
    }

    public void visitMetaVariableReferenceNode(MetaVariableReferenceNode node) 
    {
	if ( _metaExpanded ) {
	    node.sm_value().accept(this);
	}
	else {
	    _outputLiteralExp(node.lx_name());
	}
    }

    public void visitMetaActionExpressionReferenceNode(MetaActionExpressionReferenceNode node) 
    {
	if ( _metaExpanded ) {
	    node.sm_value().accept(this);
	}
	else {
	    _outputLiteralExp(node.lx_name());
	}
    }

    public void visitMetaContentExpressionReferenceNode(MetaContentExpressionReferenceNode node) 
    {
	if ( _metaExpanded ) {
	    node.sm_value().accept(this);
	}
	else {
	    _outputLiteralExp(node.lx_name());
	}
    }

    public void visitDoneNode(DoneNode node) {
	_outputTaggedExp(node, "done");
    }

    public void visitFeasibleNode(FeasibleNode node) {
	_outputTaggedExp(node, "feasible");
    }

    public void visitPropositionSymbolNode(PropositionSymbolNode node) {
        node.childrenAccept(this);
    }

    public void visitResultNode(ResultNode node) {
	_outputTaggedExp(node, "result");
    }

    public void visitPredicateNode(PredicateNode node) {
	_outputNoTaggedExp(node);
    }

    public void visitTrueNode(TrueNode node) {
	_outputLiteralExp("true");
    }

    public void visitFalseNode(FalseNode node) {
	_outputLiteralExp("false");
    }

    public void visitIntegerConstantNode(IntegerConstantNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitRealConstantNode(RealConstantNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitWordConstantNode(WordConstantNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitByteConstantNode(ByteConstantNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitStringConstantNode(StringConstantNode node) {
	String value = node.lx_value();
	String value2 = new String();
	for (int i=0; i<value.length(); i++) {
	    if ( value.charAt(i) == '\\' || value.charAt(i) == '\"' ) {
		value2 += "\\";
	    }
	    value2 += value.charAt(i);
	}
	_outputLiteralExp("\""+value2+"\"");
    }

    public void visitDateTimeConstantNode(DateTimeConstantNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitTermSetNode(TermSetNode node) {
	_outputTaggedExp(node, "set");
    }

    public void visitTermSequenceNode(TermSequenceNode node) {
	_outputTaggedExp(node, "sequence");
    }

    public void visitFunctionalTermNode(FunctionalTermNode node) {
	_outputNoTaggedExp(node);
    }

    public void visitFunctionalTermParamNode(FunctionalTermParamNode node) {
	_outputNoTaggedExp(node);
    }

    public void visitParameterNode(ParameterNode node) {
	_outputLiteralExp(node.lx_name());
        node.childrenAccept(this);
    }

    public void visitSymbolNode(SymbolNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitVariableNode(VariableNode node) {
	_outputLiteralExp(node.lx_name());
    }

    public void visitNotNode(NotNode node) 
    {
	_outputTaggedExp(node, "not");
    }

    public void visitAndNode(AndNode node) 
    {
	_outputTaggedExp(node, "and");
    }

    public void visitOrNode(OrNode node) 
    {
	_outputTaggedExp(node, "or");
    }

    public void visitImpliesNode(ImpliesNode node) 
    {
	_outputTaggedExp(node, "implies");
    }

    public void visitEquivNode(EquivNode node) 
    {
	_outputTaggedExp(node, "equiv");
    }

    public void visitAnyNode(AnyNode node) 
    {
	_outputTaggedExp(node, "any");
    }

    public void visitIotaNode(IotaNode node) 
    {
	_outputTaggedExp(node, "iota");
    }

    public void visitAllNode(AllNode node) 
    {
	_outputTaggedExp(node, "all");
    }

    public void visitEqualsNode(EqualsNode node) 
    {
	_outputTaggedExp(node, "=");
    }

    public void visitForallNode(ForallNode node) 
    {
	_outputTaggedExp(node, "forall");
    }

    public void visitExistsNode(ExistsNode node) 
    {
	_outputTaggedExp(node, "exists");
    }

    public void visitBelieveNode(BelieveNode node) 
    {
	_outputTaggedExp(node, "B");
    }

    public void visitUncertaintyNode(UncertaintyNode node) 
    {
	_outputTaggedExp(node, "U");
    }

    public void visitIntentionNode(IntentionNode node) 
    {
	_outputTaggedExp(node, "I");
    }

    public void visitPersistentGoalNode(PersistentGoalNode node) 
    {
	_outputTaggedExp(node, "PG");
    }
}
