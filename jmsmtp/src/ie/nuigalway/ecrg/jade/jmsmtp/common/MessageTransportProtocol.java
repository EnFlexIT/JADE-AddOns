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
 * Interface point for JMS-MTP and JADE platform
 * 
 * <p>
 * Handles the send and receving of JMS messages to and from the JADE platform
 * </p>
 * 
 * @version 0.5 10 Mar 2003
 * @author Edward Curry - NUI, Galway
 */ 
package ie.nuigalway.ecrg.jade.jmsmtp.common;

import jade.domain.FIPAAgentManagement.Envelope;

import jade.mtp.InChannel;
import jade.mtp.MTP;
import jade.mtp.MTPException;
import jade.mtp.TransportAddress;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;


public class MessageTransportProtocol implements MTP {

  private static Category log;
  private static HashMap providerManagers; // HashMap of connections to JMS broker

  static {

    // Load in config info
    String resource = "/log4j-mtp.properties";
    URL configFileResource = MessageTransportProtocol.class.getResource(
                                   resource);
    PropertyConfigurator.configure(configFileResource);

    JmsMtpConfig temp = new JmsMtpConfig();
    log = Category.getRoot();

    if (log.isDebugEnabled()) {
      log.debug("Init of JMS-MTP");
    }

    if (log.isDebugEnabled()) {
      log.debug("Starting QL Manager");
    }

    providerManagers = new HashMap();
  }

  /**
   * Activate a default Address
   * 
   * @param disp Passed messages to the platform
   * @return Trnasport Address for the address activated
   * @throws MTPException Error during address activation
   */
  public TransportAddress activate (InChannel.Dispatcher disp, jade.core.Profile p)
                             throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Default Activate Called");
    }

    if (log.isDebugEnabled()) {
      log.debug("Create a Transport Address");
    }

    JMSAddress jmsTA = new JMSAddress();

    try {

      if (log.isDebugEnabled()) {
        log.debug("Create a Default QL");
      }

      this.activate(disp, jmsTA, p);
    } catch (Exception e) {
      log.error("Error in Addition:" + e.toString());
      throw new MTPException("Error during default activation: ", e);
    }

    if (log.isDebugEnabled()) {
      log.debug("Returning TA: " + jmsTA.getString());
    }

    return (TransportAddress) jmsTA;
  }

  /**
   * Activate a specific TransportAddress
   * 
   * @param disp Used to pass messages to the platform
   * @param ta Address to activate
   * @throws MTPException Error during address activation
   */
  public void activate (InChannel.Dispatcher disp, TransportAddress ta, jade.core.Profile p)
                 throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Activate on a specific Transport Address");
    }

    try {

      JMSAddress jmsTA = (JMSAddress) ta;

      if (log.isDebugEnabled()) {
        log.debug("Create a specific QL: " + jmsTA.getString());
      }

      getProviderManager(jmsTA).activate(disp, jmsTA);
    } catch (Exception e) {
      throw new MTPException("Error during address specific activation: ", e);
    }
  }

  /**
   * Deactivate a specific address
   * 
   * @param ta Address to deactivate
   * @throws MTPException Error during address deactivation
   */
  public void deactivate (TransportAddress ta) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Deactivate specific TA");
    }

    try {

      JMSAddress jmsTA = (JMSAddress) ta;

      if (log.isDebugEnabled()) {
        log.debug("Remove QL for ta");
      }

      getProviderManager(jmsTA).deactivate(jmsTA);
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

    this.closeProviderManagers();

    if (log.isDebugEnabled()) {
      log.debug("Remove all QLs");
    }

    try {
      this.closeProviderManagers();
    } catch (Exception e) {
      throw new MTPException("Error in deactivating MTP: ", e);
    }
  }

  /**
   * Deliver a message to a jmsTA
   * 
   * @param addr Address to deliver too
   * @param env Envelope of message
   * @param payload Message payload
   * @throws MTPException Error during message send
   */
  public void deliver (String addr, Envelope env, byte[] payload)
                throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Deliver a Message to an address");
    }

    try {

      if (log.isDebugEnabled()) {

        log.debug("Create the TA");
      }

      JMSAddress jmsTA = new JMSAddress(addr);
      getProviderManager(jmsTA).deliver(jmsTA, env, payload);
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
   * @throws Exception Error during manager creation
   */
  public JmsProviderManager getProviderManager (JMSAddress jmsTA)
                                         throws Exception {

    // check to see if the Queue connection has been connected
    if (! providerManagers.containsKey(jmsTA.getProviderType())) {
      setupProviderManager(jmsTA);
    }

    return (JmsProviderManager) providerManagers.get(jmsTA.getProviderType());
  }

  /**
   * Create a QueueConnection to a specified broker and place it in the hashmap
   * 
   * @param jmsTA Contains details of the broker
   * @throws Exception Error during provider setup
   */
  private void setupProviderManager (JMSAddress jmsTA) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Creating a connection to broker: " + 
                jmsTA.getProviderType());
    }

    try {

      ClassLoader urlCl = URLClassLoader.newInstance(JmsMtpConfig.getProviderLib(jmsTA.getProviderType()), 
                                                     this.getClass().getClassLoader());

      JmsProviderManager pm = (JmsProviderManager) Class.forName(
                                                         "ie.nuigalway.ecrg.jade.jmsmtp.providersupport.ProviderManager", 
                                                         false, urlCl).newInstance();

      if (log.isDebugEnabled()) {
        log.debug("Placing the connection into the hashmap");
      }

      providerManagers.put(jmsTA.getProviderType(), pm);
    } catch (Exception e) {
      log.error("Failed to create connection to queue:" + e.toString());
      throw new MTPException("Failed to create connection to queue: " + 
                          jmsTA.toString(), e);
    }
  }

  /**
   * Close all connections in the hashmap
   */
  private void closeProviderManagers () {

    if (log.isDebugEnabled()) {
      log.debug("Close all connections in the hashmap");
    }

    Set pms = providerManagers.entrySet();

    for (Iterator i = pms.iterator(); i.hasNext();) {

      JmsProviderManager temp = (JmsProviderManager) i.next();

      try {
        temp.deactivate();
      } catch (Exception e) {

        // Ignore the closing errors
      }
    }

    providerManagers.clear();
  }

  /**
   * Converts a string to a JMS Transport address
   * 
   * @param rep Contains the address as a string
   * @return TransportAddress The address as a JMS Transport Address
   * @throws MTPException Error in Address conversion
   */
  public TransportAddress strToAddr (String rep) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Convert String to TA");
    }

    return new JMSAddress(rep);
  }

  /**
   * Given a TransportAddress convert it to a string
   * 
   * @param ta The Address as a TransportAddress object
   * @return String Address as a string
   * @throws MTPException Error in Address conversion
   */
  public String addrToStr (TransportAddress ta) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Convert TA to String");
    }

    try {

      JMSAddress jmsTA = (JMSAddress) ta;

      return jmsTA.getString();
    } catch (ClassCastException cce) {
      cce.printStackTrace();
      throw new MTPException("Address mismatch: this is not a valid JMS address.");
    }
  }

  /**
   * Return the name of this MTP
   * 
   * @return MTP Name
   */
  public String getName () {

    return "jms";
  }

  /**
   * Get prtocols supported by this MTP
   * 
   * @return The Protocols supported by this MTP
   */
  public String[] getSupportedProtocols () {

    return new String[] { "jms" };
  }
}
