
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

public class ParameterNode extends Parameter
{
    java.lang.String _lx_name;
    static int _as_value = 0;

    public ParameterNode(Term as_value, java.lang.String lx_name)  {
        super(1);
        as_value(as_value);
        lx_name(lx_name);
    }

    public ParameterNode(Term as_value)  {
        super(1);
        as_value(as_value);
        lx_name(null);
        initNode();
    }

    public ParameterNode() {
        super(1);
        as_value(null);
        lx_name(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitParameterNode(this);}
    public Node getClone() {
        Node clone = new ParameterNode(null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof ParameterNode) {
            super.copyValueOf(n);
            ParameterNode tn = (ParameterNode)n;
            _lx_name= tn._lx_name;
        }
        initNode();

    }
    public java.lang.String lx_name() {return _lx_name;}
    public void lx_name(java.lang.String o) {_lx_name = o;}
    public Term as_value() {return (Term)_nodes[_as_value];}
    public void as_value(Term s) {_nodes[_as_value] = s;}
}