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

import java.security.Permission;
import java.security.PermissionCollection;

import java.util.StringTokenizer;


public class AuthPermission extends java.security.Permission implements jade.util.leap.Serializable {

	private int mask = 0;
	private String name;
	private transient String actions;
													
	public AuthPermission(String name, String actions) {
		super(name);
		if (name == null)
			throw new NullPointerException("Name can't be null");
		if (actions == null)
			throw new NullPointerException("Actions can't be null");
		this.mask = decodeActions(actions);
	}

	public String getActions() {
		if (actions == null)
			actions = encodeActions(this.mask);
		return actions;
	}
	
	public int hashCode() {
		return getName().hashCode();
	}

	public boolean equals(Object p) {
		if (!getClass().isInstance(p))
			return false;
		AuthPermission that = (AuthPermission) p;
		return (this.mask == that.mask) && that.getName().equals(getName());
	}

	public boolean implies(Permission p) {
		if (!getClass().isInstance(p))
			return false;
		AuthPermission that = (AuthPermission) p;
		boolean result = ((this.mask & that.mask) == that.mask) && that.getTarget().implies(getTarget());
		return result;
	}
	
	public PermissionCollection newPermissionCollection() {
		return new AuthPermissionCollection();
	}
	
	public PrincipalImpl getTarget() {
		return new PrincipalImpl(getName());
	}
	
	int getActionsMask() {
		return mask;
	}

	public String[] getAllActions() {
		return new String[] {};
	}

	private int decodeActions(String actions) {
		int mask = 0;
		if (actions == null)
			return mask;
		
		String[] allActions = getAllActions();
		StringTokenizer tokenizer = new StringTokenizer(actions, ", \r\n\f\t");
		while (tokenizer.hasMoreTokens()) {
			String action = tokenizer.nextToken();
			for (int i = 0; i < allActions.length; i++) {
				if (action.equals(allActions[i])) {
					mask |= 1 << i;
					continue;
				}
			}
		}
		return mask;
	}

	private String encodeActions(int mask) {
		StringBuffer sb = new StringBuffer();
		boolean comma = false;
		String[] allActions = getAllActions();
		
		for (int i = 0; i < allActions.length; i++) {
			int onebit = 1 << i;
			if ((mask & onebit) == onebit) {
				if (comma) sb.append(',');
				else comma = true;
				sb.append(allActions[i]);
			}
		}
		
		return sb.toString();
	}

	public String toString() {
		return "(" + getClass().getName() + " " + getName() + " " + getActions() + ")";
	}
}
