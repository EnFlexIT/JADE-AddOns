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
 * KBase.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;

/**
 * Definition of the knowledge base api. 
 * @author Vincent Pautret - France Telecom
 * @version 2004/11/30 Revision: 1.0
 */
public interface KBase {
    
    /**
     * Asserts a formula in the knowledge base.
     * @param formula a formula
     **/
    public void assertFormula(Formula formula);
    
    /**
     * Returns a list <code>ListOfTerm</code> of objects that satisfy a property belonging to 
     * the knowledge base. The method return <code>null</code> if an exception 
     * occurs.
     * @return a list of solutions to the query 
     * @param expression an IdentifyingExpression
     **/
    public ListOfTerm queryRef(IdentifyingExpression expression);
    
    /**
     * Queries the knoweledge base. Returns a list of Bind. These binds are solutions to the query.
     * If the methods returns <code>null</code>, that means there is no solution.
     * @return a list of solutions to the query
     * @param formula a formula 
     **/
    public Bindings query(Formula formula);
    
    /**
     * Adds an observer to the kbase at the end of the list of observers. 
     * @param o the observer to add
     */
    public void addObserver(Observer o);
    
    /**
     * Removes from the kbase all the observers that are identified by the 
     * finder
     * @param finder a finder
     */
    public void removeObserver(Finder finder);
    
    /**
     * Removes from the kowledge base all formulae recognized by the finder
     * @param finder a finder
     */
    public void removeFormula(Finder finder);
    
} // End of interface KBase
