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
 * KbaseImpl_List.java
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
import jade.semantics.kbase.filter.assertion.ObserverFilter;
import jade.semantics.kbase.filter.query.BeliefTransferFilter;
import jade.semantics.kbase.filter.query.IntentionTransferFilter;
import jade.semantics.kbase.filter.query.X_OrFilter;
import jade.semantics.kbase.filter.queryref.GetProposalFilter;
import jade.semantics.kbase.observer.Observer;
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
 * Class that implements the knowledge base api. The data are store in an 
 * <code>ArrayList</code>. The filters and the observer of the base are also 
 * store in <code>ArrayLists</code>. 
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00 Revision: 1.0
 */
public class KbaseImpl_List implements KBase_Filter, KBFilterManagment {
    
    /**
     * The agent that owns this knowledge base
     */
    SemanticAgent agent;
    
    /**
     * Storgae of knowledge 
     */
    ArrayList dataStorage;
    
    /**
     * List of assertion filters
     */
    ArrayList assertFilterList;
    
    /**
     * List of query filters
     */
    ArrayList queryFilterList;
    
    /**
     * List of queryRef filters
     */
    ArrayList queryRefFilterList;
        
    /**
     * Patterns for assertion and query methods
     */
    final Formula uPattern = 
        SLPatternManip.fromFormula("(U ??agt ??phi)");
    final Formula notUPattern = 
        SLPatternManip.fromFormula("(not (U ??agt ??phi))");
    final Formula iPattern = 
        SLPatternManip.fromFormula("(I ??agt ??phi)");
    final Formula notIPattern = 
        SLPatternManip.fromFormula("(not (I ??agt ??phi))");
    final Formula bNotPattern = 
        SLPatternManip.fromFormula("(B ??agt (not ??phi))");
    final Formula bPattern = 
        SLPatternManip.fromFormula("(B ??agt ??phi)");
    final Formula notBPattern = 
        SLPatternManip.fromFormula("(not(B ??agt ??phi))");
    final Formula notBRefPattern = 
        SLPatternManip.fromFormula("(forall ??var (not (B ??agt ??phi)))");
    final Formula notURefPattern = 
        SLPatternManip.fromFormula("(forall ??var (not (U ??agt ??phi)))");
    final Formula BRefPattern = 
        SLPatternManip.fromFormula("(exists ??var (B ??agt ??phi))");
    final Formula URefPattern = 
        SLPatternManip.fromFormula("(exists ??var (U ??agt ??phi))");
    final IdentifyingExpression anyIREPattern = 
        (IdentifyingExpression)SLPatternManip.fromTerm("(any ??var ??phi)");
    final Formula donePattern = 
        SLPatternManip.fromFormula("(done ??act ??phi)");
    
     /**
     * Logger
     */
    Logger logger;
    
    /**
     * The filter manager
     */
    
    ArrayList observationTable;
    
    
   /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Constructor
     * @param agent the owner of the base
     * @param filterMgt the filter manager
     */
    public KbaseImpl_List(SemanticAgent agent) {
        this.agent = agent;

        assertFilterList = new ArrayList();
        queryFilterList = new ArrayList();
        queryRefFilterList = new ArrayList();
        dataStorage = new ArrayList();
        logger = Logger.getMyLogger("jade.core.semantics.kbase.KbaseImpl_List");
        
        addKBQueryFilter(new BeliefTransferFilter(agent.getMyStandardCustomization()));
        addKBQueryFilter(new IntentionTransferFilter(agent.getMyStandardCustomization()));
        addKBQueryFilter(new jade.semantics.kbase.filter.query.X_AndFilter());
        addKBQueryFilter(new X_OrFilter());

        addKBQueryRefFilter(new GetProposalFilter(agent.getMyStandardCustomization()));

        observationTable = new ArrayList();
        
        addKBAssertFilter(new ObserverFilter());
    } // End of KbaseImpl_List/2
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @see jade.semantics.kbase.KBase#assertFormula(jade.lang.sl.grammar.Formula)
     */
    public void assertFormula(Formula formula) {
       if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "AssertFormula into the KBase : " + formula);
        try {
            Formula formulaToAssert = formula;
            for (int i =0; i < assertFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Applying assert filter BEFORE (" + i + "): " + ((KBAssertFilter)assertFilterList.get(i)));
                formulaToAssert = ((KBAssertFilter)assertFilterList.get(i)).beforeAssert(formulaToAssert);
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "beforeAssert Filter (" + i + ") resulted in : " + formulaToAssert);
            }
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw asserting : " + formulaToAssert);
            MatchResult matchResult = SLPatternManip.match(bPattern,formulaToAssert);
            if (matchResult != null) {
                Formula phi = matchResult.getFormula("??phi");
                if ( ((phi instanceof AtomicFormula) && !(phi instanceof TrueNode)) ||
                    ((phi instanceof NotNode) && (((NotNode)phi).as_formula() instanceof AtomicFormula)) ) {
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
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing assert filter AFTER (" + i + "): " + ((KBAssertFilter)assertFilterList.get(i)));
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
     * @see jade.semantics.kbase.KBase#queryRef(jade.lang.sl.grammar.IdentifyingExpression)
     */   
    public ListOfTerm queryRef(IdentifyingExpression expression) {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Querying-ref from the KBase : " + expression);
        try {
            for (int i =0; i < queryRefFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing query-ref filter (" + i + "): " + ((KBQueryRefFilter)queryRefFilterList.get(i)));
                if (((KBQueryRefFilter)queryRefFilterList.get(i)).isApplicable(expression, agent.getAgentName())) {
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
     * Returns the terms of the database that match the pattern 
     * @param pattern
     * @return
     */
    private ListOfTerm getSolutions(Formula pattern) {
        ListOfTerm result = new ListOfTerm();
        for (int j = 0; j < dataStorage.size(); j++) {
            MatchResult matchResult = SLPatternManip.match(pattern, (Node)dataStorage.get(j));
            if (matchResult != null) {
               try {
                   result.add(matchResult.getTerm("??X"));
               }
               catch(Exception e) {e.printStackTrace();}
           }
        }
        return result;
    } // End of getSolutions/1

    /**
     * 
     * @param pattern
     * @return
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
     * @see jade.semantics.kbase.KBase#query(jade.lang.sl.grammar.Formula)
     */
    public Bindings query(Formula formula) {
        Bindings result = new BindingsImpl();
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Querying from the KBase : " + formula);
        try {
            for (int i =0; i < queryFilterList.size(); i++) {
                if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Testing query filter (" + i + "): " + ((KBQueryFilter)queryFilterList.get(i)));
                if (((KBQueryFilter)queryFilterList.get(i)).isApplicable(formula, agent.getAgentName())) {
                   if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "query Filter (" + i + ") applied");
                   return (((KBQueryFilter)queryFilterList.get(i)).apply(formula));
               }
            }
            
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw querying : " + formula);
            MatchResult matchResult = SLPatternManip.match(bPattern,formula);
            if (matchResult != null) {
                return getBindings(matchResult.getFormula("??phi"));
            }           
            if ((matchResult = SLPatternManip.match(notBPattern,formula)) != null) {
                if (getBindings(matchResult.getFormula("??phi")) == null) return result;
                else return null;
            }
            if (((matchResult = SLPatternManip.match(notBRefPattern,formula))!=null) && 
                matchResult.getTerm("??agt").equals(this.agent.getAgentName())) {
                IdentifyingExpression ide = (IdentifyingExpression)SLPatternManip.instantiate(anyIREPattern,
                        "??var", matchResult.getVariable("??var"),
                        "??phi", matchResult.getFormula("??phi"));
                if (ide.as_term() instanceof Variable) {
                    Formula pattern = (Formula)SLPatternManip.toPattern(ide.as_formula(), (Variable)ide.as_term());
                    if (getBindings(pattern) == null) return result;
                    else return null;
                }
            }
            if (((matchResult = SLPatternManip.match(BRefPattern, formula)) != null) && 
                matchResult.getTerm("??agt").equals(this.agent.getAgentName())) {
                IdentifyingExpression ide = (IdentifyingExpression)SLPatternManip.instantiate(anyIREPattern,
                        "??var", matchResult.getVariable("??var"),
                        "??phi", matchResult.getFormula("??phi"));
                if (ide.as_term() instanceof Variable) {
                    Formula pattern = (Formula)SLPatternManip.toPattern(ide.as_formula(), (Variable)ide.as_term());
                    return getBindings(pattern).removeBind(matchResult.getVariable("??var").lx_name());
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
    
    
    public void removeFormula(Finder finder) {
        finder.removeFromList(dataStorage);
    }
    /**
     * @see jade.semantics.kbase.KBFilterManagment#addFiltersDefinition(jade.core.semantics.kbase.FiltersDefinition)
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
     * @see jade.semantics.kbase.KBFilterManagment#addKBAssertFilter(jade.core.semantics.kbase.filter.KBAssertFilter)
     */
    public void addKBAssertFilter(KBAssertFilter filter) {
       addKBFilter(filter, -1);
    } // End of addKBAssertFilter/1

    /**
     * @see jade.semantics.kbase.KBFilterManagment#addKBAssertFilter(jade.core.semantics.kbase.filter.KBAssertFilter, int)
     */
    public void addKBAssertFilter(KBAssertFilter filter, int index) {
        addKBFilter(filter, index);
    } // End of addKBAssertFilter/2

    /**
     * @see jade.semantics.kbase.KBFilterManagment#addKBQueryFilter(jade.core.semantics.kbase.filter.KBQueryFilter)
     */
    public void addKBQueryFilter(KBQueryFilter filter) {
        addKBFilter(filter, -1);
    } // End of addKBQueryFilter/1

    /**
     * @see jade.semantics.kbase.KBFilterManagment#addKBQueryFilter(jade.core.semantics.kbase.filter.KBQueryFilter, int)
     */
    public void addKBQueryFilter(KBQueryFilter filter, int index) {
        addKBFilter(filter, index);
    } // End of addKBQueryFilter/2

    /**
     * @see jade.semantics.kbase.KBFilterManagment#addKBQueryRefFilter(jade.core.semantics.kbase.filter.KBQueryRefFilter)
     */
    public void addKBQueryRefFilter(KBQueryRefFilter filter) {
        addKBFilter(filter, -1);
    } // End of addKBQueryRefFilter/1    

    /**
     * @see jade.semantics.kbase.KBFilterManagment#addKBQueryRefFilter(jade.core.semantics.kbase.filter.KBQueryRefFilter, int)
     */
    public void addKBQueryRefFilter(KBQueryRefFilter filter, int index) {
        addKBFilter(filter, index);
    } // End of addKBQueryRefFilter/2    

    /**
     * @see jade.semantics.kbase.KBFilterManagment#removeKBAssertFilter(jade.core.semantics.interpreter.Identifier)
     */
    public void removeKBAssertFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(assertFilterList);
    } // End of removeKBAssertFilter/1
    
    /**
     * @see jade.semantics.kbase.KBFilterManagment#removeKBQueryFilter(jade.core.semantics.interpreter.Identifier)
     */
    public void removeKBQueryFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(queryFilterList);
    } // End of removeKBQueryFilter/1
    
    /**
     * @see jade.semantics.kbase.KBFilterManagment#removeKBQueryRefFilter(jade.core.semantics.interpreter.Identifier)
     */
    public void removeKBQueryRefFilter(Finder filterIdentifier) {
        filterIdentifier.removeFromList(queryRefFilterList);
    } // End of removeKBQueryRefFilter/1
    
    /**
     * @return Returns the dataStorage.
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
     */
    public String[] viewDataInBase() {
        String[] result = new String[dataStorage.size()];
            for(int i = 0; i < dataStorage.size(); i++) {
                result[i] = dataStorage.get(i).toString();
                //System.out.println(result[i]);
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
     * 
     * @param agent the semantic agent to set
     */
    public void setAgent(SemanticAgent agent) {
        this.agent = agent;
    } // End of setAgent/1
    
    
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
     * @return Returns the observationTable.
     */
    public ArrayList getObservationTable() {
        return observationTable;
    } // End of getObservationTable/0
    
    /**
     * @param observationTable The observationTable to set.
     */
    public void setObservationTable(ArrayList observationTable) {
        this.observationTable = observationTable;
    } // End of setObservationTable/1

    /**
     * 
     * @author Vincent Pautret
     * @version
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

} // End of class KbaseImpl_List



