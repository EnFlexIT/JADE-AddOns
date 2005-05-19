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
 * SequenceBehaviour.java
 * Created on 8 déc. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.semantics.actions.SemanticAction;

/**
 * Class that represents the behaviour associated with an sequence semantic
 * action.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SequenceBehaviour extends SemanticBehaviour {

    /**
     * Internal state of the behaviour
     */
    private final static int WAIT_RETURN_LEFT_ACTION = 100;
    
    /**
     * Internal state of the behaviour
     */
    private final static int WAIT_RETURN_RIGHT_ACTION = 101;
    
    /**
     * The left part of the alternative 
     */
    private SemanticAction leftAction;
 
    /**
     * The right part of the alternative
     */
    private SemanticAction rightAction;

    /**
     * The current sub-behaviour this behaviour is waiting the result
     */
    private SemanticBehaviour runningBehaviour;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor
     * @param leftAction the left action of the alternative
     * @param rightAction the right action of the alternative
     */
    public SequenceBehaviour(SemanticAction leftAction, SemanticAction rightAction) {
        super();
        this.leftAction = leftAction;
        this.rightAction = rightAction;
    } // End of AlternativeBehaviour/2

    /*********************************************************************/
    /**                         PUBLIC METHODS                          **/
    /*********************************************************************/

    /**
     * First starts the behaviour for the the left part of the sequence.
     * If the behaviour ends up with execution failure, or feasibility failure,
     * the final result is respectivly execution failure or feasibility failure. 
     * If the behaviour ends up with success, the right part of the alternative is 
     * considered and the result of the behaviour is the final result. 
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {
        if (state == START) {
            runningBehaviour = (SemanticBehaviour)leftAction.getBehaviour();
            myAgent.addBehaviour(leftAction.getBehaviour());
            state = WAIT_RETURN_LEFT_ACTION;
        } else if (state == WAIT_RETURN_LEFT_ACTION) {
            if (runningBehaviour.getState() == SUCCESS) {
                runningBehaviour = (SemanticBehaviour)rightAction.getBehaviour();
                myAgent.addBehaviour(rightAction.getBehaviour());
                state = WAIT_RETURN_RIGHT_ACTION;
            } else if (runningBehaviour.getState() == FEASIBILITY_FAILURE) {
                state = FEASIBILITY_FAILURE;
            } else if (runningBehaviour.getState() == EXECUTION_FAILURE) {
                state = EXECUTION_FAILURE;
            }
        } else if (state == WAIT_RETURN_RIGHT_ACTION) {
            if (runningBehaviour.getState() == FEASIBILITY_FAILURE) {
                state = FEASIBILITY_FAILURE;
            } else if (runningBehaviour.getState() == SUCCESS) {
                state = SUCCESS;
            } else if (runningBehaviour.getState() == EXECUTION_FAILURE) {
                state = EXECUTION_FAILURE;
            }
        }
    } // End of action/0

} // End of class SequenceBehaviour
