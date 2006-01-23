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

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.performatives.InformRef;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Class that represents the behaviour associated with an <code>InformRef</code> semantic
 * action.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class InformRefBehaviour extends CommunicativeActionBehaviour {
    
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates an InformRefBehaviour.
     * @param action the semantic action to which this behaviour belongs
     */
    public InformRefBehaviour(InformRef action) {
        super();
        this.action = action;
    } // End of InformRefBehaviour/1
    
    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     * @return if there is an answer to the query on the object description of the content of the action <code>InformRef</code>, returns true. 
     * In this case, the parameters of the action are set. Return false, in others cases. 
     * @see jade.semantics.behaviours.CommunicativeActionBehaviour#compute()
     */
    public boolean compute() throws Exception {
        ListOfTerm listOfResult = ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().queryRef((IdentifyingExpression)action.getContentElement(0));
        if (listOfResult != null) {
            EqualsNode equalsNode = new EqualsNode();
            equalsNode.as_left_term((Term)action.getContentElement(0));
            if (action.getContentElement(0) instanceof AllNode ||
                    action.getContentElement(0) instanceof SomeNode) {
                equalsNode.as_right_term(new TermSetNode(listOfResult));    
            } else {
                equalsNode.as_right_term((Term)listOfResult.get(0));
            }
            
            Formula proposePattern = SLPatternManip.fromFormula("(or (not (I ??agent1 (done ??act ??condition))) (I ??agent2 (done ??act ??condition)))");
            MatchResult matchProposeResult = SLPatternManip.match(proposePattern, equalsNode.getSimplifiedFormula());
            if (matchProposeResult != null) {
                // This case has been introduced to work around a bug in the FIPA specification
                action.setSurfacePerformative(ACLMessage.PROPOSE);
                action.setSurfaceContent(2);
                action.setSurfaceContentElement(0, matchProposeResult.getTerm("act"));
                action.setSurfaceContentElement(1, matchProposeResult.getFormula("condition"));
            }
            else {
                action.setSurfacePerformative(ACLMessage.INFORM);
                action.setSurfaceContentElement(0, equalsNode.getSimplifiedFormula());
            }
            return true;
        }
        return false;
    } // End of compute/0
    
} // End of class InformRefBehaviour
