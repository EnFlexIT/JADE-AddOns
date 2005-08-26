
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

public abstract class ActionExpression extends Term
{
        public interface Operations extends Term.Operations
        {
            public ListOfTerm getAgents(ActionExpression node);
        }
        public ListOfTerm getAgents()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((ActionExpression.Operations)_thisoperations).getAgents(this );
        }
    jade.semantics.actions.SemanticAction _sm_action;

    public ActionExpression(int capacity)  {
      super (capacity);
    }

    public void copyValueOf(Node n) {
        if (n instanceof ActionExpression) {
            super.copyValueOf(n);
            ActionExpression tn = (ActionExpression)n;
            _sm_action= tn._sm_action;
        }
    }
    public jade.semantics.actions.SemanticAction sm_action() {return _sm_action;}
    public void sm_action(jade.semantics.actions.SemanticAction o) {_sm_action = o;}
}