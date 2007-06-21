
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

public abstract class Parameter extends Node
{
    public static Integer ID = new Integer(10024);
    public int getClassID() {return ID.intValue();}
    java.lang.String _lx_name;
    java.lang.Boolean _lx_optional;
    static int _as_value = 0;

    public Parameter(int capacity, Term as_value, java.lang.String lx_name, java.lang.Boolean lx_optional)  {
      super (capacity);
        as_value(as_value);
        lx_name(lx_name);
        lx_optional(lx_optional);
    }

    public void copyValueOf(Node n) {
        if (n instanceof Parameter) {
            super.copyValueOf(n);
            Parameter tn = (Parameter)n;
            _lx_name= tn._lx_name;
            _lx_optional= tn._lx_optional;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public java.lang.String lx_name() {return _lx_name;}
    public void lx_name(java.lang.String o) {_lx_name = o;}
    public java.lang.Boolean lx_optional() {return _lx_optional;}
    public void lx_optional(java.lang.Boolean o) {_lx_optional = o;}
    public Term as_value() {return (Term)_nodes[_as_value];}
    public void as_value(Term s) {_nodes[_as_value] = s;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("lx_name") ) return true;
        if ( attrname.equals("lx_optional") ) return true;
        if ( attrname.equals("as_value") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("lx_name") ) return lx_name();
        if ( attrname.equals("lx_optional") ) return lx_optional();
        if ( attrname.equals("as_value") ) return as_value();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("lx_name") ) {lx_name((java.lang.String)attrvalue);return;}
        if ( attrname.equals("lx_optional") ) {lx_optional((java.lang.Boolean)attrvalue);return;}
        if ( attrname.equals("as_value") ) {as_value((Term)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}