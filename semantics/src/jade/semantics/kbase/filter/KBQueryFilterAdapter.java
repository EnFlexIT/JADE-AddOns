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
 * KBQueryFilterAdapter.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */

package jade.semantics.kbase.filter;

import jade.semantics.kbase.Bindings;
import jade.semantics.kbase.BindingsImpl;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SLPatternManip;

/**
 * Particular type of <code>KBQueryFilter</code>
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0
 */
public abstract class KBQueryFilterAdapter extends KBQueryFilter {

	/**
	 * Pattern that must match to apply the filter adapter
	 */

    protected Formula pattern;
	
	/**
	 * Constructor
	 * @param pttrn a pattern. The pattern must contain a variable named "??agent" 
	 * representing the semantic agent itself.
	 */
    public KBQueryFilterAdapter(String pttrn) {
        super();
		try {
			pattern = SLParser.getParser().parseFormula(pttrn, true);
		}
		catch(ParseException pe) {pe.printStackTrace();}
    } // End of KBQueryFilterAdapter/1
    
	/**
	 * Constructor
	 */
    public KBQueryFilterAdapter() {
    } // End of KBQueryFilterAdapter/0

    /**
     * @return true if the filter is applicable, false if not.
     * @see jade.semantics.kbase.filter.KBQueryFilter#isApplicable(jade.lang.sl.grammar.Formula, jade.lang.sl.grammar.Term)
     */
    public boolean isApplicable(Formula formula, Term agent) {
        try {
            
			SLPatternManip.set(pattern, "??agent", agent);
			applyResult = SLPatternManip.match(pattern, formula);
			return (applyResult != null);
		} catch (Exception e) {
            return false;
        }
    } // End of isApplicable/2
    
    /**
     * @see jade.semantics.kbase.filter.KBQueryFilter#apply(jade.lang.sl.grammar.Formula)
     */
    public Bindings apply(Formula formula, Term agent) {
        try {
            SLPatternManip.set(pattern, "??agent", agent);
            applyResult = SLPatternManip.match(pattern, formula);
            if (applyResult != null) return new BindingsImpl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String toString() {
        return pattern.toString();
    }
} // End of KBQueryFilterAdapter
