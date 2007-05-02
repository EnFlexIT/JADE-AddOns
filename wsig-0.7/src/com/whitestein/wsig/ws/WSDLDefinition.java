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
package com.whitestein.wsig.ws;

//import jade.lang.acl.*;
//import jade.domain.FIPAAgentManagement.DFAgentDescription;
import java.net.URL;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.Operation;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author jna
 *
 * Class is extention of com.ibm.wsdl.DefinitionImpl.
 * The interface Definition and a conversion to ACL are required for wsigs purpose.
 * 
 * Note: this class functionality will be moved to translators
 *  
 */
public class WSDLDefinition {

	private URL location = null;
	private Definition definition;
	
	/**
	 * sets location of this document
	 * 
	 * @param location document's location
	 */
	public void setURL( URL location ) {
		this.location = location;
	}
	
	/**
	 * returns location of this document
	 * 
	 * @return URL of this document, null if it is not set
	 */
	public URL getURL() {
		return location;
	}
	
	/**
	 * sets wsdl definition
	 * 
	 * @param definition a wsdl definition
	 */
	public void setDefinition( Definition definition ) {
		this.definition = definition;
	}
	
	/**
	 * gives wsdl definition
	 * 
	 * @return a wsdl definition
	 */
	public Definition getDefinition() {
		return definition;
	}
	
	/**
	 * gives all operation's names
	 * 
	 * @return a collection of operation's names
	 */
	public Collection getAllOperations() {
		Collection col = new ArrayList();
		
		if ( null == definition ) {
			return col;
		}
		
		PortType pt;
		Operation op;
		Iterator it, ops;
		
		it = definition.getPortTypes().values().iterator();
		// go through all portTypes, but only main one is expected in a wsdl interface
		while ( it.hasNext() ) {
			pt = (PortType) it.next();
			ops = pt.getOperations().iterator();
			while ( ops.hasNext() ) {
				op = (Operation) ops.next();
				col.add( op.getName() );
			}
		}
		
		return col;
	}
	
//	/**
//	 * Creates ACL registration message of services described by this WSDL definition.
//	 * See also warning related to a content size in (@link jade.lang.acl.ACLMessage).
//	 *
//	 *
//	 *  @return makes ACL message for registration in DF
//	 */
//	public Object getACLforServiceRegistration() {
//		// create empty ACL message
//		jade.lang.acl.ACLMessage aclMessage = new ACLMessage( ACLMessage.REQUEST );
//		
//		
//		
//		// all is done, return message
//		return aclMessage;
//	}
	
//	/**
//	 *  generates DFAgentDescription for this WSDL
//	 * 
//	 * @return agent description
//	 */
//	public DFAgentDescription getDFAgentDescription() {
//		DFAgentDescription dfd = new DFAgentDescription();
//		return dfd;
//	}
	
	
}
