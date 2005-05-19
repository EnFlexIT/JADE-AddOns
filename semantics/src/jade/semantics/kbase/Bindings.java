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
 * @author Vincent Pautret
 * @version 
 */
public interface Bindings {
    
    /**
     * 
     * @param varName
     * @return
     */
    public Node getValue(String varName);
    
    /**
     * 
     * @param varName
     * @param varValue
     * @return
     */
    public boolean addBind(String varName, Node varValue);
    
    /**
     * 
     * @param varName
     * @return
     */
    public Bindings removeBind(String varName);
    
    /**
     * 
     * @param varName
     * @return
     */
    public boolean containsBind(String varName);
    
    /**
     * 
     * @return
     */
    public int size();
    
    /**
     * 
     * @param bindings
     * @return
     */
    public boolean equals(Bindings bindings);
   
    /**
     * 
     * @param index
     * @return
     */
    public Bind getBind(int index);
} // End of interface Bindings
