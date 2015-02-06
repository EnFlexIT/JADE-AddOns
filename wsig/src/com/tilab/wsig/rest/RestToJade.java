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

import org.apache.axis.Message;
import org.xml.sax.helpers.DefaultHandler;

import com.tilab.wsig.soap.SoapToJade;
import com.tilab.wsig.store.WSIGService;

public class RestToJade extends DefaultHandler {

	public RestToJade() {		
	}
	
	public synchronized Object convert(String message, WSIGService wsigService, String operationName) throws Exception {
		message = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:impl=\"urn:"+operationName+"\">\n"+
				"<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"+
				message+ 
				"</soapenv:Body>\n"+
				"</soapenv:Envelope>";
				
		Message soapRequest =  new Message(message); 
		SoapToJade soapToJade = new SoapToJade();
		return soapToJade.convert(soapRequest, wsigService, operationName);
	}	
}

