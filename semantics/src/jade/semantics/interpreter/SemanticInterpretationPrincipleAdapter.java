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
 * SemanticInterpretationPrincipleAdapter.java
 * Created on 7 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;





import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.behaviours.SemanticInterpreterBehaviour;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * @author Vincent Pautret
 * @version 
 */
public class SemanticInterpretationPrincipleAdapter extends
        SemanticInterpretationPrincipleImpl {

    Formula pattern;
    
    MatchResult matchResult;
    
    public SemanticInterpretationPrincipleAdapter(SemanticAgent agent, Formula formula) {
        this.pattern = formula;
        this.agent = agent;
    }
    
    
    /* (non-Javadoc)
     * @see jade.core.semantics.interpreter.SemanticInterpretationPrinciple#apply(jade.core.semantics.interpreter.SemanticRepresentation)
     */
    public ArrayList apply(SemanticRepresentation sr)
            throws SemanticInterpretationPrincipleException {
        try {
            matchResult = SLPatternManip.match(pattern, sr.getSLRepresentation());  
            if (matchResult != null) {
                ArrayList result = new ArrayList();
                result.add(sr);
                potentiallyAddBehaviour(new OneShotBehaviour() {
                    public void action() {
                        execute(matchResult);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of apply/1
    
    /**
     * 
     * @param matchResult
     */
    public void execute(MatchResult matchResult) {
        System.out.println("SEMANTICINTERPRETATIONPRINCIPLEADAPTER : GENERIC EXECUTE !");
    } // End of execute/1
    
    /**
     * Adds a behaviour to the agent list
     * @param b a behaviour
     */
    public void potentiallyAddBehaviour(Behaviour b) {
        ((SemanticInterpreterBehaviour)agent.getInterpretationAlgorithm()).getBehaviourToAdd().add(b);
    } // End of addProvisoryBehaviour/1
    
    /**
     * Removes a behaviour from the list of behaviour
     * @param b a behaviour
     */
    public void potentiallyRemoveBehaviour(Behaviour b) {
        ((SemanticInterpreterBehaviour)agent.getInterpretationAlgorithm()).getBehaviourToRemove().add(b);
    } // End of removeProvisoryBehaviour/1
    
    /**
     * 
     * @param formula
     */
    public void potentiallyAssertFormula(Formula formula) {
        ((SemanticInterpreterBehaviour)agent.getInterpretationAlgorithm()).getFormulaToAssert().add(formula);
    } // End of potentiallyAssertFormula/1

    
} // End of class SemanticInterpretationAdapter
