
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

public class RealConstantNode extends Constant
{
    public static Integer ID = new Integer(34);
    public final int getClassID() {return ID.intValue();}
    java.lang.Double _lx_value;

    public RealConstantNode(java.lang.Double lx_value)  {
        super(0);
        lx_value(lx_value);
    }

    public RealConstantNode() {
        super(0);
        lx_value(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitRealConstantNode(this);}

    public Node getClone() {
        Node clone = new RealConstantNode();
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof RealConstantNode) {
            super.copyValueOf(n);
            RealConstantNode tn = (RealConstantNode)n;
            _lx_value= tn._lx_value;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public java.lang.Double lx_value() {return _lx_value;}
    public void lx_value(java.lang.Double o) {_lx_value = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("lx_value") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("lx_value") ) return lx_value();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("lx_value") ) {lx_value((java.lang.Double)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}