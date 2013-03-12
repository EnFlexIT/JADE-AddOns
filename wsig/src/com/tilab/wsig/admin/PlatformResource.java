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

import jade.wrapper.ControllerException;
import jade.wrapper.gateway.JadeGateway;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.tilab.wsig.WSIGConfiguration;

@Path("platform")
public class PlatformResource {

	private static Logger log = Logger.getLogger(PlatformResource.class.getName());

	@GET
	public Response getStatus(@Context ServletContext servletContext) {
		String wsigActive;
		Boolean status = (Boolean)servletContext.getAttribute("WSIGActive");

		if (status== null) {
			wsigActive="UNKNOWN";
		} 
		else if (status == true) {
			wsigActive="ACTIVE";
		} 
		else {
			wsigActive="DOWN";
		}
		log.info("WSIG state retrieved: "+wsigActive);
		return Response.ok(wsigActive).build(); 
	}

	// This method is used only for administration via web-console
	@Path("{status}")
	@GET
	public void getModifyStatus(@PathParam("status") String status, @Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse) throws IOException {
		modifyStatus(status);
		
		// Redirect to console home page
		URL consoleUrl = WSIGConfiguration.getAdminUrl(httpRequest);
		httpResponse.sendRedirect(consoleUrl.toString());
	}

	@Path("{status}")
	@PUT
	public Response putModifyStatus(@PathParam("status") String status, @Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse) throws IOException {
		modifyStatus(status);
		return Response.ok().build(); 
	}

	private void modifyStatus(String status) {
		log.info("WSIG agent command arrived ("+status+")");

		if (status.equalsIgnoreCase("connect")) {
			// Start WSIGAgent
			try {
				log.info("Starting WSIG agent...");
				JadeGateway.checkJADE();
			} catch (ControllerException e) {
				log.warn("Jade platform not present...WSIG agent not started", e);
			}			
			try {
				// Wait a bit for the registration of services before refreshing the page
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		} else if (status.equalsIgnoreCase("disconnect")) {
			// Stop WSIGAgent
			log.info("Stopping WSIG agent...");
			JadeGateway.shutdown();			
		} else {
			log.warn("WSIG agent command not implementated");
		}

		log.info("WSIG agent command elaborated");
	}
} 