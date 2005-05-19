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
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.actions.SemanticAction;
import jade.semantics.behaviours.IntentionalBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle consists in adding to the Jade agent a Jade Bahaviour
 * performing the targeted semantic action <i>act</i>.   
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class ActionPerformance extends SemanticInterpretationPrincipleImpl {

    /**
     * Pattern used to test the applicability of the semantic interpretation principle
     */
    Formula pattern;
	
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param agent agent that owns of the semantic interpretation principle
     */
    public ActionPerformance(SemanticAgent agent) {
        setAgent(agent);
        pattern = SLPatternManip.fromFormula("(I " + agent.getAgentName() + " (done ??act true))");
    } // End of ActionPerformance/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * Adds a new behaviour.
     * @see jade.semantics.interpreter.SemanticInterpretationPrincipleImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = SLPatternManip.match(pattern,sr.getSLRepresentation());
            if (matchResult != null) {
                SemanticAction act = agent.getMySemanticActionTable().getSemanticActionInstance(matchResult.getActionExpression("??act"));
                if (act != null) {
    	            act.getBehaviour().setAgent(getAgent());
                    
    	            potentiallyAddBehaviour(
                            new IntentionalBehaviour((SemanticBehaviour)act.getBehaviour(),
                                    sr.getSLRepresentation(),
                                    4));
//                    System.out.println("**** Action performance ajoute une formule");
//                    potentiallyAssertFormula(sr.getSLRepresentation());

    	            return new ArrayList();
    	        } 
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1

} // End of class ActionPerformance
