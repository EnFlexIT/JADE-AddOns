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

/*
 * BindingsImpl.java
 * Created on 6 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;



import jade.semantics.lang.sl.grammar.Node;
import jade.util.leap.ArrayList;

/**
 * @author Vincent Pautret
 * @version 
 */
public class BindingsImpl extends ArrayList implements Bindings {

    /* (non-Javadoc)
     * @see jade.core.semantics.kbase.Bindings#getValue(java.lang.String)
     */
    public Node getValue(String varName) {
        for (int i =0; i < size(); i++) {
            if (((Bind)get(i)).getVarName().equals(varName)) return ((Bind)get(i)).getVarValue(); 
        }
        return null;
    }

    /* (non-Javadoc)
     * @see jade.core.semantics.kbase.Bindings#addBind(jade.core.semantics.kbase.Bind)
     */
    public boolean addBind(String varName, Node varValue) {
        if (containsBind(varName)) return false;
        add(new Bind(varName, varValue));
        return true;
    }

    /* (non-Javadoc)
     * @see jade.core.semantics.kbase.Bindings#removeBind(java.lang.String)
     */
    public Bindings removeBind(String varName) {
        for (int i = 0; i < size(); i++) {
            Bind b = (Bind)get(i);
            if (b.getVarName().equals(varName)) {
                remove(i);
                return this;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see jade.core.semantics.kbase.Bindings#containsBind(jade.core.semantics.kbase.Bind)
     */
    public boolean containsBind(String varName) {
        for (int i = 0; i < size(); i++) {
            if (((Bind)get(i)).getVarName().equals(varName)) return true;
        }
        return false;
    }

    public boolean equals(Bindings bindings) {
        if (bindings != null) {
            if (this.size() == bindings.size()){
                for (int i = 0; i < bindings.size(); i++) {
                    if (!((Bind)bindings.getBind(i)).getVarValue().equals(this.getValue(((Bind)bindings.getBind(i)).getVarName()))) return false;
                }
                return true;
            }
        }
        return false;
    }

    public Bind getBind(int index) {
        return (Bind)get(index);
    }
    
    public String toString() {
        String result = "";
        for (int i = 0; i < size(); i++) {
            result = result + ((Bind)get(i)).getVarName() + " = " + ((Bind)get(i)).getVarValue() + "\n";
        }
        return result;
    }
} // End of class BindingsImpl
