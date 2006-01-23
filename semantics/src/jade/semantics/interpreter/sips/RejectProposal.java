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
 * RejectProposal.java
 * Created on 11 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This principle is intended to be applied if an agent is no longer interested
 * in the Jade agent doing an action under a condition. The Jade agent may then
 * drop his behaviour that currently seeks performing this action. This principle
 *  may be apply when the Jade agent receives a <code>Cancel</code> or a 
 *  <code>RejectProposal</code> message.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/11 Revision: 1.0
 */
public class RejectProposal extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern that must match to apply the principle
     */
    private Formula pattern;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public RejectProposal(SemanticCapabilities capabilities) {
        super(capabilities);
        pattern = SLPatternManip.fromFormula("(not (I ??agentI (done (action " + myCapabilities.getAgentName() + " ??act) ??condition)))");
    } // End of CancelMyActionFilter/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * Calls {@link jade.semantics.interpreter.StandardCustomization#handleRejectProposal(Term, ActionExpression, Formula)} method of the 
     * <code>StandardCustomization</code> class.
     * @param sr a semantic representation
     * @return if the pattern (not (I ??agentI (done (action ??agent ??act) ??condition)))
     * matches with the incoming SR, and the term ??agent is the current agent and different of ??agentI 
     * and the handleRejectProposal
     * method returns true, this method returns an empty ArrayList. Returns null
     * in the other cases. 
     * @throws SemanticInterpretationPrincipleException if any exception occurs
     */
    public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        try {
            MatchResult applyResult = SLPatternManip.match(pattern,sr.getSLRepresentation());
            if (applyResult != null  
                    && !myCapabilities.getAgentName().equals(applyResult.getTerm("agentI"))
                    && (myCapabilities.getMyStandardCustomization().handleRejectProposal(applyResult.getTerm("agentI"), 
                            (ActionExpression)applyResult.getTerm("act"), 
                            applyResult.getFormula("condition"))))
            {
                return new ArrayList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1
    
} // End of class RejectProposal