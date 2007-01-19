package com.tilab.wsig;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.ArrayList;

import org.apache.log4j.Logger;
import com.whitestein.wsig.WSIGConstants;

public class MathAgent extends Agent implements WSIGConstants {

	  
	private Logger log = Logger.getLogger(MathAgent.class.getName());
	  public static AID myAID = null;
	  private SLCodec codec = new SLCodec();


	  protected void setup() {
		log.info("A MathAgent is starting.");

		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		getContentManager().registerOntology(MathOntology.getInstance());

		// ------------------------------
		// register the agent into the DF

		// prepare a DFAgentDescription
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(this.getAID());
		dfad.addLanguages(codec.getName() );
		dfad.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST );
		ServiceDescription sd;
		sd = new ServiceDescription();
		sd.setName("MathService"); //Nome WSDL
		sd.addLanguages(codec.getName());
		// Eventuale Verifica language su wsig
		// Aggiunta in wsig.properties delle ontologie ke deve conoscere per parlare con gli agenti
		// Pero ora ci limitiamo alla FIPA_REQUEST
		sd.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		sd.setType("MathAgent"); //Eventuale type del wsdl
		sd.setOwnership("MathOwner"); // Eventuale owner del wsdl
		sd.addProperties(new Property(WSIG_FLAG,"true"));
		sd.addOntologies(MathOntology.getInstance().getName());

		sd.addProperties(new Property(WSIG_MAPPER,"com.tilab.wsig.MathOntologyMapper"));
		dfad.addServices(sd);

		try {
			DFService.register(this, dfad);
		}catch (Exception e) {
		  //something is wrong
		  e.printStackTrace();
		}
		log.debug("A MathAgent is started.");

		this.addBehaviour( new CyclicBehaviour( this ) {
			private MessageTemplate template = MessageTemplate.MatchOntology(MathOntology.getInstance().getName());
			public void action() {
			  ACLMessage msg = myAgent.receive(template);
			  if (msg != null) {
					  Action actExpr;
					  try {
						  actExpr = (Action) myAgent.getContentManager().extractContent(msg);
						  AgentAction action = (AgentAction) actExpr.getAction();
						  if (action instanceof Sum) {
							  serveSumAction((Sum) action, actExpr, msg);
						  }
						  else if (action instanceof Diff) {
							  serveDiffAction((Diff) action, actExpr, msg);
						  }
						  else if (action instanceof Abs) {
							  serveAbsAction((Abs) action, actExpr, msg);
						  }
					  } catch (Exception e) {
						  // TODO Auto-generated catch block
						  log.error("Exception: "+e.getMessage());
						  e.printStackTrace();
					  }
			  }else{
				block();
			  }
			}

		  } );
	  }


	  private void serveAbsAction(Abs abs, Action actExpr, ACLMessage msg) {
		  float real = abs.getComplex().getReal();
		  float immaginary = abs.getComplex().getImmaginary();
		  float result = Float.parseFloat(Double.toString(Math.sqrt(Math.pow(real, 2) + Math.pow(immaginary, 2))));
		  sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	  }

	  private void serveDiffAction(Diff diff, Action actExpr, ACLMessage msg) {
		  float result = diff.getFirstElement() - diff.getSecondElement();
		  sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	  }
	  private void serveSumAction(Sum sum, Action actExpr, ACLMessage msg) {
		  float result = sum.getFirstElement() + sum.getSecondElement();
		  sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	  }

	  private void sendNotification(Action actExpr, ACLMessage request, int performative, Object result) {
			// Send back a proper reply to the requester
			ACLMessage reply = request.createReply();
			if (performative == ACLMessage.INFORM) {
				reply.setPerformative(ACLMessage.INFORM);
				try {
					ContentElement ce = null;
					if (result != null) {
						// If the result is a java.util.List, convert it into a jade.util.leap.List t make the ontology "happy"
						if (result instanceof java.util.List) {
							ArrayList l = new ArrayList();
							l.fromList((java.util.List) result);
							result = l;
						}
						ce = new Result(actExpr, result);
					}
					else {
						ce = new Done(actExpr);
					}
					getContentManager().fillContent(reply, ce);
				}
				catch (Exception e) {
					log.error("Agent "+getName()+": Unable to send notification"+e);
					e.printStackTrace();
				}
			}
			else {
				reply.setPerformative(performative);

			}
			reply.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");
			send(reply);
		}

	  protected void takeDown() {
		//deregister itself from the DF

		try {
			DFService.deregister(this);
		}catch (Exception e) {
		  log.error( e );
		}

		log.debug("A MathAgent is taken down now.");
	  }
}
