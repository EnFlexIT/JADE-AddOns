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


package jade.semantics.ext.institutions.sips.planning;

/*
 * Class PerseveranceSIP.java
 * Created by Carole Adam, December 2007
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.sips.interaction.InstitutionalIntentionTransfer;
import jade.semantics.ext.institutions.sips.interaction.ObligationNotification;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP intercepts (is_trying agent institution action) formulas,
 * corresponding to actions that the agent is obliged to perform in 
 * the given institution, but that he has previously failed to perform
 * because feasibility preconditions were false.
 * (This predicate is put as an annotation (for feasibility failures) 
 * to intentions generated by an ObligationTransferSIPAdapter from 
 * the agent's obligations)
 * TODO : all intentions should trigger this kind of perseverance
 * 
 * This SIP posts two observers:
 * 	- one waiting for the feasibility precondition to become true: in this
 * case it interprets the intention to retry the failed action (that was
 * abandoned when it was recognised (temporarily) not feasible). This time
 * this intention should succeed;
 *  - one waiting for the achievement of the goal (possibly independent from
 *  the agent performing his own plan to reach it) in order to retract the
 *  is_trying formula (the agent stops trying to reach the goal once achieved).
 * 
 * This way, the agent perseveres in his plans to respect his obligations: if 
 * the obligation cannot be reached at once, he will try again as soon as 
 * possible.
 * 
 * Finally this predicate is asserted to allow the agent to answer when he 
 * is notified of his (not respected yet) obligation, or when he receives a
 * request on the same action
 * (see {@link ObligationNotification} and {@link InstitutionalIntentionTransfer})
 * 
 * Modification: now this SIP decomposes the precondition to only observe the
 * part of the precondition (if it is a AndNode) that was false
 * 
 * @author wdvh2120 Carole Adam
 * @version 1.0 date December 2007
 * @version 1.1 date 13 February 2008, interpret intention to retry instead of obligation
 * @version 1.2 date 31 March 2008, only observe the false part of the and-precondition
 * @since JSA 1.5
 */

public class PerseveranceSIP extends SemanticInterpretationPrinciple {

	private final boolean DEBUG = false;
	
	public PerseveranceSIP(SemanticCapabilities capabilities) {
		super(capabilities,
				(Formula)SL.instantiate(InstitutionTools.istrying_AGENT_INSTITUTION_ACTION,
						"agent",capabilities.getAgentName()),
								// class index FIXME
								SemanticInterpretationPrincipleTable.ACTING);
	}


	/**
	 * This method handle the is_trying predicates intercepted by this SIP.
	 * It posts 2 observers responsible for watching if the feasibility precondition
	 * of the action being tried become true (and then re-try), and if the goal is
	 * reached (and then stop trying).
	 * The interpreted SR is then directly asserted into the agent's KBase (returned
	 * with a maximal interpretation index).
	 * 
	 * FIXME: one shot observers ? (second one: yes; first one: ??)
	 */
	/*
	 * (non-Javadoc)
	 * @see jade.semantics.interpreter.SemanticInterpretationPrinciple#apply(jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {
		MatchResult applyResult = pattern.match(sr.getSLRepresentation());
		if (applyResult != null) {
			InstitutionTools.printTraceMessage(myCapabilities.getAgentName()+" applies PerseveranceSIP to "+sr,DEBUG);

			// the action being tried (ActionExpression)
			ActionExpression action = (ActionExpression)applyResult.term("action");

			/* if (is_trying ??action) is interpreted, it is because
			 * the action failed (feasibility failure)
			 * => interested agents must be notified of this failure
			 * as they would be in case of success.
			 * 
			 * This was previously done in PrimitiveActionBehaviour.action 
			 * in case of feasibility failure, and the method was in 
			 * InstActionBehaviour. Moved for JSAinst v2, CA, 29/01/08
			 */
			notifyFailureToInterestedAgents(((BelieveNode)sr.getSLRepresentation()).as_formula(), action);
			
				try {
					// get the feasibility precondition of the action being tried
					Formula precondition = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(
							action).getFeasibilityPrecondition();
					// build the agent's goal (that this action is done)
					Formula doneAction = new DoneNode(action,SL.TRUE);

					// interpret the intention instead of the obligation
					Formula intendToRetry =
						new IntentionNode(myCapabilities.getAgentName(),doneAction);
					InstitutionTools.printTraceMessage("intend to retry = "+intendToRetry,DEBUG);

					// DECOMPOSE A AND-PRECONDITION
					Formula preconditionToObserve;
					if (precondition instanceof AndNode) {
						ListOfFormula list = ((AndNode)precondition).getLeaves();
						ListOfFormula listOfFalseFormulas = new ListOfFormula();
						for (int i=0;i<list.size();i++) {
							Formula f = list.element(i);
							InstitutionTools.printTraceMessage("formula leave f = "+f, DEBUG);
							// only add false formulas to this list
							if (myCapabilities.getMyKBase().query(f) == null) {
								InstitutionTools.printTraceMessage("query(f) returns null, f is false", DEBUG);
								listOfFalseFormulas.add(f);   // new BelieveNode(myCapabilities.getAgentName(),f) ??
							}
						}
						// watch the AndNode or each formula separately ?? FIXME
						// build an AndNode from the list of false formulas
						preconditionToObserve = SL.and(listOfFalseFormulas);
					}
					// if not a AndNode directly observe it
					else {
						preconditionToObserve = precondition; 
					}
										
					/*
					 * POST A FIRST OBSERVER waiting for this precondition to be true
					 * In this case it reinterprets obligation(done(action))
					 * that will be interpreted by ObligationTransfer to create intend(done(action)) (with annotations)
					 * that will make the agent perform this action.
					 * (when the action will be done the second observer will retract the is_trying predicate)
					 */ 
					InstitutionTools.printTraceMessage("observer added on "+preconditionToObserve,DEBUG);
					EventCreationObserver perseveranceObs = 
						new EventCreationObserver(
								// KBase
								myCapabilities.getMyKBase(),
								// observed formula
								preconditionToObserve,
								// triggered event: interprets the intention to retry 
								intendToRetry,
								// semantic interpreter behaviour
								myCapabilities.getSemanticInterpreterBehaviour()) {
						@Override
						public String toString() {
							return "PerseveranceObserver on "+getObservedFormula()+" to trigger "+getSubscribedEvent();
						}
					};
					myCapabilities.getMyKBase().addObserver(perseveranceObs);
					perseveranceObs.update(null);

					/* 
					 * POST A SECOND OBSERVER watching for the realization of the goal
					 * (by the agent or by any other way)
					 * In this case it retracts the is_trying predicate.
					 * 
					 * (FIXME: put an annotation to retract is_trying when the intention succeeds on all
					 * intentions generated by ObligationTransfer (even if is_trying is not asserted) ??
					 */
					EventCreationObserver resultWatchingObs =
						new EventCreationObserver(
								// KBase
								myCapabilities.getMyKBase(),
								// observed formula: the tried action was finally performed
								doneAction,
								// triggered event: stop trying
								new NotNode(sr.getSLRepresentation()),
								myCapabilities.getSemanticInterpreterBehaviour());
					myCapabilities.getMyKBase().addObserver(resultWatchingObs);
					resultWatchingObs.update(null);

					// return the original SR to be asserted in the agent's KBase
					// (is_trying is asserted but not interpreted any further)
					ArrayList result = new ArrayList();
					sr.setSemanticInterpretationPrincipleIndex(SemanticRepresentation.NO_LONGER_APPLICABLE);
					result.add(sr);
					return result;
				}
				catch(SemanticInterpretationException sie) {
					sie.printStackTrace();
					return null;
				}
				
				/* 
				 * TODO : observer for cancellation of intention
				 * stop trying if the request is cancelled ?
				 * PB: what if there are several sources of this intention ?
				 */
				
			//}//end if action is an ActionExpression
		}//end if applyResult not null
		return null;
	}

	/********************
	 * AUXILIARY METHOD *
	 ********************/

	// called by PrimitiveActionBehaviour.action in case of
	// feasibility failure to inform interested agents about the failed
	// attempt to perform this action (requested or notified as obligatory)
	public void notifyFailureToInterestedAgents(Formula isTryingFormula,ActionExpression actionExpr) {
		InstitutionTools.printTraceMessage(actionExpr+" has failed...",DEBUG);

		// only InstitutionalAgents have this SIP so the class cast is OK
		// do not remove interested agents since they should be informed when the
		// action finally succeeds later
		ArrayList interestedAgents = ((InstitutionalAgent)myCapabilities.getAgent()).getInterestedAgents(actionExpr,false);
		Formula intendToInformPattern = InstitutionTools.buildIntendToInform(isTryingFormula,myCapabilities.getAgentName(), new MetaTermReferenceNode("receiver"));
		for (int i=0;i<interestedAgents.size();i++) {
			Formula intendToInformReceiverK = intendToInformPattern.instantiate("receiver",(Term)interestedAgents.get(i));
			myCapabilities.interpret(intendToInformReceiverK);
		}
	}
}
