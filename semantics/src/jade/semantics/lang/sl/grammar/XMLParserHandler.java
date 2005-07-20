
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

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.lang.reflect.*;
public class XMLParserHandler extends DefaultHandler 
{
    java.util.Stack stack = new java.util.Stack();
    public Node getRoot() {return (Node)(stack.empty()?null:stack.peek());}
    public void startElement(String nsuri,String lname,String qname,Attributes atts)
    {
        Node son = null;
        Node parent = (stack.empty()?null:(Node)stack.peek());
        if (qname.equals("content")) {
            son = new ContentNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Content.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
        }
        if (qname.equals("expressions")) {
            son = new ListOfContentExpression();
            if ( parent != null ) {
                try {
                    Method setAttrMethod = parent.getClass().getMethod("as_expressions", new Class[]{ListOfContentExpression.class});
                    setAttrMethod.invoke(parent, new Object[]{son});
                } catch(Exception e) {e.printStackTrace();}
            }
        }
        if (qname.equals("action_content_expression")) {
            son = new ActionContentExpressionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ContentExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
        }
        if (qname.equals("identifying_content_expression")) {
            son = new IdentifyingContentExpressionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ContentExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
        }
        if (qname.equals("formula_content_expression")) {
            son = new FormulaContentExpressionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ContentExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
        }
        if (qname.equals("meta_content_expression_reference")) {
            son = new MetaContentExpressionReferenceNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ContentExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_value", new Class[]{ContentExpression.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("meta_formula_reference")) {
            son = new MetaFormulaReferenceNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_value", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("not")) {
            son = new NotNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{UnaryLogicalFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("proposition_symbol")) {
            son = new PropositionSymbolNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{AtomicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("result")) {
            son = new ResultNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{AtomicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("predicate")) {
            son = new PredicateNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{AtomicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("terms")) {
            son = new ListOfTerm();
            if ( parent != null ) {
                try {
                    Method setAttrMethod = parent.getClass().getMethod("as_terms", new Class[]{ListOfTerm.class});
                    setAttrMethod.invoke(parent, new Object[]{son});
                } catch(Exception e) {e.printStackTrace();}
            }
        }
        if (qname.equals("true")) {
            son = new TrueNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{AtomicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("false")) {
            son = new FalseNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{AtomicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("equals")) {
            son = new EqualsNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{AtomicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("believe")) {
            son = new BelieveNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ModalLogicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("uncertainty")) {
            son = new UncertaintyNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ModalLogicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("intention")) {
            son = new IntentionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ModalLogicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("persistent_goal")) {
            son = new PersistentGoalNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ModalLogicFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("done")) {
            son = new DoneNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ActionFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("feasible")) {
            son = new FeasibleNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ActionFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("exists")) {
            son = new ExistsNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{QuantifiedFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("forall")) {
            son = new ForallNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{QuantifiedFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("implies")) {
            son = new ImpliesNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{BinaryLogicalFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("equiv")) {
            son = new EquivNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{BinaryLogicalFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("or")) {
            son = new OrNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{BinaryLogicalFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("and")) {
            son = new AndNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{BinaryLogicalFormula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Formula.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_formula") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_formula", new Class[]{Formula.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("meta_term_reference")) {
            son = new MetaTermReferenceNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_value", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("any")) {
            son = new AnyNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{IdentifyingExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("iota")) {
            son = new IotaNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{IdentifyingExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("all")) {
            son = new AllNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{IdentifyingExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("variable")) {
            son = new VariableNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Variable.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("meta_variable_reference")) {
            son = new MetaVariableReferenceNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Variable.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_value", new Class[]{Variable.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("integer_constant")) {
            son = new IntegerConstantNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Constant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.lang.Long.class});
                        setAttrMethod.invoke(son, new Object[]{new Long(atts.getValue(i))});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("real_constant")) {
            son = new RealConstantNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Constant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.lang.Double.class});
                        setAttrMethod.invoke(son, new Object[]{new Double(atts.getValue(i))});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("date_time_constant")) {
            son = new DateTimeConstantNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Constant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.util.Date.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("string_constant")) {
            son = new StringConstantNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{StringConstant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Constant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("word_constant")) {
            son = new WordConstantNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{StringConstant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Constant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("byte_constant")) {
            son = new ByteConstantNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{StringConstant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Constant.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.lang.Byte[].class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("term_set")) {
            son = new TermSetNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{TermSet.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("terms")) {
            son = new ListOfTerm();
            if ( parent != null ) {
                try {
                    Method setAttrMethod = parent.getClass().getMethod("as_terms", new Class[]{ListOfTerm.class});
                    setAttrMethod.invoke(parent, new Object[]{son});
                } catch(Exception e) {e.printStackTrace();}
            }
        }
        if (qname.equals("term_sequence")) {
            son = new TermSequenceNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{TermSequence.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("terms")) {
            son = new ListOfTerm();
            if ( parent != null ) {
                try {
                    Method setAttrMethod = parent.getClass().getMethod("as_terms", new Class[]{ListOfTerm.class});
                    setAttrMethod.invoke(parent, new Object[]{son});
                } catch(Exception e) {e.printStackTrace();}
            }
        }
        if (qname.equals("action_expression")) {
            son = new ActionExpressionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ActionExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("action") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_action", new Class[]{jade.semantics.actions.SemanticAction.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("alternative_action_expression")) {
            son = new AlternativeActionExpressionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ActionExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("action") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_action", new Class[]{jade.semantics.actions.SemanticAction.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("sequence_action_expression")) {
            son = new SequenceActionExpressionNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{ActionExpression.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("action") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_action", new Class[]{jade.semantics.actions.SemanticAction.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("functional_term")) {
            son = new FunctionalTermNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{FunctionalTerm.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("terms")) {
            son = new ListOfTerm();
            if ( parent != null ) {
                try {
                    Method setAttrMethod = parent.getClass().getMethod("as_terms", new Class[]{ListOfTerm.class});
                    setAttrMethod.invoke(parent, new Object[]{son});
                } catch(Exception e) {e.printStackTrace();}
            }
        }
        if (qname.equals("functional_term_param")) {
            son = new FunctionalTermParamNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{FunctionalTerm.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Term.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("simplified_term") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_simplified_term", new Class[]{Term.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("parameters")) {
            son = new ListOfParameter();
            if ( parent != null ) {
                try {
                    Method setAttrMethod = parent.getClass().getMethod("as_parameters", new Class[]{ListOfParameter.class});
                    setAttrMethod.invoke(parent, new Object[]{son});
                } catch(Exception e) {e.printStackTrace();}
            }
        }
        if (qname.equals("parameter")) {
            son = new ParameterNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Parameter.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("optional") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_optional", new Class[]{java.lang.Boolean.class});
                        setAttrMethod.invoke(son, new Object[]{new Boolean(atts.getValue(i))});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("symbol")) {
            son = new SymbolNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Symbol.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_value", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        if (qname.equals("meta_symbol_reference")) {
            son = new MetaSymbolReferenceNode();
            if ( parent != null ) {
                if ( parent instanceof ListOfNodes ) {
                    ((ListOfNodes)parent).add(son);
                }
                else {
                    String attname = atts.getValue(atts.getIndex("ATTRIBUTE"));
                    if ( attname == null ) {
                        attname = qname;
                    }
                    Method setAttrMethod = null;
                    if ( setAttrMethod == null ) {
                        try {
                            setAttrMethod = parent.getClass().getMethod(("as_"+attname), new Class[]{Symbol.class});
                        } catch(NoSuchMethodException e) {}
                    }
                    if ( setAttrMethod != null ) {
                        try {
                            setAttrMethod.invoke(parent, new Object[]{son});
                        } catch(Exception e) {e.printStackTrace();}
                    }
                }
            }
            for (int i=0; i<atts.getLength();i++) {
                if ( atts.getQName(i).equals("name") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("lx_name", new Class[]{java.lang.String.class});
                        setAttrMethod.invoke(son, new Object[]{atts.getValue(i)});
                    } catch(Exception e) {e.printStackTrace();}
                }
                if ( atts.getQName(i).equals("value") ) {
                    try {
                        Method setAttrMethod = son.getClass().getMethod("sm_value", new Class[]{Symbol.class});
                    } catch(Exception e) {e.printStackTrace();}
                }
            }
        }
        stack.push(son);
    }
    public void endElement(String nsuri,String lname,String qname)
    {
        if ( stack.size() > 1 ) {
            stack.pop();
        }
    }
}
