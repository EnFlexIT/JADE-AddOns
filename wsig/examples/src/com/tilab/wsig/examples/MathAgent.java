/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package com.tilab.wsig.examples;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;
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
import jade.util.Logger;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.util.Date;
import java.util.logging.Level;

public class MathAgent extends Agent {

	public static final String WSIG_FLAG = "wsig";
	public static final String WSIG_MAPPER = "wsig-mapper";
	public static final String WSIG_PREFIX = "wsig-prefix";
	public static final String WSIG_HIERARCHICAL_TYPE = "wsig-hierarchical-type";
	
	private Logger logger = Logger.getMyLogger(MathAgent.class.getName());
	private SLCodec codec = new SLCodec();
	private Date startDate;
	private Ontology onto;

	protected void setup() {
		logger.log(Level.INFO, "MathAgent starting...");
		logger.log(Level.INFO, "Agent name: "+getLocalName());

		// Get agent arguments
		Object[] args = getArguments();

		// Prepare a DFAgentDescription
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(this.getAID());
		dfad.addLanguages(codec.getName());
		dfad.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		ServiceDescription sd;
		sd = new ServiceDescription();
		sd.addLanguages(codec.getName());
		sd.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		sd.setType("MathAgent");
		sd.setOwnership("MathOwner");

		// WSIG properties
		sd.addProperties(new Property(WSIG_FLAG, "true"));
		
		// Service name
		String wsigServiceName = "Math"; 
		if (args.length >= 1) {
			wsigServiceName = (String)args[0];
		}
		logger.log(Level.INFO, "Service name: "+wsigServiceName);
		sd.setName(wsigServiceName);
		
		// Mapper
		boolean isMapperPresent = false; 
		if (args.length >= 2) {
			if (!"".equals((String)args[1])) {
				isMapperPresent = Boolean.parseBoolean((String)args[1]);
			}
		}
		logger.log(Level.INFO, "Mapper present: "+isMapperPresent);
		if (isMapperPresent) {
			sd.addProperties(new Property(WSIG_MAPPER, "com.tilab.wsig.examples.MathOntologyMapper"));
		}
		
		// Ontology
		boolean beanOnto = false;
		if (args.length >= 3) {
			if (!"".equals((String)args[2])) {
				beanOnto = Boolean.parseBoolean((String)args[2]);
			}
		}
		logger.log(Level.INFO, "Use bean-ontology: "+beanOnto);
		if (beanOnto) {
			onto = MathBeanOntology.getInstance();
			sd.addProperties(new Property(WSIG_HIERARCHICAL_TYPE, "true"));
		} else {
			onto = MathOntology.getInstance();
		}
		sd.addOntologies(onto.getName());

		// Prefix
		String wsigPrefix = ""; 
		if (args.length >= 4) {
			wsigPrefix = (String)args[3];
		}
		logger.log(Level.INFO, "Prefix: "+wsigPrefix);
		if (wsigPrefix != null && !wsigPrefix.equals("")) {
			sd.addProperties(new Property(WSIG_PREFIX, wsigPrefix));
		}
		
		dfad.addServices(sd);

		// Register codec/onto
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		getContentManager().registerOntology(onto);
		
		// DF registration
		try {
			DFService.register(this, dfad);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Problem during DF registration", e);
			doDelete();
		}

		logger.log(Level.INFO, "MathAgent started");
		startDate = new Date();
		
		// Add math behaviour
		this.addBehaviour(new CyclicBehaviour(this) {
			private MessageTemplate template = MessageTemplate.MatchOntology(onto.getName());

			public void action() {
				ACLMessage msg = myAgent.receive(template);
				if (msg != null) {
					Action actExpr;
					try {
						actExpr = (Action) myAgent.getContentManager().extractContent(msg);
						AgentAction action = (AgentAction) actExpr.getAction();
						
						logger.log(Level.INFO, "Execute action: "+action.getClass().getSimpleName());
						if (action instanceof Sum) {
							serveSumAction((Sum) action, actExpr, msg);
						} else if (action instanceof Diff) {
							serveDiffAction((Diff) action, actExpr, msg);
						} else if (action instanceof Abs) {
							serveAbsAction((Abs) action, actExpr, msg);
						} else if (action instanceof Multiplication) {
							serveMultiplicationAction((Multiplication) action, actExpr, msg);
						} else if (action instanceof SumComplex) {
							serveSumComplexAction((SumComplex) action, actExpr, msg);
						} else if (action instanceof GetComponents) {
							serveGetComponentsAction((GetComponents) action, actExpr, msg);
						} else if (action instanceof GetRandom) {
							serveGetRandomAction((GetRandom) action, actExpr, msg);
						} else if (action instanceof PrintComplex) {
							servePrintComplexAction((PrintComplex) action, actExpr, msg);
						} else if (action instanceof GetAgentInfo) {
							serveGetAgentInfoAction((GetAgentInfo) action, actExpr, msg);
						} else if (action instanceof ConvertDate) {
							serveConvertDateAction((ConvertDate) action, actExpr, msg);
						} else if (action instanceof PrintTime) {
							servePrintTimeAction((PrintTime) action, actExpr, msg);
						} else if (action instanceof CompareNumbers) {
							serveCompareNumbersAction((CompareNumbers) action, actExpr, msg);
						} else if (action instanceof OrderShapesByArea) {
							serveOrderShapesByArea((OrderShapesByArea) action, actExpr, msg);
						}

					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error serving action", e);
					}
				} else {
					block();
				}
			}
		});
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
		
		ACLMessage notification = prepareNotification(actExpr, msg, ACLMessage.INFORM, result);
		notification.addUserDefinedParameter("USER-PARAM", "Test value passed as user defined parameter");
		send(notification);
	}

	private void serveMultiplicationAction(Multiplication multiplication, Action actExpr, ACLMessage msg) {
		double result = 1;
		Iterator it = multiplication.getNumbers().iterator();
		while(it.hasNext()) {
			Object num = it.next(); 
			result *= (((Number)num).doubleValue());
		}
//		for (Object num : multiplication.getNumbers()) {
//			result *= (((Number)num).doubleValue());
//		}
		sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	}

	private void serveSumComplexAction(SumComplex sumComplex, Action actExpr, ACLMessage msg) {
		Complex result = new Complex();
		result.setReal(sumComplex.getFirstComplexElement().getReal()+sumComplex.getSecondComplexElement().getReal());
		result.setImmaginary(sumComplex.getFirstComplexElement().getImmaginary()+sumComplex.getSecondComplexElement().getImmaginary());
		sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	}

	private void serveGetComponentsAction(GetComponents getComponets, Action actExpr, ACLMessage msg) {
		List result = new ArrayList();
		result.add(getComponets.getComplex().getReal());
		result.add(getComponets.getComplex().getImmaginary());
		sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	}

	private void serveGetRandomAction(GetRandom rnd, Action actExpr, ACLMessage msg) {
		Complex result = new Complex();
		result.setReal((float)Math.random() * 10);
		result.setImmaginary((float)Math.random() * 10);
		sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	}

	private void serveGetAgentInfoAction(GetAgentInfo getAgentInfo, Action actExpr, ACLMessage msg) {
		AgentInfo result = new AgentInfo();
		result.setAgentAid(getAID());
		result.setStartDate(startDate);
		sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	}
	
	private void serveConvertDateAction(ConvertDate convertDate, Action actExpr, ACLMessage msg) {
		long result = convertDate.getDate().getTime();
		sendNotification(actExpr, msg, ACLMessage.INFORM, Long.valueOf(result).toString());
	}
	
	private void servePrintComplexAction(PrintComplex printComplex, Action actExpr, ACLMessage msg) {
		logger.log(Level.INFO, "Complex number is "+printComplex.getComplex());
		sendNotification(actExpr, msg, ACLMessage.INFORM, null);
	}

	private void servePrintTimeAction(PrintTime printTime, Action actExpr, ACLMessage msg) {
		logger.log(Level.INFO, "Time is "+(new Date()).toString());
		sendNotification(actExpr, msg, ACLMessage.INFORM, null);
	}

	private void serveCompareNumbersAction(CompareNumbers compareNumbers, Action actExpr, ACLMessage msg) {
		if (compareNumbers.getFirstElement() == compareNumbers.getSecondElement()) {
			sendNotification(actExpr, msg, ACLMessage.INFORM, true);
		} else {
			sendNotification(actExpr, msg, ACLMessage.FAILURE, compareNumbers.getFirstElement()+" not equals to "+compareNumbers.getSecondElement());			
		}
	}
	
	private void serveOrderShapesByArea(OrderShapesByArea orderShapesByArea, Action actExpr, ACLMessage msg) {
		Shape shape = orderShapesByArea.getShape();
		shape.calculateArea();

		Parallelepiped parallelepiped = orderShapesByArea.getParallelepiped();
		parallelepiped.calculateArea();
		
		List result = new ArrayList();
		
		if (shape.getArea() > parallelepiped.getArea()) {
			result.add(shape);
			result.add(parallelepiped);
		} else {
			result.add(parallelepiped);
			result.add(shape);
		}
		sendNotification(actExpr, msg, ACLMessage.INFORM, result);
	}
	
	private void sendNotification(Action actExpr, ACLMessage request, int performative, Object result) {
		ACLMessage notification = prepareNotification(actExpr, request, performative, result);
		send(notification);
	}
	
	private ACLMessage prepareNotification(Action actExpr, ACLMessage request, int performative, Object result) {
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
				} else {
					ce = new Done(actExpr);
				}
				getContentManager().fillContent(reply, ce);
			}
			catch (Exception e) {
				logger.log(Level.SEVERE, "Agent " + getName() + ": Unable to send notification", e);
			}
		} else {
			reply.setPerformative(performative);
			if (result instanceof String) {
				reply.setContent((String)result);
			}
		}
		reply.addUserDefinedParameter(ACLMessage.IGNORE_FAILURE, "true");
		return reply;
	}

	protected void takeDown() {
		// Deregister from the DF
		try {
			DFService.deregister(this);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in DF deregistration", e);
		}

		logger.log(Level.INFO, "MathAgent stopped");
	}
}
