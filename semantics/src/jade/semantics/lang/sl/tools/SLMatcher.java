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

package jade.semantics.lang.sl.tools;

import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.ByteConstantNode;
import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.EquivNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FormulaContentExpressionNode;
import jade.semantics.lang.sl.grammar.FunctionalTermNode;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingContentExpressionNode;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfParameter;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.ParameterNode;
import jade.semantics.lang.sl.grammar.PersistentGoalNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.PropositionSymbolNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.ResultNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Symbol;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSequenceNode;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.UncertaintyNode;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.grammar.VisitorBase;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This class implement a SL pattern matcher.
 */
public class SLMatcher extends VisitorBase {
	
	//============================================================
	//                      PUBLIC METHOD
	//============================================================
    /**
     * Try to match to SL expressions
     * @param expression1 a SL expression
     * @param expression2 another SL expression
     * @return null if the 2 expressions doesn't match, or a MatchResult otherwise
     */
    public MatchResult match(Node expression1, Node expression2) {
        if (match(expression1, expression2, 
        		  new MatchResult(), 
        		  new ListOfNodes(), new ListOfNodes(), new ListOfNodes(), new ListOfNodes())) {
        	try {
				_metaReferences.completeClosure();
			} catch (WrongTypeException e) {
				return null;
			}
            _metaReferences.instantiate();
            removeExpsAssignments(_metaReferences);
            return _metaReferences;
        } else {
            return null;
        }
    }

	//============================================================
	//                      PRIVATE FIELDS
	//============================================================
    Node _expression1;        
    Node _expression2;
        
    boolean _match;
        
    MatchResult _metaReferences;
        
    ListOfNodes _firstAndResidue;        
    ListOfNodes _secondAndResidue;        
    ListOfNodes _firstOrResidue;
    ListOfNodes _secondOrResidue;
        
                
	//============================================================
	//                      PRIVATE METHODS
	//============================================================
              
    /**
     * This method is used inside the recursive matching
     * process.
     * @param expression1
     * @param expression2
     * @param metaReferences
     * @param firstList
     * @param secondList
     * @param firstOrResidue
     * @param secondOrResidue
     * @return
     */
    private boolean match(Node expression1, 
    		              Node expression2,
    		              MatchResult metaReferences, 
    		              ListOfNodes firstList, 
    		              ListOfNodes secondList,
    		              ListOfNodes firstOrResidue, 
    		              ListOfNodes secondOrResidue)
    {
    	_match = false;
    	_metaReferences = metaReferences;    	
    	_expression1 = expression1;
    	_expression2 = expression2;
    	_firstAndResidue = firstList;
    	_secondAndResidue = secondList;
    	_firstOrResidue = firstOrResidue;
    	_secondOrResidue = secondOrResidue;
    	
    	// Keep the old metareference size to restore it if !_match
    	int size = _metaReferences.size();
    	
    	_expression1.accept(this);
    	
    	if ( !_match ) {
    		ListOfNodes metaRefs = _metaReferences.restore(size);
    		removeExpsAssignments(metaRefs);
    	}
    	
    	return _match;
    }
        
   /**
     * Returns true if the two lists given in parameter matches, false if 
     * not.
     * @param ref the reference list
     * @param other the other list
     * @param type 0 for an AndNode, 1 for an OrNode
     */
    private boolean analyze(ListOfNodes ref, ListOfNodes other, int type) {
    	for (int i = 0; i < ref.size(); i++) {
    		if (ref.get(i) instanceof MetaFormulaReferenceNode) {
    			if (i == (ref.size() -1)) {
    				if (type == 0) {
    					doPatternMatchingOnMetaReference((MetaFormulaReferenceNode)ref.get(i), createEndAndNode(other));
    				} else {
    					doPatternMatchingOnMetaReference((MetaFormulaReferenceNode)ref.get(i), createEndOrNode(other));
    				}
    				return _match;
    			} else {
    				for (int j = 0; j < other.size(); j++) {
    					doPatternMatchingOnMetaReference((MetaFormulaReferenceNode)ref.get(i), other.get(j));
    					if (_match) {
    						other.remove(other.get(j));
    						break;
    					}
    				}
    				if (!_match) return false;
    			}
    		} else {
    			boolean find = false;
    			for (int j = 0; j < other.size(); j++) {
     				find = new SLMatcher().match(ref.get(i),other.get(j),
     											 _metaReferences, 
     											 new ListOfNodes(),new ListOfNodes(),new ListOfNodes(),new ListOfNodes())
     					|| new SLMatcher().match(other.get(j),ref.get(i), 
     							                 _metaReferences, 
     							                 new ListOfNodes(),new ListOfNodes(),new ListOfNodes(),new ListOfNodes());
     				
     				if ( find ) {
    					other.remove(other.get(j));
    					break;
    				}
    			}
    			if (!find) {
    				if (type == 0) {
    					_secondAndResidue.removeAll();
    					_firstAndResidue.removeAll();
    				} else {
    					_secondOrResidue.removeAll();
    					_firstOrResidue.removeAll();
    				}
    				return false;
    			}
    		}
    	}
    	return (other.size() == 0);
    }
        
    /**
     * Creates an And Formula with the nodes of the list. If the list contains
     * only one node, this one is returned. The list given in parameter is
     * cleared. 
     * @param l a list of nodes
     * @return a formula that is an AndNode formula if the list size if at
     * least 2, or the node in the list if the size equals 1, or null if the
     * list is empty. 
     */
    private Formula createEndAndNode(ListOfNodes l) {
    	Formula solution = buildAndNode(l);
    	if (solution != null && solution instanceof AndNode) {
    		solution = sort(solution);
    	} 
    	l.removeAll();
    	return solution;
    }
        
    /**
     * Creates an Or Formula with the nodes of the list. If the list contains
     * only one node, this one is returned. The list given in parameter is
     * cleared. 
     * @param l a list of nodes
     * @return a formula that is an OrNode formula if the list size if at
     * least 2, or the node in the list if the size equals 1, or null if the
     * list is empty. 
     */
    private Formula createEndOrNode(ListOfNodes l) {
    	Formula solution = buildOrNode(l);
    	if (solution != null && solution instanceof OrNode) {
    		solution = sort(solution);
    	}
    	l.removeAll();
    	return solution;
    }                

    /**
     * Sorts the list given in parameter. The comparison between nodes is 
     * used to sort the list. 
     * @param l the list of nodes to be sorted
     */
    private void quickSort(ListOfNodes l) {
    	int length=l.size();
    	quickSort(l,0,length-1);
    }
        
    /**
     * Returns the index of the pivot in the given list.
     * @param l a list to be sorted
     * @param begin the beginning index
     * @param end the ending index
     * @return the index of the pivot in the given list.
     */
    private int partition(ListOfNodes l,int begin,int end) {
    	int compt=begin;
    	Node pivot=l.get(begin);
    	for(int i=begin+1;i<=end;i++) {
    		if (l.get(i).compare(pivot) <0 && !(l.get(i) instanceof MetaFormulaReferenceNode) && (l.get(i).childrenOfKind(MetaFormulaReferenceNode.class, new ListOfNodes()))
    			&& !(pivot instanceof MetaFormulaReferenceNode) && !(pivot.childrenOfKind(MetaFormulaReferenceNode.class, new ListOfNodes()))) {
    			exchange(l,compt,i);
    			compt++;
    		} else if (pivot.compare(l.get(i)) <0 && !(pivot instanceof MetaFormulaReferenceNode) && (pivot.childrenOfKind(MetaFormulaReferenceNode.class, new ListOfNodes()))
    				&& !(l.get(i) instanceof MetaFormulaReferenceNode) && !(l.get(i).childrenOfKind(MetaFormulaReferenceNode.class, new ListOfNodes()))) {
    			compt++;
    		}                
    		else if (l.get(i).compare(pivot)< 0 && !(l.get(i) instanceof MetaFormulaReferenceNode)) {
    			compt++;
    			exchange(l,compt,i);
    		} else if (pivot instanceof MetaFormulaReferenceNode && 
    				(!(l.get(i) instanceof MetaFormulaReferenceNode) )) {
    			compt++;
    			exchange(l,compt,i);
    		}
    	}
    	exchange(l,begin,compt);
    	return(compt);
    }
        
    /**
     * Permutes the node at index i and the node at index j in the list l.
     * @param l a list of nodes
     * @param i an index in the list
     * @param j an index in the list
     */
    private void exchange(ListOfNodes l,int i,int j) {
    	Node memory=l.get(i);
    	l.replace(i, l.get(j));
    	l.replace(j, memory);
    }
        
    /**
     * Sort the nodes of the given list using the compare method of each kind
     * of nodes.
     * @param l the list to be sorted
     * @param beg the beginning index in the list to sort
     * @param end the ending index in the list to sort
     */
    private void quickSort(ListOfNodes l,int beg,int end) {
    	if(beg<end) {
    		int pivot=partition(l,beg,end);
    		quickSort(l,beg,pivot-1);
    		quickSort(l,pivot+1,end);
    	}
    }
        
    /**
     * Extracts all the elements of a AndNode and puts them into the list
     * of nodes given in parameter.
     * @param n an AndNode 
     * @param l the resulting list of nodes
     */
    private void getList(AndNode n, ListOfNodes l) {
    	if (n.as_left_formula() instanceof AndNode) {
    		getList((AndNode)n.as_left_formula(), l);
    	} else {
    		l.add(n.as_left_formula());
    	}
    	if (n.as_right_formula() instanceof AndNode) {
    		getList((AndNode)n.as_right_formula(), l);
    	} else {
    		l.add(n.as_right_formula());
    	}
    }
   
    /**
     * Extracts all the elements of a OrNode and puts them into the list
     * of nodes given in parameter.
     * @param n an OrNode 
     * @param l the resulting list of nodes
     */
    private void getList(OrNode n, ListOfNodes l) {
    	if (n.as_left_formula() instanceof OrNode) {
    		getList((OrNode)n.as_left_formula(), l);
    	} else {
    		l.add(n.as_left_formula());
    	}
    	if (n.as_right_formula() instanceof OrNode) {
    		getList((OrNode)n.as_right_formula(), l);
    	} else {
    		l.add(n.as_right_formula());
    	}
    }
        
    /**
     * If the given formula is a OrNode or a AndNode, this method returns sorts
     * the formula and returns the sorted formula. If not, returns the same
     * formula.
     * @param node a formula to be sorted
     * @return a sorted formula.
     */
    private Formula sort(Formula node) {
    	if (node instanceof OrNode || node instanceof AndNode) {
    		ListOfNodes l = new ListOfNodes();
    		if (node instanceof AndNode) {
    			getList((AndNode)node, l);
    		} else {
    			getList((OrNode)node, l);
    		}
    		quickSort(l);
    		if (node instanceof AndNode) {
    			return buildAndNode(l);
    		} else {
    			return buildOrNode(l);
    		}
    	} else {
    		return node;
    	}
    }
        
    /**
     * Sort recursivly all the OrNode or AndNode of a formula.
     * @param node the formula to be sorted
     * @return a sorted formula
     */
    private Formula sortFormula(Formula node) {
    	Formula temp = sort(node);
    	if (temp instanceof OrNode) {
    		temp = new OrNode(sortFormula(((OrNode)temp).as_left_formula()),
    				sortFormula(((OrNode)temp).as_right_formula()));
    	} else if (node instanceof AndNode){
    		temp = new AndNode(sortFormula(((AndNode)temp).as_left_formula()), sortFormula(((AndNode)temp).as_right_formula()));
    	}
    	return temp;
    }

    /**
     * Tries to return a AndNode built with the Formulae of the given list. 
     * If the size of the list equals 1, it returns the only formula of the list.
     * If the size of the list equals 0 or if the list is null, returns null.
     * Else, it returns an AndFormula. 
     * @param l a list of formulae
     * @return a AndNode built with the Formulae of the given list, 
     * or a Formula, or null. 
     */
    private Formula buildAndNode(ListOfNodes l) {
    	Formula solution = null;
    	if (l != null) {
    		if (l.size() == 1) {
    			solution = (Formula)l.get(0);
    		} else if (l.size() >= 2) {
    			AndNode andNode = new AndNode((Formula)l.get(l.size()-2), (Formula)l.get(l.size()-1));
    			for (int i = l.size() - 3; i >= 0; i--) {
    				andNode = new AndNode((Formula)l.get(i), andNode);
    			}
    			solution = andNode;
    		}
    	}
    	return solution;
    }
        
    /**
     * Tries to return a OrNode built with the Formulae of the given list.
     * If the size of the list equals 1, it returns the only formula of the list.
     * If the size of the list equals 0 or if the list is null, returns null.
     * Else, it returns an OrFormula. 
     * @param l a list of formulae
     * @return a OrNode built with the Formulae of the given list, 
     * or a Formula, or null. 
     */
    private Formula buildOrNode(ListOfNodes l) {
    	Formula solution = null;
    	if (l != null) {
    		if (l.size() == 1) {
    			solution = (Formula)l.get(0);
    		} else if (l.size() >= 2) {
    			OrNode orNode = new OrNode((Formula)l.get(l.size()-2), (Formula)l.get(l.size()-1));
    			for (int i = l.size() - 3; i >= 0; i--) {
    				orNode = new OrNode((Formula)l.get(i), orNode);
    			}
    			solution = orNode;
    		}
    	}
    	return solution;
    }

    /**
     * This method remove all assigments in expressions.
     */
    private void removeExpsAssignments(ListOfNodes metaRefs)
    {
    	for (int i = 0; i < metaRefs.size(); i++) {
    		Node var = metaRefs.get(i);
    		try {
    			SL.set(_expression1, SL.getMetaReferenceName(var), null);
    			SL.set(_expression2, SL.getMetaReferenceName(var), null);
    		} catch (WrongTypeException e) {
    			e.printStackTrace();
    		}
    	}
    }
        
    /**
     * Returns a meta reference with the class and the same name from the previously 
     * stored meta references (_metaReferences), or null if no such 
     * meta reference exists.
     * @param metaReference
     * @return
     */
    private Node getMetaReference(Node metaReference)
    {
    	ListOfNodes foundRef = new ListOfNodes();
    	if (_metaReferences.find(metaReference.getClass(), "lx_name", SL.getMetaReferenceName(metaReference), foundRef, false)) {
    		return foundRef.get(0);
    	} else {
    		return null;
    	}
    }
        
    /**
     * @param expression1
     * @param expression2
     */
    private void doPatternMatchingOnChildren(Node expression1, Node expression2)
    {
    	if (expression1.getClass() == expression2.getClass()) {
    		Node[] nodes1 = expression1.children();
    		Node[] nodes2 = expression2.children();
    		_match = (nodes1.length == nodes2.length);
    		for (int i = 0; i < nodes1.length && _match; i++) {
    			_match = _match && matchExpressions(nodes1[i], nodes2[i]);
    		}
    	} else {
    		_match = false;
    	}
    }
        
    /**
     * @param expression1
     * @param expression2
     * @return
     */
    boolean matchExpressions(Node expression1, Node expression2)
    {
     	boolean result = new SLMatcher().match(expression1, expression2,
    								   _metaReferences,
    								   _firstAndResidue, _secondAndResidue, _firstOrResidue, _secondOrResidue)
    	              || new SLMatcher().match(expression2,expression1, 
    	    		                  _metaReferences, 
    	    		                  _secondAndResidue, _firstAndResidue, _secondOrResidue,  _firstOrResidue);
		return result;
    }
        
    /**
     * Try to find another reference with the same type and the same name.
     * If found then 
     *    If the two meta references match then assign the new one with the value of the other one.
     *    Else _match is set to false.
     * Else
     *    _match is set to true, the new meta reference value is exp, and the new meta reference is stored.
     * @param metaRef
     * @param exp
     */
    private void doPatternMatchingOnMetaReference(Node metaRef, Node exp)
    {
    	Node otherRef = getMetaReference(metaRef);
		Node otherValue;
    	if (otherRef != null) {
    		otherValue = SL.getMetaReferenceValue(otherRef);
    		_match = otherValue == exp || otherValue.equals(exp) || matchExpressions(otherValue, exp);
    		
    		if (_match) {
    			try {
    				SL.setMetaReferenceValue(metaRef, otherValue);
    			} catch (WrongTypeException e) {
    				e.printStackTrace();
    			}
    		}
    	} else {
    		_match = true;
    		_metaReferences.add(metaRef);
    		try {
    	   		assert( exp != null );
     	   		SL.setMetaReferenceValue(metaRef, exp);
    		} catch (WrongTypeException e) {
    			e.printStackTrace();
    		}
    	}
    }
        
	//============================================================
	//                 VISITOR IMPLEMENTATION
	//============================================================
    public void visitMetaFormulaReferenceNode(MetaFormulaReferenceNode node) {
    	if (_expression2 instanceof Formula) {
    		doPatternMatchingOnMetaReference(node, _expression2);
    	} else {
    		_match = false;
    	}
    }
        
    public void visitMetaSymbolReferenceNode(MetaSymbolReferenceNode node) {
    	if (_expression2 instanceof Symbol) {
    		doPatternMatchingOnMetaReference(node, _expression2);
    	} else {
    		_match = false;
    	}
    }
        
    public void visitMetaVariableReferenceNode(
    		MetaVariableReferenceNode node) {
    	if (_expression2 instanceof Variable) {
    		doPatternMatchingOnMetaReference(node, _expression2);
    	} else {
    		_match = false;
    	}
    }
        
    public void visitMetaTermReferenceNode(MetaTermReferenceNode node) {
    	if (_expression2 instanceof Term) {
    		doPatternMatchingOnMetaReference(node, _expression2);
    	} else {
    		_match = false;
    	}
    }
        
    public void visitMetaContentExpressionReferenceNode(
    		MetaContentExpressionReferenceNode node) {
    	if (_expression2 instanceof ContentExpression) {
    		doPatternMatchingOnMetaReference(node, _expression2);
    	} else {
    		_match = false;
    	}
    }
        
    public void visitVariableNode(VariableNode node) {
    	_match = ((_expression2 instanceof VariableNode) && ((VariableNode) _expression2)
    			.lx_name().equals(node.lx_name()));
    }
        
    public void visitIntegerConstantNode(IntegerConstantNode node) {
    	_match = ((_expression2 instanceof IntegerConstantNode) && ((IntegerConstantNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitRealConstantNode(RealConstantNode node) {
    	_match = ((_expression2 instanceof RealConstantNode) && ((RealConstantNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitStringConstantNode(StringConstantNode node) {
    	_match = ((_expression2 instanceof StringConstantNode) && ((StringConstantNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitWordConstantNode(WordConstantNode node) {
    	_match = ((_expression2 instanceof WordConstantNode) && ((WordConstantNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitByteConstantNode(ByteConstantNode node) {
    	_match = ((_expression2 instanceof ByteConstantNode) && ((ByteConstantNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitDateTimeConstantNode(DateTimeConstantNode node) {
    	_match = ((_expression2 instanceof DateTimeConstantNode) && ((DateTimeConstantNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitSymbolNode(SymbolNode node) {
    	_match = ((_expression2 instanceof SymbolNode) && ((SymbolNode) _expression2)
    			.lx_value().equals(node.lx_value()));
    }
        
    public void visitParameterNode(ParameterNode node) {
    	if (_expression2 instanceof ParameterNode) {
    		_match = (node.lx_name().equals(
    				((ParameterNode) _expression2).lx_name()) && matchExpressions(
    						node.as_value(), ((ParameterNode) _expression2).as_value()));
    	} else {
    		_match = false;
    	}
    }
        
    public void visitTrueNode(TrueNode node) {
    	_match = (_expression2 instanceof TrueNode);
    }
        
    public void visitFalseNode(FalseNode node) {
    	_match = (_expression2 instanceof FalseNode);
    }
        
    public void visitListOfContentExpression(ListOfContentExpression node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitListOfTerm(ListOfTerm node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitListOfParameter(ListOfParameter node) {
    	if (node.getClass() == _expression2.getClass()) {
    		ListOfParameter node2 = (ListOfParameter)_expression2;
    		int i = 0, j = 0;
    		_match = true;
    		
    		while (i < node.size() && j < node2.size()) {
    			_match = matchExpressions(node.element(i), node2.element(j));
    			if ( _match ) {i++; j++;}
    			else if (((ParameterNode)node.element(i)).lx_optional().booleanValue()) {i++;}
    			else if (((ParameterNode)node2.element(j)).lx_optional().booleanValue()) {j++;}
    			else {_match = false; break;}
    		}
    		// i == node.size() || j == node2.size()
                
    		while ( _match && i < node.size() ) {
    			_match = _match && ((ParameterNode)node.element(i++)).lx_optional().booleanValue();
    		}
    		while ( _match && j < node2.size() ) {
    			_match = _match && ((ParameterNode)node2.element(j++)).lx_optional().booleanValue();
    		}
    	}
    }
        
    public void visitContentNode(ContentNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
    
    public void visitActionContentExpressionNode(
    		ActionContentExpressionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitFormulaContentExpressionNode(
    		FormulaContentExpressionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitIdentifyingContentExpressionNode(
    		IdentifyingContentExpressionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitActionExpressionNode(ActionExpressionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitAlternativeActionExpressionNode(
    		AlternativeActionExpressionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitSequenceActionExpressionNode(
    		SequenceActionExpressionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
    
    public void visitPropositionSymbolNode(PropositionSymbolNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitResultNode(ResultNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitPredicateNode(PredicateNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitEqualsNode(EqualsNode node) {
    	if (node.getClass() == _expression2.getClass()) {
    		int size = _metaReferences.size();
    		boolean first = (matchExpressions(node.as_left_term(), ((EqualsNode)_expression2).as_left_term()) &&
    				         matchExpressions(node.as_right_term(), ((EqualsNode)_expression2).as_right_term()));
    		if (!first) {
    			ListOfNodes mrefs = _metaReferences.restore(size);
    			removeExpsAssignments(mrefs);
    			boolean second = (matchExpressions(node.as_right_term(), ((EqualsNode)_expression2).as_left_term()) &&
    					          matchExpressions(node.as_left_term(), ((EqualsNode)_expression2).as_right_term()));
    			_match = second;
    		} else {
    			_match = true;
    		}		
    	} else {
    		_match = false;
    	}
    }
        
    public void visitDoneNode(DoneNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitFeasibleNode(FeasibleNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitNotNode(NotNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitAndNode(AndNode node) {
    	if (node.getClass() == _expression2.getClass()) {
    		AndNode firstPart = (AndNode)sort((AndNode) node);
    		AndNode secondPart = (AndNode)sort((AndNode)_expression2);
    		if (firstPart.as_right_formula() instanceof AndNode && secondPart.as_right_formula() instanceof AndNode) {
    			if (matchExpressions(firstPart.as_left_formula(), secondPart.as_left_formula())) {
    				_match = matchExpressions(firstPart.as_right_formula(), secondPart.as_right_formula());
    			} else {
    				_firstAndResidue.add(firstPart.as_left_formula());
    				_secondAndResidue.add(secondPart.as_left_formula());
    				_match = matchExpressions(firstPart.as_right_formula(), secondPart.as_right_formula());
    			}
    		} else {
    			if (firstPart.as_right_formula() instanceof AndNode) {
    				_secondAndResidue.add(secondPart.as_left_formula());
    				_secondAndResidue.add(secondPart.as_right_formula());
    				getList(firstPart, _firstAndResidue);
    				_match = analyze(_secondAndResidue, _firstAndResidue, 0);
    			} else {
    				_firstAndResidue.add(firstPart.as_left_formula());
    				_firstAndResidue.add(firstPart.as_right_formula());
    				getList(secondPart, _secondAndResidue);
    				_match = analyze(_firstAndResidue, _secondAndResidue, 0);
    			}
    		}
    	} else {
    		if (_expression2 instanceof MetaFormulaReferenceNode) {
    			doPatternMatchingOnMetaReference((MetaFormulaReferenceNode)_expression2,node);
    		} else {
    			_match = false;
    		}
    	}
    }
        
    public void visitOrNode(OrNode node) {
    	if (node.getClass() == _expression2.getClass()) {
    		OrNode firstPart = (OrNode)sort((OrNode) node);
    		OrNode secondPart = (OrNode)sort((OrNode)_expression2);
    		if (firstPart.as_right_formula() instanceof OrNode && secondPart.as_right_formula() instanceof OrNode) {
    			if (matchExpressions(firstPart.as_left_formula(), secondPart.as_left_formula())) {
    				_match = matchExpressions(firstPart.as_right_formula(), secondPart.as_right_formula());
    			} else {
    				_firstOrResidue.add(firstPart.as_left_formula());
    				_secondOrResidue.add(secondPart.as_left_formula());
    				_match = matchExpressions(firstPart.as_right_formula(), secondPart.as_right_formula());
    			}
    		} else {
    			if (firstPart.as_right_formula() instanceof OrNode) {
    				_secondOrResidue.add(secondPart.as_left_formula());
    				_secondOrResidue.add(secondPart.as_right_formula());
    				getList(firstPart, _firstOrResidue);
    				_match = analyze(_secondOrResidue, _firstOrResidue, 1);
    			} else {
    				_firstOrResidue.add(firstPart.as_left_formula());
    				_firstOrResidue.add(firstPart.as_right_formula());
    				getList(secondPart, _secondOrResidue);
    				_match = analyze(_firstOrResidue, _secondOrResidue, 1);
    			}
    		}
    	} else {
    		if (_expression2 instanceof MetaFormulaReferenceNode) {
    			doPatternMatchingOnMetaReference((MetaFormulaReferenceNode)_expression2,node);
    		} else {
    			_match = false;
    		}
    	}
    }
        
    public void visitImpliesNode(ImpliesNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitEquivNode(EquivNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitForallNode(ForallNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitExistsNode(ExistsNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitBelieveNode(BelieveNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitUncertaintyNode(UncertaintyNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitIntentionNode(IntentionNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitPersistentGoalNode(PersistentGoalNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitTermSetNode(TermSetNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitTermSequenceNode(TermSequenceNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitFunctionalTermNode(FunctionalTermNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitFunctionalTermParamNode(FunctionalTermParamNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitAnyNode(AnyNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitSomeNode(SomeNode node) 
    {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitIotaNode(IotaNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
        
    public void visitAllNode(AllNode node) {
    	doPatternMatchingOnChildren(node, _expression2);
    }
}