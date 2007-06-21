/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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


import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.kbase.observer.ObserverAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.SL;

/**
 * This behaviour his mainly used by the ActionPerformance, the 
 * RationalityPrinciple, and the Planning SIP to handle correctly the intentions
 * of the agent. This behaviour extends jade.core.behaviours.SequentialBehaviour,
 *  and encapsulates only one sub-behaviour.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/12 Revision: 1.0
 * @version Date: 2007/02/28 Revision: 1.1 - Thierry Martinez
 */
public class IntentionalBehaviour extends SequentialBehaviour implements SemanticBehaviour {
    
    
    /**
     * The encapsulated behaviour
     */
    private ActionExpression plan;
	
	/**
	 * The capabilities of the holder agent.
	 */
	private SemanticCapabilities myCapabilities;
	
	/**
	 * The sr that represents the intention attached to this behaviour
	 */
	private SemanticRepresentation intentionSR;
	
	private SemanticBehaviour planBehaviour;
	
    
	private Formula notIntention;

	/**
     * Observer to monitor the dropout of the intention attached to this 
     * behaviour 
     */
    private Observer notIntentionObserver;
    
    private SemanticRepresentation afterPlan = null;
    
    private static Term SUCCEED = SL.fromTerm("(action foo SUCCEED)");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates an IntentionalBehaviour on the given behaviour.
     * @param plan the plan to execute to reach the intention associated to this IntentionalBehaviour
     * @param capabilities the capabilities of the owner agent
     * @param intentionSR the SR representing the intention associated to this IntentionalBehaviour,
     *                    which is interpreted if the given plan ends on a feasibility failure (so
     *                    that another PlanningSIP can be tried)
     */
    public IntentionalBehaviour(ActionExpression plan, 
    						    SemanticCapabilities capabilities,
    						    SemanticRepresentation intentionSR)
    throws SemanticInterpretationException {
        super();
        this.intentionSR = intentionSR;
		this.myCapabilities = capabilities;
		
		ActionExpression beforePlan = (ActionExpression)intentionSR
				.removeAnnotation(SemanticRepresentation.BEFORE_GOAL_KEY); // Before plan must be executed just once on the first attempt to achieve the intention
		if (beforePlan != null) {
			this.plan = new SequenceActionExpressionNode(
					new AlternativeActionExpressionNode(
							(ActionExpression)beforePlan,
							SUCCEED),
					plan);
		}
		else {
			this.plan = plan;
		}
		
		Object afterPlan = intentionSR.getAnnotation(SemanticRepresentation.REACHED_GOAL_KEY);
		if (afterPlan != null && afterPlan instanceof ActionExpression) {
			this.plan = new SequenceActionExpressionNode(
					this.plan,
					new AlternativeActionExpressionNode(
							(ActionExpression)afterPlan,
							SUCCEED));
		}
		else {
			this.afterPlan = (SemanticRepresentation)afterPlan;
		}
		
		this.planBehaviour = (SemanticBehaviour)capabilities.getMySemanticActionTable().getSemanticActionInstance(this.plan).getBehaviour(); 
        addSubBehaviour((Behaviour)this.planBehaviour);
        
        setBehaviourName("Intention for: " + intentionSR);

        this.notIntention = new NotNode(intentionSR.getSLRepresentation());
        notIntentionObserver = new ObserverAdapter(myCapabilities.getMyKBase(), notIntention) {
            public void action(ListOfMatchResults listOfMatchResults) {
				myCapabilities.getMyKBase().removeObserver(notIntentionObserver);
                IntentionalBehaviour.this.myAgent.removeBehaviour(IntentionalBehaviour.this);
            }
        };
    } 
        
    /*********************************************************************/
    /**                         PUBLIC METHODS                          **/
    /*********************************************************************/
    
    public void onStart() {
		getMySemanticCapabilities().getMyKBase().addObserver(notIntentionObserver);
		notIntentionObserver.update(null);
    }
    
    
    /**
     * If the internal behaviour finish with <code>SUCCESS</code>: <br>
     * interprets the feedback if needed, and sets the state of this behaviour to 
     * <code>SUCCESS</code>. Reinserts  the same intention to be interpreted  by 
     * another semantic interpretation principle, if the behaviour finish with 
     * <code>FEASIBILITY_FAILURE</code>.In this case, the state of the behaviour
     *  is set to <code>FEASIBILITY_FAILURE</code>. Finally, if the internal 
     *  behaviour finish with <code>EXECUTION_FAILURE</code>, the state is set
     *  to <code>EXECUTION_FAILURE</code>. 
     * @return 0
     * @see jade.core.behaviours.Behaviour#onEnd()
     */
    public int onEnd() {
    	// To end properly with the not intention observers
        getMySemanticCapabilities().getMyKBase().removeObserver(notIntentionObserver);
        
        // To go on depending on the result of the behaviour
        if (getState() == FEASIBILITY_FAILURE) {
        	getMySemanticCapabilities().interpret(intentionSR);
        }
        else {
        	getMySemanticCapabilities().interpret(notIntention);
        	if (getState() == SUCCESS) {
        		if (afterPlan != null) {
        			getMySemanticCapabilities().interpret(afterPlan);
        		}
        	} 
        	else if (getState() == EXECUTION_FAILURE) {
        		SemanticRepresentation executionFailureSR =
        			(SemanticRepresentation)intentionSR.getAnnotation(SemanticRepresentation.FAILED_GOAL_KEY);
        		if (executionFailureSR != null) {
        			getMySemanticCapabilities().interpret(executionFailureSR);
        		}
        	}
        }
        return 0;
    } 
    
    /**
     * Checks the termination of the behaviour
     * @return true if the state of the behaviour is <code>SUCCESS</code>, 
     * <code>FEASIBILITY_FAILURE</code>, or <code>EXECUTION_FAILURE</code>.
     * @see jade.core.behaviours.CompositeBehaviour#checkTermination(boolean, int)
     */
    protected boolean checkTermination(boolean currentDone, int currentResult) {
        return (getState() == SUCCESS || getState() == FEASIBILITY_FAILURE || getState() == EXECUTION_FAILURE);
    } 
    
    /**
     * Sets the state of the semantic behaviour
     * @param state the state
     */
    public void setState(int state) {
        planBehaviour.setState(state);
    } 
    
    /**
     * Returns the state of the behaviour
     * @return the state of the behaviour
     */
    public int getState() {
        return planBehaviour.getState();
    } 
    
	/* (non-Javadoc)
	 * @see jade.semantics.behaviours.SemanticBehaviour#getMySemanticCapabilities()
	 */
	public SemanticCapabilities getMySemanticCapabilities() {
		return myCapabilities;
	}
} 
