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
   @author Givanni Caire - TILAB
   @author Elisabetta Cortese - TILAB
 */
public class InterPlatformCommunicationTesterAgent extends TesterAgent {

	// Names and default values for group arguments
	public static final String REMOTE_AMS_KEY = "remote-ams";
	
	public static final String REMOTE_PLATFORM_NAME = "Remote-platform";
	public static final String REMOTE_PLATFORM_PORT = "9003";
	
	public static final String ADDITIONAL_CLASSPATH_KEY = "classpath";
	public static final String ADDITIONAL_CLASSPATH_DEFAULT = "c:/jade/add-ons/http/classes";
	
	protected TestGroup getTestGroup() {
		TestGroup tg = new TestGroup("test/interPlatform/interPlatformTestsList.xml"){		
			
			private JadeController jc;
			
			public void initialize(Agent a) throws TestException {
				try {
					// Start the remote platform with a SUNOrb-based IIOP MTP
					jc = TestUtility.launchJadeInstance(REMOTE_PLATFORM_NAME, null, new String("-name "+REMOTE_PLATFORM_NAME+" -port "+REMOTE_PLATFORM_PORT), new String[]{"IOR"}); 
		
					// Construct the AID of the AMS of the remote platform and make it
					// accessible to the tests as a group argument
					AID remoteAMS = new AID("ams@"+REMOTE_PLATFORM_NAME, AID.ISGUID);
					Iterator it = jc.getAddresses().iterator();
					while (it.hasNext()) {
						remoteAMS.addAddresses((String) it.next());
					}
					setArgument(REMOTE_AMS_KEY, remoteAMS);
				}
				catch (TestException te) {
					throw te;
				}
				catch (Exception e) {
					throw new TestException("Error initializing TestGroup", e);
				}
			}
			
			public void shutdown(Agent a) {
  			try {
  				// Kill the remote platform
  				Thread.sleep(1000);
  				jc.kill();
  			}
  			catch (Exception e) {
  				e.printStackTrace();
  			}
			}
		};
		
		tg.specifyArgument(ADDITIONAL_CLASSPATH_KEY, "Additional classpath", ADDITIONAL_CLASSPATH_DEFAULT);
		return tg;
	}
		
	// Main method that allows launching this test as a stand-alone program	
	public static void main(String[] args) {
		try {
			// Get a hold on JADE runtime
      Runtime rt = Runtime.instance();

      // Exit the JVM when there are no more containers around
      rt.setCloseVM(true);
      
      Profile pMain = new ProfileImpl(null, Test.DEFAULT_PORT, null);

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
