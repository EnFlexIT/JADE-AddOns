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
package com.tilab.wsig.admin;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.tilab.wsig.WSIGConfiguration;

@XmlRootElement(name = "configuration")
@XmlType(propOrder={"jadeMainHost",
		"jadeMainPort", 
		"jadeContainerName",
		"containerLocalPort",
		"WSIGAgentClass",
		"WSIGVersion",
		"WSIGServicesURL",
		"WSIGAdminURL",
		"WSIGTimeOut",
		"WSIGJavaTypePreservation",
		"WSDLHierarchicalComplexType",
		"WSDLLocalNameSpace",
		"WSDLStyle",
		"WSDLWriteEnable",
		"WSDLWritePath",
		"uddiEnable",
		"uddiQueryManager",
		"uddiLifeCycleManager",
		"uddiBusinessKey",
		"uddiUserName",
		"uddiPassword",
		"uddiTModel"})
class Configuration {
	private HttpServletRequest hsr;
	private WSIGConfiguration conf = WSIGConfiguration.getInstance();

	Configuration() {
	}

	public Configuration(HttpServletRequest hsr) {
		this.hsr = hsr;
	}

	@XmlElement
	public String getJadeMainHost() {
		return conf.getMainHost();
	}

	public void setJadeMainHost(String mainHost) {
		conf.setMainHost(mainHost);
	}

	@XmlElement
	public String getJadeMainPort() {
		return conf.getMainPort();
	}

	public void setJadeMainPort(String mainPort) {
		conf.setMainPort(mainPort);
	}

	@XmlElement
	public String getJadeContainerName() {
		return conf.getContainerName();
	}

	public void setJadeContainerName(String jadeContainerName) {
		conf.setContainerName(jadeContainerName);
	}

	@XmlElement
	public String getContainerLocalPort() {
		return conf.getLocalPort();
	}

	public void setContainerLocalPort(String containerLocalPort) {
		conf.setLocalPort(containerLocalPort);
	}

	@XmlElement
	public String getWSIGAgentClass() {
		return conf.getAgentClassName();
	}

	public void setWSIGAgentClass(String wsigAgentClass) {
		conf.setAgentClassName(wsigAgentClass);
	}

	@XmlElement
	public String getWSIGVersion() {
		return conf.getWsigVersion();
	}

	@XmlElement
	public String getWSIGServicesURL() throws MalformedURLException {
		return conf.getServicesUrl(hsr);
	}	

	public void setWSIGServicesURL(String wsigServicesURL) {
		conf.setServicesUrl(wsigServicesURL);
	}

	@XmlElement
	public URL getWSIGAdminURL() throws MalformedURLException {
		return conf.getAdminUrl(hsr);
	}

	@XmlElement
	public int getWSIGTimeOut() {
		return conf.getWsigTimeout();
	}	

	public void setWSIGTimeOut(int wsigTimeOut) {
		conf.setWsigTimeout(wsigTimeOut);
	}

	@XmlElement
	public String getWSIGJavaTypePreservation() {
		return conf.getPreserveJavaType();
	}	

	public void setWSIGJavaTypePreservation(String wsigJavaTypePreservation) {
		conf.setPreserveJavaType(wsigJavaTypePreservation);
	}

	@XmlElement
	public Boolean getWSDLHierarchicalComplexType() {
		return conf.isHierarchicalComplexTypeEnable();
	}

	public void setWSDLHierarchicalComplexType(Boolean wsdlLHierarchicalComplexType) {
		conf.setHierarchicalComplexTypeEnable(wsdlLHierarchicalComplexType);
	}

	@XmlElement
	public String getWSDLLocalNameSpace() {
		return conf.getLocalNamespacePrefix();
	}

	public void setWSDLLocalNameSpace(String wsdlLocalNameSpace) {
		conf.setLocalNamespacePrefix(wsdlLocalNameSpace);
	}

	@XmlElement
	public String getWSDLStyle() {
		return conf.getWsdlStyle();
	}

	public void setWSDLStyle(String wsdlStyle) {
		conf.setWsdlStyle(wsdlStyle);
	}

	@XmlElement
	public Boolean getWSDLWriteEnable() {
		return conf.isWsdlWriteEnable();
	}

	public void setWSDLWriteEnable(Boolean  wsdlWriteEnable) {
		conf.setWsdlWriteEnable(wsdlWriteEnable);
	}

	@XmlElement
	public String getWSDLWritePath() {
		return conf.getWsdlDirectory();
	}


	public void setWSDLWritePath(String wsdlDirectory) {
		conf.setWsdlDirectory(wsdlDirectory);
	}

	@XmlElement
	public Boolean isUddiEnable() {
		return  conf.isUddiEnable();
	}

	public void setUddiEnable(Boolean uddiEnable) {
		conf.setUddiEnable(uddiEnable);
	}

	@XmlElement
	public String getUddiQueryManager() {
		return  conf.getQueryManagerURL();
	}

	public void setUddiQueryManager(String uddiQueryManager) {
		conf.setQueryManagerURL(uddiQueryManager);
	}

	@XmlElement
	public String getUddiLifeCycleManager() {
		return conf.getLifeCycleManagerURL();
	}

	public void setUddiLifeCycleManager(String uddiLifeCycleManager) {
		conf.setLifeCycleManagerURL(uddiLifeCycleManager);
	}

	@XmlElement
	public String getUddiBusinessKey() {
		return conf.getBusinessKey();
	}

	public void setUddiBusinessKey(String uddiBusinessKey) {
		conf.setBusinessKey(uddiBusinessKey);
	}

	@XmlElement
	public String getUddiUserName() {
		return conf.getUserName();
	}

	public void setUddiUserName(String uddiUserName) {
		conf.setUserName(uddiUserName);
	}

	@XmlElement
	public String getUddiPassword() {
		return conf.getUserPassword();
	}

	public void setUddiPassword(String uddiPassword) {
		conf.setUserPassword(uddiPassword);
	}

	@XmlElement
	public String getUddiTModel() {
		return conf.getTModel();
	}

	public void setUddiTModel(String uddiTModel) {
		conf.setTModel(uddiTModel);
	}
}