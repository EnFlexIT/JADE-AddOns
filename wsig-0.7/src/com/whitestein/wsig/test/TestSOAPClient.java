/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is WebService Integration Gateway (WSIG).
 *
 * The Initial Developer of the Original Code is
 * Whitestein Technologies AG.
 * Portions created by the Initial Developer are Copyright (C) 2004, 2005
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Jozef Nagy (jna at whitestein.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */
package com.whitestein.wsig.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;
import org.apache.log4j.Logger;
import org.uddi4j.UDDIException;
import org.uddi4j.client.UDDIProxy;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.OverviewDoc;
import org.uddi4j.datatype.binding.AccessPoint;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.binding.BindingTemplates;
import org.uddi4j.datatype.binding.TModelInstanceDetails;
import org.uddi4j.datatype.binding.TModelInstanceInfo;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.datatype.tmodel.TModel;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.response.ServiceInfo;
import org.uddi4j.response.ServiceInfos;
import org.uddi4j.response.ServiceList;
import org.uddi4j.response.TModelDetail;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.CategoryBag;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.util.KeyedReference;
import org.uddi4j.util.TModelBag;

import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.struct.CalledMessage;
import com.whitestein.wsig.ws.WSEndPoint;

/**
 * @author jna
 *
 * tests a SOAP message sending
 *
 */
public class TestSOAPClient implements Runnable {

  private final static String fipaServiceName = "plus";

  private boolean isRunning =  true;
  private static Logger log =
    Logger.getLogger( TestSOAPClient.class.getName());
  private static MessageFactory mf;
  private static SOAPFactory soapFactory;
  private CalledMessage returnedMessage;

  private UDDIProxy uddiProxy;

  // a message factory setup
  static {
    try {
      mf = MessageFactory.newInstance();
      soapFactory = SOAPFactory.newInstance();
    }catch (SOAPException e) {
      e.printStackTrace();
      log.error(e);
    }
  }

  /**
   * sets up the uddi4j. It starts components required.
   *
   */
  private void setupUDDI4j() {
    // to register into UDDI
    // structures used for a communication with UDDI is retrieved
    Configuration c = Configuration.getInstance();

    synchronized ( c ) {
      // synchronized on main Configuration instance
      // to prevent changes in configuration

      System.setProperty( Configuration.KEY_UDDI4J_LOG_ENABLED,
        c.getUDDI4jLogEnabled());
      System.setProperty( Configuration.KEY_UDDI4J_TRANSPORT_CLASS,
        c.getUDDI4jTransportClass());

      uddiProxy = new UDDIProxy();

      // Select the desired UDDI server node
      try {
        // contact a back end UDDI repository
        uddiProxy.setInquiryURL(c.getQueryManagerURL());
        uddiProxy.setPublishURL(c.getLifeCycleManagerURL());

        // it is possible to contact the gateway
        //uddiProxy.setInquiryURL(uddiQueryManagerURL);
        //uddiProxy.setPublishURL(uddiLifeCycleManagerURL);
      }catch( Exception e ) {
        log.error(e);
      }
    }
  }

  /**
   * finds services wanted
   * @return a list of services
   */
  private ServiceList findServices() {
    ServiceList sl = new ServiceList(); // default is an empty list
    try {
      String businessKey = "";   // all business
      Vector names = new Vector(1);
      names.add( new Name("%WSIG%") );  // substring is WSIG

      CategoryBag cb = new CategoryBag();
      KeyedReference kr = new KeyedReference();
      kr.setTModelKey(TModel.GENERAL_KEYWORDS_TMODEL_KEY); // uddi-org:general_keywords
      kr.setKeyName("fipaServiceName");
      kr.setKeyValue( fipaServiceName );
      cb.add( kr );
      TModelBag tmb = new TModelBag();  //empty
      FindQualifiers fq = new FindQualifiers();  //empty

      sl = uddiProxy.find_service(
        businessKey,
        names,
        cb,
        tmb,
        fq,
        10 );

    } catch ( UDDIException ue ) {
      log.debug( ue );
    } catch ( TransportException te ) {
      log.debug( te );
    }
    return sl;
  }

  /**
   * writes out a list of services into a log
   *
   */
  private void writeToLog( ServiceList list ) {
    ServiceInfo info;
    ServiceInfos infos = list.getServiceInfos();
    String s;
    int k;
    for ( k = 0; k < infos.size(); k ++ ) {
      info = infos.get( k );
      s = info.getDefaultNameString();
      log.debug(" a service found: " + s );
    }
  }

  /**
   * performs a test
   */
  private void test(){
    setupUDDI4j();

    // find Services
    ServiceList sList = findServices();
    if ( log.isDebugEnabled() ) {
      writeToLog( sList );
    }

    ServiceInfo info;
    ServiceInfos infos = sList.getServiceInfos();

    if ( infos.size() < 1 ) {
      log.info(" No service is available.");
      return;
    }

    info = infos.get( 0 );
    ServiceDetail sd = null;
    try {
      sd = uddiProxy.get_serviceDetail( info.getServiceKey() );
    }catch ( UDDIException ue ) {
      log.debug( ue );
    }catch ( TransportException te ) {
      log.debug( te );
    }
    if ( null == sd ) {
      log.info(" No service is available in the 2nd step.");
      return;
    }

    Vector sv = sd.getBusinessServiceVector();
    if ( sv.size() < 1 ) {
      log.info(" No service is available in the 2nd step.");
      return;
    }

    // take the first service
    BusinessService bs = (BusinessService) sv.elementAt( 0 );

    // get an accessPoint
    BindingTemplates bts = bs.getBindingTemplates();
    if ( bts.size() < 1 ) {
      log.info(" No bindingTemplate is available. ");
      return;
    }
    BindingTemplate bt = bts.get(0);
    AccessPoint aPoint = bt.getAccessPoint();
    URL ap = null;
    try {
      log.info(" An accessPoint is " + aPoint.getText()
        + " and type is " + aPoint.getURLType() );
      ap = new URL( aPoint.getText() );
    }catch (MalformedURLException mfe) {
      log.error( mfe );
      return;
    }

    // get TModel, only one is expected
    TModelInstanceDetails tmids = bt.getTModelInstanceDetails();
    if ( tmids.size() < 1 ) {
      log.info(" No TModelInstanceInfo is available. ");
      return;
    }
    TModelInstanceInfo tmii = tmids.get(0);
    String tmk = tmii.getTModelKey();
    TModelDetail tmd = null;
    try {
      tmd = uddiProxy.get_tModelDetail( tmk );
    }catch ( UDDIException ue ) {
      log.debug( ue );
    }catch ( TransportException te ) {
      log.debug( te );
    }

    if ( null == tmd ) {
      log.info(" No TModelDetail is available.");
      return;
    }

    Vector tmdv = tmd.getTModelVector();

    if ( tmdv.size() < 1 ) {
      log.info(" No TModel is available.");
      return;
    }
    TModel tm = (TModel) tmdv.get(0);

    // get wsdl url from TModel, only one is expected
    OverviewDoc ovd = tm.getOverviewDoc();
    if ( null == ovd ) {
      log.info(" No OverviewDoc is available in TModel.");
      return;
    }
    String wsdlURL = ovd.getOverviewURLString();
    if ( null == wsdlURL ) {
      log.info(" OverviewDoc's URL is null.");
      return;
    }
    log.info(" TModel refers to wsdl: " + wsdlURL );

    // get an operation for fipaServiceName
    CategoryBag cb = bs.getCategoryBag();
    KeyedReference kr;
    int k;
    for ( k = 0; k < cb.size(); k ++ ) {
      kr = cb.get( k );

      if ( fipaServiceName.equalsIgnoreCase( kr.getKeyName() )) {

        // it is found, call it
        callOperation( ap, kr.getKeyValue(), wsdlURL );
          // it is better to extract nameSpace from the wsdl in the future
        return;
      }
    }
  }

  /**
   * calls an operation
   *
   * @param accessPoint access point of a WS
   * @param opName an operation's name
   * @param wsdlNS a wsdl name space
   */
  private void callOperation( URL accessPoint, String opName, String wsdlNS ){
    URL serverURL = accessPoint;
    HttpURLConnection c = null;

    SOAPMessage retSOAP = null;
		
    // generate a test's message
    String str;
    int[] values = { 3, 5, 7 };
    str = generatePlus( opName, values, wsdlNS );

    SOAPMessage soap;
    soap = new Message( str, false,
      "application/soap+xml; charset=utf-8", "" );

    // debug to write down
    ByteArrayOutputStream baos;
    try {
      baos = new ByteArrayOutputStream();
      soap.writeTo(baos);
      log.info("A SOAP sent: \n  " + baos.toString());
    } catch (SOAPException e) {
      log.error(e);
    } catch (IOException ioe) {
      log.error(ioe);
    }

    try {
      // send a request
      c = WSEndPoint.sendHTTPRequest( serverURL, soap );
      
      // read a response
      retSOAP = WSEndPoint.receiveHTTPResponse( c );
      
      // debug to write down
      if ( retSOAP != null ) {
        try {
          baos = new ByteArrayOutputStream();
          retSOAP.writeTo(baos);
          log.info("A SOAP received: \n  " + baos.toString());
        } catch (SOAPException e) {
          log.error(e);
        } catch (IOException ioe) {
          log.error(ioe);
        }
      }else {
        log.info("A SOAP received: null.");
      }


      // release resources
      c.disconnect();
  
    }catch (SOAPException se) {
      log.error(se);
    }catch (IOException ioe) {
      log.error(ioe);
    }finally{
      if (c != null) {
        c.disconnect();
      }
      isRunning = false;
      //return;
    }
  
  }

  /**
   * generates a SOAP message.
   *
   * @param op_name a name of a operation
   * @param nums an array of integers as arguments
   * @param wsdlNS a wsdl namespace
   * @return a message in string
   */
  public static String generatePlus( String op_name, int[] nums, String wsdlNS ) {
    String str = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?> " +
      " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">  " +
      "  <soapenv:Body>\r\n" +
      "   <tns:" + op_name + " xmlns:tns=\"" + wsdlNS + "\" >   ";
    for ( int k = 0; k < nums.length; k ++ ) {
      str += "    <tns:BO_Integer>" + nums[k] + "</tns:BO_Integer>\r\n";
    }
    str +=
      "	  </tns:" + op_name + ">  " +
      "	 </soapenv:Body>   " +
      "	</soapenv:Envelope>\r\n";
    return str;
  }

  /**
   * implements a runnable interface
   */
  public void run() {
    log.info(" Test SOAP Client starts. ");

    while ( isRunning ) {
      test();
    }
    log.info(" Test SOAP Client ends. ");
  }
  
  public static void main(String[] args) {
    new Thread( new TestSOAPClient()).start();
  }
}
