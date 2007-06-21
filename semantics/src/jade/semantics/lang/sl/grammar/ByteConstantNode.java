
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

public class ByteConstantNode extends StringConstant
{
    public static Integer ID = new Integer(38);
    public final int getClassID() {return ID.intValue();}
    byte[] _lx_value;

    public ByteConstantNode(byte[] lx_value)  {
        super(0);
        lx_value(lx_value);
    }

    public ByteConstantNode() {
        super(0);
        lx_value(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitByteConstantNode(this);}

    public Node getClone() {
        Node clone = new ByteConstantNode();
        clone.copyValueOf(this);
        return clone;
    }

    public void copyValueOf(Node n) {
        if (n instanceof ByteConstantNode) {
            super.copyValueOf(n);
            ByteConstantNode tn = (ByteConstantNode)n;
            _lx_value= tn._lx_value;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    public byte[] lx_value() {return _lx_value;}
    public void lx_value(byte[] o) {_lx_value = o;}

    public boolean hasAttribute(String attrname) {
        if ( attrname.equals("lx_value") ) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(String attrname) {
        if ( attrname.equals("lx_value") ) return lx_value();
        return super.getAttribute(attrname);
    }

    public void setAttribute(String attrname, Object attrvalue) {
        if ( attrname.equals("lx_value") ) {lx_value((byte[])attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}