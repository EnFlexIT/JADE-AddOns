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
 * FilterKbaseImpl.java
 * Created on 23 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.kbase.filter.KBAssertFilter;
import jade.semantics.kbase.filter.KBFilter;
import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.kbase.filter.assertion.AllIREFilter;
import jade.semantics.kbase.filter.assertion.AndFilter;
import jade.semantics.kbase.filter.assertion.EventMemoryFilter;
import jade.semantics.kbase.filter.assertion.ObserverFilter;
import jade.semantics.kbase.filter.query.BeliefTransferFilter;
import jade.semantics.kbase.filter.query.CFPFilter;
import jade.semantics.kbase.filter.query.ExistsFilter;
import jade.semantics.kbase.filter.query.ForallFilter;
import jade.semantics.kbase.filter.query.IREFilter;
import jade.semantics.kbase.filter.query.IntentionTransferFilter;
import jade.semantics.kbase.filter.query.OrFilter;
import jade.semantics.kbase.filter.query.QueryResult;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.AtomicFormula;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.LoopingInstantiationException;
import jade.util.Logger;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

import java.util.HashSet;

/**
 * Class that implements the belief base api. The data are stored in an 
 * <code>ArrayList</code>. The filters and the observers of the base are also 
 * stored in <code>ArrayList</code>s. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 */
public class FilterKBaseImpl implements FilterKBase {
    
    /**
     * The maximum size of the event memory list
     */
    private int event_memory_size = 10;
    
    /**
     * The agent that owns this belief base
     */
    private SemanticAgent agent;
    
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
     * List of patterns which are closed 
     */
    private ArrayList closedPredicateList;
    
    /**
     * List of events already done
     */
    private ArrayList eventMemory;

    /**
     * List of observations on the table
     */
    private HashMapForCollections observationTable;

    /**
     * Patterns for assertion and query methods
     */
    private final Formula uPattern = 
        SLPatternManip.fromFormula("(U ??agt ??phi)");
    private final Formula notUPattern = 
        SLPatternManip.fromFormula("(not (U ??agt ??phi))");
    private final Formula iPattern = 
        SLPatternManip.fromFormula("(I ??agt ??phi)");
    private final Formula notIPattern = 
        SLPatternManip.fromFormula("(not (I ??agt ??phi))");
    private final Formula bPattern = 
        SLPatternManip.fromFormula("(B ??agt ??phi)");
    private final Formula notBPattern = 
        SLPatternManip.fromFormula("(not(B ??agt ??phi))");
    private final Formula notURefPattern = 
        SLPatternManip.fromFormula("(forall ??var (not (U ??agt ??phi)))");
    private final Formula URefPattern = 
        SLPatternManip.fromFormula("(exists ??var (U ??agt ??phi))");
    private final Formula ireFormula = 
        SLPatternManip.fromFormula("(B ??agent (= ??ire ??Result))");
    /**
     * Logger
     */
    private Logger logger;
    
    /**
     * HashSet of Observations which contains the observers to trigger when a
     * formula is asserted
     */
    HashSet observersToApplied;
    
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
     * @param agent the owner of the base
     */
    public FilterKBaseImpl(SemanticAgent agent) {
        this.agent = agent;
        
        this.assertFilterList = new ArrayList();
        this.queryFilterList = new ArrayList();
        this.queryRefFilterList = new ArrayList();
        this.dataStorage = new ArrayList();
        this.eventMemory = new ArrayList();
        this.closedPredicateList = new ArrayList();
        this.observationTable = new HashMapForCollections();
        
        this.logger = Logger.getMyLogger("jade.core.semantics.kbase.KbaseImpl_List");

        addKBQueryFilter(new CFPFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        addKBQueryFilter(new IREFilter());
        addKBQueryFilter(new BeliefTransferFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        addKBQueryFilter(new IntentionTransferFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        addKBQueryFilter(new jade.semantics.kbase.filter.query.AndFilter());
        addKBQueryFilter(new OrFilter());
        addKBQueryFilter(new jade.semantics.kbase.filter.query.EventMemoryFilter());
        addKBQueryFilter(new ExistsFilter());
        addKBQueryFilter(new ForallFilter());
        
     //   addKBQueryRefFilter(new jade.semantics.kbase.filter.queryref.CFPFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        
        addKBAssertFilter(new AllIREFilter());
        addKBAssertFilter(new AndFilter());
        addKBAssertFilter(new jade.semantics.kbase.filter.assertion.ForallFilter());
        addKBAssertFilter(new EventMemoryFilter());
        addKBAssertFilter(new ObserverFilter());
    } // End of KbaseImpl_List/2
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
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
        formula = new BelieveNode(agent.getSemanticCapabilities().getAgentName(), formula).getSimplifiedFormula();
        try {
            Formula formulaToAssert = formula;
            for (int i =0; i < assertFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Applying assert filter BEFORE (" + i + "): " + assertFilterList.get(i));
                formulaToAssert = ((KBAssertFilter)assertFilterList.get(i)).apply(formulaToAssert);
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "beforeAssert Filter (" + i + ") resulted in : " + formulaToAssert);
            }
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw asserting : " + formulaToAssert);
            MatchResult matchResult = SLPatternManip.match(bPattern,formulaToAssert);
            if (matchResult != null) {
                Formula phi = matchResult.getFormula("phi");
                if ( ((phi instanceof AtomicFormula) && !(phi instanceof TrueNode)) ||
                        ((phi instanceof NotNode) && (((NotNode)phi).as_formula() instanceof AtomicFormula))) {
                    if (!dataStorage.contains(phi)) {
                        removeFormula(new NotNode(phi).getSimplifiedFormula());
                        dataStorage.add(phi);
                        setObserversToBeapplied(phi);
                    }
                }
            }
            else if ((matchResult = SLPatternManip.match(iPattern,formulaToAssert)) != null) {
                dataStorage.add(formulaToAssert);
                setObserversToBeapplied(formulaToAssert);
            }
            else if ((matchResult = SLPatternManip.match(notIPattern,formulaToAssert)) != null) {
                dataStorage.remove(((NotNode)formulaToAssert).as_formula());
                setObserversToBeapplied(formulaToAssert);
            }
            else if (((matchResult = SLPatternManip.match(uPattern,formulaToAssert)) != null) || 
                    ((matchResult = SLPatternManip.match(notUPattern, formulaToAssert)) != null) ) {
            }
            else if ((matchResult = SLPatternManip.match(notBPattern, formulaToAssert)) != null) {
                Formula phi = matchResult.getFormula("phi");
                if ( ((phi instanceof AtomicFormula) && !(phi instanceof TrueNode)) ||
                        ((phi instanceof NotNode) && (((NotNode)phi).as_formula() instanceof AtomicFormula))) {
                    removeAllFormulae(matchResult.getFormula("phi"));
                }
            }
            
            for (int i =0; i < assertFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing assert filter AFTER (" + i + "): " + assertFilterList.get(i));
                if (((KBAssertFilter)assertFilterList.get(i)).mustApplyAfter) {
                    if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "applying afterAssert Filter (" + i + ")");
                    ((KBAssertFilter)assertFilterList.get(i)).afterAssert(formula);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();   
        }
    } // End of assertFormula/1

    /**
     * Sets the observers which must be tested according to the formula which 
     * has been just asserted.
     * @param formulaToAssert the formula which has been just asserted
     */
    private void setObserversToBeapplied(Formula formulaToAssert) {
        observersToApplied = new HashSet();
        for (Iterator iter = observationTable.keySet().iterator(); iter.hasNext();) {
            Formula pattern = (Formula)iter.next();
            if (SLPatternManip.match(pattern,formulaToAssert) != null || 
                    (!formulaToAssert.isMentalAttitude(agent.getSemanticCapabilities().getAgentName()) && SLPatternManip.match(pattern,new NotNode(formulaToAssert).getSimplifiedFormula()) != null)) {
                observersToApplied.addAll((HashSet)observationTable.get(pattern));
            }
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
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Querying-ref from the KBase : " + expression);
        try {
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw querying-ref : " + expression);
            Formula form = (Formula)SLPatternManip.instantiate(ireFormula, 
                    "agent", agent.getSemanticCapabilities().getAgentName(),
                    "ire", expression);
            
            ListOfMatchResults solutions = query(form);
            if (solutions != null && solutions.size() > 0) {
                Term term;
                if (((MatchResult)solutions.get(0)).getTerm("Result") instanceof IdentifyingExpression) {
                    term = ((MatchResult)solutions.get(0)).getTerm("ire");
                } else {
                    term = ((MatchResult)solutions.get(0)).getTerm("Result");    
                }
                ListOfTerm result = new ListOfTerm();
                if (term instanceof TermSetNode) {
                    for (int j = 0; j < ((TermSetNode)term).as_terms().size(); j++) {
                        result.add( ((TermSetNode)term).as_terms().get(j));
                    }
                } else {
                    result.add(term);
                }
                return result;
            } else if (solutions != null && solutions.size() == 0) {
                return new ListOfTerm();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of queryRef/1
    
    /**
     * Tries to match the formula on each data stored in the base. In case of
     * success, a new ListOfMatchResults is created and contains all 
     * the corresponding MatchResults.
     * @param pattern a pattern to test on each data of the base
     * @return a list of MatchResults, which match the given pattern, and null if
     * the pattern does not match with any data stored in the base.  
     */
    private ListOfMatchResults getMatchResults(Formula pattern) {
        ListOfMatchResults solutions = null;
        for (int j = 0; j < dataStorage.size(); j++) {
            MatchResult matchResult = SLPatternManip.match(pattern, (Node)dataStorage.get(j));
            if (matchResult != null) {
                if (solutions == null) solutions = new ListOfMatchResults();
                if (matchResult.size() != 0) solutions.add(matchResult);
            }
        }
        return solutions;
    } // End of getMatchResults/1
    
    /**
     * First tests if a query filter is applicable. In this case, returns the result
     * of the filter. If no filter applies, queries directly the belief base.  
     * @return a list of solutions to the query
     * @param formula a formula 
     */
    public ListOfMatchResults query(Formula formula) {
        ListOfMatchResults result = new ListOfMatchResults();
        formula = new BelieveNode(agent.getSemanticCapabilities().getAgentName(), formula).getSimplifiedFormula();
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Querying from the KBase : " + formula);
        try {
            for (int i =0; i < queryFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing query filter (" + i + "): " + queryFilterList.get(i));
                QueryResult queryResult = ((KBQueryFilter)queryFilterList.get(i)).apply(formula, agent.getSemanticCapabilities().getAgentName());
                if (queryResult.isFilterApplied()) {
                    return queryResult.getResult();
                }
            }
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw querying : " + formula);
            MatchResult matchResult = SLPatternManip.match(bPattern,formula);
            if (matchResult != null && 
                  matchResult.getTerm("agt").equals(agent.getSemanticCapabilities().getAgentName())) {
                if (matchResult.getFormula("phi") instanceof NotNode) {
                    result = getMatchResults(matchResult.getFormula("phi"));
                    if (result == null) {
                        if (!isClosed(((NotNode)matchResult.getFormula("phi")).as_formula(), result)) {
                            return null;
                        } else {
                            ListOfMatchResults result2 = query(((NotNode)matchResult.getFormula("phi")).as_formula());
                            if (result2 != null) {
                                return null;
                            } else {
                                return new ListOfMatchResults();
                            }
                        }
                    } else {
                        return result;
                    }
                } else {
                    result = getMatchResults(matchResult.getFormula("phi"));
                    if (result == null) {
                        NotNode notNode = new NotNode(matchResult.getFormula("phi"));
                        if (!isClosed(notNode, result)) {
                            return null;
                        } else {
                            ListOfMatchResults result2 = query(notNode);
                            if (result2 != null) {
                                return null;
                            } else {
                                return new ListOfMatchResults();
                            }
                        }
                    } else {
                        return result;
                    }
                }
            }                   
            if ((matchResult = SLPatternManip.match(notBPattern,formula)) != null && 
                  matchResult.getTerm("agt").equals(agent.getSemanticCapabilities().getAgentName())) {
                if (getMatchResults(matchResult.getFormula("phi")) == null){
                    return result;
                }
                return null;
            }
            if ((matchResult = SLPatternManip.match(iPattern, formula)) != null) {
                return getMatchResults(formula);
            }
            if ((matchResult = SLPatternManip.match(notIPattern, formula)) != null) {
                if (getMatchResults(((NotNode)formula).as_formula()) == null) {
                    return result;
                }
                return null;
            }    
            if (formula.equals(new TrueNode())) {
                return result;
            }
            if (((matchResult = SLPatternManip.match(URefPattern,formula)) != null) || 
                    ((matchResult = SLPatternManip.match(uPattern,formula)) != null)) {
                return null;
            }           
            if (((matchResult = SLPatternManip.match(notURefPattern,formula)) != null) || 
                    ((matchResult = SLPatternManip.match(notUPattern, formula)) != null)) {
                return result;
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of query/1
    
    
    /**
     * Removes the specified formula from the base.
     * @param formula a formula
     * @return true if the formula to remove was actually in the KBase
     */
    public boolean removeFormula(Formula formula) {
        boolean result = false;
        for (int i = dataStorage.size() - 1; i >=0 ; i--) {
            if (dataStorage.get(i).equals(formula)) {
                dataStorage.remove(i);
                result = true;
            }
        }
        return result;
    } // End of removeFormula/1
    
    /**
     * @inheritDoc
     */
    public void removeFormula(Finder finder) {
        finder.removeFromList(dataStorage);
    }
    
    /**
     * It retracts the formula given in parameter from the belief base by 
     * asserting (not (B agent formula)). The formula to retract may include 
     * metavariables.
     * @param formula the formula to retract from the belief base
     */
    public void retractFormula(Formula formula) {
        assertFormula(new NotNode(new BelieveNode(agent.getSemanticCapabilities().getAgentName(), formula)).getSimplifiedFormula());
    }
    
    /**
     * @inheritDoc
     */
    public void addFiltersDefinition(FiltersDefinition filtersDefinition) {
        for (int i=0 ; i < filtersDefinition.size() ; i++) {
            addKBFilter(filtersDefinition.getFilterDefinition(i).getFilter(), filtersDefinition.getFilterDefinition(i).getIndex());
        }
    } // End of addFiltersDefinition/1
    
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
    } // End of addKBFilter/
    
    /**
     * @inheritDoc
     */
    public void addKBAssertFilter(KBAssertFilter filter) {
        addKBFilter(filter, -1);
    } // End of addKBAssertFilter/1
    
    /**
     * @inheritDoc
     */
    public void addKBAssertFilter(KBAssertFilter filter, int index) {
        addKBFilter(filter, index);
    } // End of addKBAssertFilter/2
    
    /**
     * @inheritDoc
     */
    public void addKBQueryFilter(KBQueryFilter filter) {
        addKBFilter(filter, -1);
    } // End of addKBQueryFilter/1
    
    /**
     * @inheritDoc
     */
    public void addKBQueryFilter(KBQueryFilter filter, int index) {
        addKBFilter(filter, index);
    } // End of addKBQueryFilter/2
    
    
    /**
     * @inheritDoc
     */
    public void removeKBAssertFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(assertFilterList);
    } // End of removeKBAssertFilter/1
    
    /**
     * @inheritDoc
     */
    public void removeKBQueryFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(queryFilterList);
    } // End of removeKBQueryFilter/1
    
    /**
     * Returns the dataStorage.
     * @return the dataStorage.
     */
    public ArrayList getDataStorage() {
        return dataStorage;
    } // End of getDataStorage/0
    
    /**
     * Removes all the formulae that match the specified pattern.
     * @param pattern an SL pattern
     */
    private void removeAllFormulae(Formula pattern) {
        for (int i = dataStorage.size()-1; i >= 0 ; i--) {
            MatchResult matchResult = SLPatternManip.match(pattern, (Node)dataStorage.get(i));
            if (matchResult != null) {
                dataStorage.remove(i);
            }
        }
    } // End of removeAllFormulae/1
    
    /**
     * For debugging purpose only
     * @return an array that contains a string representation of each data in 
     * the base
     */
    public String[] viewDataInBase() {
        String[] result = new String[dataStorage.size()];
        for(int i = 0; i < dataStorage.size(); i++) {
            result[i] = dataStorage.get(i).toString();
        }
        return result;
    } // End of viewDataInBase/0
    
    /**
     * For debugging purpose only
     */
    public void viewFilterQuery() {
        System.err.println("----------- Query Filter ---------");
        for(int i = 0; i < queryFilterList.size(); i++) {
            System.err.println(queryFilterList.get(i));
        }
        System.err.println("----------------------------------");
    } // End of viewFilterQuery/0
    
    /**
     * Sets the agent that owns the base
     * @param agent the semantic agent to set
     */
    public void setAgent(SemanticAgent agent) {
        this.agent = agent;
    } // End of setAgent/1
    
    /**
     * Gets the SemanticAgent that owns the base
     * @return the SemanticAgent that owns the base
     */
    public SemanticAgent getAgent() {
        return this.agent;
    } // End of getAgent/1
    /**
     * Adds a new event in the event memory
     * @param action an action expression of an done event 
     */
    public void addEventInMemory(ActionExpression action) {
        try {
            SLPatternManip.substituteMetaReferences(action);
            SLPatternManip.removeOptionalParameter(action);
        } catch (LoopingInstantiationException e) {
            e.printStackTrace();
        }
        eventMemory.add(0, action);
        if (eventMemory.size() == event_memory_size + 1) eventMemory.remove(event_memory_size);
    }
    
    /**
     * Returns the event memory
     * @return the event memory
     */
    public ArrayList getEventMemory() {
        return eventMemory;
    }
    /**
     * Removes the top of the event memory and returns it.
     * @return the top of the event memory
     */
    public ActionExpression removeTopEventFromMemory() {
        return (ActionExpression)eventMemory.remove(0);
    }
    
    /**
     * Returns the top of the event memory.
     * @return the top of event memory
     */
    public ActionExpression getTopEventFromMemory() {
        return (ActionExpression)eventMemory.get(0);
    }
    
    /**
     * Adds a new Observer in the observer table
     * @param o the observer to add
     */
    public void addObserver(final Observer o) {
        Observation observation = new Observation(o);
        observation.setCurrentValue(query(o.getObservedFormula()));
        observation.setTriggerPatterns(computeObserverTriggerPatterns(o.getObservedFormula()));
        for (Iterator iter = observation.getTriggerPatterns().iterator(); iter.hasNext();) {
            observationTable.put(iter.next(), observation);
        }
    } // End of addObserver/1
    
    /**
     * Removes from the kbase the observers detected by the the finder.
     * @param finder the finder
     */
    public void removeObserver(Finder finder) {
        ArrayList toSupress = new ArrayList();
        for (Iterator iter = observationTable.values().iterator(); iter.hasNext();) {
            HashSet elem = (HashSet)iter.next();
            for (java.util.Iterator iter2 = elem.iterator(); iter2.hasNext();) {
                Observation elem2 = (Observation)iter2.next();
                if (finder.identify(elem2.getObserver())) {
                  toSupress.add(elem2);
              } 
            }
            for (int i = 0; i < toSupress.size(); i++) {
                elem.remove(toSupress.get(i));    
            }
        }
        observationTable.removeUselessKeys();
    } // End of removeObserver/1
    
    /**
     * Removes the given observer
     * @param obs the observer to be removed
     */
    public void removeObserver(Observer obs) {
        ArrayList toSupress = new ArrayList();
        for (Iterator iter = observationTable.values().iterator(); iter.hasNext();) {
            HashSet elem = (HashSet)iter.next();
            for (java.util.Iterator iter2 = elem.iterator(); iter2.hasNext();) {
                Observation elem2 = (Observation)iter2.next();
                if (obs.equals(elem2.getObserver())) {
                  toSupress.add(elem2);
              } 
            }
            for (int i = 0; i < toSupress.size(); i++) {
                elem.remove(toSupress.get(i));    
            }
        }
        observationTable.removeUselessKeys();
    }
    /**
     * Returns the observationTable.
     * @return the observationTable.
     */
    public HashMapForCollections getObservationTable() {
        return observationTable;
    } // End of getObservationTable/0
    
    /**
     * Sets the observation table.
     * @param observationTable The observationTable to set.
     */
    public void setObservationTable(HashMapForCollections observationTable) {
        this.observationTable = observationTable;
    } // End of setObservationTable/1
    
    /**
     * Sets the size of the event memory
     * @param size the new size of the event memory
     */
    public void setEventMemorySize(int size) {
        event_memory_size = size;
    }
    
    /**
     * Adds a closed predicate if it is not already in the list.
     * @param pattern a new closed predicate
     */
    public void addClosedPredicate(Formula pattern) {
        if (!(pattern instanceof BelieveNode) && !(pattern instanceof IntentionNode) &&
                !(pattern instanceof UncertaintyNode) && !(pattern instanceof OrNode) &&
                !(pattern instanceof ExistsNode)) {
            if ((pattern instanceof NotNode)) {
                if (
                    !(((NotNode)pattern).as_formula() instanceof BelieveNode) &&
                    !(((NotNode)pattern).as_formula() instanceof IntentionNode) &&
                    !(((NotNode)pattern).as_formula() instanceof UncertaintyNode) &&
                    !isClosed(pattern, null)) {
                closedPredicateList.add(pattern);
                }
            } else if (pattern instanceof AndNode) {
                addClosedPredicate(((AndNode)pattern).as_left_formula());
                addClosedPredicate(((AndNode)pattern).as_right_formula());
            } else if (pattern instanceof ForallNode) {
                addClosedPredicate(new NotNode(pattern));
            } else if (!isClosed(pattern, null)) {
                 closedPredicateList.add(pattern);    
            }
        }        
    }
    
    /**
     * Removes the closed predicate identifying by the given finder.
     * @param finder a finder
     */
    public void removeClosedPredicate(Finder finder) {
        finder.removeFromList(closedPredicateList);
    }
    
    /**
     * Returns the list of closed predicates
     * @return the list of closed predicates
     */
    public ArrayList getCLosedPredicatList() {
        return closedPredicateList;
    }
    
    /**
     * Returns true if the formula given in parameter is closed on the domain of 
     * values defined by the list of MatchResult given in parameter. 
     * In details, it returns true if:
     * <ul>
     * <li>the formula is a mental attitude of the current agent;
     * <li>if the formula is an And Formula, the left part or the right part of
     * the formula should be closed;
     * <li>if the formula is an Or Formula, the left part and the right part of
     * the formula should be closed;
     * <li>in the other cases, the formula should be match one of the patterns
     * stored in the closed patterns list. In this case, the solutions on the 
     * query on the formula should be the same as the domain of values.
     * </ul>  
     * Otherwise, returns false.
     * @param pattern the pattern to be tested
     * @param b the domain of values
     * @return true if the pattern is closed on the given domain, false if not.
     */
    public boolean isClosed(Formula pattern, ListOfMatchResults b) {
        if (pattern.isMentalAttitude(agent.getSemanticCapabilities().getAgentName())) {
            return true;
        } else if (pattern instanceof AndNode) {
            return (isClosed(((AndNode)pattern).as_left_formula(), b) || isClosed(((AndNode)pattern).as_right_formula(), b));
        } else if (pattern instanceof OrNode) {
            return (isClosed(((OrNode)pattern).as_left_formula(), b) && isClosed(((OrNode)pattern).as_right_formula(), b));
        } else if (pattern instanceof ExistsNode) {
            return (isClosed(((ExistsNode)pattern).as_formula(),b));
        } else if (pattern instanceof ForallNode) {
            return (isClosed(((ForallNode)pattern).as_formula(),b));
        }         
        else {
            try {
                for (int i = 0; i < closedPredicateList.size(); i++) {
                    if ((SLPatternManip.match((Node)closedPredicateList.get(i),pattern) != null)) {
                        ListOfMatchResults values = query(pattern);
                        if (b != null) {
                            return (b.equals(values));
                        }
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
    
    /**
     * Inner class which is a Data structure that gathers :
     * <ul>
     * <li> a boolean. If it is true, it means that the inner observer is applicable
     * and not if the boolean is false;
     * <li> an observer. 
     * <li> a set of patterns. If one of this patterns match an asserted formula
     * the value of the boolean is set to true (the observer becomes applicable).
     * <li> a list of match results corresponding to the current value of the
     * observed formula.  
     * </ul>
     * @author Vincent Pautret - France Telecom
     * @version Date:  Revision: 1.0
     */
    public class Observation {
        
        /**
         * The observer that makes the observation
         */
        Observer observer;
        
        /**
         * Set of patterns which make it possible to trigger the observer
         */
        Set triggerPatterns;
        
        /**
         * The current observed value
         */
        ListOfMatchResults currentValue;
        
        /**
         * Constructor. 
         * The boolean that tests the applicability is set to false, 
         * @param observer the observer that makes observation
         */
        public Observation(Observer observer) {
            this.observer = observer;
            triggerPatterns = new SortedSetImpl();
        } // End of Observation/1
        
        /**
         * @return the observer
         */
        public Observer getObserver() {
            return observer;
        } // End of getObserver/0
        
        /**
         * @return the currentValue.
         */
        public ListOfMatchResults getCurrentValue() {
            return currentValue;
        } // End of getCurrentValue/0
        
        /**
         * @param currentValue The currentValue to set.
         */
        public void setCurrentValue(ListOfMatchResults currentValue) {
            this.currentValue = currentValue;
        } // End of setCurrentValue/1
        
        
        /**
         * Returns the set of pattern which make it possible to trigger the
         * observer.
         * @return the set of pattern which make it possible to trigger the
         * observer.
         */
        public Set getTriggerPatterns() {
            return triggerPatterns;
        }
        
        /**
         * Sets the set of patterns which makes possible to trigger the 
         * observer. 
         * @param set set of patterns
         */
        public void setTriggerPatterns(Set set) {
            triggerPatterns = set;
        }
        
        /**
         * The String representation is the observed formula
         * @return the String representation of an observation.
         */
        public String toString() {
            return "Observation on : " + getObserver().getObservedFormula() +" \n";
            
        }
    } // End of class Observation
    
    /**
     * Calls the getObserverTriggerPatterns method of each query filters. 
     * At least, the pattern corresponding to the observed formula 
     * is returned.  
     * @param formula an observed formula
     * @return set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter. 
     */
    private Set computeObserverTriggerPatterns(Formula formula) {
        Set result = new SortedSetImpl();
        getObserverTriggerPatterns(formula, result);
        return result;
    }
    
    public void getObserverTriggerPatterns(Formula formula, Set result) {
        for (int i =0; (i < queryFilterList.size()); i++) {
        	if (!((KBQueryFilter)queryFilterList.get(i)).getObserverTriggerPatterns(formula, result)) {
        		return;
        	}
        }
        try {
            MatchResult matchResult = SLPatternManip.match(bPattern,formula);
            if (matchResult != null && 
                  matchResult.getTerm("agt").equals(agent.getSemanticCapabilities().getAgentName())) {
          //      if (matchResult.getFormula("phi") instanceof AtomicFormula && !(matchResult.getFormula("phi") instanceof TrueNode)) {
                    result.add(matchResult.getFormula("phi"));
           //     }
            } else {
                result.add(formula);
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }    	
    }
    
    /**
     * Gets the HashSet of observers to applied
     * @return the HashSet of observers to applied
     */
    public HashSet getObserversToApplied() {
        return observersToApplied;
    }
    
    /**
     * Sets the HashSet of observers to applied
     * @param set the HashSet of observers to applied
     */
    public void setObserversToBeApplied(HashSet set) {
        observersToApplied = set;
    }
} // End of class FilterKbaseImpl