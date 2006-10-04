/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebService Integration Gateway (WSIG).
 *
 * The Initial Developer of the Original Code is
 * Whitestein Technologies AG.
 * Portions created by the Initial Developer are Copyright (C) 2004, 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Jozef Nagy (jna at whitestein.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.whitestein.wsig.struct;

import java.util.Hashtable;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Category;

import com.whitestein.wsig.*;
import com.whitestein.wsig.fipa.FIPAServiceIdentificator;
import com.whitestein.wsig.fipa.GatewayAgent;
import com.whitestein.wsig.ws.UDDIOperationIdentificator;
//import com.whitestein.wsigs.ws.DFToUDDI4j;
import jade.core.AID;

/**
 * @author jna
 *
 * stores a structures of served operations.
 */
public class ServedOperationStore {
	
	
	private static ServedOperationStore instance;
	
	// fipaServiceId to ServedOperation index
	private Hashtable fipaSIdToServedOperation = new Hashtable();
	// uddiOperationId to ServedOperation index
	private Hashtable uddiOIdToServedOperation = new Hashtable();
	// agentId to ServedOperations
	private Hashtable agentIdToServedOperations = new Hashtable();
	
	private static final String BASE = "operation";
	private static int count = 0;
	private static Category cat = Category.getInstance(ServedOperationStore.class.getName());
	
	public static synchronized ServedOperationStore getInstance() {
		if ( null == instance ) {
			instance = new ServedOperationStore();
		}
		return instance;
	}
	
	/**
	 * finds a served operation for a uddi's operation identificator.
	 * 
	 * @param uddiOId identificator
	 * @return operation found, else null
	 */
	public ServedOperation find( UDDIOperationIdentificator uddiOId ) {
		return (ServedOperation) uddiOIdToServedOperation.get( uddiOId );
	}
	
	/**
	 * finds a served operation for a fipa service identificator.
	 * 
	 * @param fipaSId identificator
	 * @return operation found, else null
	 */
	public ServedOperation find( FIPAServiceIdentificator fipaSId ) {
		return (ServedOperation) fipaSIdToServedOperation.get( fipaSId );
	}
	
	/**
	 * returns all operations served by an agent.
	 * If no operation is served, then an empty Collection is returned.
	 * 
	 * @param agentId request is for the agent
	 * @return operations served
	 */
	public Collection findAllServedOperations( AID agentId ) {
		Collection col = (Collection) agentIdToServedOperations.get(agentId);
		if ( null != col ) {
			return col;
		}
		return new HashSet();
	}
	
	/**
	 * generates new UDDI operation identificator
	 * 
	 * @return uddi operation identificator
	 */
	public static UDDIOperationIdentificator generateUDDIOperationId() {
		return new UDDIOperationIdentificator(
				Configuration.getInstance().getHostURI()
					+ Configuration.getInstance().getAccessPointPath(),
				generateName());
	}
	
	/**
	 * generates new FIPA service identificator
	 * 
	 * @return service identificator
	 */
	public static FIPAServiceIdentificator generateFIPAServiceId( ) {
		return new FIPAServiceIdentificator(
				Configuration.getInstance().getGatewayAID(),
				generateName());
	}
	
	/**
	 * generates new unique operation name
	 * 
	 * @return operation name
	 */
	private synchronized static String generateName() {
		if ( Integer.MAX_VALUE == count ) {
			cat.error(" Operation's count is overflowed.");
			// termination is needed to avoid a misfunctionality
		}
		return BASE + (count++);
	}

	/**
	 * adds a served operation into this
	 * 
	 * @param so served operation added
	 */
	public void put( ServedOperation so ) {
		if ( so == null ) {
			return;
		}
		OperationID oid = so.getOperationID();
		fipaSIdToServedOperation.put( oid.getFIPAServiceIdentificator(), so);
		uddiOIdToServedOperation.put(oid.getUDDIOperationIdentificator(), so);
		cat.debug(" operation is added : " + oid.getFIPAServiceIdentificator().getAgentID()
				+ " " + oid.getFIPAServiceIdentificator().getServiceName()
				+ ",   " + oid.getUDDIOperationIdentificator().getAccessPoint()
				+ " " + oid.getUDDIOperationIdentificator().getWSDLOperation()
				);
		
		AID agentId = oid.getFIPAServiceIdentificator().getAgentID();
		Collection col = (Collection) agentIdToServedOperations.get( agentId );
		if ( null == col ) {
			col = new HashSet();
			agentIdToServedOperations.put( agentId, col );
		}
		col.add(so);

		// inform also GatewayAgent
		GatewayAgent myGateway = GatewayAgent.getInstance();
		myGateway.addOperationForLog( so );
	}
	
	/**
	 * adds a served operation into this
	 * 
	 * @param col servedOperations in a collection 
	 */
	public void put( Collection col ) {
		if ( col == null || col.isEmpty()) {
			return;
		}
		ServedOperation op;
		for ( Iterator i = col.iterator(); i.hasNext();) {
			op = (ServedOperation) i.next();
			put( op );
		}
	}
	
	/**
	 * removes a served operation into this
	 * 
	 * @param so served operation deleted
	 */
	public void remove( ServedOperation so ) {
		if ( so == null ) {
			return;
		}
		OperationID oid = so.getOperationID();
		fipaSIdToServedOperation.remove( oid.getFIPAServiceIdentificator());
		uddiOIdToServedOperation.remove(oid.getUDDIOperationIdentificator());
		
		AID agentId = oid.getFIPAServiceIdentificator().getAgentID();
		Collection col = (Collection) agentIdToServedOperations.get( agentId );
		if ( null != col ) {
			col.remove( so );
		}

		// inform also GatewayAgent
		GatewayAgent myGateway = GatewayAgent.getInstance();
		myGateway.removeOperationForLog( so );
	}

}
