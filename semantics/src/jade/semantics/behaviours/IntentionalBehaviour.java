/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2004 France T�l�com
 
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


import java.util.HashSet;
import java.util.Iterator;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.SemanticRepresentation.FeedBackData;
import jade.semantics.kbase.FilterKBaseImpl;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.kbase.observer.ObserverAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * This behaviour his mainly used by the ActionPerformance, the 
 * RationalityPrinciple, and the Planning SIP to handle correctly the intentions
 * of the agent. This behaviour extends jade.core.behaviours.SequentialBehaviour,
 *  and encapsulates only one sub-behaviour.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/12 Revision: 1.0
 */
public class IntentionalBehaviour extends SequentialBehaviour implements SemanticBehaviour {
    
    
    /**
     * The intention to add in the belief base
     */
    private Formula intention;
    
    /**
     * The encapsulated behaviour
     */
    private SemanticBehaviour behaviour;
    
    /**
     * Index of the semantic interpretation principle that generates this 
     * behaviour 
     */
    private int sipIndex;
    
    /**
     * State of this behaviour
     */
    private int state;
    
    /**
     * Data to feed back
     */
    private FeedBackData dataToFeedBack;
    
    /**
     * Pattern to apply the behaviour 
     */
    private Formula donePattern;

    /**
     * Pattern to apply the behaviour
     */
    private Formula dataPattern;
    
    /**
     * Pattern to apply the behaviour
     */
    private Formula existPattern;
    
    private Formula observedIntention;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates an IntentionalBehaviour on the given behaviour.
     * @param behaviour the encapsulated behaviour
     * @param intention the intention to add in the belief base
     * @param index index of the semantic interpretation principle that 
     * generates this behaviour
     * @param data data to feed back
     */
    public IntentionalBehaviour(SemanticBehaviour behaviour, Formula intention, int index, FeedBackData data) {
        super();
        this.behaviour = behaviour;
        this.intention = intention;
        addSubBehaviour((Behaviour)behaviour);
        setBehaviourName("Intention for: " +intention);
        sipIndex = index;
        state = START;
        dataToFeedBack = data;
        donePattern = SLPatternManip.fromFormula("(done ??action ??phi)");
        dataPattern = SLPatternManip.fromFormula("(I ??agent (B ??sender ??phi))");
        existPattern = SLPatternManip.fromFormula("(I ??agent (B ??sender (exists ?e (done (; ??act ?e)))))");
        observedIntention = new NotNode(intention);
    } // End of IntentionalBehaviour/2
    
    /*********************************************************************/
    /**                         PUBLIC METHODS                          **/
    /*********************************************************************/
    
    public void onStart() {
        ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().addObserver(new ObserverAdapter(observedIntention) {
            public void notify(ListOfMatchResults listOfMatchResults) {
                ((SemanticAgent)IntentionalBehaviour.this.myAgent).getSemanticCapabilities().getMyKBase().removeObserver(
                        new Finder() {
                            public boolean identify(Object object) {
                                if (object instanceof Observer) {
                                    return ((Observer)object).getObservedFormula().equals(observedIntention);
                                }
                                return false;
                            }
                        });
                IntentionalBehaviour.this.myAgent.removeBehaviour(IntentionalBehaviour.this);
            }
        });
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
        if (behaviour.getState() == SUCCESS) {
            if (dataToFeedBack != null) {
                MatchResult matchDoneResult =SLPatternManip.match(donePattern, dataToFeedBack.getGoal());
                try {
                    if (matchDoneResult != null) {
                        if (!Tools.isCommunicativeActionFromMeToReceiver((ActionExpression)matchDoneResult.getTerm("action"), dataToFeedBack.getReceiver(), (SemanticAgent)myAgent)) {
                            ((SemanticAgent)myAgent).getSemanticCapabilities().getSemanticInterpreterBehaviour().interpret(
                                    new SemanticRepresentation(((Formula)SLPatternManip.instantiate(existPattern,
                                            "agent", ((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName(),
                                            "sender", dataToFeedBack.getReceiver(),
                                            "act", matchDoneResult.getTerm("action"))).getSimplifiedFormula()));
                        }
                    } else if (!dataToFeedBack.getGoal().isMentalAttitude(dataToFeedBack.getReceiver())) {  
                        ((SemanticAgent)myAgent).getSemanticCapabilities().getSemanticInterpreterBehaviour().interpret(
                                new SemanticRepresentation(((Formula)SLPatternManip.instantiate(dataPattern,
                                        "agent", ((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName(),
                                        "sender", dataToFeedBack.getReceiver(),
                                        "phi", dataToFeedBack.getGoal())).getSimplifiedFormula()));
                    }
                } catch (SLPatternManip.WrongTypeException e) {
                    e.printStackTrace();
                }
            }
            state = SUCCESS;
        } else if (behaviour.getState() == FEASIBILITY_FAILURE) {
            SemanticRepresentation sr = new SemanticRepresentation();
            sr.setSemanticInterpretationPrincipleIndex(sipIndex+1);
            sr.setSLRepresentation(intention);
            sr.setDataToFeedback(dataToFeedBack);
            ((SemanticAgent)myAgent).getSemanticCapabilities().getSemanticInterpreterBehaviour().interpret(sr);
            state = FEASIBILITY_FAILURE;
            
        } else if (behaviour.getState() == EXECUTION_FAILURE) {
            state = EXECUTION_FAILURE;
            // TODO Generate a Failure message if needed
        }
        ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().removeObserver(
                new Finder() {
                    public boolean identify(Object object) {
                        if (object instanceof Observer) {
                            return ((Observer)object).getObservedFormula().equals(observedIntention);
                        }
                        return false;
                    }
                });
        ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().retractFormula(intention);
        return 0;
    } // End of onEnd/0
    
    /**
     * Checks the termination of the behaviour
     * @return true if the state of the behaviour is <code>SUCCESS</code>, 
     * <code>FEASIBILITY_FAILURE</code>, or <code>EXECUTION_FAILURE</code>.
     * @see jade.core.behaviours.CompositeBehaviour#checkTermination(boolean, int)
     */
    protected boolean checkTermination(boolean currentDone, int currentResult) {
        return (behaviour.getState() == SUCCESS || behaviour.getState() == FEASIBILITY_FAILURE || behaviour.getState() == EXECUTION_FAILURE);
    } //End of checkTermination/2
    
    /**
     * Sets the state of the semantic behaviour
     * @param state the state
     */
    public void setState(int state) {
        this.state = state;
    } // End of setState/1
    
    /**
     * Returns the state of the behaviour
     * @return the state of the behaviour
     */
    public int getState() {
        return state;
    } // End of getState/0
    
} // End of class IntentionalBehaviour
