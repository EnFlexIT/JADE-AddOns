/*
 * Created on Jun 17, 2004
 *
 */

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
 * Portions created by the Initial Developer are Copyright (C) 2004
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
package com.whitestein.wsig.fipa;

import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.core.AID;

/**
 * Identifies unique FIPA service.
 * 
 * @author jna
 *
 * Pair (Agent ID, ServiceDesccription) identifies the FIPA service.
 */
public class FIPAServiceIdentificator {
	
	private AID agentID;
	private ServiceDescription sd;
	private String serviceName;

	/**
	 * constructs the new identificator
	 * 
	 * @param agentID agent identificator for service
	 * @param sd service's description
	 */
	public FIPAServiceIdentificator(AID agentID, ServiceDescription sd) {
		this.agentID = agentID;
		this.sd = sd;
		serviceName = sd.getName();
	}
	
	/**
	 * constructs the new identificator
	 * 
	 * @param agentID agent identificator for service
	 * @param serviceName service's name
	 */
	public FIPAServiceIdentificator(AID agentID, String serviceName) {
		this.agentID = agentID;
		this.serviceName = serviceName;
	}
	
	/**
	 * joins two hash codes
	 * 
	 * @param a hash code
	 * @param b hash code
	 * @return joined hash
	 */
	private int joinHashCodes( int a, int b) {
		return ( a >> 16 + (b << 17) >> 1 );
	}
	
	/**
	 * gives hash code for this.
	 * 
	 * @return hash code
	 */
	public int hashCode() {
		if ( null == this.getAgentID() || null == this.getServiceName() ) {
			return 0;
		}
		return joinHashCodes(this.getAgentID().hashCode(), this.getServiceName().hashCode());
	}
	
	/**
	 * indicates equal identificators.
	 * The same agent ID and the same operation gives an equal FIPAServiceIdentificator.
	 */
	public boolean equals( Object o ) {
		if ( (o != null)
			&& (o instanceof FIPAServiceIdentificator)
			&& (this.getAgentID() != null)
			&& (this.getServiceName() != null) ) {
			FIPAServiceIdentificator fsid = (FIPAServiceIdentificator) o;
			return
				( this.getAgentID().equals( fsid.getAgentID())
				&& this.getServiceName().equalsIgnoreCase( fsid.getServiceName()) );
		}
		return false;
	}
	
	/**
	 * get agent identificator
	 * 
	 * @return
	 */
	public AID getAgentID() {
		return agentID;
	}
	
	/**
	 * get service description
	 * 
	 * @return
	 */
	private ServiceDescription getServiceDescription() {
		return sd;
	}

	/**
	 * get service name
	 * 
	 * @return
	 */
	public String getServiceName() {
		return serviceName;
	}
}
