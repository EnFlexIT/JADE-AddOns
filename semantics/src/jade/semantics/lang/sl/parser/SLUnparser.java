/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.semantics.lang.sl.parser;

import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.CountAsNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.EquivNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.PropositionSymbolNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.RelativeTimeConstantNode;
import jade.semantics.lang.sl.grammar.ResultNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.TermSequenceNode;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.grammar.VisitorBase;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.SL;

public class SLUnparser extends VisitorBase
{
    java.io.PrintWriter _out;
    boolean _trueSL;
	String _invalidSLExpr;

	public static class InvalidSLExpressionException extends Exception 
	{	
		private static final long serialVersionUID = 1L;
		
		public InvalidSLExpressionException(String reason) {
			super(reason);
		}
	}

	static final String WHITE_CHAR = " ";
    static final String NULL_CHAR = "";

    String _nextChar = NULL_CHAR;

    void _outputLiteralExp(Object exp)
    {
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
	
	// -----------------------------------------------
	void _outputMetaReference(Node value, String name)
	// This method outputs a meta reference in respect
	// to the output mode (i.e., _trueSL or not).
	// If _trueSL then it outputs the value or set _invalidSLExpr if no value is assigned to the meta reference.
	// If !_trueSL then is outputs the value or the name of the meta reference if no value is assigned to.
	// -----------------------------------------------
	{
		try {
			if ( value != null ) {
				value.accept(this);
			}
			else if ( !_trueSL ) {
				_outputLiteralExp("??"+name);
			}
			else {
				_invalidSLExpr = "??"+name;
			}
		}
		catch (StackOverflowError e) {
			System.err.println("!!!!!!! Looping while trying to output "+name);
			System.exit(1);
		}
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
		_trueSL = false;
        node.accept(this);
		flush();
    }

    public void unparseTrueSL(Node node)
		throws InvalidSLExpressionException
    {
		_trueSL = true;
		_invalidSLExpr = null;
        node.accept(this);
		flush();
		if ( _invalidSLExpr != null ) {
			throw new InvalidSLExpressionException(_invalidSLExpr);
		}
    }
    
    public void visitFactNode(FactNode node) {
    	_outputTaggedExp(node, "fact");
    }

//    public void visitFactNode(FactNode node) {
//    	_out.print(_nextChar+"(");
//    	if ( !_trueSL ) _out.print("fact ");
//		_nextChar = NULL_CHAR;
//        node.childrenAccept(this);
//		_out.print(")");
//    }
    
	public void visitContentNode(ContentNode node) {
    	_out.print(_nextChar+"(");
    	if ( !_trueSL ) _out.print("content ");
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
		_outputMetaReference(node.sm_value(), node.lx_name());
	}

    public void visitMetaTermReferenceNode(MetaTermReferenceNode node)
    {
		_outputMetaReference(node.sm_value(), node.lx_name());
	}

    public void visitMetaSymbolReferenceNode(MetaSymbolReferenceNode node) 
    {
		_outputMetaReference(node.sm_value(), node.lx_name());
    }

    public void visitMetaVariableReferenceNode(MetaVariableReferenceNode node) 
    {
		_outputMetaReference(node.sm_value(), node.lx_name());
    }

    public void visitMetaContentExpressionReferenceNode(MetaContentExpressionReferenceNode node) 
    {
		_outputMetaReference(node.sm_value(), node.lx_name());
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
    
    public void visitRelativeTimeConstantNode(RelativeTimeConstantNode node) {
    	_outputLiteralExp(node.lx_value());
    }

    public void visitRealConstantNode(RealConstantNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitWordConstantNode(WordConstantNode node) {
    	String value = node.lx_value();
    	if ( value.indexOf(" ") != -1 ) {
    		_outputLiteralExp("'"+value+"'");
    	}
    	else {
    		_outputLiteralExp(value);
    	}
    }

    public void visitByteConstantNode(ByteConstantNode node) {
		char[] chars = Base64.encode(node.lx_value());
		_outputLiteralExp("#"+chars.length+"\""+new String(chars));
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
	_outputLiteralExp(SL.DATE_FORMAT.format(node.lx_value()));
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
    	Node value = node.as_value();
		if( (value instanceof MetaTermReferenceNode || 
			 value instanceof MetaVariableReferenceNode) &&
			 value.getAttribute("sm_value") == null ) {
			// Value is a meta variable and is not instantiated.
			if ( _trueSL ) {
				if ( node.lx_optional().booleanValue() ) {
					// Nothing to print.
				}
				else {
					_invalidSLExpr = node.toString();
				}
			}
			else {
				if ( node.lx_optional().booleanValue() ) {
					_out.print(_nextChar);
					_out.print("(::?");
					_nextChar = WHITE_CHAR;
				}
				_outputLiteralExp(":"+node.lx_name());
				node.childrenAccept(this);
				if ( node.lx_optional().booleanValue() ) {
					_out.print(")");
					_nextChar = WHITE_CHAR;
				}
			}
		}
		else {
			// No meta variable or an instantiated meta variable .
			if ( node.lx_optional().booleanValue() && !_trueSL) {
				_out.print(_nextChar);
				_out.print("(::?");
				_nextChar = WHITE_CHAR;
			}
			_outputLiteralExp(":"+node.lx_name());
			if ( _trueSL && node.as_value() instanceof Content ) {
				SL.string(((Content)node.as_value()).toSLString()).accept(this);
			}
			else {
				node.childrenAccept(this);				
				if ( node.lx_optional().booleanValue() && !_trueSL) {
					_out.print(")");
					_nextChar = WHITE_CHAR;
				}
			}
		}
	}

    public void visitSymbolNode(SymbolNode node) {
	_outputLiteralExp(node.lx_value());
    }

    public void visitVariableNode(VariableNode node) {
	_outputLiteralExp("?"+node.lx_name());
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

    public void visitSomeNode(SomeNode node) 
    {
	_outputTaggedExp(node, "some");
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

    public void visitCountAsNode(CountAsNode node) 
    {
	_outputTaggedExp(node, "countas");
    }
    public void visitInstitutionalFactNode(InstitutionalFactNode node) 
    {
	_outputTaggedExp(node, "D");
    }
    public void visitObligationNode(ObligationNode node) 
    {
	_outputTaggedExp(node, "O");
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
