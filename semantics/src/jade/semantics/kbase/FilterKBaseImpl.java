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
import jade.semantics.kbase.filter.KBQueryRefFilter;
import jade.semantics.kbase.filter.assertion.AllIREFilter;
import jade.semantics.kbase.filter.assertion.EventMemoryFilter;
import jade.semantics.kbase.filter.assertion.ObserverFilter;
import jade.semantics.kbase.filter.query.BeliefTransferFilter;
import jade.semantics.kbase.filter.query.IntentionTransferFilter;
import jade.semantics.kbase.filter.query.OrFilter;
import jade.semantics.kbase.filter.queryref.CFPFilter;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.AtomicFormula;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.Logger;
import jade.util.leap.ArrayList;

/**
 * Class that implements the knowledge base api. The data are stored in an 
 * <code>ArrayList</code>. The filters and the observers of the base are also 
 * stored in <code>ArrayList</code>s. 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 */
public class FilterKBaseImpl implements FilterKBase, KBFilterManagment {
    
    /**
     * The maximum size of the event memory list
     */
    private int event_memory_size = 10;
    
    /**
     * The agent that owns this knowledge base
     */
    private SemanticAgent agent;
    
    /**
     * Storgae of knowledge 
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
     * List of events already done
     */
    private ArrayList eventMemory;

    /**
     * List of observations on the table
     */
    private ArrayList observationTable;

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
//  private final Formula bNotPattern = 
//  SLPatternManip.fromFormula("(B ??agt (not ??phi))");
    private final Formula bPattern = 
        SLPatternManip.fromFormula("(B ??agt ??phi)");
    private final Formula notBPattern = 
        SLPatternManip.fromFormula("(not(B ??agt ??phi))");
    private final Formula notBRefPattern = 
        SLPatternManip.fromFormula("(forall ??var (not (B ??agt ??phi)))");
    private final Formula notURefPattern = 
        SLPatternManip.fromFormula("(forall ??var (not (U ??agt ??phi)))");
    private final Formula BRefPattern = 
        SLPatternManip.fromFormula("(exists ??var (B ??agt ??phi))");
    private final Formula URefPattern = 
        SLPatternManip.fromFormula("(exists ??var (U ??agt ??phi))");
    private final IdentifyingExpression anyIREPattern = 
        (IdentifyingExpression)SLPatternManip.fromTerm("(any ??var ??phi)");
//  private final Formula donePattern = 
//  SLPatternManip.fromFormula("(done ??act ??phi)");
    
    /**
     * Logger
     */
    private Logger logger;
    
    
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new knowledge base. Adds the five query filters:
     * <ul>
     * <li>BeliefTransferFilter
     * <li>IntentionTransferFilter
     * <li>AndFilter
     * <li>OrFilter
     * <li>EventMemoryFilter
     * </ul>
     * Adds the queryRef filter: CFPFilter.<br>
     * Adds three assert filters:
     * <ul>
     * <li>ObserverFilter
     * <li>EventMemoryFilter
     * <li>AllIREFilter
     * </ul>
     * @param agent the owner of the base
     */
    public FilterKBaseImpl(SemanticAgent agent) {
        this.agent = agent;
        
        assertFilterList = new ArrayList();
        queryFilterList = new ArrayList();
        queryRefFilterList = new ArrayList();
        dataStorage = new ArrayList();
        eventMemory = new ArrayList();
        logger = Logger.getMyLogger("jade.core.semantics.kbase.KbaseImpl_List");
        
        addKBQueryFilter(new BeliefTransferFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        addKBQueryFilter(new IntentionTransferFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        addKBQueryFilter(new jade.semantics.kbase.filter.query.AndFilter());
        addKBQueryFilter(new OrFilter());
        addKBQueryFilter(new jade.semantics.kbase.filter.query.EventMemoryFilter());
        
        addKBQueryRefFilter(new CFPFilter(agent.getSemanticCapabilities().getMyStandardCustomization()));
        
        observationTable = new ArrayList();
        
        addKBAssertFilter(new ObserverFilter());
        addKBAssertFilter(new EventMemoryFilter());
        addKBAssertFilter(new AllIREFilter());
    } // End of KbaseImpl_List/2
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * First tests the filters via the method <code>beforeAssert</code>, tries to assert the 
     * formula, and then, if needed, tests the filters via the method <code>afterAssert</code>
     * The assertion depends of the formula. If the formula match the pattern:
     * <ul>
     * <li> "(I jsa ??phi)", the formula is asserted.
     * <li> "(not (I ??agt ??phi))", the formula "(I ??agt ??phi)" is removed if
     * it is in the base 
     * <li> "(B jsa ??phi)", the formula phi is asserted if it is an 
     * <code>AtomicFormula</code>
     * <li> "(B jsa (not ??phi)), if the formula phi is in the base, it is removed
     * </ul>  
     * If the formula matches another pattern nothing is done.
     * @inheritDoc
     */
    public void assertFormula(Formula formula) {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "AssertFormula into the KBase : " + formula);
        try {
            Formula formulaToAssert = formula;
            for (int i =0; i < assertFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Applying assert filter BEFORE (" + i + "): " + assertFilterList.get(i));
                formulaToAssert = ((KBAssertFilter)assertFilterList.get(i)).beforeAssert(formulaToAssert);
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "beforeAssert Filter (" + i + ") resulted in : " + formulaToAssert);
            }
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw asserting : " + formulaToAssert);
            MatchResult matchResult = SLPatternManip.match(bPattern,formulaToAssert);
            if (matchResult != null) {
                Formula phi = matchResult.getFormula("??phi");
                if ( ((phi instanceof AtomicFormula) && !(phi instanceof TrueNode)) ||
                        ((phi instanceof NotNode) && (((NotNode)phi).as_formula() instanceof AtomicFormula))) {
                    if (!dataStorage.contains(phi)) {
                        removeFormula(new NotNode(phi).getSimplifiedFormula());
                        dataStorage.add(phi);
                    }
                }
            }
            else if ((matchResult = SLPatternManip.match(iPattern,formulaToAssert)) != null) {
                dataStorage.add(formulaToAssert);
            }
            else if ((matchResult = SLPatternManip.match(notIPattern,formulaToAssert)) != null) {
                dataStorage.remove(((NotNode)formulaToAssert).as_formula());
            }
            else if (((matchResult = SLPatternManip.match(uPattern,formulaToAssert)) != null) || 
                    ((matchResult = SLPatternManip.match(notUPattern, formulaToAssert)) != null) ) {
            }
            else if ((matchResult = SLPatternManip.match(notBPattern, formulaToAssert)) != null) {
                throw new Exception();
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
     * Tests the queryRef filters. If no filter applies, 
     * queries directly the base and returns the found solutions identified by
     * the given expression.
     * @inheritDoc
     */   
    public ListOfTerm queryRef(IdentifyingExpression expression) {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Querying-ref from the KBase : " + expression);
        try {
            for (int i =0; i < queryRefFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing query-ref filter (" + i + "): " + queryRefFilterList.get(i));
                if (((KBQueryRefFilter)queryRefFilterList.get(i)).isApplicable(expression, agent.getSemanticCapabilities().getAgentName())) {
                    if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "query-ref Filter (" + i + ") applied");
                    return ((KBQueryRefFilter)queryRefFilterList.get(i)).apply(expression);
                }
            }
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw querying-ref : " + expression);
            if (expression.as_term() instanceof Variable) {
                Formula pattern = (Formula)SLPatternManip.toPattern(expression.as_formula(), (Variable)expression.as_term());
                ListOfTerm listOfResult = getSolutions(pattern);
                if ((expression instanceof AnyNode && listOfResult.size() >= 1) ||
                        (expression instanceof AllNode) ||
                        (expression instanceof IotaNode && listOfResult.size() == 1)) {
                    return listOfResult;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of queryRef/1
    
    /**
     * Returns the terms of the database that match the pattern. 
     * @param pattern a pattern
     * @return list of solutions
     */
    private ListOfTerm getSolutions(Formula pattern) {
        ListOfTerm result = new ListOfTerm();
        for (int j = 0; j < dataStorage.size(); j++) {
            MatchResult matchResult = SLPatternManip.match(pattern, (Node)dataStorage.get(j));
            if (matchResult != null) {
                try {
                    result.add(matchResult.getTerm("X"));
                }
                catch(Exception e) {e.printStackTrace();}
            }
        }
        return result;
    } // End of getSolutions/1
    
    /**
     * Tries to match the formula on each data stored in the base. In case of
     * success, a new Bind is created with the name of the <code>MetaTermReferenceNode</code>
     * name and <code>MetaTermReferenceNode</code> value.
     * @param pattern a pattern 
     * @return a list of solutions
     */
    private Bindings getBindings(Formula pattern) {
        Bindings bindings = null;
        for (int j = 0; j < dataStorage.size(); j++) {
            MatchResult matchResult = SLPatternManip.match(pattern, (Node)dataStorage.get(j));
            if (matchResult != null) {
                bindings = new BindingsImpl();
                for (int i = 0; i < matchResult.size(); i++) {
                    Node node = matchResult.get(i);
                    if (node instanceof MetaTermReferenceNode) {
                        bindings.addBind(((MetaTermReferenceNode)node).lx_name(), ((MetaTermReferenceNode)node).sm_value());
                    } else {
                        return null;
                    }
                }
                break;
            }
        }
        return bindings;
    } // End of getBindings/1
    
    /**
     * First tests if a query filter is applicable. In this case, returns the result
     * of the filter. If no filter applies, queries directly the knowledge base.  
     * @inheritDoc
     */
    public Bindings query(Formula formula) {
        Bindings result = new BindingsImpl();
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Querying from the KBase : " + formula);
        try {
            for (int i =0; i < queryFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing query filter (" + i + "): " + queryFilterList.get(i));
                if (((KBQueryFilter)queryFilterList.get(i)).isApplicable(formula, agent.getSemanticCapabilities().getAgentName())) {
                    if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "query Filter (" + i + ") applied");
                    return (((KBQueryFilter)queryFilterList.get(i)).apply(formula));
                }
            }
            
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw querying : " + formula);
            MatchResult matchResult = SLPatternManip.match(bPattern,formula);
            if (matchResult != null) {
                return getBindings(matchResult.getFormula("phi"));
            }           
            if ((matchResult = SLPatternManip.match(notBPattern,formula)) != null) {
                if (getBindings(matchResult.getFormula("phi")) == null) return result;
                else return null;
            }
            if (((matchResult = SLPatternManip.match(notBRefPattern,formula))!=null) && 
                    matchResult.getTerm("agt").equals(agent.getSemanticCapabilities().getAgentName())) {
                IdentifyingExpression ide = (IdentifyingExpression)SLPatternManip.instantiate(anyIREPattern,
                        "var", matchResult.getVariable("var"),
                        "phi", matchResult.getFormula("phi"));
                if (ide.as_term() instanceof Variable) {
                    Formula pattern = (Formula)SLPatternManip.toPattern(ide.as_formula(), (Variable)ide.as_term());
                    if (getBindings(pattern) == null) return result;
                    else return null;
                }
            }
            if (((matchResult = SLPatternManip.match(BRefPattern, formula)) != null) && 
                    matchResult.getTerm("agt").equals(agent.getSemanticCapabilities().getAgentName())) {
                IdentifyingExpression ide = (IdentifyingExpression)SLPatternManip.instantiate(anyIREPattern,
                        "var", matchResult.getVariable("var"),
                        "phi", matchResult.getFormula("phi"));
                if (ide.as_term() instanceof Variable) {
                    Formula pattern = (Formula)SLPatternManip.toPattern(ide.as_formula(), (Variable)ide.as_term());
                    return getBindings(pattern).removeBind(matchResult.getVariable("var").lx_name());
                }
            }
            if ((matchResult = SLPatternManip.match(iPattern, formula)) != null) {
                return getBindings(formula);
            }
            if ((matchResult = SLPatternManip.match(notIPattern, formula)) != null) {
                if (getBindings(((NotNode)formula).as_formula()) == null) return result;
                else return null;
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
     * @inheritDoc
     */
    public void addFiltersDefinition(FiltersDefinition filtersDefinition) {
        for (int i=0 ; i < filtersDefinition.size() ; i++) {
            addKBFilter(filtersDefinition.get(i).getFilter(), filtersDefinition.get(i).getIndex());
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
            assertFilterList.add((index<0 ? assertFilterList.size() : index), (KBAssertFilter)filter);
        }
        else if (filter instanceof KBQueryFilter) {
            queryFilterList.add((index<0 ? queryFilterList.size() : index), filter);
        } 
        else if (filter instanceof KBQueryRefFilter) {
            queryRefFilterList.add((index<0 ? queryRefFilterList.size() : index), filter);
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
    public void addKBQueryRefFilter(KBQueryRefFilter filter) {
        addKBFilter(filter, -1);
    } // End of addKBQueryRefFilter/1    
    
    /**
     * @inheritDoc
     */
    public void addKBQueryRefFilter(KBQueryRefFilter filter, int index) {
        addKBFilter(filter, index);
    } // End of addKBQueryRefFilter/2    
    
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
     * @inheritDoc
     */
    public void removeKBQueryRefFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(queryRefFilterList);
    } // End of removeKBQueryRefFilter/1
    
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
    public void removeAllFormulae(Formula pattern) {
        for (int i = dataStorage.size()-1; i >= 0 ; i--) {
            MatchResult matchResult = SLPatternManip.match(pattern, (Node)dataStorage.get(i));
            if (matchResult != null) {
                dataStorage.remove(i);
            }
        }
    } // End of removeAllFormulae/1
    
    /**
     * For debugging purpose only
     * @return an array that conatains a string representation of each data in 
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
     * Adds a new event in the event memory
     * @param action an action expression of an done event 
     */
    public void addEventInMemory(ActionExpression action) {
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
        observationTable.add(observation);
    } // End of addObserver/1
    
    /**
     * Removes from the kbase the observers detected by the the finder.
     * @param finder the finder
     */
    public void removeObserver(Finder finder) {
        for (int i= observationTable.size() -1; i >=0; i--) {
            if (finder.identify(((Observation)observationTable.get(i)).getObserver())) {
                observationTable.remove(i);
            }
        }
    } // End of removeObserver/1
    
    /**
     * Returns the observationTable.
     * @return the observationTable.
     */
    public ArrayList getObservationTable() {
        return observationTable;
    } // End of getObservationTable/0
    
    /**
     * Sets the observation table.
     * @param observationTable The observationTable to set.
     */
    public void setObservationTable(ArrayList observationTable) {
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
     * Defines an observation on the knowledge base.
     * @author Vincent Pautret
     * @version Date: 2005/06/10 Revision: 1.0
     */
    public class Observation {
        
        /**
         * The observer that makes the observation
         */
        Observer observer;
        
        /**
         * The current observed value
         */
        Bindings currentValue;
        
        /**
         * Constructor
         * @param observer the observer that makes observation
         */
        public Observation(Observer observer) {
            this.observer = observer;
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
        public Bindings getCurrentValue() {
            return currentValue;
        } // End of getCurrentValue/0
        
        /**
         * @param currentValue The currentValue to set.
         */
        public void setCurrentValue(Bindings currentValue) {
            this.currentValue = currentValue;
        } // End of setCurrentValue/1
        
    } // End of class Observation
    
} // End of class FilterKbaseImpl



