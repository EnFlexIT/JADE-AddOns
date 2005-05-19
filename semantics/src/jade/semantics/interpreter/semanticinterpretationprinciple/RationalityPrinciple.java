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
 * RationalityPrinciple.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.operators.Alternative;
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
 * This deductive step enables the Jade agent to compute some trivial planning 
 * according to the rationality principle. It looks for all the semantic actions
 * available to the Jade agent, the rationnal effect of which matches the 
 * intention <i>phi</i> of the Jade agent. It then builds (and adds to the agent)
 * a Jade Behaviour implementing one of these actions (which is represented by
 * an alternative action expression).
 * @author Vincent Pautret
 * @version $Date: 2004/11/30 17:00:00 $ $Revision: 1.0 $
 */
public class RationalityPrinciple extends SemanticInterpretationPrincipleImpl {

    /**
     * Pattern used to test the applicability of the deductive step
     */
    Formula rationalityPattern;
	
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param agent agent that owns of the Deductive Step
     */
    public RationalityPrinciple(SemanticAgent agent) {
        setAgent(agent);
        rationalityPattern = SLPatternManip.fromFormula("(I " + agent.getAgentName() + " ??phi)");
    } // End of RationalityPrinciple/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * Adds a new behaviour.
     * @see jade.semantics.interpreter.SemanticInterpretationPrincipleImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = SLPatternManip.match(rationalityPattern,sr.getSLRepresentation());
            if (matchResult != null) {
                Formula phi = matchResult.getFormula("??phi");
                ArrayList actionList = new ArrayList();
                agent.getMySemanticActionTable().getSemanticActionInstance(actionList, phi, agent.getAgentName());
                if (actionList.size() > 0) {
                    SemanticAction alternative = new Alternative(((SemanticAgent)agent).getMySemanticActionTable()).newAction(actionList);
                    if (alternative != null) {
                        alternative.getBehaviour().setAgent(getAgent());
                        potentiallyAddBehaviour(new IntentionalBehaviour(
                                (SemanticBehaviour)alternative.getBehaviour(),
                                sr.getSLRepresentation(),
                                5));
                        return new ArrayList();
                    } 
                }
                return null;
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1

} // End of class RationalityPrinciple
