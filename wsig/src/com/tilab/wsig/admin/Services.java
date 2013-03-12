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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.store.WSIGStore;

@XmlRootElement(name="services")
public class Services {

	private List<String> servicesNames = new ArrayList<String>();
	private ServletContext servletContext;

	Services(){
	}	

	public Services(ServletContext servletContext){
		this.servletContext = servletContext;
	}	

	@XmlElement(name="service")
	public List<String> getServicesNames() {
		WSIGService service;
		WSIGStore wsigStore= (WSIGStore)servletContext.getAttribute("WSIGStore"); 
		Collection<WSIGService> services = wsigStore.getAllServices();
		Iterator<WSIGService> itServices = services.iterator();

		servicesNames= new ArrayList<String>();
		while(itServices.hasNext()) {
			service = (WSIGService)itServices.next();
			servicesNames.add(service.getServiceName());
		}

		return servicesNames;
	}

	public void setServicesNames(List<String> servicesNames) {
		this.servicesNames = servicesNames;
	}
}
