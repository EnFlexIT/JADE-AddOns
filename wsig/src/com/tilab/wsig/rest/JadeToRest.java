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

import com.tilab.wsig.soap.JadeToSoap;
import com.tilab.wsig.store.OperationResult;
import com.tilab.wsig.store.WSIGService;

public class JadeToRest {

	public JadeToRest() {		
	}

	public synchronized String convert(OperationResult opResult, WSIGService wsigService, String operationName) throws Exception {
		JadeToSoap jadeToSoap = new JadeToSoap();
		String bodyResponse = jadeToSoap.convert(opResult, wsigService, operationName).getSOAPBody().toString();

		return bodyResponse;
	}

	public static String convert(RestException e) throws Exception {
		String faultString = e.getFaultActor() +" "+e.getFaultCode()+" "+e.getFaultString() ;
		return faultString;
	}
}
