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
 * ActionFeatures.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.semanticinterpretationprinciple;

import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleImpl;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.SemanticRepresentationImpl;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * Class that represents the Action Features Deductive Step. This step is 
 * intented to be applied to the initial formula representing the fact that the 
 * Jade agent has perceived an incoming ACL message.  
 * It produces four Semantic Representations :
 * <ul>
 * <li> one for checking the consistency.
 * <li> one stating that the Jade agent believes the persistent feasibility 
 * precondition of the received message is satisfied.
 * <li> one stating the the Jade agent believes the intentional effect of the 
 * received message.
 * <li> the last is the poscondition of an action, the performance of which has just 
 * been observed by the Jade agent.
 * </ul>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class ActionFeatures extends SemanticInterpretationPrincipleImpl {
    
    /**
     * Pattern used to test the applicability of the deductive step
     */
     Formula pattern;
     
     /**
      * Pattern used to build the feasibility precondition 
      */
     Formula feasibilityPreconditonPattern; 
    
     /**
      * Pattern used to build the persistent precondition 
      */
     Formula persistentPreconditionPattern;
    
     /**
      * Pattern used to build the intentional effect 
      */
     Formula intentionalEffectPattern;

     /**
      * Pattern used to build the postcondition 
      */
	  Formula postConditionPattern;
	     
     /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor of the principle
     * @param agent agent that owns of the Semantic interpretation principle
     */
    public ActionFeatures(SemanticAgent agent) {
        setAgent(agent);
        pattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " (done ??act true))");
        feasibilityPreconditonPattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " (done ??act ??fp))");
        persistentPreconditionPattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " ??p)");
        intentionalEffectPattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " (I ??sender ??re))");
        postConditionPattern = SLPatternManip.fromFormula("(B " + agent.getAgentName() + " ??pc)");
    } // End of ActionFeatures/1
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * Produces four semantic representation : the feasibility precondition, 
     * the persistent feasibility precondition, the rational effect and the
     * postcondition.
     * @see jade.semantics.interpreter.SemanticInterpretationPrincipleImpl#apply(jade.core.semantics.interpreter.SemanticRepresentation, jade.util.leap.ArrayList)
     */
    public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
//        Long debut = new Date().getTime();
//        System.out.println("------- Debut Action features");
        try {
            MatchResult matchResult = SLPatternManip.match(pattern,sr.getSLRepresentation());
            if (matchResult != null) {
                
                ActionExpression action = matchResult.getActionExpression("??act");
                SemanticAction act = agent.getMySemanticActionTable().getSemanticActionInstance(action);
                if (act != null) {
    	            // Feasibility Precondition 
    	            SemanticRepresentation feasibilityPreconditionSR = new SemanticRepresentationImpl(); 
    	            feasibilityPreconditionSR.setMessage(sr.getMessage());
    	            feasibilityPreconditionSR.setSLRepresentation(
    						((Formula)SLPatternManip.instantiate(feasibilityPreconditonPattern,
                                    "??act", action,
                                    "??fp", act.getFeasibilityPrecondition())));
    
    				// Persistent Precondition 
    	            SemanticRepresentation persistentPreconditionSR = new SemanticRepresentationImpl();
    	            persistentPreconditionSR.setMessage(sr.getMessage());
    	            persistentPreconditionSR.setSLRepresentation(
    	                    ((Formula)SLPatternManip.instantiate(persistentPreconditionPattern,
                                     "??p", act.getPersistentFeasibilityPrecondition())));
    
    						// Intentional Effect 
    	            SemanticRepresentation intentionaleEffectSR = new SemanticRepresentationImpl();
    	            intentionaleEffectSR.setMessage(sr.getMessage());
    	            intentionaleEffectSR.setSLRepresentation(
    	                    ((Formula)SLPatternManip.instantiate(intentionalEffectPattern,
                                    "??sender", ((ActionExpressionNode)action).as_agent(),
                                    "??re", act.getRationalEffect())));
    	
    				// Postconditions 
    	            SemanticRepresentation postConditionsSR = new SemanticRepresentationImpl();
    	            postConditionsSR.setMessage(sr.getMessage());
    	            postConditionsSR.setSLRepresentation(
    	            		((Formula)SLPatternManip.instantiate(postConditionPattern, 
                                                                "??pc", act.getPostCondition())));
                    ArrayList listOfSR = new ArrayList();
    				listOfSR.add(feasibilityPreconditionSR);
    	            listOfSR.add(persistentPreconditionSR);
    	            listOfSR.add(intentionaleEffectSR);
    	            listOfSR.add(postConditionsSR);
//                    System.out.println("--------------------- ACTION FEATURES");
//                    System.out.println("FP : " + feasibilityPreconditionSR);
//                    System.out.println("PFP : " + persistentPreconditionSR);
//                    System.out.println("ER : " + intentionaleEffectSR);
//                    System.out.println("PC : " + postConditionsSR);
//                    System.out.println("-------------------------------------");
    	            action = null;
//                    Long end = new Date().getTime();
//                    System.out.println("---- Fin Action Features ok : " + (debut - end));
    	            return listOfSR;
                } 
            } 
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
//        Long end = new Date().getTime();
//        System.out.println("---- Fin Action Features nok : " + (debut - end));
        return null;
    } // End of apply/1
    
} // End of class ActionFeatures
