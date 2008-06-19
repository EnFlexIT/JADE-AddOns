package seller;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

import java.util.Hashtable;
import java.util.StringTokenizer;


/**
   This is the agent that tries to sell books on behalf of its user
   
   @author Giovanni Caire - TILAB
 */
public class BookSellerAgent extends Agent {
	private static long PERIOD = 30000;
	
	private BookSellerGui myGui;
	private Hashtable catalogue = new Hashtable();
	
	protected void setup() {
		// Show the gui
		myGui = new BookSellerGui(this);
		myGui.show();

		// Register with the DF
		registerWithDF();
		
		// Add the behaviour serving requests from buyer agents
		addBehaviour(new PurchaseServer());
	}
	
	protected void takeDown() {
		if (myGui != null) {
			myGui.dispose();
		}
	}
	
	/** 
	   This method is called by the GUI when the user makes a book
	   available for sale.
	 */
	public void addToCatalogue(String title, int price, int minPrice, long deadline) {
		// Add the book to the catalogue of books on sale
		CatalogueEntry entry = new CatalogueEntry(title, price, minPrice, deadline);
		catalogue.put(title, entry);
		
		// Add the behaviour that manages the price of the newly added book
		addBehaviour(new PriceManager(entry));
	}
	
	private void registerWithDF() {
		ServiceDescription sd = new ServiceDescription();
		sd.setName("JADE book trading");
		sd.setType(TradingVocabulary.BOOK_SELLING_SERVICE);
		sd.addOntologies(TradingVocabulary.TRADING_ONTOLOGY);
		DFAgentDescription myDescription = new DFAgentDescription();
		myDescription.addServices(sd);
		try {
			DFService.register(this, myDescription);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}
	}
	
	
	/**
	   Inner class PurchaseServer
	   This behaviour serves the requests for book quotation book purchase 
	 */
	private class PurchaseServer extends CyclicBehaviour {
		private MessageTemplate myTemplate = MessageTemplate.and(
			MessageTemplate.MatchOntology(TradingVocabulary.TRADING_ONTOLOGY),
			MessageTemplate.or(
				MessageTemplate.MatchPerformative(ACLMessage.CFP),
				MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL) ) );
				
		public PurchaseServer() {
			super(BookSellerAgent.this);
		}
		
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				System.out.println("Message received.");
				if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					// The other agent wants to buy a book
					serveAcceptProposal(msg);
				}
				else {
					// The other agent wants to know how much a book is
					serveCFP(msg);
				}
			}
			else {
				block();
			}
		}
		
		private void serveAcceptProposal(ACLMessage msg) {
			ACLMessage reply = msg.createReply();
			String title = null;
			int acceptedPrice = -1;
			String creditCardNumber = null;
			try {
				// Parse the content that must be in the form:
				// title#accepted-price#credit-card-number
				String content = msg.getContent();
				StringTokenizer st = new StringTokenizer(content, "#");
				title = st.nextToken();
				acceptedPrice = Integer.parseInt(st.nextToken());
//				creditCardNumber = st.nextToken();
			
				CatalogueEntry entry = (CatalogueEntry) catalogue.get(title);
				if (entry != null) {
					int price = entry.getPrice();
					if (price <= acceptedPrice) {
						// Handle payment and shipment: OUT OF SCOPE!!!!!
						
						// Remove the entry from the catalogue, stop the price manager
						// and notify the user
						catalogue.remove(title);
						entry.getPriceManager().stop();
						myGui.showMessage("Book "+entry.getTitle()+" successfully sold. Price = "+price); 
						
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent(String.valueOf(price));
					}
					else {
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent("Price higher");
					}
				}
				else {
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("Book not available");
				}
			}
			catch (Exception e) {
				// The content is not in the correct form
				reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			}
			myAgent.send(reply);
		}
		
		private void serveCFP(ACLMessage msg) {
			ACLMessage reply = msg.createReply();
			// Get the title from the content
			String title = msg.getContent();
			System.out.println("CFP. title is "+title);
			
			CatalogueEntry entry = (CatalogueEntry) catalogue.get(title);
			if (entry != null) {
				reply.setPerformative(ACLMessage.PROPOSE);
				reply.setContent(String.valueOf(entry.getPrice()));
			}
			else {
				reply.setPerformative(ACLMessage.REFUSE);
				reply.setContent("Book not available");
			}
			myAgent.send(reply);
		}
	} // END of inner class PurchaseServer		

	
	/**
	   Inner class PriceManager
	   The behaviour managing the price of a given book on sale.
	 */
	private class PriceManager extends TickerBehaviour {
		CatalogueEntry myEntry;
		
		private int deltaPrice;
		private long startTime, deltaTime;
		
		private PriceManager(CatalogueEntry entry) {
			super(BookSellerAgent.this, PERIOD);
			
			myEntry = entry;
			myEntry.setPriceManager(this);
			
			startTime = System.currentTimeMillis();
			
			deltaTime = myEntry.getDeadline() - startTime;
			deltaPrice = myEntry.getPrice() - myEntry.getMinPrice();			
		}
			
		public void onStart() {
			super.onStart();
			
			myGui.showMessage("Trying to sell book "+myEntry.getTitle()+"....");
		}
		
		public void onTick() {
			long currentTime = System.currentTimeMillis();
			if (currentTime <= myEntry.getDeadline() + PERIOD) {
				long elapsedTime = currentTime - startTime;
				int newPrice = (int) (myEntry.getPrice() - deltaPrice * elapsedTime / deltaTime);
				myEntry.setPrice(newPrice);
			}
			else {
				// Deadline expired. Remove the entry from the catalogue, 
				// notify the user and terminate.
				catalogue.remove(myEntry.getTitle());
				myGui.showMessage("Could not sell book "+myEntry.getTitle()+". :-((");
				stop();
			}
		}		
	} // END of inner class CatalogueManager		
		
		
	/**
	   Inner class CatalogueEntry
	   This class embeds the information related to a book currently 
	   on sale: The title and the current price.
	 */
	private class CatalogueEntry {
		private String title;
		private int price;
		private int minPrice;
		private long deadline;
		private PriceManager myPriceManager;
		
		public CatalogueEntry(String title, int price, int minPrice, long deadline) {
			this.title = title;
			this.price = price;
			this.minPrice = minPrice;
			this.deadline = deadline;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setPriceManager(PriceManager pm) {
			myPriceManager = pm;
		}
	
		public PriceManager getPriceManager() {
			return myPriceManager;
		}
		
		public int getPrice() {
			return price;
		}
		
		public void setPrice(int p) {
			price = p;
		}
	
		public int getMinPrice() {
			return minPrice;
		}
		
		public long getDeadline() {
			return deadline;
		}		
	} // END of inner class CatalogueEntry		
}
		