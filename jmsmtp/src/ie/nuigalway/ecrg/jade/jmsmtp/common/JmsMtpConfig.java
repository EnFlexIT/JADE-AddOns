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
 * Reads the properties file and control the configuration for the JMS-MTP
 * 
 * <p>
 * Used to configure the JMS-MTP and store setting for the plugable Provider
 * admin objects
 * </p>
 */
package ie.nuigalway.ecrg.jade.jmsmtp.common;

import java.io.File;

import java.lang.Exception;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Properties;

import org.apache.log4j.Category;


public class JmsMtpConfig {

  private static Properties props;

  // Default Setting read in from the Configuration file
  public static String DEFAULT_MSG_PERSISTENCE;
  public static String DEFAULT_MSG_TYPE;
  public static String DEFAULT_PROVIDER_TYPE;
  public static String DEFAULT_BROKER_URL;
  public static String DEFAULT_QUEUE_NAME;
  public static String DEFAULT_USERNAME;
  public static String DEFAULT_PASSWORD;

  // Constants used within the JMS-MTP
  public final static String MSG_XML = "xml";
  public final static String MSG_MAP = "map";
  public final static String MSG_PERSISTENT = "persistent";
  public final static String MSG_NON_PERSISTENT = "non_persistent";
  private static Category log = Category.getRoot();

  /** stores the packagename of use with keys in the propertyfile */
  private static String packageName = "ie.nuigalway.ecrg.jade.jmsmtp.";

  /**
   * Constructor which loads in the configuration file
   */
  public JmsMtpConfig () {

    try {

      String resource = "/jms-mtp.properties";
      props = new Properties();
      props.load(JmsMtpConfig.class.getResourceAsStream(resource));
    } catch (Exception e) {
      log.error("Error Reading in Config File :" + e.toString());
    }

    // default providerFormat setting
    DEFAULT_PROVIDER_TYPE = props.getProperty(
                                  packageName + "default.providerType", 
                                  "jbossmq").trim();

    // default brokerURL setting
    DEFAULT_BROKER_URL = props.getProperty(packageName + 
                                           "default.brokerURL", 
                                           "127.0.0.1:1099").trim();

    // default queueName setting
    DEFAULT_QUEUE_NAME = props.getProperty(packageName + 
                                           "default.queueName", 
                                           "jade/127.0.0.1:1098").trim();

    // default username setting
    DEFAULT_USERNAME = props.getProperty(packageName + "default.username", "").trim();

    // default password setting
    DEFAULT_PASSWORD = props.getProperty(packageName + "default.password", "").trim();

    // default Message Persistence setting
    String msgPer = props.getProperty(packageName + "messagePersistence", 
                                      MSG_PERSISTENT).trim();
    DEFAULT_MSG_PERSISTENCE = msgPer;

    if ((! msgPer.equals(MSG_NON_PERSISTENT)) && 
        (! msgPer.equals(MSG_PERSISTENT))) {
      DEFAULT_MSG_PERSISTENCE = MSG_PERSISTENT;
    }

    // default Message Type setting
    String msgType = props.getProperty(packageName + "messageType", MSG_XML).trim();
    DEFAULT_MSG_TYPE = msgType;

    if ((! msgType.equals(MSG_XML)) && (! msgType.equals(MSG_MAP))) {
      DEFAULT_MSG_TYPE = MSG_XML;
    }
  }

  /**
   * Reads a given directory and returns the contents as a URL []
   * 
   * @param providerType JMS Provider Type to learn
   * @return A URL [] of the contents of the directory
   * @throws Exception Error reading in Librarys
   */
  public static URL[] getProviderLib (String providerType) throws Exception {

    String lib = props.getProperty(
                       packageName + "providerType." + providerType + 
                       ".lib").trim();

    if ((lib == null) || (lib.equals(""))) {
      log.debug(
            "Error in Config File: No lib set for " + providerType + 
            " Format");
      throw new Exception("Error in Config File: No lib set for " + 
                          providerType + " Format");
    }

    try {

      return getDirList(lib);
    } catch (Exception e) {
      log.error(
            "Error in loading library :" + lib + " for provider :" + 
            providerType + " is the path correct ?");
      throw new Exception("Error in loading library :" + lib + 
                          " for provider :" + providerType + 
                          " is the path correct ?" + e.toString());
    }
  }

  /**
   * Reads a given directory and returns the contents as a URL []
   * 
   * @param lib Location of Directory to Read in
   * @return A URL [] of the contents of the directory
   * @throws Exception Specified location was not a directory
   */
  private static URL[] getDirList (String lib) throws Exception {

    File libDir = new File(lib);
    File[] libFileList = {  };
    URL[] libURLList;

    if (libDir.isDirectory()) {
      log.debug("Its a Dir");
      libFileList = libDir.listFiles();
    } else {
      log.debug("Its ! a Dir");
      throw new Exception("Location: " + lib + " is not a directory");
    }

    libURLList = new URL[libFileList.length];

    for (int ii = 0; ii < libFileList.length; ii++) {

      try {

        if (log.isDebugEnabled()) {
          log.debug("copying : " + libFileList[ii].toString());
        }

        libURLList[ii] = libFileList[ii].toURL();
      } catch (Exception e) {
        log.error("Error loading the following jar file:" + e.toString());
      }
    }

    return libURLList;
  }

  /**
   * Given a key return its value from the configfile
   * 
   * @param key Key to search for in the prop file
   * @param defaultValue Default value to use if not found
   * @return Value from propfile or default value
   */
  public static String getProperty (String key, String defaultValue) {

    return props.getProperty(key, defaultValue).trim();
  }
}
