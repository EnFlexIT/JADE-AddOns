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
 * JMS implementation of a Transport Address
 * 
 * <p>
 * Used to represent a transport address in the JMS-MTP
 * </p>
 * 
 */
package ie.nuigalway.ecrg.jade.jmsmtp.common;

import jade.mtp.MTPException;
import jade.mtp.TransportAddress;

import org.apache.log4j.Category;


public class JMSAddress implements TransportAddress {

  private String protocol; // protocols supported by this address
  private String providerType; // format of the JMS Provider
  private String msgType; // Format of message to be used on this address
  private String msgPersistence; // Message Persistence Setting for this Address
  private String username; // Username used to access queue
  private String password; // Password used to access queue
  private String brokerURL;
  private String queueName;
  private static Category log = Category.getRoot();

  // Address makeup jms:provider_type:messge_type:persistence:username:password:hostname:port/queuename
  // Example Address jms:sonicmq:xml:persistent:edcurry:edspass:message.broker.foobar.com:1099/ade/jade-platform.foobar.com:1098

  /**
   * Creates a new JMSAddress object.
   * 
   * @param addy A specific Address
   * @throws MTPException Malformed Address
   */
  public JMSAddress (String addy) throws MTPException {

    if (log.isDebugEnabled()) {
      log.debug("Creating specific TA:" + addy);
    }

    try {

      if (! addy.startsWith("jms:")) {
        throw new MTPException("Missing 'jms': " + addy);
      }

      protocol = "jms";

      // Provider Type: eg.'jossmq'
      int endOfProviderType = addy.indexOf(':', 4);
      providerType = addy.substring(4, endOfProviderType);

      if (providerType.equals("")) {
        throw new MTPException("Missing provider format");
      }

      if (log.isDebugEnabled()) {
        log.debug("Provider Type: " + providerType);
      }

      // Message Type: eg. 'xml'
      int endOfMsgType = addy.indexOf(':', endOfProviderType + 1);
      msgType = addy.substring(endOfProviderType + 1, endOfMsgType);

      if (msgType.equals("") | 
          ((! msgType.equals(JmsMtpConfig.MSG_XML)) && 
            (! msgType.equals(JmsMtpConfig.MSG_MAP)))) {
        throw new MTPException("Missing  or invalid message type: " + 
                               msgType);
      }

      if (log.isDebugEnabled()) {
        log.debug("Message  Type: " + msgType);
      }

      // Message Persistence: eg. 'persistent'
      int endOfMsgPer = addy.indexOf(':', endOfMsgType + 1);
      msgPersistence = addy.substring(endOfMsgType + 1, endOfMsgPer);

      if (msgPersistence.equals("") | 
          ((! msgPersistence.equals(JmsMtpConfig.MSG_PERSISTENT)) && 
            (! msgPersistence.equals(JmsMtpConfig.MSG_NON_PERSISTENT)))) {
        throw new MTPException("Missing  or invalid message persistence: " + 
                               msgPersistence);
      }

      if (log.isDebugEnabled()) {
        log.debug("Message  Persistence: " + msgPersistence);
      }

      // Username '' can be blank
      int endOfUsername = addy.indexOf(':', endOfMsgPer + 1);
      username = addy.substring(endOfMsgPer + 1, endOfUsername);

      if (log.isDebugEnabled()) {
        log.debug("Username: " + username);
      }

      // Password '' can be blank
      int endOfPassword = addy.indexOf(':', endOfUsername + 1);
      password = addy.substring(endOfUsername + 1, endOfPassword);

      if (log.isDebugEnabled()) {
        log.debug("Password: " + password);
      }

      // Broker URL: '127.0.0.1:1099'
      int brokerUrlEnd = addy.indexOf('/', endOfPassword + 1);
      brokerURL = addy.substring(endOfPassword + 1, brokerUrlEnd);

      if (log.isDebugEnabled()) {
        log.debug("Broker URL: " + brokerURL);
      }

      // Queue Name : 'queue/jade/159.134.244.58'
      queueName = addy.substring(brokerUrlEnd + 1);

      if (log.isDebugEnabled()) {
        log.debug("Queue Name: " + queueName);
      }
    } catch (Exception e) {
      log.error("Error in supplied bTA, Default setting assigned");
      throw new MTPException("Invalid JMS Address': " + addy, e);
    }
  }

  /**
   * Creates a new JMSAddress object.
   * 
   * @throws MTPException Error with Address Activation
   */
  public JMSAddress () throws MTPException {

    if (log.isDebugEnabled()) {

      // Create a default address using the default address
      log.debug("Create TA with Defaults");
    }

    protocol = "jms";
    providerType = JmsMtpConfig.DEFAULT_PROVIDER_TYPE;
    msgType = JmsMtpConfig.DEFAULT_MSG_TYPE;
    msgPersistence = JmsMtpConfig.DEFAULT_MSG_PERSISTENCE;
    username = JmsMtpConfig.DEFAULT_USERNAME;
    password = JmsMtpConfig.DEFAULT_PASSWORD;
    brokerURL = JmsMtpConfig.DEFAULT_BROKER_URL;
    queueName = JmsMtpConfig.DEFAULT_QUEUE_NAME;
  }

  /**
   * Return a String repsentation of this JMSAddress
   * 
   * @return String representation of a JMSAddress
   */
  public String getString () {

    if (log.isDebugEnabled()) {
      log.debug("Get String of TA");
    }

    return protocol + ":" + providerType + ":" + msgType + ":" + 
           msgPersistence + ":" + username + ":" + password + ":" + 
           brokerURL + "/" + queueName;
  }

  /**
   * Returns the JMS Provider used by this address
   * 
   * @return JMS Provider for this Address
   */
  public String getProviderType () {

    return providerType;
  }

  /**
   * Message Type used by this address XML or MapMessage
   * 
   * @return Message Type used by this Address
   */
  public String getMsgType () {

    return msgType;
  }

  /**
   * Message Persistence setting of this address
   * 
   * @return Setting of this Address
   */
  public String getMsgPersistence () {

    return msgPersistence;
  }

  /**
   * Username to access the boker/queue (Optional)
   * 
   * @return Usename of this Address
   */
  public String getUsername () {

    return username;
  }

  /**
   * Password used to access broker/queue (Optional)
   * 
   * @return Password of this Address
   */
  public String getPassword () {

    return password;
  }

  /**
   * URL Connection information of Broker
   * 
   * @return BrokerURL for this Address
   */
  public String getBrokerURL () {

    return brokerURL;
  }

  /**
   * Queue releated to this Address
   * 
   * @return queue to listen or send to
   */
  public String getQueueName () {

    return queueName;
  }

  /**
   * Protocol used by this Address
   * 
   * @return protocol of this address
   */
  public String getProto () {

    return protocol;
  }

  /**
   * Hostname of the broker for this address
   * 
   * @return Brokers host name
   */
  public String getHost () {

    return brokerURL.substring(0, brokerURL.length() - 5);
  }

  /**
   * Not used in Address
   * 
   * @return null
   */
  public String getPort () {

    return "";
  }

  /**
   * Not used in Address
   * 
   * @return null
   */
  public String getFile () {

    return "";
  }

  /**
   * Not used in Address
   * 
   * @return null
   */
  public String getAnchor () {

    return "";
  }
}
