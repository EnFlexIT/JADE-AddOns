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

import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BasicOntology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import org.apache.log4j.Logger;
import com.whitestein.wsig.Configuration;
import com.whitestein.wsig.fipa.SL0Helper;
import com.whitestein.wsig.translator.FIPASL0ToSOAP;


/**
 * @author jna
 *
 * testing agent as a server.
 * A server agent is implemented for testing purpose.
 */
public class TestAgent001 extends Agent {

  private String UNNAMED = "_JADE.UNNAMED";
  private Logger log = Logger.getLogger(TestAgent001.class.getName());
  public static AID myAID = null;

  private SLCodec codec = new SLCodec();

  public static final String SERVICE_PLUS = "plus";
  private int convId = 0;
                                                                                
	
  protected void setup() {
    log.info("A TestAgent001 is starting.");


	getContentManager().registerLanguage( codec );
      getContentManager().registerOntology(
        FIPAManagementOntology.getInstance());


	// add behaviour of the Agent
    this.addBehaviour( new CyclicBehaviour( this ) {
      public void action() {
        ACLMessage msg = myAgent.receive();
        if ( msg != null ) {
          switch ( msg.getPerformative() ) {
            case ACLMessage.REQUEST:
              doFIPARequest( msg );
              break;
            default:
              // other messages are ignored
              break;
          }
          try {
            log.debug("A testAgent001 receives: "
              + SL0Helper.toString(msg) );
          }catch ( Exception e ) {
            log.error(e);
          }

        }else{
          block();
        }
      }
    } );

    // ------------------------------
    // register the agent into the DF


    // prepare a DFAgentDescription
    DFAgentDescription dfad = new DFAgentDescription();
	dfad.setName( this.getAID());
    dfad.addLanguages( codec.getName() );
    dfad.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );
    ServiceDescription sd;
    sd = new ServiceDescription();
    sd.setName( SERVICE_PLUS ); // here is the service name
    sd.addLanguages( codec.getName());
    sd.addProtocols( FIPANames.InteractionProtocol.FIPA_REQUEST );
    sd.addProperties(new Property("WSIG","true"));
    sd.setType(Configuration.WEB_SERVICE);
    // or set properties

    dfad.addServices(sd);


    // send the request for registration
    try {


		DFService.register(this, dfad);
	}catch (Exception e) {
      // something is wrong
      e.printStackTrace();
    }
                                                                              
    // ------------------------------
                                                                              
    log.debug("A TestAgent001 is started.");
  }

	
  protected void takeDown() {
    //deregister itself from the DF 

    try {

		DFService.deregister(this);
	}catch (Exception e) {
      log.error( e );
    }
		
    log.debug("A TestAgent001 is taken down now.");
  }

  /**
   * serves a request
   *
   * @param acl a request
   */
  private void doFIPARequest( ACLMessage acl ) {
    ACLMessage resp = acl.createReply();
    AbsContentElement ac = null;
    AbsObject ao, ao2;
    long sum = 0;
    String str = "";

    // decode the request
    try {
      ac = codec.decode( BasicOntology.getInstance(), acl.getContent() );
    }catch ( CodecException ce ) {
      str = "(error CodecException ( " + ce + " ))";
      SL0Helper.fillAsNotUnderstood( acl, resp, str );
      send(resp);
      return;
    }

    if( null == ac ) {
      str = "(error action null)";
      SL0Helper.fillAsNotUnderstood( acl, resp, str );
      send(resp);
      return;
    }

    if ( ! SL0Vocabulary.ACTION.equalsIgnoreCase( ac.getTypeName()) ) {
      str = "(unknown action_format)";
      SL0Helper.fillAsNotUnderstood( acl, resp, str );
      send(resp);
      return;
    }else{
      // parse the action
      ao = FIPASL0ToSOAP.getActionSlot( ac );
      if ( null == ao ) {
        str = "(unknown action_slot_format)";
        SL0Helper.fillAsNotUnderstood( acl, resp, str );
        send(resp);
        return;
      }

      // check a service name
      String opName = ao.getTypeName();
      if ( SERVICE_PLUS.equalsIgnoreCase(opName) ) {
        // unnamed parameters are expected
        if ( ! FIPASL0ToSOAP.isWithUnnamed(ao) ) {
          str = "(unknown (format " + opName + " ))";
          SL0Helper.fillAsNotUnderstood( acl, resp, str );
          send(resp);
          return;
        }

        // do plus on agruments
        String[] name = ao.getNames();
        for(int i = 0; i < ao.getCount(); i ++ ) {
          // get unnamed slot
          ao2 = ao.getAbsObject( UNNAMED+i );
          try{
            sum += ((AbsPrimitive)ao2).getLong();
          }catch(java.lang.ClassCastException cce) {
            str = "(error (argument_format " + opName + " at " + i + " ))";
            SL0Helper.fillAsNotUnderstood( acl, resp, str );
            send(resp);
            return;
          }
        }
        resp = SL0Helper.createInformResult( acl, "" + sum );
      }else{
        str = "(unknown (service " + opName + " ))";
        SL0Helper.fillAsNotUnderstood( acl, resp, str );
      }
    }

    send(resp);
  }

}
