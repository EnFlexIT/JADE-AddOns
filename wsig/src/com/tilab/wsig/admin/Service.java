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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.uddi4j.util.ServiceKey;

import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.wsdl.WSDLUtils;

@XmlRootElement(name = "service")
@XmlType(propOrder={"name",
					"prefix",
					"mapperClass",
					"hierarchicalComplexType",
					"jadeOntology",
					"jadeAgent",
					"uddiServiceKey",
					"wsdlUrl",
					"operation"})

public class Service {
	
	
	private  WSIGService wsigService;
	private HttpServletRequest hsr;
	
	
	public Service(){		
	}	
		
	public Service(WSIGService service, HttpServletRequest hsr){
		this.wsigService=service;		
		this.hsr=hsr;	
	}

	
	@XmlElement(name="name")
	public String getName() {
	
	 String name= wsigService.getServiceName();
	if (name == null) {
		name ="";
	}	
		return name;
	}
	
	@XmlElement(name="prefix")
	public String getPrefix() {
	
		String servicePrefix = wsigService.getServicePrefix();
		String servicePrefixName = servicePrefix;
		if (servicePrefix == null || "".equals(servicePrefix)) {
			servicePrefixName = "";
		} else {
			servicePrefixName = servicePrefixName.substring(0, servicePrefixName.length()-1);
		}
		return servicePrefixName;
	}
	
	@XmlElement(name="mapperClass")
	public String getMapperClass() {
	
		String mapperClass="";
		Class mclass = wsigService.getMapperClass();
		if (mclass != null) {
			mapperClass= mclass.getCanonicalName();
		}
		return mapperClass;
	}
	
	@XmlElement(name="hierarchicalComplexType")
	public Boolean isHierarchicalComplexType() {
			return wsigService.isHierarchicalComplexType();
	}
	
	
	@XmlElement(name="jadeOntology")
	public String getJadeOntology() {
	
		return wsigService.getServiceOntology().getName();
	}
	
	@XmlElement(name="jadeAgent")
	public String getJadeAgent() {
	
		return wsigService.getAid().getName();
	}
	
	@XmlElement(name="uddiServiceKey")
	public String getUddiServiceKey() {
		
		String uddiKeyName = "";
		ServiceKey uddiServiceKey = wsigService.getUddiServiceKey();
		if (uddiServiceKey != null) {
			uddiKeyName = uddiServiceKey.getText();
		}
		
		return uddiKeyName;
	}
	
	@XmlElement(name="wsdlUrl")
	public String getWsdlUrl() {
	
		String wsdlUrl = WSDLUtils.getWsdlUrl(wsigService.getServiceName(), hsr);
		return wsdlUrl;
	}
	
	@XmlElementWrapper(name="operations")
	@XmlElement(name="operation")
	public Collection<String> getOperation() {
			
		return wsigService.getOperations();
	}	
	
}
