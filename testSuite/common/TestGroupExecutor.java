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
import test.common.xml.TestDescriptor;

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
	private static final String END_STATE = "End";
	private static final String PAUSE_STATE = "Pause";
	
	// Data store key for the result of a test
	private static final String TEST_RESULT_KEY = "_test-result_";
	
	// Exit values from the LOAD_TEST_STATE
	private static final int EXIT = 0;
	private static final int EXECUTE = 1;
	private static final int SKIP = 2;
	private static final int PAUSE = 3;
	private static final int ABORT = 4;
	
	private boolean aborted = false;
	
	// Counters for statistics
	protected int passedCnt = 0;
	protected int failedCnt = 0;
	protected int skippedCnt = 0;
	
	// The pool of tests to be executed 
	private TestGroup tests;
	
	// The test object that is currently in execution
	private Test currentTest;

	// Flag indicating whether debug (step-by-step) execution is selected
	private boolean debugMode = false;
	// Flag indicating whether the execution is currently paused
	private boolean inPause = false;
	
	// The Logger used to handle outputs
	private Logger l;

	public TestGroupExecutor(Agent a, TestGroup tg) {
		super(a);
		l = Logger.getLogger();
		
		if (tg == null) {
			throw new IllegalArgumentException("Null test group");
		}
		tests = tg;
		
		// Transition table
		registerDefaultTransition(INIT_TEST_GROUP_STATE, LOAD_TEST_STATE);
		registerTransition(INIT_TEST_GROUP_STATE, END_STATE, ABORT);
		registerTransition(LOAD_TEST_STATE, EXECUTE_TEST_STATE, EXECUTE);
		registerTransition(LOAD_TEST_STATE, LOAD_TEST_STATE, SKIP);
		registerTransition(LOAD_TEST_STATE, END_STATE, EXIT);
		registerTransition(LOAD_TEST_STATE, PAUSE_STATE, PAUSE);
		registerDefaultTransition(PAUSE_STATE, EXECUTE_TEST_STATE);
		registerDefaultTransition(EXECUTE_TEST_STATE, HANDLE_RESULT_STATE);
		registerDefaultTransition(HANDLE_RESULT_STATE, LOAD_TEST_STATE);
		
		// INIT_TEST_GROUP_STATE
		Behaviour b = new OneShotBehaviour() {
			public void action() {
				try {
					tests.initialize(myAgent);
				}
				catch (TestException te) {
					l.log("Error in TestGroup initialization. Abort");
					te.printStackTrace();
					aborted = true;
				}
			}
			
			public int onEnd() {
				return (aborted ? ABORT : -1);
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
						StringBuffer sb = new StringBuffer("\n--------------------------------------------\n");
						TestDescriptor td = currentTest.getDescriptor();
						sb.append("Executing test: "+td.getName()+"\n");
						sb.append("WHAT: "+td.getWhat()+"\n");
						sb.append("HOW:  "+td.getHow()+"\n");
						sb.append("PASSED WHEN: "+td.getPassedWhen()+"\n\n");
						l.log(sb.toString());
						Behaviour b2 = currentTest.load(myAgent, getDataStore(), TEST_RESULT_KEY);
						registerState(b2, EXECUTE_TEST_STATE);
						
						if (debugMode) {
							ret = PAUSE;
							inPause = true;
						}
					}
					else {
						// When next() returns null there are no more tests to execute
						ret = EXIT;
					}
				}
				catch (TestException te) {
					// Some problems occured initializing this test. Skip it
					l.log("Problems in test initialization ["+te.getMessage()+"]");
					l.log("Skip this test.");
					skippedCnt++;
					ret = SKIP;
				}
			}
			
			public int onEnd() {
				if (ret == EXECUTE) {
					// Before entering the execution state flush messages that
					// may have been left into the queue and that may confuse 
					// the test execution
					flushMessageQueue();
				}
				return ret;
			}
		};
		b.setDataStore(getDataStore());
		registerState(b, LOAD_TEST_STATE);

		// HANDLE_RESULT_STATE
		b = new OneShotBehaviour() {
			public void action() {
				int result = Test.NOT_AVAILABLE;
				try {
					Integer i = (Integer) getDataStore().get(TEST_RESULT_KEY);
	  			result = i.intValue();
				}
				catch (Exception e) {
				}
  			
  			try {
		  		currentTest.clean(myAgent);
  			}
  			catch (Exception e) {
  				// Just print a warning
  				l.log("Warning: Exception in test cleaning ["+e.getMessage()+"]");
  			}
  			finally {
  				try {
  					Thread.sleep(1000);
  				}
  				catch (Exception e ) {}
  			}
  			
				if (result == Test.TEST_PASSED) {
  				l.log("Test PASSED");
  				passedCnt++;
  			}
  			else if (result == Test.TEST_FAILED) {
  				l.log("Test FAILED");
  				failedCnt++;
  			}
  			else {
  				l.log("WARNING: Test result not available!!!");
  				skippedCnt++;
  			}
			}			
		};
		b.setDataStore(getDataStore());
		registerState(b, HANDLE_RESULT_STATE);

		// END_STATE
		b = new OneShotBehaviour() {
			public void action() {
				if (!aborted) {
					StringBuffer sb = new StringBuffer("\n--------------------------------------------\n");
					sb.append("--------------------------------------------\n");
    			sb.append("Test summary:\n");
    			sb.append(passedCnt+" tests PASSED\n");
    			sb.append(failedCnt+" tests FAILED\n");
	    		if (skippedCnt > 0) {
  	  			sb.append(skippedCnt+" tests SKIPPED due to initailization/termination problems\n");
    			}	
    			l.log(sb.toString());
    		
					tests.shutdown(myAgent);
				}
			}			
		};
		b.setDataStore(getDataStore());
		registerLastState(b, END_STATE);
	
		// PAUSE_STATE
		b = new SimpleBehaviour() {
			private boolean finished;
			
			public void action() {
				if (inPause) {
					block();
					finished = false;
				}
				else {
					finished = true;
				}
			}	
			
			public boolean done() {
				return finished;
			}
			
			public int onEnd() {
				// Before entering the execution state flush messages that
				// may have been left into the queue and that may confuse 
				// the test execution
				flushMessageQueue();
				return 0;
			}
		};
		b.setDataStore(getDataStore());
		registerState(b, PAUSE_STATE);
	}
	
	void resume() {
		inPause = false;
		super.restart();
	}
	
	void setDebugMode(boolean b) {
		debugMode = b;
	}
	
	private void flushMessageQueue() {
		while (myAgent.receive() != null) {
			;
		}
	}
}

