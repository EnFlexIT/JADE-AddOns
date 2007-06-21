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
 * created on 7 mars 2007 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.interpreter.sips.adapters;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.LoopingInstantiationException;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * @author Vincent Louis - France Telecom
 *
 */
public class CFPSIPAdapter extends QueryRefPreparationSIPAdapter {

	static Formula PROPOSE_FORMULA_PATTERN = SL.fromFormula(
			"(or (not (I ??__agent (done ??__act ??__condition))) (I ??myself (done ??__act ??__condition)))");
	
	Formula equals_all_VARS_PHI_VALUES = SL.fromFormula("(= (all ??__vars " + PROPOSE_FORMULA_PATTERN + ") ??__values)");
		
	private Term actPattern;
	
	private Formula conditionPattern;
	
	private Term agentPattern;
	
//	/**
//	 * @param capabilities
//	 * @param timeout
//	 */
//	public CFPSIPAdapter(SemanticCapabilities capabilities, Date timeout) {
//		super(capabilities, timeout);
//	}
//
//	/**
//	 * @param capabilities
//	 * @param timeout
//	 */
//	public CFPSIPAdapter(SemanticCapabilities capabilities, long timeout) {
//		super(capabilities, timeout);
//	}

	public CFPSIPAdapter(SemanticCapabilities capabilities) {
		this(capabilities, QueryRefPreparationSIPAdapter.IRE_QUANTIFIER, new MetaTermReferenceNode("__ireVariables"),
				new MetaTermReferenceNode("__act"),
				new MetaFormulaReferenceNode("__condition"),
				new MetaTermReferenceNode("__agent"));
	}
	
	/**
	 * @param capabilities
	 */
	public CFPSIPAdapter(SemanticCapabilities capabilities, int ireQuantifierPattern,
			Term ireVariablesPattern, Term actPattern, Formula conditionPattern, Term agentPattern) {
		super(capabilities, ireQuantifierPattern, ireVariablesPattern, PROPOSE_FORMULA_PATTERN, agentPattern);
		this.actPattern = actPattern;
		this.conditionPattern = conditionPattern;
		this.agentPattern = agentPattern;
		equals_all_VARS_PHI_VALUES = equals_all_VARS_PHI_VALUES.instantiate("myself", capabilities.getAgentName());
	}


	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.QueryRefPreparationSIPAdapter#prepareQueryRef(jade.semantics.lang.sl.grammar.IdentifyingExpression, jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.semantics.interpreter.SemanticRepresentation)
	 */
	final protected ArrayList prepareQueryRef(IdentifyingExpression ire, MatchResult ireVariablesMatch, MatchResult ireFormulaMatch, MatchResult agentMatch, ArrayList result, SemanticRepresentation sr) {
		Term agent = ireFormulaMatch.term("__agent");
		MatchResult agentInFormulaMatch = agentPattern.match(agent);
		if (agentInFormulaMatch != null && agentInFormulaMatch.equals(agentMatch)) {
			ActionExpression act = (ActionExpression)ireFormulaMatch.term("__act");
			Term myself = myCapabilities.getAgentName();
			MatchResult actMatch = actPattern.match(act);
			if (act instanceof ActionExpressionNode &&
					myself.equals(ireFormulaMatch.term("myself")) &&
					!myself.equals(agent) &&
					myself.equals(act.getActor()) &&
					actMatch != null) {
				Formula condition = ireFormulaMatch.formula("__condition");
				MatchResult conditionMatch = conditionPattern.match(condition);
				if (conditionMatch != null) {
					return prepareProposal(ire, (ActionExpressionNode)act, condition, agent,
							actMatch, conditionMatch, agentMatch, result, sr);
				}
			}
		}
		return null;
	}

	protected ArrayList prepareProposal(IdentifyingExpression ire,
											ActionExpressionNode act, Formula condition, Term agent,
											MatchResult actMatch, MatchResult conditionMatch, MatchResult agentMatch,
											ArrayList result, SemanticRepresentation sr) {
		IdentifyingExpression simplifiedIre = ire;
		simplifiedIre.as_formula(condition);
		assertProposals(simplifiedIre.as_term(), agent, act, condition, myCapabilities.getMyKBase().queryRef(simplifiedIre));
		return result;
	}
	
	protected void assertProposals(Term variables, Term agent, ActionExpressionNode act, Formula condition, ListOfTerm values) {
		Formula assertion = (Formula)equals_all_VARS_PHI_VALUES.getClone();
		try {
			SL.set(assertion, "__vars", variables);
			SL.set(assertion, "__agent", agent);
			SL.set(assertion, "__act", act);
			SL.set(assertion, "__condition", condition);
			SL.set(assertion, "__values", new TermSetNode(values));
			SL.substituteMetaReferences(assertion);
		} catch (WrongTypeException e) {
			e.printStackTrace();
			return;
		} catch (LoopingInstantiationException e) {
			e.printStackTrace();
			return;
		}
		myCapabilities.getMyKBase().assertFormula(assertion);
	}
}

