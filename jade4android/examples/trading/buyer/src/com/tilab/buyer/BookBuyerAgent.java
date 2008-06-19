package com.tilab.buyer;


import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;
import jade.util.Logger;
import jade.wrapper.gateway.GatewayAgent;

public class BookBuyerAgent extends GatewayAgent {

	private Vector knownSellers = new Vector();
	private static long PERIOD = 30000;
	private BookBuyerGui theGui;
	
	
	private final Logger myLogger = Logger.getMyLogger(BookBuyerAgent.class.getName());

	@Override
	protected void processCommand(final Object command) {
		if (command instanceof AgentCommand){
			AgentCommand cmd = (AgentCommand) command;
			//we received a purchase command
			if (cmd.getCommandName().equals(PurchaseManager.CMD_NAME_PURCHASE)){
				cmd.execute(this, new PurchaseManager(cmd.getCommandParams()));
			}
			
		} else if (command instanceof BookBuyerGui){
			theGui = (BookBuyerGui) command;
		}
		
		releaseCommand(command);
	}
	
	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		// Subscribe to the DF
		super.setup();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(TradingVocabulary.BOOK_SELLING_SERVICE);
		DFAgentDescription dfTemplate = new DFAgentDescription();
		dfTemplate.addServices(sd);
		SearchConstraints sc = new SearchConstraints();
		// Explicitly set the max-results (default is 1)
		sc.setMaxResults(new Long(10));
		ACLMessage subscribe = DFService.createSubscriptionMessage(this, getDefaultDF(), dfTemplate, sc);
		Behaviour b = new SubscriptionInitiator(this, subscribe) {
			protected void handleInform(ACLMessage inform) {
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
					for (int i = 0; i < dfds.length; ++i) {
						knownSellers.addElement(dfds[i].getName());
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		};
		addBehaviour(b);
		
		
	}
	
	
	/**
	   Inner class PurchaseManager
	   The behaviour managing the purchase of a book.
	 */
	public class PurchaseManager extends TickerBehaviour {
		private String bookTitle;
		private int desiredCost;
		private int deltaCost;
		private long startTime, deltaTime, deadline;
		
		public static final String PARAM_NAME_BOOK_TITLE="bookTitle";
		public static final String PARAM_NAME_DESIRED_COST="desiredCost";
		public static final String PARAM_NAME_DEADLINE="deadline";
		public static final String PARAM_NAME_MAXCOST="maxCost";
		public static final String CMD_NAME_PURCHASE = "purchase";
		
		private PurchaseManager(Map<String,Object> commandParams) {
			super(BookBuyerAgent.this, PERIOD);
			
			this.bookTitle = (String) commandParams.get(PARAM_NAME_BOOK_TITLE);
			this.desiredCost = ((Integer) commandParams.get(PARAM_NAME_DESIRED_COST)).intValue();
			this.deadline = ((Long) commandParams.get(PARAM_NAME_DEADLINE)).longValue();
			
			startTime = System.currentTimeMillis();
			
			deltaTime = deadline - startTime;
			deltaCost = ((Integer) commandParams.get(PARAM_NAME_MAXCOST)).intValue() - desiredCost;
		}
			
		public void onStart() {
			super.onStart();
			theGui.showMessage("------------------------------------");
			theGui.showMessage("Trying to buy book "+bookTitle+"....");
			startNegotiation();
		}
		
		public void onTick() {
			startNegotiation();
		}
		
		private void startNegotiation() {
			// Update the acceptable cost
			long currentTime = System.currentTimeMillis();
			if (currentTime <= deadline + PERIOD) {
				long elapsedTime = currentTime - startTime;
				int acceptableCost = (int) (desiredCost + deltaCost * elapsedTime / deltaTime); 
				
				// Add a BookBuyer behaviour that actually negotiates with the
				// known sellers the purchased of the desired book at the currently 
				// acceptable cost.
				myAgent.addBehaviour(new BookBuyer(bookTitle, acceptableCost, this));
			}
			else {
				// Deadline expired. Notify the user and terminate.
				theGui.showMessage("Could not buy book "+bookTitle+". :-((");
				stop();
			}	
		}
			
	} // END of inner class PurchaseManager
	

	/**
	   Inner class BookBuyer
	   The behaviour actually negotiating the purchase of a book with 
	   known sellers.
	 */
	private class BookBuyer extends Behaviour {
		private static final int INIT_STATE = 0;
		private static final int WAIT_FOR_PROPOSALS_STATE = 1;
		private static final int ACCEPT_SENT_STATE = 2;
		private static final int SUCCESS_STATE = 3;
		private static final int UNSUCCESS_STATE = 4;
		
		private String title;
		private int acceptableCost;
		private PurchaseManager myPurchaseManager;
		
		private MessageTemplate myTemplate;
		private int totExpectedReplies;
		private int repliesCnt;
		private int state = INIT_STATE;
		private int minPrice;
		private ACLMessage bestProposal;
		private String notificationMessage;
		
		private BookBuyer(String title, int acceptableCost, PurchaseManager mgr) {
			super(BookBuyerAgent.this);
			
			this.title = title;
			this.acceptableCost = acceptableCost;
			myPurchaseManager = mgr;
		}
		
		public void onStart() {
			// Send a CFP message to all known sellers
			if (knownSellers.size() > 0) {
				myLogger.log(Logger.INFO, "Starting negotiation. Acceptable cost is "+acceptableCost);
				theGui.showMessage("Starting negotiation. Acceptable cost is "+acceptableCost);
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				Enumeration e = knownSellers.elements();
				while (e.hasMoreElements()) {
					cfp.addReceiver((AID) e.nextElement());
				}
				totExpectedReplies = knownSellers.size();
				repliesCnt = 0;
				String convId = "C_"+System.currentTimeMillis()+"_"+myAgent.getName();
				cfp.setConversationId(convId);
				cfp.setContent(title);
				cfp.setOntology(TradingVocabulary.TRADING_ONTOLOGY);
				myAgent.send(cfp);
				myTemplate = MessageTemplate.MatchConversationId(convId);
				state = WAIT_FOR_PROPOSALS_STATE;
			}
			else {
				state = UNSUCCESS_STATE;
			}
		}
		
		public void action() {
			ACLMessage msg = myAgent.receive(myTemplate);
			if (msg != null) {
				switch (state) {
					case WAIT_FOR_PROPOSALS_STATE: {
						switch (msg.getPerformative()) {
						case ACLMessage.PROPOSE:
							int price = Integer.parseInt(msg.getContent());
							myLogger.log(Logger.FINE, "Proposal received. Price is "+price);
							theGui.showMessage("Proposal received. Price is "+price);
							if (bestProposal == null || price < minPrice) {
								// Up to now this is the best offer. Store it
								myLogger.log(Logger.FINE, "This is the best offer at the moment.");
								theGui.showMessage("This is the best offer at the moment.");
								minPrice = price;
								bestProposal = msg;
							}
							break;
						case ACLMessage.REFUSE:
							// The sender of this message is not selling the book. 
							// Don't care
							break;
						case ACLMessage.NOT_UNDERSTOOD:
							// The sender of this message didn't understant the CFP.
							// Log a warning.
							break;
						case ACLMessage.FAILURE:
							// If the sender is the AMS, one of the known sellers
							// is no longer active --> Remove it from the list of known
							// sellers
							if (msg.getSender().equals(myAgent.getAMS())) {
								try {
									AID oldSeller = AMSService.getFailedReceiver(myAgent, msg);
									knownSellers.removeElement(oldSeller);
								}
								catch (FIPAException fe) {
									// Should never happen
									fe.printStackTrace();
								}
							}
							else {
								// An unexpected error happened somewhere. Log a warning
								myLogger.log(Logger.WARNING, "Unexpected FAILURE message received");
							}
							break;
						default:
							// Unexpected message. Log a warning
							myLogger.log(Logger.WARNING, "Unexpected message received");
						}
						repliesCnt++;
						if (repliesCnt >= totExpectedReplies) {
							myLogger.log(Logger.FINE, "All replies received.");
							theGui.showMessage("All replies received.");
							// We got all the replies. If the best offer is <= of the
							// acceptable cost, try to actually buy the book.
							if (bestProposal != null && minPrice <= acceptableCost) {
								myLogger.log(Logger.INFO, "Accepting proposal "+minPrice+" of seller "+bestProposal.getSender().getName());
								theGui.showMessage("Accepting proposal "+minPrice+" of seller "+bestProposal.getSender().getName());
								ACLMessage accept = bestProposal.createReply();
								accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
								accept.setContent(title+"#"+minPrice);
								myAgent.send(accept);
								myLogger.log(Logger.INFO, "Sent an accept proposal message: " + accept.toString());
								state = ACCEPT_SENT_STATE;
							}
							else {
								myLogger.log(Logger.INFO, "Negotiation unsuccessful.");
								theGui.showMessage("Negotiation unsuccessful.");
								state = UNSUCCESS_STATE;
							}
						}							
					}
					break;
					
					case ACCEPT_SENT_STATE: {
						switch (msg.getPerformative()) {
						case ACLMessage.INFORM:
							// Book successfully purchased. Notify the user
							theGui.showMessage("Book "+title+" successfully purchased. Price = "+msg.getContent()+". :-))");
							myPurchaseManager.stop();
							state = SUCCESS_STATE;
							break;
						case ACLMessage.NOT_UNDERSTOOD:
							// The seller didn't understant the ACCEPT_PROPOSAL.
							myLogger.log(Logger.WARNING, "ACCEPT_PROPOSAL not understood.");
							state = UNSUCCESS_STATE;
							break;
						case ACLMessage.FAILURE:
							// If the sender is the AMS, the seller has disappeared
							// in the meanwhile. Remove it.
							if (msg.getSender().equals(myAgent.getAMS())) {
								try {
									AID oldSeller = AMSService.getFailedReceiver(myAgent, msg);
									myLogger.log(Logger.INFO, "Seller "+oldSeller.getName()+" disappeared in the meanwhile");
									theGui.showMessage("Seller "+oldSeller.getName()+" disappeared in the meanwhile");
									knownSellers.removeElement(oldSeller);
								}
								catch (FIPAException fe) {
									// Should never happen
									fe.printStackTrace();
								}
							}
							else {
								// Someone else purchased the book in the meanwhile.
								myLogger.log(Logger.INFO, "Purchase failed. Book already sold");
								theGui.showMessage("Purchase failed. Book already sold");
							}
							state = UNSUCCESS_STATE;
							break;
						default:
							// Unexpected message. Log a warning
							myLogger.log(Logger.WARNING, "Unexpected message received");
							theGui.showMessage("Unexpected message received");
						}
					}
					break;
				}
			}
			else {
				block();
			}
		}
		
		public boolean done() {
			return (state == SUCCESS_STATE || state == UNSUCCESS_STATE);
		}		
	}

}
