
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

public class ListOfTerm extends ListOfNodes
{
    public static Integer ID = new Integer(10012);
    public int getClassID() {return ID.intValue();}
    public ListOfTerm(Term[] nodes) {
        super(nodes);
    }

    public ListOfTerm() {
        super();
    }

    public Term element(int i) {return (Term)super.get(i);}
    public void append(Term e) {add(e);}
    public void prepend(Term e) {add(0,e);}
    public Term last() {return (Term)super.getLast();}
    public Term first() {return (Term)super.getFirst();}
    public void accept(Visitor visitor) {visitor.visitListOfTerm(this);}
    public Node getClone() {
        Node clone = new ListOfTerm();
        clone.copyValueOf(this);
        return clone;
    }

    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
}