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

package jade.semantics.interpreter.sips.adapters;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This Semantic Inperpretation Principle Adapter makes it possible to finely
 * control the goals from other agents the semantic agent is allowed to adopt 
 * for himself (in a cooperative way).
 * The goal adoption control must be specified within the abstract
 * {@link #doApply(MatchResult, MatchResult, ArrayList, ArrayList, SemanticRepresentation)}
 * method.
 * <br>
 * Roughly speaking, this SIP adapter consumes Semantic Representations of the
 * form <code>(B ??myself (I ??agent ??goal))</code>, and, if applicable (that
 * is, if the goal adoption is decided), produces SRs of the form
 * <code>(I ??myself ??goal)</code>.
 * </br>
 * <br>
 * Several instances of such a SIP may be added to the SIP table of the agent.
 * </br>
 * <br>
 * The goals to adopt, which are not handled by an Intention Transfer SIP Adapter,
 * will be handled by the default generic
 * {@link jade.semantics.interpreter.sips.IntentionTransfer} SIP (that is, the
 * semantic agent will systematically adopt other agents' goals, and so
 * systematically try to achieve their intentions).
 * </br>
 * @see jade.semantics.interpreter.sips.IntentionTransfer
 * @author Vincent Louis - France Telecom
 * @since JSA 1.4
 */
public abstract class IntentionTransferSIPAdapter extends
		SemanticInterpretationPrinciple {
	

    /**
     * Patterns used to compute the application of this SIP
     */
    private Formula b_SENDER_i_myself_PHI;
    private Term action_myself_do_PHI;
    private Formula b_SENDER_PHI;
    private Formula i_myself_PHI; 
    private Formula i_myself_b_SENDER_not_i_myself_PHI;
    private Formula i_myself_b_SENDER_forall_not_b_myself_feasible_PHI;
    private Formula not_i_myself_PHI;
    private Formula i_myself_b_SENDER_PHI;
	
	Formula goalPattern;
	Term agentPattern;
	
	boolean feedBackRequired;
    	
	/**
	 * Creates an Intention Transfer SIP Adapter that controls the adoption of a
	 * given pattern of goal intended by a given pattern of agent. If a feed-back
	 * is required, this SIP may additionally produce appropriate SRs to send to
	 * the external agent intending the goal to adopt:
	 * <ul>
	 *   <li>if the goal is adopted: the equivalent of an <code>AGREE</code> act
	 *       to confirm the goal has been adopted, and the equivalent of an
	 *       <code>INFORM-DONE</code> act to notify when the goal has been
	 *       actually achieved.</li>
	 *   <li>if the goal is not adopted: the equivalent of a <code>REFUSE</code>
	 *       act to notify the semantic agent does not want to adopt the goal.</li>
	 * </ul>
	 * The actual form of the generated feed-bakc acts depends on the form of
	 * the goal. For example, if the goal is <code>(done <i>myAction</i>)</code>,
	 * then the agree feed-back will be an <code>AGREE</code> act on the
	 * <code><i>myAction</i></code> action. If it is a fact like
	 * <code>(light on)</code>, then the agree feed-back will be an
	 * <code>INFORM</code> act on <code>(I <i>myself</i> (light on))</code>, where
	 * <code><i>myself</i></code> equals the semantic agent's AID.
	 * 
	 * @param capabilities      {@link SemanticCapabilities} instance of the
	 *                          semantic agent owning this instance of SIP.
	 * @param goalPattern       the pattern of the goal to adopt, which is
	 *                          controlled by this Intention SIP adapter.
	 * @param agentPattern      the pattern of the external agent intending the
	 *                          goal to adopt, which is controlled by this SIP
	 *                          adapter.
	 *                          <br>If set to <code>null</code>, there is no
	 *                          control on the agent intending the goal to
	 *                          adopt.</br>
	 * @param feedBackRequired  if <code>true</code>, the Intention Transfer
	 *                          SIP will additionally produce SRs to generate
	 *                          feed-back to the agent intending the goal to
	 *                          adopt.
	 */
	public IntentionTransferSIPAdapter(SemanticCapabilities capabilities, Formula goalPattern, Term agentPattern, boolean feedBackRequired) {
		super(capabilities,
			  "(B ??myself (I ??sender ??phi))",
			  SemanticInterpretationPrincipleTable.INTENTION_TRANSFER);
        i_myself_PHI = SL.fromFormula("(I " +  myCapabilities.getAgentName() + " ??phi)");
        b_SENDER_i_myself_PHI = SL.fromFormula("(B ??sender (I " + myCapabilities.getAgentName() + " ??phi))");
        action_myself_do_PHI = SL.fromTerm("(action " + myCapabilities.getAgentName() + " (DO ??__phi))");
        b_SENDER_PHI = SL.fromFormula("(B ??sender ??phi)");
        i_myself_b_SENDER_forall_not_b_myself_feasible_PHI = SL.fromFormula("(I " + myCapabilities.getAgentName() + " (B ??__sender (forall ?e (not (B " + myCapabilities.getAgentName() + " (feasible ?e ??__phi))))))");

        not_i_myself_PHI = SL.fromFormula("(not (I " +  myCapabilities.getAgentName() + " ??phi))");        
        i_myself_b_SENDER_not_i_myself_PHI = SL.fromFormula("(I " +  myCapabilities.getAgentName() + " (B ??sender (not (I " +  myCapabilities.getAgentName() + " ??phi))))");

        i_myself_b_SENDER_PHI = SL.fromFormula("(I " + myCapabilities.getAgentName() + " (B ??sender ??phi))");

        this.goalPattern = goalPattern;
		this.agentPattern = (agentPattern == null ? new MetaTermReferenceNode("__any_agent") :
                                                    agentPattern);
		this.feedBackRequired = feedBackRequired;
	}
	
	/**
	 * Creates an Intention Transfer SIP Adapter equivalent to the
	 * {@link #IntentionTransferSIPAdapter(SemanticCapabilities, Formula, Term, boolean)}
	 * constructor, whith the <code>feedBackRequired</code> parameter set to
	 * <code>true</code>. In other words, the created SIP adapter automatically
	 * produces, when applied, SRs to send some feed-back to the external agent
	 * intending the goal to adopt.
	 * 
	 * @param capabilities      {@link SemanticCapabilities} instance of the
	 *                          semantic agent owning this instance of SIP.
	 * @param goalPattern       the pattern of the goal to adopt, which is
	 *                          controlled by this Intention SIP adapter.
	 * @param agentPattern      the pattern of the external agent intending the
	 *                          goal to adopt, which is controlled by this SIP
	 *                          adapter.
	 *                          <br>If set to <code>null</code>, there is no
	 *                          control on the agent intending the goal to
	 *                          adopt.</br>
	 */
	public IntentionTransferSIPAdapter(SemanticCapabilities capabilities, Formula goalPattern, Term agentPattern) {
		this(capabilities, goalPattern, agentPattern, true);
	}

	/**
	 * Creates an Intention Transfer SIP Adapter that controls the adoption of a
	 * given pattern of goal intended by a given pattern of agent.
	 * Equivalent to the
	 * {@link #IntentionTransferSIPAdapter(SemanticCapabilities, Formula, Term, boolean)}
	 * constructor, with the <code>goalPattern</code> and
	 * <code>agentPattern</code> parameters specified as
	 * {@link String} objects (representing FIPA-SL expressions).
	 * 
	 * @param capabilities      {@link SemanticCapabilities} instance of the
	 *                          semantic agent owning this instance of SIP.
	 * @param goalPattern       the pattern of the goal to adopt, which is
	 *                          controlled by this Intention SIP adapter.
	 * @param agentPattern      the pattern of the external agent intending the
	 *                          goal to adopt, which is controlled by this SIP
	 *                          adapter.
	 *                          <br>If set to <code>null</code>, there is no
	 *                          control on the agent intending the goal to
	 *                          adopt.</br>
	 * @param feedBackRequired  if <code>true</code>, the Intention Transfer
	 *                          SIP will additionally produce SRs to generate
	 *                          feed-back to the agent intending the goal to
	 *                          adopt.
	 */
	public IntentionTransferSIPAdapter(SemanticCapabilities capabilities, String goalPattern, String agentPattern, boolean feedBackRequired) {
		this(capabilities,
			 SL.fromFormula(goalPattern), 
			 (agentPattern == null ? null : SL.fromTerm(agentPattern)),
			 feedBackRequired);
	}

	/**
	 * Creates an Intention Transfer SIP Adapter equivalent to the
	 * {@link #IntentionTransferSIPAdapter(SemanticCapabilities, String, String, boolean)}
	 * constructor, whith the <code>feedBackRequired</code> parameter set to
	 * <code>true</code>. In other words, the created SIP adapter automatically
	 * produces, when applied, SRs to send some feed-back to the external agent
	 * intending the goal to adopt.
	 * 
	 * @param capabilities      {@link SemanticCapabilities} instance of the
	 *                          semantic agent owning this instance of SIP.
	 * @param goalPattern       the pattern of the goal to adopt, which is
	 *                          controlled by this Intention SIP adapter.
	 * @param agentPattern      the pattern of the external agent intending the
	 *                          goal to adopt, which is controlled by this SIP
	 *                          adapter.
	 *                          <br>If set to <code>null</code>, there is no
	 *                          control on the agent intending the goal to
	 *                          adopt.</br>
	 */
	public IntentionTransferSIPAdapter(SemanticCapabilities capabilities, String goalPattern, String agentPattern) {
		this(capabilities, goalPattern, agentPattern, true);
	}
	
	
	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.SemanticInterpretationPrinciple#apply(jade.semantics.interpreter.SemanticRepresentation)
	 */
	final public ArrayList apply(SemanticRepresentation sr)
			throws SemanticInterpretationPrincipleException {
		try {
			MatchResult matchResult = pattern.match(sr.getSLRepresentation());
			if (matchResult != null) {
				Term sender = matchResult.term("sender");
				Formula goal = matchResult.formula("phi");
				ActionExpression action = (goal instanceof DoneNode ? (ActionExpression)((DoneNode)goal).as_action() : null);
				
				if (action == null	|| !action.getActors().contains(sender)) {
					if (myCapabilities.getMyKBase().query(goal) == null) {
						Formula intention = i_myself_PHI.instantiate("phi", goal).getSimplifiedFormula();
						if (myCapabilities.getMyKBase().query(intention) == null) {
							MatchResult matchGoal = goalPattern.match(goal);
							MatchResult matchAgent = agentPattern.match(sender);
							
							if (matchAgent != null && matchGoal != null) {
								// Prepare the SR to produce if intention transer is accepted
								ArrayList acceptResult = new ArrayList();

                                SemanticRepresentation intentionSR = new SemanticRepresentation(intention, sr);
                                // Sets the feed-back data
                                if (feedBackRequired &&
                                		((action != null && !Tools.isCommunicativeActionFromMeToReceiver(action, sender, myCapabilities))
                                				|| (action == null && !goal.isMentalAttitude(sender)))) {
                                	intentionSR.putAnnotation(SemanticRepresentation.BEFORE_GOAL_KEY,
                                			action_myself_do_PHI.instantiate("__phi", new StringConstantNode(
                                    				SL.instantiate(b_SENDER_i_myself_PHI, "sender", sender, "phi", goal).toString())));
                                	intentionSR.putAnnotation(SemanticRepresentation.REACHED_GOAL_KEY,
                                			action_myself_do_PHI.instantiate("__phi", new StringConstantNode(
                                    				SL.instantiate(b_SENDER_PHI, "sender", sender, "phi", goal).toString())));
                                }
                                // Sets the SR to interpret if the accepted goal is eventually unreachable
                            	intentionSR.putAnnotation(SemanticRepresentation.UNREACHABLE_GOAL_KEY,
                            			new SemanticRepresentation(
                            					(Formula)SL.instantiate(i_myself_b_SENDER_forall_not_b_myself_feasible_PHI,
                            							"__sender", sender,
                            							"__phi", goal),
                            					sr));
                                acceptResult.add(intentionSR);
                                
                                // Prepare the SR to produce if intention transfer is refused
								ArrayList refuseResult = new ArrayList();
								
								refuseResult.add(new SemanticRepresentation(
										not_i_myself_PHI.instantiate("phi",goal).getSimplifiedFormula(),
										sr));
								// Sets the feed-back data
								if (feedBackRequired) {
									refuseResult.add(new SemanticRepresentation(
											((Formula) SL.instantiate(i_myself_b_SENDER_not_i_myself_PHI, 
													"sender",sender, 
													"phi", goal)).getSimplifiedFormula(), 
											sr));
								}
								
								return doApply(matchGoal, matchAgent, acceptResult, refuseResult, sr);
							}
						}
					} else {
						// Produce a SR informing the sender the goal already holds
						ArrayList result = new ArrayList();
						result.add(new SemanticRepresentation(
								((Formula) SL.instantiate(i_myself_b_SENDER_PHI,
										"sender", sender, 
										"phi", goal)).getSimplifiedFormula(), 
								sr));
						return result;
					}
				}
			}
		} catch (SL.WrongTypeException e) {
			e.printStackTrace();
			throw new SemanticInterpretationPrincipleException();
		}
		return null;
	}
	
	/**
	 * Method to be overriden in each sub-class, to control the goal adoption
	 * defined by this Intention Transfer SIP Adapter.
	 * This method works as the regular
	 * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)}
	 * method and must return either:
	 * <ul>
	 *     <li><code>null</code> if the SIP Adapter is actually not applicable,</li>
	 *     <li>or a list of produced Semantic Representations otherwise.</li>
	 * </ul>
	 * To facilitate the design, the Semantic Representation stating that the
	 * controlled goal has finally to be adopted by the agent is pre-computed
	 * and passed as the <code>acceptResult</code> argument of the method. In
	 * the same way, the <code>refuseResult</code> argument is a pre-computed
	 * set of produced SR that must be returned if the goal has finally not to
	 * be adopted.
	 * <br>
	 * If a more complex activity of the agent is needed to determine whether
	 * the controlled goal must be adopted, this method should return an empty
	 * {@link ArrayList} (meaning the SIP Adapter is "absorbent") and add a
	 * behaviour to the agent, which will perform this activity. When finished,
	 * if the goal must finally be adopted, this behaviour should run a new
	 * interpretation process on the <code>acceptResult</code> pre-computed SR,
	 * by calling the {@link SemanticCapabilities#interpret(SemanticRepresentation)}
	 * method with this parameter. Otherwise, this behaviour should run an
	 * interpretation process on the <code>refuseResult</code> SR.
	 * </br>
	 * @param matchGoal    result of the matching of the pattern of goal to
	 *                     adopt, which is controlled by this Intention Transfer
	 *                     SIP Adapter, against the goal intended by an external
	 *                     agent.
	 * @param matchAgent   result of the matching of the pattern of external
	 *                     agent, which is controlled by this Intention Transfer
	 *                     SIP Adapter, against the external agent intending
	 *                     the goal to adopt.
	 * @param acceptResult pre-computed result to return, if the goal intended by
	 *                     the external agent must finally be adopted by the
	 *                     semantic agent.
	 * @param refuseResult pre-computed result to return, if the goal intended by
	 *                     the external agent must finally not be adopted by the
	 *                     semantic agent.
	 * @param sr           incoming Semantic Representation that triggered the
	 *                     application of this Intention Transfer SIP Adapter (this
	 *                     SR has therefore been consummed).
	 * @return             a list of SR produced by the application of this SIP,
	 *                     or <code>null</code> if this SIP is not applicable.
	 */
	protected abstract ArrayList doApply(MatchResult matchGoal, 
										 MatchResult matchAgent, 
										 ArrayList acceptResult, 
										 ArrayList refuseResult,
										 SemanticRepresentation sr);
}
