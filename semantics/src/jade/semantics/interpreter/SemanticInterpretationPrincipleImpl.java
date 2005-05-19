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
 * SemanticInterpretationPrincipleImpl.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.semantics.behaviours.SemanticInterpreterBehaviour;
import jade.semantics.lang.sl.grammar.Formula;
import jade.util.leap.ArrayList;

/**
 * Class that implements the semantic interpretation principles.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public abstract class SemanticInterpretationPrincipleImpl implements SemanticInterpretationPrinciple {

    /**
     * Owner of the Deductive Step
     */
    protected SemanticAgent agent;
    
    /**
     * This exception is thrown when the <code>SemanticRepresentation</code> 
     * of the method "apply" is different from that on which the deductive step
     * is applicable
     */
    public static class SemanticInterpretationPrincipleException extends Exception {}
    /**************************************************************************/
    /**									ABSTRACT METHODS					 **/
    /**************************************************************************/

    /**
    * Tries to apply the semantic interpretation principle. 
    * It returns an ArrayList that contains SR produced 
    * by the semantic interpretation principle. This list could be empty. It returns null if the
    * principle is not applicable
    * @param sr a semantic representation
    * @return a list of SR produced by the deductive step
    * @throws SemanticInterpretationPrincipleException if an exception occurs
    **/
    public abstract ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException;

    /**************************************************************************/
    /**									PUBLIC METHODS						 **/
    /**************************************************************************/

    /**
     * @return Returns the agent.
     */
    public Agent getAgent() {
        return agent;
    } // End of getAgent/0
    
    /**
     * @param agent The agent to set.
     */
    public void setAgent(SemanticAgent agent) {
        this.agent = agent;
    } // End of setAgent/1
    
    /**
     * Adds a behaviour to the agent list
     * @param b a behaviour
     */
    public void potentiallyAddBehaviour(Behaviour b) {
        ((SemanticInterpreterBehaviour)agent.getInterpretationAlgorithm()).getBehaviourToAdd().add(b);
    } // End of addProvisoryBehaviour/1
    
    /**
     * Removes a behaviour from the list of behaviour
     * @param b a behaviour
     */
    public void potentiallyRemoveBehaviour(Behaviour b) {
        ((SemanticInterpreterBehaviour)agent.getInterpretationAlgorithm()).getBehaviourToRemove().add(b);
    } // End of removeProvisoryBehaviour/1
    
    public void potentiallyAssertFormula(Formula formula) {
        ((SemanticInterpreterBehaviour)agent.getInterpretationAlgorithm()).getFormulaToAssert().add(formula);
    }
} // End of the class SemanticInterpretationPrincipleImpl
