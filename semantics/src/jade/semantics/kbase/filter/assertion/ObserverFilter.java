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
 * ObserverFilter.java
 * Created on 7 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.assertion;

import java.util.Iterator;

import jade.semantics.kbase.FilterKBaseImpl;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.ListOfMatchResults;

/**
 * Filter used to observe value of formula in the belief base. 
 * Notifies the observers whose observed formula becomes true.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/07 Revision 1.0
 */
public class ObserverFilter extends KBAssertFilter {
    
    /**
     * Returns the given formula.
     * @param formula a formula to assert
     * @return the given formula  
     */
    public Formula apply(Formula formula) {
        mustApplyAfter = true;
        return formula;
    } // End of beforeAssert/1
    
    /**
     * For each <code>Observation</code>, notifies the associated observer if
     * the value of the observation becomes true. Do nothing, in the other case. 
     * @inheritDoc
     */
    public void afterAssert(Formula formula) {
        if (myKBase.getObserversToApplied() != null) {
            for (java.util.Iterator iter = myKBase.getObserversToApplied().iterator(); iter.hasNext();) {
                FilterKBaseImpl.Observation observation = (FilterKBaseImpl.Observation)iter.next();
                ListOfMatchResults newValue = myKBase.query(observation.getObserver().getObservedFormula());
                if (newValue == null) {
                    observation.setCurrentValue(null);
                } else if (!newValue.equals(observation.getCurrentValue())) {
                    observation.setCurrentValue(newValue);
                    observation.getObserver().notify(newValue); 
                }
            }
            myKBase.setObserversToBeApplied(null);
        }
    } // End of afterAssert/1
} // End of class ObserverFilter
