package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.ListOfVariable;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;

public class FormulaNodeOperations extends DefaultNodeOperations implements Formula.Operations {
	
    public void initNode(Node node)
	{		
		((Formula)node).sm_simplified_formula(null);
	}
	 
	public void simplify(Formula node)
	{
		node.sm_simplified_formula(node);
	}
	
    public Formula getSimplifiedFormula(Formula node) {
		doSimplifyNode(node);
		return node.sm_simplified_formula();
    }

    public boolean isMentalAttitude(Formula node, Term term) {
        return false;
    }
    
    public Formula isBeliefFrom(Formula node, Term agent) {
        return null;
    }

    public boolean isSubsumedBy(Formula node, Formula formula) {
        if (formula instanceof FalseNode) {
            return true;
        }
        else if (formula instanceof IntentionNode) {
            Term i_agent = ((IntentionNode) formula).as_agent();
            Formula i_form = ((IntentionNode) formula).as_formula();
            return node.isSubsumedBy(new BelieveNode(i_agent, new NotNode(i_form)).getSimplifiedFormula());
        }
        else if (formula instanceof AndNode) {
            return (node.isSubsumedBy(((AndNode) formula).as_left_formula()) || (node.isSubsumedBy(((AndNode) formula)
                    .as_right_formula())));
        }
        else if (formula instanceof OrNode) {
            return (node.isSubsumedBy(((OrNode) formula).as_left_formula()) && (node.isSubsumedBy(((OrNode) formula)
                    .as_right_formula())));
        }
        else {
 //           return node.equals(formula.getVariablesSubstitutionAsIn(node));
            return node.equals(formula);
		}
    }

    public boolean isConsistentWith(Formula node, Formula formula) {
        return !((new AndNode(node, formula)).getSimplifiedFormula().equals(new FalseNode()));
    }

    public Formula getDoubleMirror(Formula node, Term i, Term j, boolean default_result_is_true) {
        if (default_result_is_true) {
            return new TrueNode();
        }
        else {
            return new FalseNode();
        }
    }

    public boolean isAFreeVariable(Formula node, Variable x) {
        ListOfNodes variables = new ListOfNodes();
        if (x instanceof VariableNode && node.find(VariableNode.class, "lx_name", x.lx_name(), variables, false)) {
            return true;
        }
        else {
            return false;
        }
    }

    public Formula getVariablesSubstitution(Formula node, Variable x, Variable y) {
        Formula result = (Formula) node.getClone();
        if (x instanceof VariableNode && y instanceof VariableNode) {
            ListOfVariable rvars = new ListOfVariable();
            result.find(VariableNode.class, "lx_name", x.lx_name(), rvars, true);
            for (int i = 0; i < rvars.size(); i++) {
                if (rvars.get(i) instanceof VariableNode) {
                    rvars.element(i).lx_name(y.lx_name());
                }
            }
        }
        return result;
    }

    public Formula getVariablesSubstitution(Formula node, ListOfVariable vars) {
        Formula result = (Formula) node.getClone();
        ListOfVariable rvars = new ListOfVariable();
        result.childrenOfKind(VariableNode.class, rvars);
        for (int i = 0; i < rvars.size() && i < vars.size(); i++) {
            if (rvars.get(i) instanceof VariableNode && vars.get(i) instanceof VariableNode) {
                rvars.element(i).lx_name(vars.element(i).lx_name());
            }
        }
        return result;
    }

    public Formula getVariablesSubstitutionAsIn(Formula node, Formula formula) {
        ListOfVariable vars = new ListOfVariable();
        formula.childrenOfKind(Variable.class, vars);
        return node.getVariablesSubstitution(vars);
    }
	
	public jade.semantics.lang.sl.tools.MatchResult match(Formula node, Node expression)
	{
		return SLPatternManip.match(node, expression);
	}
	
	public Node instantiate(Formula node, String varname, Node expression)
	{
		try {
			return SLPatternManip.instantiate(node, varname, expression);
		} catch (WrongTypeException e) {
			e.printStackTrace();
			return null;
		}
	}

}
