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
* AndFilter.java
* Created on 23 nov. 2004
* Author : Vincent Pautret
*/
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.Set;


/**
* This filter applies when an "and formula" is asserted in the belief Base.
* @author Vincent Pautret - France Telecom
* @version Date: 2004/11/30 Revision: 1.0 
*/
public class AndFilter extends KBQueryFilter {
   
   
   /**
    * Pattern that must match to apply the filter
    */
   private Formula pattern;
   
   /*********************************************************************/
   /**                          CONSTRUCTOR                             **/
   /*********************************************************************/
   
   /**
    * Creates a new Filter on the pattern (and ??phi ??psi)
    */
   public AndFilter() {
       pattern = SL.fromFormula("(and ??phi ??psi)");
   } // End of AndFilter/1
   
   /*********************************************************************/
   /**                          METHODS                                 **/
   /*********************************************************************/
   
   /** 
    * Returns true as first element if the formula matches the pattern 
    * (and ??phi ??psi), fals if not. If true, if the two parts of the formula are in the 
    * base, the ListOfMatchResults contains the
    * joint of the whole of solutions of the first part of the formula and 
    * the whole of solutions of the second part of the formula.
    * If the filter returns false as first element, the second element is null.
    * @param formula a formula on which the filter is tested
    * @param agent a term that represents the agent is trying to apply the filter
    * @return an array with a Boolean meaning the applicability of the filter,
    * and a ListOfMatchResults that is the result of performing the filter. 
    */
   public QueryResult apply(Formula formula, Term agent) {
       QueryResult queryResult = new QueryResult(!QueryResult.IS_APPLICABLE);
//       try {
           MatchResult applyResult = SL.match(pattern,formula);
           if (applyResult != null) {
        	   queryResult.setSolutions(andPhiPsi(
        			   applyResult.formula("phi"), myKBase,
        			   applyResult.formula("psi"), myKBase));
//				ListOfMatchResults result = null;
//			   	Formula phi = applyResult.getFormula("phi");
//				Formula psi = applyResult.getFormula("psi");
//				ListOfMatchResults phiBindings = myKBase.query(phi);
//				if ( phiBindings == null ) {
//					// We try the other way round
//					Formula tmp = phi;
//					phi = psi;
//					psi = tmp;
//					phiBindings = myKBase.query(phi);
//				}
//				if ( phiBindings != null ) {
//					if ( phiBindings.size() == 0 ) {
//						queryResult.setResult(myKBase.query(psi));
//					}
//					else {
//						for (int i=0; i<phiBindings.size(); i++) {
//							MatchResult phiBinding = (MatchResult)phiBindings.get(i);
//							Formula npsi = (Formula)psi.getClone();
//							for (int j=0; j<phiBinding.size(); j++) {
//								SL.set(npsi, 
//										SL.getMetaReferenceName(phiBinding.get(j)), 
//										SL.getMetaReferenceValue(phiBinding.get(j)));
//							}
//				            SL.substituteMetaReferences(npsi);
//
//							ListOfMatchResults psiBindings = myKBase.query(npsi);
//							if (psiBindings != null) {
//								// We will probably add something
//								if (result == null) {
//									result = new ListOfMatchResults();
//								}
//								if ( psiBindings.isEmpty() ) {
//									// This phiBinding is sufficient
//									result.add(phiBinding);
//								}
//								else {
//									// There are possibly several psiBindings for this phiBinding
//									for (int k=0; k<psiBindings.size(); k++) {
//										MatchResult mr = phiBinding.join((MatchResult)psiBindings.get(k));
//										if ( mr != null) {
//											result.add(mr);
//										}
//									}
//								}
//							}
//						}
//						queryResult.setResult(result);
//					}
//					queryResult.setFilterApplied(true);
//				}
//				return queryResult;
           }
//       } catch (Exception e) {
//           e.printStackTrace();
//       }
       return queryResult;
   } 

   /**
    * Adds in the set, the patterns for the formula phi and for the formula
    * psi.
    * @param formula an observed formula
    * @param set set of patterns. Each pattern corresponds to a kind a formula
    * which, if it is asserted in the base, triggers the observer that
    * observes the formula given in parameter.
    */
   public boolean getObserverTriggerPatterns(Formula formula, Set set) {
	   MatchResult match = SL.match(formula, pattern);
	   if (match != null) {
		   try {
			   myKBase.getObserverTriggerPatterns(match.getFormula("phi"), set);
			   myKBase.getObserverTriggerPatterns(match.getFormula("psi"), set);
			   return false;
		   } catch (SL.WrongTypeException wte) {
			   wte.printStackTrace();
		   }
	   }
	   return true;
   }
   
   public static ListOfMatchResults andPhiPsi(Formula phi, KBase phiKB, Formula psi, KBase psiKB) {
	   ListOfMatchResults result = null;
	   ListOfMatchResults phiBindings = phiKB.query(phi);
	   if ( phiBindings == null ) {
		   // We try the other way round
		   Formula tmp = phi;
		   KBase tmpKB = phiKB;
		   phi = psi;
		   phiKB = psiKB;
		   psi = tmp;
		   psiKB = tmpKB;
		   phiBindings = phiKB.query(phi);
	   }
	   try {
		   if ( phiBindings != null ) {
			   if ( phiBindings.size() == 0 ) {
				   return psiKB.query(psi);
			   }
			   else {
				   for (int i=0; i<phiBindings.size(); i++) {
					   MatchResult phiBinding = (MatchResult)phiBindings.get(i);
					   Formula npsi = (Formula)psi.getClone();
					   for (int j=0; j<phiBinding.size(); j++) {
						   SL.set(npsi, 
								   SL.getMetaReferenceName(phiBinding.get(j)), 
								   SL.getMetaReferenceValue(phiBinding.get(j)));
					   }
					   SL.substituteMetaReferences(npsi);
					   
					   ListOfMatchResults psiBindings = psiKB.query(npsi);
					   if (psiBindings != null) {
						   // We will probably add something
						   if (result == null) {
							   result = new ListOfMatchResults();
						   }
						   if ( psiBindings.isEmpty() ) {
							   // This phiBinding is sufficient
							   result.add(phiBinding);
						   }
						   else {
							   // There are possibly several psiBindings for this phiBinding
							   for (int k=0; k<psiBindings.size(); k++) {
								   MatchResult mr = phiBinding.join((MatchResult)psiBindings.get(k));
								   if ( mr != null) {
									   result.add(mr);
								   }
							   }
						   }
					   }
				   }
			   }
		   }
	   }
	   catch (Exception e) {
		   e.printStackTrace();
	   }
	   return result;
   }
} // End of class AndFilter