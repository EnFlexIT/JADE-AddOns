/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

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

package jade.semantics.actions;

/*
 * Created by Carole Adam, February 2008
 */

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.behaviours.SemanticBehaviourBase;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;


/**
 * This action allows an agent to wait a formula to be true (during a given
 * maximal delay) before continuing his plan
 * ??_until = delay in milliseconds
 * 
 * @author wdvh2120
 *
 */
public class Wait extends SemanticActionImpl implements Cloneable {

	private final boolean DEBUG = false;
	
	Formula waitedFormula;
	long timeout;
	long wakeUpDate;
	final int WAITING = 77;
	
	// until contains a Long value = the maximal delay to wait for
	static Term WAIT_ACTION_PATTERN = SL.term("(action ??" + ACTOR + " (WAIT (fact ??__phi) ??__until))");

	// build prototype
	public Wait(SemanticCapabilities capabilities) {
		super(capabilities);
	}


	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
	 */
	public SemanticAction newAction(ActionExpression actionExpression)
	throws SemanticInterpretationException {
		MatchResult matchResult = WAIT_ACTION_PATTERN.match(actionExpression);
		if (matchResult != null) {
			Wait result;
			try {
				result = (Wait)clone();
				result.setAuthor(matchResult.getTerm(ACTOR));
				result.waitedFormula = matchResult.getFormula("__phi");
				result.timeout = Long.parseLong(matchResult.term("__until").toString());
				result.wakeUpDate = System.currentTimeMillis() + result.timeout;
			} catch (Exception e) { // WrongTypeException or CloneNotSupported
				throw new SemanticInterpretationException("cannot-read-author", SL.string(actionExpression.toString()));
			}
			return result;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.Formula, jade.lang.acl.ACLMessage)
	 */
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
		return null;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#toActionExpression()
	 */
	public ActionExpression toActionExpression()
	throws SemanticInterpretationException {
		ActionExpression result = (ActionExpression)WAIT_ACTION_PATTERN.instantiate(ACTOR, getAuthor());
		result.sm_action(this);
		return result;
	}

	// true preconditions and effects
	public Formula computeFeasibilityPrecondition() throws WrongTypeException {
		return SL.TRUE;
	}

	public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException {
		return SL.TRUE;
	}

	public Formula computePostCondition() throws WrongTypeException {
		return SL.TRUE;
	}

	public Formula computeRationalEffect() throws WrongTypeException {
		return SL.TRUE;
	}




	public Behaviour computeBehaviour() {

		return new SemanticBehaviourBase(capabilities) {

			// The empty action does nothing!
			public void action() {
				try {
					if (getState() == SemanticBehaviour.START) {
						// starting: initialisation of wakeupdate
						//setState(WAITING);
						wakeUpDate = System.currentTimeMillis() + timeout;
						InstitutionTools.printTraceMessage("start!!! formula="+waitedFormula,DEBUG);
						setState(RUNNING);
					}
					else if (getState() == RUNNING) {
						InstitutionTools.printTraceMessage("running...",DEBUG);
						Long current = System.currentTimeMillis();
						InstitutionTools.printTraceMessage("current="+current+"; wakeup="+wakeUpDate,DEBUG);
						if (wakeUpDate - System.currentTimeMillis() > 0) {
							// SPECIAL BUGGING CASE : OR FORMULAS
							// DECOMPOSE OR FORMULAS (otherwise: bug)
							InstitutionTools.printTraceMessage("waited formula="+waitedFormula,DEBUG);
							if ((waitedFormula instanceof BelieveNode) && 
									(((BelieveNode)waitedFormula).as_formula() instanceof OrNode)) {
								InstitutionTools.printTraceMessage("special case !!!!",DEBUG);
								OrNode orFormula = (OrNode) ((BelieveNode)waitedFormula).as_formula();
								Formula phi = orFormula.as_left_formula();
								Formula psi = orFormula.as_right_formula();
								QueryResult qrPhi = capabilities.getMyKBase().query(phi);
								InstitutionTools.printTraceMessage("phi="+phi,DEBUG);
								InstitutionTools.printTraceMessage("qrphi="+qrPhi,DEBUG);
								if ((qrPhi != null) && (qrPhi != QueryResult.UNKNOWN)) {
									setState(SemanticBehaviour.SUCCESS);
								}
								else {
									//second chance
									QueryResult qrPsi = capabilities.getMyKBase().query(psi);
									InstitutionTools.printTraceMessage("psi="+psi,DEBUG);
									InstitutionTools.printTraceMessage("qrpsi="+qrPsi,DEBUG);
									if ((qrPsi != null) && (qrPsi != QueryResult.UNKNOWN)) {
										setState(SemanticBehaviour.SUCCESS);
									}
									else {
										// no leave of the or formula is true: continue
										setState(SemanticBehaviour.RUNNING);
									}
								}
							}
							// NORMAL CASE
							else {
								// query to KBase 
								QueryResult qr = capabilities.getMyKBase().query(waitedFormula);
								InstitutionTools.printTraceMessage("waited formula="+waitedFormula,DEBUG);
								if ((qr != null) && (qr != QueryResult.UNKNOWN)) {
									InstitutionTools.printTraceMessage("behaviour de l'action WAIT("+waitedFormula+"): SUCCESS",DEBUG);
									InstitutionTools.printTraceMessage("qr waited="+qr,DEBUG);
									setState(SemanticBehaviour.SUCCESS);
									InstitutionTools.printTraceMessage(" !!!!!!! myKBASE in WAIT !!!!!!! \n"+capabilities.getMyKBase().toStrings(),DEBUG);
								}
								else {
									InstitutionTools.printTraceMessage("retry",DEBUG);
									setState(RUNNING);
								}
								// if not found: do nothing until end of timeout
							}
						}
						else { // end of timeout
							System.err.println("behaviour de l'action WAIT("+waitedFormula+"): FEASIBILITY FAILURE (timeout elapsed)");
							setState(SemanticBehaviour.FEASIBILITY_FAILURE);
//							behaviour.block(timeout);			 						
						}
					}// end if waiting
//					break;
					else {
						System.err.println("Strange state in WAIT action : "+getState());
					}

//					}//end switch
				}//end try
				catch (Exception e) {
					e.printStackTrace();
					setState(SemanticBehaviour.EXECUTION_FAILURE);
				}

			}
		};
	}



}
