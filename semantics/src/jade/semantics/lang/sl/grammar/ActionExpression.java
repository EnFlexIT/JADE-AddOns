
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
    public static Integer ID = new Integer(10019);
    public int getClassID() {return ID.intValue();}
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

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public jade.semantics.actions.SemanticAction sm_action() {return _sm_action;}
    public void sm_action(jade.semantics.actions.SemanticAction o) {_sm_action = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("sm_action") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("sm_action") ) return sm_action();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("sm_action") ) {sm_action((jade.semantics.actions.SemanticAction)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}