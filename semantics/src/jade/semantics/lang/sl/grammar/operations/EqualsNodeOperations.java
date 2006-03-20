package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.EqualsNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.Util;
 
public class EqualsNodeOperations
    extends FormulaNodeOperations
{
	public void simplify(Formula node) {
 
		Term left = ((EqualsNode)node).as_left_term().sm_simplified_term();
		Term right = ((EqualsNode)node).as_right_term().sm_simplified_term();
		
		if ( left.compare(right) == 1 ) {
			Term t = left;
			left = right;
			right = t;
		}

        if ( left instanceof AnyNode) {
            AnyNode any = (AnyNode)left;
            Term term = any.as_term();
            Formula formula = any.as_formula().sm_simplified_formula();
            ListOfNodes listOfNodes = new ListOfNodes();
            Formula formulaPattern = formula;
            Term termPattern = term;
            if (term.childrenOfKind(VariableNode.class, listOfNodes)) {
                formulaPattern = (Formula)SLPatternManip.toPattern(formula, listOfNodes, null);
                termPattern = (Term)SLPatternManip.toPattern(term, listOfNodes, null);
            } 
            try {
                Formula formulaPrim = formulaPattern;
                MatchResult termMatchResult = SLPatternManip.match(termPattern, right);
            	if (termMatchResult != null) { // added VL 14/03/2006
            		for (int j = 0; j < listOfNodes.size(); j++) {
            			formulaPrim = (Formula)SLPatternManip.instantiate(formulaPrim, 
            					((VariableNode)listOfNodes.get(j)).lx_name(), termMatchResult.getTerm(((VariableNode)listOfNodes.get(j)).lx_name()));
            		}
            		formulaPrim.getSimplifiedFormula();
            		node.sm_simplified_formula(formulaPrim.sm_simplified_formula()); // modified VL 14/03/2006
                    return;
            	} // added VL 14/03/2006
            }
            catch(Exception e) {e.printStackTrace();}
        } else if (left instanceof SomeNode && right instanceof TermSetNode) {
            Term term = ((SomeNode)left).as_term();
            Formula formula = ((SomeNode)left).as_formula().sm_simplified_formula();
            ListOfNodes listOfNodes = new ListOfNodes();
            Formula formulaPattern = formula;
            Term termPattern = term;
            if (term.childrenOfKind(VariableNode.class, listOfNodes)) {
                formulaPattern = (Formula)SLPatternManip.toPattern(formula, listOfNodes, null);
                termPattern = (Term)SLPatternManip.toPattern(term, listOfNodes, null);
            } 
            try {
                ListOfNodes result = new ListOfNodes();
                ListOfTerm list = ((TermSetNode)right).as_terms();
                for(int i = 0; i < list.size(); i++) {
                    Formula formulaPrim = formulaPattern;
                    MatchResult termMatchResult = SLPatternManip.match(termPattern, list.get(i));
                    if (termMatchResult != null) { // added VL 14/03/2006
                    	for (int j = 0; j < listOfNodes.size(); j++) {
                    		formulaPrim = (Formula)SLPatternManip.instantiate(formulaPrim, 
                    				((VariableNode)listOfNodes.get(j)).lx_name(), termMatchResult.getTerm(((VariableNode)listOfNodes.get(j)).lx_name()));
                    	}
                    	result.add(formulaPrim);
                    } 					// added VL 14/03/2006
                    else { 				// added VL 14/03/2006
                    	result = null; 	// added VL 14/03/2006
                    	break; 			// added VL 14/03/2006
                    } 					// added VL 14/03/2006
                }
                if (result != null) { // added VL 14/03/2006
                	Formula nodeResult = Util.buildAndNode(result);
                	if (nodeResult != null) {
                		nodeResult.getSimplifiedFormula();
                		node.sm_simplified_formula(nodeResult);
                		return;
                	}
                } // added VL 14/03/2006
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
		Formula equalsNode = new EqualsNode(left, right);
		equalsNode.sm_simplified_formula(equalsNode);
		node.sm_simplified_formula(equalsNode); 
    }
}
