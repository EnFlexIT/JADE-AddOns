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
   included in a <code>TestGroup</code> and executed by a 
   <code>TestGroupExecutor</code>
   @author Giovanni Caire - TILAB
 */
public abstract class Test {
  public static final int TEST_PASSED = 1;
  public static final int TEST_FAILED = 0;
  
  public static final int DEFAULT_PORT = 1099;

  private TestGroup myGroup;
  private TestDescriptor myDescriptor;
  
  public abstract Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException;
  public void clean(Agent a) {
  }
  
  protected Object getGroupArgument(String name) {
  	return myGroup.getArgument(name);
  }
  
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

