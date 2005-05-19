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
 * CancelMyActionFilter.java
 * Created on 11 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * @author Vincent Pautret
 * @version 
 */
public class CancelMyAction extends SemanticInterpretationPrincipleImpl {

     /**
     * Pattern that must match to apply the filter
     */
    Formula pattern;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * 
     */
    public CancelMyAction(SemanticAgent agent) {
        setAgent(agent);
        pattern = SLPatternManip.fromFormula("(not (I ??agentI (done (action " + agent.getAgentName() + " ??act) ??condition)))");
    } // End of CancelMyActionFilter/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/

    /**
     * @see jade.semantics.interpreter.DeductiveStepImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr)
            throws SemanticInterpretationPrincipleException {
        try {
            MatchResult applyResult = SLPatternManip.match(pattern,sr.getSLRepresentation());
            if (applyResult != null  
                    && !agent.getAgentName().equals(applyResult.getTerm("??agentI"))
                    && (agent.getMyStandardCustomization().trapCancelMyAction(applyResult.getTerm("??agentI"), 
                                applyResult.getActionExpression("??act"), 
                                applyResult.getFormula("??condition"))))
            {
                return new ArrayList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1

} // End of class CancelMyActionFilter
