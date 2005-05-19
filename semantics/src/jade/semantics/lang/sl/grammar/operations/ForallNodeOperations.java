package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
 
public class ForallNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
    
		Variable x = ((ForallNode)node).as_variable();
		Formula formula = (Formula)((ForallNode)node).as_formula().sm_simplified_formula();
		if ( formula instanceof AndNode ) {
			node.sm_simplified_formula((new AndNode(new ForallNode(x, ((AndNode)formula).as_left_formula()),
							new ForallNode(x, ((AndNode)formula).as_right_formula()))).getSimplifiedFormula());
		}
		else if ( !formula.isAFreeVariable(x) ) {
			node.sm_simplified_formula(formula);
		}
		else if ( formula instanceof BelieveNode ) {
			node.sm_simplified_formula((new BelieveNode(((BelieveNode)formula).as_agent(), 
					new ForallNode(x, ((BelieveNode)formula).as_formula()))).getSimplifiedFormula());
		}
		else if ( formula instanceof DoneNode ) {
			node.sm_simplified_formula((new DoneNode(((DoneNode)formula).as_expression(),
					new ForallNode(x, ((DoneNode)formula).as_formula()))).getSimplifiedFormula());
		}
		else if ( formula instanceof FeasibleNode ) {
			node.sm_simplified_formula((new FeasibleNode(((FeasibleNode)formula).as_expression(),
					new ForallNode(x, ((FeasibleNode)formula).as_formula()))).getSimplifiedFormula());
		}
		else {
			ForallNode forallNode = new ForallNode(x, formula);
			forallNode.sm_simplified_formula(forallNode);
			node.sm_simplified_formula(forallNode);
		}
    }

    public boolean isMentalAttitude(Formula node, Term term)
    {
	return ((ForallNode)node).as_formula().isMentalAttitude(term);
    }

    public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true)
    {
	return (new ForallNode(((ForallNode)node).as_variable(), 
			       ((ForallNode)node).as_formula().getDoubleMirror(i,j,default_result_is_true))).getSimplifiedFormula();
    }

    public boolean isSubsumedBy(Formula node, Formula formula)
    {
	// tests if (forall ??A ??PHI) is subsumed by (forall ??A ??PSI)
	if ( formula instanceof ForallNode ) {
	    ForallNode n = (ForallNode)node;
	    ForallNode f = (ForallNode)formula;
	    ForallNode f2 = (ForallNode)f.getVariablesSubstitution(f.as_variable(), n.as_variable());
	    return n.as_formula().isSubsumedBy(f2.as_formula());
	}
	else {
	    return super.isSubsumedBy(node, formula);
	}
    }
}
