
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

public abstract class Formula extends Node
{
        public interface Operations extends Node.Operations
        {
            public Formula getSimplifiedFormula(Formula node);
            public void simplify(Formula node);
            public boolean isMentalAttitude(Formula node, Term term);
            public boolean isSubsumedBy(Formula node, Formula formula);
            public boolean isConsistentWith(Formula node, Formula formula);
            public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true);
            public boolean isAFreeVariable(Formula node, Variable x);
            public Formula getVariablesSubstitution(Formula node, ListOfVariable vars);
            public Formula getVariablesSubstitutionAsIn(Formula node, Formula formula);
            public Formula getVariablesSubstitution(Formula node, Variable x, Variable y);
            public Formula isBeliefFrom(Formula node, Term agent);
            public jade.semantics.lang.sl.tools.MatchResult match(Formula node, Node expression);
            public Node instantiate(Formula node, String varname, Node expression);
        }
        public Formula getSimplifiedFormula()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).getSimplifiedFormula(this );
        }
        public void simplify()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((Formula.Operations)_thisoperations).simplify(this );
        }
        public boolean isMentalAttitude(Term term)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).isMentalAttitude(this , term);
        }
        public boolean isSubsumedBy(Formula formula)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).isSubsumedBy(this , formula);
        }
        public boolean isConsistentWith(Formula formula)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).isConsistentWith(this , formula);
        }
        public Formula getDoubleMirror(Term i, Term j, boolean default_result_is_true)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).getDoubleMirror(this , i, j, default_result_is_true);
        }
        public boolean isAFreeVariable(Variable x)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).isAFreeVariable(this , x);
        }
        public Formula getVariablesSubstitution(ListOfVariable vars)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).getVariablesSubstitution(this , vars);
        }
        public Formula getVariablesSubstitutionAsIn(Formula formula)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).getVariablesSubstitutionAsIn(this , formula);
        }
        public Formula getVariablesSubstitution(Variable x, Variable y)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).getVariablesSubstitution(this , x, y);
        }
        public Formula isBeliefFrom(Term agent)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).isBeliefFrom(this , agent);
        }
        public jade.semantics.lang.sl.tools.MatchResult match(Node expression)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).match(this , expression);
        }
        public Node instantiate(String varname, Node expression)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Formula.Operations)_thisoperations).instantiate(this , varname, expression);
        }
    Formula _sm_simplified_formula;

    public Formula(int capacity)  {
      super (capacity);
    }

    public void copyValueOf(Node n) {
        if (n instanceof Formula) {
            super.copyValueOf(n);
            Formula tn = (Formula)n;
            _sm_simplified_formula= tn._sm_simplified_formula;
        }
    }
    public Formula sm_simplified_formula() {return _sm_simplified_formula;}
    public void sm_simplified_formula(Formula o) {_sm_simplified_formula = o;}
}