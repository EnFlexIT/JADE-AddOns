/*
 * Created on Jun 24, 2004
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

import com.whitestein.wsig.translator.*;

import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Category;

/**
 * @author jna
 *
 * represents a call to service operation.
 * An asynchronous sending and receiving is implemented.
 */
public class Call implements ReturnMessageListener, Runnable{

	private ServedOperation so;
	private EndPoint endPoint;
	private CalledMessage retMsg;
	private CalledMessage message;
	private Collection messages;
	private ReturnMessageListener listener;
	private Translator translator, backTranslator;
	private int messagesCount = 0;
	private int returnedCount = 0;
	private boolean isReceived = false;
	private static Category cat = Category.getInstance(Call.class.getName());

	/**
	 * creates new call to the served operation
	 * 
	 * @param so served operation, which is called
	 */
	public Call(ServedOperation so) {
		this.so = so;
		this.endPoint = so.getEndPoint();
	}
	
	/**
	 * performs this call
	 * Properties of this call are already set.
	 * The method returns without waiting for a sending to be completed.
	 * The sending is computed in a thread.
	 *
	 */
	public void invoke() {
		if ( ! endPoint.isTheSameType( message )) {
			// translation is needed
			backTranslator = TranslatorStore.getInstance().getTranslator(
					endPoint.getType(),
					message.getType() );
			translator = TranslatorStore.getInstance().getTranslator(
					message.getType(),
					endPoint.getType() );
		}
		try {
			if ( translator != null ) {
				messages = translator.translate(message);
				Iterator it;
				
				// count the messages as the first to avoid misunderstanding
				//  the end of messages received
				for( it = messages.iterator(); it.hasNext(); it.next()) {
					messagesCount ++ ;
				}
			}else {
				messagesCount = 1;
			}

			// proces message(s) in a thread
			new Thread(this).start();
		} catch ( MessageIsNotNativeException e ) {
			cat.error(e);
		} catch ( Exception e ) {
			cat.error(e);
		}
	}
	
	/**
	 * implements the interface Runnable.
	 * Messages are sent in a thread.
	 */
	public void run() {
		try {
			if (messages != null) {
				// a collection of messages exists
				CalledMessage msg;
				Iterator it = messages.iterator();
				while ( it.hasNext() ) {
					msg = (CalledMessage) it.next();
					endPoint.send( msg, this );
				}
			}else {
				// only one original message exists
				endPoint.send( message, this );
			}
			
		}catch (MessageIsNotNativeException e) {
			cat.error(e);
		}
	}

	/**
	 * cleans up before terminated
	 *
	 */
	public void close() {
		endPoint.removeReturnMessageListener( this );
	}
	
	/**
	 * get received message if a return is expected.
	 * Otherwice return null message.
	 * 
	 */
	public CalledMessage getReceivedMessage() {
		return retMsg;
	}
	
	/**
	 *  checks if a received message is come.
	 *  It must be checked an available returned message.
	 */
	public boolean isReceived() {
		return isReceived;
	}
	
	/**
	 * sets the content or agruments of this call.
	 * A served operation of this call is set in the message.
	 * 
	 * @param message arguments to call service's operation
	 */
	public void setMessage( CalledMessage message ) {
		this.message = message;
		message.setServedOperation( so );
	}
	
	/**
	 * takes a returned message to proccess.
	 * It implements ReturnMessageListener. 
	 *
	 * @param retMsg returned message
	 */
	public synchronized void setReturnedMessage( CalledMessage retMsg ) {
		// count messages returned
		returnedCount ++;

		fillAsReturned( retMsg );
		
		if ( backTranslator != null 
			&& retMsg != null
			&& ( backTranslator.getInputType().compareTo( retMsg.getType()) == 0 ) ) {
			try {
				Iterator it = backTranslator.translate(retMsg).iterator();
				this.retMsg = null;
				if( it.hasNext() ) {
					this.retMsg = (CalledMessage) it.next();
					
					// collect messages in a future version,
					// today only the first is returned
				}
			}catch (Exception e ) {
				// this.retMsg = TRANSLATION_ERROR;
			}
		}else {
			// else simply store
			this.retMsg = retMsg;
		}
		if ( returnedCount == messagesCount ) {
			// whole answer is received
			isReceived = true;
			
			// inform the listener
			if ( listener != null ) {
				listener.setReturnedMessage( this.retMsg );
			}
		}
	}
	
	/**
	 * sets listener for a message returned
	 * 
	 * @param listener 
	 */
	public void setReturnMessageListener( ReturnMessageListener listener ) {
		this.listener = listener;
	}

	/**
	 * fills message's properties.
	 * Properties are set as a returned message.
	 * 
	 * @param retMsg message to fill
	 */
	private void fillAsReturned( CalledMessage retMsg ) {
		if ( retMsg != null ) {
			retMsg.setServedOperation( so );
			retMsg.setResponse( true );
		}
	}

}
