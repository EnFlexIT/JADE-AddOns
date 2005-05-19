
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

public class ListOfVariable extends ListOfNodes
{
    public ListOfVariable(Variable[] nodes) {
        super(nodes);
    }

    public ListOfVariable() {
        super();
    }

    public Variable element(int i) {return (Variable)super.get(i);}
    public void append(Variable e) {add(e);}
    public void prepend(Variable e) {add(0,e);}
    public Variable last() {return (Variable)super.getLast();}
    public Variable first() {return (Variable)super.getFirst();}
    public void accept(Visitor visitor) {visitor.visitListOfVariable(this);}
    public Node getClone() {
        Node clone = new ListOfVariable();
        clone.copyValueOf(this);
        return clone;
    }
}