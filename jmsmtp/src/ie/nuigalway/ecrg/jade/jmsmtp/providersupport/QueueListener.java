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
 * Agent Queue Listener
 * 
 * <p>
 * Once started a queue listener connects to a queue listening for message
 * sent to it when it receives a message it decodes it and passes it to the
 * platform
 * </p>
 */
package ie.nuigalway.ecrg.jade.jmsmtp.providersupport;

import ie.nuigalway.ecrg.jade.jmsmtp.common.JMSAddress;
import ie.nuigalway.ecrg.jade.jmsmtp.common.JmsMtpConfig;
import ie.nuigalway.ecrg.jade.jmsmtp.common.ProviderAdmin;

import jade.domain.FIPAAgentManagement.Envelope;

import jade.mtp.InChannel;
import jade.mtp.MTPException;

import java.util.Set;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

import org.apache.log4j.Category;


public class QueueListener implements javax.jms.MessageListener,
                                      javax.jms.ExceptionListener {

  private static Category log = Category.getRoot();
  private InChannel.Dispatcher dispatcher; // dispatcher used to pass messages to the platform
  private JMSAddress jmsTA;
  private FipaXMLUtil xmlUtil;
  private MapMessageUtil mapUtil;
  private QueueConnection conn;
  private QueueSession session;
  private Queue que;
  private ProviderAdmin pAdmin;

  /**
   * Creates a new QueueListener object.
   * 
   * @param conn Connection to use
   * @param dispatcher Dispatcher to send the messages to
   * @param jmsTA Address to listen to
   * @throws Exception Error during listener activation
   */
  public QueueListener (QueueConnection conn, InChannel.Dispatcher dispatcher, 
                        JMSAddress jmsTA) throws Exception {

    if (log.isDebugEnabled()) {
      log.debug("New QL Setup");
    }

    this.conn = conn;
    this.dispatcher = dispatcher;
    this.jmsTA = jmsTA;
    xmlUtil = new FipaXMLUtil();
    mapUtil = new MapMessageUtil();

    // Get the QueueAdmin
    pAdmin = getProviderAdmin(jmsTA.getProviderType());

    // Start the listener
    try {
      this.setupConnection();
    } catch (Exception e) {
      log.error("Error in QL connection setup:" + e.toString());
      throw e;
    }

    if (log.isDebugEnabled()) {
      log.debug("QL Setup Complete");
    }
  }

  /**
   * Method is executed in order to create a connection to a JMS server and
   * listen to
   * 
   * @exception JMSException
   * @exception MTPException
   */
  private void setupConnection () throws JMSException, MTPException {

    if (log.isDebugEnabled()) {
      log.debug("QL setup");
    }

    if (log.isDebugEnabled()) {
      log.debug("Looking up queue");
    }

    try {
      que = pAdmin.getOrCreateQueue(jmsTA);
    } catch (Exception e) {
      log.error("Failed in connection to queue:" + e.toString());
      throw new MTPException("Failed in connection to queue", e);
    }

    if (log.isDebugEnabled()) {
      log.debug("Set listener");
    }

    session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);

    QueueReceiver recv = session.createReceiver(que);
    recv.setMessageListener(this);

    if (log.isDebugEnabled()) {
      log.debug("Installing Exception Listener");
    }

    conn.setExceptionListener((javax.jms.ExceptionListener) this);

    if (log.isDebugEnabled()) {
      log.debug("Setup Complete");
    }
  }

  /**
   * Method is executed when a message is receivd from the agents queue
   * 
   * @param msg JMS Message received
   */
  public void onMessage (Message msg) {

    if (log.isDebugEnabled()) {
      log.debug("Incoming Message");
    }

    if (log.isDebugEnabled()) {
      log.debug("Deconstruct the message");
    }

    StringBuffer payload = new StringBuffer();
    Envelope env = new Envelope();

    if (msg instanceof TextMessage) {
      log.debug("Text Message");

      TextMessage tm = (TextMessage) msg;

      try {
        env = xmlUtil.decode(tm.getText(), payload);
      } catch (Exception jmse) {
        log.error("Error in JMS TextMessage Extraction: " + jmse.toString());
      }
    } else if (msg instanceof MapMessage) {

      if (log.isDebugEnabled()) {
        log.debug("Map Message");
      }

      MapMessage mm = (MapMessage) msg;

      try {
        env = mapUtil.decode(mm, payload);
      } catch (Exception jmse) {
        log.error("Error in JMS MapMessage Extraction: " + jmse.toString());
      }
    }

    if (log.isDebugEnabled()) {
      log.debug("Message decode");
    }

    dispatcher.dispatchMessage(env, payload.toString().getBytes());
  }

  /**
   * Method is executed when an execption is thrown by the JMS Provider
   * Attempts to reconnect to the provider
   * 
   * @param jmse JMS Exception thrown
   */
  public void onException (javax.jms.JMSException jmse) {

    if (log.isDebugEnabled()) {
      log.debug("Exception Thrown: " + jmse.toString());
      log.debug("Attempt to Re-establish the connection to the JMS Provider");
    }

    conn = null;

    // Reestablish the connection
    while (conn == null) {

      try {

        if (log.isDebugEnabled()) {
          log.debug("Sleeping for 10 Seconds");
        }

        Thread.sleep(10000);
      } catch (Exception e) {
        ;
      }

      try {
        setupConnection();
      } catch (Exception e) {

        continue;
      }
    }
  }

  /**
   * Method is executed to disconnect from a JMS server
   * 
   * @throws Exception Error in closeing JMS Connection
   */
  public void stop () throws Exception {
    session.close();
  }

  /**
   * Get the Admin interface for a specific JMS provider
   * 
   * @param providerType Providers interface to return
   * @return JMS providers admin interface
   * @throws Exception Error in creating the provider interface
   */
  public ProviderAdmin getProviderAdmin (String providerType)
                                  throws Exception {

    ProviderAdmin pa = null;
    String packageName = "ie.nuigalway.ecrg.jade.jmsmtp.";
    String classname = JmsMtpConfig.getProperty(
                             packageName + "providerType." + providerType + 
                             ".providerAdmin.className", "").trim();

    if ((classname == null) || (classname.equals(""))) {
      log.debug(
            "Error in Config File: No providerAdmin.className set for " + 
            providerType + " Format");
      throw new Exception("Error in Config File: No providerAdmin.className set for " + 
                          providerType + " Format");
    }

    try {
      pa = (ProviderAdmin) Class.forName(classname).newInstance();
    } catch (Exception e) {
      log.error(
            "Error in loading class:" + classname + 
            " is it in the classpath ?");
      throw new Exception("Error in loading class:" + classname + 
                          " is it in the classpath ?");
    }

    return pa;
  }
} // End of QueueListener class
