
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

public class ActionExpressionNode extends ActionExpression
{
    public final int getNodeID(){
        return 40;
    }
    static int _as_agent = 0;
    static int _as_term = 1;

    public ActionExpressionNode(Term as_agent, Term as_term)  {
        super(2);
        as_agent(as_agent);
        as_term(as_term);
        initNode();
    }

    public ActionExpressionNode() {
        super(2);
        as_agent(null);
        as_term(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitActionExpressionNode(this);}
    public Node getClone() {
        Node clone = new ActionExpressionNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof ActionExpressionNode) {
            super.copyValueOf(n);
            ActionExpressionNode tn = (ActionExpressionNode)n;
        }
        initNode();
    }
    public Term as_agent() {return (Term)_nodes[_as_agent];}
    public void as_agent(Term s) {_nodes[_as_agent] = s;}
    public Term as_term() {return (Term)_nodes[_as_term];}
    public void as_term(Term s) {_nodes[_as_term] = s;}
}