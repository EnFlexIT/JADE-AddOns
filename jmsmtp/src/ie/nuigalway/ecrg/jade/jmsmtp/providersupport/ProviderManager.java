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
 * Interface point for JMS-MTP and MTP Booter class
 * 
 * <p>
 * Handles the send and receving of JMS messages to and from the JADE platform
 * </p>
 * 
 */
package ie.nuigalway.ecrg.jade.jmsmtp.providersupport;

import ie.nuigalway.ecrg.jade.jmsmtp.common.JMSAddress;
import ie.nuigalway.ecrg.jade.jmsmtp.common.JmsMtpConfig;
import ie.nuigalway.ecrg.jade.jmsmtp.common.JmsProviderManager;
import ie.nuigalway.ecrg.jade.jmsmtp.common.ProviderAdmin;

import jade.domain.FIPAAgentManagement.Envelope;

import jade.mtp.InChannel;
import jade.mtp.MTP;
import jade.mtp.MTPException;

import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.jms.DeliveryMode;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

import org.apache.log4j.Category;


public class ProviderManager implements JmsProviderManager {

  private static Category log = Category.getRoot();
  private QLManager qlManager; // Use to create queue listeners
  private FipaXMLUtil xmlUtil; // Utility used for FIPA XML messages
  private MapMessageUtil mapUtil; // Utility used for JMS MapMessages
  public ProviderAdmin providerAdmin;
  private HashMap brokerConnections; // HashMap of connections to JMS broker

  /**
   * Creates a new ProviderManager object.
   */
  public ProviderManager () {

    xmlUtil = new FipaXMLUtil();
    mapUtil = new MapMessageUtil();
    brokerConnections = new HashMap();
    qlManager = new QLManager();

  }

  /**
   * Activate a specific TransportAddress
   * 
   * @param disp Used to pass messages to the platform
   * @param jmsTA Address to activate
   * @throws MTPException Error during address activation
   */
  public void activate (InChannel.Dispatcher disp, JMSAddress jmsTA)
                 throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Activate on a specific Transport Address");
    }

    try {

      if (log.isDebugEnabled()) {
        log.debug("Create a specific QL: " + jmsTA.getString());
      }

      qlManager.addQL(this.getBrokerConnection(jmsTA), disp, jmsTA);
    } catch (Exception e) {
      throw new MTPException("Error during address specific activation: ", e);
    }
  }

  /**
   * Deactivate a specific address
   * 
   * @param jmsTA Address to deactivate
   * @throws MTPException Error during address deactivation
   */
  public void deactivate (JMSAddress jmsTA) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Deactivate specific TA");
    }

    try {

      if (log.isDebugEnabled()) {
        log.debug("Remove QL for ta");
      }

      qlManager.removeQL(jmsTA.getString());
    } catch (Exception e) {
      throw new MTPException("Error deactivating Transport Address: ", e);
    }
  }

  /**
   * Deactivate the MTP
   * 
   * @throws MTPException Error with MTP deactivation
   */
  public void deactivate () throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Remove all Connections to JMS Providers");
    }

    if (log.isDebugEnabled()) {
      log.debug("Remove all QLs");
    }

    try {
      this.closeBrokerConnections();
      qlManager.removeAllQL();
    } catch (Exception e) {
      throw new MTPException("Error in deactivating MTP: ", e);
    }
  }

  /**
   * Deliver a message to a jmsTA
   * 
   * @param jmsTA Address to deliver too
   * @param env Envelope of message
   * @param payload Message payload
   * @throws MTPException Error during message send
   */
  public void deliver (JMSAddress jmsTA, Envelope env, byte[] payload)
                throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Deliver a Message to an address");
    }

    try {

      if (log.isDebugEnabled()) {
        log.debug("Create connection to the JMS Server");
      }

      QueueConnection conn = this.getBrokerConnection(jmsTA);
      QueueSession session = conn.createQueueSession(false, 
                                                     QueueSession.AUTO_ACKNOWLEDGE);
      Queue que = session.createQueue(jmsTA.getQueueName());
      QueueSender send = session.createSender(que);

      if (log.isDebugEnabled()) {
        log.debug("Setting Persistence");
      }

      if (jmsTA.getMsgPersistence().equals(JmsMtpConfig.MSG_PERSISTENT)) {
        send.setDeliveryMode(DeliveryMode.PERSISTENT);
      } else {
        send.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
      }

      if (log.isDebugEnabled()) {
        log.debug("Create the Message");
      }

      Message message;

      if (jmsTA.getMsgType().equals(JmsMtpConfig.MSG_XML)) {
        log.debug("Build FIPA XML envelope");
        message = session.createTextMessage(xmlUtil.encode(env, payload));
      } else {
        log.debug("Build MapMessage");
        message = session.createMapMessage();
        mapUtil.encode((MapMessage) message, env, payload);
      }

      if (log.isDebugEnabled()) {
        log.debug("Send the Message");
      }

      send.send(message);
      send.close();
      session.close();
    } catch (ClassCastException cce) {
      log.error("Invaild JMS Address");
      throw new MTPException("Address mismatch: this is not a valid JMS address.", 
                             cce);
    }
     catch (Exception e) {
      log.error("Error in sending: " + e.toString());
      throw new MTPException("Error sending the Message: ", e);
    }
  }

  /**
   * Used to create a new broker connection or to retrive one from the hashmap
   * 
   * @param jmsTA Address of the Broker
   * @return QueueConnection A QueueConnection object for the broker in jmsTA
   * @throws Exception Error During Connection Activation
   */
  public QueueConnection getBrokerConnection (JMSAddress jmsTA)
                                       throws MTPException {

    // check to see if the Queue connection has been connected
    if (! brokerConnections.containsKey(jmsTA.getBrokerURL())) {
      setupBrokerConnection(jmsTA);
    }

    return (QueueConnection) brokerConnections.get(jmsTA.getBrokerURL());
  }

  /**
   * Create a QueueConnection to a specified broker and place it in the hashmap
   * 
   * @param jmsTA Contains details of the broker
   * @throws Exception Error during connection setup
   */
  private void setupBrokerConnection (JMSAddress jmsTA) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug(
            "Creating a connection to broker: " + jmsTA.getProviderType() + 
            jmsTA.getBrokerURL());
    }

    try {

      // TODO: DIRTY HACK  FIXME
      if (providerAdmin == null) {
        providerAdmin = getProviderAdmin(jmsTA.getProviderType());
      }

      QueueConnectionFactory qcf = providerAdmin.getQueueConnectionFactory(
                                         jmsTA);
      QueueConnection conn;

      if (jmsTA.getUsername().equals("")) {
        conn = qcf.createQueueConnection();
      } else {
        conn = qcf.createQueueConnection(jmsTA.getUsername(), 
                                         jmsTA.getPassword());
      }

      if (log.isDebugEnabled()) {
        log.debug("Start the new broker connection");
      }

      conn.start();

      if (log.isDebugEnabled()) {
        log.debug("Placing the connection into the hashmap");
      }

      brokerConnections.put(jmsTA.getBrokerURL(), conn);
    } catch (Exception e) {
      log.error("Failed to create connection to queue:" + e.toString());
      throw new MTPException("Failed to create connection to queue: " + 
                          jmsTA.toString(), e);
    }
  }

  /**
   * Close all connections in the hashmap
   */
  private void closeBrokerConnections () {

    if (log.isDebugEnabled()) {
      log.debug("Close all connections in the hashmap");
    }

    Set conns = brokerConnections.entrySet();

    for (Iterator i = conns.iterator(); i.hasNext();) {

      QueueConnection temp = (QueueConnection) i.next();

      try {
        temp.stop();
        temp.close();
      } catch (Exception e) {

        // Ignore the closing errors
      }
    }

    brokerConnections.clear();
  }

  /**
   * Get the Admin interface for a specific JMS provider
   * 
   * @param providerType Providers interface to return
   * @return JMS providers admin interface
   * @throws Exception Error in creating the provider interface
   */
  public ProviderAdmin getProviderAdmin (String providerType)
                                  throws MTPException {

    ProviderAdmin pa = null;
    String packageName = "ie.nuigalway.ecrg.jade.jmsmtp.";
    String classname = JmsMtpConfig.getProperty(
                             packageName + "providerType." + providerType + 
                             ".providerAdmin.className", "").trim();

    if ((classname == null) || (classname.equals(""))) {
      log.debug(
            "Error in Config File: No providerAdmin.className set for " + 
            providerType + " Format");
      throw new MTPException("Error in Config File: No providerAdmin.className set for " + 
                          providerType + " Format");
    }

    try {
      pa = (ProviderAdmin) Class.forName(classname).newInstance();
    } catch (Exception e) {
      log.error(
            "Error in loading class:" + classname + 
            " is it in the classpath ?");
      throw new MTPException("Error in loading class:" + classname + 
                          " is it in the classpath ?");
    }

    return pa;
  }
}
