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
 * Interface used for JMS propertiry code to create queues
 * 
 * <p>
 * This interface is used to add support for new JMS providers
 * </p>
 */
package ie.nuigalway.ecrg.jade.jmsmtp.common;

import jade.mtp.MTPException;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;


public abstract interface ProviderAdmin {

  /**
   * Method used to get get a queue connection factory on the JMS provider
   * 
   * @param jmsTA Contains the address of the provider to conntect to
   * @return QueueConnectionFactory A connection factory for the JMS Provider
   * @throws MTPException Error during QueueConnectionFactory Activation
   */
  public abstract QueueConnectionFactory getQueueConnectionFactory (JMSAddress jmsTA)
    throws MTPException;

  /**
   * Method used to get or Create the queue contained in the transport address
   * on the JMS provider
   * 
   * @param jmsTA JMS containing the queue to be created
   * @return Queue Queue specified in jmsTA
   * @throws MTPException Error during queue creation
   */
  public abstract Queue getOrCreateQueue (JMSAddress jmsTA)
                                   throws MTPException;
}
