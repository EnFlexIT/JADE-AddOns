/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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
* Created on 15 déc. 2004
* Author : louisvi
*/
package demo;

import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.BindingsImpl;
import jade.semantics.kbase.FiltersDefinition;
import jade.semantics.kbase.FilterKBaseImpl;
import jade.semantics.kbase.filter.KBAssertFilterAdapter;
import jade.semantics.kbase.filter.KBQueryFilterAdapter;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.SLPatternManip;

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
    * Removes from the base all the knowledge about this kind of predicat
    * @param kbase the base to clean
    */
   protected void cleanKBase(FilterKBaseImpl kbase) 
   {
       kbase.removeAllFormulae(NOT_VALUE_X_PATTERN);
       kbase.removeAllFormulae(VALUE_X_PATTERN);
       kbase.removeAllFormulae(VALUE_GT_X_PATTERN);
       kbase.removeAllFormulae(NOT_VALUE_GT_X_PATTERN);
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
       
       ALL_VALUES = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y ("+name+" ?y))");
       
       ALL_VALUES_GT = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y ("+name+"_gt ?y))");
       
       ALL_VALUES_NOT_GT = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (not ("+name+"_gt ?y)))");
       
       // ASSERT FILTERS
       // --------------
       // These filters are used to let only one information about this 
       //predicat in the base.
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + VALUE_X_PATTERN + ")") {
           //If the predicat is already in the base, does nothing, otherwise
           //cleans the base of all knowledge related to this predicat
           public Formula applyBefore(Formula formula) {
               mustApplyAfter = false;
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               else {
                   cleanKBase((FilterKBaseImpl) myKBase);
                   return formula;
               }
           }
       });
       
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + VALUE_GT_X_PATTERN + ")") {
           public Formula applyBefore(Formula formula) {
               //If the predicat is already in the base, does nothing, otherwise
               //cleans the base of all knowledge related to this predicat
               mustApplyAfter = false;
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               else {
                   cleanKBase((FilterKBaseImpl) myKBase);
                   return formula;
               }
           }
       });
       
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + NOT_VALUE_GT_X_PATTERN + ")") {
           public Formula applyBefore(Formula formula) {
               //If the predicat is already in the base, does nothing, otherwise
               //cleans the base of all knowledge related to this predicat
               mustApplyAfter = false;
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               else {
                   cleanKBase((FilterKBaseImpl) myKBase);
                   return formula;
               }
           }
       });
       
       // QUERY FILTERS
       // -------------
       
       defineFilter(new KBQueryFilterAdapter("(B ??agent " + VALUE_GT_X_PATTERN + ")") {
           // Compare the sought value with that known to give the answer. 
           public Bindings apply(Formula formula) {
               Bindings result = null;
               try {
                   Long queriedValue = ((Constant)applyResult.getTerm("X")).intValue();
                   ListOfTerm queryResult = myKBase.queryRef(ALL_VALUES);
                   if ( queryResult.size() != 0 ) {
                       if ( ((Constant)queryResult.get(0)).intValue().longValue() > queriedValue.longValue() ) {
                           result = new BindingsImpl();
                       }
                   }
                   else {
                       queryResult = myKBase.queryRef(ALL_VALUES_GT);
                       if ( queryResult.size() != 0 ) {
                           if (((Constant)queryResult.get(0)).intValue().longValue() >= queriedValue.longValue() ) {
                               result = new BindingsImpl();
                           }
                       }
                   }                   
               }
               catch(Exception e) {e.printStackTrace();}
               return result;
           }
       });
       
       defineFilter(new KBQueryFilterAdapter("(B ??agent " + NOT_VALUE_GT_X_PATTERN + ")") {
           // Compare the sought value with that known to give the answer. 
           public Bindings apply(Formula formula) {
               Bindings result = null;
               try {
                   Long queriedValue = ((Constant)applyResult.getTerm("X")).intValue();
                   ListOfTerm queryResult = myKBase.queryRef(ALL_VALUES);
                   if ( queryResult.size() != 0 ) {
                       if ( ((Constant)queryResult.get(0)).intValue().longValue() < queriedValue.longValue() ) {
                           result = new BindingsImpl();
                       }
                   }
                   else {
                       queryResult = myKBase.queryRef(ALL_VALUES_NOT_GT);
                       if ( queryResult.size() != 0 ) {
                           if (((Constant)queryResult.get(0)).intValue().longValue() <= queriedValue.longValue() ) {
                               result = new BindingsImpl();
                           }
                       }
                   }                   
               }
               catch(Exception e) {e.printStackTrace();}
               return result;
           }
       });
       
   } // End of SingleNumValueDefinition
   
} // End of SingleNumValueDefinition