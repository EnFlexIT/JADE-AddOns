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
 * Planning.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.behaviours.IntentionalBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This deductive step provides the Jade agent with a general mean of planning.
 * It calls an external component that returns a Jade Bhaviour that implements 
 * a way to reach an input goal <i>phi</i>, and adds this Behaviour to the 
 * agent.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class Planning extends SemanticInterpretationPrincipleImpl {

 
    Formula pattern;
	
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param agent agent that owns of the Deductive Step
     */
    public Planning(SemanticAgent agent) {
        setAgent(agent);
        pattern = SLPatternManip.fromFormula("(I ??agentName ??phi)");
    } // End of Planning/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @see jade.semantics.interpreter.SemanticInterpretationPrincipleImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr) {
        if (agent.getMyPlanner() != null) {
            potentiallyAddBehaviour(new IntentionalBehaviour(
                    (SemanticBehaviour)agent.getMyPlanner().findPlan(sr.getSLRepresentation()),
                    sr.getSLRepresentation(),
                    6));
           // potentiallyAssertFormula(sr.getSLRepresentation());
            return new ArrayList();
        }
        return null;
    } // End of apply/1
} // End of class Planning
