
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

public class AlternativeActionExpressionNode extends ActionExpression
{
    public static Integer ID = new Integer(42);
    public final int getClassID() {return ID.intValue();}
    static int _as_left_action = 0;
    static int _as_right_action = 1;

    public AlternativeActionExpressionNode(Term as_left_action, Term as_right_action)  {
        super(2);
        as_left_action(as_left_action);
        as_right_action(as_right_action);
        initNode();
    }

    public AlternativeActionExpressionNode() {
        super(2);
        as_left_action(null);
        as_right_action(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitAlternativeActionExpressionNode(this);}

    public Node getClone() {
        Node clone = new AlternativeActionExpressionNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof AlternativeActionExpressionNode) {
            super.copyValueOf(n);
            AlternativeActionExpressionNode tn = (AlternativeActionExpressionNode)n;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public Term as_left_action() {return (Term)_nodes[_as_left_action];}
    public void as_left_action(Term s) {_nodes[_as_left_action] = s;}
    public Term as_right_action() {return (Term)_nodes[_as_right_action];}
    public void as_right_action(Term s) {_nodes[_as_right_action] = s;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("as_left_action") ) return true;
        if ( attrname.equals("as_right_action") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("as_left_action") ) return as_left_action();
        if ( attrname.equals("as_right_action") ) return as_right_action();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("as_left_action") ) {as_left_action((Term)attrvalue);return;}
        if ( attrname.equals("as_right_action") ) {as_right_action((Term)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}