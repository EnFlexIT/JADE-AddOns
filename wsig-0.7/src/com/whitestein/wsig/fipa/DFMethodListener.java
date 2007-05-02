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
package com.whitestein.wsig.fipa;

import jade.util.leap.List;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 *
 *  listens for incomming DF gateway request.
 *  In future will be moved into WSIG.
 * 
 * @author jna
 */
public interface DFMethodListener {
	
	
	/**
	 * performs registration
	 * 
	 * @param description
	 * @param aid
	 * @throws Exception
	 */
	public void registerAction(Agent agent, DFAgentDescription dfad, AID aid ) throws Exception;
	
	/**
	 * performs deregistration
	 * 
	 * @param description
	 * @param aid
	 * @throws Exception
	 */
	public void deregisterAction(DFAgentDescription dfad, AID aid ) throws Exception;
	
	/**
	 * performs modification
	 * 
	 * @param modify
	 * @param aid
	 * @throws Exception
	 */
	public void modifyAction( Modify modify, AID aid ) throws Exception;
	
	/**
	 * performs searching.
	 * The list exists after superclasse's call and is passed by the generatedList parameter. 
	 * 
	 * @param search searching request
	 * @param aid requester
	 * @param generatedList already generated list by suprclass
	 * @return
	 * @throws Exception
	 */
	public List searchAction( Search search, AID aid, List generatedList ) throws Exception;
	
}
