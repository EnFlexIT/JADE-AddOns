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

import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.KbaseImpl_List;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.lang.sl.grammar.Formula;

/**
 * @author Vincent Pautret
 * @version 
 */
public class ObserverFilter extends KBAssertFilter {

    
    
    public ObserverFilter() {
    }

    public Formula beforeAssert(Formula formula) {
        mustApplyAfter = true;
        return formula;
    } // End of beforeAssert/1
    
    public void afterAssert(Formula formula) {
//        System.out.println("++ on entre dans le Observer filter avec : " + formula);
        for(int i = 0; i < myKBase.getObservationTable().size(); i++) {
            KbaseImpl_List.Observation observation = ((KbaseImpl_List.Observation)myKBase.getObservationTable().get(i));
            Bindings newValue = myKBase.query(observation.getObserver().getObservedFormula());
//            System.out.println("On est dans l'observer : " + observation.getObserver().getObservedFormula());
//            System.out.println("OldValue = " + observation.getCurrentValue());
//            System.out.println("NewValue = " + newValue);
            
            if (newValue == null) {
                observation.setCurrentValue(null);
            } else if (!newValue.equals(observation.getCurrentValue())) {
                observation.setCurrentValue(newValue);
                observation.getObserver().notify(newValue); 
            }
        }
    } // End of AfterAssert/1
}
