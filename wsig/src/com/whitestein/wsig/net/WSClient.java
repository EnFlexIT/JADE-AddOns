/*
 * Created on Nov 17, 2004
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
package com.whitestein.wsig.net;

import java.io.ByteArrayOutputStream;
import java.util.Properties;
import javax.xml.soap.SOAPMessage;
import org.apache.log4j.Category;

import com.whitestein.wsig.struct.Call;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.struct.ReturnMessageListener;
import com.whitestein.wsig.struct.ServedOperation;
import com.whitestein.wsig.struct.ServedOperationStore;
import com.whitestein.wsig.ws.WSMessage;

/**
 * @author jna
 *
 * Implements the interface for an WS client HTTP request.
 */
public class WSClient implements SOAPHandler, ReturnMessageListener {

	private WSMessage returnedMessage;
	private Call call;
	private Category cat = Category.getInstance(SOAPHandler.class.getName());

	/**
	 * performs a request.
	 * The close method is used to close a call blocked.
	 * 
	 * @param header HTTP request's header
	 * @param soap  HTTP request's body
	 * @return a response
	 */
	public SOAPMessage doRequest( Properties header, SOAPMessage soap ) {
		WSMessage msg = new WSMessage( soap );
		msg.setAccessPoint(	Connection.createAccessPoint( header ));
		msg.setResponse( false );
		synchronized ( this ) {
			sendWSMessage( msg, this );
			
			try {
				// wait active for a message returned
				while ( call != null && ! call.isReceived() ) {
					this.wait(5000);
				}
			}catch (InterruptedException ie) {
				cat.error(ie);
			}
		}
		if ( returnedMessage != null ) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				returnedMessage.getSOAPMessage().writeTo(baos);
				cat.debug("A SOAP returned to a client: " + baos.toString());
			}catch (Exception e ){
				cat.error(e);
			}
			return returnedMessage.getSOAPMessage();
		}else {
			cat.debug("A SOAP returned to a client: NULL");
			return null;
		}
	}

	/**
	 * closes a request processed
	 *
	 */
	public void close() {
		//  cancel/close the call
		call.close();
		call = null;
		synchronized ( this ) {
			this.notifyAll();
		}
	}

	/**
	 * sends a WSMessage.
	 * The WSMessage must be set correctly.
	 * 
	 * @param msg a message
	 * @param listener listener for a message returned
	 */
	public void sendWSMessage(WSMessage msg, ReturnMessageListener listener) {
		ServedOperation so = ServedOperationStore.getInstance().find(
				msg.getTheFirstUDDIOperationId() );
		//isAnswering = so.isAnswering();
		if ( null == so ) {
			cat.debug( "Operation is not found for "
					+ msg.getTheFirstUDDIOperationId().getAccessPoint() + " -> "
					+ msg.getTheFirstUDDIOperationId().getWSDLOperation() );
			listener.setReturnedMessage( null );
			return;
		}
		call = so.createCall();
		try {
			cat.debug(" WSIGS is called by a http access point now.");
			call.setMessage( msg );
			call.setReturnMessageListener( listener );
			call.invoke();
		}catch (Exception e) {
			cat.error(e);
		}
	}
	
	/**
	 * sets message returned by a service
	 * 
	 * @param retMsg message returned
	 */
	public synchronized void setReturnedMessage( CalledMessage retMsg ) {
		cat.debug(" A WSClient's listener is invoked.");
		returnedMessage = (WSMessage) retMsg;
		//isListenerInformed = true;
		call = null;  //is not needed anymore
		this.notifyAll(); // weak up waiters
	}
	

}
