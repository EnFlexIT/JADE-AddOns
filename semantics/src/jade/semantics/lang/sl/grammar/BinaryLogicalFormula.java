
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

public abstract class BinaryLogicalFormula extends Formula
{
    static int _as_left_formula = 0;
    static int _as_right_formula = 1;

    public BinaryLogicalFormula(int capacity, Formula as_left_formula, Formula as_right_formula)  {
      super (capacity);
        as_left_formula(as_left_formula);
        as_right_formula(as_right_formula);
    }

    public void copyValueOf(Node n) {
        if (n instanceof BinaryLogicalFormula) {
            super.copyValueOf(n);
            BinaryLogicalFormula tn = (BinaryLogicalFormula)n;
        }
    }
    public Formula as_left_formula() {return (Formula)_nodes[_as_left_formula];}
    public void as_left_formula(Formula s) {_nodes[_as_left_formula] = s;}
    public Formula as_right_formula() {return (Formula)_nodes[_as_right_formula];}
    public void as_right_formula(Formula s) {_nodes[_as_right_formula] = s;}
}