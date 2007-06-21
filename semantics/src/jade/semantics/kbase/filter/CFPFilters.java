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
 * created on 28 mars 2007 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.kbase.filter;

import jade.semantics.kbase.FiltersDefinition;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filter.query.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;
import jade.util.leap.Set;

/**
 * @author Vincent Louis - France Telecom
 *
 */
public class CFPFilters extends FiltersDefinition {
	
	Formula proposalVectorPattern = SL.fromFormula("(B (sequence ??__agent ??__act) ??__condition)");
	Formula b_MYSELF_propose_AGENT_ACT_CONDITION =
		SL.fromFormula("(B ??myself (or (not (I ??__agent (done ??__act ??__condition))) (I ??myself (done ??__act ??__condition))))");
	Formula not_b_MYSELF_propose_AGENT_ACT_CONDITION = new NotNode(b_MYSELF_propose_AGENT_ACT_CONDITION);
	
	private ArrayList proposals;
	private KBase myKBase;
	
	/**
	 * 
	 */
	public CFPFilters(KBase kbase) {
		proposals = new ArrayList();
		myKBase = kbase;
		
		/***********************************************************************
		 * ASSERTING A PROPOSAL
		 **********************************************************************/
		defineFilter(//0,
				new KBAssertFilterAdapter(b_MYSELF_propose_AGENT_ACT_CONDITION) {
			/* (non-Javadoc)
			 * @see jade.semantics.kbase.filter.KBAssertFilterAdapter#doApply(jade.semantics.lang.sl.grammar.Formula, jade.semantics.lang.sl.tools.MatchResult)
			 */
			public Formula doApply(Formula formula, MatchResult match) {
				if (checkProposalPattern(match)) {
					Node proposalVector = buildProposalVector(match);
					if (proposalVector != null && !proposals.contains(proposalVector)) {
						proposals.add(proposalVector);
					}
					return new TrueNode();
				}
				return formula;
			}
		});
		
		/***********************************************************************
		 * RETRACTING A PATTERN OF PROPOSALS
		 **********************************************************************/
		defineFilter(//0,
				new KBAssertFilterAdapter(not_b_MYSELF_propose_AGENT_ACT_CONDITION) {
			/* (non-Javadoc)
			 * @see jade.semantics.kbase.filter.KBAssertFilterAdapter#doApply(jade.semantics.lang.sl.grammar.Formula, jade.semantics.lang.sl.tools.MatchResult)
			 */
			public Formula doApply(Formula formula, MatchResult match) {
				for (int i=proposals.size()-1; i>=0; i--) {
					if (SL.match((Node)proposals.get(i), buildProposalVector(match)) != null) {
						proposals.remove(i);
					}
				}
				return new TrueNode();
			}	
		});
		
		/***********************************************************************
		 * QUERYING FOR A PROPOSAL
		 **********************************************************************/
		defineFilter(//0,
				new KBQueryFilter() {
			/* (non-Javadoc)
			 * @see jade.semantics.kbase.filter.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.semantics.lang.sl.grammar.Term)
			 */
			public QueryResult apply(Formula formula, Term a) {
				MatchResult match = b_MYSELF_propose_AGENT_ACT_CONDITION.match(formula);
				if (checkProposalPattern(match)) {
					QueryResult result = new QueryResult(QueryResult.IS_APPLICABLE);
					Node proposalVector = buildProposalVector(match);
					for (int i=0; i<proposals.size(); i++) {
						MatchResult matchVector = SL.match(proposalVector, (Node)proposals.get(i));
						if (matchVector != null) {
							result.addSolution(matchVector);
						}
					}
					return result;
				}
				return null;
			}
			
			/* (non-Javadoc)
			 * @see jade.semantics.kbase.filter.KBQueryFilter#getObserverTriggerPatterns(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.Set)
			 */
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}
	
	private boolean checkProposalPattern(MatchResult match) {
		if (match != null) {
			Term act = match.term("__act");
			Term agent = match.term("__agent");
			return (//myKBase.getAgentName().equals(match.term("myself")) && // should be implicit
					!myKBase.getAgentName().equals(agent) &&
					act instanceof ActionExpressionNode &&
					myKBase.getAgentName().equals(((ActionExpressionNode)act).getActor()));
		}
		return false;
	}
	
	private Node buildProposalVector(MatchResult match) {
		try {
			return SL.instantiate(proposalVectorPattern,
					"__agent", match.term("__agent"),
					"__act", match.term("__act"),
					"__condition", match.formula("__condition"));
		} catch (WrongTypeException e) {
			e.printStackTrace();
			return null;
		}
	}
}

