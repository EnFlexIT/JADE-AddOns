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
 * BeliefTransfer.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.SemanticRepresentationImpl;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle expresses a necessary cooperation principle of the Jade
 * agent towards the beliefs that the sender (of an ACL message) intends to
 * communicate. Typically, this step is used to interpret incoming <code>Inform</code>
 * messages. 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class BeliefTransfer extends SemanticInterpretationPrincipleImpl {
    
    /**
     * Pattern used to test the applicability of the deductive step
     */
    Formula beliefPattern; 

    /**
     * Pattern used to create the new <code>SemanticRepresentation</code>
     */
    Formula applyPattern;

	 /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param agent agent that owns of the semantic interpretation principle
     */
    public BeliefTransfer(SemanticAgent agent) {
        setAgent(agent);
        beliefPattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " (I ??sender (B " + agent.getAgentName() + " ??phi)))");
        applyPattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " ??phi)");
    } // End of BeliefTransfer/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    /**
     * Creates a new semantic representation.
     * @see jade.semantics.interpreter.SemanticInterpretationPrincipleImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = SLPatternManip.match(beliefPattern,sr.getSLRepresentation());
            if (matchResult != null) {
                Formula phi = matchResult.getFormula("??phi");
                if (agent.getMyStandardCustomization().acceptBeliefTransfer(phi,matchResult.getTerm("??sender"))) {
    	            SemanticRepresentation newSR = new SemanticRepresentationImpl();
    	            newSR.setMessage(sr.getMessage());
    	            newSR.setSLRepresentation(
    	                    ((Formula)SLPatternManip.instantiate(applyPattern, "??phi", phi)).getSimplifiedFormula());
                    ArrayList listOfSR = new ArrayList();
    	            listOfSR.add(newSR);
    	            phi = null;
    	            return listOfSR;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }

        return null;
    } // End of apply/1
    
} // End of class BeliefTransfer
