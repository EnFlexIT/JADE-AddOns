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

import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * Adapter for a semantic interpretation principle.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/07 Revision: 1.0
 */
public class SemanticInterpretationPrincipleAdapter extends
SemanticInterpretationPrinciple {
    
    /**
     * The pattern that defines the principle
     */
    private Formula pattern;
    
    /**
     * The match result between the pattern and the incoming formula 
     */
    private MatchResult matchResult;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new Semantic Interpretaiton Principle with the semantic 
     * capabilities to which it belongs and the pattern to test its applicability. 
     * @param capabilities capabilities to which the principle belongs 
     * @param formula the pattern that must match to apply the principle
     */
    public SemanticInterpretationPrincipleAdapter(SemanticCapabilities capabilities, Formula formula) {
        super(capabilities);
        this.pattern = formula;
    } // End of SemanticInterpretationPrincipleAdapter/2
    
    /*********************************************************************/
    /**                         METHODS                             **/
    /*********************************************************************/
    
    /**
     * If it is applicable, adds a <code>OneShotBehaviour</code> which calls the
     * execute method.
     * @inheritDoc
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
     * Method to be overriden. Corresponds to the body of the behaviour added by
     * this principle.
     * @param matchResult match result between the pattern and the incoming 
     * formula
     */
    public void execute(MatchResult matchResult) {
        System.out.println("SEMANTICINTERPRETATIONPRINCIPLEADAPTER : GENERIC EXECUTE !");
    } // End of execute/1
    
} // End of class SemanticInterpretationAdapter
