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

package com.tilab.wsig.agent;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.util.Logger;
import jade.wrapper.gateway.GatewayAgent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Level;

import org.uddi4j.util.ServiceKey;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.WSIGConstants;
import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.store.WSIGStore;
import com.tilab.wsig.uddi.UDDIManager;
import com.tilab.wsig.wsdl.JadeToWSDL;
import com.tilab.wsig.wsdl.WSDLConstants;


public class WSIGAgent extends GatewayAgent implements WSIGConstants {

	private static final long serialVersionUID = 3815496986569126415L;

	private static Logger logger = Logger.getMyLogger(WSIGAgent.class.getName());

	private WSIGStore wsigStore;
	private UDDIManager uddiManager;

	protected void setup() {
		super.setup();

		WSIGConfiguration wsigConfiguration = WSIGConfiguration.getInstance();
		
		// Set non-standard archive scheme called “wsjar" 
		System.setProperty("org.eclipse.emf.common.util.URI.archiveSchemes", "wsjar wszip jar zip");

		// Set soap message factory library (solve jboss problem)
		System.setProperty("javax.xml.soap.MessageFactory", "org.apache.axis.soap.MessageFactoryImpl"); 
		
		logger.log(Level.INFO, "Agent "+getLocalName()+" - starting...");

		// Get agent arguments
		Object[] args = getArguments();
		for (int i = 0; i < args.length; i++) {
			logger.log(Level.INFO, "arg[" + i + "]" + args[i]);
		}

		// Verify if wsigStore is passed as agent parameter
		if (args.length == 1 && (args[0] instanceof WSIGStore)) {
			wsigStore = (WSIGStore)args[0];
		}
		if (wsigStore == null) {
			wsigStore = new WSIGStore();
		}

		// Create UDDIManager
		if (wsigConfiguration.isUddiEnable()) {
			uddiManager = new UDDIManager();
		}

		// Register ontology & language
		getContentManager().registerOntology(FIPAManagementOntology.getInstance());
		getContentManager().registerOntology(JADEManagementOntology.getInstance());
		getContentManager().registerLanguage(new SLCodec());
		
		// Manage log file manager
		if (wsigConfiguration.isLogManagerEnable()) {
			if (wsigConfiguration.isJadeMiscPresent()) {
				try {
					String fileManagerName = wsigConfiguration.getLogManagerName();
					String fileManagerRoot = wsigConfiguration.getLogManagerRoot();
					Integer fileManagerDownloadBlockSize = wsigConfiguration.getLogManagerDownloadBlockSize();
					
					Class cfmabClass = Class.forName("jade.misc.CreateFileManagerAgentBehaviour");
					Class[] cfmabConstructorArgsType = new Class[] { String.class, String.class, Integer.class };
					Constructor cfmabConstructor = cfmabClass.getConstructor(cfmabConstructorArgsType);
					Object[] cfmabConstructorArgs = new Object[] { fileManagerName, fileManagerRoot, fileManagerDownloadBlockSize };
					
					Behaviour cfmab = (Behaviour)cfmabConstructor.newInstance(cfmabConstructorArgs);
					addBehaviour(cfmab);
				} catch(Exception e) {
					logger.log(Level.SEVERE, "Agent "+getLocalName()+" - Error creating CreateFileManagerAgentBehaviour", e);
				}
			} else {
				logger.log(Level.WARNING, "Agent "+getLocalName()+" - Log manager enabled bat jadeMisc.jar not present in WSIG classpath");
			}
		}
		
		// Register into a DF
		registerIntoDF();

		// Subscribe to the DF
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.addProperties(new Property(WSIG_FLAG, "true"));
		template.addServices(sd);
		ACLMessage subscriptionMsg = DFService.createSubscriptionMessage(this, getDefaultDF(), template, null);
		addBehaviour(new SubscriptionInitiator(this, subscriptionMsg) {

			protected void handleInform(ACLMessage inform) {
				logger.log(Level.FINE, "Agent "+getLocalName()+" - Notification received from DF ("+inform.getContent()+")");
				try {
					DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
					for (int i = 0; i < dfds.length; ++i) {
						Iterator services = dfds[i].getAllServices();
						if (services.hasNext()) {
							// Registration of an agent
							registerAgent(dfds[i]);
						} else {
							// Deregistration of an agent
							deregisterAgent(dfds[i]);
						}
					}
				}
				catch (Exception e) {
					logger.log(Level.WARNING, "Agent "+myAgent.getLocalName() + " - Error processing DF notification", e);
				}
			}
		});

		logger.log(Level.INFO, "Agent "+getLocalName()+" - started!");
	}

	public WSIGStore getWSIGStore() {
		return wsigStore;
	}

	private synchronized void registerAgent(DFAgentDescription dfad) throws Exception {

		logger.log(Level.INFO, "Start wsig registration from agent: " + dfad.getName());

		// Loop all services of agent
		ServiceDescription sd;
		WSIGService wsigService;

		AID agentId = dfad.getName();
		Iterator it = dfad.getAllServices();
		while (it.hasNext()) {
			sd = (ServiceDescription) it.next();

			// Create wsdl & wsig service
			wsigService = createWSIGService(agentId, sd);

			if (wsigService != null) {
				// Register new service
				registerService(wsigService);
			}
		}

		logger.log(Level.INFO, "End wsig registration from agent: " + dfad.getName());
	}

	private void registerService(WSIGService wsigService) throws Exception {
			logger.log(Level.INFO, "Create new wsig service: "+wsigService.toString());

			// Register wsigService into UDDI
			if (uddiManager != null) {
				ServiceKey uddiServiceKey = uddiManager.UDDIRegister(wsigService);
				wsigService.setUddiServiceKey(uddiServiceKey);
			}

			// Store wsigService into WSIGStore
			wsigStore.addService(wsigService.getServiceName(), wsigService);
	}

	private synchronized void deregisterAgent(DFAgentDescription dfad) throws Exception {

		logger.log(Level.INFO, "Start wsigs's deregistration from agent: " + dfad.getName());

		WSIGService wsigService;

		AID agentId = dfad.getName();
		Iterator<WSIGService> it = wsigStore.getServices(agentId).iterator();
		while (it.hasNext()) {
			wsigService = it.next();

			// Deregister service
			deregisterService(wsigService);
		}

		logger.log(Level.INFO, "End wsigs's deregistration from agent: " + dfad.getName());
	}

	private void deregisterService(WSIGService wsigService) throws Exception {

		String serviceName = wsigService.getServiceName();

		logger.log(Level.INFO, "Remove wsig service "+serviceName);

		// DeRegister wsigService from UDDI
		try {
			if (uddiManager != null) {
				uddiManager.UDDIDeregister(wsigService);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error removing service from UDDI", e);
		}

		// Remove wsigService from WSIGStore
		wsigStore.removeService(serviceName);
	}

	private boolean isWSIGService(ServiceDescription sd) {

		Property p = null;
		Iterator it = sd.getAllProperties();
		while (it.hasNext()) {
			p = (Property) it.next();

			if (WSIGConstants.WSIG_FLAG.equalsIgnoreCase(p.getName()) && p.getValue().toString().equals("true")) {
				return true;
			}
		}
		return false;
	}

	private Boolean isHierarchicalComplexTypeEnable(ServiceDescription sd) {

		Property p = null;
		Iterator it = sd.getAllProperties();
		while (it.hasNext()) {
			p = (Property) it.next();

			if (WSIGConstants.WSIG_HIERARCHICAL_TYPE.equalsIgnoreCase(p.getName())) {
				return p.getValue().toString().equals("true");
			}
		}
		return null;
	}
	
	private String getServicePrefix(ServiceDescription sd) {

		Property p = null;
		Iterator it = sd.getAllProperties();
		while (it.hasNext()) {
			p = (Property) it.next();

			if (WSIGConstants.WSIG_PREFIX.equalsIgnoreCase(p.getName()) && !p.getValue().toString().equals("")) {
				return p.getValue().toString()+WSDLConstants.SEPARATOR;
			}
		}
		return "";
	}

	private WSIGService createWSIGService(AID aid, ServiceDescription sd) throws Exception {

		// Get service prefix & name
		String servicePrefix = getServicePrefix(sd);
		String serviceName = servicePrefix + sd.getName();

		// Verify if is a wsig service
		if (!isWSIGService(sd)) {
			logger.log(Level.INFO, "Service "+serviceName+" discarded (no wsig service)");
			return null;
		}

		// Verify if the service is already registered
		if (wsigStore.isServicePresent(serviceName)) {
			logger.log(Level.INFO, "Service "+serviceName+" of agent "+aid.getName()+" is already registered");
			return null;
		}
		
		logger.log(Level.INFO, "Managing service "+serviceName);

		// Get ontology
		// FIX-ME: elaborate only first ontology
		String ontoName = null;
		Iterator ontoIt = sd.getAllOntologies();
		if (ontoIt.hasNext()) {
			ontoName = (String)ontoIt.next();
		}
		if (ontoName == null) {
			logger.log(Level.INFO, "Service "+serviceName+" of agent "+aid.getName()+" do not have any ontology registered. Discard it.");
			return null;
		}
		
		// Get ontology className
		String ontoClassname = getOntologyClassName(sd, ontoName);
		if (ontoClassname == null) {
			logger.log(Level.WARNING, "Ontology "+ontoName+" for service "+serviceName+" not present in ServiceDescriptor or WSIG configuration file. Discard service.");
			return null;
		}

		// Get ontology class 
		Class ontoClass;
		try {
			ontoClass = Class.forName(ontoClassname);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Ontology class "+ontoClassname+" for service "+serviceName+" can not be loaded. Discard service.", e);
			return null;
		}

		// Get ontology instance
		Ontology onto = null;
		try {
			// Try to create by constructor
			onto = (Ontology)ontoClass.newInstance();
		} catch (Exception e) {
			try {
				// Try to create by getInstance() method 
				Method getInstanceMethod = ontoClass.getMethod("getInstance", null);
				onto = (Ontology)getInstanceMethod.invoke(null, null);
			} catch (Exception e1) {
				logger.log(Level.WARNING, "Ontology class "+ontoClassname+" for service "+serviceName+" can not be instantiated. Discard service.", e);
				return null;
			}
		}
		
		// Register new onto in agent
		getContentManager().registerOntology(onto);

		// Get mapper class
		Class mapperClass = null;
		String mapperClassName = getMapperClassName(sd);
		if (mapperClassName != null) {
			try {
				mapperClass = Class.forName(mapperClassName);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Mapper class "+mapperClassName+" for service "+serviceName+" can not be loaded. Discard service.", e);
				return null;
			}
		}

		// Get hierarchicalComplexType flag
		Boolean hierarchicalComplexType = isHierarchicalComplexTypeEnable(sd);
		if (hierarchicalComplexType == null) {
			hierarchicalComplexType = WSIGConfiguration.getInstance().isHierarchicalComplexTypeEnable();
		}
		
		// Create new WSIGService
		WSIGService wsigService = new WSIGService();
		wsigService.setServiceName(serviceName);
		wsigService.setServicePrefix(servicePrefix);
		wsigService.setAid(aid);
		wsigService.setAgentOntology(onto);
		wsigService.setMapperClass(mapperClass);
		wsigService.setHierarchicalComplexType(hierarchicalComplexType);

		// Create wsdl
		JadeToWSDL jadeToWSDL = new JadeToWSDL();  
		jadeToWSDL.createWSDL(wsigService);
		
		return wsigService;
	}

	private String getMapperClassName(ServiceDescription sd) {
		Iterator it = sd.getAllProperties();
		while (it.hasNext()) {
			Property p = (Property) it.next();
			if (WSIGConstants.WSIG_MAPPER.equalsIgnoreCase(p.getName())) {
				return (String)p.getValue();
			}
		}

		return null;
	}

	private String getOntologyClassName(ServiceDescription sd, String ontoName) {
		String ontoClassname = null;
		
		// Check in ServiceDescription properties
		Iterator it = sd.getAllProperties();
		while (it.hasNext()) {
			Property p = (Property) it.next();
			if (WSIGConstants.WSIG_ONTOLOGY_CLASSNAME.equalsIgnoreCase(p.getName())) {
				ontoClassname = (String)p.getValue();
			}
		}

		// Check in configuration file
		if (ontoClassname == null) {
			ontoClassname = WSIGConfiguration.getInstance().getOntoClassname(ontoName);
		}
		
		return ontoClassname;
	}
	
	private void registerIntoDF() {
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
			logger.log(Level.SEVERE, "Agent "+getLocalName()+" - Error during DF registration", e);
		}
	}

	protected void takeDown() {

		// Deregister all service
		try {
			WSIGService wsigService;
			Iterator<WSIGService> it = wsigStore.getAllServices().iterator();
			while(it.hasNext()) {
				wsigService = it.next();
				deregisterService(wsigService);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Agent "+getLocalName()+" - Error during service deregistration", e);
		}

		// Deregister WSIG agent
		try {
			DFService.deregister(this, getDefaultDF());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Agent "+getLocalName()+" - Error during DF deregistration", e);
		}

		super.takeDown();
		logger.log(Level.INFO, "Agent "+getLocalName()+" - Taken down now");
	}
}
