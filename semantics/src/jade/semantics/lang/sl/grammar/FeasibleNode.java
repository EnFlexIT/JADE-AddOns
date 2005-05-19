
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

public class FeasibleNode extends ActionFormula
{

    public FeasibleNode(ActionExpression as_expression, Formula as_formula)  {
        super(2, as_expression, as_formula);
        initNode();
    }

    public FeasibleNode() {
        super(2, null, null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitFeasibleNode(this);}
    public Node getClone() {
        Node clone = new FeasibleNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof FeasibleNode) {
            super.copyValueOf(n);
            FeasibleNode tn = (FeasibleNode)n;
        }
        initNode();

    }
}