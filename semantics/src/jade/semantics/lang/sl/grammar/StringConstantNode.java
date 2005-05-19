
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

public class StringConstantNode extends StringConstant
{
    java.lang.String _lx_value;

    public StringConstantNode(java.lang.String lx_value)  {
        super(0);
        lx_value(lx_value);
    }

    public StringConstantNode() {
        super(0);
        lx_value(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitStringConstantNode(this);}
    public Node getClone() {
        Node clone = new StringConstantNode();
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof StringConstantNode) {
            super.copyValueOf(n);
            StringConstantNode tn = (StringConstantNode)n;
            _lx_value= tn._lx_value;
        }
        initNode();

    }
    public java.lang.String lx_value() {return _lx_value;}
    public void lx_value(java.lang.String o) {_lx_value = o;}
}