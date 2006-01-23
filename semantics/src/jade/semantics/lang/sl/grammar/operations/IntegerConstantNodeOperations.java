package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.Node;

public class IntegerConstantNodeOperations
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return ((IntegerConstantNode)node).lx_value();
	}
	
    public Double realValue(Constant node)
	{
		return new Double(((IntegerConstantNode)node).lx_value().doubleValue());
	}
	
    public String stringValue(Constant node)
	{
		return ((IntegerConstantNode)node).lx_value().toString();
	}
	
	public int compare(Node node1, Node node2)
	{  
        //#PJAVA_EXCLUDE_BEGIN
		return ((IntegerConstantNode)node1).lx_value().compareTo(((IntegerConstantNode)node2).lx_value());
        //#PJAVA_EXCLUDE_END
        
        /*#PJAVA_INCLUDE_BEGIN
           if ( ((IntegerConstantNode)node1).lx_value().longValue() < ((IntegerConstantNode)node2).lx_value().longValue() ) {
           return -1;
           }
           else if ( ((IntegerConstantNode)node1).lx_value().longValue() == ((IntegerConstantNode)node2).lx_value().longValue() ) {
               return 0;
           }
           else {
              return 1;
           }
         #PJAVA_INCLUDE_END*/
	}
}
