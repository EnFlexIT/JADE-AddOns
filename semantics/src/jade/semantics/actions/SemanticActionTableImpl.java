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

import jade.semantics.actions.operators.Alternative;
import jade.semantics.actions.performatives.Agree;
import jade.semantics.actions.performatives.Confirm;
import jade.semantics.actions.performatives.Disconfirm;
import jade.semantics.actions.performatives.Inform;
import jade.semantics.actions.performatives.InformIf;
import jade.semantics.actions.performatives.InformRef;
import jade.semantics.actions.performatives.QueryIf;
import jade.semantics.actions.performatives.QueryRef;
import jade.semantics.actions.performatives.Request;
import jade.semantics.actions.performatives.RequestWhenever;
import jade.semantics.interpreter.Finder;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;


/**
 * Class that represents all the actions knowed by the agent.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SemanticActionTableImpl extends ArrayList implements SemanticActionTable {

    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor
     */
    public SemanticActionTableImpl() {
        super();
    } // End of SemanticActionTableImpl/0
    
    /**************************************************************************/
    /**									PUBLIC METHODS						 **/
    /**************************************************************************/

    /**
     * Adds a semanticAction into the semanticActionTable
     * @param action the semanticAction to add
     */
    public void addSemanticAction(SemanticAction action) {
		add(action);
    } // End of addSemanticAction/1

    /**
     * Removes some semantic actions from the SemanticActionTable
     * @param actionIdentifier the identifier that identifies the semantic actions to remove
     */
    public  void removeSemanticAction(Finder actionIdentifier) {
        actionIdentifier.removeFromList(this);
    } // End of removeAction/1
    
    /**
     * Loads all the actions predefined in the actions package.
     */
    public void loadTable() {
        addSemanticAction(new Agree(this));
        addSemanticAction(new Confirm(this));
        addSemanticAction(new Disconfirm(this));
        addSemanticAction(new Inform(this));
        addSemanticAction(new Request(this));
        addSemanticAction(new InformRef(this));
        addSemanticAction(new InformIf(this));
        addSemanticAction(new QueryIf(this));
        addSemanticAction(new QueryRef(this));;
        addSemanticAction(new RequestWhenever(this));
        addSemanticAction(new Alternative(this));
// TODO Use a classloader to load application-specific semantic actions
    } // End of loadTable/0
    
   
    /**
     * Creates an instanciated semanticAction from the semanticAction prototype
     * within the table corresponding to an actionExpression
     * @param action the actionExpression representing the semanticAction to create
     * @return an instanciated semanticAction that implements action
     */
    public SemanticAction getSemanticActionInstance(ActionExpression action) {
    	SemanticAction result = null;
    	Iterator actionIterator = this.iterator();
        while(result == null && actionIterator.hasNext()) {
            SemanticAction elem = ((SemanticAction)actionIterator.next());
            result = elem.newAction(action);
        }
    	return result;
    } // End of newAction/1

    /**
     * Creates a list of instanciated semanticActions from the semanticAction prototypes
     * within the table, such that each semanticAction has a specified rational effect
     * @param actionList the list of actions to complete with the created semanticActions
     * @param rationalEffect the rational effect of the semanticActions to create
     * @param agentName the name of the SemanticAgent
     * @return actionList, completed with instanciated semanticActions created from the table, the rational effect of which equals rationalEffect
     */
    public void getSemanticActionInstance(ArrayList actionList, Formula rationalEffect, Term agentName) {
        ArrayList classList = new ArrayList();
        Iterator actionIterator = this.iterator(); 
        while(actionIterator.hasNext()) {
            SemanticAction action = ((SemanticAction)actionIterator.next()).newAction(rationalEffect, agentName);
            
            if (action != null) {
                Class actionClass = action.getClass();
                if (!classList.contains(actionClass)) {
                    actionList.add(action);
                    classList.add(actionClass);
                }
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
