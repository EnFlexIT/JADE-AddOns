
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

public class MetaVariableReferenceNode extends Variable
{
    public static Integer ID = new Integer(32);
    public final int getClassID() {return ID.intValue();}
    Variable _sm_value;

    public MetaVariableReferenceNode(java.lang.String lx_name)  {
        super(0, lx_name);
    }

    public MetaVariableReferenceNode() {
        super(0, null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitMetaVariableReferenceNode(this);}

    public Node getClone() {
        Node clone = new MetaVariableReferenceNode();
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof MetaVariableReferenceNode) {
            super.copyValueOf(n);
            MetaVariableReferenceNode tn = (MetaVariableReferenceNode)n;
            _sm_value= tn._sm_value;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public Variable sm_value() {return _sm_value;}
    public void sm_value(Variable o) {_sm_value = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("sm_value") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("sm_value") ) return sm_value();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("sm_value") ) {sm_value((Variable)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}