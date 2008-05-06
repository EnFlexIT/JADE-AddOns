
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

public class ObligationNode extends InstitutionalLogicFormula
{
    public static Integer ID = new Integer(14);
    public final int getClassID() {return ID.intValue();}
    static int _as_formula = 0;

    public ObligationNode(Formula as_formula)  {
        super(1);
        as_formula(as_formula);
        initNode();
    }

    public ObligationNode() {
        super(1);
        as_formula(null);
        initNode();
    }

    public void accept(Visitor visitor) {visitor.visitObligationNode(this);}

    public Node getClone(HashMap clones) {
        Node clone = new ObligationNode(null);
        clone.copyValueOf(this, clones);
        return clone;
    }

    public void copyValueOf(Node n, HashMap clones) {
        if (n instanceof ObligationNode) {
            super.copyValueOf(n, clones);
            ObligationNode tn = (ObligationNode)n;
        }
        initNode();
    }


    public Node.Operations getOperations() {
        Node.Operations result = (Node.Operations)_operations.get(ID);
        if ( result == null ) {result = super.getOperations();}
        return result;
    }
    static public int as_formula_ID = new String("as_formula").hashCode();
    public Formula as_formula() {return (Formula)_nodes[_as_formula];}
    public void as_formula(Formula s) {_nodes[_as_formula] = s;}

    public boolean hasAttribute(int attrname) {
        if ( attrname == as_formula_ID) return true;
        return super.hasAttribute(attrname);
    }

    public Object getAttribute(int attrname) {
        if ( attrname == as_formula_ID) return as_formula();
        return super.getAttribute(attrname);
    }

    public void setAttribute(int attrname, Object attrvalue) {
        if ( attrname == as_formula_ID) {as_formula((Formula)attrvalue);return;}
        super.setAttribute(attrname, attrvalue);
    }
}