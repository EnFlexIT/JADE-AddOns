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

package test.common.remote;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.net.InetAddress;
import java.util.Hashtable;

import test.common.*;
import jade.util.leap.List;

/**
   @author Giovanni Caire - TILAB
 */
public class TSDaemon extends UnicastRemoteObject implements RemoteManager {

	public static final String DEFAULT_NAME = "TSDaemon";
	public static final int DEFAULT_PORT = 7777;
	
	private Hashtable controllers = new Hashtable();
	private int instanceCnt = 0;
	
	private String additionalArgs = null;
	
  public TSDaemon(String additionalArgs) throws RemoteException {
    super();
    this.additionalArgs = additionalArgs;
  }

	public static void main(String args[]) {
		StringBuffer sb = new StringBuffer();
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; ++i) {
				sb.append(args[i]);
				sb.append(" ");
			}
		}
		
    try {
    	// Create an RMI registry on the local host and DAEMON port
    	// (if one is already running just get it)
      String hostName = InetAddress.getLocalHost().getHostName();      
      Registry theRegistry = getRmiRegistry(hostName, DEFAULT_PORT);
      String registryID = new String("rmi://"+hostName+":"+DEFAULT_PORT);
      
      // Create a TSDaemon and bind it to the registry
      TSDaemon daemon = new TSDaemon(sb.toString());
      Naming.bind(new String(registryID+"//"+DEFAULT_NAME), daemon);
      System.out.println("Test Suite Daemon ready.");
    }
    catch(Exception e) {
    	System.out.println("ERROR starting Test Suite Daemon");
    	e.printStackTrace();
    	System.exit(1);
    }
  }
  
  /**
   * If an RMI registry is already active on this host
   * at the given portNumber, then that registry is returned, 
   * otherwise a new registry is created and returned.
   */
  private static Registry getRmiRegistry(String host, int portNumber) throws RemoteException {
		Registry rmiRegistry = null;
		// See if a registry already exists and
		// make sure we can really talk to it.
		try {
	    rmiRegistry = LocateRegistry.getRegistry(host, portNumber);
	    rmiRegistry.list();
		} 
		catch (Exception exc) {
	    rmiRegistry = null;
		}

		// If rmiRegistry is null, then we failed to find an already running
		// instance of the registry, so let's create one.
		if (rmiRegistry == null) {
			rmiRegistry = LocateRegistry.createRegistry(portNumber);
		}

		return rmiRegistry;
	}

	//////////////////////////////////////////
  // RemoteManager INTERFACE IMPLEMENTATION
  //////////////////////////////////////////
  public int launchJadeInstance(String instanceName, String classpath, String mainClass, String jadeArgs, String[] protoNames) throws TestException, RemoteException {
		instanceCnt++;
		jadeArgs = additionalArgs + jadeArgs;
		JadeController jc = null;
		if (mainClass.equals("jade.Boot")) {
			// Stand alone mode
	  	jc = TestUtility.launchJadeInstance(instanceName, classpath, jadeArgs, protoNames);
		}
		else {
			// Split mode
	  	jc = TestUtility.launchSplitJadeInstance(instanceName, classpath, jadeArgs);
		}
  	controllers.put(new Integer(instanceCnt), jc);
  	return instanceCnt;
	}
	
	public List getJadeInstanceAddresses(int id) throws TestException, RemoteException {
		JadeController jc = (JadeController) controllers.get(new Integer(id));
		if (jc != null) {
			return jc.getAddresses();
		}
		else {
			throw new TestException("No JADE instance corresponding to ID "+id);
		}
	}
	
	public String getJadeInstanceContainerName(int id) throws TestException, RemoteException {
		JadeController jc = (JadeController) controllers.get(new Integer(id));
		if (jc != null) {
			return jc.getContainerName();
		}
		else {
			throw new TestException("No JADE instance corresponding to ID "+id);
		}
	}
	
	public void killJadeInstance(int id) throws TestException, RemoteException {
		JadeController jc = (JadeController) controllers.remove(new Integer(id));
		if (jc != null) {
			jc.kill();
		}
		else {
			throw new TestException("No JADE instance corresponding to ID "+id);
		}
	}
}

