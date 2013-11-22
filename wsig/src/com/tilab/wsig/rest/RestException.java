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
package com.tilab.wsig.rest;

public class RestException extends Exception {

	public static final String FAULT_CODE_CLIENT = "Client";
	public static final String FAULT_CODE_SERVER = "Server";
	
	public static final String FAULT_STRING_TIMEOUT = "TIMEOUT";
	
	public static final String FAULT_ACTOR_WSIG = "WSIG core";
	
	String faultString;
	String faultCode;
	String faultActor;
	
	public RestException(String faultCode, String faultString, String faultActor) {
		super();
		
		this.faultCode = faultCode;
		this.faultString = faultString;
		this.faultActor = faultActor;
	}
	
	public String getFaultString() {
		return faultString;
	}

	public String getFaultCode() {
		return faultCode;
	}

	public String getFaultActor() {
		return faultActor;
	}
}
