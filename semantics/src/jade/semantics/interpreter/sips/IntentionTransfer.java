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
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.SemanticRepresentation.FeedBackData;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle expresses a necessary cooperation principle of the Jade
 * agent (receiving the ACL message) towards the intentions that the sender 
 * intends to communicate. This step is typically used to interpret incoming 
 * <code>Request</code> or <code>Inform</code> messages, the content of which 
 * being an intention of the sender. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class IntentionTransfer extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern used to test the applicability of the semantic interpretation principle
     */
    private Formula intentionTransferPattern; 
    
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula intentionPattern; 
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula intentionPattern2;
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula notIntentionPattern;
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula notIntentionPattern2;
    
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula donePattern;
    
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula bPattern;
    
    /**
     * Pattern used to create a new <code>SemanticRepresentation</code>
     */
    private Formula intentionPattern3;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public IntentionTransfer(SemanticCapabilities capabilities) {
        super(capabilities);
        intentionTransferPattern = SLPatternManip.fromFormula("(B " +  myCapabilities.getAgentName() + " (I ??sender ??phi))");
        intentionPattern = SLPatternManip.fromFormula("(I " +  myCapabilities.getAgentName() + " ??phi)");
        intentionPattern2 = SLPatternManip.fromFormula("(I " + myCapabilities.getAgentName() + " (B ??sender (I " + myCapabilities.getAgentName() + " ??phi)))");
        notIntentionPattern = SLPatternManip.fromFormula("(I " +  myCapabilities.getAgentName() + " (B ??sender (not (I " +  myCapabilities.getAgentName() + " ??phi))))");
        notIntentionPattern2 = SLPatternManip.fromFormula("(not (I " +  myCapabilities.getAgentName() + " ??phi))");        
        donePattern = SLPatternManip.fromFormula("(done ??action ??phi)");
        bPattern = SLPatternManip.fromFormula("(B " +  myCapabilities.getAgentName() + " ??phi)");
        intentionPattern3 = SLPatternManip.fromFormula("(I " + myCapabilities.getAgentName() + " (B ??sender ??phi))");
    } // End of IntentionTransfer/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Creates new Semantic representations using the <code>intentionPatterns</code> 
     * or the <code>notIntentionPatterns</code> depending of the choice of the
     * Jade agent to accept or not the intention transfer.
     * @inheritDoc
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = SLPatternManip.match(intentionTransferPattern,sr.getSLRepresentation());
            if (matchResult != null) {
                
                Term sender = matchResult.getTerm("sender");
                Formula phi = matchResult.getFormula("phi");
                MatchResult matchDoneResult = SLPatternManip.match(phi, donePattern);
                if ( matchDoneResult == null ||
                        !((ActionExpression)matchDoneResult.getTerm("action")).getAgents().contains(sender)) {
                    ArrayList listOfSR = new ArrayList();
                    if (myCapabilities.getMyKBase().query(
                            (Formula)SLPatternManip.instantiate(bPattern, "phi", phi)) == null) {
                        if (myCapabilities.getMyStandardCustomization().acceptIntentionTransfer(phi, sender)) {
                            if (matchDoneResult==null || !Tools.isCommunicativeActionFromMeToReceiver(
                                    (ActionExpression)matchDoneResult.getTerm("action"), sender, myCapabilities.getAgent())) {
                                listOfSR.add(new SemanticRepresentation(
                                        sr.getMessage(),
                                        ((Formula)SLPatternManip.instantiate(intentionPattern2,
                                                "sender", sender,
                                                "phi", phi)).getSimplifiedFormula(),
                                                0, null));
                            }
                            listOfSR.add(new SemanticRepresentation(
                                    sr.getMessage(),
                                    ((Formula)SLPatternManip.instantiate(intentionPattern, "phi", phi)).getSimplifiedFormula(),
                                    0, new FeedBackData(sender,phi)));
                        } else {
                            listOfSR.add(new SemanticRepresentation(
                                    sr.getMessage(),
                                    ((Formula)SLPatternManip.instantiate(notIntentionPattern2, "phi", phi)).getSimplifiedFormula(),
                                    0, new FeedBackData(sender, phi)));
                            listOfSR.add(new SemanticRepresentation(
                                    sr.getMessage(),
                                    ((Formula)SLPatternManip.instantiate(notIntentionPattern, "sender", sender, "phi", phi)).getSimplifiedFormula(),
                                    0,
                                    null));
                        }
                    }
                    else {
                        listOfSR.add(new SemanticRepresentation(
                                sr.getMessage(),
                                ((Formula)SLPatternManip.instantiate(intentionPattern3, "sender", sender, "phi", phi)).getSimplifiedFormula(),
                                0,
                                null));
                    }
                    
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
