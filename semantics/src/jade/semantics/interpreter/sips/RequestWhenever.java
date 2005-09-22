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
 * RequestWhenever.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This principle is intended to be applied when an agent receives a RequestWhenever 
 * message.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/14 Revision: 1.0
 */
public class RequestWhenever extends Subscription {
    
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    
    /**
     * Constructor of the principle with a specified subscribe property.
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     * @param subscribeProperty precondition to perform the action
    */
    public RequestWhenever(SemanticCapabilities capabilities, String subscribeProperty) {
        super(capabilities, "(or (not (B ??agent " + subscribeProperty + " )) " +
                "    (or (I ??subscriber ??goal)" +
                "        (forall ??e (not (done ??e (not (B ??agent " + subscribeProperty + " )))))))", false);
    } // End of RequestWhenever/1
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public RequestWhenever(SemanticCapabilities capabilities) {
        this(capabilities, "??property");
    } // End of RequestWhenever/1
    
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    protected Formula computeEventToExecute(MatchResult applyResult) throws WrongTypeException {
        return applyResult.getFormula("goal");
    }
    
    /**
     *@inheritDoc 
     */
    
    protected Formula computePropertyToObserve(MatchResult applyResult) throws WrongTypeException {
        return applyResult.getFormula("property"); 
    }
} // End of class RequestWhenever
