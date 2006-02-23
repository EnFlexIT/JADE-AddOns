package jade.semantics.lang.sl.tools;

import jade.semantics.lang.sl.grammar.ContentExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaContentExpressionReferenceNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaSymbolReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Symbol;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

/**
 * This class holds the result of a matching operation.
 */
public class MatchResult extends ListOfNodes {
	/**
	 * Creates an empty matching result.
	 */
	public MatchResult() {
	}

	/**
	 * Return the formula assigned to the named meta variable
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the formula which is the value of the meta variable named
	 *         with the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no formula is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 * @exception WrongTypeException
	 *                Thrown if the value of the variable is not a formula
	 */
	public Formula getFormula(String name) throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		Formula result = null;
		if (find(MetaFormulaReferenceNode.class, "lx_name", nameOf(name), metaReferences, true)) {
			result = ((MetaFormulaReferenceNode) metaReferences
					.getFirst()).sm_value();
//			if (result instanceof MetaFormulaReferenceNode &&
//					!((MetaFormulaReferenceNode)result).lx_name().equals(name) &&
//					((MetaFormulaReferenceNode)result).sm_value() == null) {
//				Formula result2 = getFormula(((MetaFormulaReferenceNode)result).lx_name());
//				if (result2 != null) {
//					result = result2;
//				}
//			}
		}
		return result;
	}

	/**
	 * Return the formula assigned to the named meta variable
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the formula which is the value of the meta variable named
	 *         with the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no formula is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 */
	public Formula formula(String name) {
		try {
			return getFormula(name);
		}
		catch(WrongTypeException wte) {wte.printStackTrace();}
		return null;
	}
	
	/**
	 * Return the term assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the term which is the value of the meta variable named with
	 *         the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no term is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 * @exception WrongTypeException
	 *                Thrown if the value of the variable is not a term.
	 */
	public Term getTerm(String name) throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		Term result = null;
		if (find(MetaTermReferenceNode.class, "lx_name", nameOf(name), metaReferences, true)) {
			result = ((MetaTermReferenceNode) metaReferences.getFirst())
			.sm_value();
//			if (result instanceof MetaTermReferenceNode &&
//					!((MetaTermReferenceNode)result).lx_name().equals(name) &&
//					((MetaTermReferenceNode)result).sm_value() == null) {
//				Term result2 = getTerm(((MetaTermReferenceNode)result).lx_name());
//				if (result2 != null) {
//					result = result2;
//				}
//			}
		} 
		else {
			result = getVariable(name);
		}
		return result;
	}

	/**
	 * Return the term assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the term which is the value of the meta variable named with
	 *         the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no term is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 */
	public Term term(String name) {
		try {
			return getTerm(name);
		}
		catch(WrongTypeException wte) {wte.printStackTrace();}
		return null;
	}
	
	/**
	 * Return the variable assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the variable which is the value of the meta variable named
	 *         with the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no variable is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 * @exception WrongTypeException
	 *                Thrown if the value of the variable is not a variable.
	 */
	public Variable getVariable(String name) throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		Variable result = null;
		if (find(MetaVariableReferenceNode.class, "lx_name", nameOf(name), metaReferences, true)) {
			result = ((MetaVariableReferenceNode) metaReferences
					.getFirst()).sm_value();
//			if (result instanceof MetaVariableReferenceNode &&
//					!((MetaVariableReferenceNode)result).lx_name().equals(name) &&
//					((MetaVariableReferenceNode)result).sm_value() == null) {
//				Variable result2 = getVariable(((MetaVariableReferenceNode)result).lx_name());
//				if (result2 != null) {
//					result = result2;
//				}
//			}
		}
		return result;
	}

	/**
	 * Return the variable assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the variable which is the value of the meta variable named
	 *         with the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no variable is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 */
	public Variable variable(String name) {
		try {
			return getVariable(name);
		}
		catch(WrongTypeException wte) {wte.printStackTrace();}
		return null;
	}
	
	/**
	 * Return the symbol assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the symbol which is the value of the meta variable named with
	 *         the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no symbol is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 * @exception WrongTypeException
	 *                Thrown if the value of the variable is not a symbol.
	 */
	public Symbol getSymbol(String name) throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		Symbol result = null;
		if (find(MetaSymbolReferenceNode.class, "lx_name", nameOf(name), metaReferences, true)) {
			result = ((MetaSymbolReferenceNode) metaReferences
					.getFirst()).sm_value();
//			if (result instanceof MetaSymbolReferenceNode &&
//					!((MetaSymbolReferenceNode)result).lx_name().equals(name) &&
//					((MetaSymbolReferenceNode)result).sm_value() == null) {
//				Symbol result2 = getSymbol(((MetaSymbolReferenceNode)result).lx_name());
//				if (result2 != null) {
//					result = result2;
//				}
//			}
		}
		return result;
	}

	/**
	 * Return the symbol assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the symbol which is the value of the meta variable named with
	 *         the given <b><code>name</code></b>, or <b><code>null</code></b>
	 *         if no symbol is assigned to this meta variable, meaning the
	 *         last matching operation has failed, or no matching operation
	 *         has been performed on this pattern.
	 */
	public Symbol symbol(String name) {
		try {
			return getSymbol(name);
		}
		catch(WrongTypeException wte) {wte.printStackTrace();}
		return null;
	}
	
	/**
	 * Return the content expression assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the content expression which is the value of the meta
	 *         variable named with the given <b><code>name</code></b>,
	 *         or <b><code>null</code></b> if no content expression is
	 *         assigned to this meta variable, meaning the last matching
	 *         operation has failed, or no matching operation has been
	 *         performed on this pattern.
	 * @exception WrongTypeException
	 *                Thrown if the value of the variable is not a content
	 *                expression.
	 */
	public ContentExpression getContentExpression(String name)
			throws WrongTypeException {
		ListOfNodes metaReferences = new ListOfNodes();
		ContentExpression result = null;
		if (find(MetaContentExpressionReferenceNode.class, "lx_name", nameOf(name), metaReferences, true)) {
			result = ((MetaContentExpressionReferenceNode) metaReferences
					.getFirst()).sm_value();
//			if (result instanceof MetaContentExpressionReferenceNode &&
//					!((MetaContentExpressionReferenceNode)result).lx_name().equals(name) &&
//					((MetaContentExpressionReferenceNode)result).sm_value() == null) {
//				ContentExpression result2 = getContentExpression(((MetaContentExpressionReferenceNode)result).lx_name());
//				if (result2 != null) {
//					result = result2;
//				}
//			}
		}
		return result;
	}

	/**
	 * Return the content expression assigned to the named meta variable.
	 * 
	 * @param name
	 *            the name of the meta variable to get its value
	 * @return the content expression which is the value of the meta
	 *         variable named with the given <b><code>name</code></b>,
	 *         or <b><code>null</code></b> if no content expression is
	 *         assigned to this meta variable, meaning the last matching
	 *         operation has failed, or no matching operation has been
	 *         performed on this pattern.
	 */
	public ContentExpression contentExpression(String name) {
		try {
			return getContentExpression(name);
		}
		catch(WrongTypeException wte) {wte.printStackTrace();}
		return null;
	}
	
	/**
	 * This method return a display of the matching result.
	 */
	public String toString() {
		String result = "";
		for (int i = 0; i < size(); i++) {
			Node var = get(i);
			result += (i == 0 ? "??"+SLPatternManip.getMetaReferenceName(var) + " = " 
						      : "\n??"+SLPatternManip.getMetaReferenceName(var) + " = ");
			result += SLPatternManip.getMetaReferenceValue(var).toString();
		}
		return result;
	}
	
	/**
	 * 
	 * @param object The match result object to compare with
	 * @return true is this match result object equals the one given as an argument. "Equal" means that
	 * the 2 objects holds the same meta variables (name and value) regardless their rank in the list.  
	 */
	public boolean equals(Object object)
	{
		boolean isEqual = object instanceof MatchResult && ((MatchResult)object).size() == size();
		if ( isEqual && size() != 0 ) {
			ListOfNodes l1 = new ListOfNodes(children());
			ListOfNodes l2 = new ListOfNodes(((MatchResult)object).children());
			do {
				Node n1 = l1.getFirst();
				ListOfNodes lo = new ListOfNodes();
				isEqual = l2.find(n1.getClass(), "lx_name", SLPatternManip.getMetaReferenceName(n1), lo, true);
				if ( isEqual ){
					Node n2 = lo.getFirst();
					isEqual = SLPatternManip.getMetaReferenceValue(n1).equals(SLPatternManip.getMetaReferenceValue(n2));
					l1.remove(n1);
					l2.remove(n2);
				}
			} while (isEqual && l1.size() > 0);
		}
		return isEqual;
	}
	
	/**
	 * 
	 * @param other the other match result to compute the intersection between
	 * @return the intersection between this match result and the one given as an argument or null if
	 *         the 2 match results are incompatible, i.e., they hold the same variable with different 
	 *         values.
	 */
	public MatchResult intersect(MatchResult other)
	{
		MatchResult result = new MatchResult();
		for (int i = 0; i < size(); i++) {
			Node m = get(i);
			ListOfNodes lo = new ListOfNodes();
			if ( other.find(m.getClass(), "lx_name", SLPatternManip.getMetaReferenceName(m), lo, true) ) {
				if ( SLPatternManip.getMetaReferenceValue(m).equals(SLPatternManip.getMetaReferenceValue(lo.getFirst())) ) {
					result.add(m.getClone());
				}
				else {
					// the 2 match results are incompatible
					return null;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param other the other match result to join with
	 * @return the union between this match result and the one given as an argument or null if
	 *         the 2 match results are incompatible, i.e., they hold the same variable with different 
	 *         values.
	 */
	public MatchResult join(MatchResult other)
	{
		MatchResult result = intersect(other);
		if ( result != null ) {
			for (int i = 0; i < size(); i++) {
				Node m = get(i);
				ListOfNodes lo = new ListOfNodes();
//				if ( !result.find(m.getClass(), "lx_name", SLPatternManip.getMetaReferenceName(m), lo, true) ) {
				if ( !result.contains(m) ) {
					result.add(m.getClone());
				}
			}
			for (int i = 0; i < other.size(); i++) {
				Node m = other.get(i);
				ListOfNodes lo = new ListOfNodes();
//				if ( !result.find(m.getClass(), "lx_name", SLPatternManip.getMetaReferenceName(m), lo, true) ) {
				if ( !result.contains(m) ) {
					result.add(m.getClone());
				}
			}
		}
		return result;
	}

	/**
    * This method return a clone of the list.
    * @return a new recreated graph.
    */
    public Node getClone()
    {
        Node clone = new MatchResult();
        clone.copyValueOf(this);
        return clone;
    }	
	
    public static Class[] META_REFERENCE_CLASSES = new Class[] {MetaContentExpressionReferenceNode.class,
        MetaFormulaReferenceNode.class, 
        MetaSymbolReferenceNode.class, 
        MetaTermReferenceNode.class, 
        MetaVariableReferenceNode.class};
    
    /**
     * Assigns the value of all occurrences of the meta-reference meta within exp
     * with the value of meta 
     */
    public void completeClosure() throws WrongTypeException {
        ListOfNodes metaReferences = new ListOfNodes();
                
        for (int i=0; i<size(); i++) {
        	Node value = SLPatternManip.getMetaReferenceValue(get(i));
        	if (value!=null) {
        		value.childrenOfKind(META_REFERENCE_CLASSES, metaReferences);
        	}
        }
        for (int i=0; i<metaReferences.size(); i++) {
        	if (!contains(metaReferences.get(i))) {
        		add(metaReferences.get(i));
        	}
        }
        for (int i=0; i<metaReferences.size(); i++) {
        	Node metaToAssign = metaReferences.get(i);
        	if (SLPatternManip.getMetaReferenceValue(metaToAssign) == null) {
        		ListOfNodes result = new ListOfNodes();
        		find(META_REFERENCE_CLASSES, "lx_name", SLPatternManip.getMetaReferenceName(metaToAssign), result, false);
				SLPatternManip.setMetaReferenceValue(metaToAssign, SLPatternManip.getMetaReferenceValue(result.get(0)));
        	}
        }
    }

	 
    /**
     * This method returns true if a MetaVariable has the given varName in the 
     * given MatchResult and if it succeeds in giving it the given value.  
     * @param varName the name of a metavaraible
     * @param value the value of the metavariable
     * @return true if a MetaVariable has the given varName in the 
     * given MatchResult and if it succeeds in giving it the given value, false
     * if not.
     */
	public boolean set(String varName, Node value) {
        try {
			for (int i = 0; i < size(); i++) {
				Node meta = (Node)get(i);
				if ( SLPatternManip.getMetaReferenceName(meta).equals(varName)) {
					// The meta reference to be assigned has been found.
					Node metaValue = SLPatternManip.getMetaReferenceValue(meta);
					if ( metaValue == null ) {
						SLPatternManip.setMetaReferenceValue(meta, value);
						completeClosure();
						return true;
					}
					else {
						// Assuming the meta variable already has a value
						MatchResult matchValue = SLPatternManip.match(metaValue, value);
						if ( matchValue == null ) {
							// The two values are not compatible
							return false;
						}
						else {
							for (int j=0; j<matchValue.size(); j++) {
								boolean toAdd = true;
								for (int k=0; k<size(); k++) {
									if (get(k).equals(matchValue.get(j))) {
										SLPatternManip.setMetaReferenceValue(get(k), SLPatternManip.getMetaReferenceValue(matchValue.get(j)));
										toAdd = false;
										break;
									}
								}
								if (toAdd) {
									add(matchValue.get(j));
								}
							}
							completeClosure();
							return true;
						}
					}
                }
            }
        } catch (SLPatternManip.WrongTypeException wte) {
            wte.printStackTrace();
        }
        return false;
    }

	// ===============================================
	// Package private implementation
	// ===============================================
	String nameOf(String name) {
		return name.startsWith("??") ?
				name.substring(2) :
					name;
	}
	
	void instantiate() {
		for (int i = 0; i < size(); i++) {
			replace(i, get(i).getClone());
			if (SLPatternManip.getMetaReferenceValue(get(i)) != null) {
				try {
					SLPatternManip.setMetaReferenceValue(get(i), SLPatternManip.instantiate(SLPatternManip.getMetaReferenceValue(get(i))));
				} catch (WrongTypeException e) {
					e.printStackTrace();
				}
			}
		}
	}

}