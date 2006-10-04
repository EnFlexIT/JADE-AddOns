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

import com.whitestein.wsig.struct.CalledMessageImpl;

import jade.lang.acl.ACLMessage;
import org.apache.log4j.Category;


/**
 * @author jna
 *
 *  FIPA Message implementation of CalledMessage interface.
 *  Concrete instance is based on ACL message given. 
 */
public class FIPAMessage extends CalledMessageImpl {
	
	/**
	 * the unique identificator for CalledMessages 
	 */
	public static final String TYPE = "FIPA ACL SL0";
	
	/**
	 * this acl message stored
	 */
	private ACLMessage aclMessage;

	/**
	 * to log messages
	 */
	private static Category cat = Category.getInstance(FIPAMessage.class.getName());

	/**
	 * creates an empty message
	 */
	public FIPAMessage() {
		this.aclMessage = new ACLMessage( ACLMessage.INFORM );
	}
	
	
	/**
	 * creates a message based on ACL message
	 * 
	 * @param aclMessage base message
	 */
	public FIPAMessage( ACLMessage aclMessage ) {
		this.aclMessage = aclMessage;
	}
	
	/**
	 * implements interface CalledMessage
	 * 
	 * @see CalledMessages com.whitestein.wsigs.structs.CalledMessage
	 */
	public String getType(){
		return TYPE;
	}
	
	/**
	 * provides this message as ACLMessage
	 * 
	 * @return this message
	 */
	public ACLMessage getACLMessage() {
		return aclMessage;
	}

	/**
	 * gives a string's representation of the message
	 */
	public String toString() {
		return SL0Helper.toString( this.aclMessage );
	}
	
}
