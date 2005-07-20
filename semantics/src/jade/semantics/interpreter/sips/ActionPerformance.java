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
 * ActionPerformance.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.actions.SemanticAction;
import jade.semantics.behaviours.IntentionalBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle consists in adding to the Jade agent a Jade Behaviour
 * performing the targeted semantic action <i>act</i>.   
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class ActionPerformance extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern used to test the applicability of the semantic interpretation 
     * principle
     */
    private Formula pattern;
    
    /**
     * Pattern used to test the applicability of the semantic interpretation 
     * principle
     */
    private Formula bPattern; 
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities agent that owns of the semantic interpretation principle
     */
    public ActionPerformance(SemanticCapabilities capabilities) {
        super(capabilities);
        pattern = SLPatternManip.fromFormula("(I " + myCapabilities.getAgentName() + " (done ??act ??phi))");
        bPattern = SLPatternManip.fromFormula("(B " + myCapabilities.getAgentName() + "??phi)");
    } // End of ActionPerformance/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Adds a new intentional behaviour {@link IntentionalBehaviour}if it is applicable.
     * @inheritDoc
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = SLPatternManip.match(pattern,sr.getSLRepresentation());
            if (matchResult != null) {
                if (myCapabilities.getMyKBase().query(((Formula)SLPatternManip.instantiate(bPattern, "phi", matchResult.getFormula("phi"))).getSimplifiedFormula()) != null) {
                    SemanticAction act = myCapabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpression)matchResult.getTerm("act"));
                    
                    if (act != null) {
                        // 	            act.getBehaviour().setAgent(getAgent());
                        potentiallyAddBehaviour(
                                new IntentionalBehaviour((SemanticBehaviour)act.getBehaviour(),
                                        sr.getSLRepresentation(),
                                        getOrderIndex(),
                                        sr.getDataToFeedback()));
                        ArrayList result = new ArrayList();
                        sr.setSemanticInterpretationPrincipleIndex(myCapabilities.getMySemanticInterpretationTable().size() + 1);
                        result.add(sr);
                        return result;
                    }
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of apply/1
    
} // End of class ActionPerformance
