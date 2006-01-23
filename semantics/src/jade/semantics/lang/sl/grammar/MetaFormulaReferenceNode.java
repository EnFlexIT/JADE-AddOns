
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

public class MetaFormulaReferenceNode extends Formula
{
    public static Integer ID = new Integer(6);
    public final int getClassID() {return ID.intValue();}
    java.lang.String _lx_name;
    Formula _sm_value;

    public MetaFormulaReferenceNode(java.lang.String lx_name)  {
        super(0);
        lx_name(lx_name);
    }

    public MetaFormulaReferenceNode() {
        super(0);
        lx_name(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitMetaFormulaReferenceNode(this);}

    public Node getClone() {
        Node clone = new MetaFormulaReferenceNode();
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof MetaFormulaReferenceNode) {
            super.copyValueOf(n);
            MetaFormulaReferenceNode tn = (MetaFormulaReferenceNode)n;
            _lx_name= tn._lx_name;
            _sm_value= tn._sm_value;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public java.lang.String lx_name() {return _lx_name;}
    public void lx_name(java.lang.String o) {_lx_name = o;}
    public Formula sm_value() {return _sm_value;}
    public void sm_value(Formula o) {_sm_value = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("lx_name") ) return true;
        if ( attrname.equals("sm_value") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("lx_name") ) return lx_name();
        if ( attrname.equals("sm_value") ) return sm_value();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("lx_name") ) {lx_name((java.lang.String)attrvalue);return;}
        if ( attrname.equals("sm_value") ) {sm_value((Formula)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}