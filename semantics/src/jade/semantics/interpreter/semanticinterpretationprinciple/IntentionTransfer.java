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
 * IntentionTransfer.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.SemanticRepresentationImpl;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle expresses a necessary cooperation principle of the Jade
 * agent (receiving the ACL message) towards the intentions that the sender 
 * intends to communicate. Thi step is typically used to interpret incoming 
 * <code>Request</code> or <code>Inform</code> messages, the content of which 
 * being an intention of the sender. 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class IntentionTransfer extends SemanticInterpretationPrincipleImpl {

    /**
     * Pattern used to test the applicability of the deductive step
     */
    Formula intentionTransferPattern; 
    
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    Formula intentionPattern; 
    Formula intentionPattern2;
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    Formula notIntentionPattern;
    Formula notIntentionPattern2;
    
    Formula actionPattern;
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param agent agent that owns of the Deductive Step
     */
    public IntentionTransfer(SemanticAgent agent) {
        setAgent(agent);
        intentionTransferPattern = SLPatternManip.fromFormula("(B " +  agent.getAgentName() + " (I ??sender ??phi))");
        intentionPattern = SLPatternManip.fromFormula("(I " +  agent.getAgentName() + " ??phi)");
        intentionPattern2 = SLPatternManip.fromFormula("(I " + agent.getAgentName() + " (B ??sender (I " + agent.getAgentName() + " ??phi)))");
        notIntentionPattern = SLPatternManip.fromFormula("(I " +  agent.getAgentName() + " (B ??sender (not (I " +  agent.getAgentName() + " ??phi))))");
        notIntentionPattern2 = SLPatternManip.fromFormula("(not (I " +  agent.getAgentName() + " ??phi))");
        
        actionPattern = SLPatternManip.fromFormula("(done (action ??actor ??performative) ??condition)");
    } // End of IntentionTransfer/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * Creates new Semantic representations.
     * @see jade.semantics.interpreter.SemanticInterpretationPrincipleImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        //TODO Two other SRs have to be generated (see spec doc section 2.3.6)
        try {
            MatchResult matchResult = SLPatternManip.match(intentionTransferPattern,sr.getSLRepresentation());
            if (matchResult != null) {
                
                Term sender = matchResult.getTerm("??sender");
                Formula phi = matchResult.getFormula("??phi");
                MatchResult matchResult2 = SLPatternManip.match(phi, actionPattern);
                Term actor = null;
                if ( matchResult2 != null) {actor = matchResult2.getTerm("??actor");}
                if ( matchResult2 == null || (!sender.equals(actor))) {
                    SemanticRepresentation newFirstSR = new SemanticRepresentationImpl();
                    SemanticRepresentation newSecondSR = new SemanticRepresentationImpl();
                    newFirstSR.setMessage(sr.getMessage());
                    newSecondSR.setMessage(sr.getMessage());
    	            if (agent.getMyStandardCustomization().acceptIntentionTransfer(phi, sender)) {
    	                newFirstSR.setSLRepresentation((Formula)SLPatternManip.instantiate(intentionPattern, "??phi", phi));
                        newSecondSR.setSLRepresentation((Formula)SLPatternManip.instantiate(intentionPattern2,
                                "??sender", sender,
                                "??phi", phi));
    	            } else {
    	                newFirstSR.setSLRepresentation((Formula)SLPatternManip.instantiate(notIntentionPattern, "??sender", sender, "??phi", phi));
                        newSecondSR.setSLRepresentation((Formula)SLPatternManip.instantiate(notIntentionPattern2,
                                "??phi", phi));
    	            }
                    ArrayList listOfSR = new ArrayList();
    	            listOfSR.add(newFirstSR);
                    listOfSR.add(newSecondSR);
    	            sender = null;
    	            phi = null;
    	            return listOfSR;
                }
            } 
        } catch (SLPatternManip.WrongTypeException e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1

} // End of class IntentionTransfer
