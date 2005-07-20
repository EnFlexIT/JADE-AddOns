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

/*
 * BindingsImpl.java
 * Created on 6 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;



import jade.semantics.lang.sl.grammar.Node;
import jade.util.leap.ArrayList;

/**
 * Class that implements the interface <code>Bindings</code>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/06 Revision: 1.0
 */
public class BindingsImpl extends ArrayList implements Bindings {

    /**
     * @inheritDoc
     */
    public Node getValue(String varName) {
        for (int i =0; i < size(); i++) {
            if (((Bind)get(i)).getVarName().equals(varName)) return ((Bind)get(i)).getVarValue(); 
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public boolean addBind(String varName, Node varValue) {
        if (containsBind(varName)) return false;
        add(new Bind(varName, varValue));
        return true;
    }

    /**
     * @inheritDoc
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

    /**
     * @inheritDoc
     */
    public boolean containsBind(String varName) {
        for (int i = 0; i < size(); i++) {
            if (((Bind)get(i)).getVarName().equals(varName)) return true;
        }
        return false;
    }
    
    /**
     * @inheritDoc
     */
    public boolean equals(Bindings bindings) {
        if (bindings != null) {
            if (this.size() == bindings.size()){
                for (int i = 0; i < bindings.size(); i++) {
                    if (!(bindings.getBind(i).getVarValue().equals(this.getValue(bindings.getBind(i).getVarName())))) return false;
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * @inheritDoc
     */
    public Bind getBind(int index) {
        return (Bind)get(index);
    }
    
    /**
     * @inheritDoc
     */
    public String toString() {
        String result = "";
        for (int i = 0; i < size(); i++) {
            result = result + ((Bind)get(i)).getVarName() + " = " + ((Bind)get(i)).getVarValue() + "\n";
        }
        return result;
    }
} // End of class BindingsImpl
