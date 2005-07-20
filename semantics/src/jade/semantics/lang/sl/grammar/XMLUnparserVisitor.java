
/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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


//-----------------------------------------------------
// This file has been automatically produced by a tool.
//-----------------------------------------------------

package jade.semantics.lang.sl.grammar;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;

import javax.xml.transform.dom.*;

import javax.xml.transform.stream.*;

public class XMLUnparserVisitor extends VisitorBase 
{
    org.w3c.dom.Document _doc;
    org.w3c.dom.Node _node;

    String _attrName;

    public XMLUnparserVisitor()
    {
    }

    public void unparse(Node node, javax.xml.transform.stream.StreamResult result)
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
            DocumentBuilder db = dbf.newDocumentBuilder ();
            _doc = db.newDocument ();
            _node = _doc;
            _attrName = null;
            node.accept(this);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            Source source = new DOMSource(_doc);
            transformer.transform(source, result);
        } 
        catch(Exception e) {System.out.println(e);}
    }
    public void unparse(Node node, org.w3c.dom.Document doc)
    {
        try {
            _node = _doc = doc;
            node.accept(this);
        } 
        catch(Exception e) {System.out.println(e);}
    }
    public void visitListOfContentExpression(ListOfContentExpression node) {
        node.childrenAccept(this);
    }
    public void visitListOfFormula(ListOfFormula node) {
        node.childrenAccept(this);
    }
    public void visitListOfTerm(ListOfTerm node) {
        node.childrenAccept(this);
    }
    public void visitListOfVariable(ListOfVariable node) {
        node.childrenAccept(this);
    }
    public void visitListOfParameter(ListOfParameter node) {
        node.childrenAccept(this);
    }
    public void visitContentNode(ContentNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("content")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("content") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("content")); 
            _node = _node.getLastChild();
        }
        if ((node.as_expressions() != null) && (node.as_expressions().size() != 0))
        {
            _node.appendChild(_doc.createElement("expressions")); 
            _node = _node.getLastChild();
            _attrName = null;
            node.as_expressions().childrenAccept(this);
            _node = _node.getParentNode();
        }
        _node = _node.getParentNode();
    }
    public void visitActionContentExpressionNode(ActionContentExpressionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("action_content_expression")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("action_content_expression") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("action_content_expression")); 
            _node = _node.getLastChild();
        }
        if (node.as_action_expression() != null)
        {
            _attrName = "action_expression";
            node.as_action_expression().accept(this);
            _attrName = null;
        }
        _node = _node.getParentNode();
    }
    public void visitIdentifyingContentExpressionNode(IdentifyingContentExpressionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("identifying_content_expression")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("identifying_content_expression") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("identifying_content_expression")); 
            _node = _node.getLastChild();
        }
        if (node.as_identifying_expression() != null)
        {
            _attrName = "identifying_expression";
            node.as_identifying_expression().accept(this);
            _attrName = null;
        }
        _node = _node.getParentNode();
    }
    public void visitFormulaContentExpressionNode(FormulaContentExpressionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("formula_content_expression")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("formula_content_expression") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("formula_content_expression")); 
            _node = _node.getLastChild();
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        _node = _node.getParentNode();
    }
    public void visitMetaContentExpressionReferenceNode(MetaContentExpressionReferenceNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("meta_content_expression_reference")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("meta_content_expression_reference") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("meta_content_expression_reference")); 
            _node = _node.getLastChild();
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.sm_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.sm_value().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitMetaFormulaReferenceNode(MetaFormulaReferenceNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("meta_formula_reference")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("meta_formula_reference") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("meta_formula_reference")); 
            _node = _node.getLastChild();
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.sm_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.sm_value().toString());
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitNotNode(NotNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("not")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("not") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("not")); 
            _node = _node.getLastChild();
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitPropositionSymbolNode(PropositionSymbolNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("proposition_symbol")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("proposition_symbol") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("proposition_symbol")); 
            _node = _node.getLastChild();
        }
        if (node.as_symbol() != null)
        {
            _attrName = "symbol";
            node.as_symbol().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitResultNode(ResultNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("result")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("result") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("result")); 
            _node = _node.getLastChild();
        }
        if (node.as_term1() != null)
        {
            _attrName = "term1";
            node.as_term1().accept(this);
            _attrName = null;
        }
        if (node.as_term2() != null)
        {
            _attrName = "term2";
            node.as_term2().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitPredicateNode(PredicateNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("predicate")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("predicate") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("predicate")); 
            _node = _node.getLastChild();
        }
        if (node.as_symbol() != null)
        {
            _attrName = "symbol";
            node.as_symbol().accept(this);
            _attrName = null;
        }
        if ((node.as_terms() != null) && (node.as_terms().size() != 0))
        {
            _node.appendChild(_doc.createElement("terms")); 
            _node = _node.getLastChild();
            _attrName = null;
            node.as_terms().childrenAccept(this);
            _node = _node.getParentNode();
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitTrueNode(TrueNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("true")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("true") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("true")); 
            _node = _node.getLastChild();
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitFalseNode(FalseNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("false")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("false") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("false")); 
            _node = _node.getLastChild();
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitEqualsNode(EqualsNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("equals")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("equals") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("equals")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_term() != null)
        {
            _attrName = "left_term";
            node.as_left_term().accept(this);
            _attrName = null;
        }
        if (node.as_right_term() != null)
        {
            _attrName = "right_term";
            node.as_right_term().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitBelieveNode(BelieveNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("believe")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("believe") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("believe")); 
            _node = _node.getLastChild();
        }
        if (node.as_agent() != null)
        {
            _attrName = "agent";
            node.as_agent().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitUncertaintyNode(UncertaintyNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("uncertainty")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("uncertainty") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("uncertainty")); 
            _node = _node.getLastChild();
        }
        if (node.as_agent() != null)
        {
            _attrName = "agent";
            node.as_agent().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitIntentionNode(IntentionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("intention")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("intention") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("intention")); 
            _node = _node.getLastChild();
        }
        if (node.as_agent() != null)
        {
            _attrName = "agent";
            node.as_agent().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitPersistentGoalNode(PersistentGoalNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("persistent_goal")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("persistent_goal") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("persistent_goal")); 
            _node = _node.getLastChild();
        }
        if (node.as_agent() != null)
        {
            _attrName = "agent";
            node.as_agent().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitDoneNode(DoneNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("done")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("done") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("done")); 
            _node = _node.getLastChild();
        }
        if (node.as_action() != null)
        {
            _attrName = "action";
            node.as_action().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitFeasibleNode(FeasibleNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("feasible")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("feasible") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("feasible")); 
            _node = _node.getLastChild();
        }
        if (node.as_action() != null)
        {
            _attrName = "action";
            node.as_action().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitExistsNode(ExistsNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("exists")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("exists") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("exists")); 
            _node = _node.getLastChild();
        }
        if (node.as_variable() != null)
        {
            _attrName = "variable";
            node.as_variable().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitForallNode(ForallNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("forall")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("forall") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("forall")); 
            _node = _node.getLastChild();
        }
        if (node.as_variable() != null)
        {
            _attrName = "variable";
            node.as_variable().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitImpliesNode(ImpliesNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("implies")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("implies") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("implies")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_formula() != null)
        {
            _attrName = "left_formula";
            node.as_left_formula().accept(this);
            _attrName = null;
        }
        if (node.as_right_formula() != null)
        {
            _attrName = "right_formula";
            node.as_right_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitEquivNode(EquivNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("equiv")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("equiv") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("equiv")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_formula() != null)
        {
            _attrName = "left_formula";
            node.as_left_formula().accept(this);
            _attrName = null;
        }
        if (node.as_right_formula() != null)
        {
            _attrName = "right_formula";
            node.as_right_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitOrNode(OrNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("or")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("or") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("or")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_formula() != null)
        {
            _attrName = "left_formula";
            node.as_left_formula().accept(this);
            _attrName = null;
        }
        if (node.as_right_formula() != null)
        {
            _attrName = "right_formula";
            node.as_right_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitAndNode(AndNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("and")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("and") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("and")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_formula() != null)
        {
            _attrName = "left_formula";
            node.as_left_formula().accept(this);
            _attrName = null;
        }
        if (node.as_right_formula() != null)
        {
            _attrName = "right_formula";
            node.as_right_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_formula()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_formula",node.sm_simplified_formula().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitMetaTermReferenceNode(MetaTermReferenceNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("meta_term_reference")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("meta_term_reference") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("meta_term_reference")); 
            _node = _node.getLastChild();
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.sm_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.sm_value().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitAnyNode(AnyNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("any")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("any") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("any")); 
            _node = _node.getLastChild();
        }
        if (node.as_term() != null)
        {
            _attrName = "term";
            node.as_term().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitIotaNode(IotaNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("iota")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("iota") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("iota")); 
            _node = _node.getLastChild();
        }
        if (node.as_term() != null)
        {
            _attrName = "term";
            node.as_term().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitAllNode(AllNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("all")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("all") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("all")); 
            _node = _node.getLastChild();
        }
        if (node.as_term() != null)
        {
            _attrName = "term";
            node.as_term().accept(this);
            _attrName = null;
        }
        if (node.as_formula() != null)
        {
            _attrName = "formula";
            node.as_formula().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitVariableNode(VariableNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("variable")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("variable") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("variable")); 
            _node = _node.getLastChild();
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitMetaVariableReferenceNode(MetaVariableReferenceNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("meta_variable_reference")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("meta_variable_reference") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("meta_variable_reference")); 
            _node = _node.getLastChild();
        }
        if (node.sm_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.sm_value().toString());
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitIntegerConstantNode(IntegerConstantNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("integer_constant")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("integer_constant") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("integer_constant")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitRealConstantNode(RealConstantNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("real_constant")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("real_constant") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("real_constant")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitDateTimeConstantNode(DateTimeConstantNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("date_time_constant")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("date_time_constant") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("date_time_constant")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitStringConstantNode(StringConstantNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("string_constant")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("string_constant") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("string_constant")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            if (!node.lx_value().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value());
            }
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitWordConstantNode(WordConstantNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("word_constant")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("word_constant") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("word_constant")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            if (!node.lx_value().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value());
            }
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitByteConstantNode(ByteConstantNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("byte_constant")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("byte_constant") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("byte_constant")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitTermSetNode(TermSetNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("term_set")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("term_set") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("term_set")); 
            _node = _node.getLastChild();
        }
        if ((node.as_terms() != null) && (node.as_terms().size() != 0))
        {
            _node.appendChild(_doc.createElement("terms")); 
            _node = _node.getLastChild();
            _attrName = null;
            node.as_terms().childrenAccept(this);
            _node = _node.getParentNode();
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitTermSequenceNode(TermSequenceNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("term_sequence")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("term_sequence") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("term_sequence")); 
            _node = _node.getLastChild();
        }
        if ((node.as_terms() != null) && (node.as_terms().size() != 0))
        {
            _node.appendChild(_doc.createElement("terms")); 
            _node = _node.getLastChild();
            _attrName = null;
            node.as_terms().childrenAccept(this);
            _node = _node.getParentNode();
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitActionExpressionNode(ActionExpressionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("action_expression")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("action_expression") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("action_expression")); 
            _node = _node.getLastChild();
        }
        if (node.as_agent() != null)
        {
            _attrName = "agent";
            node.as_agent().accept(this);
            _attrName = null;
        }
        if (node.as_term() != null)
        {
            _attrName = "term";
            node.as_term().accept(this);
            _attrName = null;
        }
        if (node.sm_action()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("action",node.sm_action().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitAlternativeActionExpressionNode(AlternativeActionExpressionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("alternative_action_expression")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("alternative_action_expression") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("alternative_action_expression")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_action() != null)
        {
            _attrName = "left_action";
            node.as_left_action().accept(this);
            _attrName = null;
        }
        if (node.as_right_action() != null)
        {
            _attrName = "right_action";
            node.as_right_action().accept(this);
            _attrName = null;
        }
        if (node.sm_action()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("action",node.sm_action().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitSequenceActionExpressionNode(SequenceActionExpressionNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("sequence_action_expression")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("sequence_action_expression") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("sequence_action_expression")); 
            _node = _node.getLastChild();
        }
        if (node.as_left_action() != null)
        {
            _attrName = "left_action";
            node.as_left_action().accept(this);
            _attrName = null;
        }
        if (node.as_right_action() != null)
        {
            _attrName = "right_action";
            node.as_right_action().accept(this);
            _attrName = null;
        }
        if (node.sm_action()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("action",node.sm_action().toString());
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitFunctionalTermNode(FunctionalTermNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("functional_term")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("functional_term") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("functional_term")); 
            _node = _node.getLastChild();
        }
        if ((node.as_terms() != null) && (node.as_terms().size() != 0))
        {
            _node.appendChild(_doc.createElement("terms")); 
            _node = _node.getLastChild();
            _attrName = null;
            node.as_terms().childrenAccept(this);
            _node = _node.getParentNode();
        }
        if (node.as_symbol() != null)
        {
            _attrName = "symbol";
            node.as_symbol().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitFunctionalTermParamNode(FunctionalTermParamNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("functional_term_param")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("functional_term_param") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("functional_term_param")); 
            _node = _node.getLastChild();
        }
        if ((node.as_parameters() != null) && (node.as_parameters().size() != 0))
        {
            _node.appendChild(_doc.createElement("parameters")); 
            _node = _node.getLastChild();
            _attrName = null;
            node.as_parameters().childrenAccept(this);
            _node = _node.getParentNode();
        }
        if (node.as_symbol() != null)
        {
            _attrName = "symbol";
            node.as_symbol().accept(this);
            _attrName = null;
        }
        if (node.sm_simplified_term()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("simplified_term",node.sm_simplified_term().toString());
        }
        _node = _node.getParentNode();
    }
    public void visitParameterNode(ParameterNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("parameter")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("parameter") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("parameter")); 
            _node = _node.getLastChild();
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.lx_optional()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("optional",node.lx_optional().toString());
        }
        if (node.as_value() != null)
        {
            _attrName = "value";
            node.as_value().accept(this);
            _attrName = null;
        }
        _node = _node.getParentNode();
    }
    public void visitSymbolNode(SymbolNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("symbol")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("symbol") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("symbol")); 
            _node = _node.getLastChild();
        }
        if (node.lx_value()!=null) {
            if (!node.lx_value().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("value",node.lx_value());
            }
        }
        _node = _node.getParentNode();
    }
    public void visitMetaSymbolReferenceNode(MetaSymbolReferenceNode node) {
        if ( _attrName != null ) {
            _node.appendChild(_doc.createElement("meta_symbol_reference")); 
            _node = _node.getLastChild();
            if ( !_attrName.equals("meta_symbol_reference") ) {
                ((org.w3c.dom.Element)_node).setAttribute("ATTRIBUTE",_attrName);
            }
        }
        else {
            _node.appendChild(_doc.createElement("meta_symbol_reference")); 
            _node = _node.getLastChild();
        }
        if (node.lx_name()!=null) {
            if (!node.lx_name().equals("")) {
                ((org.w3c.dom.Element)_node).setAttribute("name",node.lx_name());
            }
        }
        if (node.sm_value()!=null) {
            ((org.w3c.dom.Element)_node).setAttribute("value",node.sm_value().toString());
        }
        _node = _node.getParentNode();
    }

};
