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
package com.whitestein.wsig.ws;

import com.whitestein.wsig.struct.CalledMessageImpl;
import com.whitestein.wsig.test.TestSOAPClient;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBodyElement;

import org.apache.log4j.Category;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author jna
 *
 *  covers the webservice's SOAP message
 */
public class WSMessage extends CalledMessageImpl {

	/**
	 * the unique identificator for CalledMessages 
	 */
	public static final String TYPE = "WS SOAP";
	private SOAPMessage myMsg;
	private UDDIOperationIdentificator id;
	private String accessPoint = "";
	private static Category cat = Category.getInstance(WSMessage.class.getName());

	/**
	 * creates a message
	 * 
	 * @param msg base SOAP message
	 */
	public WSMessage( SOAPMessage msg ) {
		this.myMsg = msg;
	}
	
	/**
	 * gives SOAP message stored
	 * 
	 * @return message stored
	 */
	public SOAPMessage getSOAPMessage() {
		return myMsg;
	}
	
	/**
	 * implements interface CalledMessage
	 * 
	 * @see CalledMessages com.whitestein.wsigs.structs.CalledMessage
	 */
	public String getType(){
		return TYPE;
	}
	
	//public void setId( UDDIOperationIdentificator id ) {
	//	this.id = id;
	//}
	
	//public UDDIOperationIdentificator getId() {
	//	return id;
	//}
	
	public void setAccessPoint( String accessPoint ) {
		this.accessPoint = accessPoint;
	}
	
	public String getAccessPoint() {
		return accessPoint;
	}
	
	public UDDIOperationIdentificator getTheFirstUDDIOperationId() {
		if ( null == myMsg ) {
			return null;
		}
		UDDIOperationIdentificator id = null;
		String operation = "";
		try {
			Iterator it = myMsg.getSOAPPart().getEnvelope().getBody().getChildElements();
			if( it.hasNext() ) {
				operation = ((SOAPBodyElement) it.next()).getElementName().getLocalName();
			}
			id = new UDDIOperationIdentificator( getAccessPoint(), operation );
		} catch ( Exception e ) {
			cat.error(e);
		}
		// cat.debug(" The first identifier is: " + id.getAccessPoint() + ", " + id.getWSDLOperation());
		return id;
	}

        /**
         * gives a string's representation of the message
         */
        public String toString() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			myMsg.writeTo(baos);
			return baos.toString();
		}catch (Exception e ){
			cat.error(e);
		}
		return null;
	}

}
