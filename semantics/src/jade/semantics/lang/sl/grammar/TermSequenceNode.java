
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

public class TermSequenceNode extends TermSequence
{
    public static Integer ID = new Integer(40);
    public final int getClassID() {return ID.intValue();}

    public TermSequenceNode(ListOfTerm as_terms)  {
        super(1, as_terms);
        initNode();
    }

    public TermSequenceNode() {
        super(1, null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitTermSequenceNode(this);}

    public Node getClone() {
        Node clone = new TermSequenceNode(null);
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof TermSequenceNode) {
            super.copyValueOf(n);
            TermSequenceNode tn = (TermSequenceNode)n;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
}