/*****************************************************************
"DistilledStateChartBehaviour" is a work based on the library "HSMBehaviour"
(authors: G. Caire, R. Delucchi, M. Griss, R. Kessler, B. Remick).
Changed files: "HSMBehaviour.java", "HSMEvent.java", "HSMPerformativeTransition.java",
"HSMTemplateTransition.java", "HSMTransition.java".
Last change date: 18/06/2010
Copyright (C) 2010 G. Fortino, F. Rango

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation;
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
*****************************************************************/

package jade.core.behaviours;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * <p>DistilledStateChartEvent: extension to ACLMessage for other types of events.</p>
 * <p>We extend ACLMessage with other performatives to indicate other
 *    types of events. The user is free to utilize any of the fields in the
 *    ACLMessage to store information useful to the event. For example, timeouts
 *    typically have a name associated with them. This could be simply stored in
 *    the content field.
 *    <p> An Event has the following important fields:
 *    <ul>
 *    <li> type the type of event (there are static variables that contain the strings
 *         that are used for the type).
 *    <li> id a string that is used to identify a particular instance of an event (for example,
 *         timer1, timer2, defaulttimer, etc.).
 *    <li> content another string that can contain anything relevant to the event.
 *    </ul>
 *    For each of these fields, we map into the ACLMessage fields of ontology,
 *    conversationId and content. We also use the language field to contain the
 *    DistilledStateChartEvent keyword to use to indicate that this is indeed a DistilledStateChartEvent
 *    (note this is redundant as we could just check the type of the object, but when we serialize
 *    the object, then we need to be able to easily distinguish events). Finally, we
 *    use the UNKNOWN performative for these types of events.
 * </p>
 * 
 * @author G. Fortino, F. Rango
 */
public class DistilledStateChartEvent extends ACLMessage {

  /** constant identifying the DSC events **/

  public static final String TIMEOUT = "TIMEOUT";
  public static final String STRING = "STRING";
  public static final String FAIL = "FAIL";
  public static final String SUCCESS = "SUCCESS";
  public static final String ONSTART = "ONSTART";
  public static final String ONEND = "ONEND";

  public static final String DISTILLED_STATE_CHART_EVENT = "DistilledStateChartEvent";

  /** Constructor providing an event type.
   * No testing is done of the event type to ensure that it is legal.
   * @param aType The string type of this event.
   */
  public DistilledStateChartEvent(String aType) {
    super(ACLMessage.UNKNOWN);
    super.setLanguage(DISTILLED_STATE_CHART_EVENT);
    setOntology(aType);
  }

  /** Constructor providing an event type and an ID.
   * No testing is done of the event type to ensure that it is legal.
   * @param aType The string type of this event.
   * @param anId The string ID of the event.
   */
  public DistilledStateChartEvent(String aType, String anId) {
    this(aType);
    setId(anId);
  }

  /** Override the setLanguage method so you can NOT change the language.
   * @param aLanguage The new language (THIS IS IGNORED).
   */
  public void setLanguage(String aLanguage) {
  }

  /** Retrieve the type of event (as a String).
   * @return The event type.
   */
  public String getType() {
    return getOntology();
  }

  /** Set the type of event.
   * @param aType The String type of the event.
   */
  public void setType(String aType) {
    setOntology(aType);
  }

  /** Retrieve the ID of the event (a String).
   * @return The ID.
   */
  public String getId() {
    return getConversationId();
  }

  /** Set the ID of the event.
   * @param anId The String ID of the event.
   */
  public void setId(String anId) {
    setConversationId(anId);
  }

  /** Used as a template to make sure that we have the correct event type.
   * @param value The String type that we are trying to find.
   * @return The MessageTemplate to do the match for us.
   */
  public static MessageTemplate MatchType(String value) {
      return MessageTemplate.and(MessageTemplate.MatchLanguage(DISTILLED_STATE_CHART_EVENT),
                                 MessageTemplate.MatchOntology(value));
  }

  /** Used as a template to make sure that we have the correct event ID.
   * @param value The String ID that we are trying to find.
   * @return The MessageTemplate to do the match for us.
   */
  public static MessageTemplate MatchId(String value) {
    return MessageTemplate.and(MessageTemplate.MatchLanguage(DISTILLED_STATE_CHART_EVENT),
                               MessageTemplate.MatchConversationId(value));
  }

  /** Used as a template to make sure that we have the correct event Content.
   * @param value The String content that we are trying to find.
   * @return The MessageTemplate to do the match for us.
   */
  public static MessageTemplate MatchContent(String value) {
    return MessageTemplate.and(MessageTemplate.MatchLanguage(DISTILLED_STATE_CHART_EVENT),
                               MessageTemplate.MatchContent(value));
  }

  /** Used as a template to make sure that we have the correct event ID for a timeout.
   * @param value The String content that we are trying to find.
   * @return The MessageTemplate to do the match for us.
   */
  public static MessageTemplate MatchTimeout(String value) {
    return MessageTemplate.and(MatchType(TIMEOUT), MatchId(value));
  }
}