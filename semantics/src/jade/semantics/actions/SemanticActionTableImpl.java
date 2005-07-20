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
 * SemanticActionTableImpl.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.actions.operators.Sequence;
import jade.semantics.actions.performatives.AcceptProposal;
import jade.semantics.actions.performatives.Agree;
import jade.semantics.actions.performatives.CallForProposal;
import jade.semantics.actions.performatives.Cancel;
import jade.semantics.actions.performatives.Confirm;
import jade.semantics.actions.performatives.Disconfirm;
import jade.semantics.actions.performatives.Failure;
import jade.semantics.actions.performatives.Inform;
import jade.semantics.actions.performatives.InformIf;
import jade.semantics.actions.performatives.InformRef;
import jade.semantics.actions.performatives.NotUnderstood;
import jade.semantics.actions.performatives.Propose;
import jade.semantics.actions.performatives.QueryIf;
import jade.semantics.actions.performatives.QueryRef;
import jade.semantics.actions.performatives.Refuse;
import jade.semantics.actions.performatives.RejectProposal;
import jade.semantics.actions.performatives.Request;
import jade.semantics.actions.performatives.RequestWhen;
import jade.semantics.actions.performatives.RequestWhenever;
import jade.semantics.actions.performatives.Subscribe;
import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;


/**
 * Class that implements the interface <code>SemanticActionTable</code>. It 
 * represents all the actions knowed by the agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class SemanticActionTableImpl extends ArrayList implements SemanticActionTable {
    
    /**
     * The semantic capabilities that hold the action table. 
     */
    private SemanticCapabilities myCapabilities;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new <code>SemanticActionTable</code>.
     * @param capabilities the semantic capabilities that hold the action table 
     */
    public SemanticActionTableImpl(SemanticCapabilities capabilities) {
        super();
        myCapabilities = capabilities;
    } // End of SemanticActionTableImpl/0
    
    /**************************************************************************/
    /**									PUBLIC METHODS						 **/
    /**************************************************************************/
    
    /**
     * Returns the semantic capabilities that hold the action table
     * @return the semantic capabilities that hold the action table 
     */
    public SemanticCapabilities getSemanticCapabilities() {
        return myCapabilities;
    }
    
    /**
     * Adds a semanticAction into the table.
     * @param action the semanticAction to add
     */
    public void addSemanticAction(SemanticAction action) {
        add(action);
    } // End of addSemanticAction/1
    
    /**
     * Removes some semantic actions from the table.
     * @param actionIdentifier the identifier that identifies the semantic actions to remove
     */
    public  void removeSemanticAction(Finder actionIdentifier) {
        actionIdentifier.removeFromList(this);
    } // End of removeAction/1
    
    /**
     * Loads all the actions predefined in the <i>actions</i> package.
     */
    public void loadTable() {
        addSemanticAction(new AcceptProposal(this));
        addSemanticAction(new Agree(this));
        addSemanticAction(new CallForProposal(this));
        addSemanticAction(new Cancel(this));
        addSemanticAction(new Failure(this));
        addSemanticAction(new NotUnderstood(this));
        addSemanticAction(new Propose(this));
        addSemanticAction(new Refuse(this));
        addSemanticAction(new RejectProposal(this));
        addSemanticAction(new QueryIf(this));
        addSemanticAction(new QueryRef(this));
        addSemanticAction(new InformRef(this));
        addSemanticAction(new InformIf(this));
        addSemanticAction(new RequestWhenever(this));
        addSemanticAction(new Subscribe(this));
        addSemanticAction(new RequestWhen(this));
        
        addSemanticAction(new Request(this));
        addSemanticAction(new Inform(this));
        addSemanticAction(new Confirm(this));
        addSemanticAction(new Disconfirm(this));
        
        addSemanticAction(new Alternative(this));
        addSemanticAction(new Sequence(this));
    } // End of loadTable/0
    
    
    /**
     * Creates an instanciated <code>SemanticAction</code> from the 
     * <code>SemanticAction</code> prototype
     * within the table corresponding to an <code>ActionExpression</code>
     * @param action the actionExpression representing the semanticAction to create
     * @throws SemanticInterpretationException
     * @return an instanciated semanticAction that implements action
     */
    public SemanticAction getSemanticActionInstance(ActionExpression action) throws SemanticInterpretationException {
        SemanticAction result = null;
        Iterator actionIterator = this.iterator();
        while(result == null && actionIterator.hasNext()) {
            result = ((SemanticAction)actionIterator.next()).newAction(action);
        }
        if (result == null) {
            throw new SemanticInterpretationException("unknown-message", new StringConstantNode(action.toString()));
        }
        return result;
    } // End of newAction/1
    
    /**
     * Creates an instanciated <code>SemanticAction</code> from the <code>SemanticAction</code> prototype
     * within the table corresponding to an ACL Message
     * @param aclMessage an ACL message
     * @throws SemanticInterpretationException
     * @return an instanciated semanticAction that implements action
     */
    public SemanticAction getSemanticActionInstance(ACLMessage aclMessage) throws SemanticInterpretationException {
        SemanticAction result = null;
        Iterator actionIterator = this.iterator();
        while(result == null && actionIterator.hasNext()) {
            Object elem = actionIterator.next();
            if (elem instanceof CommunicativeAction) {
                result = ((CommunicativeAction)elem).newAction(aclMessage);
            }
        }
        if (result == null) {
            throw new SemanticInterpretationException("unknown-message", new StringConstantNode(aclMessage.toString()));
        }
        return result;
    } // End of newAction/1
    
    /**
     * Creates a list of instanciated semantic actions from the <code>SemanticAction</code> prototypes
     * within the table, such that each <code>SemanticAction</code> has a specified rational effect
     * @param actionList the list of actions to complete with the created semanticActions
     * @param rationalEffect the rational effect of the semantic actions to create
     * @param inReplyTo the message to reply to
     */
    public void getSemanticActionInstance(ArrayList actionList, Formula rationalEffect, ACLMessage inReplyTo) {
        Iterator actionIterator = this.iterator(); 
        while(actionIterator.hasNext()) {
            SemanticAction action = ((SemanticAction)actionIterator.next()).newAction(rationalEffect, inReplyTo);
            if (action != null && !actionList.contains(action)) {
                actionList.add(action);
            }
        }
    } // End of newAction/3
    
    /**
     * For debugging purpose only
     */
    public void viewData() {
        System.err.println(this.toString());
    } // End of viewData/0
    
    /**
     * For debugging purpose only
     * @return the string that represents the actions of the table
     */
    public String toString() {
        String result = ("------------------SEMANTIC ACTION TABLE CONTENT\n");
        for (int i=0 ; i<this.size() ; i++) {
            result = result + "(" + i + ") " + this.get(i).getClass() + "\n";
        }
        return result + "-----------------------------------------------";
    } // End of viewData/0
    
} // End of class SemanticActionTableImpl
