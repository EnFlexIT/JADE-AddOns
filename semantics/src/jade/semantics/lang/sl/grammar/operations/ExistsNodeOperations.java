package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.ExistsNode;
import jade.semantics.lang.sl.grammar.FeasibleNode;
import jade.semantics.lang.sl.grammar.ForallNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.QuantifiedFormula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.SLPatternManip;

public class ExistsNodeOperations extends FormulaNodeOperations {
	
	public void simplify(Formula node) {	
        Variable x = ((ExistsNode) node).as_variable();
        Formula formula = (Formula)((ExistsNode) node).as_formula().sm_simplified_formula();
        if (formula instanceof OrNode) {
            node.sm_simplified_formula((new OrNode(new ExistsNode(x, ((OrNode) formula).as_left_formula()), 
					                               new ExistsNode(x, ((OrNode) formula).as_right_formula())))
					                               .getSimplifiedFormula());
        }
        else if (!formula.isAFreeVariable(x)) {
			node.sm_simplified_formula(formula);
        }
        else if (formula instanceof NotNode && ((NotNode)formula).as_formula() instanceof BelieveNode) {
        	Formula beliefFormula = ((BelieveNode)((NotNode)formula).as_formula()).as_formula();
        	Term beliefAgent = ((BelieveNode)((NotNode)formula).as_formula()).as_agent();
        	node.sm_simplified_formula((new NotNode(new BelieveNode(beliefAgent,
        															new ForallNode(x,
        																		   beliefFormula)))).getSimplifiedFormula());
        }
        else if (formula instanceof DoneNode && ((DoneNode)formula).as_formula().isAFreeVariable(x)) {
			node.sm_simplified_formula((new DoneNode(((DoneNode) formula).as_action(), 
												     new ExistsNode(x, ((DoneNode) formula).as_formula())))
												     .getSimplifiedFormula());
        }
        else if (formula instanceof FeasibleNode && ((FeasibleNode)formula).as_formula().isAFreeVariable(x)) {
			node.sm_simplified_formula((new FeasibleNode(((FeasibleNode) formula).as_action(), 
					                                     new ExistsNode(x,((FeasibleNode) formula).as_formula())))
					                                     .getSimplifiedFormula());
        }
        else {
			ExistsNode existsNode = new ExistsNode(x, formula);
			existsNode.sm_simplified_formula(existsNode);
			node.sm_simplified_formula(existsNode);
        }
    }

    public boolean isMentalAttitude(Formula node, Term term) {
        return ((ExistsNode) node).as_formula().isMentalAttitude(term);
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (super.isSubsumedBy(node, formula)) {
            return true;
        }
        // tests if (exists ??X ??PHI) is subsumed by (exists ??Y ??PSI)
        else if (formula instanceof QuantifiedFormula) {
            QuantifiedFormula n = (QuantifiedFormula) node;
            QuantifiedFormula f = (QuantifiedFormula) formula;
            QuantifiedFormula f2 = (QuantifiedFormula) f.getVariablesSubstitution(f.as_variable(), n.as_variable());
            return n.as_formula().isSubsumedBy(f2.as_formula());
        }
        // tests if (exists ??X ??PHI) is subsumed by PSI[??X/o]
        else {
            Formula f = (Formula) SLPatternManip.toPattern(((ExistsNode) node).as_formula(), ((ExistsNode) node)
                    .as_variable());
            return SLPatternManip.match(f, formula) != null;
        }
    }

    public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true) {
        return (new ExistsNode(((ExistsNode) node).as_variable(), ((ExistsNode) node).as_formula().getDoubleMirror(i,
                j, default_result_is_true))).getSimplifiedFormula();
    }
    

}
