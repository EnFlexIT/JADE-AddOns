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
* TemperatureDefinition.java
* Created on 15 déc. 2004
* Author : louisvi
*/
package test.demos;

import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.BindingsImpl;
import jade.semantics.kbase.FiltersDefinition;
import jade.semantics.kbase.KbaseImpl_List;
import jade.semantics.kbase.filter.KBAssertFilterAdapter;
import jade.semantics.kbase.filter.KBQueryFilterAdapter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
* Definition of all the filters used to manage the temperature variations
* @author louisvi
* @version Date: 2004/11/30 17:00:00  Revision: 1.0 
*/
public class TemperatureDefinition extends FiltersDefinition 
{

    Formula TEMP_X_PATTERN;

   Formula NOT_TEMP_X_PATTERN;

   Formula TEMPSUP_X_PATTERN;

   Formula NOT_TEMPSUP_X_PATTERN;

   IdentifyingExpression ALL_TEMPERATURE;

   IdentifyingExpression ALL_TEMPSUP;

   IdentifyingExpression ALL_NOT_TEMPSUP;

    boolean temperatureHasChanged = false;
   
   /**
    * Removes all the knowledge about the temperature from the base
    * @param kbase the base to clean
    */
   protected void cleanKBase(KbaseImpl_List kbase) 
   {
       kbase.removeAllFormulae(NOT_TEMP_X_PATTERN);
       kbase.removeAllFormulae(TEMP_X_PATTERN);
       kbase.removeAllFormulae(TEMPSUP_X_PATTERN);
       kbase.removeAllFormulae(NOT_TEMPSUP_X_PATTERN);
       temperatureHasChanged = true;
   } // End of cleanKBase/1
   
   /**
    * Constructor
    */
   public TemperatureDefinition() 
   {        
        TEMP_X_PATTERN = SLPatternManip.fromFormula("(temperature ??X)");

        NOT_TEMP_X_PATTERN = SLPatternManip.fromFormula("(not (temperature ??X))");

        TEMPSUP_X_PATTERN = SLPatternManip.fromFormula("(tempSup ??X)");

        NOT_TEMPSUP_X_PATTERN = SLPatternManip.fromFormula("(not (tempSup ??X))");

        ALL_TEMPERATURE = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (temperature ?y))");

        ALL_TEMPSUP = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (tempSup ?y))");

        ALL_NOT_TEMPSUP = (IdentifyingExpression)SLPatternManip.fromTerm("(all ?y (not (tempSup ?y)))");

    // ASSERT FILTERS
    // --------------
       defineFilter(new KBAssertFilterAdapter("(B ??agent " + TEMP_X_PATTERN + ")") {
           public Formula applyBefore(Formula formula) {
               mustApplyAfter = false;
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               else {
                   cleanKBase((KbaseImpl_List) myKBase);
                   return formula;
               }
           }
       });

       defineFilter(new KBAssertFilterAdapter("(B ??agent " + TEMPSUP_X_PATTERN + ")") {
           public Formula applyBefore(Formula formula) {
               mustApplyAfter = false;
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               else {
                   cleanKBase((KbaseImpl_List) myKBase);
                   return formula;
               }
           }
       });

       defineFilter(new KBAssertFilterAdapter("(B ??agent " + NOT_TEMPSUP_X_PATTERN + ")") {
           public Formula applyBefore(Formula formula) {
               mustApplyAfter = false;
               if ((myKBase.query(formula) != null)) {
                   return new TrueNode();
               }
               else {
                   cleanKBase((KbaseImpl_List) myKBase);
                   return formula;
               }
           }
       });

       // QUERY FILTERS
    // -------------
       defineFilter(new KBQueryFilterAdapter("(B ??agent " + TEMPSUP_X_PATTERN + ")") {
           protected Long temperatureValue;
           protected long queriedTemp;

           public boolean isApplicable(Formula formula, Term agent) {
                temperatureValue = null;
                queriedTemp = 0;

               if (super.apply(formula, agent) != null) {
                   try {
                      queriedTemp = ((IntegerConstantNode)applyResult.getTerm("??X")).lx_value().longValue();
                   }
                   catch (Exception e) {e.printStackTrace(); return false;}
                   ListOfTerm queryResult = myKBase.queryRef(ALL_TEMPERATURE);
                   if (queryResult.size() == 1) {
                       temperatureValue = ((IntegerConstantNode)queryResult.get(0)).lx_value();
                       return true;
                   }
                   else {
                       temperatureValue = null;
                       queryResult = myKBase.queryRef(ALL_TEMPSUP);
                       return(queriedTemp <= (queryResult.size()==0 ? Long.MIN_VALUE :
                            ((IntegerConstantNode)queryResult.get(0)).lx_value().longValue()));
                   }
               }
               return false;
           }

           public Bindings apply(Formula formula) {
               if (((temperatureValue != null) && (temperatureValue.longValue() > queriedTemp))
                        || (temperatureValue == null)) return new BindingsImpl();
               else return null;
           }
       });

       defineFilter(new KBQueryFilterAdapter("(B ??agent " + NOT_TEMPSUP_X_PATTERN + ")") {
           protected Long temperatureValue = null;
           protected long queriedTemp;

           public boolean isApplicable(Formula formula, Term agent) {
                temperatureValue = null;
                queriedTemp = 0;
                if (super.apply(formula, agent) != null) {
                   try {
                       queriedTemp = ((IntegerConstantNode)applyResult.getTerm("??X")).lx_value().longValue();
                   }
                   catch (Exception e) {e.printStackTrace(); return false;}
                   ListOfTerm queryResult = myKBase.queryRef(ALL_TEMPERATURE);
                   if (queryResult.size() == 1) {
                       temperatureValue = ((IntegerConstantNode)queryResult.get(0)).lx_value();
                       return true;
                   }
                   else {
                       queryResult = myKBase.queryRef(ALL_NOT_TEMPSUP);
                       return (queriedTemp >= (queryResult.size()==0 ? Long.MAX_VALUE :
                            ((IntegerConstantNode)queryResult.get(0)).lx_value().longValue()));
                   }
               }
               return false;
           }

           public Bindings apply(Formula formula) {
               if (((temperatureValue != null) && (temperatureValue.longValue() <= queriedTemp))
                        || (temperatureValue == null)) return new BindingsImpl();
               else return null;
           }
       });

//       defineFilter(new KBQueryFilterAdapter("(B ??agent (tempHasChanged))") {
//           public Bindings apply(Formula formula, Term agent) {
//               boolean result = temperatureHasChanged;
//              temperatureHasChanged = false;
//              if (result) return new BindingsImpl(); 
//               else return null;
//           }
//       });
   } // End of TemperatureDefinition
   
} // End of TemperatureDefinition