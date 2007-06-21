
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

public abstract class FunctionalTerm extends Term
{
    public static Integer ID = new Integer(10018);
    public int getClassID() {return ID.intValue();}
    static int _as_symbol = 0;

    public FunctionalTerm(int capacity, Symbol as_symbol)  {
      super (capacity);
        as_symbol(as_symbol);
    }

    public void copyValueOf(Node n) {
        if (n instanceof FunctionalTerm) {
            super.copyValueOf(n);
            FunctionalTerm tn = (FunctionalTerm)n;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public Symbol as_symbol() {return (Symbol)_nodes[_as_symbol];}
    public void as_symbol(Symbol s) {_nodes[_as_symbol] = s;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("as_symbol") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("as_symbol") ) return as_symbol();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("as_symbol") ) {as_symbol((Symbol)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}