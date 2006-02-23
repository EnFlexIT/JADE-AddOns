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
 * SensorCapabilities.java
 * Created on 13 mai 2005
 * Author : Vincent Pautret
 */
package demo;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.kbase.FilterKBase;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Capbilities of a sensor.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/13 Revision: 1.0
 */
public class SensorCapabilities extends SemanticCapabilities {
    /**
     * 
     */
    SingleNumValueDefinition temperatureDefinition = new SingleNumValueDefinition("temperature");
    
    /**
     * 
     */
    Formula bPattern;
    /** ****************************************************************** */
    /** CONSTRUCTOR * */
    /** ****************************************************************** */
    /**
     * Creates a new SensorCapabilities
     */
    public SensorCapabilities() {
        super();
        bPattern = SLPatternManip.fromFormula("(B ??agent ??phi)");
    } // End of SensorCapabilities/0
    
    /** ****************************************************************** */
    /** METHODS * */
    /** ****************************************************************** */
    
    /**
     * setup StandardCustomization
     */
    public void setupStandardCustomization() {
        setMyStandardCustomization( new StandardCustomizationAdapter() {
            // Accepts no information about the temperature
            public boolean acceptBeliefTransfer(Formula formula, Term agent) {
                return (SLPatternManip.match(temperatureDefinition.VALUE_X_PATTERN, formula)==null)
                && (SLPatternManip.match(temperatureDefinition.NOT_VALUE_X_PATTERN, formula)==null)
                && (SLPatternManip.match(temperatureDefinition.VALUE_GT_X_PATTERN, formula)==null)
                && (SLPatternManip.match(temperatureDefinition.NOT_VALUE_GT_X_PATTERN, formula)==null);
            }
			// Reply to a CFP about the precision by the sensor precision
			public ListOfTerm handleCFPAny(Variable variable, Formula formula, ActionExpression action, Term agent) {
				if ( SLPatternManip.match(SLPatternManip.fromFormula("(precision ??X)"), formula) != null ) {
					return myKBase.queryRef(new AnyNode(variable, formula));
				}
				return null;
				
			}
			// Sets the color to yellow when it receives a subscribe
			public void notifySubscribe(Term subscriber, Formula obsverved, Formula goal) {
				((SensorAgent)getAgent()).setSubscribed(true);
			}
            //Sets the color to gray when it receives an unsubscribe
			public void notifyUnsubscribe(Term subscriber, Formula obsverved, Formula goal) {
				((SensorAgent)getAgent()).setSubscribed(false);
			}
		});
    } // End of setupStandardCustomization/0
    
    /**
     * 
     */
    public void setupKbase() {
        super.setupKbase();
        ((FilterKBase) myKBase).addFiltersDefinition(temperatureDefinition);
    } // End of setupKbase
	
} // End of class SensorCapabilities/0
