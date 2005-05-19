package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Formula;
 
public class TrueNodeOperations
    extends FormulaNodeOperations
{
    public boolean isSubsumedBy(Formula node, Formula formula)
    {
	return true;
    }
}
