/*
 * Created on Jun 23, 2004
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
package com.whitestein.wsig.struct;

import com.whitestein.wsig.ws.WSDLDefinition;

/**
 * @author jna
 *
 * identifies and makes access to an operation served.
 * The ServedOperation covers a FIPA service or a WSDL operation.
 * 
 * REM: to set isAnswering as false does not mean, that no result is produced
 * in a transport used by SOAP's delivering,
 * for example HTTP's transport may produce a response without a content 
 */
public class ServedOperation {
	
	private OperationID id;
	private EndPoint endPoint;
	private boolean answering;
	private boolean active = true;
	private WSDLDefinition wsdl;
	
	/**
	 * creates access class to the operation
	 * 
	 * @param id identificator of the operation
	 * @param ep concrete point to call the service's operation
	 * @param answering if a return message is generated
	 */
	public ServedOperation( OperationID id, EndPoint ep, boolean answering ){
		this.id = id;
		this.endPoint = ep;
		this.answering = answering;
	}

	/**
	 * creates a call to service
	 * 
	 * @return access class to call configuration
	 */
	public synchronized Call createCall() {
		if ( active ) {
			Call c;
			c = new Call( this );
			// register open call c
			return c;
		}
		return null;
	}
	
	/**
	 * gives EndPoint served by this.
	 * 
	 * @return end point served
	 */
	public EndPoint getEndPoint() {
		return endPoint;
	}
	
	/**
	 * returns ID oject for this operation
	 * 
	 * @return ID object for this
	 */
	public OperationID getOperationID() {
		return id;
	}
	
	/**
	 * returns true if this generates answer
	 */
	public boolean isAnswering() {
		return answering;
	}
	
	/**
	 * terminates this operation
	 *
	 */
	public void close() {
		synchronized (this) {
			active = false;
		}
		// terminate all open calls
	}
	
	/**
	 * sets a wsdl definition for this operation
	 * This operation is included in that wsdl.
	 * 
	 * @param wsdl a definition
	 */
	public void setWSDL( WSDLDefinition wsdl ) {
		this.wsdl = wsdl;
	}
	
	/**
	 * returns a wsdl definition of this operation.
	 * This operation is included in that wsdl.
	 * 
	 * @return a wsdl definition
	 */
	public WSDLDefinition getWSDL() {
		return wsdl;
	}
}
