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

package test.interPlatform;

import jade.core.Agent;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.AID;
import jade.wrapper.*;
import jade.util.leap.*;
import test.common.*;
import java.io.*;
import java.net.InetAddress;

/**
 */
public class InterPlatformCommunicationTesterAgent extends Agent {
	private static final String DEFAULT_CLASSPATH = "c:\\jade\\classes";
	
	protected void setup() {
		TestGroup tg = new TestGroup(new String[] {
			"test.interPlatform.tests.TestRemotePing"
		}) {
			
			private RemoteController rc;
			
			public void initialize(Agent a) throws TestException {
				try {
    			String localHost = InetAddress.getLocalHost().getHostAddress();
					rc = TestUtility.launchJadeInstance("Remote platform", DEFAULT_CLASSPATH, new String("-host "+localHost+" -port 9002"), new String[]{"IOR"}); 
		
					AID remoteAMS = new AID("ams@"+localHost+":9002/JADE", AID.ISGUID);
					Iterator it = rc.getAddresses().iterator();
					while (it.hasNext()) {
						remoteAMS.addAddresses((String) it.next());
					}
					
					setArguments(new Object[] {remoteAMS});
				}
				catch (TestException te) {
					throw te;
				}
				catch (Exception e) {
					throw new TestException("Error initializing TestGroup", e);
				}
			}
			
			public void shutdown(Agent a) {
				rc.kill();
			}
		};
		
		addBehaviour(new TestGroupExecutor(this, tg) {
			public int onEnd() {
				myAgent.doDelete();
				return 0;
			}
		} );
		
	}	
		
	protected void takeDown() {
		System.out.println("Exit...");
	}
	
	
	// Main method that allows launching this test as a stand-alone program	
	public static void main(String[] args) {
		try {
			// Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);
      
      Profile pMain = new ProfileImpl(null, 8888, null);

      MainContainer mc = rt.createMainContainer(pMain);

      AgentController rma = mc.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
      rma.start();

      AgentController tester = mc.createNewAgent("tester", "test.interPlatform.InterPlatformCommunicationTesterAgent", args);
      tester.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
