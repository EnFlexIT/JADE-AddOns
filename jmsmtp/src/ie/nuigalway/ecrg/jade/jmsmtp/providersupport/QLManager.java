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
 * Agent Queue Listener (QL) Manager
 * 
 * <p>
 * Manager starts and controls AgentQueueListeners that listen to queues
 * specified in a JMSAddresse on a JMS provider.
 * </p>
 * 
 */
package ie.nuigalway.ecrg.jade.jmsmtp.providersupport;

import ie.nuigalway.ecrg.jade.jmsmtp.common.JMSAddress;

import jade.mtp.InChannel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.jms.Queue;
import javax.jms.QueueConnection;

import org.apache.log4j.Category;


public class QLManager {

  private static Category log = Category.getRoot();
  private HashMap listenerMap; // stores the queue listeners

  /**
   * Creates a new QLManager object.
   */
  public QLManager () {

    if (log.isDebugEnabled()) {
      log.debug("QLManager Started");
    }

    listenerMap = new HashMap();
  }

  /**
   * Add a queue listener for a specific queue
   * 
   * @param conn Connection to use
   * @param disp Dispatcher to send the messages to
   * @param jmsTA Address to listen to
   * @throws Exception Error during listener activation
   */
  public void addQL (QueueConnection conn, InChannel.Dispatcher disp, 
                     JMSAddress jmsTA) throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("Adding QL:" + jmsTA.getString());
    }

    // Cast Transport Address to JMS Address
    // Create a new Agent Listener...place in a hastable under the key of its trnsport address!
    try {
      listenerMap.put(jmsTA.getString(), new QueueListener(conn, disp, jmsTA));
    } catch (Exception e) {

      if (log.isDebugEnabled()) {
        log.debug("Error adding the QL to the listenerMap: " + e.toString());
      }

      throw e;
    }

    if (log.isDebugEnabled()) {
      log.debug("QL Added");
    }
  }

  /**
   * Remove a specific queue listener
   * 
   * @param key Key of the queue listener to remove
   * @throws Exception Error during queue removal
   */
  public void removeQL (String key) throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("Removing QL:" + key);
    }

    // Get string from the TA and remove item from hash
    ((QueueListener) listenerMap.get(key)).stop();
    listenerMap.remove(key);
  }

  /**
   * Remove all queue listeners
   * 
   * @throws Exception Error during listener removal
   */
  public void removeAllQL () throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("Removing all QLs");
    }

    // Get all the QLs
    Set keys = listenerMap.keySet();

    // Remove each of the QLs
    for (Iterator i = keys.iterator(); i.hasNext();) {

      try {
        this.removeQL((String) i.next());
      } catch (Exception e) {
        throw e;
      }
    }
  }
}
