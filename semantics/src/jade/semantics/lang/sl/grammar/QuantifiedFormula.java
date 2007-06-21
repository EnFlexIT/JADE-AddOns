
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

public abstract class QuantifiedFormula extends Formula
{
    public static Integer ID = new Integer(10010);
    public int getClassID() {return ID.intValue();}
    static int _as_variable = 0;
    static int _as_formula = 1;

    public QuantifiedFormula(int capacity, Variable as_variable, Formula as_formula)  {
      super (capacity);
        as_variable(as_variable);
        as_formula(as_formula);
    }

    public void copyValueOf(Node n) {
        if (n instanceof QuantifiedFormula) {
            super.copyValueOf(n);
            QuantifiedFormula tn = (QuantifiedFormula)n;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public Variable as_variable() {return (Variable)_nodes[_as_variable];}
    public void as_variable(Variable s) {_nodes[_as_variable] = s;}
    public Formula as_formula() {return (Formula)_nodes[_as_formula];}
    public void as_formula(Formula s) {_nodes[_as_formula] = s;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("as_variable") ) return true;
        if ( attrname.equals("as_formula") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("as_variable") ) return as_variable();
        if ( attrname.equals("as_formula") ) return as_formula();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("as_variable") ) {as_variable((Variable)attrvalue);return;}
        if ( attrname.equals("as_formula") ) {as_formula((Formula)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}