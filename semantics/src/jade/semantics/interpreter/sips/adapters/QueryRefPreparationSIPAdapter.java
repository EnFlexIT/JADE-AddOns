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
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.util.Date;

/**
 * @author Vincent Louis - France Telecom
 *
 */
public abstract class QueryRefPreparationSIPAdapter extends ApplicationSpecificSIPAdapter {
	
	static public int ANY = 1;
	static public int IOTA = 1 <<2;
	static public int SOME = 1 <<3;
	static public int ALL = 1 <<4;
	
	static public int IRE_QUANTIFIER = ANY | IOTA | SOME | ALL;

	Formula exists_VAR_b_AGENT_equals_IRE_VAR = SL.fromFormula("(exists ??__var (B ??__agent (= ??__ire ??__var)))");

	private Term ireVariablesPattern;
	
	private Formula ireFormulaPattern;
	
	private Term agentPattern;
	
	private int ireQuantifierPattern;
	
	/**
	 * @param capabilities
	 * @param timeout
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities,
			int ireQuantifierPattern, String ireVariablesPattern, String ireFormulaPattern, String agentPattern, Date timeout) {
		this(capabilities, ireQuantifierPattern, ireVariablesPattern, ireFormulaPattern, agentPattern);
		setTimeout(timeout);
	}

	/**
	 * @param capabilities
	 * @param timeout
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities,
			int ireQuantifierPattern, String ireVariablesPattern, String ireFormulaPattern, String agentPattern, long timeout) {
		this(capabilities, ireQuantifierPattern, ireVariablesPattern, ireFormulaPattern, agentPattern);
		setTimeout(timeout);
	}

	/**
	 * @param capabilities
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities, int ireQuantifierPattern, String ireVariablesPattern, String ireFormulaPattern, String agentPattern) {
		this(capabilities, ireQuantifierPattern, SL.fromTerm(ireVariablesPattern), SL.fromFormula(ireFormulaPattern), SL.fromTerm(agentPattern));
	}

	/**
	 * @param capabilities
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities, int ireQuantifierPattern, Term ireVariablesPattern, Formula ireFormulaPattern, Term agentPattern) {
		super(capabilities, SL.fromFormula("(I ??myself ??__phi)"));
		this.ireQuantifierPattern = ireQuantifierPattern;
		this.ireVariablesPattern = ireVariablesPattern;
		this.ireFormulaPattern = ireFormulaPattern;
		this.agentPattern = agentPattern;
	}


	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
	 */
	final protected ArrayList doApply(MatchResult applyResult, ArrayList result, SemanticRepresentation sr) {
		Formula goal = applyResult.formula("__phi");
		if (goal instanceof DoneNode &&
				((DoneNode)goal).as_formula() instanceof TrueNode &&
				((DoneNode)goal).as_action() instanceof ActionExpression) {
			try {
				goal = myCapabilities.getMySemanticActionTable()
						.getSemanticActionInstance((ActionExpression)((DoneNode)goal).as_action())
						.getRationalEffect();
			} catch (SemanticInterpretationException e) {
			}
		}
		
		Term ire = null;
		Term agent = null;
		
		applyResult = exists_VAR_b_AGENT_equals_IRE_VAR.match(goal);
		if (applyResult == null) {
			MetaVariableReferenceNode var = new MetaVariableReferenceNode("__var");
			Formula quantifiedFormula = goal.isExistsOn(var);
			if (quantifiedFormula != null && var.sm_value() != null) {
				MetaTermReferenceNode ag = new MetaTermReferenceNode("__agent");
				Formula believedFormula = quantifiedFormula.isBeliefFrom(ag);
				if (believedFormula != null) {
					ire = new AnyNode(var.sm_value(), believedFormula);
					agent = ag.sm_value();
				}
			}
		}
		else {
			ire = applyResult.term("__ire");
			agent = applyResult.term("__agent");
		}
		
		if (ire != null && ire instanceof IdentifyingExpression
				&& agent != null && !myCapabilities.getAgentName().equals(agent)) {
			int ireQuantifierMatch = 0;
			if (ire instanceof AnyNode) {
				ireQuantifierMatch = ireQuantifierPattern & ANY;
			}
			else if (ire instanceof IotaNode) {
				ireQuantifierMatch = ireQuantifierPattern & IOTA;
			}
			else if (ire instanceof SomeNode) {
				ireQuantifierMatch = ireQuantifierPattern & SOME;
			}
			else if (ire instanceof AllNode) {
				ireQuantifierMatch = ireQuantifierPattern & ALL;
			}

			MatchResult ireVariablesMatch = ireVariablesPattern.match(((IdentifyingExpression)ire).as_term());
			MatchResult ireFormulaMatch = ireFormulaPattern.match(((IdentifyingExpression)ire).as_formula());
			MatchResult agentMatch = agentPattern.match(agent);
			
			if (ireQuantifierMatch != 0 && ireVariablesMatch != null && ireFormulaMatch != null && agentMatch != null) {
				return prepareQueryRef((IdentifyingExpression)ire, ireVariablesMatch, ireFormulaMatch, agentMatch, result, sr);
			}
		}
		return null;
	}
	
	abstract protected ArrayList prepareQueryRef(IdentifyingExpression ire,
											  MatchResult ireVariablesMatch,
											  MatchResult ireFormulaMatch,
											  MatchResult agentMatch,
											  ArrayList result,
											  SemanticRepresentation sr);
}

