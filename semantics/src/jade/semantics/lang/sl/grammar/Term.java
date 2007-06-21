
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

public abstract class Term extends Node
{
        public interface Operations extends Node.Operations
        {
            public Term getSimplifiedTerm(Term node);
            public void simplify(Term node);
            public jade.semantics.lang.sl.tools.MatchResult match(Term node, Node expression);
            public Term instantiate(Term node, String varname, Node expression);
        }
        public Term getSimplifiedTerm()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Term.Operations)_thisoperations).getSimplifiedTerm(this );
        }
        public void simplify()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((Term.Operations)_thisoperations).simplify(this );
        }
        public jade.semantics.lang.sl.tools.MatchResult match(Node expression)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Term.Operations)_thisoperations).match(this , expression);
        }
        public Term instantiate(String varname, Node expression)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Term.Operations)_thisoperations).instantiate(this , varname, expression);
        }
    public static Integer ID = new Integer(10013);
    public int getClassID() {return ID.intValue();}
    Term _sm_simplified_term;

    public Term(int capacity)  {
      super (capacity);
    }

    public void copyValueOf(Node n) {
        if (n instanceof Term) {
            super.copyValueOf(n);
            Term tn = (Term)n;
            _sm_simplified_term= tn._sm_simplified_term;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public Term sm_simplified_term() {return _sm_simplified_term;}
    public void sm_simplified_term(Term o) {_sm_simplified_term = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("sm_simplified_term") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("sm_simplified_term") ) return sm_simplified_term();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("sm_simplified_term") ) {sm_simplified_term((Term)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}