
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

public class ResultNode extends AtomicFormula
{
    public final int getNodeID(){
        return 9;
    }
    static int _as_term1 = 0;
    static int _as_term2 = 1;

    public ResultNode(Term as_term1, Term as_term2)  {
        super(2);
        as_term1(as_term1);
        as_term2(as_term2);
        initNode();
    }

    public ResultNode() {
        super(2);
        as_term1(null);
        as_term2(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitResultNode(this);}
    public Node getClone() {
        Node clone = new ResultNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof ResultNode) {
            super.copyValueOf(n);
            ResultNode tn = (ResultNode)n;
        }
        initNode();
    }
    public Term as_term1() {return (Term)_nodes[_as_term1];}
    public void as_term1(Term s) {_nodes[_as_term1] = s;}
    public Term as_term2() {return (Term)_nodes[_as_term2];}
    public void as_term2(Term s) {_nodes[_as_term2] = s;}
}