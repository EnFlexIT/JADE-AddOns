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


package jade.core.persistence;


import net.sf.hibernate.Hibernate;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import jade.core.Agent;
import jade.core.AID;
import jade.core.ServiceException;
import jade.core.NotFoundException;

import jade.lang.acl.ACLMessage;

import jade.util.leap.Map;
import jade.util.leap.List;
import jade.util.leap.HashMap;
import jade.util.leap.Properties;


/**

   The store/retrieve engine used to manage saving and retrieving
   agents and containers to persistent storage, using Hibernate.

   @author Giovanni Rimassa - FRAMeTech s.r.l.

*/
public class PersistenceManager {


    public PersistenceManager(String nodeName) {
	repositories = new HashMap();

	// Create a default repository
	try {
	    Configuration defaultConf = new Configuration();

	    // Insert the common class definitions
	    defaultConf.addResource("jade/core/persistence/common.hbm.xml", getClass().getClassLoader());

	    // Insert the definitions for a saved agent and a saved
	    // ACL message
	    defaultConf.addClass(SavedAgent.class);
	    defaultConf.addClass(SavedACLMessage.class);
	    defaultConf.addClass(FrozenAgent.class);
	    defaultConf.addClass(FrozenMessageQueue.class);

	    schemaManager = new SchemaExport(defaultConf);

	    // Add the name of this container to the DB file
	    String dbURL = defaultConf.getProperty("hibernate.connection.url");
	    dbURL = dbURL + "-" + nodeName;
	    defaultConf.setProperty("hibernate.connection.url", dbURL);

	    System.out.println(">>> Connecting to the DB [" + dbURL + "] <<<");

	    SessionFactory sf = defaultConf.buildSessionFactory();
	    checkSchema("JADE-DB", sf);

	    repositories.put("JADE-DB", sf);

	}
	catch(HibernateException he) {
	    he.printStackTrace();
	}

    }

    public synchronized void addRepository(String name, Properties p) {
	// FIXME: To be implemented -- Should take the Hibernate
	// configuration from the passed properties.
    }

    public synchronized void removeRepository(String name) {
	repositories.remove(name);
    }

    public void saveAgent(Agent target, String repository, java.util.List pendingMessages) throws ServiceException, NotFoundException {

	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {
		// Save this agent
		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    tx = s.beginTransaction();
		    SavedAgent toSave = new SavedAgent(target, pendingMessages);
		    java.util.List resultSet = s.find("from jade.core.persistence.SavedAgent as item where item.agentIdentifier.name = ?", target.getName(), Hibernate.STRING);
		    if(!resultSet.isEmpty()) {
			toSave = (SavedAgent)resultSet.get(0);
		    }

		    s.save(toSave);
		    tx.commit();
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while persisting the agent", he);
	}
    }

    public Agent loadAgent(AID target, String repository) throws ServiceException, NotFoundException {
	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {

		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    tx = s.beginTransaction();
		    java.util.List resultSet = s.find("from jade.core.persistence.SavedAgent as item where item.agentIdentifier.name = ?", target.getName(), Hibernate.STRING);
		    tx.commit();

		    if(!resultSet.isEmpty()) {
			SavedAgent loaded = (SavedAgent)resultSet.get(0);
			Agent result = loaded.getAgent();
			java.util.List pendingMessages = loaded.getPendingMessages();

			// Restore the agent message queue inserting
			// received messages at the start of the queue
			for(int i = pendingMessages.size(); i > 0; i--) {
			    result.putBack((ACLMessage)pendingMessages.get(i - 1));
			}

			return result;
		    }
		    else {
			throw new NotFoundException("Agent <" + target.getLocalName() + "> was not found in repository <" + repository + ">");
		    }
		}
		catch(HibernateException he) {
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while loading agent <" + target.getLocalName() + ">", he);
	}
    }

    public void deleteAgent(AID target, String repository) throws ServiceException, NotFoundException {

	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {

		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    tx = s.beginTransaction();
		    int deleted1 = s.delete("from jade.core.persistence.FrozenAgent as item where item.agent.agentIdentifier.name = ?", target.getName(), Hibernate.STRING);
		    int deleted2 = s.delete("from jade.core.persistence.SavedAgent as item where item.agentIdentifier.name = ?", target.getName(), Hibernate.STRING);
		    tx.commit();
		    System.out.println("--- Deleted " + deleted1 + " frozen and " + deleted2 + " saved agents ---");
		}
		catch(HibernateException he) {
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while deleting agent <" + target.getLocalName() + ">", he);
	}
    }

    public void deleteFrozenAgent(Long agentPK, String repository) throws ServiceException, NotFoundException {

	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {

		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    tx = s.beginTransaction();
		    Object toDelete = s.load(FrozenAgent.class, agentPK);
		    s.delete(toDelete);
		    tx.commit();

		    System.out.println("--- Deleted frozen agent <" + agentPK + "> ---");
		}
		catch(HibernateException he) {
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while deleting a frozen agent <" + agentPK + ">", he);
	}
    }

    public Long freezeAgent(Agent target, String repository, java.util.List pendingMessages) throws ServiceException, NotFoundException {

	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {
		// Save this agent
		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    SavedAgent sa = new SavedAgent(target, pendingMessages);
		    FrozenAgent toFreeze = new FrozenAgent(sa);
		    tx = s.beginTransaction();
		    Long newID  = (Long)s.save(toFreeze);
		    tx.commit();
		    return newID;
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while persisting the agent", he);
	}
    }

    public Long createFrozenMessageQueue(AID agentID, Long agentFK, String repository) throws ServiceException, NotFoundException {
	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {
		// Save this agent
		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    FrozenMessageQueue toFreeze = new FrozenMessageQueue(agentID, agentFK);
		    tx = s.beginTransaction();
		    Long newID  = (Long)s.save(toFreeze);
		    tx.commit();
		    return newID;
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while persisting the agent", he);
	}
    }

    public void connectToMessageQueue(Long agentID, Long messageQueueFK, String repository) throws ServiceException, NotFoundException {
	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {
		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    // Update the frozen agent with the foreign key
		    // for its frozen message queue (possibly stored
		    // on another DBMS)
		    tx = s.beginTransaction();
		    FrozenAgent frozen = (FrozenAgent)s.load(FrozenAgent.class, agentID);
		    frozen.setMessageQueueFK(messageQueueFK);
		    tx.commit();
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while persisting the agent", he);
	}
    }

    public void bufferMessage(Long queueID, String repository, ACLMessage msg) throws ServiceException, NotFoundException {
	try {
	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {
		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    // Add the received message to the buffered messages
		    tx = s.beginTransaction();
		    FrozenMessageQueue frozen = (FrozenMessageQueue)s.load(FrozenMessageQueue.class, queueID);
		    frozen.getBufferedMessages().add(msg);
		    tx.commit();
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while buffering the message", he);
	}
    }

    public Agent thawAgent(AID target, String repository, Long persistentID) throws ServiceException, NotFoundException {
	try {

	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {

		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    tx = s.beginTransaction();

		    FrozenAgent frozen = (FrozenAgent)s.load(FrozenAgent.class, persistentID);
		    SavedAgent sa = frozen.getAgent();
		    Agent result = sa.getAgent();


		    // Restore the agent message queue inserting
		    // received messages at the start of the queue.
		    // We put the buffered messages in front of the
		    // messages pending at the moment of freezing the
		    // agent.

		    java.util.List pendingMessages = sa.getPendingMessages();
		    for(int i = pendingMessages.size(); i > 0; i--) {
			ACLMessage msg = (ACLMessage)pendingMessages.get(i - 1);
			result.putBack((ACLMessage)msg.clone());
		    }

		    // Remove the frozen instance from the persistent store
		    s.delete(frozen);

		    tx.commit();
		    return result;
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while thawing agent <" + target.getLocalName() + ">", he);
	}
    }

    public Long evictFrozenMessageQueue(Long id, String repository, List bufferedMessages) throws ServiceException, NotFoundException {
	try {

	    SessionFactory sf = getRepository(repository);
	    if(sf != null) {

		Session s = sf.openSession();
		Transaction tx = null;
		try {
		    tx = s.beginTransaction();

		    FrozenMessageQueue frozen = (FrozenMessageQueue)s.load(FrozenMessageQueue.class, id);
		    Long result = frozen.getAgentFK();

		    if(bufferedMessages != null) {
			bufferedMessages.clear();
			java.util.List l = frozen.getBufferedMessages();
			for(int i = 0; i < l.size(); i++) {
			    ACLMessage msg = (ACLMessage)l.get(i);
			    bufferedMessages.add(msg);
			}
		    }

		    // Remove the frozen instance from the persistent store
		    s.delete(frozen);

		    tx.commit();
		    return result;
		}
		catch(HibernateException he) {
		    he.printStackTrace();
		    if(tx != null) {
			tx.rollback();
		    }
		    throw he;
		}
		finally {
		    s.close();
		}
	    }
	    else {
		throw new NotFoundException("The repository <" + repository + "> was not found");
	    }
	}
	catch(HibernateException he) {
	    throw new ServiceException("An error occurred while evicting a message queue", he);
	}
    }

    private synchronized SessionFactory getRepository(String name) {
	return (SessionFactory)repositories.get(name);
    }

    private void checkSchema(String repository, SessionFactory sf) throws HibernateException {
	// Tries a simple query, and rebuild the schema if the query fails
	Session s = null;
	try {
	    s = sf.openSession();
	    int howMany = ((Integer)s.iterate("select count(*) from jade.core.persistence.SavedAgent").next()).intValue();
	    System.out.println("--- The repository " + repository + " holds " + howMany + " agents ---");
	}
	catch(HibernateException he) {
	    System.out.println("--- The repository " + repository + " does not appear to have a valid schema ---");
	    System.out.println("--- Rebuilding DB schema ---");

	    // Remove the DB schema
	    schemaManager.drop(false, true);

	    // Export the DB schema
	    schemaManager.create(false, true);
	}
	finally {
	    if(s != null) {
		s.close();
	    }
	}
    }

    private Map repositories;

    // Hibernate-specific variables
    private SchemaExport schemaManager;


}
