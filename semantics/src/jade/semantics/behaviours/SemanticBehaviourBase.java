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
 * SemanticBehaviour.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.core.behaviours.Behaviour;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.Logger;

/**
 * Class that represents a semanticBehaviour. This class extends Behaviour and 
 * add a state to the behaviour, which indicates the final execution state 
 * (SUCCESS, FEASIBILITY_FAILURE, or EXECUTION_FAILURE) of it.  
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class SemanticBehaviourBase extends Behaviour implements SemanticBehaviour {
    
    /**
     * Pattern used to identify an agent
     */
    public static Term agentPattern = null;
    
    static {
        agentPattern = SLPatternManip.fromTerm("(agent-identifier :name ??agent)");		
    }
    
    /**
     * Execution state of the behaviour
     */
    public int state;
    
    /**
     * Logger
     */
    protected Logger logger;
    
    
    /*********************************************************************/
    /**				 		  DEFAULT CONSTRUCTOR						**/
    /*********************************************************************/
    
    /**
     * Creates a Semantic Behaviour.
     */
    public SemanticBehaviourBase() {
        super();
        logger = Logger.getMyLogger("jade.core.semantics.behaviours.SemanticBehaviour");
        state = START;
    } // End of SemanticBehaviour/0
    
    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/
    
    /**
     * Runs the behaviour. This abstract method must be implemented by
     * <code>SemanticBehaviour</code> subclasses to perform ordinary behaviour
     * duty.
     * 
     */
    public abstract void action();
    
    /**
     * Check if this behaviour is done.
     * 
     */
    public boolean done() {
        return (state == SUCCESS || state == EXECUTION_FAILURE || state == FEASIBILITY_FAILURE);
    } // End of done/0
    
    /**
     * Returns the execution state of the behaviour
     * @return <code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    public int getState() {
        return state;
    } // End of getState/0
    
    /**
     * Sets the execution state of the behaviour
     * @param state <code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    public void setState(int state) {
        this.state = state;
    } // End of setState/1
    
} // End of class SemanticBehaviour
