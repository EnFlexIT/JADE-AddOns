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
 * IREFilter.java
 * Created on 30 sept. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.kbase.filter.query;

import jade.semantics.kbase.filter.KBQueryFilter;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.ListOfMatchResults;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.Set;

/**
 * This filter applies when the query relates to the equality between an 
 * identifying expression and a term.
 * @author Vincent Pautret - France Telecom
 * @version Date:  Revision: 1.0
 */
public class IREFilter extends KBQueryFilter {

    /**
     * First part of the formula
     */
    private IdentifyingExpression ire ;
    
    /**
     * Second part of the formula
     */
    private Term term;
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula pattern;
    
    /**
     * Pattern that must match to apply the filter
     */
    private Formula notPattern;
    
    
    private ListOfNodes listOfNodes;
    private Term termPattern;
    private Formula formulaPattern;
    private Formula believeFormulaPattern;
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new Filter on the patterns (B ??agent (= ??ire ??term)) and 
     * (B ??agent (not (= ??ire ??term))).
     */
    public IREFilter() {
        pattern = SL.fromFormula("(B ??agent (= ??ire ??term))");
        notPattern = SL.fromFormula("(B ??agent (not (= ??ire ??term)))");
        listOfNodes = new ListOfNodes();
        termPattern = null;
        formulaPattern = null;
    } // End of AndFilter/1
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /** 
     * Returns true as first element if if the formula matches the pattern 
     * (B ??agent (= ??ire ??term)) or (B ??agent (not (= ??ire ??term)).  
     * The second element is a list of MatchResults corresponding to the solution
     * to the question.
     * If the filter returns false as first element, the second element is null.
     * @param formula a formula on which the filter is tested
     * @param agent a term that represents the agent is trying to apply the filter
     * @return an array with a Boolean meaning the applicability of the filter,
     * and a ListOfMatchResults that is the result of performing the filter. 
     */
    public QueryResult apply(Formula formula, Term agent) {
        QueryResult queryResult = new QueryResult();
        ListOfNodes listOfNodes = null;
        Term termPattern = null;
        Formula formulaPattern = null;
        try {
            MatchResult applyResult = SL.match(pattern,formula);
            if (applyResult != null) {
                if (applyResult.getTerm("ire") instanceof IdentifyingExpression) {
                    ire = (IdentifyingExpression)applyResult.getTerm("ire");
                    term = applyResult.getTerm("term");
                } else if (applyResult.getTerm("term") instanceof IdentifyingExpression) {
                    ire = (IdentifyingExpression)applyResult.getTerm("term");
                    term = applyResult.getTerm("ire");
                } else {
                    return queryResult;
                }
                believeFormulaPattern = SL.fromFormula("(B "+ agent +" ??Formula)");
                getPattern();
                listOfNodes = this.listOfNodes;
                termPattern = this.termPattern;
                formulaPattern = this.formulaPattern;
                
                queryResult.setResult(patternProcess(formula, listOfNodes, termPattern, formulaPattern));
                queryResult.setFilterApplied(true);
                return queryResult;
            } else {
                applyResult = SL.match(notPattern,formula);
                if (applyResult != null) {
                    if (applyResult.getTerm("ire") instanceof IdentifyingExpression) {
                        ire = (IdentifyingExpression)applyResult.getTerm("ire");
                        term = applyResult.getTerm("term");
                    } else if (applyResult.getTerm("term") instanceof IdentifyingExpression) {
                        ire = (IdentifyingExpression)applyResult.getTerm("term");
                        term = applyResult.getTerm("ire");
                    } else {
                        return queryResult;
                    }
                    believeFormulaPattern = SL.fromFormula("(B "+ agent +" ??Formula)");
                    getPattern();
                    listOfNodes = this.listOfNodes;
                    termPattern = this.termPattern;
                    formulaPattern = this.formulaPattern;

                    queryResult.setResult(notPatternProcess(formula, listOfNodes, termPattern, formulaPattern));
                    queryResult.setFilterApplied(true);
                    return queryResult;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryResult;
    } // End of apply/1
    
    /**
     * Returns a ListOfMatchResults corresponding to the answer to the query. If
     * the ??term is a meta variable, returns a list of MatchResults with only one 
     * MatchResult containing the assignation between the meta varaible corresponding to
     * ??term and the solution. If ??term is a constant, returns an empty list
     * if ??term equals the answer to the query on formula, and null if not.
     * @param formula the formula to be queried
     * @return a list of solutions.
     */
    private ListOfMatchResults patternProcess(Formula formula, ListOfNodes listOfNodes, Term termPattern, Formula formulaPattern) {
        MatchResult bMatchResult = SL.match(formulaPattern, believeFormulaPattern);
        ListOfMatchResults listOfMatchResults = myKBase.query(formulaPattern);
        if (listOfMatchResults == null && myKBase.isClosed(formulaPattern, listOfMatchResults)) {
            return new ListOfMatchResults();
        } else if (listOfMatchResults == null) {
            return null;
        } else if ((ire instanceof AnyNode && listOfMatchResults.size() >= 1) ||
                (ire instanceof SomeNode && listOfMatchResults.size() >= 1) ||
                (ire instanceof AllNode && myKBase.isClosed(formulaPattern, listOfMatchResults)) || 
                (ire instanceof IotaNode && listOfMatchResults.size() == 1 && myKBase.isClosed(formulaPattern, listOfMatchResults))) {
            ListOfTerm result = new ListOfTerm();
            for (int j = 0; j < listOfMatchResults.size(); j++) {
                    Term termResult = (Term)termPattern.getClone();
                    try {
                        for (int i=0; i<listOfNodes.size(); i++) {
                            if (termPattern instanceof MetaTermReferenceNode) {
                                termResult = ((MatchResult)listOfMatchResults.get(j)).getTerm(((VariableNode)listOfNodes.get(i)).lx_name());
                            } else {
                                if (((MatchResult)listOfMatchResults.get(j)).getTerm(((VariableNode)listOfNodes.get(i)).lx_name()) == null) {
                                    termResult = (Term)SL.instantiate(termResult, 
                                            ((VariableNode)listOfNodes.get(i)).lx_name(), 
                                            listOfNodes.get(i));
                                } else {
                                    termResult = (Term)SL.instantiate(termResult, 
                                            ((VariableNode)listOfNodes.get(i)).lx_name(), 
                                            ((MatchResult)listOfMatchResults.get(j)).getTerm(((VariableNode)listOfNodes.get(i)).lx_name()));
                                }
                            }
                        }
                        result.add(termResult);
                    } catch(Exception e) {e.printStackTrace();}
            }

            if (term instanceof MetaTermReferenceNode) {
                ListOfMatchResults listResult = new ListOfMatchResults();
                MatchResult match = null;
                if (ire instanceof IotaNode) {
                    match = SL.match(term, result.get(0));
                } else if (ire instanceof AnyNode) {
                    match = SL.match(term, result.get(0));
                } else {
                    match = SL.match(term, new TermSetNode(result));   
                }
                if (match != null) {
                    listResult.add(match);
                    return listResult;
                }
                return null;
            } else {
                if (ire instanceof IotaNode) {
                    if (result.get(0).equals(term)) {
                        return new ListOfMatchResults();
                    }
                } else if (ire instanceof AnyNode) {
                    if (result.contains(term)) {
                        return new ListOfMatchResults();
                    }
                } else {
                    if (term instanceof TermSetNode &&
                            ((TermSetNode)term).as_terms().size() == result.size() 
                             ) {
                        ListOfTerm list = ((TermSetNode)term).as_terms();
                        for(int i = 0; i < list.size(); i++) {
                            if (!result.contains(list.get(i))) {return null;}
                        }
                        return new ListOfMatchResults();
                    } 
                }
            }
        }
        return null;
    }


    /**
     * Returns a ListOfMatchResults corresponding to the answer to the query. If
     * the ??term is a meta variable, returns an empty list. 
     * If ??term is a constant, returns an empty list
     * if ??term does not equal the answer to the query on formula, and null if not.
     * @param formula the formula to be queried
     * @return a list of solutions.
     */
    private ListOfMatchResults notPatternProcess(Formula formula, ListOfNodes listOfNodes, Term termPattern, Formula formulaPattern) {
        IdentifyingExpression ide = ire;
        MatchResult bMatchResult = SL.match(formulaPattern, believeFormulaPattern);
        ListOfMatchResults listOfMatchResults = myKBase.query(formulaPattern);
        if (listOfMatchResults == null) return new ListOfMatchResults();
        if ((ire instanceof AnyNode && listOfMatchResults.size() >= 1) ||
                (ire instanceof SomeNode && listOfMatchResults.size() >= 1) ||
                (ire instanceof AllNode && myKBase.isClosed(formulaPattern, listOfMatchResults)) || 
                (ire instanceof IotaNode && listOfMatchResults.size() == 1 && myKBase.isClosed(formulaPattern, listOfMatchResults))) {
            ListOfTerm result = new ListOfTerm();
            for (int j = 0; j < listOfMatchResults.size(); j++) {
                    Term termResult = (Term)termPattern.getClone();
                    
                    try {
                        for (int i=0; i<listOfNodes.size(); i++) {
                            if (termPattern instanceof MetaTermReferenceNode) {
                                termResult = ((MatchResult)listOfMatchResults.get(j)).getTerm(((VariableNode)listOfNodes.get(i)).lx_name());
                            } else {
                                if (((MatchResult)listOfMatchResults.get(j)).getTerm(((VariableNode)listOfNodes.get(i)).lx_name()) == null) {
                                    termResult = (Term)SL.instantiate(termResult, 
                                            ((VariableNode)listOfNodes.get(i)).lx_name(), 
                                            listOfNodes.get(i));
                                } else {
                                    termResult = (Term)SL.instantiate(termResult, 
                                            ((VariableNode)listOfNodes.get(i)).lx_name(), 
                                            ((MatchResult)listOfMatchResults.get(j)).getTerm(((VariableNode)listOfNodes.get(i)).lx_name()));
                                }
                            }
                        }
                        result.add(termResult);
                    } catch(Exception e) {e.printStackTrace();}
            }
            if (term instanceof MetaTermReferenceNode) {
                if (ire instanceof IotaNode && !term.equals(result.get(0))) {
                    return new ListOfMatchResults();
                } else if (ire instanceof AnyNode) {
                    return new ListOfMatchResults();
                } else if (ire instanceof AllNode || ire instanceof SomeNode){
                    return new ListOfMatchResults();
                }
            } else {
                if (ire instanceof IotaNode && !term.equals(result.get(0))) {
                    return new ListOfMatchResults();
                } else if (ire instanceof AnyNode) {
                    if (!result.contains(term)) {
                        return new ListOfMatchResults();
                    }
                } else if (ire instanceof AllNode || ire instanceof SomeNode) {
                    TermSetNode node = new TermSetNode(result);
                    if (node.equals(term)) {
                        return null;
                    } else {
                        return new ListOfMatchResults();
                    }
                }
            }
        } 
        return null;
    }
    
    /**
     * Creates the formulaPattern and the TermPattern by changing into meta variables 
     * all the variables that appear in the term of the identifying expression. 
     */
    private void getPattern() {
        listOfNodes = new ListOfNodes();
        termPattern = null;
        formulaPattern = null;
        if (ire.as_term().childrenOfKind(VariableNode.class, listOfNodes)) {
            termPattern = (Term)SL.toPattern(ire.as_term(), (VariableNode)listOfNodes.get(0), ((VariableNode)listOfNodes.get(0)).lx_name());
            formulaPattern = (Formula)SL.toPattern(ire.as_formula(), (VariableNode)listOfNodes.get(0), ((VariableNode)listOfNodes.get(0)).lx_name());
            for (int i = 1; i < listOfNodes.size(); i++) {
                formulaPattern = (Formula)SL.toPattern(formulaPattern, (VariableNode)listOfNodes.get(i), ((VariableNode)listOfNodes.get(i)).lx_name());
                termPattern = (Term)SL.toPattern(termPattern, (VariableNode)listOfNodes.get(i), ((VariableNode)listOfNodes.get(i)).lx_name());
            }
        } else {
            formulaPattern = ire.as_formula();
            termPattern = ire.as_term();
        }
    }
    /**
     * By default, this method does nothing. 
     * @param formula an observed formula
     * @param set set of patterns. Each pattern corresponds to a kind a formula
     * which, if it is asserted in the base, triggers the observer that
     * observes the formula given in parameter.
     */
    public boolean getObserverTriggerPatterns(Formula formula, Set set) {
    	MatchResult applyResult = SL.match(pattern,formula);
        if (applyResult != null) {
        	try {
        		if (applyResult.getTerm("ire") instanceof IdentifyingExpression) {
        			ire = (IdentifyingExpression)applyResult.getTerm("ire");
        			term = applyResult.getTerm("term");
        		} else if (applyResult.getTerm("term") instanceof IdentifyingExpression) {
        			ire = (IdentifyingExpression)applyResult.getTerm("term");
        			term = applyResult.getTerm("ire");
        		} else {
        			return true;
        		}
        		getPattern();
				myKBase.getObserverTriggerPatterns(formulaPattern, set);
        		return false;
        	}
        	catch (WrongTypeException wte) {
        		wte.printStackTrace();
        	}
        }
        return true;
    }
}
