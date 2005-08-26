/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2004 France T�l�com

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

package jade.semantics.lang.sl.tools;

import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentExpression;
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
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfParameter;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Parameter;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.PropositionSymbolNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.ResultNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Symbol;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSequenceNode;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.grammar.VisitorBase;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;

import java.lang.reflect.Method;

/**
 * The SLPatternManip class provides a simple mechanism to check if 2 SL
 * expressions considered as patterns match. If true, it becomes possible to
 * retrieve the value of meta variables that has been unified during the match
 * checking.
 * 
 * @author Thierry Martinez - France T�l�com
 * @version $Date: 2004/11/30 17:00:00 $ $Revision: 1.0 $
 */
public class SLPatternManip {
	/**
	 * This exception is thrown when trying to get the value of a meta variable
	 * with the wrong type. For example, when trying to get a formula while the
	 * meta variable value is a term.
	 * 
	 * @see jade.semantics.lang.sl.tools.MatchResult#getFormula(String name)
	 * @see jade.semantics.lang.sl.tools.MatchResult#getTerm(String name)
	 * @see jade.semantics.lang.sl.tools.MatchResult#getVariable(String name)
	 * @see jade.semantics.lang.sl.tools.MatchResult#getSymbol(String name)
	 * @see jade.semantics.lang.sl.tools.MatchResult#getActionExpression(String name)
	 * @see jade.semantics.lang.sl.tools.MatchResult#getContentExpression(String name)
	 */
	public static class WrongTypeException extends Exception {
	}

	public static class LoopingInstantiationException extends Exception {
		public LoopingInstantiationException(Node meta, Node value) {
			super("trying to instanciate " + meta.toString() + ", with "
					+ value.toString());
		}
	}

	/**
	 * This method return a new expression corresponding to the given one in
	 * which all variables equals to <b><code>x</code></b> are replaced by a
	 * meta variable named <b><code>??X</code></b>.
	 * 
	 * @param expression
	 *            the expression to transform as a pattern.
	 * @param x
	 *            the variable to be replaced by a meta variable.
	 * @return the pattern node.
	 */
	public static Node toPattern(Node expression, Variable x) {
		Node result = expression.getClone();
		variable2MetaVariable(result, x);
		return result;
	}

	/**
	 * Assigned the value of the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to assign its value.
	 * @param value
	 *            the value to be assigned to this meta variable.
	 * @return the manipulator itself.
	 * @exception WrongTypeException
	 *                Thrown if the value of the variable is not f the good
	 *                type.
	 */
	public static void set(Node expression, String name, Node value)
			throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		if (expression.find(new Class[] {MetaContentExpressionReferenceNode.class,
										 MetaFormulaReferenceNode.class,
										 MetaSymbolReferenceNode.class,
										 MetaTermReferenceNode.class,
										 MetaVariableReferenceNode.class},
						     "lx_name", nameOf(name), metaReferences, true)) {
			for (int i = 0; i < metaReferences.size(); i++) {
				setMetaReferenceValue(metaReferences.get(i), value);
			}
		}
	}

	/**
	 * Return a tree representing the instanciated pattern, meaning all meta
	 * variables have been replaced by their value.
	 * 
	 * @param expression
	 *            the expression to instanciate.
	 * @return the instanciated expression.
	 */
	public static Node instantiate(Node expression) {
		try {
			Node result = expression.getClone();
			substituteMetaReferences(result);
			return result;
		} catch (LoopingInstantiationException lie) {
//			System.err.println("Exception occurs when trying to instanciate "+ expression);
			lie.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @param varname
	 * @param value
	 * @return
	 * @throws WrongTypeException
	 */
	public static Node instantiate(Node expression, String varname, Node value)
			throws WrongTypeException {
		try {
			Node result = expression.getClone();
			set(result, varname, value);
			substituteMetaReferences(result);
			return result;
		} catch (LoopingInstantiationException lie) {
			System.err.println("Exception occurs when trying to instanciate "+ expression);
			lie.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @param varname1
	 * @param value1
	 * @param varname2
	 * @param value2
	 * @return
	 * @throws WrongTypeException
	 */
	public static Node instantiate(Node expression, String varname1,
			Node value1, String varname2, Node value2)
			throws WrongTypeException {
		try {
			Node result = expression.getClone();
			set(result, varname1, value1);
			set(result, varname2, value2);
			substituteMetaReferences(result);
			return result;
		} catch (LoopingInstantiationException lie) {
			System.err.println("Exception occurs when trying to instanciate "+ expression);
			lie.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @param varname1
	 * @param value1
	 * @param varname2
	 * @param value2
	 * @param varname3
	 * @param value3
	 * @return
	 * @throws WrongTypeException
	 */
	public static Node instantiate(Node expression, String varname1,
			Node value1, String varname2, Node value2, String varname3,
			Node value3) throws WrongTypeException {
		try {
			Node result = expression.getClone();
			set(result, varname1, value1);
			set(result, varname2, value2);
			set(result, varname3, value3);
			substituteMetaReferences(result);
			return result;
		} catch (LoopingInstantiationException lie) {
			System.err.println("Exception occurs when trying to instanciate "+ expression);
			lie.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @param varname1
	 * @param value1
	 * @param varname2
	 * @param value2
	 * @param varname3
	 * @param value3
	 * @param varname4
	 * @param value4
	 * @return
	 * @throws WrongTypeException
	 */
	public static Node instantiate(Node expression, String varname1,
			Node value1, String varname2, Node value2, String varname3,
			Node value3, String varname4, Node value4) throws WrongTypeException {
		try {
			Node result = expression.getClone();
			set(result, varname1, value1);
			set(result, varname2, value2);
			set(result, varname3, value3);
			set(result, varname4, value4);
			substituteMetaReferences(result);
			return result;
		} catch (LoopingInstantiationException lie) {
			System.err.println("Exception occurs when trying to instanciate "+ expression);
			lie.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @return
	 */
	public static Formula fromFormula(String expression) {
		try {
			return SLParser.getParser().parseFormula(expression, true);
		} catch (ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @return
	 */
	public static Term fromTerm(String expression) {
		try {
			return SLParser.getParser().parseTerm(expression, true);
		} catch (ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}

	/**
	 * @param expression
	 * @return
	 */
	public static Content fromContent(String expression) {
		try {
			return SLParser.getParser().parseContent(expression, true);
		} catch (ParseException pe) {
			pe.printStackTrace();
			return null;
		}
	}

	/**
	 * Return a new pattern matcher
	 * 
	 * @return a new pattern matcher
	 */
	public static Matcher newMatcher() {
		return new Matcher();
	}

	/**
	 * Check if the expressions 1 et 2 match. If true, all meta variables that
	 * has been unified can be accessed using one of the <b><code>getFormula, getTerm, ... </code></b>
	 * methods of the matching result. This method is equivalent to <b><code>SLPatternManip.newMatcher().match(expression1, expression2);</code></b>
	 * 
	 * @param expression1
	 *            the first expression.
	 * @param expression2
	 *            the second expression.
	 * @return A matching result if the matching complete, null otherwise.
	 * @see jade.semantics.lang.sl.tools.MatchResult
	 */
	public static MatchResult match(Node expression1, Node expression2) {
		return newMatcher().match(expression1, expression2);
	}

	/**
	 * @param metaReference
	 * @return
	 */
	public static String getMetaReferenceName(Node metaReference)
	{
		String result = null;
		try {
			Method smValueMethod = metaReference.getClass().getMethod(
					"lx_name");
			if (smValueMethod != null) {
				result = (String) smValueMethod.invoke(metaReference);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param metaReference
	 * @return
	 */
	public static Node getMetaReferenceValue(Node metaReference)
	{
		Node result = null;
		try {
			Method smValueMethod = metaReference.getClass().getMethod(
					"sm_value");
			if (smValueMethod != null) {
				result = (Node) smValueMethod.invoke(metaReference);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param metaReference
	 * @param value
	 * @throws WrongTypeException
	 */
	public static void setMetaReferenceValue(Node metaReference, Node value)
			throws WrongTypeException
	{
		try {
			if (metaReference instanceof MetaFormulaReferenceNode) {
				MetaFormulaReferenceNode mRef = (MetaFormulaReferenceNode)metaReference;
				if (value == null || value instanceof Formula) {
					mRef.sm_value((Formula) value);
				} else {
					mRef.sm_value(((FormulaContentExpressionNode) value).as_formula());
				}
			} else if (metaReference instanceof MetaTermReferenceNode) {
				((MetaTermReferenceNode) metaReference).sm_value((Term) value);
			} else if (metaReference instanceof MetaVariableReferenceNode) {
				((MetaVariableReferenceNode) metaReference).sm_value((Variable) value);
			} else if (metaReference instanceof MetaSymbolReferenceNode) {
				((MetaSymbolReferenceNode) metaReference).sm_value((Symbol) value);
			} else if (metaReference instanceof MetaContentExpressionReferenceNode) {
				MetaContentExpressionReferenceNode mRef = (MetaContentExpressionReferenceNode)metaReference;
				if (value == null || value instanceof ContentExpression) {
					mRef.sm_value((ContentExpression) value);
				} else if (value instanceof Formula) {
					mRef.sm_value(new FormulaContentExpressionNode((Formula) value));
				} else if (value instanceof ActionExpression) {
					mRef.sm_value(new ActionContentExpressionNode((ActionExpression) value));
				} else {
					mRef.sm_value(new IdentifyingContentExpressionNode((IdentifyingExpression) value));
				}
			} else {
				throw new WrongTypeException();
			}
		} catch (ClassCastException cce) {
			throw new WrongTypeException();
		}
	}

	/**
	 * @param expression
	 */
	public static void clearMetaReferences(Node expression)
	// -----------------------------------------------
	{
		ListOfNodes metaReferences = new ListOfNodes();
		expression.childrenOfKind(new Class[] {MetaContentExpressionReferenceNode.class,
											   MetaFormulaReferenceNode.class, 
											   MetaSymbolReferenceNode.class, 
											   MetaTermReferenceNode.class, 
											   MetaVariableReferenceNode.class}, 
								  metaReferences);
		for (int i = 0; i < metaReferences.size(); i++) {
			try {
				setMetaReferenceValue(metaReferences.get(i), null);
			} catch (WrongTypeException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param node
	 * @throws LoopingInstantiationException
	 */
	public static void substituteMetaReferences(Node node)
			throws LoopingInstantiationException
	{
		Node[] children = node.children();

		for (int i = 0; i < children.length; i++) {
			Node childNode = children[i];
			Node valueNode = null;

			while ((childNode instanceof MetaFormulaReferenceNode
					|| childNode instanceof MetaTermReferenceNode
					|| childNode instanceof MetaVariableReferenceNode
					|| childNode instanceof MetaSymbolReferenceNode
					|| childNode instanceof MetaContentExpressionReferenceNode)
					&& ((valueNode = getMetaReferenceValue(childNode)) != null)) {
				if (childNode == valueNode) {
					throw new LoopingInstantiationException(childNode,
							valueNode);
				}
				childNode = valueNode;
			}
			// childNode is not a meta-reference or its value is null
			substituteMetaReferences(childNode);

			if (childNode != children[i]) {
				node.replace(i, childNode);
			}
		}
	}

	/**
	 * @param node
	 */
	public static void removeOptionalParameter(Node node)
	{
		ListOfNodes terms = new ListOfNodes();
		if ( node.childrenOfKind(FunctionalTermParamNode.class, terms) ) {
			for (int i=0; i<terms.size(); i++) {
				FunctionalTermParamNode term = (FunctionalTermParamNode)terms.get(i);
				for (int j=term.as_parameters().size()-1; j>=0; j--) {
					Parameter p = term.as_parameters().element(j);
					if ( p.lx_optional() &&
						((p.as_value() instanceof MetaTermReferenceNode && ((MetaTermReferenceNode)p.as_value()).sm_value() == null)
					  || (p.as_value() instanceof MetaVariableReferenceNode && ((MetaTermReferenceNode)p.as_value()).sm_value() == null)) ) {
						term.as_parameters().remove(p);
					}
				}
				
			}
		}
	}
	
	/**
	 * This class implement a SL pattern matcher.
	 */
	public static class Matcher extends VisitorBase {
		Node _expression1;

		Node _expression2;

		boolean _match;

		MatchResult _metaReferences;

		/**
		 * Check if the expressions 1 et 2 match. If true, all meta variables
		 * that has been unified can be accessed using one of the <b><code>getFormula, getTerm, ... </code></b>
		 * methods of the matching result.
		 * 
		 * @param expression1
		 *            the first expression.
		 * @param expression2
		 *            the second expression.
		 * @return A matching result if the matching complete, null otherwise.
		 */
		public MatchResult match(Node expression1, Node expression2) {
			MatchResult result = new MatchResult();
			if (match(expression1, expression2, result)) {
				// Because, the complete instanciation of result also
				// depends on the two expressions, these expressions
				// must be consolidated before the instanciation.
				completeExpsAssignments();
				result.instantiate();
				removeExpsAssignments();
				return result;
			} else {
				return null;
			}
		}

		// ===============================================
		// Private implementation
		// ===============================================
		// -----------------------------------------------
		private void completeExpsAssignments()
		// This method consolidate both expressions
		// by assigning all meta vrariables according to
		// the matching result.
		// -----------------------------------------------
		{
			for (int i = 0; i < _metaReferences.size(); i++) {
				Node var = _metaReferences.get(i);
				Node value = getMetaReferenceValue(var);
				String name = getMetaReferenceName(var);
				ListOfNodes metaReferences = new ListOfNodes();
				if (_expression1.find(var.getClass(), "lx_name", name, metaReferences, true)
				 || _expression2.find(var.getClass(), "lx_name", name, metaReferences, true)) {
					for (int j = 0; j < metaReferences.size(); j++) {
						try {
							setMetaReferenceValue(metaReferences.get(j), value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void removeExpsAssignments()
		// This method remove all assigments in expressions.
		// -----------------------------------------------
		{
			for (int i = 0; i < _metaReferences.size(); i++) {
				Node var = _metaReferences.get(i);
				try {
					set(_expression1, getMetaReferenceName(var), null);
					set(_expression2, getMetaReferenceName(var), null);
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
			}
		}

		// -----------------------------------------------
		private Node getMetaReference(Node metaReference)
		// -----------------------------------------------
		{
			ListOfNodes foundRef = new ListOfNodes();
			if (_metaReferences.find(metaReference.getClass(), "lx_name", metaReference
					.toString(), foundRef, false)) {
				return foundRef.get(0);
			} else {
				return null;
			}
		}

		// -----------------------------------------------
		private void doPatternMatchingOnChildren(Node expression1,
				Node expression2)
		// -----------------------------------------------
		{
			if (expression1.getClass() == expression2.getClass()) {
				Node[] nodes1 = expression1.children();
				Node[] nodes2 = expression2.children();
				_match = (nodes1.length == nodes2.length);
				for (int i = 0; i < nodes1.length && _match; i++) {
					_match = _match && matchExpressions(nodes1[i], nodes2[i]);
				}
			} else {
				_match = false;
			}
		}

		// -----------------------------------------------
		boolean matchExpressions(Node expression1, Node expression2)
		// -----------------------------------------------
		{
			return (newMatcher().match(expression1, expression2,
					_metaReferences) || newMatcher().match(expression2,
					expression1, _metaReferences));
		}

		// -----------------------------------------------
		private boolean match(Node expression1, Node expression2,
				MatchResult metaReferences)
		// This method is used inside the recursive matching
		// process.
		// -----------------------------------------------
		{
			_match = false;
			_metaReferences = metaReferences;
			_expression1 = expression1;
			_expression2 = expression2;
			_expression1.accept(this);
			return _match;
		}

		// -----------------------------------------------
		private void doPatternMatchingOnMetaReference(Node metaRef, Node exp)
		// -----------------------------------------------
		{
			Node otherRef = getMetaReference(metaRef);
			if (otherRef != null) {
				Node otherValue = getMetaReferenceValue(otherRef);
				_match = matchExpressions(otherValue, exp);
				if (_match) {
					try {
						setMetaReferenceValue(metaRef, otherValue);
					} catch (WrongTypeException e) {
						e.printStackTrace();
					}
				}
			} else {
				_match = true;
				_metaReferences.add(metaRef);
				try {
					setMetaReferenceValue(metaRef, exp);
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
			}
		}

		// ===============================================
		// Visitor implementation
		// ===============================================
		public void visitMetaFormulaReferenceNode(MetaFormulaReferenceNode node) {
			if (_expression2 instanceof Formula) {
				doPatternMatchingOnMetaReference(node, _expression2);
			} else {
				_match = false;
			}
		}

		public void visitMetaSymbolReferenceNode(MetaSymbolReferenceNode node) {
			if (_expression2 instanceof Symbol) {
				doPatternMatchingOnMetaReference(node, _expression2);
			} else {
				_match = false;
			}
		}

		public void visitMetaVariableReferenceNode(
				MetaVariableReferenceNode node) {
			if (_expression2 instanceof Variable) {
				doPatternMatchingOnMetaReference(node, _expression2);
			} else {
				_match = false;
			}
		}

		public void visitMetaTermReferenceNode(MetaTermReferenceNode node) {
			if (_expression2 instanceof Term) {
				doPatternMatchingOnMetaReference(node, _expression2);
			} else {
				_match = false;
			}
		}

		public void visitMetaContentExpressionReferenceNode(
				MetaContentExpressionReferenceNode node) {
			if (_expression2 instanceof ContentExpression) {
				doPatternMatchingOnMetaReference(node, _expression2);
			} else {
				_match = false;
			}
		}

		public void visitVariableNode(VariableNode node) {
			_match = ((_expression2 instanceof VariableNode) && ((VariableNode) _expression2)
					.lx_name().equals(node.lx_name()));
		}

		public void visitIntegerConstantNode(IntegerConstantNode node) {
			_match = ((_expression2 instanceof IntegerConstantNode) && ((IntegerConstantNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitRealConstantNode(RealConstantNode node) {
			_match = ((_expression2 instanceof RealConstantNode) && ((RealConstantNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitStringConstantNode(StringConstantNode node) {
			_match = ((_expression2 instanceof StringConstantNode) && ((StringConstantNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitWordConstantNode(WordConstantNode node) {
			_match = ((_expression2 instanceof WordConstantNode) && ((WordConstantNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitByteConstantNode(ByteConstantNode node) {
			_match = ((_expression2 instanceof ByteConstantNode) && ((ByteConstantNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitDateTimeConstantNode(DateTimeConstantNode node) {
			_match = ((_expression2 instanceof DateTimeConstantNode) && ((DateTimeConstantNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitSymbolNode(SymbolNode node) {
			_match = ((_expression2 instanceof SymbolNode) && ((SymbolNode) _expression2)
					.lx_value().equals(node.lx_value()));
		}

		public void visitParameterNode(ParameterNode node) {
			if (_expression2 instanceof ParameterNode) {
				_match = (node.lx_name().equals(
						((ParameterNode) _expression2).lx_name()) && matchExpressions(
						node.as_value(), ((ParameterNode) _expression2).as_value()));
			} else {
				_match = false;
			}
		}

		public void visitTrueNode(TrueNode node) {
			_match = (_expression2 instanceof TrueNode);
		}

		public void visitFalseNode(FalseNode node) {
			_match = (_expression2 instanceof FalseNode);
		}

		public void visitListOfContentExpression(ListOfContentExpression node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitListOfTerm(ListOfTerm node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitListOfParameter(ListOfParameter node) {
			if (node.getClass() == _expression2.getClass()) {
				ListOfParameter node2 = (ListOfParameter)_expression2;
				int i = 0, j = 0;
				_match = true;
				
				while (i < node.size() && j < node2.size()) {
					_match = matchExpressions(node.element(i), node2.element(j));
					if ( _match ) {i++; j++;}
					else if (((ParameterNode)node.element(i)).lx_optional()) {i++;}
					else if (((ParameterNode)node2.element(j)).lx_optional()) {j++;}
					else {_match = false; break;}
				}
				// i == node.size() || j == node2.size()
				
				while ( _match && i < node.size() ) {
					_match = _match && ((ParameterNode)node.element(i++)).lx_optional();
				}
				while ( _match && j < node2.size() ) {
					_match = _match && ((ParameterNode)node2.element(j++)).lx_optional();
				}
			}
		}

		public void visitContentNode(ContentNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitActionContentExpressionNode(
				ActionContentExpressionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitFormulaContentExpressionNode(
				FormulaContentExpressionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitIdentifyingContentExpressionNode(
				IdentifyingContentExpressionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitActionExpressionNode(ActionExpressionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitAlternativeActionExpressionNode(
				AlternativeActionExpressionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitSequenceActionExpressionNode(
				SequenceActionExpressionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitPropositionSymbolNode(PropositionSymbolNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitResultNode(ResultNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitPredicateNode(PredicateNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitEqualsNode(EqualsNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitDoneNode(DoneNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitFeasibleNode(FeasibleNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitNotNode(NotNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitAndNode(AndNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitOrNode(OrNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitImpliesNode(ImpliesNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitEquivNode(EquivNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitForallNode(ForallNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitExistsNode(ExistsNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitBelieveNode(BelieveNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitUncertaintyNode(UncertaintyNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitIntentionNode(IntentionNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitPersistentGoalNode(PersistentGoalNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitTermSetNode(TermSetNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitTermSequenceNode(TermSequenceNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitFunctionalTermNode(FunctionalTermNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitFunctionalTermParamNode(FunctionalTermParamNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}
		
		public void visitAnyNode(AnyNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitIotaNode(IotaNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}

		public void visitAllNode(AllNode node) {
			doPatternMatchingOnChildren(node, _expression2);
		}
	}

	// ===============================================
	// Protected
	// ===============================================
	// -----------------------------------------------
	private static String nameOf(String name)
    // -----------------------------------------------
	{
		return name.startsWith("??") ?
				name.substring(2) :
					name;
	}
	
	// -----------------------------------------------
	private static void variable2MetaVariable(Node node, Variable x)
	// -----------------------------------------------
	{
		Node[] children = node.children();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof VariableNode
					&& ((VariableNode) children[i]).lx_name().equals(
							x.lx_name())) {
				node.replace(i, new MetaTermReferenceNode("X"));
			} else {
				variable2MetaVariable(children[i], x);
			}
		}
	}
}