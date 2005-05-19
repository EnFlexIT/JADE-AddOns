
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

public class SequenceActionExpressionNode extends ActionExpression
{
    static int _as_left_action_expression = 0;
    static int _as_right_action_expression = 1;

    public SequenceActionExpressionNode(ActionExpression as_left_action_expression, ActionExpression as_right_action_expression)  {
        super(2);
        as_left_action_expression(as_left_action_expression);
        as_right_action_expression(as_right_action_expression);
        initNode();
    }

    public SequenceActionExpressionNode() {
        super(2);
        as_left_action_expression(null);
        as_right_action_expression(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitSequenceActionExpressionNode(this);}
    public Node getClone() {
        Node clone = new SequenceActionExpressionNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof SequenceActionExpressionNode) {
            super.copyValueOf(n);
            SequenceActionExpressionNode tn = (SequenceActionExpressionNode)n;
        }
        initNode();

    }
    public ActionExpression as_left_action_expression() {return (ActionExpression)_nodes[_as_left_action_expression];}
    public void as_left_action_expression(ActionExpression s) {_nodes[_as_left_action_expression] = s;}
    public ActionExpression as_right_action_expression() {return (ActionExpression)_nodes[_as_right_action_expression];}
    public void as_right_action_expression(ActionExpression s) {_nodes[_as_right_action_expression] = s;}
}