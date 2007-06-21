/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France Télécom

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
 * created on 15 déc. 2006 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.behaviours;

import jade.core.Agent;
import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.NotificationSIPAdapter;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;

import java.util.Date;

import sun.security.krb5.internal.ac;

/**
 * @author Vincent Louis - France Telecom
 *
 */
public abstract class PrimitiveActionBehaviour extends SemanticBehaviourBase {

    /**
     * Semantic action, which the primitive behaviour is associated to. 
     */
    protected SemanticAction action;

    private long defaultTimeout;
    private static long DEFAULT_TIMEOUT_VALUE = 60000;
    protected static String REPLY_BY_KEY = "REPLY_BY"; 
    
    protected boolean firstRound = true;
    
	/**
	 * Constructor
	 */
	public PrimitiveActionBehaviour(SemanticAction action) {
		this(action, DEFAULT_TIMEOUT_VALUE);
	}
	
	public PrimitiveActionBehaviour(SemanticAction action, long defaultTimeout) {
		super(action.getSemanticCapabilities());
		this.action = action;
		this.defaultTimeout = defaultTimeout;
		firstRound = true;
        setBehaviourName(action.getClass().toString());
	}

	/* (non-Javadoc)
	 * @see jade.semantics.behaviours.SemanticBehaviourBase#action()
	 */
	public void action() {
		if (!getMySemanticCapabilities().getAgentName().equals(action.getActor())) {
			if (firstRound) {
				Date timeout = (Date)root().getDataStore().get(REPLY_BY_KEY);
				// As the NotificationSIPAdapter below will be invoked when the Behaviour will be removed from the agent,
				// we must backup the reference to the agent, to be used further by this SIP
				final Agent myAgent = super.myAgent;
				Formula effect = (action.getRationalEffect() instanceof TrueNode ? action.getPostCondition() : action.getRationalEffect());
				if (effect instanceof TrueNode) {
					try {
						effect = new DoneNode(action.toActionExpression(), new TrueNode());
					} catch (SemanticInterpretationException e) {
						e.printStackTrace();
						state = EXECUTION_FAILURE;
						return;
					}
				}
				NotificationSIPAdapter waitingSIP = new NotificationSIPAdapter(getMySemanticCapabilities(), effect) {
					protected void notify(MatchResult applyResult, SemanticRepresentation sr) {
						myAgent.addBehaviour(PrimitiveActionBehaviour.this.root());
						state = SUCCESS;
					}
					protected void timeout() {
						myAgent.addBehaviour(PrimitiveActionBehaviour.this.root());
						state = FEASIBILITY_FAILURE;
					}
				};
				if (timeout == null) {
					waitingSIP.setTimeout(defaultTimeout);
				}
				else {
					waitingSIP.setTimeout(timeout);
				}
				
				getMySemanticCapabilities().getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(waitingSIP);
				myAgent.removeBehaviour(root());
			}
			firstRound = false;
        }
        else {
        	if (firstRound) {
        		firstRound = false;
        		if (!checkFeasibility()) {
        			state = FEASIBILITY_FAILURE;
        			return;
        		}
        	}
        	doAction();
        	if (state == SUCCESS) {
        		try {
					getMySemanticCapabilities().getSemanticInterpreterBehaviour().interpret(
							new SemanticRepresentation(new DoneNode(action.toActionExpression(), new TrueNode())), true);
				} catch (SemanticInterpretationException e) {
					e.printStackTrace();
				}
        	}
        }
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#reset()
	 */
	public void reset() {
		super.reset();
		firstRound = true;
	}
	
	protected abstract void doAction();
	
	protected boolean checkFeasibility() {
		return (getMySemanticCapabilities().getMyKBase().query(action.getFeasibilityPrecondition()) != null);
	}
}

