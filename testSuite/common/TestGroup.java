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

package test.common;

import jade.core.Agent;

/**
   Class representing a group of tests (often related to a given 
   single functionality) that have to be executed in sequence and 
   require similar configurations
   @author Giovanni Caire - TILAB
 */
public class TestGroup {
	private String[] testClassNames;
	private int cnt;
  private Object[] args;
	
	public TestGroup(String[] tests) {
		testClassNames = (tests != null ? tests : new String[0]);
		cnt = 0;
	}
	
	public Test next() throws TestException {		
		String className = null;
		try {
			className = testClassNames[cnt++];
			Test t = (Test) Class.forName(className).newInstance();
			t.setGroupArguments(args);
			return t;
		}
		catch (IndexOutOfBoundsException ioobe) {
			return null;
		}
		catch (Exception e) {
			throw new TestException("Error creating test "+className, e);
		}
	}
	
	public void initialize(Agent a) throws TestException {
	}
	
	public void shutdown(Agent a) {
	}
	
	public void setArguments(Object[] args) {
		this.args = args;
	}
}
	
