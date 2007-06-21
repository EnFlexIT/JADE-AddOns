/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

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
 * FilterKbaseImpl.java
 * Created on 23 nov. 2004
 * Modified on december, the 21th, 2006 
 * Author : Vincent Pautret, Thierry Martinez
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.kbase.filter.KBFilter;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.kbase.filter.query.QueryResult;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.Logger;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

/**
 * Class that implements the filter belief base api. The data are stored in an
 * <code>ArrayListKBaseImpl</code> by default. The filters of the base are
 * stored in <code>ArrayList</code>s.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 * @version Date: 2006/12/21 Revision: 1.4 (Thierry Martinez)
 */
public class FilterKBaseImpl implements FilterKBase {
	
    private final Formula bPattern;

	/**
	 * The continuous of the KBase
	 */
	private KBase continuous;
	
    /**
     * The wrapping KBase
     */
    private KBase wrappingKBase;
    
    /**
     * Name of the agent which holds the stored beliefs
     */
    private Term agentName;

    /**
     * Storgae of belief
     */
    private ArrayList dataStorage;

    /**
     * List of assertion filters
     */
    private ArrayList assertFilterList;

    /**
     * List of query filters
     */
    private ArrayList queryFilterList;

    /**
     * List of queryRef filters
     */
    private ArrayList queryRefFilterList;

     /**
     * Logger
     */
    private Logger logger;

    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Creates a new belief base. Adds the eight query filters:
     * <ul>
     * <li>IREFIlter
     * <li>BeliefTransferFilter
     * <li>IntentionTransferFilter
     * <li>AndFilter
     * <li>OrFilter
     * <li>EventMemoryFilter
     * <li>ExistsFilter
     * <li>ForallFilter
     * </ul>
     * Adds the queryRef filter: CFPFilter.<br>
     * Adds three assert filters:
     * <ul>
     * <li>AllIREFilter
     * <li>ObserverFilter
     * <li>EventMemoryFilter
     * </ul>
     * @param agentName the name of the agent holding the beliefs stored in this KBase
     * @param wrappingKBase the most enclosing KBase on which to issu assert and query
     * @param standardCustomisation kept for compatibility reason. Notice this class is derpecated.
     */
    public FilterKBaseImpl(Term agentName, KBase wrappingKBase) {
        this.wrappingKBase = wrappingKBase == null ? this : wrappingKBase; 
        this.agentName = agentName;
        this.continuous = new ArrayListKBaseImpl(agentName, this.wrappingKBase);
		this.assertFilterList = new ArrayList();
        this.queryFilterList = new ArrayList();
        this.queryRefFilterList = new ArrayList();
        this.logger = Logger.getMyLogger(getClass().getName());

        this.bPattern = new BelieveNode(agentName, new MetaFormulaReferenceNode("phi"));
    } 

    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/

    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#getWrappingKBase()
     */
    public KBase getWrappingKBase() {
    	return wrappingKBase;
    }
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#getAgentName()
     */
    public Term getAgentName() {
    	return agentName;
    }

    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#setAgentName(jade.semantics.lang.sl.grammar.Term)
     */
    public void setAgentName(Term agent) {
    	agentName = agent;
    	continuous.setAgentName(agent);
    }
    
    /**
     * First, tests the filters via the method <code>beforeAssert</code>, tries to assert the
     * formula, and then, if needed, tests the filters via the method <code>afterAssert</code>
     * The assertion depends of the formula. If the formula match the pattern:
     * <ul>
     * <li> "(I agent ??phi)", the formula is asserted.
     * <li> "(not (I ??agt ??phi))", the formula "(I ??agt ??phi)" is removed if
     * it is in the base
     * <li> "(B agent ??phi)", the formula phi is asserted if it is an
     * <code>AtomicFormula</code>
     * <li> "(B agent (not ??phi)), if the formula phi is in the base, it is removed
     * </ul>
     * <i>agent</i> represents the current semantic agent.<br>
     * If the formula matches another pattern nothing is done.
     * At the end of the assert, each boolean that corresponds to the
     * applicability of the observers is set to false.
     * @param formula the formula to assert in the belief base
     */
    public void assertFormula(Formula formula) {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "AssertFormula into the KBase : " + formula);
        formula = new BelieveNode(agentName, formula).getSimplifiedFormula();
		try {
            Formula formulaToAssert = formula;
			for (int i =0; i < assertFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Applying assert filter BEFORE (" + i + "): " + assertFilterList.get(i));
                if (formulaToAssert instanceof TrueNode) return;
                formulaToAssert = ((KBAssertFilter)assertFilterList.get(i)).apply(formulaToAssert);
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "beforeAssert Filter (" + i + ") resulted in : " + formulaToAssert);
            }
			
			continuous.assertFormula(formulaToAssert);

		} catch (Exception e) {
            e.printStackTrace();
        }
    } 

    /**
     * Tests the queryRef filters. If no filter applies, queries the belief
     * base on the formula (B ??agent (= ??ire ??Result)), where
     * ??ire is instantiated with the identifying expression given in parameter.
     * The ListOfTerm corresponding to the solutions is the value of the meta
     * variable ??Result, which could be null.
     * @param expression the identifying expression on which the query relates.
     * @return a list of terms that corresponds to the question or null (meaning
     * the answer : "I do not know")
     */
    public ListOfTerm queryRef(IdentifyingExpression expression) {
		return continuous.queryRef(expression);
     } 

    /**
     * First tests if a query filter is applicable. In this case, returns the result
     * of the filter. If no filter applies, queries directly the belief base.
     * @return a list of solutions to the query
     * @param formula a formula
     */
    public ListOfMatchResults query(Formula formula) {
		formula = new BelieveNode(agentName, formula).getSimplifiedFormula();
        logger.log(Logger.FINEST, "Querying from the FilterKBaseImpl : " + formula);
        try {
            for (int i =0; i < queryFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing query filter (" + i + "): " + queryFilterList.get(i));
				QueryResult queryResult = ((KBQueryFilter)queryFilterList.get(i)).apply(formula, agentName);
                if (queryResult != null && queryResult.isFilterApplied()) {
                    return queryResult.getResult();
                }
            }
			return continuous.query(formula);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
   } 

    /** (non-Javadoc)
     * @deprecated 
     * @see jade.semantics.kbase.KBase#removeFormula(jade.semantics.interpreter.Finder)
     */
    public void removeFormula(Finder finder) {
        continuous.removeFormula(finder);
    }

    /**
     * It retracts the formula given in parameter from the belief base by
     * asserting (not (B agent formula)). The formula to retract may include
     * metavariables.
     * @param formula the formula to retract from the belief base
     */
    public void retractFormula(Formula formula) {
		continuous.retractFormula(formula);
    }

    /**
     * @inheritDoc
     */
    public void addFiltersDefinition(FiltersDefinition filtersDefinition) {
        for (int i=0 ; i < filtersDefinition.size() ; i++) {
            addKBFilter(filtersDefinition.getFilterDefinition(i).getFilter(), filtersDefinition.getFilterDefinition(i).getIndex());
        }
    } 

    /**
     * Adds a new <code>KBFilter</code>.
     * @param filter a filter
     * @param index the index to add the filter
     */
    private void addKBFilter(KBFilter filter, int index) {
        filter.setMyKBase(this);
        if (filter instanceof KBAssertFilter) {
            if (index == FilterKBase.END) {
                 assertFilterList.add(filter);
            } else {
                assertFilterList.add((index<0 ? assertFilterList.size() : index), filter);
            }
        }
        else if (filter instanceof KBQueryFilter) {
            if (index == FilterKBase.END) {
                queryFilterList.add(filter);
            } else {
                queryFilterList.add((index<0 ? queryFilterList.size() : index), filter);
            }
        }
    } 

    /* (non-Javadoc)
     * @see jade.semantics.kbase.FilterKBase#addKBAssertFilter(jade.semantics.kbase.filter.KBAssertFilter)
     */
    public void addKBAssertFilter(KBAssertFilter filter) {
	       addKBFilter(filter, -1);
	} 

    /**
     * @inheritDoc
     */
    public void addKBAssertFilter(KBAssertFilter filter, int index) {
        addKBFilter(filter, index);
    } 

    /**
     * @inheritDoc
     */
    public void addKBQueryFilter(KBQueryFilter filter) {
        addKBFilter(filter, -1);
    } 

    /**
     * @inheritDoc
     */
    public void addKBQueryFilter(KBQueryFilter filter, int index) {
        addKBFilter(filter, index);
    } 


    /**
     * @inheritDoc
     */
    public void removeKBAssertFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(assertFilterList);
    } 

    /**
     * @inheritDoc
     */
     public void removeKBQueryFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(queryFilterList);
    } 

    /**
     * For debugging purpose only
     */
    public void viewFilterQuery() {
        System.err.println("----------- Query Filter ---------");
        for(int i = 0; i < queryFilterList.size(); i++) {
            System.err.println(queryFilterList.get(i));
        }
        System.err.println("----------------------------------");
    } 

	/**
     * @inheritDoc
     */
    public void addObserver(final Observer obs) {
		continuous.addObserver(obs);
		Formula[] formulas = obs.getObservedFormulas();
		for (int i=0; i<formulas.length; i++) {
	        Set result = new SortedSetImpl();
	        getObserverTriggerPatterns(formulas[i], result);	
			for (Iterator it = result.iterator(); it.hasNext();) {
				obs.addFormula((Formula)it.next());
			}
		}
    } 

	/**
     * @inheritDoc
     */
    public void removeObserver(Finder finder) {
		continuous.removeObserver(finder);
    } 

	/**
     * @inheritDoc
     */
    public void removeObserver(Observer obs) {
		continuous.removeObserver(obs);
    }
	
	/**
     * @inheritDoc
     */
	public void updateObservers(Formula formula) {
		continuous.updateObservers(formula);
	}

	/**
	 * @return the internal continuous mechanism which is a also KBase
	 */
	public KBase getContinuous() {
		return continuous;
	}
	
	/**
     * @inheritDoc
     */
	public String[] toStrings() {
		return continuous.toStrings();
	}
	
    /**
     * Calls the getObserverTriggerPatterns method of each query filters.
     * At least, the pattern corresponding to the observed formula
     * is returned.
     * @param formula an observed formula
     * @return set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public void getObserverTriggerPatterns(Formula formula, Set result) {
		for (int i =0; i < queryFilterList.size(); i++) {
			((KBQueryFilter)queryFilterList.get(i)).getObserverTriggerPatterns(formula, result);
        }
        try {
            MatchResult matchResult = SL.match(bPattern,formula);
            if (matchResult != null) {
				Formula phi = matchResult.getFormula("phi");
				result.add(phi);
			} else {
                result.add(formula);
            }
        } catch (SL.WrongTypeException wte) {
            wte.printStackTrace();
        }
    }
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#isClosed(jade.semantics.lang.sl.grammar.Formula, jade.semantics.lang.sl.tools.ListOfMatchResults)
     */
    public boolean isClosed(Formula pattern, ListOfMatchResults values)
    {
    	return continuous.isClosed(pattern, values);
    }
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#addClosedPredicate(jade.semantics.lang.sl.grammar.Formula)
     */
    public void addClosedPredicate(Formula pattern)
    {
    	continuous.addClosedPredicate(pattern);
    }
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#removeClosedPredicate(jade.semantics.interpreter.Finder)
     */
    public void removeClosedPredicate(Finder finder)
    {
    	continuous.removeClosedPredicate(finder);
    }

} 