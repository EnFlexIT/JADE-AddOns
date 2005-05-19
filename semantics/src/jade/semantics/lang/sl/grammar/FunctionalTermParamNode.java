
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

public class FunctionalTermParamNode extends FunctionalTerm
{
    static int _as_symbol = 0;
    static int _as_parameters = 1;

    public FunctionalTermParamNode(Symbol as_symbol, ListOfParameter as_parameters)  {
        super(2);
        as_symbol(as_symbol);
        as_parameters(as_parameters);
        initNode();
    }

    public FunctionalTermParamNode() {
        super(2);
        as_symbol(null);
        as_parameters(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitFunctionalTermParamNode(this);}
    public Node getClone() {
        Node clone = new FunctionalTermParamNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof FunctionalTermParamNode) {
            super.copyValueOf(n);
            FunctionalTermParamNode tn = (FunctionalTermParamNode)n;
        }
        initNode();

    }
    public Symbol as_symbol() {return (Symbol)_nodes[_as_symbol];}
    public void as_symbol(Symbol s) {_nodes[_as_symbol] = s;}
    public ListOfParameter as_parameters() {return (ListOfParameter)_nodes[_as_parameters];}
    public void as_parameters(ListOfParameter s) {_nodes[_as_parameters] = s;}
}