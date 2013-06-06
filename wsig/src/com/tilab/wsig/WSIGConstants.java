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

package com.tilab.wsig;

import java.text.SimpleDateFormat;

public interface WSIGConstants {

	public static final String AGENT_TYPE = "WSIG Agent";
	public static final String WSIG_FLAG = "wsig";
	public static final String WSIG_MAPPER = "wsig-mapper";
	public static final String WSIG_PREFIX = "wsig-prefix";
	public static final String WSIG_HIERARCHICAL_TYPE = "wsig-hierarchical-type";
	public static final String WSIG_ONTOLOGY_CLASSNAME = "wsig-ontology-classname";
	
	public static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	public static final String RESULT_CONVERTER_SUFFIX = "ResultConverter";
	public static final String FAULT_CONVERTER_SUFFIX = "FaultConverter";
	
	public static final String HTTP_HEADER_PREFIX = "HTTP";
	public static final String WSIG_HEADER_PREFIX = "WSIG";
	public static final String WSIG_HEADER_CLIENT_IP = "client-ip";
}
