/*
 * Created on Jul 2, 2004
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

/**
 * @author jna
 *
 * covers the end point to service's operation.
 * It accepts native calling messages only.
 * The possibility to convert messages may be done internaly by a class extension or externaly with a conversion function. 
 */
public interface EndPoint {

	/**
	 * gets the type of this end point
	 * 
	 * @return type of the end point
	 */
	public String getType();

	/**
	 * checks the message's type 
	 * 
	 * @param cMsg message to check
	 * @return checking for the same type of the message
	 */
	public boolean isTheSameType(CalledMessage cMsg);
	
	/**
	 * sends the message to this end point service
	 * 
	 * The nativeSend method is called at the end.
	 * The subclasses for specific protocols are responsible to implement its own nativeSend method.
	 * The listener waits for an answer message.
	 *  
	 * @param cMsg message to be sent
	 * @param listener waiter for a value(s) returned
	 */
	public void send(CalledMessage cMsg, ReturnMessageListener listener) throws MessageIsNotNativeException;
	
	/**
	 * removes a waiting listener
	 * A registered listener is removed from this end point.
	 * If a listener is not registered, then nothing is done.
	 *   
	 * @param listener a listener to remove
	 */
	public void removeReturnMessageListener( ReturnMessageListener listener );
}