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
 * And.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.ArrayList;

/**
 * This principle is intended to be applied to an AND formula.  
 * It produces two Semantic Representations:
 * <ul>
 * <li> the left part of the conjonction;
 * <li> the right part of the conjonction.
 * </ul>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/28 Revision: 1.0 
 */
public class And extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern used to test the applicability of the semantic interpretation principle
     */
    private Formula pattern;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public And(SemanticCapabilities capabilities) {
        super(capabilities);
        pattern = SLPatternManip.fromFormula("(and ??phi ??psi)");
    } // End of And/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * Splits the SR into two SR if the SL representation of the SR is an AND formula.
     * The first new SR is the left part of the conjunction, and the second one 
     * the right part of the conjunction.
     * @inheritDoc
     */
    public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        try {
            MatchResult applyResult = SLPatternManip.match(pattern, sr.getSLRepresentation());
            if (applyResult != null) {
                Formula phi = applyResult.getFormula("phi");
                Formula psi = applyResult.getFormula("psi");
                ArrayList listOfSR = new ArrayList();
                SemanticRepresentation phiSR = new SemanticRepresentation();
                phiSR.setSLRepresentation(phi);
                phiSR.setMessage(sr.getMessage());
                listOfSR.add(phiSR);
                SemanticRepresentation psiSR = new SemanticRepresentation();
                psiSR.setSLRepresentation(psi);
                psiSR.setMessage(sr.getMessage());
                listOfSR.add(psiSR);
                return listOfSR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1
    
} // End of class And
