/*
 * (c) Copyright Enterprise Computing Research Group (ECRG),
 *               National University of Ireland, Galway 2003.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE and
 * no warranty that the program does not infringe the Intellectual Property rights of a third party.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

/**
 * JBossMQ implementation of the ProviderAdmin interface
 * 
 * <p>
 * Provides support for the JBossMQ JMS provider
 * </p>
 */
package ie.nuigalway.ecrg.jade.jmsmtp.providersupport;

import ie.nuigalway.ecrg.jade.jmsmtp.common.JMSAddress;
import ie.nuigalway.ecrg.jade.jmsmtp.common.ProviderAdmin;

import jade.mtp.MTPException;

import java.util.Properties;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;

import javax.management.ObjectName;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Category;

import org.jboss.jmx.adaptor.rmi.RMIAdaptor;


public class JBossMqProviderAdmin implements ProviderAdmin {

  private static Category log = Category.getRoot();
  Properties props;

  /**
   * Setup the JBossMQ Provider Admin
   */
  public JBossMqProviderAdmin () {
    props = new Properties();

    if (log.isDebugEnabled()) {
      log.debug("Creating InitialContext");
    }

    props.put("java.naming.factory.initial", 
              "org.jnp.interfaces.NamingContextFactory");
    props.put("java.naming.factory.url.pkgs", 
              "org.jboss.naming:org.jnp.interfaces");
  }

  /**
   * Create a QueueConnection to a specified broker and place it in the hashmap
   * 
   * @param jmsTA Details of broker to create connection factory too
   * @return QueueConnectionFactory onnection to this broker
   * @throws MTPException Error creating the ConnectionFactory
   */
  public QueueConnectionFactory getQueueConnectionFactory (JMSAddress jmsTA)
                                                    throws MTPException {
    props.put("java.naming.provider.url", jmsTA.getBrokerURL());

    ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

    try {

      // Save the class loader so that you can restore it later
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

      InitialContext ctx = new InitialContext(props);

      return (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
    } catch (NamingException ne) {
      log.error("ConnectionFactory not found:" + ne.toString());
      throw new MTPException("Connection Factory not found", ne);
    }
     catch (Exception e) {
      log.error("Failed to create InitialContext:" + e.toString());
      throw new MTPException("Failed to create InitialContext", e);
    } finally {

      // Restore
      Thread.currentThread().setContextClassLoader(prevCl);
    }
  }

  /**
   * Lookup or create a specified queue and return it
   * 
   * @param jmsTA Contains details of the queue to lookup or create
   * @return Queue The specified queue returned
   * @throws MTPException Error while creating the queue
   */
  public Queue getOrCreateQueue (JMSAddress jmsTA) throws MTPException {

    Queue endResult = null;
    props.put("java.naming.provider.url", jmsTA.getBrokerURL());

    ClassLoader prevCl = Thread.currentThread().getContextClassLoader();

    try {

      // Save the class loader so that you can restore it later
      Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

      InitialContext iniCtx = new InitialContext(props);

      // Lookup Queue
      try {

        if (log.isDebugEnabled()) {
          log.debug("Trying to connect to existing queue...");
        }

        endResult = (Queue) iniCtx.lookup("queue/" + jmsTA.getQueueName());

        if (log.isDebugEnabled()) {
          log.debug("...queue exists!");
        }
      } catch (NamingException ne) {

        if (log.isDebugEnabled()) {
          log.debug("...queue does not exist");
        }

        // Use the JMX RMIAdaptor to connect to the server
        RMIAdaptor jmxAccess = (RMIAdaptor) iniCtx.lookup("jmx/rmi/RMIAdaptor");
        ObjectName objName = new ObjectName(
                                   "jboss.mq:service=DestinationManager");
        String[] signature = { "java.lang.String" };
        Object[] arguments = { jmsTA.getQueueName() };
        Object result = jmxAccess.invoke(objName, "createQueue", arguments, 
                                         signature);

        // Append 'queue/' before the queue name to locate it in JNDI  (JBossMQ convention)
        endResult = (Queue) iniCtx.lookup("queue/" + jmsTA.getQueueName());
      }
    } catch (Exception e) {

      if (log.isDebugEnabled()) {
        log.debug("Failed to get or create the queue:" + e.toString());
      }

      throw new MTPException("Failed to get or create the queue:", e);
    } finally {

      // Restore
      Thread.currentThread().setContextClassLoader(prevCl);
    }

    return endResult;
  }
}
