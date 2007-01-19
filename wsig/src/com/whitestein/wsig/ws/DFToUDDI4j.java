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
package com.whitestein.wsig.ws;

import jade.content.onto.Ontology;
import jade.content.onto.BasicOntology;
import jade.content.schema.*;
import jade.content.schema.facets.CardinalityFacet;
import jade.content.schema.facets.TypedAggregateFacet;
import jade.content.ContentManager;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.List;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.File;

import org.apache.log4j.Logger;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.OverviewDoc;
import org.uddi4j.datatype.OverviewURL;
import org.uddi4j.datatype.binding.AccessPoint;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BindingDetail;
import org.uddi4j.response.DispositionReport;
import org.uddi4j.response.Result;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.response.ServiceInfo;
import org.uddi4j.response.ServiceInfos;
import org.uddi4j.response.ServiceList;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.KeyedReference;
import org.uddi4j.util.ServiceKey;
import org.uddi4j.util.TModelBag;
import org.uddi4j.util.TModelKey;
import org.xml.sax.InputSource;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDModelGroup;
import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.WSIGConstants;
import com.whitestein.wsig.fipa.DFMethodListener;
import com.whitestein.wsig.fipa.FIPAEndPoint;
import com.whitestein.wsig.fipa.FIPAServiceIdentificator;
import com.whitestein.wsig.struct.OperationID;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.ibm.wsdl.*;


import javax.wsdl.*;
import javax.wsdl.xml.WSDLWriter;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;


/**
 * This class provides a UDDI connection for an agent.
 * The agent communicates by FIPA messages to the WSIGS's DF.
 *
 * @author jna
 */
public class DFToUDDI4j implements DFMethodListener {

	private static DFToUDDI4j anInstance;
	private Logger log = Logger.getLogger(DFToUDDI4j.class.getName());

	private Hashtable aidToService = new Hashtable();
	//private Hashtable aidToConcept = new Hashtable();
	private ServedOperationStore operationStore = ServedOperationStore.getInstance();
	private UDDIProxy uddiProxy;
	private String businessKey;
	private String userName;
	private String password;
	private static Hashtable types = new Hashtable();

	public static final String xsd = "http://www.w3.org/2001/XMLSchema";


	static {
		types.put(BasicOntology.FLOAT, "float");
		types.put(BasicOntology.INTEGER, "int");
		types.put(BasicOntology.STRING, "string");
		types.put(BasicOntology.BOOLEAN, "boolean");
		types.put(BasicOntology.DATE, "dateTime");
		types.put(BasicOntology.BYTE_SEQUENCE, "byte");//verify it!!!


	}


	/**
	 * creates new DFToUDDI4j instance
	 */
	public DFToUDDI4j() {
		try {
			setupUDDI4j();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * gives an instance.
	 * The singleton instance is provided.
	 *
	 * @return an instance
	 */
	public static synchronized DFToUDDI4j getInstance() {
		if (anInstance == null) {
			anInstance = new DFToUDDI4j();
		}
		return anInstance;
	}

	/**
	 * creates served operations.
	 * For each FIPA operation new ServedOperation instance is created.
	 *
	 * @param dfad agent's registration structure
	 * @return collection of served operations
	 */
	private synchronized Collection createServedOperations(Agent agent, DFAgentDescription dfad) {
		Collection coll = new ArrayList();
		ServiceDescription sd;
		FIPAServiceIdentificator fipaSId;
		UDDIOperationIdentificator uddiOId;
		Iterator it = dfad.getAllServices();
		AID agentId = dfad.getName();
		ServedOperation op;
		OperationID opId;
		FIPAEndPoint fipaEP;

		while (it.hasNext()) {
			sd = (ServiceDescription) it.next();
			op = createServedOperation(agent, agentId, sd);
			if (null != op) {
				coll.add(op);
			}
		}
		// return all new operations
		return coll;
	}

	/**
	 * creates a new servedOperation.
	 *
	 * @param fipaSId an end point identification
	 * @return servedOperation created or null
	 */
	private ServedOperation createServedOperation(Agent agent, AID agentId, ServiceDescription sd) {
		UDDIOperationIdentificator uddiOId;
		OperationID opId;
		FIPAServiceIdentificator fipaSId;
		FIPAEndPoint fipaEP;
		ServedOperation op;

		Property p = null;
		Iterator it = sd.getAllProperties();
		boolean found = false;
		while (it.hasNext() && !found) {
			p = (Property) it.next();
			if (WSIGConstants.WSIG_FLAG.equalsIgnoreCase(p.getName()) && p.getValue().toString().equals("true")) {
				found = true;
			}
		}

		if (!found) {
			return null;
		}

		//manageAgentOntologies(sd);

		Definition definition = createDefinitionFromOntologies(agent, sd);
		fipaSId = new FIPAServiceIdentificator(agentId, sd);
		fipaEP = new FIPAEndPoint(fipaSId);
		uddiOId = ServedOperationStore.generateUDDIOperationId();
		opId = new OperationID(fipaSId, uddiOId);
		op = new ServedOperation(opId, fipaEP, true);

		// add structure into a WSDL
		// input data:  sd, uddiOId
		WSDLDefinition wsdl = new WSDLDefinition();
		try {
			wsdl.setURL(new URL(Configuration.getInstance().getURIPathForWSDLs() + uddiOId.getWSDLOperation() + ".wsdl"));
			op.setWSDL(wsdl);
		} catch (Exception e) {
			log.debug(e); // a url does not exist
		}

		log.debug("   -> new operation: fipa_service=" + fipaSId.getServiceName()
			+ ", WSoperation=" + uddiOId.getWSDLOperation());
		return op;
	}

	private Definition createDefinitionFromOntologies(Agent agent, ServiceDescription sd) {
		boolean toAdd = false;
		XSDSchema xsdSchema = null;


		Iterator ontologies = sd.getAllOntologies();
		ContentManager cntManager = agent.getContentManager();

		Definition definition = new DefinitionImpl();
		String tns = "urn:" + sd.getName();
		definition.setQName(new QName(tns, sd.getName()));
		definition.setTargetNamespace(tns);
		definition.addNamespace("tns", tns);
		definition.addNamespace("xsd", xsd);

		PortType portType = new PortTypeImpl();
		portType.setUndefined(false);
		definition.addPortType(portType);

		/*
				// TO DO Binding-Service Node of WSDL
				definition.addNamespace("impl", "urn:Trail");
				Binding binding = new BindingImpl();

				binding.setPortType(portType);
				binding.setQName(new QName("urn:Trail","impl"));
				binding.setUndefined(false);
				definition.addBinding(binding);
				Service service = new ServiceImpl();
				service.setQName(new QName(sd.getName()));
				Port port = new PortImpl();
				port.setName(sd.getName());
				port.setBinding(binding);
				service.addPort(port);
				definition.addService(service);
				 */
		while (ontologies.hasNext()) {
			String ontoName = (String) ontologies.next();
			Ontology onto = cntManager.lookupOntology(ontoName);
			java.util.List actionNames = onto.getActionNames();
			//if (actionNames.size() > 0) portType.setUndefined(false);
			//definition.addPortType(portType);

			for (int i = 0; i < actionNames.size(); i++) {
				try {
					String actionName = (String) actionNames.get(i);
					Operation op = new OperationImpl();
					portType.addOperation(op);
					op.setName(actionName);
					op.setUndefined(false);
					AgentActionSchema actionSchema = (AgentActionSchema) onto.getSchema(actionName);

					//Output Params
					Output output = new OutputImpl();
					Message messageOut = new MessageImpl();
					messageOut.setQName(new QName(tns, actionName + "Output"));
					messageOut.setUndefined(false);
					output.setMessage(messageOut);
					output.setName(actionName + "Output");
					op.setOutput(output);
					definition.addMessage(messageOut);
					ObjectSchema resultSchema = actionSchema.getResultSchema();
					if (resultSchema instanceof PrimitiveSchema) {
						PrimitiveSchema resultPrimitive = (PrimitiveSchema) resultSchema;
						String wsdlType = (String) types.get(resultPrimitive.getTypeName());
						Part part = new PartImpl();
						QName qNameType = new QName(xsd, wsdlType);
						part.setTypeName(qNameType);
						part.setName("out");
						messageOut.addPart(part);
					} else if (resultSchema instanceof AggregateSchema) {
						throw new Exception("Not yet handled");
					} else if (resultSchema instanceof ConceptSchema) {


					}
					//Input Parameters: retrieve all slot of action
					String[] slotNames = actionSchema.getNames();

					Message messageIn = new MessageImpl();
					messageIn.setQName(new QName(tns, actionName + "Input"));
					messageIn.setUndefined(false);
					definition.addMessage(messageIn);
					Input input = new InputImpl();
					input.setMessage(messageIn);
					input.setName(actionName + "Input");
					op.setInput(input);

					for (String slotName : slotNames) {
						ObjectSchema slotSchema = actionSchema.getSchema(slotName);
						String typeName = slotSchema.getTypeName();
						if (slotSchema instanceof PrimitiveSchema) {
							PrimitiveSchema schema = (PrimitiveSchema) slotSchema;
							String wsdlType = (String) types.get(schema.getTypeName());
							Part part = new PartImpl();
							QName qNameType = new QName(xsd, wsdlType);
							part.setTypeName(qNameType);
							part.setName(slotName);
							messageIn.addPart(part);

						} else if (slotSchema instanceof AggregateSchema) {
							AggregateSchema schema = (AggregateSchema) slotSchema;
							Facet[] facets = schema.getFacets(slotName);
							int cardMax;
							int cardMin;
							for (Facet facet : facets) {
								if (facet instanceof CardinalityFacet) {
									cardMax = ((CardinalityFacet) facet).getCardMax();
									cardMin = ((CardinalityFacet) facet).getCardMin();

								} else if (facet instanceof TypedAggregateFacet) {

								} else {
									System.out.println("Facet is unknown");
								}
							}
							//create Type
							Types ts = definition.createTypes();

							//ts.setDocumentationElement();

							/*Types t = new TypesImpl();
							Element element = new Element()
							t.setDocumentationElement();
*/

						} else if (slotSchema instanceof ConceptSchema) {
							toAdd = true;
							String[] conceptSlotNames = slotSchema.getNames();
							xsdSchema = SchemaGeneratorUtils.createSchema();
							XSDComplexTypeDefinition complexType = SchemaGeneratorUtils.addComplexTypeToSchema(xsdSchema, slotName);
							XSDModelGroup sequence = SchemaGeneratorUtils.addSequenceToComplexType(complexType);
							for (String conceptSlotName : conceptSlotNames) {
								ObjectSchema objSchema = slotSchema.getSchema(conceptSlotName);
								if (objSchema instanceof PrimitiveSchema) {
									String slotType = (String) types.get(objSchema.getTypeName());
									SchemaGeneratorUtils.addElementToSequence(xsdSchema, slotName, slotType, sequence);
								} else {
									throw new Exception("Not yet handled");
								}
							}
							String wsdlType = complexType.getName();
							Part part = new PartImpl();
							QName qNameType = new QName(xsd, wsdlType);
							part.setTypeName(qNameType);
							part.setName(slotName);
							messageIn.addPart(part);

						}

					}

				} catch (Exception e) {

				}
			}

			if (toAdd)
				WSDLGeneratorUtils.addTypeToDefinition(definition, xsdSchema.getElement());

			//onto
			try {
				WSDLFactory factory = WSDLFactory.newInstance();
				WSDLWriter writer = factory.newWSDLWriter();
				String wsdlDir = Configuration.getInstance().getWsdlDirectory();
				File file = new File(wsdlDir + File.separator + sd.getName() + ".wsdl");
				PrintWriter output = new PrintWriter(file);
				//writer.writeWSDL(definition, System.out);
				writer.writeWSDL(definition, output);


			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return definition;

	}

	/**
	 * removes served operations.
	 * For each FIPA service a corresponding ServedOperation instance
	 * is removed from ServedOperationStore.
	 *
	 * @param dfad agent's registration structure
	 */
	private synchronized void removeServedOperations(DFAgentDescription dfad) {
		ServiceDescription sd;
		FIPAServiceIdentificator fipaSId;
		Iterator it = dfad.getAllServices();
		ServedOperation op;
		while (it.hasNext()) {
			sd = (ServiceDescription) it.next();
			fipaSId = new FIPAServiceIdentificator(dfad.getName(), sd);
			op = operationStore.find(fipaSId);
			if (null == op) {
				// an op has not been registered
				continue;
			}
			operationStore.remove(op);
			op.close();
		}
	}

	/**
	 * performs registration.
	 * See jade.domain.GatewayAgentMethodListener interface.
	 *
	 * @param register
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void registerAction(Agent agent, DFAgentDescription dfad, AID aid) throws FIPAException {
		log.debug("A wsigs's registration from an agent: " + dfad.getName() + ".");

		// test an existence
		if (aidToService.containsKey(dfad.getName())) {
			//already registered
			//original registerAction gives a responce
			log.debug("Already registered " + dfad.getName());
			return;
		}

		// create and store ServedOperations into a ServedOperationStore
		Collection col = createServedOperations(agent, dfad);
		if (col.isEmpty()) {
			// nothing to do
			log.debug("No operations are created.");
			return;
		}
		// store a ServedOperation generated
		operationStore.put(col);

		try {
			// create identification names
			String tName = "WSIG's tModel for " + dfad.getName().getLocalName();
			String sName = "WSIG's businessService for " + dfad.getName().getLocalName();

			// create a new tModel
			TModel tModel;
			tModel = createTModel(extractWsdlUrl(col), tName);

			// Add new service into an organization running WSIGS.
			// This is only functional in UDDI v3.0 by accessPoint's useType attribute.
			// It is left on hostingRedirector in UDDI v2.0.

			// generate a category bag with service's names
			CategoryBag cb = new CategoryBag();
			Iterator it = col.iterator();//dfad.getAllServices();
			KeyedReference kr;
			ServedOperation so;
			OperationID id;
			while (it.hasNext()) {
				so = (ServedOperation) it.next();
				if (null == so) {
					continue;
				}
				id = so.getOperationID();
				kr = new KeyedReference();
				kr.setTModelKey(Configuration.getInstance().getProperty(Configuration.KEY_UDDI_TMODEL)); // uddi-org:general_keywords  in UDDI v2.0
				kr.setKeyName("fipaServiceName");
				kr.setKeyValue(id.getFIPAServiceIdentificator().getServiceName());
				cb.add(kr);

				kr = new KeyedReference();
				kr.setTModelKey(Configuration.getInstance().getProperty(Configuration.KEY_UDDI_TMODEL)); // uddi-org:general_keywords  in UDDI v2.0
				kr.setKeyName(id.getFIPAServiceIdentificator()
					.getServiceName());
				kr.setKeyValue(id.getUDDIOperationIdentificator().getWSDLOperation());
				cb.add(kr);
			}

			// create a new businessService
			BusinessService businessService = new BusinessService("");
			businessService.setDefaultNameString(sName, null);
			businessService.setBusinessKey(businessKey);
			businessService.setCategoryBag(cb);

			// save the Service
			Vector services = new Vector();
			services.addElement(businessService);
			ServiceDetail serviceDetail = uddiProxy.save_service(getAuthToken().getAuthInfoString(), services);

			// get a service key returned
			Vector businessServices = serviceDetail.getBusinessServiceVector();
			BusinessService businessServiceReturned = (BusinessService) (businessServices.elementAt(0));
			String serviceKey = businessServiceReturned.getServiceKey();

			// create bindingTemplate
			createBindingTemplate(
				extractAccessPoint(col),
				new ServiceKey(serviceKey),
				new TModelKey(tModel.getTModelKey()));

			//store reference for AID and new service for removing the service
			aidToService.put(dfad.getName(), new ServiceKey(serviceKey));
			log.debug("An agent services registration done.");

		} catch (UDDIException e) {
			log.error(e);
		} catch (TransportException e) {
			log.error(e);
		}

	}

	/**
	 * performs deregistration
	 * See jade.domain.GatewayAgentMethodListener interface.
	 *
	 * @param deregister
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void deregisterAction(DFAgentDescription dfad, AID aid) throws FIPAException {
		log.debug("A wsigs's deregistration from an agent: " + dfad + ".");

		// test an existence
		if (!aidToService.containsKey(dfad.getName())) {
			// is not registered
			//original deregisterAction gives a responce
			return;
		}

		// remove all operations owned by an agent from the ServedOperationStore
		removeServedOperations(dfad);

		//delete service identified by the Key stored
		ServiceKey k = (ServiceKey) aidToService.get(dfad.getName());
		aidToService.remove(dfad.getName());
		log.debug("A serviceKey removed " + k.getText());

		DispositionReport dr;
		try {
			// delete tModel
			BusinessService bs = takeService(k);
			BindingTemplate bt = bs.getBindingTemplates().get(0);
			TModelKey tModelKey = new TModelKey(
				bt.getTModelInstanceDetails().get(0).getTModelKey());
			dr = uddiProxy.delete_tModel(getAuthToken().getAuthInfoString(), tModelKey.getText());
			if (! dr.success()) {
				log.error("Error during deletion of TModel\n" +
					"\n operator:" + dr.getOperator() +
					"\n generic:" + dr.getGeneric());
			}

			// delete a service
			dr = uddiProxy.delete_service(getAuthToken().getAuthInfoString(), k.getText());
			if (! dr.success()) {
				log.error("Error during deletion of Service\n" +
					"\n operator:" + dr.getOperator() +
					"\n generic:" + dr.getGeneric());

				Vector results = dr.getResultVector();
				for (int j = 0; j < results.size(); j++) {
					Result r = (Result) results.elementAt(j);
					log.error(" errno:" + r.getErrno());
					if (r.getErrInfo() != null) {
						log.error("\n errCode:" + r.getErrInfo().getErrCode() +
							"\n errInfoText:" + r.getErrInfo().getText());
					}
				}
			}
		} catch (UDDIException e) {
			log.error(" Deregistration " + e);
		} catch (TransportException e) {
			log.error(" Deregistration " + e);
		}
	}

	/**
	 * performs modification
	 * See jade.domain.GatewayAgentMethodListener interface.
	 *
	 * @param modify
	 * @param aid
	 * @throws FIPAException
	 */
	public synchronized void modifyAction(Modify modify, AID aid) throws FIPAException {
		DFAgentDescription dfad = (DFAgentDescription) modify.getDescription();
		log.debug("A wsigs's modification from an agent: " + dfad + ".");
		// modification of the agent's tModel
		//  - a coresponding wsdl structure may be afected
		//  - identificators and categories may be changed
		//       (a management is not proposed still)
		//  - a servedOperation structure may be changed

		// update ServedOperationStore
		//  find ServedOperations for an agent in a ServedOperationStore
		//  create a ServedOperation if it is not found
		//  delete one if it is not registered again
		ServiceDescription sd;
		FIPAServiceIdentificator fipaSId;
		ServedOperation op;
		Iterator i = dfad.getAllServices();
		Collection col = new HashSet();
		while (i.hasNext()) {
			sd = (ServiceDescription) i.next();
			fipaSId = new FIPAServiceIdentificator(aid, sd);
			op = operationStore.find(fipaSId);
			if (null == op) {
				// create a new servedOperation
				//	op = createServedOperation( aid, sd );
				if (null == op) {
					// is not operation for web service
					continue;
				}
				operationStore.put(op);
			}
			col.add(op);
		}
		i = operationStore.findAllServedOperations(aid).iterator();
		while (i.hasNext()) {
			op = (ServedOperation) i.next();
			if (! col.contains(op)) {
				// delete a servedOperation
				operationStore.remove(op);
			}
		}
	}

	/**
	 * performs searching.
	 * The list exists after superclasse's call and is passed by the generatedList parameter.
	 * See jade.domain.GatewayAgentMethodListener interface.
	 * An implementation is empty now, nothing special is required for a searching.
	 *
	 * @param search		searching request
	 * @param aid		   requester
	 * @param generatedList already generated list by suprclass
	 * @return generatedList afected
	 * @throws FIPAException
	 */
	public List searchAction(Search search, AID aid, List generatedList) throws FIPAException {
		// nothing advanced, generatedList is only returned
		return generatedList;
	}


	/**
	 * removes old records in UDDI
	 */
	private void resetUDDI4j() {
		ServiceList sl = new ServiceList(); // default is an empty list
		try {
			Vector names = new Vector(1);
			names.add(new Name("%WSIG%"));
			CategoryBag cb = new CategoryBag();
			TModelBag tmb = new TModelBag();
			FindQualifiers fq = new FindQualifiers();
			int maxRows = Integer.MAX_VALUE;  // unlimited

			sl = uddiProxy.find_service(
				businessKey,
				names,
				cb,
				tmb,
				fq,
				maxRows);

			ServiceInfo info;
			ServiceInfos infos = sl.getServiceInfos();
			String s;
			Vector sKeys = new Vector();
			int k;

			if (infos.size() < 1) {
				log.debug("Old records do not exist in UDDI.");
				return;
			}

			for (k = 0; k < infos.size(); k ++) {
				info = infos.get(k);
				s = info.getServiceKey();
				log.debug(" service to delete: " + s);
				sKeys.add(s);
			}

			DispositionReport dr;
			dr = uddiProxy.delete_service(
				getAuthToken().getAuthInfoString(), sKeys);

		} catch (UDDIException ue) {
			log.debug(ue);
		} catch (TransportException te) {
			log.debug(te);
		}
	}

	/**
	 * sets up the DFToUDDI4j. It starts components required.
	 * Class fields authToken and uddiProxy are set properly as main result.
	 */
	private void setupUDDI4j() {
		// to register into UDDI
		// structures used for a communication with UDDI is retrieved
		Configuration c = Configuration.getInstance();

		//System.setProperty("hpsoap.logDirectory","/tmp");
		//System.setProperty("hpsoap.logFileName","uddi4j.log");

		synchronized (c) {
			// synchronized on main Configuration instance
			// to prevent changes in configuration

			System.setProperty(Configuration.KEY_UDDI4J_LOG_ENABLED,
				c.getUDDI4jLogEnabled());
			System.setProperty(Configuration.KEY_UDDI4J_TRANSPORT_CLASS,
				c.getUDDI4jTransportClass());

			businessKey = c.getBusinessKey();
			userName = c.getUserName();
			password = c.getUserPassword();

			//Moved after setting System Properties
			uddiProxy = new UDDIProxy();
			// Select the desired UDDI server node
			try {
				uddiProxy.setInquiryURL(c.getQueryManagerURL());
				uddiProxy.setPublishURL(c.getLifeCycleManagerURL());
			} catch (Exception e) {
				log.error(e);
			}
		}

		resetUDDI4j();
	}

	/**
	 * asks for an authentification.
	 * Configuration's name and password are used.
	 *
	 * @return an authentification
	 * @throws TransportException if transport problems are occured
	 * @throws UDDIException	  if UDDI problems are occured
	 */
	private AuthToken getAuthToken() throws TransportException, UDDIException {
		// Get an authorization token
		log.debug("Ask for authToken.");

		// Pass in userid and password registered at the UDDI site
		AuthToken authToken = uddiProxy.get_authToken(userName, password);
		log.debug("Returned authToken from a UDDI:" + authToken.getAuthInfoString());
		return authToken;
	}

	/**
	 * creates and registers a bindingTemplate
	 *
	 * @param accessPoint an accessPoint for a service
	 * @param serviceKey  service key for a bindingTemplate
	 * @param tModelKey   tModel key to be reffered
	 * @return a new bindingTemplate registered in a UDDI
	 */
	public BindingTemplate createBindingTemplate(AccessPoint accessPoint, ServiceKey serviceKey, TModelKey tModelKey) {
		log.debug("A bindingTemplate is going to be created.");
		BindingTemplate bindingTemplateReturned = null;
		try {
			// create TModelInstanceDetails
			Vector tModelInstanceInfoVector = new Vector();
			TModelInstanceInfo tModelInstanceInfo = new TModelInstanceInfo(tModelKey.getText());
			tModelInstanceInfoVector.add(tModelInstanceInfo);
			TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();
			tModelInstanceDetails.setTModelInstanceInfoVector(tModelInstanceInfoVector);

			// create a new bindingTemplate
			BindingTemplate bindingTemplate =
				new BindingTemplate("",
					tModelInstanceDetails,
					accessPoint);
			bindingTemplate.setServiceKey(serviceKey.getText());
			Vector bindingTemplatesVector = new Vector();
			bindingTemplatesVector.addElement(bindingTemplate);

			// save bindingTemplate
			BindingDetail bindingDetail = uddiProxy.save_binding(
				getAuthToken().getAuthInfoString(),
				bindingTemplatesVector);

			// BindingDetail returned is given as a result
			Vector bindingTemplateVector = bindingDetail.getBindingTemplateVector();
			bindingTemplateReturned = (BindingTemplate) (bindingTemplateVector.elementAt(0));

		} catch (UDDIException e) {
			log.error(e);
		} catch (TransportException e) {
			log.error(e);
		}

		log.debug("New BindingKey: " + bindingTemplateReturned.getBindingKey());
		return bindingTemplateReturned;
	}


	/**
	 * creates and registers a new TModel
	 *
	 * @param wsdlURL url for a wsdl's description
	 * @param name	name for a tModel
	 * @return tModel created
	 */
	public TModel createTModel(String wsdlURL, String name) {
		log.debug("A tModel is going to be created.");
		TModel tModelReturned = null;
		try {
			// to point into a WSDL
			OverviewURL overviewURL = new OverviewURL(wsdlURL);
			OverviewDoc overviewDoc = new OverviewDoc();
			overviewDoc.setOverviewURL(overviewURL);

			// create a new TModel
			TModel tModel = new TModel("", name);
			tModel.setOverviewDoc(overviewDoc);
			Vector tModelsVector = new Vector();
			tModelsVector.addElement(tModel);

			// save bindingTemplate
			TModelDetail tModelDetail = uddiProxy.save_tModel(
				getAuthToken().getAuthInfoString(),
				tModelsVector);

			// tModelDetail returned is given as a result
			tModelsVector = tModelDetail.getTModelVector();
			tModelReturned = (TModel) (tModelsVector.elementAt(0));

		} catch (UDDIException e) {
			log.error(e);
		} catch (TransportException e) {
			log.error(e);
		}

		log.debug("New tModelKey: " + tModelReturned.getTModelKey());
		return tModelReturned;
	}

	/**
	 * extract a wsdl's URL from servedOperations.
	 * A wsdl's document is the same for all FIPA services provided by an agent.
	 *
	 * @param col a collection of servedOperations
	 * @return a wsdl's URL as String
	 */
	private String extractWsdlUrl(Collection col) {
		if (col.isEmpty()) {
			return "";
		}
		Iterator i = col.iterator();
		ServedOperation op = (ServedOperation) i.next();
		return op.getWSDL().getURL().toString();
	}

	/**
	 * extract an accessPoint from servedOperations.
	 * An accessPoint is the same for all FIPA services provided by an agent.
	 *
	 * @param col a collection of servedOperations
	 * @return an accessPoint
	 */
	private AccessPoint extractAccessPoint(Collection col) {
		if (col.isEmpty()) {
			return new AccessPoint(Configuration.getInstance().getHostURI(), "http");
		}
		Iterator i = col.iterator();
		ServedOperation op = (ServedOperation) i.next();
		return new AccessPoint(
			op.getOperationID().getUDDIOperationIdentificator().getAccessPoint(),
			"http");
	}


	/**
	 * gets a service identified by a key
	 *
	 * @param serviceKey key of service requested
	 * @return service requested
	 * @throws UDDIException
	 * @throws TransportException
	 */
	public BusinessService takeService(ServiceKey serviceKey) throws UDDIException, TransportException {
		ServiceDetail serviceDetail = uddiProxy.get_serviceDetail(serviceKey.getText());
		Vector businessServices = serviceDetail.getBusinessServiceVector();
		return (BusinessService) (businessServices.elementAt(0));
	}
}
