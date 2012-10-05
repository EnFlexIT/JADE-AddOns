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
package jade.webservice.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;

public class ThreadSafeAuthenticator extends Authenticator {
	
	private static final String PROXY_KEY = "PROXY";
	
	private static ThreadSafeAuthenticator theInstance;
	private static Map<String, PasswordAuthentication> passwordAuthentications = new HashMap<String, PasswordAuthentication>();

	private ThreadSafeAuthenticator() {
	}
	
	public final static ThreadSafeAuthenticator getInstance() {
		if (theInstance == null) {
			theInstance = new ThreadSafeAuthenticator(); 
			
			Authenticator.setDefault(theInstance);			
		}
		return theInstance;
	}
	
	public void setProxyCredential(String username, String password) {
		setHttpCredential(PROXY_KEY, username, password);
	}
	
	public void setHttpCredential(String threadGroupName, String username, String password) {
		if (username != null) {
			PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username, password != null ? password.toCharArray() : null);
			passwordAuthentications.put(threadGroupName, passwordAuthentication);
		} else {
			reset(threadGroupName);
		}
	}

	public void reset(String threadGroupName) {
		passwordAuthentications.remove(threadGroupName);
	}
	
	@Override
    protected PasswordAuthentication getPasswordAuthentication() {
		String threadGroupName;
		if (getRequestorType().equals(Authenticator.RequestorType.PROXY)) {
			threadGroupName = PROXY_KEY;
		} else {
			threadGroupName = Thread.currentThread().getThreadGroup().getName(); 
		}
		
    	PasswordAuthentication pa = passwordAuthentications.get(threadGroupName);
    	return pa;
    }
}