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

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.tilab.wsig.store.WSIGService;
import com.tilab.wsig.store.WSIGStore;
import com.tilab.wsig.wsdl.WSDLUtils;


@Path("/services")
public class ServicesResource {

	private static Logger log = Logger.getLogger(ServicesResource.class.getName());

	//get the list with the names of the services
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Services getServices(@Context ServletContext servletContext) {
		log.info("The list with the Services'names has been retrieved...");
		return new Services(servletContext);
	}

	//get the {serviceName} information
	@GET
	@Path("{serviceName}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Service getService(@Context ServletContext servletContext, @PathParam("serviceName") String serviceName, @Context HttpServletRequest hsr) {
		WSIGStore wsigStore = (WSIGStore)servletContext.getAttribute("WSIGStore");
		WSIGService wsigservice = wsigStore.getService(serviceName);
		Service service = new Service(wsigservice, hsr);
		log.info("The information of "+serviceName+" service has been retrieved...");
		return service;	
	}

	//get the WSDL of {serviceName}
	@GET
	@Path("{serviceName}/wsdl")
	@Produces({MediaType.APPLICATION_XML,MediaType.TEXT_PLAIN})
	public void getWsdl(@PathParam("serviceName") String serviceName,@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse) throws IOException {
		String wsdlUrl = WSDLUtils.getWsdlUrl(serviceName, httpRequest);
		URL consoleUrl = new URL(wsdlUrl);
		httpResponse.sendRedirect(consoleUrl.toString());
		log.info("The WSDL of "+serviceName+" service has been retrieved...");
	}
} 