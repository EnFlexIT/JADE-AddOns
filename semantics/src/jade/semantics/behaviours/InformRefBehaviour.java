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
 * InformRefBehaviour.java
 * Created on 24 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.performatives.InformRef;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Class that represents the behaviour associated with an inform-ref semantic
 * action.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class InformRefBehaviour extends SemanticBehaviour {
    
    /**
     * Internal state of this behaviour
     */
    private static final int WAIT_RESULT_OF_INFORM = 900;
    
    /**
     * The semantic action to which this behaviour belongs 
     */
    private InformRef action;
	    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
   
    /**
     * Constructor
     * @param action the semantic action to which this behaviour belongs
     */
    public InformRefBehaviour(InformRef action) {
        super();
        this.action = action;
    } // End  of InformRefBehaviour/1

    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/

    /**
     * If there is an answer to the query, an ACL message <i>Inform</i> is sent, 
     * and the final result is success. In the other cases, the final result is 
     * execution failure, or feasibility failure.
     * @see jade.core.behaviours.Behaviour#action()
     */

    public void action() {
        try { 
	        if (((SemanticAgent)myAgent).getAgentName().equals(action.getSender())) {
	            ListOfTerm listOfResult = ((SemanticAgent)myAgent).getMyKBase().queryRef(action.getMyContent());
	            EqualsNode equalsNode = new EqualsNode();
	            equalsNode.as_left_term(action.getMyContent());
                if (listOfResult != null) {
                    if (action.getMyContent() instanceof AllNode) {
                        equalsNode.as_right_term(new TermSetNode(listOfResult));    
                    } else {
                        equalsNode.as_right_term((Term)listOfResult.get(0));
                    }
	            } else {
	                state = FEASIBILITY_FAILURE;
	            }
	            if (state != FEASIBILITY_FAILURE) {
	                ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
		            Content content = new ContentNode(new ListOfContentExpression(new ContentExpression[] {new FormulaContentExpressionNode(equalsNode)}));
                    MatchResult matchResult = SLPatternManip.match(agentPattern, action.getReceiver());
                    reply.addReceiver(new AID(matchResult.getTerm("??agent").toString(), false));
                    reply.setContent(content.toString());
                    myAgent.send(reply);
                    state = SUCCESS;
	            } 
	        } else {
	            state = FEASIBILITY_FAILURE;
	        }
        } catch (Exception e) {
            e.printStackTrace();
            state = EXECUTION_FAILURE;
        }
    } // End of action/0

} // End of class InformRefBehaviour
