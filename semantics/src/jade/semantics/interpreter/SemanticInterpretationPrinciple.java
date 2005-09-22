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

import jade.core.behaviours.Behaviour;
import jade.semantics.lang.sl.grammar.Formula;
import jade.util.leap.ArrayList;

/**
 * Abstract class that represents a semantic interpretation principle.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class SemanticInterpretationPrinciple {
    
    /**
     * Owner of the Semantic interpretation principle
     */
    protected SemanticCapabilities myCapabilities;
    
    /**
     * The order index of this semantic interpretation principle used by the 
     * load process
     */
    private int orderIndex;
    
    /**************************************************************************/
    /**									CONSTRUCTOR		 			         **/
    /**************************************************************************/
    /**
     * Creates a new SemanticInterpretationPrinciple.
     * @param capabilities capabilities of the owner (the agent) of the semantic
     * interpretation principle
     */
    protected SemanticInterpretationPrinciple(SemanticCapabilities capabilities) {
        myCapabilities = capabilities;
    } // End of SemanticInterpretationPrinciple/1
    
    /**
     * This exception is thrown when the <code>SemanticRepresentation</code> 
     * of the method "apply" is different from that on which the semantic 
     * interpretation principle is applicable
     */
    public static class SemanticInterpretationPrincipleException extends Exception {}
    /**************************************************************************/
    /**									ABSTRACT METHODS					 **/
    /**************************************************************************/
    
    /**
     * Tries to apply the semantic interpretation principle. 
     * It returns an <code>ArrayList</code> that contains <code>SemanticRepresentation</code> produced 
     * by the semantic interpretation principle. This list could be empty. 
     * It returns <code>null</code> if the principle is not applicable.
     * @param sr a semantic representation
     * @return a list of SR produced by the semantic interpretation principle
     * @throws SemanticInterpretationPrincipleException if an exception occurs
     **/
    public abstract ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException;
    
    /**************************************************************************/
    /**									PUBLIC METHODS						 **/
    /**************************************************************************/
    
    /**
     * Returns the order index of the principle
     * @return the order index.
     */
    public int getOrderIndex() {
        return orderIndex;
    } // End of getOrderIndex/0
    
    /**
     * Sets the order index of the principle
     * @param orderIndex The orderIndex to set.
     */
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    } // End of setOrderIndex/1
    
    /**
     * Adds a behaviour to the agent list
     * @param b a behaviour
     */
    public void potentiallyAddBehaviour(Behaviour b) {
        myCapabilities.getSemanticInterpreterBehaviour().getBehaviourToAdd().add(b);
    } // End of addProvisoryBehaviour/1
    
    /**
     * Removes a behaviour from the list of behaviour
     * @param b a behaviour
     */
    public void potentiallyRemoveBehaviour(Behaviour b) {
        myCapabilities.getSemanticInterpreterBehaviour().getBehaviourToRemove().add(b);
    } // End of removeProvisoryBehaviour/1
    
    /**
     * Asserts a formula in the knowledge base
     * @param formula the formula to be added
     */
    public void potentiallyAssertFormula(Formula formula) {
        myCapabilities.getSemanticInterpreterBehaviour().getFormulaToAssert().add(formula);
    } // End of potentiallyAssertFormula/1
} // End of class SemanticInterpretationPrincipleImpl
