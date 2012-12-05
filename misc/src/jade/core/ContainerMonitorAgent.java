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

//#J2ME_EXCLUDE_FILE
//#APIDOC_EXCLUDE_FILE

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;

import jade.core.behaviours.*;
import jade.core.messaging.MessagingService;
import jade.domain.introspection.IntrospectionServer;
import jade.lang.acl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import jade.util.leap.Iterator;
import jade.util.leap.Map;

public class ContainerMonitorAgent extends Agent {
	// ContainerMonitorAgent specific startup configuration options 
	public static final String MANAGE_OUT_OF_MEMORY = "manage-oom";
	public static final String MANAGE_OUT_OF_MEMORY_PERIOD = "manage-oom-period";
	public static final String MANAGE_OUT_OF_MEMORY_BLOCKSIZE = "manage-oom-blocksize";
	public static final String MANAGE_OUT_OF_MEMORY_ACTION = "manage-oom-action";
	
	// Out of memory management actions
	public static final String LOG = "LOG";
	public static final String KILL = "KILL";
	
	public static final String CONTAINER_MONITOR_ONTOLOGY = "container-monitor";
	
	public static final String HELP_ACTION = "HELP";
	public static final String DUMP_AGENTS_ACTION = "DUMP-AGENTS";
	public static final String DUMP_AGENT_ACTION = "DUMP-AGENT";
	public static final String DUMP_MESSAGEQUEUE_ACTION = "DUMP-MESSAGEQUEUE";
	public static final String DUMP_MESSAGEMANAGER_ACTION = "DUMP-MESSAGEMANAGER";
	public static final String DUMP_LADT_ACTION = "DUMP-LADT";
	public static final String DUMP_SERVICES_MAP_ACTION = "DUMP-SERVICES-MAP";
	public static final String DUMP_PLATFORM_MANAGER_ACTION = "DUMP-PLATFORM-MANAGER";
	public static final String DUMP_SERVICE_ACTION = "DUMP-SERVICE";
	public static final String DUMP_THREADS_ACTION = "DUMP-THREADS";
	public static final String DUMP_SERVICES_ACTION = "DUMP-SERVICES";
	
	private AgentContainerImpl myContainer;
	private LADT myLADT;
	private String oomAction = LOG;
	
	private MessageTemplate template = MessageTemplate.and(
			MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
			MessageTemplate.MatchOntology(CONTAINER_MONITOR_ONTOLOGY) );
	
	private MessageTemplate helpTemplate = MessageTemplate.not(template);
	
	protected void setup() {
		Object[] args = getArguments();
		myContainer = (AgentContainerImpl) args[0];
		myLADT = (LADT) args[1];
		
		addBehaviour(new IntrospectionServer(this));
		
		if ("true".equals(getProperty(MANAGE_OUT_OF_MEMORY, "false"))) {
			long period = 60000; // 1 min
			try {
				period = Long.parseLong(getProperty(MANAGE_OUT_OF_MEMORY_PERIOD, "60000"));
			}
			catch (Exception e) { 
				// Ignore and keep default
			}
			int blocksize = 10000; // 10 K
			try {
				blocksize = Integer.parseInt(getProperty(MANAGE_OUT_OF_MEMORY_BLOCKSIZE, "10000"));
			}
			catch (Exception e) { 
				// Ignore and keep default
			}
			// If Out Of Memory management is enabled default action is KILL
			oomAction = getProperty(MANAGE_OUT_OF_MEMORY_ACTION, KILL);
			addBehaviour(new OutOfMemoryManager(period, blocksize));
		}
		
		addBehaviour(new CyclicBehaviour(this) {
			
			public void action() {
				try {
					ACLMessage msg = myAgent.receive(template);
					if (msg != null) {
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						String content = msg.getContent();
						try {
							String contentUC = content.toUpperCase();
							if (contentUC.startsWith(DUMP_AGENTS_ACTION)) {
								reply.setContent(getAgentsDump());
							}
							else if (contentUC.startsWith(DUMP_AGENT_ACTION)) {
								String agentName = getParameter(content, 0, false); // MANDATORY
								Agent a = getAgentFromLADT(agentName);
								String replyContent = null;
								if(a != null) {
									replyContent = getAgentDump(a, true);
								}
								else {
									reply.setPerformative(ACLMessage.FAILURE);
									replyContent = "Agent " + agentName + " doesn't exist";
								}
								reply.setContent(replyContent);
							}
							else if (contentUC.startsWith(DUMP_MESSAGEQUEUE_ACTION)) {
								String agentName = getParameter(content, 0, false); // MANDATORY
								Agent a = getAgentFromLADT(agentName);
								String replyContent = null;
								if(a != null) {
									replyContent = getMessageQueueDump(a);
								}
								else {
									reply.setPerformative(ACLMessage.FAILURE);
									replyContent = "Agent " + agentName + " doesn't exist";
								}
								reply.setContent(replyContent);
							}
							else if (contentUC.startsWith(DUMP_MESSAGEMANAGER_ACTION)) {
								reply.setContent(getMessageManagerDump());
							}
							else if (contentUC.startsWith(DUMP_LADT_ACTION)) {
								reply.setContent(getLADTDump());
							}
							else if (contentUC.startsWith(DUMP_SERVICES_MAP_ACTION)) {
								reply.setContent(getServicesMapDump());
							}
							else if (contentUC.startsWith(DUMP_PLATFORM_MANAGER_ACTION)) {
								reply.setContent(getPlatformManagerDump());
							}
							else if (contentUC.startsWith(DUMP_SERVICES_ACTION)){
								reply.setContent(getServicesDump());
							}
							else if (contentUC.startsWith(DUMP_SERVICE_ACTION)) {
								String serviceName = getParameter(content, 0, false); // MANDATORY
								String key = getParameter(content, 1, true); // OPTIONAL
								BaseService srv = getService(serviceName);
								if (srv != null) {
									reply.setContent(getServiceDump(srv, key));
								}
								else {
									reply.setPerformative(ACLMessage.FAILURE);
									reply.setContent("Service " + serviceName + " not installed");
								}
							}
							else if (contentUC.startsWith(DUMP_THREADS_ACTION)) {
								reply.setContent(getThreadsDump());
							}
							else if (contentUC.startsWith(HELP_ACTION)) {
								reply.setContent(getHelp());
							}
							else {
								reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							reply.setPerformative(ACLMessage.FAILURE);
							reply.setContent(e.toString());
						}
						if (reply.getPerformative() == ACLMessage.INFORM) {
							System.out.println(reply.getContent());
						}
						myAgent.send(reply);
					}
					else {
						block();
					}
				}
				catch (OutOfMemoryError oome) {
					manageOOM();
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
		
		addBehaviour(new CyclicBehaviour(this) {
			public void action() {
				try {
					ACLMessage msg = myAgent.receive(helpTemplate);
					if (msg != null) {
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
						reply.setContent(getHelp());
						myAgent.send(reply);
					}
					else {
						block();
					}
				}
				catch (OutOfMemoryError oome) {
					manageOOM();
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		} );
	}
	
	public String getHelp() {
		StringBuffer sb = new StringBuffer("This agent accepts REQUEST messages refering to the "+CONTAINER_MONITOR_ONTOLOGY+" ontology.\nSupported actions:\n");
		sb.append(DUMP_AGENTS_ACTION).append('\n');
		sb.append(DUMP_AGENT_ACTION).append(" <agent-local-name>").append('\n');
		sb.append(DUMP_MESSAGEQUEUE_ACTION).append(" <agent-local-name>").append('\n');
		sb.append(DUMP_LADT_ACTION).append('\n');
		sb.append(DUMP_MESSAGEMANAGER_ACTION).append('\n');
		sb.append(DUMP_SERVICES_MAP_ACTION).append("(only available on Main-Containers)").append('\n');
		sb.append(DUMP_SERVICES_ACTION).append('\n');
		sb.append(DUMP_SERVICE_ACTION).append(" <service-name>").append('\n');
		sb.append(DUMP_THREADS_ACTION).append('\n');
		sb.append(DUMP_PLATFORM_MANAGER_ACTION).append("(only available on Main-Containers)").append('\n');
		return sb.toString();
	}
	
	private String getParameter(String content, int index, boolean optional) throws Exception {
		String action = null;
		StringTokenizer st = new StringTokenizer(content, " ");
		try {
			action = st.nextToken();
			// Skip parameters before the one we want (first parameter is at index 0: no skip)
			for (int i = 0; i < index; ++i) {
				st.nextToken();
			}
			return st.nextToken();
		}
		catch (NoSuchElementException nsee) {
			if (optional) {
				return null;
			}
			else {
				throw new Exception("Missing parameter for action "+action);
			}
		}
	}
	
	public String[] getLADTStatus() {
		return myLADT.getStatus();
	}
	
	public String getAgentsDump() {
		StringBuffer sb = new StringBuffer();
		sb.append("-------------------------------------------------------------\n");
		sb.append("Container ");
		sb.append(myContainer.getID().getName());
		sb.append(" agents DUMP\n");
		sb.append("-------------------------------------------------------------\n");
	    Agent[] agents = myLADT.values();
	    for (int i = 0; i < agents.length; ++i) {
	    	Agent a = agents[i];
	    	String agentDump = getAgentDump(a, false);
	    	sb.append(agentDump);
	    }
		sb.append("-------------------------------------------------------------\n");
		return sb.toString();
	}
	
	public static String getAgentDump(Agent a, boolean stackTraceMode) {
		StringBuffer sb = new StringBuffer();
		if (a != null) {
    		try {
	    		sb.append("Agent "+a.getName()+"\n");
	    		sb.append("  - Class = "+a.getClass().getName()+"\n");
	    		sb.append("  - State = "+a.getState()+"\n");
	    		sb.append("  - MessageQueue size = "+a.getMessageQueue().size()+"\n");
	    		sb.append("  - Behaviours\n");
	    		Behaviour[] bb = a.getScheduler().getBehaviours();
	    		for (int j = 0; j < bb.length; ++j) {
	    			Behaviour b = bb[j];
		    		sb.append("    - Behaviour "+b.getBehaviourName()+"\n");
		    		appendBehaviourInfo(b, sb, "      ", stackTraceMode);
	    		}
	    		if(stackTraceMode) {
	    			String dumpAgentThread = dumpThread("    ", a.getThread());
	    			sb.append("  - Agent thread dump\n");
	    			sb.append(dumpAgentThread);
	    		}
	    		try {
	    			Method dumpMethod = a.getClass().getMethod("dump", new Class[0]);
	    			String agentSpecificDump = (String) dumpMethod.invoke(a, new Object[0]);
	    			sb.append("Agent specific dump\n");
	    			sb.append(agentSpecificDump);
	    		}
	    		catch (Throwable t) {
	    			// dump() method not present --> Just do nothing 
	    		}
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
		return sb.toString();
	}
	
	private Agent getAgentFromLADT(String agentName) {
		Agent result = null;
		Agent[] agents = myLADT.values();
	    for (int i = 0; i < agents.length; ++i) {
	    	Agent a = agents[i];
	    	if(a.getAID().getLocalName().equals(agentName)) {
	    		result = a;
	    		break;
	    	}
	    }
		return result;
	}
	
	private BaseService getService(String serviceName) throws Exception {
		BaseService srv = null;
		try {
			srv = (BaseService) myContainer.getServiceFinder().findService(serviceName);
		}
		catch (ClassCastException cce) {
			throw new Exception("Service "+serviceName+" is not a BaseService. It cannot be dumped");
		}
		catch (Exception e) {
			// ServiceException and IMTPException should never happen as this is a local call
			e.printStackTrace();
		}
		return srv;
	}
	
	public String getServiceDump(BaseService srv, String key) {
		StringBuffer sb = new StringBuffer();
		if (srv != null) {
    		sb.append("SERVICE ").append(srv.getName()).append('\n');
    		sb.append("--------------------------------------------\n");
    		sb.append(srv.dump(key));
    		sb.append("--------------------------------------------\n");
		}
		return sb.toString();
	}
	
	private static String dumpThread(String prefix, Thread t) {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo threadInfo = threadMXBean.getThreadInfo(t.getId());
		return dumpThread(prefix, t, threadInfo);
	}

	private static String dumpThread(String prefix, Thread t, ThreadInfo threadInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix + "\"" + t.getName() + "\"");
		if(t.isDaemon()) {
			sb.append(" daemon");
		}
		String threadId = threadInfo != null ? String.valueOf(threadInfo.getThreadId()) : String.valueOf(t.getId());
		sb.append(" tid=" + threadId);
		sb.append(" " + t.getState().toString().toLowerCase());
		if(threadInfo != null) {
			String lockedOn = threadInfo.getLockName();
			if(lockedOn != null) {
				String lockedBy = threadInfo.getLockOwnerName();
				sb.append(" on " + lockedOn);
				if(lockedBy != null) {
					sb.append(" held by " + lockedBy);
				}
			}
		}
		sb.append("\n");
		StackTraceElement[] ste = t.getStackTrace();
		for(int i=0; i<ste.length; i++) {
			sb.append(prefix + "\t at " + ste[i] + "\n");
		}
		return sb.toString();
	}
	
	private String dumpAllThreads() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		StringBuffer sb = new StringBuffer();
		java.util.Map<Thread, StackTraceElement[]> allStackTraces = Thread.currentThread().getAllStackTraces();
		Set<Thread> threads = allStackTraces.keySet();
		for (Thread thread : threads) {
			ThreadInfo threadInfo = threadMXBean.getThreadInfo(thread.getId());
			sb.append(dumpThread("", thread, threadInfo));
		}
		long[] threadIds = threadMXBean.findMonitorDeadlockedThreads();
		if(threadIds != null) {
			ThreadInfo[] threadInfoInDeadlock = threadMXBean.getThreadInfo(threadIds);
			sb.append("\n\n\n**************** WARNING ****************: Threads ");
			for (int i = 0; i < threadInfoInDeadlock.length; i++) {
				sb.append(" \"" + threadInfoInDeadlock[i].getThreadName() + "\"");
			}
			sb.append(" are in deadlock!");
		}
		return sb.toString();
	}	
	
	private static void appendBehaviourInfo(Behaviour b, StringBuffer sb, String prefix, boolean stackTraceMode) {
		sb.append(prefix+"- Class = "+b.getClass().getName()+"\n");
		sb.append(prefix+"- State = "+b.getExecutionState()+"\n");
		sb.append(prefix+"- Runnable = "+b.isRunnable()+"\n");
		if (b instanceof CompositeBehaviour) {
			sb.append(prefix+"- Type = "+getCompositeType((CompositeBehaviour) b)+"\n");
			Behaviour child = getCurrent((CompositeBehaviour) b, b.getClass());
			if (child != null) {
				sb.append(prefix+"- Current child information\n");
				sb.append(prefix+"  - Name = "+child.getBehaviourName()+"\n");
				appendBehaviourInfo(child, sb, prefix+"  ", stackTraceMode);
			}
		}
		else if (b instanceof WrapperBehaviour) {
			sb.append(prefix+"- Type = Wrapper\n");
			sb.append(prefix+"- Wrapped-Behaviour Information\n");
			Behaviour wb = ((WrapperBehaviour) b).getWrappedBehaviour();
			sb.append(prefix+"  - Name = "+wb.getBehaviourName()+"\n");
			appendBehaviourInfo(wb, sb, prefix+"  ", stackTraceMode);
		}
		else if (b instanceof ThreadedBehaviourFactory.ThreadedBehaviourWrapper) {
			ThreadedBehaviourFactory.ThreadedBehaviourWrapper w = (ThreadedBehaviourFactory.ThreadedBehaviourWrapper) b;
			sb.append(prefix+"- Type = Threaded\n");
			sb.append(prefix+"- Thread-state = "+w.getThreadState()+"\n");
			sb.append(prefix+"- Thread Information\n");
			Thread t = w.getThread();
			if (t != null) {
				sb.append(prefix+"  - Alive = "+t.isAlive()+"\n");
				sb.append(prefix+"  - Interrupted = "+t.isInterrupted()+"\n");
				if(stackTraceMode) {
					String dumpBehaviourThread = dumpThread(prefix+"    ", t);
					sb.append(prefix+"  - Behaviour thread dump\n");
					sb.append(dumpBehaviourThread);
				}
			}
			sb.append(prefix+"- Threaded Behaviour Information\n");
			Behaviour tb = w.getBehaviour();
			sb.append(prefix+"  - Name = "+tb.getBehaviourName()+"\n");
			appendBehaviourInfo(tb, sb, prefix+"  ", stackTraceMode);
		}
		else {
			sb.append(prefix+"- Type = "+getSimpleType(b)+"\n");
		}
		// Behaviour specific dump
		try {
			Method dumpMethod = b.getClass().getMethod("dump", new Class[0]);
			String behaviourSpecificDump = (String) dumpMethod.invoke(b, new Object[0]);
			if (behaviourSpecificDump != null) {
				behaviourSpecificDump = behaviourSpecificDump.replace("\n", "\n"+prefix);
				if (behaviourSpecificDump.endsWith("\n"+prefix)) {
					behaviourSpecificDump = behaviourSpecificDump.substring(0, behaviourSpecificDump.length() - prefix.length());
				}
				sb.append(prefix+"Behaviour specific dump\n");
				sb.append(prefix+behaviourSpecificDump);
			}
		}
		catch (Throwable t) {
			// dump() method not present --> Just do nothing 
		}
	}
	
	private static String getSimpleType(Behaviour b) {
		if (b instanceof CyclicBehaviour) {
			return "Cyclic";
		}
		else if (b instanceof OneShotBehaviour) {
			return "OneShot";
		}
		else if (b instanceof WakerBehaviour) {
			return "Waker";
		}
		else if (b instanceof TickerBehaviour) {
			return "Ticker";
		}
		else {
			return "Simple";
		}
	}
	
	private static String getCompositeType(CompositeBehaviour cb) {
		if (cb instanceof FSMBehaviour) {
			return "FSM";
		}
		else if (cb instanceof SequentialBehaviour) {
			return "Sequential";
		}
		else if (cb instanceof ParallelBehaviour) {
			return "Parallel";
		}
		else {
			return "Composite";
		}
	}
	
	private static Behaviour getCurrent(CompositeBehaviour cb, Class c) {
		Method getCurrentMethod = null;
		try {
			Behaviour b = null;
			getCurrentMethod = c.getDeclaredMethod("getCurrent", (Class[]) null);
			boolean accessibilityChanged = false;
			if (!getCurrentMethod.isAccessible()) {
				try {
					getCurrentMethod.setAccessible(true);
					accessibilityChanged = true;
				}
				catch (SecurityException se) {
					// Cannot change accessibility 
					return null;
				}
			}					
			try { 			
				b = (Behaviour) getCurrentMethod.invoke(cb, (Object[]) null);
				// Restore accessibility if changed
				if (accessibilityChanged) {
					getCurrentMethod.setAccessible(false);
				}
			}
			catch (Exception e) {
				// Should never happen
				e.printStackTrace();
			}
			return b;
		}
		catch (NoSuchMethodException e) {
			// getCurrent() method not defined. Try in the superclass if any.	
			Class superClass = c.getSuperclass();
			if (superClass != null) {
				return getCurrent(cb, superClass);
			}
		}
		catch (Exception e1) {
		}
		return null;
	}
	
	public String getMessageQueueDump(Agent a) {
		// WE don't use acquire() to avoid risk of blocking in case there is a deadlock
		StringBuffer sb = new StringBuffer();
		sb.append("-------------------------------------------------------------\n");
		sb.append("Agent ");
		sb.append(a.getLocalName());
		sb.append(" MessageQueue DUMP\n");
		sb.append("-------------------------------------------------------------\n");
		MessageQueue queue = a.getMessageQueue();
		if (queue instanceof InternalMessageQueue) {
			Object[] messages = ((InternalMessageQueue) queue).getAllMessages();
			if (messages.length > 0) {
	    		for (int j = 0; j < messages.length; ++j) {
	    			sb.append("Message # ");
	    			sb.append(j);
	    			sb.append('\n');
	    			sb.append(messages[j]);
	    			sb.append('\n');
	    		}
			}
			else {
				sb.append("Queue is empty\n");
			}
		}
		else {
			sb.append("MessageQueue is not an InternalMessageQueue. Cannot dump it\n");
		}
		return sb.toString();
	}
	
	public String getLADTDump() {
		StringBuffer sb = new StringBuffer();
		sb.append("-------------------------------------------------------------\n");
		sb.append("Container ");
		sb.append(myContainer.getID().getName());
		sb.append(" LADT DUMP\n");
		sb.append("-------------------------------------------------------------\n");
		try {
			String[] ladtStatus = myLADT.getStatus();	
			for (int i = 0; i < ladtStatus.length; ++i) {
				sb.append("- "+ladtStatus[i]+"\n");
			}
		}
		catch (Exception e) {
		}
		sb.append("-------------------------------------------------------------\n");
		return sb.toString();
	}
	
	public String getMessageManagerDump() {
		StringBuffer sb = new StringBuffer();
		sb.append("-------------------------------------------------------------\n");
		sb.append("Container ");
		sb.append(myContainer.getID().getName());
		sb.append(" Message-Manager DUMP\n");
		sb.append("-------------------------------------------------------------\n");
		try {
			ServiceFinder sf = myContainer.getServiceFinder();
			MessagingService service = (MessagingService) sf.findService(MessagingService.NAME);
			sb.append("- Global information: " + service.getMessageManagerGlobalInfo() + "\n");
			String[] queueStatus = service.getMessageManagerQueueStatus();	
			sb.append("- Queue status:\n");
			if (queueStatus.length == 0) {
				sb.append("    EMPTY\n");
			}
			else {
				for (int i = 0; i < queueStatus.length; ++i) {
					sb.append("  - "+queueStatus[i]+"\n");
				}
			}
			sb.append("- Thread pool status:\n");
			String[] threadPoolStatus = service.getMessageManagerThreadPoolStatus();	
			for (int i = 0; i < threadPoolStatus.length; ++i) {
				sb.append("  - "+threadPoolStatus[i]+"\n");
			}
			sb.append("- Thread pool dump:\n");
			Thread[] threadPool = service.getMessageManagerThreadPool();
			for (int i = 0; i < threadPool.length; i++) {
				String dumpDelivererThread = dumpThread("  ", threadPool[i]);
				sb.append(dumpDelivererThread);
			}
		}
		catch (Exception e) {
		}
		sb.append("-------------------------------------------------------------\n");
		return sb.toString();
	}
	
	public String getServicesMapDump() {
		StringBuffer sb = new StringBuffer();
		try {
			MainContainerImpl mc = (MainContainerImpl) myContainer.getMain();
			if (mc != null) {
				PlatformManagerImpl pm = (PlatformManagerImpl) mc.getPlatformManager();
				sb.append("-------------------------------------------------------------\n");
				sb.append("Platform services DUMP\n");
				sb.append("-------------------------------------------------------------\n");
				Map services = pm.getServicesMap();
				Iterator it = services.keySet().iterator();
				while (it.hasNext()) {
					String serviceName = (String) it.next();
					PlatformManagerImpl.ServiceEntry se = (PlatformManagerImpl.ServiceEntry) services.get(serviceName);
					sb.append("Service entry "+serviceName+"\n");
					dumpServiceEntry(se, sb);
				}
				sb.append("-------------------------------------------------------------\n");
			}
			else {
				sb.append("Container "+myContainer.getID().getName()+" is not a Main!");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			sb.append(e.toString());
		}
		return sb.toString();
	}
	
	public String getPlatformManagerDump() {
		StringBuffer sb = new StringBuffer();
		try {
			MainContainerImpl mc = (MainContainerImpl) myContainer.getMain();
			if (mc != null) {
				PlatformManagerImpl pm = (PlatformManagerImpl) mc.getPlatformManager();
				sb.append("-------------------------------------------------------------\n");
				sb.append("PlatformManager DUMP\n");
				sb.append("-------------------------------------------------------------\n");
				sb.append("NODES\n");
				Map nodes = pm.getNodesMap();
				Map monitors = pm.getMonitorsMap();
				Iterator it = nodes.keySet().iterator();
				while (it.hasNext()) {
					NodeDescriptor dsc = (NodeDescriptor) nodes.get(it.next());
					sb.append("- "+dsc.getName()+": ");
					Node n = dsc.getNode();
					if (n.hasPlatformManager()) {
						sb.append("PlatformManager-node, ");
					}
					else {
						Node parent = dsc.getParentNode();
						if (parent != null) {
							sb.append("child-node ["+parent.getName()+"], ");
						}
						else {
							sb.append("normal-node, ");
						}
					}
					if (monitors.get(dsc.getName()) != null) {
						sb.append("monitored\n");
					}
					else {
						sb.append("not-monitored\n");
					}
				}
				sb.append("\n");
				sb.append("REPLICAS\n");
				Map replicas = pm.getReplicasMap();
				it = replicas.keySet().iterator();
				while (it.hasNext()) {
					sb.append("- "+it.next()+"\n");
				}
				sb.append("-------------------------------------------------------------\n");
			}
			else {
				sb.append("Container "+myContainer.getID().getName()+" is not a Main!");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			sb.append(e.toString());
		}
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public String getServicesDump() {
		ServiceManager serviceManager = myContainer.getServiceManager();
		Vector<ServiceDescriptor> localServices = serviceManager.getLocalServices();
		StringBuffer sb = new StringBuffer();
		for (ServiceDescriptor ls : localServices) {
			sb.append(ls.getName()+ "\n");
		}
		return sb.toString();
	}

	public String getThreadsDump() {
		StringBuffer sb = new StringBuffer();
		sb.append("-------------------------------------------------------------\n");
		sb.append("JVM Threads DUMP\n");
		sb.append("-------------------------------------------------------------\n");
		sb.append(dumpAllThreads());
		sb.append("-------------------------------------------------------------\n");
		return sb.toString();
	}
	
	public void dumpServiceEntry(PlatformManagerImpl.ServiceEntry se, StringBuffer sb) {
		sb.append("  - Name = "+se.getService().getName()+"\n");
		sb.append("  - Class = "+se.getService().getClass().getName()+"\n");
		sb.append("  - Slices:\n");
		Map slices = se.getSlicesMap();
		Iterator it = slices.keySet().iterator();
		while (it.hasNext()) {
			String sliceName = (String) it.next();
			PlatformManagerImpl.SliceEntry sle = (PlatformManagerImpl.SliceEntry) slices.get(sliceName);
			sb.append("    - Slice "+sliceName+"\n");
			sb.append("      - Class = "+sle.getSlice().getClass().getName()+"\n");
			try {
				Node associatedNode = sle.getNode();
				sb.append("      - Associated node = "+associatedNode);
				sb.append("\n");
				Node innerNode = sle.getSlice().getNode();
				sb.append("      - Inner node      = "+innerNode);
				sb.append("\n");
				if (!checkConsistency(sliceName, associatedNode, innerNode)) {
					sb.append("      WARNING!!!!!!!!!!! Slice is inconsistent\n");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkConsistency(String name, Node n1, Node n2) {
		if (n1 != null && n2 != null) {
			return name.equals(n1.getName()) && name.equals(n2.getName());
		}
		else {
			return false;
		}
	}
	
	private void manageOOM() {
		try {
			if (oomAction.equals(KILL)) {
				System.out.println("OUT OF MEMORY DETECTED!!!!!!!! Kill JVM");
				System.exit(100);
			}
			else if (oomAction.equals(LOG)) {
				System.out.println("OUT OF MEMORY DETECTED!!!!!!!!");
			}
		}
		catch (Throwable t) {			
		}
	}
	
	
	/**
	 * Inner class OutOfMemoryManager
	 */
	private class OutOfMemoryManager extends TickerBehaviour {
		private int blocksize;
		
		public OutOfMemoryManager(long period, int blocksize) {
			super(null, period);
			this.blocksize = blocksize;
		}

		@Override
		protected void onTick() {
			try {
				byte[] dummy = new byte[blocksize];
			}
			catch (Throwable oome) {
				manageOOM();
			}
		}
	}
}
