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

/**
 * @author jna
 *
 * provides methods to access message's structures
 * The type field is required as the call's distinguisher.
 *  
 */
public interface CalledMessage {
	
	/**
	 * gives the type of this message
	 * 
	 * @return type of message
	 */
	public String getType();
	
	/**
	 * showes if this message is a result message.
	 * Othervice the message is a request.
	 * 
	 * @return true, if this is a result returned
	 */
	public boolean isResponse();
	
	/**
	 * set a response
	 * 
	 * @param val value of this responce
	 */
	public void setResponse( boolean val );

	/**
	 * gives served operation bound with this message.
	 * A messages's direction is accessible through isResponse().
	 * 
	 * @return a served operation
	 */
	public ServedOperation getServedOperation();

	/**
	 * set served operation for this message
	 * 
	 * @param operation served operation
	 */
	public void setServedOperation( ServedOperation operation );

}