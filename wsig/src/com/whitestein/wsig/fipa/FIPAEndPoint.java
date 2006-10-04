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
package com.whitestein.wsig.fipa;

// import com.whitestein.wsig.Gateway;
import com.whitestein.wsig.fipa.GatewayAgent;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.struct.EndPointImpl;
import com.whitestein.wsig.struct.ReturnMessageListener;

import java.lang.Error;
import jade.lang.acl.ACLMessage;

import org.apache.log4j.Logger;


/**
 * @author jna
 *
 * makes access to FIPA ganets.
 * An agent's service is called by this class. 
 */
public class FIPAEndPoint extends EndPointImpl {

	/**
	 * provides unique type for this FIPAEndPoint.
	 * The reference of FIPAMessage.type is used.
	 */
	public static final String TYPE = FIPAMessage.TYPE;

	private FIPAServiceIdentificator fipaSId;
	private ACLMessage aclSent;
	private static Logger log = Logger.getLogger( FIPAEndPoint.class.getName());
	
	public FIPAEndPoint( FIPAServiceIdentificator fipaSId ) { //, Gateway gw ) {
		super(FIPAEndPoint.TYPE);
		//this.gw = gw;
		this.fipaSId = fipaSId;
	}
	
	public FIPAServiceIdentificator getFIPAServiceIdentificator() {
		return fipaSId;
	}
	
	/**
	 * sends native FIPA ACL message
	 * 
	 * @param opMsg message to send
	 */
	protected void nativeSend( CalledMessage cMsg, ReturnMessageListener listener ) {
		// to send FIPA ACL message
		log.debug(" FIPA sending.");

		// inform also GatewayAgent
		GatewayAgent myGateway = GatewayAgent.getInstance();
		myGateway.addMessageToLog( cMsg.getServedOperation(), cMsg );

		if ( null == cMsg || ! (cMsg instanceof FIPAMessage )) {
			// is not native message, never will be happen
			// checking for type is done in send method
			throw new Error("FIPAMessage is expected.");
		}
		if ( GatewayAgent.getInstance() != null ) {
			GatewayAgent.getInstance().send( (FIPAMessage) cMsg, listener );
		}
	}
	
	/**
	 * removes a registered listener
	 * FIPA Cancel is produced.
	 * 
	 * @param listener a listener to remove 
	 */
	public void removeReturnMessageListener( ReturnMessageListener listener ) {
		GatewayAgent.getInstance().removeReturnMessageListener( listener );
	}

}
