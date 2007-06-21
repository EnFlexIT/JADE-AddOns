/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France Télécom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/**
 * Created on 6 dev. 2006 by Vincent Louis
 */

package jade.semantics.interpreter.sips.adapters;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.util.leap.ArrayList;

import java.util.Date;

/**
 * This SIP adapter is the base class to create "neutral" application-specific
 * SIPs, that is, SIPs that neither produce nor consume any SR. Such SIPs make
 * it possible to be notified that a particular pattern of SR to observer is
 * being interpreted. They are particularly useful to handle notification tasks
 * (e.g. updating a GUI by adding or removing proper behaviours).
 * 
 * <br>The right way to use this adapter simply consists in overriding the
 * abstract method notify(), which must specify what to do when notified the SR
 * to observe is being interpreted.
 * 
 * @author Vincent Louis - France Telecom
 */
public abstract class NotificationSIPAdapter extends ApplicationSpecificSIPAdapter {

	/***************************************************************************
	 **** CONSTRUCTORS
	 **************************************************************************/
	
	/**
	 * Create a Notification SIP, which observes a given pattern of SR and has a
	 * given deadline to be applied. A <code>null</code> deadline does not set
	 * any "timeout" or "one shot" option of the SIP.
	 * 
	 * @param capabilities the semantic capabilities that hold the SIP table.
	 * @param pattern the pattern of SR to observe.
	 * @param timeout the deadline (given as a Java Date) attached to the SIP.
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(Date)
	 * @see #NotificationSIPAdapter(SemanticCapabilities, Formula)
	 */
	public NotificationSIPAdapter(SemanticCapabilities capabilities, Formula pattern, Date timeout) {
		super(capabilities, pattern, timeout);
	}

	/**
	 * Create a Notification SIP, which observes a given pattern of SR and has a
	 * given timeout. A null timeout only sets the SIP in "one shot" mode
	 * (without timeout).
	 * 
	 * @param capabilities the semantic capabilities that hold the SIP table.
	 * @param pattern the pattern of SR to observe.
	 * @param timeout the timeout (in milliseconds) attached to the SIP.
	 *                If <code>0</code>, the SIP is only "one shot".
	 *                
	 * @see ApplicationSpecificSIPAdapter#setTimeout(long)
	 * @see #NotificationSIPAdapter(SemanticCapabilities, Formula)
	 */
	public NotificationSIPAdapter(SemanticCapabilities capabilities, Formula pattern, long timeout) {
		super(capabilities, pattern, timeout);
	}

	/**
	 * Create a Notification SIP, which observes a given pattern of SR. Note
	 * that this SR is generally a belief of the agent, and thus is of the form
	 * <code>(B ??myself <i>belief_to_match</i>)</code> (<code>??myself</code>
	 * is a standard meta-reference that matches the interpreting semantic
	 * agent).
	 * 
	 * @param capabilities the semantic capabilities that hold the SIP table.
	 * @param pattern the pattern of SR to observe.
	 */
	public NotificationSIPAdapter(SemanticCapabilities capabilities, Formula pattern) {
		super(capabilities, pattern);
	}

	/**
	 * Create a Notification SIP, which observes a given pattern of SR and has a
	 * given deadline to be applied. A <code>null</code> deadline does not set
	 * any "timeout" or "one shot" option of the SIP.
	 * 
	 * @param capabilities the semantic capabilities that hold the SIP table.
	 * @param pattern the pattern of SR to observe.
	 * @param timeout the deadline (given as a Java Date) attached to the SIP.
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(Date)
	 * @see #NotificationSIPAdapter(SemanticCapabilities, String)
	 */
	public NotificationSIPAdapter(SemanticCapabilities capabilities, String pattern, Date timeout) {
		super(capabilities, pattern, timeout);
	}

	/**
	 * Create a Notification SIP, which observes a given pattern of SR and has a
	 * given timeout. A null timeout only sets the SIP in "one shot" mode
	 * (without timeout).
	 * 
	 * @param capabilities the semantic capabilities that hold the SIP table.
	 * @param pattern the pattern of SR to observe.
	 * @param timeout the timeout (in milliseconds) attached to the SIP.
	 *                If <code>0</code>, the SIP is only "one shot".
	 *                
	 * @see ApplicationSpecificSIPAdapter#setTimeout(long)
	 * @see #NotificationSIPAdapter(SemanticCapabilities, String)
	 */
	public NotificationSIPAdapter(SemanticCapabilities capabilities, String pattern, long timeout) {
		super(capabilities, pattern, timeout);
	}

	/**
	 * Create a Notification SIP, which observes a given pattern of SR. Note
	 * that this SR is generally a belief of the agent, and thus is of the form
	 * <code>(B ??myself <i>belief_to_match</i>)</code> (<code>??myself</code>
	 * is a standard meta-reference that matches the interpreting semantic
	 * agent).
	 * 
	 * @param capabilities the semantic capabilities that hold the SIP table.
	 * @param pattern the pattern of SR to observe.
	 */
	public NotificationSIPAdapter(SemanticCapabilities capabilities, String pattern) {
		super(capabilities, pattern);
	}

	/***************************************************************************
	 **** OVERRIDDEN METHODS
	 **************************************************************************/
	
	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
	 */
	protected final ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {
		notify(applyResult, sr);
		return result;
	}

	/***************************************************************************
	 **** METHODS TO OVERRIDE
	 **************************************************************************/

	/**
	 * This method must be overridden to specify what to do when the SR to
	 * observe is interpreted.
	 * 
	 * @param applyResult result of the matching of the observed SR against the
	 *                    pattern to observe
	 * @param sr input SR that matches the pattern of SR to observe
	 */
	protected abstract void notify(MatchResult applyResult, SemanticRepresentation sr);
	
}