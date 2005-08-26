/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package jade.tools.ascml.launcher;

import jade.content.Term;
import jade.content.abs.AbsAgentAction;
import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsTerm;
import jade.content.abs.AbsVariable;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.*;
import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Equals;
import jade.content.onto.basic.TrueProposition;
import jade.core.*;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.introspection.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.FIPAProtocolNames;
import jade.proto.SubscriptionResponder;
import jade.tools.ToolAgent;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.repository.Repository;

import jade.tools.ascml.onto.*;
import jade.tools.ascml.launcher.Subscriptions.*;
import jade.tools.ascml.launcher.abstracts.*;
import jade.tools.ascml.launcher.behaviours.*;
import jade.tools.ascml.model.AgentTypeModel;
import jade.tools.ascml.events.*;
import jade.tools.ascml.gui.GUI;
import jade.tools.sl.SLFormatter;

import java.util.*;

/**
 * @author sven
 *
 */
public class AgentLauncher extends ToolAgent {

	public final static String ASCML_VERSION = "0.49c";

    public final String introspectorPrefix = "IntrospectorASCML";
    public final String snifferPrefix = "SnifferASCML";

    public final String benchmarkerSnifferPrefix = "BencherASCML";
    
    public Codec codec = new SLCodec(2);

    protected Repository repository;
    protected GUI gui;
    public LauncherInterface li;

    private HashMap<String, IRunnableAgentInstance> launchedAgents = new HashMap<String, IRunnableAgentInstance>();
    private HashMap<String, Integer> startedTypeCountMap = new HashMap<String, Integer>();
    protected HashMap<String, Vector<AID>> subscribedASCMLS = new HashMap<String, Vector<AID>>();
    private ToolRequester mySniffer = new ToolRequester(this, "jade.tools.sniffer.Sniffer", snifferPrefix, IRunnableAgentInstance.TOOLOPTION_SNIFF, true);
    private ToolRequester myIntrospector = new ToolRequester(this, "jade.tools.introspector.Introspector", introspectorPrefix, IRunnableAgentInstance.TOOLOPTION_DEBUG, false);
    private ToolRequester myBenchmarker = new ToolRequester(this, "jade.tools.benchmarking.BenchmarkSnifferAgent", benchmarkerSnifferPrefix,IRunnableAgentInstance.TOOLOPTION_BENCHMARK, true);
    private ListenerManagerInterface lmi;

	private StatusSubscriptionManager subscriptionManager = new StatusSubscriptionManager(this);


    /**
     * Listens to platform evens like DEADAGENT,BORNAGENT,...
     * We only care about DEADAGENT and use it to notify the repository
     */
    protected class platformEventListener extends AMSListenerBehaviour {

        protected void installHandlers(Map handlersTable) {
            handlersTable.put(IntrospectionVocabulary.DEADAGENT, new ToolAgent.EventHandler() {
                public void handle(Event ev) {
                    DeadAgent da = (DeadAgent) ev;
                    AID agent = da.getAgent();
                    if (launchedAgents.containsKey(agent.getLocalName())) {
                        IRunnableAgentInstance runnableDeadAgent = launchedAgents.get(agent.getLocalName());
                        removeLaunchedAgent(runnableDeadAgent);
                        System.err.println("DEADAGENT");
                        runnableDeadAgent.setStatus(new NonFunctional());
                    } else {
                        // System.out.println("Could not find agent
                        // "+agent.getLocalName());
                    }
                }
            });

            /*
             * handlersTable.put(IntrospectionVocabulary.BORNAGENT, new
             * ToolAgent.EventHandler() { public void handle(Event ev) {
             * BornAgent ba = (BornAgent)ev; AID agent = ba.getAgent();
             * if(launchedAgents.contains(agent.getLocalName())) {
             * nameToDependencyMap.depFulfulled(agent.getLocalName()); }
             * 
             * String agentType =
             * (String)nameToTypeMap.get(agent.getLocalName()); if (agentType !=
             * null) { System.out.println(agent.getLocalName()+ " was born.
             * Type: " + agentType); } if
             * (typeToDependencyMap.contains(agentType)) {
             * typeToDependencyMap.depFulfulled(agentType); } } });
             */
        }

    } // END of inner class plattformEventListener

    /**
     * adds a Behaviour which attempts to start a society on a remote plattform
     */
    public void StartRemoteSociety(ACLMessage msg, AbstractMARWaitThread dt) {
        System.err.print("AgentLauncher.StartRemoteSociety: request-receiver: ");
        Iterator it = msg.getAllReceiver();
        while (it.hasNext()) {
            jade.core.AID recv = (jade.core.AID) it.next();
            System.out.println(recv.getName());
        }
        ModelActionRequestBehaviour srb = new ModelActionRequestBehaviour(msg, dt, this);
        addBehaviour(srb);
    }

    /**
     * Send a message to the Sniffer to sniff an agent
     * 
     * @param agentModel
     *            Name of the agent to be sniffed
     * @param synchobject
     *            Object to be notified when the Sniffer answers
     */
    public void doSniff(IRunnableAgentInstance agentModel, StringBuffer synchobject) {
        mySniffer.requestTool(agentModel, synchobject);
    }

    public void snifferReady() {
        mySniffer.toolReady();
    }

    /**
     * Send a message to the benchmarker to sniff an agent
     * 
     * @param agentModel
     *            Name of the agent to be benchmarked
     * @param synchobject
     *            Object to be notified when the Sniffer answers
     */
    public void doBenchmark(IRunnableAgentInstance agentModel, StringBuffer synchobject) {
        myBenchmarker.requestTool(agentModel, synchobject);
    }

    public void benchmarkReady() {
        myBenchmarker.toolReady();
    }

    /**
     * Send a message to the Introspector to debug an agent
     * 
     * @param agentModel
     *            Name of the agent to be debugged
     * @param synchobject
     *            Object to be notified when the Introspector answers
     */
    public void doIntrospect(IRunnableAgentInstance agentModel, StringBuffer synchobject) {
        myIntrospector.requestTool(agentModel, synchobject);
    }

    public void introReady() {
        myIntrospector.toolReady();
    }

    /**
     * @param msg
     *            The message sent to the AMS
     * @param result
     *            The added behaviour calls result.notfiyAll() when a inform or a failure message arrives.            
     *            Afterwords tt cointains the failure message or it is empty if the answer was an inform.
     * @param aName
     *            identifier for the request
     */
    public void addAMSBehaviour(ACLMessage msg, StringBuffer result, String aName) {
        addBehaviour(new AMSClientBehaviour(msg, result, aName));
    }

    public void addAMSBehaviour(AchieveREInitiator initiator) {
        addBehaviour(initiator);
    }

    protected void clearMaps() {
        // Clean up:
        mySniffer.reset();
        myIntrospector.reset();
        launchedAgents.clear();
        startedTypeCountMap.clear();
    }

    /**
     * Adds a IRunnableAgentInstance to a maps, so we allways now which agents are started.
     * Additionally we add the instance.getClassName to the startedTypeCountMap or increment 
     * the count if it is already in there to keep count of how many agents of each type we
     * already started.   
     * 
     * @param instance
     *              The IRunnableAgentInstance to be added to the launchedAgnets-map
     *              instance.getName() will be used as the key              
     */
    protected void addLaunchedAgent(IRunnableAgentInstance instance) {
        // System.out.println("Adding '"+instance.getName()+"' to List");
        launchedAgents.put(instance.getName(), instance);
        int count;
        if (startedTypeCountMap.containsKey(instance.getClassName())) {
            count = startedTypeCountMap.get(instance.getClassName()) + 1;
        } else {
            count = 1;
        }
        startedTypeCountMap.put(instance.getClassName(), count);
        // System.out.println("Added "+instance.getClassName()+" with count
        // "+countStr+" to stcMap");
    }

    /**
     * Looks up an IRunnableAgentInstance in the launchedAgents-map and removes it
     * Additionally we decrement the count of instance.getClassName from the startedTypeCount-map
     * or remove it if it's zero to keep count of how many agents of each type we already started. 
     * 
     * @param instance
     *              The IRunnableAgentInstance to be searched for in the launchedAgnets-map
     *              instance.getName() will be used as the key         
     */
    protected void removeLaunchedAgent(IRunnableAgentInstance instance) {
        launchedAgents.remove(instance.getName());
        int count;
        if (startedTypeCountMap.containsKey(instance.getClassName())) {
            count = startedTypeCountMap.get(instance.getClassName()) - 1;
            // System.out.println("Found "+instance.getClassName()+" in stcMap
            // with count "+count);
            if (count == 0) {
                startedTypeCountMap.remove(instance.getClassName());
            } else {
                startedTypeCountMap.put(instance.getClassName(), count);
            }
        } else {
            System.out.println("Could not find " + instance.getClassName() + " in stcMap");
        }
    }


    /**
     * @param localName
     *          The name to search the launchedAgents-map for
     * @return 
     *          true if launchedAgents contains localName as a key
     */
    public boolean isAgentStarted(String localName) {
        if (launchedAgents.containsKey(localName))
            return true;
        else
            return false;
    }

    /**
     * @param className
     *          The name to search the startedTypeCount-map for
     * @return 
     *          0 if the name wasn't found, else the count of how many agents of this type are running
     */
    public int getTypeStartedCount(String className) {
        if (startedTypeCountMap.containsKey(className)) {
            return startedTypeCountMap.get(className);
        } else {
            return 0;
        }
    }
    
    protected void toolSetup() {
		
		// arguments they may have been passed to the ASCML at startup		
		boolean noGUI = false;
		String propertyFileLocation = "";

        // check if arguments habe been passed
        Object[] args = this.getArguments();

		for (int i=0; (args != null) && (i < args.length); i++)
		{
			if (((String)args[i]).equalsIgnoreCase("nogui"))
				noGUI = true;
			else
				propertyFileLocation = ((String)args[i]).trim();
		}

		if (noGUI == false)
        	System.out.println("ASCML Version " + ASCML_VERSION);
		else
			System.out.println("ASCML Version " + ASCML_VERSION + " (without GUI)");

        if (!propertyFileLocation.equals(""))
            System.out.println("ASCML properties: " + propertyFileLocation);
        System.out.println("ASCML running as: " + this.getName());
        Iterator addr = this.getAMS().getAllAddresses();
        while (addr.hasNext()) {
            System.err.println("ASCML running on: " + addr.next());
        }

        (this.getContentManager()).registerLanguage(codec);
        (this.getContentManager()).registerOntology(jade.domain.JADEAgentManagement.JADEManagementOntology.getInstance());
        (this.getContentManager()).registerOntology(ASCMLOntology.getInstance());	

        /** Registration with the DF */

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("ASCML");
        sd.setName(getName());
        sd.setOwnership("RWTHAachenAndUniHamburg");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFAgentDescription actualDfd = DFService.register(this, getDefaultDF(), dfd);
//            actualDfd.setLeaseTime(new Date(20000));
//            Date lease = actualDfd.getLeaseTime();
//            System.out.println("Lease: " + lease.toString());
            DFService.keepRegistered(this, getDefaultDF(), actualDfd, null);
        } catch (FIPAException e) {
            System.err.println(getLocalName() + " registration with the DF unsucceeded. Reason: " + e.getMessage());
            doDelete();
        }

        SequentialBehaviour AMSSubscribe = new SequentialBehaviour();
        // Send 'subscribe' message to the AMS
        AMSSubscribe.addSubBehaviour(new SenderBehaviour(this, getSubscribe()));

        // Handle incoming 'inform' messages
        AMSSubscribe.addSubBehaviour(new platformEventListener());

        // Schedule Behaviours for execution
        addBehaviour(AMSSubscribe);

        // adding ModelActionRequestListener
        MessageTemplate template = MessageTemplate.MatchOntology(ASCMLOntology.ONTOLOGY_NAME);
        template = MessageTemplate.and(template, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST));
        template = MessageTemplate.and(template, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        addBehaviour(new ModelActionRequestListener(template, this));
		
        MessageTemplate template2 = MessageTemplate.MatchOntology(ASCMLOntology.ONTOLOGY_NAME);
		template2 = MessageTemplate.and(template2, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_QUERY));
		template2 = MessageTemplate.and(template2, MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF));
        addBehaviour(new GetStatusRequestListener(this, template2));
                
        li = new LauncherInterface(this);
        lmi = new ListenerManagerInterface(this);

        repository = new Repository(noGUI);
        repository.getListenerManager().addExceptionListener(lmi); // AgentLauncher now has to implement exceptionThrown-method (see below)
        repository.getListenerManager().addToolTakeDownListener(lmi); // AgentLauncher now has to implement toolTakeDown-method (see below)
		repository.getListenerManager().addModelChangedListener(lmi); // AgentLauncher now has to implement modelChanged-method (see below)
        repository.getListenerManager().addModelActionListener(li);
        repository.getListenerManager().addLongTimeActionStartListener(lmi);

		if (!noGUI)
			gui = new GUI(repository);

        // toDO: when user presses cancel&quit in the propertyChoose-Dialog init
        // doesn't need to be called
        repository.init(propertyFileLocation);

		if (!noGUI)
        	gui.showMainApplicationGUI(getName());
        else
			System.err.println(repository.getModelIndex());
		
		/*try
		{
			int randomNumber = (int)(Math.random()*1000) % 1000;
			System.err.println("AgentLauncher: Output redirected to file " + randomNumber + "_[error|out].txt... ");
			System.setErr(new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(randomNumber + "_error.txt"), true)));
			System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(new java.io.File(randomNumber + "_out.txt"), true)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
    }


    /**
     * Get the repository-object. The repository-object allows accessing the
     * agent- and society-types.
     * 
     * @return The Repository-object.
     */
    public Repository getRepository() {
        return this.repository;
    }

    /**
     * Every toolAgent has to implement the toolTakeDown method. It is called
     * like agent.takeDown(), when an agent is killed via the RMA-GUI or when
     * the plattform is going down.
     * 
     * @see jade.core.Agent#takeDown()
     */
    protected void toolTakeDown() {
        send(getCancel());
        try {
            DFService.deregister(this);
            System.err.println("AgentLauncher.toolTakeDown: ASCML deregistered @ DF");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        repository.exit();
        gui.exit();
        super.toolTakeDown();
        System.err.println("ASCML shutdown completed.");
    }

    static {
        // Allow debugging outputs to be printed.
        System.setErr(System.out);
    }

    /**
     * This behavoiur sends messages to the AMS and gets its answers in return.
     * Whoever added this vis addAMSBehvious is informed vis result.notifyAll() 
     *
     */
    private class AMSClientBehaviour extends AchieveREInitiator {
        protected StringBuffer result;

        protected String aName;

        public AMSClientBehaviour(ACLMessage request, StringBuffer result, String aName) {
            super(AgentLauncher.this, request);
            this.result = result;
            this.aName = aName;
            // System.err.println("AMSClientBehaviour.constructor");
            // System.out.println("AMS BEHAVIOUR STARTED: " + aName);
        }

        protected void handleNotUnderstood(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: NOT-UNDERSTOOD received" + reply);
        }

        protected void handleRefuse(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: REFUSE received" + reply);
        }

        protected void handleAgree(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: AGREE received" + reply);
        }

        protected void handleFailure(ACLMessage reply) {
            System.err.println("AMSClientBehaviour: FAILURE received" + reply);
            // System.err.println("Failure: "+reply);
            String s = new String(reply.getContent());
            if (s.indexOf("nested") > 0) {
                s = s.substring(s.indexOf("nested"), s.length() - 1);
            } else if (s.indexOf("already-register") > 0) {
                s = aName + " is already registered";
            } else if (s.indexOf("internal-error") > 0) {
                s = s.substring(s.indexOf("internal-error") + "internal-error".length(), s.length() - 2);
            } else {
                System.err.println("\n" + s + "\n");
            }
            synchronized (result) {
                if (s != null) {
                    result.setLength(0);
                    result.append(s);
                }
                result.notifyAll();
            }
            removeBehaviour(this);
        }

        protected void handleInform(ACLMessage Inform) {
            // System.err.println("AMSClientBehaviour: INFORM received" +
            // reply);
            synchronized (result) {
                result.setLength(0);
                result.notifyAll();
            }
            removeBehaviour(this);
        }

    }

    public ListenerManagerInterface getlmi() {
        return lmi;
    }

	public StatusSubscriptionManager getSubscriptionManager() {
		return subscriptionManager;
	}
	
	
	/**
	 * Extracts the Status Object from an Inform-Message received from an ASMCL
	 * @param msg
	 * 	The ACLMessage received from an ASCML  
	 * @return
	 * 	The Status Object included in the message
	 * @throws CodecException
	 * @throws OntologyException
	 */
	public Status getStatusFromInform(ACLMessage msg) throws CodecException, OntologyException {
		AbsPredicate absEQ = (AbsPredicate) getContentManager().extractAbsContent(msg);
		AbsConcept absS = (AbsConcept) absEQ.getAbsObject("right");
		Status status = (Status)ASCMLOntology.getInstance().toObject(absS);		
		return status;
	}
	
	/**
	 * Extracts the full-quallified name of the Model from an Inform-Message received from an ASMCL
	 * @param msg
	 * 	The ACLMessage received from an ASCML  
	 * @return
	 * 	The full-quallified name of the Model, the inform-ref refers to
	 * @throws CodecException
	 * @throws OntologyException
	 */	
	public String getFQNfromInform(ACLMessage msg) throws CodecException, OntologyException {
		String fqn="";
		AbsPredicate absOuterEQ = (AbsPredicate) getContentManager().extractAbsContent(msg);
		AbsIRE absIota = (AbsIRE) absOuterEQ.getAbsObject("left");
		AbsPredicate absInnerEQ = absIota.getProposition();
		AbsConcept absModel = (AbsConcept) absInnerEQ.getAbsObject("left");
		fqn = absModel.getString("Name");				
		return fqn;
	}
		
	/**
	 * Creates a subscription message which can be used to subscribe to an ASCML.
	 * If you want to use it to just get the status of a model, you have to change
	 * the performative and the protocol
	 * @param ascml
	 * 	The ascml to adress the message to
	 * @param model
	 *  The model to subcribe to
	 * @return  
	 * 	The ACLMessage to send away to start a subscription
	 * @throws CodecException
	 * @throws OntologyException
	 */
	public ACLMessage createSubscription(AID ascml, AbsModel model) throws CodecException, OntologyException {
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		msg.setProtocol(FIPAProtocolNames.FIPA_SUBSCRIBE);
		msg.setLanguage(codec.getName());
		msg.setOntology(ASCMLOntology.ONTOLOGY_NAME);	
		msg.addReceiver(ascml);
		
		AbsIRE absIota = new AbsIRE(SL2Vocabulary.IOTA);
		absIota.setVariable(new AbsVariable("x",ASCMLOntology.STATUS));
		AbsConcept absModel = (AbsConcept) ASCMLOntology.getInstance().fromObject(model);
		absModel.set("Name",model.getName());
		absModel.set("ModelStatus",new AbsVariable("x",ASCMLOntology.STATUS));			
		AbsPredicate abseq = new AbsPredicate(SL2Vocabulary.EQUALS);
		abseq.set("left",absModel);
		abseq.set("right",new AbsVariable("x",ASCMLOntology.STATUS));
		absIota.setProposition(abseq);		
		
		getContentManager().fillContent(msg,absIota);
		
		return msg;
		/*
		 * This is an example of how to answer these messages:
		 *			AbsPredicate abseq2 = new AbsPredicate(SL2Vocabulary.EQUALS);
		 *			abseq2.set("left",absIota);
		 *			Status stat = new Available();
		 *			AbsConcept absStat = (AbsConcept) ASCMLOntology.getInstance().fromObject(stat);
		 *			abseq2.set("right",absStat);
		 *			
		 *			getContentManager().fillContent(msg,abseq2);
		 */		
		//System.out.println(SLFormatter.format(msg.toString()));
		
				
	}
	
	/**
	 * Adds a new Subscription at the given ascml for the given Model
	 * @param ascml
	 * 			The AID of the ascml to subscribe with
	 * @param model
	 * 			The model to subscribe for
	 * @return  
	 * 			The StatusSubscriptionInitiator to cancel the subscription if wanted 
	 */
	public StatusSubscriptionInitiator subscribeTo(AID ascml,AbsModel model) {
		try {			 
			StatusSubscriptionInitiator ssi = new StatusSubscriptionInitiator(this, createSubscription(ascml,model), model);
			return ssi;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}

}
