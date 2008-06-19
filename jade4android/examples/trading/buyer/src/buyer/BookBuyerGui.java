package buyer;


/**
   This is the interface implemented by the GUI of the agent that 
   tries to buy books on behalf of its user
 */
public interface BookBuyerGui {
	
	void setAgent(BookBuyerAgent a);
	void show();
	void dispose();
	void showMessage(String message);
}	