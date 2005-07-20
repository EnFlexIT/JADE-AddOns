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
 * Bindings.java
 * Created on 5 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.lang.sl.grammar.Node;

/**
 * Interface that provides methods to manage a structure of <code>Bind</code>.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/05 Revision: 1.0
 */
public interface Bindings {
    
    /**
     * Returns the value corresponding to the given variable name
     * @param varName a variable name
     * @return the value corresponding to the given variable name
     */
    public Node getValue(String varName);
    
    /**
     * Adds a new <code>Bind</code> in the structure.
     * @param varName variable name 
     * @param varValue variable value
     * @return true if the adding is correct, false if not.
     */
    public boolean addBind(String varName, Node varValue);
    
    /**
     * Removes the <code>Bind</code> from the structure corresponding to the given variable name.
     * @param varName a variable name
     * @return the resulting list
     */
    public Bindings removeBind(String varName);
    
    /**
     * Returns true if the structure contains a Bind that has the given variable
     * name.
     * @param varName a variable name
     * @return true if the structure contains a Bind that has the given variable name,
     * false if not
     */
    public boolean containsBind(String varName);
    
    /**
     * Returns the size of the structure.
     * @return the size of the structure
     */
    public int size();
    
    /**
     * Returns true if two <code>Bindings</code> are equals, false if not.
     * @param bindings a <code>Bindings</code>
     * @return true if two <code>Bindings</code> are equals, false if not.
     */
    public boolean equals(Bindings bindings);
   
    /**
     * Returns the Bind at the specified index.
     * @param index an index
     * @return a Bind
     */
    public Bind getBind(int index);
} // End of interface Bindings
