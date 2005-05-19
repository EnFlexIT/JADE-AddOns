
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

public class EqualsNode extends AtomicFormula
{
    static int _as_left_term = 0;
    static int _as_right_term = 1;

    public EqualsNode(Term as_left_term, Term as_right_term)  {
        super(2);
        as_left_term(as_left_term);
        as_right_term(as_right_term);
        initNode();
    }

    public EqualsNode() {
        super(2);
        as_left_term(null);
        as_right_term(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitEqualsNode(this);}
    public Node getClone() {
        Node clone = new EqualsNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof EqualsNode) {
            super.copyValueOf(n);
            EqualsNode tn = (EqualsNode)n;
        }
        initNode();

    }
    public Term as_left_term() {return (Term)_nodes[_as_left_term];}
    public void as_left_term(Term s) {_nodes[_as_left_term] = s;}
    public Term as_right_term() {return (Term)_nodes[_as_right_term];}
    public void as_right_term(Term s) {_nodes[_as_right_term] = s;}
}