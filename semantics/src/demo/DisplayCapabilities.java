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
* DisplayCapabilities.java
* Created on 13 mai 2005
* Author : Vincent Pautret
*/
package demo;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.FilterKBase;
import jade.semantics.kbase.filter.KBAssertFilterAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
* Capabilities of the display.
* @author Thierry Martinez - France Telecom
* @version Date: 2005/05/13 Revision: 1.0
*/
public class DisplayCapabilities extends SemanticCapabilities {
   
   /**
    * Definition of the temperature
    */
   SingleNumValueDefinition temperatureDefinition = new SingleNumValueDefinition("temperature");
   
   /**
    * Creates a new Display 
    */
   public DisplayCapabilities() {
       super();
   } // End of DisplayCapabilities/0
   
   /**
    * setupStandardCustomization
    */
   public void setupStandardCustomization() {
       setMyStandardCustomization(new StandardCustomizationAdapter() {
           public boolean acceptBeliefTransfer(Formula formula, Term agent) {
               // the agent agrees to believe of information one the temperature only
               // if the agent which sends it is has sensor.
                return 
               (((DisplayAgent)myAgent).selectedAgent != null 
                       && agent.equals(((DisplayAgent)myAgent).selectedAgent)) 
                       || !((SLPatternManip.match(temperatureDefinition.VALUE_X_PATTERN, formula) != null)
                               || (SLPatternManip.match(temperatureDefinition.NOT_VALUE_X_PATTERN, formula) != null)
                               || (SLPatternManip.match(temperatureDefinition.VALUE_GT_X_PATTERN, formula) != null)
                               || (SLPatternManip.match(temperatureDefinition.NOT_VALUE_GT_X_PATTERN, formula) != null));
           }
            
           public boolean handleProposal(Term agentI, ActionExpression action, Formula formula) {
               // Handles the proposal only if the action is an InformRef on temperature and 
               // if the condition relates to a precision
               MatchResult matchResult = SLPatternManip.match(DisplayAgent.CFP_ACTION, action);
               if ( matchResult != null ) {
                   matchResult = SLPatternManip.match(DisplayAgent.CFP_CONDITION, formula);
                   if ( matchResult != null ) {
                       try {
                           ((DisplayAgent)myAgent).handleProposal((IntegerConstantNode)matchResult.getTerm("X"), 
                                   agentI, action, formula);
                       }
                       catch(Exception e) {
                           e.printStackTrace();
                       }
                   }
               }
               return true;
           }
       });
   } // End of setupStandardCustomization/0
   
   
   /**
    * setupKbase
    */
   public void setupKbase() {
       super.setupKbase();
       //Adds filters about the temperature 
       ((FilterKBase)myKBase).addFiltersDefinition(temperatureDefinition);
       ((FilterKBase)myKBase).addKBAssertFilter(
               new KBAssertFilterAdapter("(B ??agent " + temperatureDefinition.VALUE_X_PATTERN + ")") {
                   // Sets the temperature to display with the suitable characteristics of display
                   public void afterAssert(Formula formula) {
                       try {
                           ((DisplayAgent)myAgent).display.setTemperature(((RealConstantNode)applyResult.getTerm("X")).lx_value());
                       } 
                        catch (ClassCastException cce) 
                        {
                           try {
                            ((DisplayAgent)myAgent).display.setTemperature(new Double(((IntegerConstantNode)applyResult.getTerm("X")).lx_value().doubleValue()));             
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               });
       
   } // End of setupKbase/0
   
} // End of class DisplayCapabilities
