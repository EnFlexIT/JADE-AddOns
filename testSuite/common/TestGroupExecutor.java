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
import jade.core.behaviours.*;
import jade.lang.acl.MessageTemplate;

/**
   Generic behaviour that executes the tests included in a 
   <code>TestGroup</code> object
   @author Giovanni Caire - TILAB
 */
public class TestGroupExecutor extends FSMBehaviour {
	
	// State names 
	private static final String INIT_TEST_GROUP_STATE = "Init-test-group";
	private static final String LOAD_TEST_STATE = "Load-test";
	private static final String EXECUTE_TEST_STATE = "Execute-test";
	private static final String HANDLE_RESULT_STATE = "Handle-result";
	private static final String END_STATE = "Dummy-final";
	
	// Data store key for the result of a test
	private static final String TEST_RESULT_KEY = "_test-result_";
	
	// Exit values from the LOAD_TEST_STATE
	private static final int EXIT = 0;
	private static final int EXECUTE = 1;
	private static final int SKIP = 2;
	
	// Counters for statistics
	private int passedCnt = 0;
	private int failedCnt = 0;
	private int skippedCnt = 0;
	
	// The pool of tests to be executed 
	private TestGroup tests;
	
	// The test object that is currently in execution
	private Test currentTest;
	
	// The MessageTemplate used to distingush messages sent to procede 
	// in step-by-step mode.
	private static final String STEP_KEYWORD = "STEP";
	private MessageTemplate stepTemplate = MessageTemplate.MatchContent(STEP_KEYWORD);
	private boolean stepByStepMode = false;

	public TestGroupExecutor(Agent a, TestGroup tg) {
		super(a);
		
		if (tg == null) {
			throw new IllegalArgumentException("Null test group");
		}
		tests = tg;
		
		// Transition table
		registerDefaultTransition(INIT_TEST_GROUP_STATE, LOAD_TEST_STATE);
		registerTransition(LOAD_TEST_STATE, EXECUTE_TEST_STATE, EXECUTE);
		registerTransition(LOAD_TEST_STATE, LOAD_TEST_STATE, SKIP);
		registerTransition(LOAD_TEST_STATE, END_STATE, EXIT);
		registerDefaultTransition(EXECUTE_TEST_STATE, HANDLE_RESULT_STATE);
		registerDefaultTransition(HANDLE_RESULT_STATE, LOAD_TEST_STATE);
		
		// INIT_TEST_GROUP_STATE
		Behaviour b = new OneShotBehaviour() {
			public void action() {
				try {
					tests.initialize(myAgent);
				}
				catch (TestException tie) {
					System.out.println("Error in TestGroup initialization");
					tie.printStackTrace();
					myAgent.doDelete();
				}
			}
		};
		registerFirstState(b, INIT_TEST_GROUP_STATE);

		// LOAD_TEST_STATE
		b = new OneShotBehaviour() {
			private int ret;
			
			public void action() {
				try {
					currentTest = tests.next();
					if (currentTest != null) {
						ret = EXECUTE;
						System.out.println("\n--------------------------------------------");
						System.out.println("Executing test: "+currentTest.getName());
						System.out.println("Description: "+currentTest.getDescription());
						Behaviour b = currentTest.load(myAgent, getDataStore(), TEST_RESULT_KEY);
						registerState(b, EXECUTE_TEST_STATE);
						
						pause();
					}
					else {
						// When next() returns null there are no more tests to execute
						ret = EXIT;
					}
				}
				catch (TestException tie) {
					// Some problems occured initializing this test. Skip it
					System.out.println("Problems in test initialization ["+tie.getMessage()+"]");
					System.out.println("Skip this test.");
					skippedCnt++;
					ret = SKIP;
				}
			}
			
			public int onEnd() {
				return ret;
			}
		};
		b.setDataStore(getDataStore());
		registerState(b, LOAD_TEST_STATE);

		// HANDLE_RESULT_STATE
		b = new OneShotBehaviour() {
			public void action() {
				
				Integer i = (Integer) getDataStore().get(TEST_RESULT_KEY);
  			int result = i.intValue();
				if (result == Test.TEST_PASSED) {
  				System.out.println("Test PASSED");
  				passedCnt++;
  			}
  			else {
  				System.out.println("Test FAILED");
  				failedCnt++;
  			}
  			
  			try {
		  		currentTest.clean(myAgent);
  			}
  			catch (Exception e) {
  				// Just print a warning
  				System.out.println("Warning: Exception in test cleaning ["+e.getMessage()+"]");
  			}
			}			
		};
		b.setDataStore(getDataStore());
		registerState(b, HANDLE_RESULT_STATE);

		// END_STATE
		b = new OneShotBehaviour() {
			public void action() {
				System.out.println("\n--------------------------------------------");
				System.out.println("--------------------------------------------");
    		System.out.println("Test summary:");
    		System.out.println(passedCnt+" tests PASSED");
    		System.out.println(failedCnt+" tests FAILED");
    		if (skippedCnt > 0) {
    			System.out.println(skippedCnt+" tests SKIPPED due to initailization problems");
    		}	
    		
				tests.shutdown(myAgent);
			}			
		};
		b.setDataStore(getDataStore());
		registerLastState(b, END_STATE);
	}
	
	private void pause() {
		if (stepByStepMode) {
			System.out.println("Send me a message with \""+STEP_KEYWORD+"\" as content to proceed...");
			myAgent.blockingReceive(stepTemplate);
			System.out.println("Go");
		}
	}
}

