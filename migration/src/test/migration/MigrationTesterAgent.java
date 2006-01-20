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

// Created on 10-jun-2004
// Authors Joan Ametller, Jordi Cucurull
package test.migration;

import test.common.TestGroup;
import test.common.TestUtility;
import test.common.TesterAgent;
import jade.core.*;
import test.common.*;

import java.io.File;
import java.net.InetAddress;
import jade.util.leap.Iterator;
/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * 
 */
public class MigrationTesterAgent extends TesterAgent {
	//Names and default values for group arguments
	public static final String REMOTE_AMS_KEY = "remote-ams";
	public static final String REMOTE_AMS_KEY_2 = "remote-ams-2";
	
	public static final String REMOTE_PLATFORM_NAME = "Remote-platform";
	public static final String REMOTE_PLATFORM_NAME_2 = "Remote-platform-2";
	public static final String REMOTE_PLATFORM_PORT_KEY_2 = "remote-platform-port-2";
	public static final String REMOTE_PLATFORM_PORT_KEY = "remote-platform-port";
	public static final String REMOTE_PLATFORM_PORT = "9003";
	public static final String REMOTE_PLATFORM_PORT_2 = "9004";
	
	public static final String MTP_KEY = "mtp";
	public static final String MTP_DEFAULT = "jade.mtp.http.MessageTransportProtocol";
	public static final String PROTO_KEY = "proto";
	public static final String PROTO_DEFAULT = "http";
	public static final String MTP_URL_KEY = "url";
	public static final String MTP_URL_DEFAULT = "";
	public static final String ADDITIONAL_CLASSPATH_KEY = "classpath";
	public static final String ADDITIONAL_CLASSPATH_DEFAULT = "c:/jade/add-ons/http/classes";
	public static final String MOBILITY_SERVICE = "jade.core.mobility.AgentMobilityService";
	public static final String MIGRATION_SERVICE = "jade.core.migration.InterPlatformMobilityService";
	public static final String CURRENT_DIR = System.getProperty("user.dir");
	public static final String AGENTS_PATH_KEY = "-jade_AgentManagement_agentspath";
	public static final String AGENTS_PATH = "migration" + File.separator + "path1";
	//public static final String AGENTS_PATH = CURRENT_DIR + File.separator + "src" + File.separator + "test" + File.separator + "migration" + File.separator + "path1";
	// "migration" + java.io.File.separator + "path1";
	public static final String AGENTS_PATH_2 = "migration" + File.separator + "path2";
	//public static final String AGENTS_PATH_2 = CURRENT_DIR + File.separator + "src" + File.separator + "test" + File.separator + "migration" + File.separator + "path2";
	
	protected TestGroup getTestGroup() {
		
		TestGroup tg = new TestGroup("test/migration/migrationTestsList.xml") {
			private JadeController jc1, jc2, cmtp;
			
			public void initialize(Agent a) throws TestException {
				try {
					String mtp = (String) getArgument(MTP_KEY);
					String proto = (String) getArgument(PROTO_KEY);
					String additionalArguments = ("http".equalsIgnoreCase(proto) ? TestUtility.HTTP_MTP_ARG : "");
					String additionalClasspath = "+" + ((String) getArgument(ADDITIONAL_CLASSPATH_KEY));
					
					setArgument(REMOTE_PLATFORM_PORT_KEY, REMOTE_PLATFORM_PORT);
					setArgument(REMOTE_PLATFORM_PORT_KEY_2, REMOTE_PLATFORM_PORT_2);

					// Start a container with an MTP to communicate with the remote platforms
					cmtp = TestUtility.launchJadeInstance("Container-mtp", additionalClasspath, new String("-container -host "+TestUtility.getLocalHostName()+" -port "+String.valueOf(Test.DEFAULT_PORT)+" -mtp jade.mtp.http.MessageTransportProtocol(http://"+InetAddress.getLocalHost().getHostName()+":8888/acc) "+additionalArguments), new String[]{proto});
					
					// Start the remote platform with the specified MTP
					jc1 = TestUtility.launchJadeInstance(REMOTE_PLATFORM_NAME, additionalClasspath,
							new String(
									"-name "
									+ REMOTE_PLATFORM_NAME
                  + " "
									+ additionalArguments
									+ " -port "
									+ REMOTE_PLATFORM_PORT
									+ " " + AGENTS_PATH_KEY + " "
									+ AGENTS_PATH
									+ " -jade_core_migration_InterPlatformMigrationService_verbosity 100 "
									+ " -services "
									+ MIGRATION_SERVICE + ";" + MOBILITY_SERVICE),
									new String[]{proto});
					
					// Construct the AID of the AMS of the remote platform and make it accessible to the tests as a group argument
					AID remoteAMS = new AID("ams@" + REMOTE_PLATFORM_NAME, AID.ISGUID);
					Iterator it = jc1.getAddresses().iterator();
					while (it.hasNext()) {
						remoteAMS.addAddresses((String) it.next());
					}
					//remoteAMS.addAddresses("http://"+InetAddress.getLocalHost().getHostName()+":7778/acc");
					setArgument(REMOTE_AMS_KEY, remoteAMS);
					
					jc2 = TestUtility.launchJadeInstance(REMOTE_PLATFORM_NAME_2, additionalClasspath,
							new String(
									"-name " 
									+ REMOTE_PLATFORM_NAME_2
									+ " "
									+ additionalArguments
									+ " -port "
									+ REMOTE_PLATFORM_PORT_2
									+ " -mtp "
									+ "jade.mtp.http.MessageTransportProtocol(http://"+InetAddress.getLocalHost().getHostName()+":7779/acc)"
									+ " " + AGENTS_PATH_KEY + " "
									+ AGENTS_PATH_2
									+ " -services "
									+ MIGRATION_SERVICE + ";" + MOBILITY_SERVICE),
									new String[]{proto});
					
					// Construct the AID of the AMS of the remote platform and make it accessible to the tests as a group argument
					AID remoteAMS2 = new AID("ams@" + REMOTE_PLATFORM_NAME_2, AID.ISGUID);
					Iterator it2 = jc2.getAddresses().iterator();
					while (it2.hasNext()) {
						remoteAMS2.addAddresses((String) it2.next());
					}
					//remoteAMS2.addAddresses("http://"+InetAddress.getLocalHost().getHostName()+":7779/acc");
					setArgument(REMOTE_AMS_KEY_2, remoteAMS2);
					
				} catch (TestException te) {
					throw te;
				} catch (Exception e) {
					throw new TestException("Error initializing TestGroup", e);
				}
			}
			
			public void shutdown(Agent a) {
				// Kill the remote platform and the mtp container
				try {jc1.kill();} catch(Exception e) {}
				try {jc2.kill();} catch(Exception e) {}
				try {cmtp.kill();} catch(Exception e) {}
			}
		};
		
		tg.specifyArgument(MTP_KEY, "MTP", MTP_DEFAULT);
		tg.specifyArgument(PROTO_KEY, "Protocol", PROTO_DEFAULT);
		tg.specifyArgument(MTP_URL_KEY, "Local MTP URL", MTP_URL_DEFAULT);
		tg.specifyArgument(ADDITIONAL_CLASSPATH_KEY, "Additional classpath", ADDITIONAL_CLASSPATH_DEFAULT);
		
		return tg;
	}
}
