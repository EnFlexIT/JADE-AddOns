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
package jade.semantics.kbase.filters;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.KBaseDecorator;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.TrueNode;
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
 * @version Date: 2007/03/21 Revision: 1.5 (Thierry Martinez) - decorator pattern applied
 */
public class FilterKBaseImpl extends KBaseDecorator implements FilterKBase {
	
    /*********************************************************************/
    /**                         INNER CLASS                             **/
    /*********************************************************************/
	static class Cache {
		
		static int CACHE_SIZE = 5;
		
		static class CacheElement  {
			protected Formula formula = null;
			protected QueryResult result = null;
			protected ArrayList reasons = null;
			public CacheElement(Formula formula, QueryResult result, ArrayList reasons) {
				this.formula = formula;
				this.result = result;
				if (result == null) {
					this.reasons = reasons;
				}
				else {
					this.reasons = null;
				}
			}
		}
		
		CacheElement[] elements = new CacheElement[CACHE_SIZE];
		int filledSize = 0;
		
		protected CacheElement query(Formula formula) {
			for(int i=0; i<filledSize; i++) {
				if (elements[i].formula.equals(formula)) {
					return elements[i];
				}
			}
			return null;
		}
		
		protected void add(Formula formula, QueryResult result, ArrayList reasons) {
			if ( filledSize < elements.length ) {
				elements[filledSize++] = new CacheElement(formula, result, reasons);
			}
			else {
				for(int i=0; i<elements.length-1; i++) {
					elements[i] = elements[i+1];
				}				
				elements[filledSize-1] = new CacheElement(formula, result, reasons);
			}
		}
	
		protected void clear() {
			filledSize = 0;
		}
		
	}

    /*********************************************************************/
    /**                        PRIVATE FIELDS                           **/
    /*********************************************************************/
    private final Formula bPattern;

    /**
     * List of assertion filters
     */
    private ArrayList assertFilterList;

    /**
     * List of query filters
     */
    private ArrayList queryFilterList;

     /**
     * Logger
     */
    private Logger logger;

    /**
     * Cache
     */
    private Cache cache;

    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Creates a new belief base.
     * @param decorated the wrapped kbase used to store the facts, 
     *                  which should not be null.
     */
    public FilterKBaseImpl(KBase decorated) {
    	this(decorated, null);
    } 

    /**
     * Creates a new belief base.
     * @param decorated the wrapped kbase used to store the facts, 
     *                  which should not be null.
     * @param loader a particular filters loader.
     */
    public FilterKBaseImpl(KBase decorated, FilterKBaseLoader loader) {
    	super(decorated);
		this.assertFilterList = new ArrayList();
        this.queryFilterList = new ArrayList();

      this.cache = new Cache();
    	this.logger = Logger.getMyLogger("SEMANTICS.FilterKBaseImpl@" + Tools.term2AID(getAgentName()).getLocalName());

        this.bPattern = new BelieveNode(getAgentName(), new MetaFormulaReferenceNode("phi"));
        
        if ( loader != null ) {loader.load(this);}
    } 
    
    
    /***************************
     * METHODS TO ACCESS KBASE *
     ***************************/
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBaseDecorator#assertFormula(jade.semantics.lang.sl.grammar.Formula)
     */
    @Override
	public void assertFormula(Formula formula) {
    	cache.clear();
       formula = new BelieveNode(getAgentName(), formula).getSimplifiedFormula();
        if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "AAAAAAAAAA ASSERTING: " + formula);
		try {
			for (int i =0; i < assertFilterList.size(); i++) {
	            Formula formulaToAssert = formula;
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Applying assert filter BEFORE (" + i + "): " + assertFilterList.get(i));
                if (formula instanceof TrueNode) return;
//                System.err.println("Applying assert filter BEFORE (" + i + "): " + assertFilterList.get(i));
                formula = ((KBAssertFilter)assertFilterList.get(i)).apply(formula);
//                System.err.println("beforeAssert Filter (" + i + ") resulted in : " + formulaToAssert);
                if (logger.isLoggable(Logger.FINE) && formulaToAssert != formula)
                	logger.log(Logger.FINE, "APPLIED ASSERT-FILTER " + assertFilterList.get(i) + "\n" +
                			"INPUT= " + formulaToAssert + "\nOUTPUT=" + formula);
            }
			
			decorated.assertFormula(formula);

		} catch (Exception e) {
            e.printStackTrace();
        }
    } 

    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBaseDecorator#query(jade.semantics.lang.sl.grammar.Formula)
     */
    @Override
	public QueryResult query(Formula formula, ArrayList reasons) {
    	QueryResult result = QueryResult.UNKNOWN;
		QueryResult.BoolWrapper goOn = new QueryResult.BoolWrapper(true);
    	ArrayList falsityReasons = new ArrayList();

		formula = new BelieveNode(getAgentName(), formula).getSimplifiedFormula();

    	Cache.CacheElement cacheResult = cache.query(formula);
//		Cache.CacheElement cacheResult = null;
    	if (cacheResult != null) {
    		QueryResult.addReasons(reasons, cacheResult.reasons);
    		return cacheResult.result;
    	}

		if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "QQQQQQQQQQ QUERYING: " + formula);
        for (int i =0; goOn.getBool() && i < queryFilterList.size(); i++) {
//      	System.err.println("Testing query filter (" + i + "): " + queryFilterList.get(i));
        	QueryResult filterResult = ((KBQueryFilter)queryFilterList.get(i)).apply(formula, falsityReasons, goOn);
        	if (filterResult != null) {
        		if (result == QueryResult.UNKNOWN) {
        			result = filterResult;
        		}
        		else {
        			result = result.union(filterResult);
        		}
        		if (logger.isLoggable(Logger.FINE))
        			logger.log(Logger.FINE, "APPLIED " + queryFilterList.get(i) + " QUERY-FILTER(cont=" + goOn.getBool() + "): " + filterResult + "\n" +
        					"ON: " + formula);
        	}
        }
        
        if (goOn.getBool()) {
        	result = (result == QueryResult.UNKNOWN ?
        			decorated.query(formula, falsityReasons) : result.union(decorated.query(formula, falsityReasons)));
        }
//        System.err.println("FilterKBaseImpl.result = " + result);
        if (result == QueryResult.UNKNOWN) {
        	QueryResult.addReasons(reasons, falsityReasons);
        }
        else if (result.isEmpty()) {
           	// To ensure that an empty result always references QueryResult.KNOWN
        	result = QueryResult.KNOWN;
        }
    	if (result == null) {
        	logger.log(Logger.FINE, "QQQQQQQQQQ RESULT: null, BECAUSE " + reasons + "\nON: " + formula);
    	}
    	else {
        	logger.log(Logger.FINE, "QQQQQQQQQQ RESULT: " + result + "\nON: " + formula);
    	}
    	cache.add(formula, result, falsityReasons);
        return result;
   } 
    
    
    /******************************
     * METHODS TO ADD NEW FILTERS *
     ******************************/
    
     /* (non-Javadoc)
     * @see jade.semantics.kbase.FilterKBase#addFiltersDefinition(jade.semantics.kbase.FiltersDefinition)
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
            if (index == END) {
                 assertFilterList.add(filter);
            } else {
                assertFilterList.add((index<0 ? assertFilterList.size() : index), filter);
            }
        }
        else if (filter instanceof KBQueryFilter) {
            if (index == END) {
                queryFilterList.add(filter);
            } else {
                queryFilterList.add((index<0 ? queryFilterList.size() : index), filter);
            }
        }
    } 

    /* (non-Javadoc)
     * @see jade.semantics.kbase.FilterKBase#addKBAssertFilter(jade.semantics.kbase.filters.KBAssertFilter)
     */
    public void addKBAssertFilter(KBAssertFilter filter) {
	       addKBFilter(filter, -1);
	} 

    /* (non-Javadoc)
     * @see jade.semantics.kbase.filters.FilterKBase#addKBAssertFilter(jade.semantics.kbase.filters.KBAssertFilter, int)
     */
    public void addKBAssertFilter(KBAssertFilter filter, int index) {
        addKBFilter(filter, index);
    } 

    /* (non-Javadoc)
     * @see jade.semantics.kbase.filters.FilterKBase#addKBQueryFilter(jade.semantics.kbase.filters.KBQueryFilter)
     */
    public void addKBQueryFilter(KBQueryFilter filter) {
        addKBFilter(filter, -1);
    } 

    /* (non-Javadoc)
     * @see jade.semantics.kbase.filters.FilterKBase#addKBQueryFilter(jade.semantics.kbase.filters.KBQueryFilter, int)
     */
    public void addKBQueryFilter(KBQueryFilter filter, int index) {
        addKBFilter(filter, index);
    } 

    
    /*****************************
     * METHODS TO REMOVE FILTERS *
     *****************************/
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.filters.FilterKBase#removeKBAssertFilter(jade.semantics.interpreter.Finder)
     */
    public void removeKBAssertFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(assertFilterList);
    } 

     /* (non-Javadoc)
     * @see jade.semantics.kbase.filters.FilterKBase#removeKBQueryFilter(jade.semantics.interpreter.Finder)
     */
    public void removeKBQueryFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(queryFilterList);
    } 

    
    /*******************************
     * METHODS TO HANDLE OBSERVERS *
     *******************************/
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBaseDecorator#addObserver(jade.semantics.kbase.observers.Observer)
     */
    /**
     * Do not forget to initialise observers once added to the KBase
     * by using <code>observer.update(null);</code>
     * Only observers added in the <code>setupKBase</code> method are
     * automatically initialised. 
     **/
    @Override
	public void addObserver(final Observer obs) {
		decorated.addObserver(obs); 
		Formula[] formulas = obs.getObservedFormulas();
		for (int i=0; i<formulas.length; i++) {
	        Set result = new SortedSetImpl();
	        //System.err.println("add observer, i="+i);
	        //System.err.println("f=formula(i)="+formulas[i]);
	        //System.err.println("now call getObsTrigPatt(f,"+result+")");
	        getObserverTriggerPatterns(formulas[i], result);	
			for (Iterator it = result.iterator(); it.hasNext();) {
				Formula f = (Formula)it.next();
				obs.addFormula(f);
			}
		}
    } 

     /* (non-Javadoc)
     * @see jade.semantics.kbase.filters.FilterKBase#getObserverTriggerPatterns(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.Set)
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
			} 
            else {
				result.add(formula);
            }
        } catch (SL.WrongTypeException wte) {
            wte.printStackTrace();
        }
    }
    
   
    /*******************
     * DISPLAY METHODS *
     *******************/
    
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBaseDecorator#toStrings()
     */
    @Override
	public ArrayList toStrings() {
    	ArrayList result = new ArrayList(queryFilterList.size() * 5);
    	
    	// standard KBase
    	result.add("******* KBase *******");
    	toStrings(result, decorated.toStrings());
    	
    	// KBases of KBQueryFilters
    	for (Iterator it = queryFilterList.iterator(); it.hasNext(); ) {
    		toStrings(result, ((KBQueryFilter)it.next()).toStrings());
    	}
    	
    	// clean result from undesired predicates (only for internal use, not useful for user)
    	// scan backward because of removal and shifts (in order not to miss one element)
    	for (int i=result.size()-1;i>=0;i--) {
    		Formula stri = SL.formula(result.get(i).toString());
    		if (stri instanceof PredicateNode) {
    			String symbol = ((PredicateNode)stri).as_symbol().toString(); 
    			if ( symbol.equals("sanction")  || symbol.equals("is-institutional") || symbol.equals("is_observing"))  {
    				result.remove(i);
//    				System.err.println("do not print "+stri);
    			}
    		}
    		// FIXME : manual removal of not-formulas from the KBase ...?
    		if (stri instanceof NotNode) {
    			result.remove(i);
    		}
    	}
    	return result;
    }
    
    private void toStrings(ArrayList result, ArrayList strings) {
		if (strings != null) {
			for (Iterator j=strings.iterator(); j.hasNext(); ) {
				result.add(j.next());
			}
		}
    }
    
    
    /*********
     * DEBUG *
     *********/
    
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

} 
