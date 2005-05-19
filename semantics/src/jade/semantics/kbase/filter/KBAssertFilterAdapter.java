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
 * KBAssertFilterAdapter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */

package jade.semantics.kbase.filter;

import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Particular type of KBassertFilter.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0
 */
public abstract class KBAssertFilterAdapter extends KBAssertFilter {
    
    protected MatchResult applyResult;
    
	/**
	 * Pattern that must match to apply the filter adapter
	 */
	protected Formula pattern;

 	/**
	 * Constructor
	 * @param pttrn a pattern. The pattern must contain a variable named "??agent" 
	 * representing the semantic agent itself.
	 */
    public KBAssertFilterAdapter(String pttrn) {
        super();
		try {
			pattern = SLParser.getParser().parseFormula(pttrn, true);
		}
		catch(ParseException pe) {pe.printStackTrace();}
    } // End of KBAssertFilterAdapter/1
 
    public KBAssertFilterAdapter(Formula formula) {
        super();
        pattern = formula;
    } // End of KBAssertFilterAdapter/1

    /**
     * @param formula a formula
     * @param agent the semantic agent
     * @return Returns true if the pattern of the adapter matchs the formula, false if 
     * not. 
     */
    public final Formula beforeAssert(Formula formula) {
        mustApplyAfter = false;
        try {
            applyResult = SLPatternManip.match(pattern, formula);
            if (applyResult != null) {
                mustApplyAfter = true;
                return applyBefore(formula);  
            }
		} catch (Exception e) {
		    e.printStackTrace();
        }
        return formula;
    } // End of isApplicable/2
    
    public String toString() {
        return pattern.toString();
    }
    
    public Formula applyBefore(Formula formula) {
        return formula;
    }
} // End of KBAssertFilterAdapter
