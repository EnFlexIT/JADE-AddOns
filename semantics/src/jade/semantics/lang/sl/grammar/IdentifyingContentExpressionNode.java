
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

public class IdentifyingContentExpressionNode extends ContentExpression
{
    static int _as_identifying_expression = 0;

    public IdentifyingContentExpressionNode(IdentifyingExpression as_identifying_expression)  {
        super(1);
        as_identifying_expression(as_identifying_expression);
        initNode();
    }

    public IdentifyingContentExpressionNode() {
        super(1);
        as_identifying_expression(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitIdentifyingContentExpressionNode(this);}
    public Node getClone() {
        Node clone = new IdentifyingContentExpressionNode(null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof IdentifyingContentExpressionNode) {
            super.copyValueOf(n);
            IdentifyingContentExpressionNode tn = (IdentifyingContentExpressionNode)n;
        }
        initNode();

    }
    public IdentifyingExpression as_identifying_expression() {return (IdentifyingExpression)_nodes[_as_identifying_expression];}
    public void as_identifying_expression(IdentifyingExpression s) {_nodes[_as_identifying_expression] = s;}
}