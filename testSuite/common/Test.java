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

/**
   Common base class for all test classes designed to be 
   included in a <code>TestGroup</code> and executed by a 
   <code>TestGroupExecutor</code>
   @author Giovanni Caire - TILAB
 */
public abstract class Test {
  public static final int TEST_PASSED = 1;
  public static final int TEST_FAILED = 0;

  private Object[] groupArgs;
  
  public abstract String getName();
  public abstract String getDescription();
  public abstract Behaviour load(Agent a, DataStore ds, String resultKey) throws TestException;
  public void clean(Agent a) {
  }
  
  void setGroupArguments(Object[] args) {
  	groupArgs = args;
  }
  protected Object[] getGroupArguments() {
  	return groupArgs;
  }
}
