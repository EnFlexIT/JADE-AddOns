/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB S.p.A. 

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

package jade.security.impl;

import jade.security.*;

import jade.util.leap.List;
import jade.util.leap.ArrayList;

import jade.core.AID;
import jade.core.ContainerID;


public class PrincipalImpl implements AgentPrincipal, ContainerPrincipal, jade.util.leap.Serializable {
	
	protected static final char sep = '/';
	protected static final char dot = '.';
	
	protected String name1 = null;
	protected String name2 = null;

	public PrincipalImpl() {
	}
	
	public PrincipalImpl(String name) {
		if (name.indexOf(sep) == -1)
			name = name + sep;
		int pos = name.indexOf(sep);
		
		name1 = pos > 0 ? name.substring(0, pos) : null;
		name2 = pos < name.length() - 1 ? name.substring(pos + 1, name.length()) : null;
	}
	
	public PrincipalImpl(AID agentID, String ownership) {
		name1 = ownership != null ? ownership : null;
		name2 = agentID != null ? agentID.getName() : null;
	}
	
	public PrincipalImpl(ContainerID containerID, String ownership) {
		name1 = ownership != null ? ownership : null;
		name2 = containerID != null ? containerID.getName() : null;
	}
	
	public String getOwnership() {
		return (name1 != null) ? name1 : "";
	}
	
	public String getName() {
		return ((name1 != null) ? name1 : "") + ((name2 != null) ? sep + name2 : "");
	}
	
	public AID getAgentID() {
		return name2 != null ? new AID(name2, AID.ISGUID) : null;
	}
	
	public ContainerID getContainerID() {
		return name2 != null ? new ContainerID(name2, null) : null;
	}
	
	public String toString() {
		return getName();
	}
	
	public boolean equals(Object o) {
		return (o != null) && getName().equals(o.toString());
	}
	
	PrincipalImpl getParent() {
		if (name2 != null)
			return new PrincipalImpl(getOwnership());
		
		if (name1 == null)
			return null;
		int pos = name1.indexOf(dot);
		return pos != -1 ? new PrincipalImpl(name1.substring(0, pos)) : new PrincipalImpl();
	}
	
	public boolean implies(JADEPrincipal p) {
		return (p instanceof PrincipalImpl) ? implies((PrincipalImpl)p) : false;
	}

	public boolean implies(PrincipalImpl p) {
		boolean impl1 = (p.name1 == null) || name1 != null && (name1.startsWith(p.name1 + dot) || name1.equals(p.name1));
		boolean impl2 = (p.name2 == null) || name2 != null && (name2.startsWith(p.name2 + '@') || name2.equals(p.name2));
	
		return impl1 && impl2;
	}
	
	public List getAllImplied() {
		AID aid = getAgentID();
		AID shortaid = null;
		if (aid != null && aid.getName().indexOf('@') != -1)
			shortaid = new AID(aid.getName().substring(0, aid.getName().indexOf('@')), AID.ISGUID);

		List implied = new ArrayList();
		PrincipalImpl p = this;
		while (p != null) {
			if (aid != null) {
				PrincipalImpl p1 = new PrincipalImpl(aid, p.getOwnership());
				implied.add(p1);
			}
			
			if (shortaid != null) {
				PrincipalImpl p2 = new PrincipalImpl(shortaid, p.getOwnership());
				implied.add(p2);
			}
			
			PrincipalImpl p3 = new PrincipalImpl((AID)null, p.getOwnership());
			implied.add(p3);

			p = p.getParent();
		}
		return implied;
	}

}
