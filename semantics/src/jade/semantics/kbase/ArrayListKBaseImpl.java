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
 * ArrayListKBaseImpl.java
 * Created on 20 december 2006
 * Author : Thierry Martinez & Vincent Pautret
 */
package jade.semantics.kbase;

import jade.semantics.interpreter.Finder;
import jade.semantics.kbase.observer.Observer;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.AtomicFormula;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.Logger;
import jade.util.leap.ArrayList;

import java.util.HashSet;

/**
 * Class that implements the belief base api. The data are stored in an
 * <code>ArrayList</code>. The observers of the base are also
 * stored in <code>ArrayList</code>s.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 * @version Date: 2006/12/21 Revision: 1.4 (Thierry Martinez)
 */
public class ArrayListKBaseImpl implements KBase {

    /**
     * The wrapping KBase
     */
    private KBase wrappingKBase;
    
    /**
     * All the beliefs of this KBase have the form (B agentName ...) 
     */
    Term agentName;

    /**
     * Storgae of belief
     */
    private ArrayList dataStorage;

    /**
     * List of patterns which are closed
     */
    private ArrayList closedPredicateList;

    /**
     * List of observers
     */
    private ArrayList observers;

    /**
     * Patterns for assertion and query methods
     */
    private final Formula uPattern;
    private final Formula notUPattern;
    private final Formula iPattern;
    private final Formula notIPattern;
    private final Formula bPattern;
    private final Formula notBPattern;
    private final Formula notURefPattern;
    private final Formula URefPattern;
    private final Formula ireFormula;

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
     * Creates a new belief base. 
     * @param agent the owner of the base
     */
    public ArrayListKBaseImpl(Term agentName, KBase wrappingKBase) {
    	this.wrappingKBase = wrappingKBase == null ? this : wrappingKBase;
    	this.agentName = agentName;
        this.dataStorage = new ArrayList();
        this.closedPredicateList = new ArrayList();
        this.observers = new ArrayList();
        this.logger = Logger.getMyLogger(getClass().getName());

        uPattern       = SL.fromFormula("(U "+agentName+" ??phi)");
        notUPattern    = SL.fromFormula("(not (U "+agentName+" ??phi))");
        iPattern       = SL.fromFormula("(I "+agentName+" ??phi)");
        notIPattern    = SL.fromFormula("(not (I "+agentName+" ??phi))");
        bPattern       = SL.fromFormula("(B "+agentName+" ??phi)");
        notBPattern    = SL.fromFormula("(not(B "+agentName+" ??phi))");
        notURefPattern = SL.fromFormula("(forall ??var (not (U "+agentName+" ??phi)))");
        URefPattern    = SL.fromFormula("(exists ??var (U "+agentName+" ??phi))");
        ireFormula     = SL.fromFormula("(B "+agentName+" (= ??ire ??Result))");
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
    }
    
	/**
     * Tries to assert the formula. The assertion depends of the formula. 
     * If the formula match the pattern:
     * <ul>
     * <li> "(I ??myself ??phi)", the formula is asserted.
     * <li> "(not (I ??myself ??phi))", the formula "(I ??myself ??phi)" is removed if
     * it is in the base
     * <li> "(B ??myself ??phi)", the formula phi is asserted if it is an
     * <code>AtomicFormula</code>
     * <li> "(B ??myself (not ??phi)), if the formula phi is in the base, it is removed
     * </ul>
     * <i>??myself</i> represents the current semantic agent.<br>
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
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw asserting : " + formulaToAssert);
            MatchResult matchResult = SL.match(bPattern,formulaToAssert);
            if (matchResult != null) {
                Formula phi = matchResult.getFormula("phi");
                if ( ((phi instanceof AtomicFormula) && !(phi instanceof TrueNode)) ||
                        ((phi instanceof NotNode) && (((NotNode)phi).as_formula() instanceof AtomicFormula))) {
                    if (!dataStorage.contains(phi)) {
                        removeFormula(new NotNode(phi).getSimplifiedFormula());
                        dataStorage.add(phi);						
						updateObservers(phi); 
                   }
                }
            }
            else if ((matchResult = SL.match(iPattern,formulaToAssert)) != null) {
                dataStorage.add(formulaToAssert);
				updateObservers(formulaToAssert); 
            }
            else if ((matchResult = SL.match(notIPattern,formulaToAssert)) != null) {
                dataStorage.remove(((NotNode)formulaToAssert).as_formula());
				updateObservers(formulaToAssert); 
            }
            else if (((matchResult = SL.match(uPattern,formulaToAssert)) != null) ||
                    ((matchResult = SL.match(notUPattern, formulaToAssert)) != null) ) {
            }
            else if ((matchResult = SL.match(notBPattern, formulaToAssert)) != null) {
                Formula phi = matchResult.getFormula("phi");
                if ( ((phi instanceof AtomicFormula) && !(phi instanceof TrueNode)) ||
                        ((phi instanceof NotNode) && (((NotNode)phi).as_formula() instanceof AtomicFormula))) {
                    removeAllFormulae(matchResult.getFormula("phi"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
	
	/* (non-Javadoc)
	 * @see jade.semantics.kbase.KBase#updateObservers()
	 */
	public void updateObservers(Formula formula) {
		for (int i=0; i<observers.size(); i++) {
			((Observer)observers.get(i)).update(formula);
		}
	}
	
    /**
     * Queries the belief base on the formula (B ??agent (= ??ire ??Result)), where
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
            Formula form = (Formula)SL.instantiate(ireFormula,
                    "agent", agentName,
                    "ire", expression);
            ListOfMatchResults solutions = getWrappingKBase().query(form);
//            ListOfMatchResults solutions = query(form); // VL, 20.02.73
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
    } 

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
            MatchResult matchResult = SL.match(pattern, (Node)dataStorage.get(j));
            if (matchResult != null) {
                if (solutions == null) solutions = new ListOfMatchResults();
                if (matchResult.size() != 0) solutions.add(matchResult);
            }
        }
        return solutions;
    } 

    /**
     * Queries directly the belief base.
     * @return a list of solutions to the query
     * @param formula a formula
     */
    public ListOfMatchResults query(Formula formula) {
        ListOfMatchResults result = new ListOfMatchResults();
        formula = new BelieveNode(agentName, formula).getSimplifiedFormula();
        logger.log(Logger.FINEST, "Querying from the KBase : " + formula);
        try {
            if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Raw querying : " + formula);
            MatchResult matchResult = SL.match(bPattern,formula);
            if (matchResult != null) {
                if (matchResult.getFormula("phi") instanceof NotNode) {
                    result = getMatchResults(matchResult.getFormula("phi"));
                    if (result == null) {
                        if (!isClosed(((NotNode)matchResult.getFormula("phi")).as_formula(), result)) {
                            return null;
                        } else {
                            ListOfMatchResults result2 = getWrappingKBase().query(((NotNode)matchResult.getFormula("phi")).as_formula());
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
                           ListOfMatchResults result2 = getWrappingKBase().query(notNode);
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
            if ((matchResult = SL.match(notBPattern,formula)) != null) {
            	// FIXME Use a continuous.query("phi") == null
                if (getMatchResults(matchResult.getFormula("phi")) == null){
                    return result;
                }
                return null;
            }
            if ((matchResult = SL.match(iPattern, formula)) != null) {
                return getMatchResults(formula);
            }
            if ((matchResult = SL.match(notIPattern, formula)) != null) {
                if (getMatchResults(((NotNode)formula).as_formula()) == null) {
                    return result;
                }
                return null;
            }
            if (formula.equals(new TrueNode())) {
                return result;
            }
            if (((matchResult = SL.match(URefPattern,formula)) != null) ||
                    ((matchResult = SL.match(uPattern,formula)) != null)) {
                return null;
            }
            if (((matchResult = SL.match(notURefPattern,formula)) != null) ||
                    ((matchResult = SL.match(notUPattern, formula)) != null)) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 


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
    } 

    /**
     * @inheritDoc
     * @deprecated use retractFormula(Formula) instead
     */
    public void removeFormula(Finder finder) {
        finder.removeFromList(dataStorage);
    }

    /**
     * It retracts the formula given in parameter from the belief base by
     * asserting (not (B ??myself ??formula)). The formula to retract may include
     * metavariables.
     * @param formula the formula to retract from the belief base
     */
    public void retractFormula(Formula formula) {
        getWrappingKBase().assertFormula(new NotNode(new BelieveNode(agentName, formula)).getSimplifiedFormula());
    }

	/**
     * Returns the dataStorage.
     * @return the dataStorage.
     */
    public ArrayList getDataStorage() {
        return dataStorage;
    } 

    /**
     * Removes all the formulae that match the specified pattern.
     * @param pattern an SL pattern
     */
    private void removeAllFormulae(Formula pattern) {
        for (int i = dataStorage.size()-1; i >= 0 ; i--) {
            MatchResult matchResult = SL.match(pattern, (Node)dataStorage.get(i));
            if (matchResult != null) {
                dataStorage.remove(i);
            }
        }
    } 

    /**
     * For debugging purpose only
     * @return an array that contains a string representation of each data in
     * the base
     */
    public String[] toStrings() {
        String[] result = new String[dataStorage.size()];
        for(int i = 0; i < dataStorage.size(); i++) {
            result[i] = dataStorage.get(i).toString();
        }
        return result;
    } 

    /**
     * Adds a new Observer in the observer table
     * @param o the observer to add
     */
    public void addObserver(final Observer obs) {
		observers.add(obs);
    } 

    /**
     * Removes from the kbase the observers detected by the the finder.
     * @param finder the finder
     */
    public void removeObserver(Finder finder) {
        ArrayList toSupress = new ArrayList();
		for (int i=0; i<observers.size(); i++) {
            if (finder.identify(observers.get(i))) {
                toSupress.add(observers.get(i));
			}
		}
		for (int i = 0; i < toSupress.size(); i++) {
			observers.remove(toSupress.get(i));
		}
    }

    /**
     * Removes the given observer
     * @param obs the observer to be removed
     */
    public void removeObserver(Observer obs) {
		observers.remove(obs);
	}
	
    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#addClosedPredicate(jade.semantics.lang.sl.grammar.Formula)
     */
    public void addClosedPredicate(Formula pattern) {
    	if (!isClosed(pattern, null)) {
    		if (pattern instanceof AndNode) {
    			addClosedPredicate(((AndNode)pattern).as_left_formula());
    			addClosedPredicate(((AndNode)pattern).as_right_formula());
    		} else if (pattern instanceof ForallNode) {
    			addClosedPredicate(new NotNode(pattern));
    		} else if (!pattern.isMentalAttitude(agentName)) {
    			closedPredicateList.add(pattern);
    		}
    	}
//        if (!(pattern instanceof BelieveNode) && !(pattern instanceof IntentionNode) &&
//                !(pattern instanceof UncertaintyNode) && !(pattern instanceof OrNode) &&
//                !(pattern instanceof ExistsNode)) {
//            if ((pattern instanceof NotNode)) {
//                if (
//                    !(((NotNode)pattern).as_formula() instanceof BelieveNode) &&
//                    !(((NotNode)pattern).as_formula() instanceof IntentionNode) &&
//                    !(((NotNode)pattern).as_formula() instanceof UncertaintyNode) &&
//                    !isClosed(pattern, null)) {
//                closedPredicateList.add(pattern);
//                }
//            } else if (pattern instanceof AndNode) {
//                addClosedPredicate(((AndNode)pattern).as_left_formula());
//                addClosedPredicate(((AndNode)pattern).as_right_formula());
//            } else if (pattern instanceof ForallNode) {
//                addClosedPredicate(new NotNode(pattern));
//            } else if (!isClosed(pattern, null)) {
//                 closedPredicateList.add(pattern);
//            }
//        }
    }

    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#removeClosedPredicate(jade.semantics.interpreter.Finder)
     */
    public void removeClosedPredicate(Finder finder) {
        finder.removeFromList(closedPredicateList);
    }

    /* (non-Javadoc)
     * @see jade.semantics.kbase.KBase#isClosed(jade.semantics.lang.sl.grammar.Formula, jade.semantics.lang.sl.tools.ListOfMatchResults)
     */
    public boolean isClosed(Formula pattern, ListOfMatchResults b) {
        if (pattern.isMentalAttitude(agentName)) {
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
                    if ((SL.match((Node)closedPredicateList.get(i),pattern) != null)) {
                        ListOfMatchResults values = getWrappingKBase().query(pattern);
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
} 