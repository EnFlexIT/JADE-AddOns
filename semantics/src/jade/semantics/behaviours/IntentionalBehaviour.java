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
 * IntentionalBehaviour.java
 * Created on 10 mai 2005
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;


import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.SemanticRepresentationImpl;
import jade.semantics.kbase.KBase_Filter;
import jade.semantics.kbase.KbaseImpl_List;
import jade.semantics.lang.sl.grammar.Formula;

/**
 * @author Vincent Pautret
 * @version 
 */
public class IntentionalBehaviour extends SemanticBehaviour {

    /**
     * Internal state of the behaviour
     */
    private final static int WAIT_RETURN = 102;

    Formula intention;
    
    SemanticBehaviour behaviour;
    
    int sipIndex;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Constructor
     * @param action the semantic action to which this behaviour belongs
     */
    public IntentionalBehaviour(SemanticBehaviour behaviour, Formula intention, int index) {
        super();
        this.behaviour = behaviour;
        this.intention = intention;
        sipIndex = index;
    } // End of IntentionalBehaviour/2

    /*********************************************************************/
    /**                         PUBLIC METHODS                          **/
    /*********************************************************************/

    /**
     * Sends an ACL message if the feasibility precondition of the action is 
     * satisfied.
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {
        if (state == START) {
            ((SemanticAgent)myAgent).getMyKBase().assertFormula(intention);
            myAgent.addBehaviour(behaviour);
            state = WAIT_RETURN;
        } else if (state == WAIT_RETURN) {
            if (behaviour.getState() == SUCCESS || behaviour.getState() == FEASIBILITY_FAILURE || behaviour.getState() == EXECUTION_FAILURE) {
                if (behaviour.getState() == SUCCESS) {
                    state = SUCCESS;
                } else if (behaviour.getState() == SUCCESS || behaviour.getState() == FEASIBILITY_FAILURE) {
                    SemanticRepresentation sr = new SemanticRepresentationImpl();
                    sr.setSemanticInterpretationPrincipleIndex(sipIndex+1);
                    sr.setSLRepresentation(intention);
                    ((SemanticAgent)myAgent).interpret(sr);
                    state = FEASIBILITY_FAILURE;
                } else if (behaviour.getState() == EXECUTION_FAILURE) {
                    state = EXECUTION_FAILURE;
                }
                ((SemanticAgent)myAgent).getMyKBase().removeFormula(new Finder() {
                    public boolean identify(Object object) {
                        if (object instanceof Formula) {
                            return intention.equals(object);
                        }
                        return false;
                    }
                });
            }
        }

    }
    
} // End of class IntentionalBehaviour
