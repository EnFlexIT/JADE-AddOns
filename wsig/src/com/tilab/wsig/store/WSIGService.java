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

package com.tilab.wsig.store;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.core.AID;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.wsdl.Definition;
import javax.wsdl.extensions.soap.SOAPAddress;

import org.uddi4j.util.ServiceKey;

import com.tilab.wsig.WSIGConfiguration;

public class WSIGService {

	private String serviceName;
	private String servicePrefix;
	private AID aid;
	private Ontology agentOnto;
	private BeanOntology serviceOnto;
	private Definition wsdlDefinition;
	private SOAPAddress soapAddress;
	private ServiceKey uddiServiceKey;
	private Class mapperClass;
	private boolean hierarchicalComplexType;
	private Map<String,ActionBuilder> actionsBuilder = new HashMap<String,ActionBuilder>();
	private Map<String,ResultBuilder> resultsBuilder = new HashMap<String,ResultBuilder>();
	
	public AID getAid() {
		return aid;
	}
	public void setAid(AID aid) {
		this.aid = aid;
	}
	public Ontology getAgentOntology() {
		return agentOnto;
	}
	public BeanOntology getServiceOntology() {
		return serviceOnto;
	}
	public void setAgentOntology(Ontology agentOnto) {
		this.agentOnto = agentOnto;
		
		// Create the service ontology (bean-onto that extend the agent-onto)
		String name = "wsig_"+agentOnto.getName();
		serviceOnto = new BeanOntology(name, agentOnto);
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public Definition getWsdlDefinition(HttpServletRequest httpRequest) throws MalformedURLException {
		if (httpRequest != null) {
			// Update soap endpoint
			soapAddress.setLocationURI(WSIGConfiguration.getInstance().getServicesUrl(httpRequest)+"/"+serviceName);
		}
		return wsdlDefinition;
	}
	public void setWsdlDefinition(Definition wsdlDefinition) {
		this.wsdlDefinition = wsdlDefinition;
	}
	public ServiceKey getUddiServiceKey() {
		return uddiServiceKey;
	}
	public void setUddiServiceKey(ServiceKey uddiServiceKey) {
		this.uddiServiceKey = uddiServiceKey;
	}
	public Collection<String> getOperations() {
		return actionsBuilder.keySet();
	}
	public Class getMapperClass() {
		return mapperClass;
	}
	public void setMapperClass(Class mapperClass) {
		this.mapperClass = mapperClass; 
	}
	public void addActionBuilder(String operationName, ActionBuilder actionBuilder) {
		actionsBuilder.put(operationName, actionBuilder);
	}
	public ActionBuilder getActionBuilder(String operationName) {
		return actionsBuilder.get(operationName);
	}
	public void addResultBuilder(String operationName, ResultBuilder resultBuilder) {
		resultsBuilder.put(operationName, resultBuilder);
	}
	public ResultBuilder getResultBuilder(String operationName) {
		return resultsBuilder.get(operationName);
	}
	public String getServicePrefix() {
		return servicePrefix;
	}
	public void setServicePrefix(String servicePrefix) {
		this.servicePrefix = servicePrefix;
	}
	public SOAPAddress getSOAPAddress() {
		return soapAddress;
	}
	public void setSOAPAddress(SOAPAddress soapAddress) {
		this.soapAddress = soapAddress;
	}
	public boolean isHierarchicalComplexType() {
		return hierarchicalComplexType;
	}
	public void setHierarchicalComplexType(boolean hierarchicalComplexType) {
		this.hierarchicalComplexType = hierarchicalComplexType;
	}
	@Override
	public String toString() {
		return "WSIGService (name="+serviceName+", agentOnto="+agentOnto.getName()+", mapper="+mapperClass+")";
	}
}
