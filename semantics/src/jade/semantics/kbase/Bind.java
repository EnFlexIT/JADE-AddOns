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
 * Bind.java
 * Created on 5 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.lang.sl.grammar.Node;

/**
 * Stores the name of a meta-variable and one of its associated value. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/05 Revision: 1.0
 */
public class Bind {
    
    /**
     * Name of a variable
     */
    private String varName;
    
    /**
     * Value of a variable
     */
    private Node varValue;
    
    /**
     * Creates a new Bind
     * @param name name of the variable
     * @param value value of the variable
     */
    public Bind(String name, Node value) {
        varName = name;
        varValue = value;
    }
    
    /**
     * Returns the variable name
     * @return Returns the variable name.
     */
    public String getVarName() {
        return varName;
    }
    /**
     * Sets the variable name
     * @param varName The variable name to set.
     */
    public void setVarName(String varName) {
        this.varName = varName;
    }
    /**
     * Returns the variable value 
     * @return Returns the variable value.
     */
    public Node getVarValue() {
        return varValue;
    }
    /**
     * Sets the variable value
     * @param varValue The varValue to set.
     */
    public void setVarValue(Node varValue) {
        this.varValue = varValue;
    }
    
    /**
     * @return the String representation of a bind: (name + value)
     */
    public String toString() {
        return varName + " = " + varValue;
    }
} // End of class Bind
