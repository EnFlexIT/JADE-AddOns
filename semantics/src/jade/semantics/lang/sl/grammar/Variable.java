
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

public abstract class Variable extends Term
{
    public static Integer ID = new Integer(10014);
    public int getClassID() {return ID.intValue();}
    java.lang.String _lx_name;

    public Variable(int capacity, java.lang.String lx_name)  {
      super (capacity);
        lx_name(lx_name);
    }

    public void copyValueOf(Node n) {
        if (n instanceof Variable) {
            super.copyValueOf(n);
            Variable tn = (Variable)n;
            _lx_name= tn._lx_name;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public java.lang.String lx_name() {return _lx_name;}
    public void lx_name(java.lang.String o) {_lx_name = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("lx_name") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("lx_name") ) return lx_name();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("lx_name") ) {lx_name((java.lang.String)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}