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
* SingleNumValueDefinition.java
* Created on 15 d�c. 2004
* Author : louisvi
*/
package demo;

import jade.semantics.kbase.FilterKBase;
import jade.semantics.kbase.FiltersDefinition;
import jade.semantics.kbase.filter.KBAssertFilterAdapter;
import jade.semantics.kbase.filter.KBQueryFilterAdapter;
import jade.semantics.kbase.filter.query.QueryResult;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Set;

/**
* General class that defines single value predicat. 
* @author Vincent Louis - France Telecom
* @version Date: 2004/11/30 Revision: 1.0 
*/
public class SingleNumValueDefinition extends FiltersDefinition {
   
   /**
    * Patterns used to manage the defined predicat
    */
   Formula VALUE_X_PATTERN;
   Formula NOT_VALUE_X_PATTERN;
   Formula VALUE_GT_X_PATTERN;
   Formula NOT_VALUE_GT_X_PATTERN;
   IdentifyingExpression ALL_VALUES;
   IdentifyingExpression ALL_VALUES_GT;
   IdentifyingExpression ALL_VALUES_NOT_GT;
   
   /**
    * Removes from the base all the belief about this kind of predicat
    * @param kbase the base to clean
    */
   protected void cleanKBase(FilterKBase kbase) {
       kbase.retractFormula(NOT_VALUE_X_PATTERN);
       kbase.retractFormula(VALUE_X_PATTERN);
       kbase.retractFormula(VALUE_GT_X_PATTERN);
       kbase.retractFormula(NOT_VALUE_GT_X_PATTERN);
   } // End of cleanKBase/1
   
   /**
    * Constructor
    * @param name name of the predicat 
    */
   public SingleNumValueDefinition(String name) 
   {        
       VALUE_X_PATTERN = SLPatternManip.fromFormula("("+name+" ??X)");
       
       NOT_VALUE_X_PATTERN = SLPatternManip.fromFormula("(not ("+name+" ??X))");
       
       VALUE_GT_X_PATTERN = SLPatternManip.fromFormula("("+name+"_gt ??X)");
       
       NOT_VALUE_GT_X_PATTERN = SLPatternManip.fromFormula("(not ("+name+"_gt ??X))");

       ALL_VALUES = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (B ??agent ("+name+" ?y)))");
       
       ALL_VALUES_GT = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (B ??agent ("+name+"_gt ?y)))");
       
       ALL_VALUES_NOT_GT = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (B ??agent (not ("+name+"_gt ?y))))");
       
       // ASSERT FILTERS
       // --------------
       // These filters are used to let only one information about this 
       //predicat in the base.
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + VALUE_X_PATTERN + ")") {
           //If the predicat is already in the base, does nothing, otherwise
           //cleans the base of all knowledge related to this predicate
           public Formula doApply(Formula formula) {
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               cleanKBase(myKBase);
               return formula;
           }
       });
       
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + VALUE_GT_X_PATTERN + ")") {
           public Formula doApply(Formula formula) {
               //If the predicat is already in the base, does nothing, otherwise
               //cleans the base of all knowledge related to this predicate
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               cleanKBase(myKBase);
               return formula;
           }
       });
       
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + NOT_VALUE_GT_X_PATTERN + ")") {
           public Formula doApply(Formula formula) {
               //If the predicat is already in the base, does nothing, otherwise
               //cleans the base of all knowledge related to this predicate
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               cleanKBase(myKBase);
               return formula;
           }
       });
       
       // QUERY FILTERS
       // -------------
       
       defineFilter(new KBQueryFilterAdapter("(B ??agent " + VALUE_GT_X_PATTERN + ")") {
           // Compare the sought value with that known to give the answer. 
           public QueryResult apply(Formula formula, Term agent) {
               QueryResult queryResult = new QueryResult();
               try {
                  SLPatternManip.set(pattern, "agent", agent);
                  MatchResult applyResult = SLPatternManip.match(pattern, formula);
                  if (applyResult != null && applyResult.getTerm("X") instanceof Constant) {
                      queryResult.setResult(null);
                      Long queriedValue = ((Constant)applyResult.getTerm("X")).intValue();
                      ListOfTerm queryRefResult = myKBase.queryRef((IdentifyingExpression)
                       SLPatternManip.instantiate(ALL_VALUES, "agent", applyResult.getTerm("agent")));
                      if (queryRefResult != null && queryRefResult.size() != 0 ) {
                          if ( ((Constant)queryRefResult.get(0)).intValue().longValue() > queriedValue.longValue() ) {
                              queryResult.setResult(new ListOfMatchResults());
                          }
                      }
                      else {
                          queryRefResult = myKBase.queryRef((IdentifyingExpression)
                                  SLPatternManip.instantiate(ALL_VALUES_GT, "agent", applyResult.getTerm("agent")));
                          if (queryRefResult != null && queryRefResult.size() != 0 ) {
                              if (((Constant)queryRefResult.get(0)).intValue().longValue() >= queriedValue.longValue() ) {
                                  queryResult.setResult(new ListOfMatchResults());
                              }
                          }
                      }
                      queryResult.setFilterApplied(true);
                      return queryResult;
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
              return queryResult;
           }
           
           public void getObserverTriggerPatterns(Formula formula, Set set) {
               try {
                   MatchResult applyResult = SLPatternManip.match(pattern, formula);
                   if (applyResult != null && applyResult.getTerm("X") instanceof Constant) {
                       set.add(VALUE_X_PATTERN);
                       set.add(VALUE_GT_X_PATTERN);
                       set.add(NOT_VALUE_GT_X_PATTERN);
                   }
               }catch (SLPatternManip.WrongTypeException wte) {
                   wte.printStackTrace();
               }
           }
       });
       
       defineFilter(new KBQueryFilterAdapter("(B ??agent " + NOT_VALUE_GT_X_PATTERN + ")") {
           // Compare the sought value with that known to give the answer. 
           public QueryResult apply(Formula formula, Term agent) {
               QueryResult queryResult = new QueryResult();
               try {
                   SLPatternManip.set(pattern, "agent", agent);
                   MatchResult applyResult = SLPatternManip.match(pattern, formula);
                   if (applyResult != null && applyResult.getTerm("X") instanceof Constant) {
                       queryResult.setResult(null);
                       Long queriedValue = ((Constant)applyResult.getTerm("X")).intValue();
                       ListOfTerm queryRefResult = myKBase.queryRef((IdentifyingExpression)
                                   SLPatternManip.instantiate(ALL_VALUES, "agent", applyResult.getTerm("agent")));
                       if (queryRefResult != null && queryRefResult.size() != 0 ) {
                           if ( ((Constant)queryRefResult.get(0)).intValue().longValue() < queriedValue.longValue() ) {
                               queryResult.setResult(new ListOfMatchResults());
                           }
                       }
                       else {
                           queryRefResult = myKBase.queryRef((IdentifyingExpression)
                                   SLPatternManip.instantiate(ALL_VALUES_NOT_GT, "agent", applyResult.getTerm("agent")));
                           if (queryRefResult != null && queryRefResult.size() != 0 ) {
                               if (((Constant)queryRefResult.get(0)).intValue().longValue() <= queriedValue.longValue() ) {
                                   queryResult.setResult(new ListOfMatchResults());
                               }
                           }
                       }
                       queryResult.setFilterApplied(true);
                       return queryResult;
                   }
               } catch(Exception e) {e.printStackTrace();}
               return queryResult;
           }
           public void getObserverTriggerPatterns(Formula formula, Set set) {
               try {
               MatchResult applyResult = SLPatternManip.match(pattern, formula);
               if (applyResult != null && applyResult.getTerm("X") instanceof Constant) {
                   set.add(NOT_VALUE_GT_X_PATTERN);
                   set.add(VALUE_X_PATTERN);
                   set.add(VALUE_GT_X_PATTERN);
               }
               }catch (SLPatternManip.WrongTypeException wte) {
                   wte.printStackTrace();
               }
               
           }

       });
       
   } // End of SingleNumValueDefinition
   
} // End of SingleNumValueDefinition