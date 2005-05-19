
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

public class MetaTermReferenceNode extends Term
{
    java.lang.String _lx_name;
    Term _sm_value;

    public MetaTermReferenceNode(java.lang.String lx_name)  {
        super(0);
        lx_name(lx_name);
    }

    public MetaTermReferenceNode() {
        super(0);
        lx_name(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitMetaTermReferenceNode(this);}
    public Node getClone() {
        Node clone = new MetaTermReferenceNode();
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof MetaTermReferenceNode) {
            super.copyValueOf(n);
            MetaTermReferenceNode tn = (MetaTermReferenceNode)n;
            _lx_name= tn._lx_name;
            _sm_value= tn._sm_value;
        }
        initNode();

    }
    public java.lang.String lx_name() {return _lx_name;}
    public void lx_name(java.lang.String o) {_lx_name = o;}
    public Term sm_value() {return _sm_value;}
    public void sm_value(Term o) {_sm_value = o;}
}