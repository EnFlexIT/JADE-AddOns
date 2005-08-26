
/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France T�l�com

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

public class PredicateNode extends AtomicFormula
{
    public final int getNodeID(){
        return 10;
    }
    static int _as_symbol = 0;
    static int _as_terms = 1;

    public PredicateNode(Symbol as_symbol, ListOfTerm as_terms)  {
        super(2);
        as_symbol(as_symbol);
        as_terms(as_terms);
        initNode();
    }

    public PredicateNode() {
        super(2);
        as_symbol(null);
        as_terms(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitPredicateNode(this);}
    public Node getClone() {
        Node clone = new PredicateNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof PredicateNode) {
            super.copyValueOf(n);
            PredicateNode tn = (PredicateNode)n;
        }
        initNode();
    }
    public Symbol as_symbol() {return (Symbol)_nodes[_as_symbol];}
    public void as_symbol(Symbol s) {_nodes[_as_symbol] = s;}
    public ListOfTerm as_terms() {return (ListOfTerm)_nodes[_as_terms];}
    public void as_terms(ListOfTerm s) {_nodes[_as_terms] = s;}
}