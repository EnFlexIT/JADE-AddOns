
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

public class FunctionalTermParamNode extends FunctionalTerm
{
    public final int getNodeID(){
        return 44;
    }
        public interface Operations extends Term.Operations
        {
            public Term getParameter(FunctionalTermParamNode node, String name);
            public void setParameter(FunctionalTermParamNode node, String name, Term term);
        }
        public Term getParameter(String name)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            return((FunctionalTermParamNode.Operations)_thisoperations).getParameter(this , name);
        }
        public void setParameter(String name, Term term)
        {
            
            if ( _thisoperations == null ) {
                _thisoperations = getOperations();
            }
            ((FunctionalTermParamNode.Operations)_thisoperations).setParameter(this , name, term);
        }
    static int _as_parameters = 1;

    public FunctionalTermParamNode(Symbol as_symbol, ListOfParameter as_parameters)  {
        super(2, as_symbol);
        as_parameters(as_parameters);
        initNode();
    }

    public FunctionalTermParamNode() {
        super(2, null);
        as_parameters(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitFunctionalTermParamNode(this);}
    public Node getClone() {
        Node clone = new FunctionalTermParamNode(null, null);
        clone.copyValueOf(this);
        return clone;
    }
    public void copyValueOf(Node n) {
        if (n instanceof FunctionalTermParamNode) {
            super.copyValueOf(n);
            FunctionalTermParamNode tn = (FunctionalTermParamNode)n;
        }
        initNode();
    }
    public ListOfParameter as_parameters() {return (ListOfParameter)_nodes[_as_parameters];}
    public void as_parameters(ListOfParameter s) {_nodes[_as_parameters] = s;}
}