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
 * Test.java
 * Created on 5 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.operations.DefaultNodeOperations;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * @author Vincent Pautret
 * @version 
 */
public class Test {

    static {
        DefaultNodeOperations.installOperations();
    }

    static Formula form = null;
    
    public static void main(String[] args) {
        SemanticAgent agent = new SemanticAgent();
        agent.setup();
        agent.setAgentName(SLPatternManip.fromTerm("(agent-identifier :name jade)"));
        form = SLPatternManip.fromFormula("(I (agent-identifier :name jade) (temperature 12))");
        ((KbaseImpl_List)agent.getMyKBase()).assertFormula(form);
        if (agent.getMyKBase().query(SLPatternManip.fromFormula("(B (agent-identifier :name jade) (temperature ??X))")) !=null) System.out.println("Dans la base");
        else System.out.println("Pas dans la base");
        System.out.println("Avant ");
        ((KbaseImpl_List)agent.getMyKBase()).viewDataInBase();
        agent.getMyKBase().removeFormula(new Finder() {
            public boolean identify(Object object) {
                if (object instanceof Formula) {
                    return form.equals(object);
                }
                return false;
            }
        });
        System.out.println("Après ");
        ((KbaseImpl_List)agent.getMyKBase()).viewDataInBase();
    }
}
