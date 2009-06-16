package jade.osgi.service.runtime.internal;

import jade.osgi.AgentManager;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

public class OsgiEventHandlerFactory {
	private static Map<String,OsgiEventHandler> handlers = new HashMap<String, OsgiEventHandler>();
	
	public static OsgiEventHandler getOsgiEventHandler(String bundleSymbolicName, AgentManager am){
		OsgiEventHandler handler = null;
		if(!handlers.containsKey(bundleSymbolicName)) {
			handler = new OsgiEventHandler(am);
			handlers.put(bundleSymbolicName, handler);
		}
			else {
				handler = handlers.get(bundleSymbolicName);
		}
		return handler;
	}
}
