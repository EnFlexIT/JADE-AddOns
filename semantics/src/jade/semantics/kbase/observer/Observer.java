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
 * Observer.java
 * Created on 11 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.observer;


import jade.semantics.kbase.Bindings;
import jade.semantics.lang.sl.grammar.Formula;



/**
 * Defines an observer. It is used to observe a particular formula in the 
 * knowledge base.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/11 Revision: 1.0 
 */
public interface Observer {
    
    /**
     * Notifies the observer that the value has changed. 
     * @param bindings the new value
     */
    public void notify(Bindings bindings);
    
    /**
     * Returns the observed formula
     * @return the observed formula
     */
    public Formula getObservedFormula();
    
} // End of interface Observer
