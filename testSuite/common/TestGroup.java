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
import jade.util.leap.*;
import java.util.Hashtable;

/**
   Class representing a group of tests (often related to a given 
   single functionality) that have to be executed in sequence and 
   require similar configurations
   @author Giovanni Caire - TILAB
 */
public class TestGroup {
	private String[] testClassNames;
	private int cnt;
	private List argumentSpecs = new ArrayList();
  private Hashtable args = new Hashtable();
	
	public TestGroup(String[] tests) {
		testClassNames = (tests != null ? tests : new String[0]);
		cnt = 0;
	}
	
	/**
	   Specify an optional argument for this TestGroup. If the user
	   does not provide a value for this argument, the default value
	   will be used.
	 */
	public void specifyArgument(String name, String label, String defaultVal) {
		ArgumentSpec a = new ArgumentSpec(name, label, defaultVal);
		a.setValue(defaultVal);
		argumentSpecs.add(a);
	}
	
	/**
	   Specify mandatory argument for this TestGroup. The user will 
	   always have to provide a value for this argument.
	 */
	public void specifyArgument(String name, String label) {
		ArgumentSpec a = new ArgumentSpec(name, label);
		argumentSpecs.add(a);
	}
	
	/**
	   Used by the tests in the group to get arguments specified at 
	   group level. These include arguments inserted by the user through
	   the InsertArgumentsDlg and arguments explicitly set (generally in
	   the initialize() method) using the setArgument() method.
	 */
	protected Object getArgument(String name) {
		return args.get(name);
	}
	
	/**
	   @see getArgument()
	 */
	protected Object setArgument(String name, Object val) {
		return args.put(name, val);
	}
	
	/**
	   The developer should override this method to perform initializations
	   common to all tests in the group
	 */
	protected void initialize(Agent a) throws TestException {
	}
	
	/**
	   The developer should override this method to perform cleanings
	   common to all tests in the group
	 */
	protected void shutdown(Agent a) {
	}
	
	/**
	   Only called by the TestGroupExecutor to get the next test to execute
	 */
	Test next() throws TestException {		
		String className = null;
		try {
			className = testClassNames[cnt++];
			Test t = (Test) Class.forName(className).newInstance();
			t.setGroup(this);
			return t;
		}
		catch (IndexOutOfBoundsException ioobe) {
			return null;
		}
		catch (Exception e) {
			throw new TestException("Error creating test "+className, e);
		}
	}
	
	/**
	   Only called by the TesterAgent to know what arguments the user 
	   must/can input
	 */
	List getArgumentsSpecification() {
		return argumentSpecs; 
	}
	
	/**
	   Only called by the TesterAgent to set the arguments inputed by 
	   the user
	 */
	void setArguments(List aa) {
		if (aa != null) {
			Iterator it = aa.iterator();
			while (it.hasNext()) {
				ArgumentSpec a = (ArgumentSpec) it.next();
				args.put(a.getName(), a.getValue());
			}
		}
	}
	
	/** 
	   Bring the test group back to the beginning. Only called by
	   the TesterAgent.
	 */
	void reset() {
		cnt = 0;
	}
}
	
