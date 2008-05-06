
/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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

//#PJAVA_EXCLUDE_BEGIN
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;
//#PJAVA_EXCLUDE_END
/*#PJAVA_INCLUDE_BEGIN
import jade.util.leap.Collection;
import jade.util.leap.Comparable;
import jade.util.leap.LinkedList;
import jade.util.leap.List;
import jade.util.leap.HashMap;
#PJAVA_INCLUDE_END*/

public class MetaTermReferenceNode extends Term
{
    public static Integer ID = new Integer(29);
    public final int getClassID() {return ID.intValue();}
    java.lang.String _lx_name;
    Term _sm_value;

    public MetaTermReferenceNode(java.lang.String lx_name)  {
        super(0);
        lx_name(lx_name);
    }

    public MetaTermReferenceNode() {
        super(0);
        lx_name(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitMetaTermReferenceNode(this);}

    public Node getClone(HashMap clones) {
        Node clone = new MetaTermReferenceNode();
        clone.copyValueOf(this, clones);
        return clone;
    }

    public void copyValueOf(Node n, HashMap clones) {
        if (n instanceof MetaTermReferenceNode) {
            super.copyValueOf(n, clones);
            MetaTermReferenceNode tn = (MetaTermReferenceNode)n;
            lx_name( tn._lx_name);
            sm_value( tn._sm_value);
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    static public int lx_name_ID = new String("lx_name").hashCode();
    public java.lang.String lx_name() {return _lx_name;}
    public void lx_name(java.lang.String o) {_lx_name = o;}
    static public int sm_value_ID = new String("sm_value").hashCode();
    public Term sm_value() {return _sm_value;}
    public void sm_value(Term o) {_sm_value = o;}

    public boolean hasAttribute(int attrname) {
        if ( attrname == lx_name_ID) return true;
        if ( attrname == sm_value_ID) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(int attrname) {
        if ( attrname == lx_name_ID) return lx_name();
        if ( attrname == sm_value_ID) return sm_value();
        return super.getAttribute(attrname);
    }

    public void setAttribute(int attrname, Object attrvalue) {
        if ( attrname == lx_name_ID) {lx_name((java.lang.String)attrvalue);return;}
        if ( attrname == sm_value_ID) {sm_value((Term)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}