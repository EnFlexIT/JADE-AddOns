
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

public class OrNode extends BinaryLogicalFormula
{
    public static Integer ID = new Integer(24);
    public final int getClassID() {return ID.intValue();}

    public OrNode(Formula as_left_formula, Formula as_right_formula)  {
        super(2, as_left_formula, as_right_formula);
        initNode();
    }

    public OrNode() {
        super(2, null, null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitOrNode(this);}

    public Node getClone() {
        Node clone = new OrNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof OrNode) {
            super.copyValueOf(n);
            OrNode tn = (OrNode)n;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
}