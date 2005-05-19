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
 * OntoActionBehaviour.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.behaviours;

import jade.core.Agent;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticRepresentationImpl;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Class that represents a behaviour for an ontologic action.
 * @author louisvi
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class OntoActionBehaviour extends SemanticBehaviour {
	
	/**
	 * Pattern to apply the behaviour
	 */
    private Formula B_PATTERN;
    
    /**
     * Internal state
     */
    private static final int BEFORE_START = -2;
    
    /**
     * The semantic action to which this behaviour belongs
     */
    OntologicalAction myAction;
 
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     * @param action the semantic action to which this behaviour belongs
     */
    public OntoActionBehaviour(OntologicalAction action) {
        super();
        state = BEFORE_START;
        myAction = action;
    } // End of OntoActionBehaviour/1
    
    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/

    /**
     * If the action is feasible; the action is performed and the belief of 
     * feasibility precondition and belief of postcondition are considered as
     * internal events.
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {
        if (state == BEFORE_START) {
            myAction.beforePerform(this);
			try {
				B_PATTERN = SLParser.getParser().parseFormula("(B " + ((SemanticAgent)myAgent).getAgentName() + " ??phi)", true);
			}
			catch(ParseException pe) {pe.printStackTrace();}
            try {
				SLPatternManip.set(B_PATTERN, "??phi", myAction.getFeasibilityPrecondition());
                if (((SemanticAgent)myAgent).getMyKBase().query((Formula)SLPatternManip.instantiate(B_PATTERN)) == null) {
                    throw new Exception("OntoAction is not believed feasible");
                }
            }
            catch (Exception e) {
                state = FEASIBILITY_FAILURE;
                return;
            }
            state = START;
        }
        myAction.perform(this);
        if (state == SUCCESS) {
            try {
				SLPatternManip.set(B_PATTERN, "??phi", myAction.getPersistentFeasibilityPrecondition());
                ((SemanticAgent)myAgent).interpret(new SemanticRepresentationImpl((Formula)SLPatternManip.instantiate(B_PATTERN)));
				SLPatternManip.set(B_PATTERN, "??phi", myAction.getPostCondition());	
				((SemanticAgent)myAgent).interpret(new SemanticRepresentationImpl((Formula)SLPatternManip.instantiate(B_PATTERN)));
             }
            catch (Exception e) {}
            myAction.afterPerform(this);
        }
    } // End of action/0
    
    /**
     * Gets the JADE agent running this behaviour
     * @return the agent running this behaviour
     */
    public Agent getMyAgent() {
        return myAgent;
    } // End of getMyAgent/0
    
} // End of OntoActionBehaviour
