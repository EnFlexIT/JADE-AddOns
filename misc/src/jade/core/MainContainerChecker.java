/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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
package jade.core;

import jade.core.ProfileImpl;
import jade.util.leap.Properties;

public class MainContainerChecker {

	/**
	 * Check the presence of Main-Container
	 * @param props Main-container properties
	 * @return true if main-container is active
	 */
	public static boolean check(Properties props) {
		IMTPManager imtpManager = null;
		try {
			// Create profile
			props.setProperty(Profile.MAIN, "false");
			ProfileImpl profile = new ProfileImpl(props);
			
			// Create and initialize the IMTPManager
			imtpManager = profile.getIMTPManager();
			imtpManager.initialize(profile);
			
			// Try to create a proxy to the PlatformManager
			// This method tries to execute the GET_PLATFORM_NAME command that fails if 
			// a Main Container with a valid skeleton object does not exist 
			imtpManager.getPlatformManagerProxy();
		} catch (Exception e) {
			return false;
		} finally {
			if (imtpManager != null) {
				imtpManager.shutDown();
			}
		}

		return true;
	}
	
	/**
	 * Get the platform name
	 * @param props Main-container properties
	 * @return the name of platform, null if main-container is active
	 */
	public static String getPlatformName(Properties props) {
		IMTPManager imtpManager = null;
		try {
			// Create profile
			props.setProperty(Profile.MAIN, "false");
			ProfileImpl profile = new ProfileImpl(props);
			
			// Create and initialize the IMTPManager
			imtpManager = profile.getIMTPManager();
			imtpManager.initialize(profile);
			
			// Try to create a proxy to the PlatformManager
			// This method try to execute the GET_PLATFORM_NAME command that fail if 
			// the skeleton object not exist
			PlatformManager pm = imtpManager.getPlatformManagerProxy();
			return pm.getPlatformName();
		} catch (Exception e) {
			return null;
		} finally {
			if (imtpManager != null) {
				imtpManager.shutDown();
			}
		}
	}
}
