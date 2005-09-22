
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

public abstract class Content extends Node
{
        public interface Operations extends Node.Operations
        {
            public String toSLString(Content node);
            public Node getContentElement(Content node, int i);
            public void setContentElement(Content node, int i, Node element);
            public void addContentElement(Content node, Node element);
            public void setContentElements(Content node, int number);
            public int contentElementNumber(Content node);
            public jade.semantics.lang.sl.tools.MatchResult match(Content node, Node expression);
            public Node instantiate(Content node, String varname, Node expression);
        }
        public String toSLString()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Content.Operations)_thisoperations).toSLString(this );
        }
        public Node getContentElement(int i)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Content.Operations)_thisoperations).getContentElement(this , i);
        }
        public void setContentElement(int i, Node element)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((Content.Operations)_thisoperations).setContentElement(this , i, element);
        }
        public void addContentElement(Node element)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((Content.Operations)_thisoperations).addContentElement(this , element);
        }
        public void setContentElements(int number)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((Content.Operations)_thisoperations).setContentElements(this , number);
        }
        public int contentElementNumber()
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Content.Operations)_thisoperations).contentElementNumber(this );
        }
        public jade.semantics.lang.sl.tools.MatchResult match(Node expression)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Content.Operations)_thisoperations).match(this , expression);
        }
        public Node instantiate(String varname, Node expression)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((Content.Operations)_thisoperations).instantiate(this , varname, expression);
        }
    static int _as_expressions = 0;

    public Content(int capacity, ListOfContentExpression as_expressions)  {
      super (capacity);
        as_expressions(as_expressions);
    }

    public void copyValueOf(Node n) {
        if (n instanceof Content) {
            super.copyValueOf(n);
            Content tn = (Content)n;
        }
    }
    public ListOfContentExpression as_expressions() {return (ListOfContentExpression)_nodes[_as_expressions];}
    public void as_expressions(ListOfContentExpression s) {_nodes[_as_expressions] = s;}
}