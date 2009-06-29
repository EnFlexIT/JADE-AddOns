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
package jade.webservice.dynamicClient;

import jade.webservice.dynamicClient.DynamicClient.State;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton and thread-safe cache manager of DynamicClients.<br>
 * Allows the use and reuse of the DynamicClient without 
 * worrying their initialization.
 * <p>
 * A typical example of use is:
 * <p>
 * <code>
 * DynamicClientCache dcc = DynamicClientCache.getInstance();<br>
 * DynamicClient dc = dcc.get(new URI("http://myWSDL"));<br>
 * ��<br>
 * WSData wsOutputData = dc.invoke(�sum�, wsInputData);<br>
 * </code>
 * 
 * @see jade.webservice.dynamicClient.DynamicClient
 */
public class DynamicClientCache {

	private static Map<URI, DynamicClient> cachedDynamicClients = new HashMap<URI, DynamicClient>();
	
	private final static DynamicClientCache theInstance = new DynamicClientCache();
	
	/**
	 * Get a instance of DynamicClientCache
	 * (Singleton thread-safe)
	 * 
	 * @return a instance of DynamicClientCache
	 */
    public final static DynamicClientCache getInstance() {
        return theInstance;
    }

    private DynamicClientCache() {
    }

    /**
     * Get a initialized DynamicClient for specified wsdl uri.<br>
     * If the wsdl is alredy present in cache reuse it, 
     * otherwise initialize and cache it.<br>
     * This operation is thread-safe.

     * @param wsdl webservice wsdl uri
     * @return initialized DynamicClient
     * @throws DynamicClientException
     */
    public DynamicClient get(URI wsdl) throws DynamicClientException {
    	return get(wsdl, null);
    }

    /**
     * Get an initialized DynamicClient for specified wsdl uri.<br>
     * If the wsdl is alredy present in cache reuse it, 
     * otherwise initialize and cache it.<br>
     * This operation is thread-safe.     
     * 
     * @param wsdl webservice wsdl uri
     * @param properties configuration properties
     * @return initialized DynamicClient
     * @throws DynamicClientException 
     * 
     *  @see jade.webservice.dynamicClient.DynamicClientProperties
     */
    public DynamicClient get(URI wsdl, DynamicClientProperties properties) throws DynamicClientException {
    	
    	DynamicClient dc;
    	Thread owner = null;
    	
		// Check if dynamic client already present in cached map
		synchronized (cachedDynamicClients) {
			dc = cachedDynamicClients.get(wsdl);

			if (dc == null) {
				// Create a new dynamic client
				dc = new DynamicClient();
				if (properties != null) {
					dc.setProperties(properties);
				}
				
				// Add new dynamic client to cache
				cachedDynamicClients.put(wsdl, dc);
				
				// Set the owner thread
				owner = Thread.currentThread();
			}
		}
		
		// Check if the current thread is the owner of dynamic client -> init it
		if (Thread.currentThread() == owner) {
			// Init client
			// This initialization is out of synchronization block because is an operation very long
			// Only the threads that request this wsdl wait, other get directly the dynamic client
			try {
				dc.initClient(wsdl);
			} catch(DynamicClientException dce) {
				// If init failed remove from map
				remove(wsdl);
				throw dce;
			}
			
		} else {
			// All other that requiring dynamic client -> wait until inited
			while(dc.getState() != State.INITIALIZED) {
				
				// If failed in initClient() -> 
				if (dc.getState() == State.INIT_FAILED) {
					throw dc.getInitializationExceptionException();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    	
    	return dc;
    }
    
    /**
     * Remove all cached wsdls
     */
	public void clear() {
		synchronized (cachedDynamicClients) {
			cachedDynamicClients.clear();
		}
	}
	
	/**
	 * Remove a cached wsdl
	 *   
	 * @param wsdl wsdl uri to remove
	 */
	public void remove(URI wsdl) {
		synchronized (cachedDynamicClients) {
			cachedDynamicClients.remove(wsdl);
		}
	}
}