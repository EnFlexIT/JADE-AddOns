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

package jade.semantics.lang.sl.tools;

import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Parameter;
import jade.semantics.lang.sl.grammar.Symbol;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.grammar.operations.SLSyntax;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;

/**
 * The SL class provides a simple mechanism to check if 2 SL
 * expressions considered as patterns match. If true, it becomes possible to
 * retrieve the value of meta variables that has been unified during the match
 * checking.
 * 
 * @author Thierry Martinez - France Télécom
 * @version $Date: 2004/11/30 17:00:00 $ $Revision: 1.0 $
 */
public class SL {
	
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
        if ( expression instanceof Variable )  {	
            return new MetaTermReferenceNode("X");
        }
        else {
            Node result = expression.getClone();
            variable2MetaVariable(result, x, "X");
            return result;
        }
    }
    
    /**
     * This method return a new expression corresponding to the given one in
     * which all variables equals to <b><code>x</code></b> are replaced by a
     * meta variable named <b><code>??\<metaname\></code></b>.
     * 
     * @param expression
     *            the expression to transform as a pattern.
     * @param x
     *            the variable to be replaced by a meta variable.
     * @param metaname
     *            the name of the introduced meta variable.
     * @return the pattern node.
     */
    public static Node toPattern(Node expression, Variable x, String metaname) {
        if ( expression instanceof Variable ) {
            return new MetaTermReferenceNode(metaname);
        }
        else {
            Node result = expression.getClone();
            variable2MetaVariable(result, x, metaname);
            return result;
        }
    }
    
    /**
     * This method return a new expression corresponding to the given one in
     * which all variables of the given list  are replaced by a
     * meta variable named <b><code>?? + given prefix + \<metaname\></code></b>.<br>
     * The metaname is the same as the variable name.
     * @param expression
     *            the expression to transform as a pattern.
     * @param l
     *            the list of variables to be replaced by meta variables.
     * @param prefix
     *            the prefix of the name of the introduced meta variable.
     * @return the pattern node.
     */
    public static Node toPattern(Node expression, ListOfNodes l, String prefix) {
        if ( expression instanceof Variable ) {
            if (prefix != null) {
                return new MetaTermReferenceNode(prefix + ((Variable)expression).lx_name());
            } else {
                return new MetaTermReferenceNode(((Variable)expression).lx_name());
            }
        }
        else {
            Node result = expression.getClone(); 
            for (int i = 0; i < l.size(); i++) {
                //node = toPattern(node, (VariableNode)l.get(i), ((VariableNode)l.get(i)).lx_name());
                if (prefix != null) {
                    variable2MetaVariable(result, (VariableNode)l.get(i), prefix + ((VariableNode)l.get(i)).lx_name());    
                } else {
                    variable2MetaVariable(result, (VariableNode)l.get(i), ((VariableNode)l.get(i)).lx_name());
                }
            }
            return result;
        }
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
     * @param arguments an array containing alternatively the var name and the value to be assigned.
     *                  var names are Strings while values are of type Node.
     * @return a node that is the result of the instantiation
     * @throws WrongTypeException
     */
    public static Node instantiate(Node expression, Object[] varNamesAndValues) throws WrongTypeException {
        try {
            Node result = expression.getClone();
			for (int i = 0; i<varNamesAndValues.length; i+=2) {
				set(result, (String)varNamesAndValues[i], (Node)varNamesAndValues[i+1]);
			}
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
     * Check if the expressions 1 et 2 match. If true, all meta variables that
     * has been unified can be accessed using one of the <b><code>getFormula, getTerm, ... </code></b>
     * methods of the matching result. This method is equivalent to <b><code>SL.newMatcher().match(expression1, expression2);</code></b>
     * 
     * @param expression1
     *            the first expression.
     * @param expression2
     *            the second expression.
     * @return A matching result if the matching complete, null otherwise.
     * @see jade.semantics.lang.sl.tools.MatchResult
     */
    public static MatchResult match(Node expression1, Node expression2) {
        return new SLMatcher().match(expression1, expression2);
    }
    
    /**
     * @param metaReference
     * @return
     */
    public static String getMetaReferenceName(Node metaReference)
    {
        return (String) metaReference.getAttribute("lx_name");
    }
    
    /**
     * @param metaReference
     * @return
     */
    public static Node getMetaReferenceValue(Node metaReference)
    {
        return (Node)metaReference.getAttribute("sm_value");
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
                    if ( p.lx_optional().booleanValue() &&
                            ((p.as_value() instanceof MetaTermReferenceNode && ((MetaTermReferenceNode)p.as_value()).sm_value() == null)
                                    || (p.as_value() instanceof MetaVariableReferenceNode && ((MetaVariableReferenceNode)p.as_value()).sm_value() == null)) ) {
                        term.as_parameters().remove(p);
                    }
                }
                
            }
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
    private static void variable2MetaVariable(Node node, Variable x, String metavarname)
    // -----------------------------------------------
    {
        Node[] children = node.children();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof VariableNode
                    && ((VariableNode) children[i]).lx_name().equals(
                            x.lx_name())) {
                node.replace(i, new MetaTermReferenceNode(metavarname));
            } else {
                variable2MetaVariable(children[i], x, metavarname);
            }
        }
    }
    
    // ==========================================================
	// Static needed statement to initialise the node operations.
    // ==========================================================
	static {
		SLSyntax.installOperations();
	}

}