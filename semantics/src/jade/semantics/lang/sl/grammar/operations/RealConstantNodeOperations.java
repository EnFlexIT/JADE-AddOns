package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.RealConstantNode;

public class RealConstantNodeOperations 
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return new Long (((RealConstantNode)node).lx_value().longValue());
	}
	
    public Double realValue(Constant node)
	{
		return ((RealConstantNode)node).lx_value();
	}
	
    public String stringValue(Constant node)
	{
		return ((RealConstantNode)node).lx_value().toString();
	}
	
	public int compare(Node node1, Node node2)
	{
	    //#PJAVA_EXCLUDE_BEGIN
		return ((RealConstantNode)node1).lx_value().compareTo(((RealConstantNode)node2).lx_value());
        //#PJAVA_EXCLUDE_END
        /*#PJAVA_INCLUDE_BEGIN
               if ( ((RealConstantNode)node1).lx_value().longValue() < ((RealConstantNode)node2).lx_value().longValue() ) {
                   return -1;
               }
               else if ( ((RealConstantNode)node1).lx_value().longValue() == ((RealConstantNode)node2).lx_value().longValue() ) {
                   return 0;
               }
               else {
                   return 1;
               }
        #PJAVA_INCLUDE_END*/
         
	}
}
