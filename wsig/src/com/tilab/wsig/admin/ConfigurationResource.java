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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.tilab.wsig.WSIGConfiguration;
import com.tilab.wsig.servlet.WSIGServlet;


@Path("/configuration")
public class ConfigurationResource {

	private static Logger log = Logger.getLogger(WSIGServlet.class.getName());

	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Configuration getConfiguration(@Context HttpServletRequest hsr) {

		Configuration conf = new Configuration(hsr);	  
		log.info("WSIG configuration retrieved.");	 
		return conf;
	}  

	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON,MediaType.TEXT_PLAIN})
	public Response updateConfiguration(Configuration conf) {

		/* The parameters passed in the Configuration object are set using
		 * the setters method of Configuration.class */	  
		log.info("WSIG configuration changed.");
		WSIGConfiguration.getInstance().store();
		return Response.ok().build();
	}    
} 