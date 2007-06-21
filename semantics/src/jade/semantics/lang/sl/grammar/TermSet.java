
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

public abstract class TermSet extends Term
{
        public interface Operations extends Term.Operations
        {
            public void addTerm(TermSet node, Term term);
            public void removeTerm(TermSet node, Term term);
            public Term getTerm(TermSet node, int i);
            public int size(TermSet node);
        }
        public void addTerm(Term term)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((TermSet.Operations)_thisoperations).addTerm(this , term);
        }
        public void removeTerm(Term term)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((TermSet.Operations)_thisoperations).removeTerm(this , term);
        }
        public Term getTerm(int i)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((TermSet.Operations)_thisoperations).getTerm(this , i);
        }
        public int size()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((TermSet.Operations)_thisoperations).size(this );
        }
    public static Integer ID = new Integer(10016);
    public int getClassID() {return ID.intValue();}
    static int _as_terms = 0;

    public TermSet(int capacity, ListOfTerm as_terms)  {
      super (capacity);
        as_terms(as_terms);
    }

    public void copyValueOf(Node n) {
        if (n instanceof TermSet) {
            super.copyValueOf(n);
            TermSet tn = (TermSet)n;
        }
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public ListOfTerm as_terms() {return (ListOfTerm)_nodes[_as_terms];}
    public void as_terms(ListOfTerm s) {_nodes[_as_terms] = s;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("as_terms") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("as_terms") ) return as_terms();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("as_terms") ) {as_terms((ListOfTerm)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}