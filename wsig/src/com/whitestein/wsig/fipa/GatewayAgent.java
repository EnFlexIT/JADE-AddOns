/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebService Integration Gateway (WSIG).
 *
 * The Initial Developer of the Original Code is
 * Whitestein Technologies AG.
 * Portions created by the Initial Developer are Copyright (C) 2004, 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Jozef Nagy (jna at whitestein.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.whitestein.wsig.fipa;

import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsObject;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.Codec;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.DFGUIManagement.DFAppletOntology;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FIPAManagementVocabulary;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SubscriptionInitiator;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.WSIGConstants;
import com.whitestein.wsig.net.HTTPServer;
import com.whitestein.wsig.struct.Call;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.struct.EndPoint;
import com.whitestein.wsig.struct.ReturnMessageListener;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.whitestein.wsig.translator.FIPASL0ToSOAP;
import com.whitestein.wsig.ws.UDDIOperationIdentificator;
import com.whitestein.wsig.ws.WSEndPoint;


/**
 * @author jna
 *         <p/>
 *         is gateway agent running in FIPA container.
 */
/* It extends df as the JADE implementation of DF to manage agents.
 * Remark: its functional and used version is placed in jade.domain package.
 *   Must be implemented in near future.
 */
public class GatewayAgent extends GuiAgent implements WSIGConstants {


	public static final String AGENT_TYPE = "WSIG Agent";
	private static final Object synchObject = new Object();
	private static GatewayAgent instance;

	private static int conversationId = 0;
	private Hashtable conversationId2listener = new Hashtable();
	private Hashtable listener2fipaMessage = new Hashtable();

	private static Logger log = Logger.getLogger(GatewayAgent.class.getName());
	private String logFileName = "a.log.txt";
	private FileAppender logFile;
	private boolean isLogFileOn = false;
	private Logger mainLog = log.getLogger("com.whitestein.wsig");

	private static SLCodec codecSL = new SLCodec();
	private Hashtable callStore = new Hashtable();
	private static final String DF_LOCAL_NAME = "df";

	private DFMethodListener dfMethodListener = null;

	private AID dfAID = new AID(DF_LOCAL_NAME, AID.ISLOCALNAME);

	private GatewayAgentGui gui;


	public static final int EXIT_EVENT = 1001;
	public static final int CLOSE_GUI_EVENT = 1002;
	public static final int RESET_EVENT = 1003;
	public static final int SET_LOG_FILE_EVENT = 1004;
	public static final int SET_LOGGING_EVENT = 1005;
	public static final int START_AGENT_SERVER001_EVENT = 1006;
	public static final int START_AGENT_CLIENT033_EVENT = 1007;
	public static final int START_WS_SERVER01_EVENT = 1008;
	public static final int START_WS_CLIENT01_EVENT = 1009;
	public static final int WS_SELECTION_EVENT = 1010;
	public static final int AGENT_SELECTION_EVENT = 1011;
	public static final int START_AGENT_CLIENT_WITH_ARGUMENTS_EVENT = 1012;
	public static final int START_WS_REGISTRATION_FOR_FIND_PLACE_EVENT = 1013;
	public static final int START_WS_REGISTRATION_FOR_GOOGLE_EVENT = 1014;
	public static final String AGENT_NAME_GET_VERSION = "testAgentForGetVersion";
	public static final String SERVICE_GET_VERSION = "getVersion";
	public static final String SERVICE_EMPTY_ARGS = "";
	public static final String SERVICE_GOOGLE_SEARCH = "doGoogleSearch";
	public static final String AGENT_NAME_GOOGLE_SEARCH = "testAgentForGoogle";
	public static final String SERVICE_GOOGLE_S_ARGS =
		"(xml-tag-key :xml-element (key " +
			Configuration.getInstance().getTestAmazonAccessKey() +
			") :xml-attributes (set ( property :name xsi:type :value xsd:string ))) (xml-tag-q :xml-element (q Foo) :xml-attributes (set ( property :name xsi:type :value xsd:string ))) (xml-tag-start :xml-element (start 0) :xml-attributes (set ( property :name xsi:type :value xsd:int ))) (xml-tag-maxResults :xml-element (maxResults 4) :xml-attributes (set ( property :name xsi:type :value xsd:int ))) (xml-tag-filter :xml-element (filter true) :xml-attributes (set ( property :name xsi:type :value xsd:boolean))) (xml-tag-restrict :xml-element (restrict) :xml-attributes (set ( property :name xsi:type :value xsd:string ))) (xml-tag-safeSearch :xml-element (safeSearch false) :xml-attributes (set ( property :name xsi:type :value xsd:boolean ))) (xml-tag-lr :xml-element (lr) :xml-attributes (set ( property :name xsi:type :value xsd:string ))) (xml-tag-ie :xml-element (ie latin1) :xml-attributes (set ( property :name xsi:type :value xsd:string ))) (xml-tag-oe :xml-element (oe latin1) :xml-attributes (set ( property :name xsi:type :value xsd:string )))";
	private Thread wsServer01 = null;
	private Thread wsClient01 = null;
	private Thread wsReg02FP = null;
	private Thread wsReg04G = null;

	private HashSet wsSelection = null;
	private HashSet agentSelection = null;
	private final Object syncObjectSel = new Object();
	private HTTPServer server;

	/**
	 * creates new GatewayAgent
	 */
	public GatewayAgent() {
		super();

		// creates a HTTPServer for a WS accessPoint
		try {
			int port = Configuration.getInstance().getHostPort();
			server = new HTTPServer(new ServerSocket(port));
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/**
	 * returns singleton instance.
	 * Running instance is returned.
	 *
	 * @return an instance
	 */
	public static GatewayAgent getInstance() {
		synchronized (synchObject) {
			while (null == instance) {
				log.debug(" instance is null, waiting");
				try {
					synchObject.wait(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return instance;
	}

	/**
	 * sends a FIPA message.
	 * The message is going to be changed.
	 *
	 * @param msg	  message to be sent
	 * @param listener listener for returned message
	 */
	public void send(FIPAMessage msg, ReturnMessageListener listener) {
		ACLMessage acl = msg.getACLMessage();
		fillACLParameters(acl);
		synchronized (conversationId2listener) {
			conversationId2listener.put(
				msg.getACLMessage().getConversationId(),
				listener);
		}
		synchronized (listener2fipaMessage) {
			listener2fipaMessage.put(
				listener,
				msg);
		}
		// testing print out
		log.debug(" WSIGS: ACL message created. " + SL0Helper.toString(acl));
		send(acl);
	}

	public void sendACL(ACLMessage acl) {
		// used by FIPAReturnMessageListener
		// create reply does not fill a sender
		log.debug(" a response is " + SL0Helper.toString(acl));
		send(acl);
	}

	/**
	 * removes a listener registered
	 *
	 * @param listener a listener registered
	 */
	public void removeReturnMessageListener(ReturnMessageListener listener) {
		FIPAMessage msg;
		synchronized (listener2fipaMessage) {
			msg = (FIPAMessage)
				listener2fipaMessage.get(listener);
			listener2fipaMessage.remove(listener);
		}
		ACLMessage cancel = msg.getACLMessage();
		cancel.setPerformative(ACLMessage.CANCEL);

		// waiting for cancel's response may produce a long time out
		// remove the listener now, then send the cancel
		String convId = cancel.getConversationId();
		synchronized (conversationId2listener) {
			listener = (ReturnMessageListener)
				conversationId2listener.get(convId);
			conversationId2listener.remove(convId);
		}
		send(cancel);
	}

	/**
	 * fills ACL mandatory parameters
	 *
	 * @param acl message to fill
	 */
	private void fillACLParameters(ACLMessage acl) {
		acl.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		acl.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		acl.setConversationId(getConversationId());
	}

	/**
	 * creates an unique conversation Id
	 *
	 * @return conversation Id
	 */
	private synchronized String getConversationId() {
		return "Query" + getName() + (new Date()).getTime();
	}

	/**
	 * sets up this agent
	 */
	protected void setup() {
		super.setup();
		registerOntologies();
		registerLanguagees();

		// initialize an instance
		try {
			synchronized (synchObject) {
				instance = this;
				dfMethodListener = Configuration.getInstance().getDFMethodListener();
				synchObject.notifyAll();
				log.info("WSIG's GatewayAgent is set up.   " + (null != instance));
			}
		} catch (Exception e) {
			log.debug(e);
		}

		// register into a df
		registerMe();

		// Subscribe to the DF
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.addProperties(new Property(WSIG_FLAG, "true"));
		template.addServices(sd);
		ACLMessage subscriptionMsg = DFService.createSubscriptionMessage(this, getDefaultDF(), template, null);
		addBehaviour(new SubscriptionInitiator(this, subscriptionMsg) {

			protected void handleInform(ACLMessage inform) {
				log.debug("Agent " + myAgent.getLocalName() + " - Notification received from DF." + inform.getContent());
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
					for (int i = 0; i < dfds.length; ++i) {
						AID agent = dfds[i].getName();
						Iterator services = dfds[i].getAllServices();
						if (services.hasNext()) {
							//Registration of an agent
							dfMethodListener.registerAction(myAgent, dfds[i], inform.getSender());
							manageListener(inform);
						} else {
							//Deregistration of an agent
							dfMethodListener.deregisterAction(dfds[i], inform.getSender());
						}
					}
				}
				catch (Exception e) {
					log.warn("Agent " + myAgent.getLocalName() + " - Error decoding DF notification. " + e);
					e.printStackTrace();
				}
			}
		});

		//add behaviour of the GatewayAgent
		this.addBehaviour(new CyclicBehaviour(this) {
			//NOT FIPA MANAGEMENT ONTOLOGY
			private MessageTemplate template = MessageTemplate.not(MessageTemplate.MatchOntology(FIPAManagementOntology.getInstance().getName()));

			//private MessageTemplate template = MessageTemplate.and(
			//		MessageTemplate.MatchPerformative(ACLMessage.INFORM),MessageTemplate.not(MessageTemplate.MatchSender(getDefaultDF())));
			public void action() {
				// receive all messages, do not ignore DFMessageTemplate

				ACLMessage msg = myAgent.receive(template);
				if (msg != null) {
					switch (msg.getPerformative()) {
						case ACLMessage.REQUEST:
							doFIPARequest(msg);
							break;
						case ACLMessage.CANCEL:
							doFIPACancel(msg);
							break;
						case ACLMessage.INFORM:
							try {
								log.debug("A request for WSIG:" + SL0Helper.toString(msg));
								manageListener(msg);
							} catch (Exception e) {
								log.error(e);
							}
							break;
						case ACLMessage.REFUSE:
						case ACLMessage.FAILURE:
						case ACLMessage.AGREE:
						case ACLMessage.NOT_UNDERSTOOD:
							// TODO
							log.warn("Unexpected message");
							break;
						default:
							// not in fipa-request protocol
							doNoFipaRequest(msg);
					}

				} else {
					block();
				}
			}
		});

		//
		// create GUI, when -gui switch is presented
		//
		if (isGuiSwitchOn()) {
			gui = new GatewayAgentGui(this);
			gui.showMeTheFirstTime();
		}
	}


	private void registerOntologies() {

		//register at least all necessary ontologies
		getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		getContentManager().registerOntology(JADEManagementOntology.getInstance());      //????
	//	getContentManager().registerOntology(DFAppletOntology.getInstance());

		String[] ontologies = Configuration.getInstance().getOntologies();
		for (String ontology : ontologies) {
			try {
				Class ontoClass = Class.forName(ontology);
				Method method  = ontoClass.getMethod("getInstance", new Class[0]);
				Ontology onto =  (Ontology)method.invoke(null,null);

				getContentManager().registerOntology(onto);
			} catch (Exception ex) {
				ex.printStackTrace();
			}


		}

	}

	private void registerLanguagees() {
		getContentManager().registerLanguage(codecSL);
		String[] languages = Configuration.getInstance().getLanguages();
		for (String language : languages) {
			try {
				Class languageClass = Class.forName(language);
				Codec codec  = (Codec)languageClass.newInstance();
				getContentManager().registerLanguage(codec);
			} catch (Exception ex) {
				ex.printStackTrace();
			}


		}

	}




	private void manageListener(ACLMessage inform) {
		ReturnMessageListener listener;
		String convId = inform.getConversationId();
		synchronized (conversationId2listener) {
			listener = (ReturnMessageListener)
				conversationId2listener.get(convId);
			conversationId2listener.remove(convId);
		}
		if (listener != null) {
			FIPAMessage fipa = new FIPAMessage(inform);
			fipa.setResponse(true);
			listener.setReturnedMessage(fipa);
			log.debug("listener invoked for conversationId " + convId);
		} else {
			log.debug("listener is null for conversationId " + convId);
		}
	}

	/**
	 * tests a presence of gui switch
	 *
	 * @return true if "-gui" switch is occured
	 */
	private boolean isGuiSwitchOn() {
		Object[] args = getArguments();
		if (args != null) {
			int i = 0;
			int len = args.length;
			while (i < len && ! "-gui".equalsIgnoreCase(args[i].toString())) {
				i ++;
			}
			return i < len;
		}
		return false;
	}

	/**
	 * registers a WSIG into a DF
	 */
	private void registerMe() {
		//register itself to a DF
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(this.getAID());

		dfad.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
		dfad.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		ServiceDescription sd = new ServiceDescription();
		sd.setType(AGENT_TYPE);
		sd.setName(getLocalName());
		dfad.addServices(sd);
		try {
			DFService.register(this, dfad);
		} catch (Exception e) {
			log.error("Error during DF registration");
			e.printStackTrace();
		}
	}

	/**
	 * takes down this agent.
	 * A configuration used is stored.
	 */
	protected void takeDown() {
		if (gui != null) {
			gui.exit();
			gui = null;
			shutDownAgents();
		}

		Configuration.store();

		try {
			DFService.deregister(this, getDefaultDF());
		} catch (Exception e) {
			log.error("Error during DF registration");
			e.printStackTrace();
		}

		log.debug("A gateway is taken down now.");
	}


	/**
	 * sends inform-done for a cancel operation.
	 * Nothing special is performed, only a requester is informed.
	 *
	 * @param acl a request for a cancel
	 */
	private void doFIPACancel(ACLMessage acl) {
		// cancel a connection into a Web Service

		// remove a call from a structure
		Call call = removeFromCallStore(acl.getSender(), acl.getConversationId());
		if (null != call) {
			call.close();
		} else {
			return;
		}

		// send an acl
		if (! call.isReceived()) {
			send(SL0Helper.createInformDoneForCancel(acl));
		} else {
			// an answer is already communicated
		}
	}

	/**
	 * serves a request for a service.
	 *
	 * @param acl a request message
	 */
	private void doFIPARequest(ACLMessage acl) {
		FIPAMessage msg = new FIPAMessage(acl);
		// a message is a request, not a response
		msg.setResponse(false);

		//  extract FIPAServiceIdentificator
		//  find a ServedOperation
		ServedOperation so = ServedOperationStore.getInstance().find(
			getFIPAServiceId(msg));

		if (null == so) {
			// I can not serve
			log.debug("Operation is not served.");
			ACLMessage r = acl.createReply();
			r.setPerformative(ACLMessage.NOT_UNDERSTOOD);
			r.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
			r.setContent(
				"( " + SL0Helper.toStringAclAsAction(acl) + "\n"
					+ "  (error-message \"The service is not provided.\")) )");
			send(r);
			return;
		}

		// create connection into a Web Service
		//  perform call
		Call call = so.createCall();
		try {
			log.debug(" WSIG is called by an agent now.");
			call.setMessage(msg);
			ReturnMessageListener listener = new FIPAReturnMessageListener(acl);
			call.setReturnMessageListener(listener);
			// store a call for a cancel
			storeACall(acl.getSender(), acl.getConversationId(), call);
			call.invoke();
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * creates and sends not understood for bad performative.
	 * A good performative is in the fipa-request protocol [FIPA SC00026H].
	 * They are: request, refuse, agree, failure, inform, cancel and not-understood.
	 *
	 * @param acl message received
	 */
	private void doNoFipaRequest(ACLMessage acl) {
		ACLMessage r = acl.createReply();
		r.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		r.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		r.setContent(
			"( " + SL0Helper.toStringAclAsAction(acl) + "\n" +
				"  (error-message \"Performative is not in the fipa-request protocol.\")) )");
		send(r);
	}


	/**
	 * gives a FIPA service's identificator
	 *
	 * @param msg a message to identify
	 * @return an identificator
	 */
	private FIPAServiceIdentificator getFIPAServiceId(FIPAMessage msg) {
		// has a meaning in a request's message

		AbsContentElement ac;
		AbsObject action;
		String service = "";
		try {
			ac = codecSL.decode(
				BasicOntology.getInstance(),
				msg.getACLMessage().getContent());
			if (SL0Vocabulary.ACTION.compareToIgnoreCase(ac.getTypeName()) == 0) {
				action = FIPASL0ToSOAP.getActionSlot(ac);
				service = action.getTypeName();
			}
		} catch (Exception e) {
			log.error(e);
		}
		return new FIPAServiceIdentificator(
			(AID) msg.getACLMessage().getAllReceiver().next(),
			service);
	}

	/**
	 * stores a call into a storage
	 *
	 * @param originator	 an originator of the call
	 * @param conversationId a conversation identificator of the call
	 * @param call		   the call to be stored
	 */
	private void storeACall(AID originator, String conversationId, Call call) {
		synchronized (callStore) {
			Hashtable convIds = (Hashtable) callStore.get(originator);
			if (null == convIds) {
				convIds = new Hashtable();
				callStore.put(originator, convIds);
			}
			convIds.put(conversationId, call);
		}
	}

	/**
	 * removes and gives a call identified by a originator and a conversation identificator
	 *
	 * @param originator	 an originator of the call
	 * @param conversationId a conversation identificator of the call
	 * @return the call to be erased
	 */
	Call removeFromCallStore(AID originator, String conversationId) {
		synchronized (callStore) {
			Hashtable convIds = (Hashtable) callStore.get(originator);
			if (null != convIds) {
				Call call = (Call) convIds.get(conversationId);
				convIds.remove(conversationId);
				return call;
			}
			return null;
		}
	}

	/**
	 * adds an operation into an DF agent description's structure.
	 * The operation is offered by gateway.
	 *
	 * @param op operation added
	 */
	public synchronized void addToDFAgentDescription(ServedOperation op) {
		//add a service
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.addServices(createSDforOperation(op));
		sendModifyDF(dfad);
	}

	/**
	 * removes an operation from an DF agent description's structure.
	 * The operation is not offered by gateway anymore.
	 *
	 * @param op operation removed
	 */
	public synchronized void removeFromDFAgentDescription(ServedOperation op) {
		// remove a service
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.removeServices(createSDforOperation(op));
		sendModifyDF(dfad);
	}

	/**
	 * creates a ServiceDescription for an operation
	 *
	 * @param op operation
	 * @return description created
	 */
	private ServiceDescription createSDforOperation(ServedOperation op) {
		//prepare a FIPA's service
		ServiceDescription sd;
		sd = new ServiceDescription();
		sd.setName(op.getOperationID().getFIPAServiceIdentificator().getServiceName());
		sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
		sd.addProtocols(FIPANames.InteractionProtocol.FIPA_REQUEST);
		sd.setType(Configuration.VOID_TYPE);

		Property p = new Property(Configuration.WEB_SERVICE + ".operation",
			op.getOperationID().getUDDIOperationIdentificator().getWSDLOperation());
		sd.addProperties(p);
		p = new Property(Configuration.WEB_SERVICE + ".accessPoint",
			op.getOperationID().getUDDIOperationIdentificator().getAccessPoint());
		sd.addProperties(p);

		//TO DO... verify for deletion
		/*p = new Property("type", "(set " + Configuration.WEB_SERVICE + ")");
		sd.addProperties(p);*/

		return sd;
	}

	/**
	 * sends a modify request into a DF
	 *
	 * @param dfad agent description
	 */
	private void sendModifyDF(DFAgentDescription dfad) {
		//prepare an ACL
		/*ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(dfAID);
		fillACLParameters(msg);
		msg.setOntology(FIPAManagementVocabulary.NAME);*/

		// create modification's action
		/*Modify modify = new Modify();
		modify.setDescription(dfad);
		Action action = new Action(dfAID, modify);*/

		try {
			//getContentManager().fillContent(msg, action);
			//send(msg);
			DFService.modify(this, dfad);
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * processes events from GUI
	 *
	 * @param event an GUI event
	 */
	protected void onGuiEvent(GuiEvent event) {
		switch (event.getType()) {
			case EXIT_EVENT:
				doDelete();
				break;
			case CLOSE_GUI_EVENT:
				if (gui != null) {
					gui.exit();
					gui = null;
				}
				break;
			case RESET_EVENT:
				resetGateway();
				break;
			case SET_LOG_FILE_EVENT:
				setLogFileName((File) event.getParameter(0));
				break;
			case SET_LOGGING_EVENT:
				Boolean val =
					(Boolean) event.getParameter(0);
				setLogging(val.booleanValue());
				break;
			case START_AGENT_SERVER001_EVENT:
				startAgent("agentServer",
					"com.whitestein.wsig.test.TestAgentServer",
					new Object[0]);
				break;
			case START_AGENT_CLIENT033_EVENT:
				startAgent("agentClient033",
					"com.whitestein.wsig.test.TestAgent033",
					new Object[0]);
				break;
			case START_WS_SERVER01_EVENT:
				if (null != wsServer01 && wsServer01.isAlive()) {
					break;
				}
				wsServer01 = startClassMain(
					"com.whitestein.wsig.test.TestSOAPServer",
					new String[0]);
				break;
			case START_WS_CLIENT01_EVENT:
				if (null != wsClient01 && wsClient01.isAlive()) {
					break;
				}
				wsClient01 = startClassMain(
					"com.whitestein.wsig.test.TestSOAPClient",
					new String[0]);
				break;
			case WS_SELECTION_EVENT:
				wsSelectionEvent((Object[]) event.getParameter(0));
				break;
			case AGENT_SELECTION_EVENT:
				agentSelectionEvent((Object[]) event.getParameter(0));
				break;
			case START_AGENT_CLIENT_WITH_ARGUMENTS_EVENT:
				startAgent((String) event.getParameter(0),
					"com.whitestein.wsig.test.TestAgentWithArgs",
					(Object[]) event.getParameter(1));
				break;
			case START_WS_REGISTRATION_FOR_FIND_PLACE_EVENT:
				if (null != wsReg02FP && wsReg02FP.isAlive()) {
					break;
				}
				wsReg02FP = startClassMain(
					"com.whitestein.wsig.test.TestFindPlaceRegistration",
					new String[0]);
				break;
			case START_WS_REGISTRATION_FOR_GOOGLE_EVENT:
				if (null != wsReg04G && wsReg04G.isAlive()) {
					break;
				}
				wsReg04G = startClassMain(
					"com.whitestein.wsig.test.TestGoogleRegistration",
					new String[0]);
				break;
		}
	}

	/**
	 * sets logging on/off.
	 * If a log file is not set, then the method does nothing.
	 *
	 * @param turnOn true, if log's informations are sent into a log file
	 */
	private void setLogging(boolean turnOn) {
		isLogFileOn = turnOn;
		if (null == logFile) {
			log.debug("Value of logFile is null.");
			return;
		}
		if (turnOn) {
			mainLog.addAppender(logFile);
		} else {
			mainLog.removeAppender(logFile);
		}
	}

	/**
	 * sets a log file
	 *
	 * @param aFile a new log file
	 */
	private void setLogFileName(File aFile) {
		if (null == aFile) {
			log.debug(" Log file posted by GUI is null.");
			return;
		}
		logFileName = aFile.getAbsolutePath();
		log.debug(" A new file for a log is : " + logFileName);
		if (null != logFile && mainLog.isAttached(logFile)) {
			mainLog.removeAppender(logFile);
		}
		try {
			logFile = new FileAppender(
				new PatternLayout("%-5p: %c : %m%n"),
				logFileName);
		} catch (IOException ioe) {
			log.debug(" A problem is occured during a logFile's creation ");
			logFile = null;
		}
		if (null != logFile && isLogFileOn) {
			mainLog.addAppender(logFile);
		}
	}

	/**
	 * resets the gateway
	 */
	private void resetGateway() {
		// TODO
		// deregister all services from DF
		// deregister all operations from UDDI
		// clear ServedOperationStore
		shutDownAgents();
	}

	//TO VERIFY
	private void shutDownAgents() {
		// code is not in LEAP, MIDP
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl(false);
		ContainerController cc = rt.createAgentContainer(profile);
		AgentController agentContr = null;
		try {
			// test if one exists
			agentContr = cc.getAgent("agentServer");
			agentContr.kill();
		} catch (ControllerException ce) {
			// does not exist
		}

		try {
			// test if one exists
			agentContr = cc.getAgent("agentClient033");
			agentContr.kill();
		} catch (ControllerException ce) {
			// does not exist
		}
	}

	/**
	 * starts an agent
	 *
	 * @param name	  a name of the agent
	 * @param className a class of the agent
	 * @param args	  arguments, which are passed to agent
	 */
	private void startAgent(String name, String className, Object[] args) {
		// code is not in LEAP, MIDP
		Runtime rt = Runtime.instance();
		Profile profile = new ProfileImpl(false);
		ContainerController cc = rt.createAgentContainer(profile);
		AgentController agentContr = null;
		try {
			// test if one exists
			agentContr = cc.getAgent(name);
			return;
		} catch (ControllerException ce) {
			// does not exist
		}
		try {
			// create a new agent
			agentContr = cc.createNewAgent(name, className, args);
			agentContr.start();
		} catch (StaleProxyException spe) {
			log.debug(spe);
		}
	}


	/**
	 * starts an application
	 *
	 * @param className a class of the application
	 * @param args	  arguments, which are passed
	 */
	private Thread startClassMain(String className, final String[] args) {
		Thread spirit = null;
		Class[] argsClass = {String[].class};
		ClassLoader cl = null;
		try {
			cl = ClassLoader.getSystemClassLoader();
		} catch (SecurityException se) {
			log.debug("Problems to get the ClassLoader." + se);
			return spirit;
		} catch (IllegalStateException ise) {
			log.debug("Problems to get the ClassLoader." + ise);
			return spirit;
		}
		Class aClass = null;
		try {
			aClass = cl.loadClass(className);
		} catch (ClassNotFoundException cnfe) {
			log.debug("Problems to load a class.");
			return spirit;
		}
		try {
			final Method m =
				aClass.getMethod("main", argsClass);
			spirit = new Thread() {
				private Method met2 = m;
				private Object[] args2 = {args};

				public void run() {
					try {
						this.met2.invoke(null, args2);
					} catch (IllegalAccessException iae) {
						log.debug("Problems to invoke a method." + iae);
					} catch (IllegalArgumentException ige) {
						log.debug("Problems to invoke a method." + ige);
					} catch (InvocationTargetException ite) {
						log.debug("Problems to invoke a method." + ite);
					} catch (NullPointerException npe) {
						log.debug("Problems to invoke a method." + npe);
					}
				}
			};
		} catch (NoSuchMethodException nsme) {
			log.debug("Problems to get a method." + nsme);
			return spirit;
		} catch (NullPointerException npe) {
			log.debug("Problems to get a method." + npe);
			return spirit;
		} catch (SecurityException se) {
			log.debug("Problems to get a method." + se);
			return spirit;
		}
		if (null != spirit) {
			spirit.start();
		}
		return spirit;
	}

	/**
	 * processes a WS selection's event
	 */
	private void wsSelectionEvent(Object[] sel) {
		ComboBoxItem item;
		ServedOperation so;
		synchronized (syncObjectSel) {
			wsSelection = new HashSet();
			for (int k = 0; k < sel.length; k ++) {
				item = (ComboBoxItem) sel[k];
				so = item.getServedOperation();
				if (null == so) {
					wsSelection = null;
					return;
				}
				wsSelection.add(so);
			}
		}
	}

	/**
	 * processes an agents selection's event
	 */
	private void agentSelectionEvent(Object[] sel) {
		ComboBoxItem item;
		ServedOperation so;
		synchronized (syncObjectSel) {
			agentSelection = new HashSet();
			for (int k = 0; k < sel.length; k ++) {
				item = (ComboBoxItem) sel[k];
				so = item.getServedOperation();
				if (null == so) {
					agentSelection = null;
					return;
				}
				agentSelection.add(so);
			}
		}
	}

	/**
	 * tests, if an operation is selected
	 */
	private boolean isWsSelected(ServedOperation so) {
		synchronized (syncObjectSel) {
			if (null == wsSelection) {
				return true;
			}
			return wsSelection.contains(so);
		}
	}

	/**
	 * tests, if an operation is selected
	 */
	private boolean isAgentSelected(ServedOperation so) {
		synchronized (syncObjectSel) {
			if (null == agentSelection) {
				return true;
			}
			return agentSelection.contains(so);
		}
	}

	/**
	 * appends a text's message into log
	 * It is for a GUI's manipulation only.
	 */
	public void addMessageToLog(ServedOperation so, CalledMessage msg) {
		synchronized (syncObjectSel) {
			if (null == gui || null == so || null == msg) {
				log.debug("A null is appeared in addMessageToLog()");
				return;
			}
			String text = msg.toString();

			EndPoint ep = so.getEndPoint();
			if (WSEndPoint.TYPE == ep.getType()) {
				if (isWsSelected(so)) {
					gui.addWsLog(text);
					log.debug(" Into WS log: " + text);
				}
			}
			if (FIPAEndPoint.TYPE == ep.getType()) {
				if (isAgentSelected(so)) {
					gui.addAgentLog(text);
					log.debug(" Into agents' log: " + text);
				}
			}
		}
	}

	/**
	 * adds an operation to a list of ones logged
	 * It is for a GUI's manipulation only.
	 */
	public void addOperationForLog(ServedOperation so) {
		synchronized (syncObjectSel) {
			if (null == gui || null == so) {
				return;
			}
			EndPoint ep = so.getEndPoint();
			if (WSEndPoint.TYPE == ep.getType()) {
				UDDIOperationIdentificator uddiId =
					so.getOperationID().getUDDIOperationIdentificator();
				gui.addOperationToWsLog(new ComboBoxItem(
					"" + uddiId.getWSDLOperation()
						+ " at " + uddiId.getAccessPoint(),
					so));
			}
			if (FIPAEndPoint.TYPE == ep.getType()) {
				FIPAServiceIdentificator fId =
					so.getOperationID().getFIPAServiceIdentificator();
				gui.addOperationToAgentsLog(new ComboBoxItem(
					"" + fId.getServiceName()
						+ " at " + fId.getAgentID(),
					so));
			}
		}
	}

	/**
	 * removes an operation from a list of ones logged.
	 * It is for a GUI's manipulation only.
	 */
	public void removeOperationForLog(ServedOperation so) {
		synchronized (syncObjectSel) {
			if (null == gui || null == so) {
				return;
			}
			EndPoint ep = so.getEndPoint();
			if (WSEndPoint.TYPE == ep.getType()) {
				gui.removeOperationFromWsLog(so);
			}
			if (FIPAEndPoint.TYPE == ep.getType()) {
				gui.removeOperationFromAgentsLog(so);
			}
		}
	}

}
