
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

public abstract class ModalLogicFormula extends Formula
{
    static int _as_agent = 0;
    static int _as_formula = 1;

    public ModalLogicFormula(int capacity, Term as_agent, Formula as_formula)  {
      super (capacity);
        as_agent(as_agent);
        as_formula(as_formula);
    }

    public void copyValueOf(Node n) {
        if (n instanceof ModalLogicFormula) {
            super.copyValueOf(n);
            ModalLogicFormula tn = (ModalLogicFormula)n;
        }
    }
    public Term as_agent() {return (Term)_nodes[_as_agent];}
    public void as_agent(Term s) {_nodes[_as_agent] = s;}
    public Formula as_formula() {return (Formula)_nodes[_as_formula];}
    public void as_formula(Formula s) {_nodes[_as_formula] = s;}
}