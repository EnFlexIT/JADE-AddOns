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
 * CommunicativeActionBehaviour.java
 * Created on 28 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.Logger;

/**
 * Used to gather the principles used by communicative action behaviours.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/28 Revision: 1.0
 */
public abstract class CommunicativeActionBehaviour extends SemanticBehaviourBase {
    
    /**
     * Semantic action, which the primitive behaviour is associated to. 
     */
    protected CommunicativeAction action;
    
    /**
     * Pattern used to store that the agent has done the action. 
     */
    public static final Formula believeDonePattern = SLPatternManip.fromFormula("(B ??agent (done ??act))");
    
    /**
     * Sends an ACL message if the feasibility precondition of the action is 
     * satisfied. Stores in the knowledge base, the postcondition of the action
     * and the fact that the agent has done the action.
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Trying the behaviour of " + ACLMessage.getPerformative(action.getSurfacePerformative()) + " ON " + action.getSurfaceContent());
        if (((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName().equals(action.getAuthor())) {
            try {          
                if (compute()) {
                    if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Feasibility Precondition is believed!");
                    
                    myAgent.send(action.toAclMessage());
                    if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "ACL Message has been sent!");
                    
                    ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().assertFormula(action.getPostCondition());
                    try {
                        ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().assertFormula(
                                ((Formula)SLPatternManip.instantiate(believeDonePattern,
                                        "agent", ((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName(),
                                        "act", action.toActionExpression())).getSimplifiedFormula());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    state = SUCCESS;
                    if (logger.isLoggable(Logger.FINEST)) {
                        logger.log(Logger.FINEST, "Asserted postcondition " + action.getPostCondition());
                        logger.log(Logger.FINEST, "-> Behaviour ended with SUCCESS");
                    }
                    return;
                }
                else {
                    if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Feasibility Precondition is not believed!");                
                }
            } catch (Exception e) { 
                state = EXECUTION_FAILURE;
                if (logger.isLoggable(Logger.FINEST)) {
                    logger.log(Logger.FINEST, "Failed in sending the ACL Message");
                    logger.log(Logger.FINEST, "-> Behaviour ended with EXECUTION_FAILURE");
                }
                return;
            }
        }
        state = FEASIBILITY_FAILURE;
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "-> Behaviour ended with FEASIBILITY_FAILURE");
    } // End of action/0
    
    /**
     * Returns true if the action could be done, false if not.
     * @return true if the action could be done, false if not.
     * @throws Exception if any exception occurs
     */
    public abstract boolean compute() throws Exception;
    
} // End of class
