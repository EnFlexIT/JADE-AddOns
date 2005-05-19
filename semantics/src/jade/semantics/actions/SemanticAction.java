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
 * SemanticAction.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.core.behaviours.Behaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;


/**
 * SemanticAction is the interface of all semantic actions that a Semantic Agent
 * may perform. Each semantic action must provide the following pieces of 
 * information, to be usable by the semantic agent interpreter:
 * <ul>
 * <li> Feasibility precondition (an SL formula): represents a condition that 
 * must hold for an agent to be able to perform the action.
 * <li> Persistent feasibility precondition (an SL formula): represents the
 *  subset of the feasibility precondition that necessarily persists just after
 *  the performance of the action. 
 * <li>Rational effect (an SL formula): represents a state intented by the agent
 *  performing the action.
 * <li>Poscondition (an SL formula): represents the effect that the performing 
 * agent considers to be true just after the performance of the action.
 * <li>A behaviour implementing the performance of this action by the agent.
 * </ul>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public interface SemanticAction {

    /*********************************************************************/
    /**				 			ACCESSOR AND MUTATOR					**/
    /*********************************************************************/
    
    /**
     * @return Returns a Term that represents the author of the action, or null if not applicable
     */
    public Term getSender();

    /**
     * @return Returns the feasibility precondition.
     **/
    public Formula getFeasibilityPrecondition();
     
    /**
     * @return Returns the rational effect of the action
     **/
    public Formula getRationalEffect();
      
    /**
     * @return Returns the persitentFeasibilityPrecondition.
     */
    public Formula getPersistentFeasibilityPrecondition();
      
    /**
     * @return Returns the postcondition of the action
     **/
    public Formula getPostCondition();

    /**
     * @return Returns the behaviour of the action
     */
    public Behaviour getBehaviour();

    /********************************************************************/
    /** 			PUBLIC METHODS
    /********************************************************************/

    /**
     * Creates a new instancied instance of the action based on the specified 
     * action expression.
     * @param actionExpression an action expresison
     * @return an instancied semantic action
     */
    public SemanticAction newAction(ActionExpression actionExpression);
    
    /**
     * Creates a new instancied instance of the action based on the specified 
     * rational effect.
     * @param rationalEffect a formula that specified the rational effet
     * @param agentName the SL representation name of the agent that 
     * performs the action
     * @return an instancied semantic actio
     */
    public SemanticAction newAction(Formula rationalEffect, Term agentName);
    
} // End of interface SemanticAction
