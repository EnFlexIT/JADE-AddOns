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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import test.common.xml.TestDescriptor;

import java.util.Hashtable;

/**
   Common base class for all test classes designed to be 
   executed within the JADE test suite
   @author Giovanni Caire - TILAB
 */
public abstract class Test {
	/** Constant value indicating that a test has been completed successfully */
	public static final int TEST_PASSED = 1;
	/** Constant value indicating that a test has NOT been completed successfully */
  public static final int TEST_FAILED = 0;
	/** Constant value indicating that the test result is not available */
  public static final int NOT_AVAILABLE = -1;
  
  public static final int DEFAULT_PORT = 1099;

  private TestGroup myGroup;
  private TestDescriptor myDescriptor;
  
  /**
     Specific tests must re-define this method to perform test specific 
     initializations and to create the <code>Behaviour</code> that will
     actually perform the test.
     @param a The agent that executes the test
     @param ds The <code>DataStore</code> where to put the test result
     @param resultKey The <code>DataStore</code> key for the test result
     @return the <code>Behaviour</code> that will actually perform the test
   */
  public abstract Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException;
  
  /**
     Specific tests can re-define this method to perform test specific 
     clean-up operations.
     @param a The agent that executes the test
   */
  public void clean(Agent a) {
  }
  
  /**
     Retrieve an argument of the <code>TestGroup</code> this 
     <code>Test</code> belongs to.
     Group arguments may be set at run-time by the 
     user through the "Set arguments" button of the TestSuite Toolbar.
     When needed a <code>TestGroup</code> can also directly set
     group arguments from within its <code>initialize()</code> method.     
     Group arguments are available to all tests in the same group.
     @param key The key identifying the group argument to be retrieved
     @return The value of the group argument identified by <code>key</code>
   */
  protected Object getGroupArgument(String key) {
  	return myGroup.getArgument(key);
  }

  /**
     Retrieve an argument of this <code>Test</code>.
     Test arguments can be set within the xml configuration file
     describing this test by adding elements of type <argument>.
     Unlike group arguments, test arguments are only available to 
     the test they refer to.
     @param key The key identifying the test argument to be retrieved
     @return The value of the test argument identified by <code>key</code>
   */
  protected String getTestArgument(String key) {
  	return myDescriptor.getArg(key);
  }
  
  //////////////////////////////////////
  // Private and package-scoped methods
  //////////////////////////////////////
  
  /** 
     Only called by the TestGroupExecutor
   */
  TestDescriptor getDescriptor() {
  	return myDescriptor;
  }
  
  /** 
     Only called by the TestGroup
   */
  void setDescriptor(TestDescriptor td) {
  	myDescriptor = td;
  }
  
  void setGroup(TestGroup tg) {
  	myGroup = tg;
  }
}

