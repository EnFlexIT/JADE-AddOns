/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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
import jade.security.dummy.*;

import jade.core.AID;
import jade.core.ContainerID;


public class PrincipalImpl extends DummyPrincipal implements jade.util.leap.Serializable {
	
	static final char dot = '.';
	
	public PrincipalImpl() {
		super();
	}
	
	public PrincipalImpl(String name) {
		super(name);
	}
	
	public UserPrincipal getUser() {
		if (name1 == null)
			return null;
		else
			return new PrincipalImpl(name1);
	}
	
	public PrincipalImpl getParent() {
		if (name2 != null)
			return (PrincipalImpl)getUser();
			
		int pos = name1.indexOf(dot);
		if (pos != -1)
			return new PrincipalImpl(name1.substring(0, pos));
		else
			return null;
	}
	
	public boolean implies(PrincipalImpl p) {
		boolean impl2 = true;
		if (p.name2 != null)
			impl2 = p.name2.equals(name2);
	
		return impl2 && (p.name1.equals(name1) || p.name1.startsWith(name1 + dot));
	}

	public boolean equals(Object o) {
		if (o instanceof String)
			return getName().equals(o);
		if (o instanceof PrincipalImpl)
			return getName().equals(((PrincipalImpl)o).getName());
		return false;
	}

}
