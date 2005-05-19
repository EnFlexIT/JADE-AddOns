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
 * PrimitiveBehaviour.java
 * Created on 3 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;


import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.performatives.Disconfirm;
import jade.semantics.actions.performatives.Inform;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.kbase.KbaseImpl_List;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.Logger;
import jade.util.leap.ArrayList;

/**
 * Class that represents the behaviour associated with a primitive semantic
 * action.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class PrimitiveBehaviour extends SemanticBehaviour {
    
    /**
     * Semantic action, which the primitive behaviour is associated to 
     */
    private CommunicativeAction action;

    
    
    Content agreeContentPattern = SLPatternManip.fromContent("((I ??agent (done ??act ??condition)))");
    
    Content cancelContentPattern = SLPatternManip.fromContent("((I ??agent (done ??act)))");

    Content proposeContentPattern = SLPatternManip.fromContent("((or (not (I ??agent1 (done ??act ??condition))) (I ??agent2 (done ??act ??condition))))");
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param action the semantic action to which this behaviour belongs
     */
    public PrimitiveBehaviour(CommunicativeAction action) {
        super();
        this.action = action;
    } // End of PrimitiveBehaviour/1

    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/

    /**
     * Sends an ACL message if the feasibility precondition of the action is 
     * satisfied.
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Trying the behaviour of " + ACLMessage.getPerformative(action.getACLMessageCode()) + " ON " + action.getContent());
        if (((SemanticAgent)myAgent).getAgentName().equals(action.getSender())) {
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Cheking Feasibility Precondition: " + action.getFeasibilityPrecondition());
            if (((SemanticAgent)myAgent).getMyKBase().query(action.getFeasibilityPrecondition()) != null) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Feasibility Precondition is believed!");
                try {
                    ACLMessage reply = new ACLMessage(action.getACLMessageCode());
                    // TODO 1/ translate the content into the correct content language
                    //      2/ deal with multi-receivers performatives
					MatchResult matchResult = SLPatternManip.match(agentPattern, action.getReceiverList().first());
                    reply.addReceiver(new AID(matchResult.getTerm("??agent").toString(), false));
                    reply.setContent(action.getContent().toString());
                    ACLMessage r2 = changeForm();
	                myAgent.send(r2);
	                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "ACL Message has been sent!");
                } catch (Exception e) { 
                    state = EXECUTION_FAILURE;
	                if (logger.isLoggable(Logger.FINEST)) {
	                    logger.log(Logger.FINEST, "Failed in sending the ACL Message");
	                    logger.log(Logger.FINEST, "-> Behaviour ended with EXECUTION_FAILURE");
	                }
                    return;
                }
                ((SemanticAgent)myAgent).getMyKBase().assertFormula(action.getPostCondition());
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
        }
        state = FEASIBILITY_FAILURE;
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "-> Behaviour ended with FEASIBILITY_FAILURE");
    } // End of action/0

    private ACLMessage changeForm() {
        ACLMessage msg = null;
        try {
            // AGREE + ACCEPT_PROPOSAL
            //TODO ajouter un test sur l'acteur de l'action ??act
            MatchResult matchResult = SLPatternManip.match(action.getContent(), agreeContentPattern);
            if (action instanceof Inform && matchResult != null) {
                ListOfContentExpression list = new ListOfContentExpression();
                list.add(0, matchResult.getActionExpression("??act"));
                list.add(1, matchResult.getFormula("??condition"));
                Content cont = new ContentNode(list);
              //  if (matchResult.getActionExpression("??act").getAgents().contains()) {
                    msg = new ACLMessage(ACLMessage.AGREE);    
//                } else {
//                    msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
//                }
                msg.setContent(cont.toString());
            } else { 
                // CANCEL
                
                matchResult = SLPatternManip.match(action.getContent(), cancelContentPattern);
                if (action instanceof Disconfirm && matchResult != null) {
                    ListOfContentExpression list = new ListOfContentExpression();
                    list.add(0, matchResult.getActionExpression("??act"));
                    Content cont = new ContentNode(list);
                    msg = new ACLMessage(ACLMessage.CANCEL);    
                    msg.setContent(cont.toString());
                } else {
                    // PROPOSE
                
                    matchResult = SLPatternManip.match(action.getContent(), proposeContentPattern);
                    if (action instanceof Inform && matchResult != null) {
                        ListOfContentExpression list = new ListOfContentExpression();
                        list.add(0, matchResult.getActionExpression("??act"));
                        list.add(1, matchResult.getFormula("??condition"));
                        Content cont = new ContentNode(list);
                        msg = new ACLMessage(ACLMessage.PROPOSE);    
                        msg.setContent(cont.toString());
                    } else {
                        msg = new ACLMessage(action.getACLMessageCode());
                        msg.setContent(action.getContent().toString());
                    }
                }
            }
            MatchResult matchResult2 = SLPatternManip.match(agentPattern, action.getReceiverList().first());
            msg.addReceiver(new AID(matchResult2.getTerm("??agent").toString(), false));
            return msg;
        } catch(Exception e) {e.printStackTrace(); return null;}
    }
    
    
} // End of class PrimitiveBehaviour
