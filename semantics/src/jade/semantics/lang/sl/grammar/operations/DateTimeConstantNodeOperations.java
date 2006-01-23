package jade.semantics.lang.sl.grammar.operations;

import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Node;

public class DateTimeConstantNodeOperations 
	extends TermNodeOperations 
	implements Constant.Operations 
{
    public Long intValue(Constant node)
	{
		return null;
	}
	
    public Double realValue(Constant node)
	{
		return null;
	}
	
    public String stringValue(Constant node)
	{
		return null;
	}
	
	public int compare(Node node1, Node node2)
	{
	    //#DOTNET_EXCLUDE_BEGIN
	    //#PJAVA_EXCLUDE_BEGIN
        return ((DateTimeConstantNode)node1).lx_value().compareTo(((DateTimeConstantNode)node2).lx_value());
        //#PJAVA_EXCLUDE_END
        //#DOTNET_EXCLUDE_END
        
        /*#DOTNET_INCLUDE_BEGIN
        java.util.Date date1 = ((DateTimeConstantNode)node1).lx_value();
        java.util.Date date2 = ((DateTimeConstantNode)node2).lx_value();
        System.DateTime dt1 = new System.DateTime(date1.getYear(), date1.getMonth(), date1.getDay(), date1.getHours(), date1.getMinutes(), date1.getSeconds
(), 0);
        System.DateTime dt2 = new System.DateTime(date1.getYear(), date1.getMonth(), date1.getDay(), date1.getHours(), date1.getMinutes(), date1.getSeconds
(), 0);
        return System.DateTime.Compare(dt1, dt2);
        #DOTNET_INCLUDE_END*/
        
        /*#PJAVA_INCLUDE_BEGIN
               if ( ((DateTimeConstantNode)node1).lx_value().getTime() < ((DateTimeConstantNode)node2).lx_value().getTime() ) {
                   return -1;
               }
               else if ( ((DateTimeConstantNode)node1).lx_value().getTime() == ((DateTimeConstantNode)node2).lx_value().getTime() )
               {
                   return 0;
               }
               else {
                   return 1;
               }
        #PJAVA_INCLUDE_END*/
	}
}
