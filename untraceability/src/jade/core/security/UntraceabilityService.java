package jade.core.security;

import jade.core.*;
import jade.core.Service.*;

/**
 *
 * The service enables untraceable migration of mobile agents.
 *
 * <p>The class was created according to <code>jade.core.security.SecurityService</code>.</p>
 *
 * @see jade.core.security.SecurityService
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 */

public class UntraceabilityService
    extends BaseService {

  private Profile myProfile;

  public static final String NAME = "jade.core.security.Untraceability";

  public static final String AUTHENTICATE_USER = "AUTH_USR";

  public String getName() {
    return NAME;
  }

  public Slice getLocalSlice() {
    return null;
  }

  public Class getHorizontalInterface() {
    return null;
  }

  public Filter getCommandFilter(boolean boolean0) {
    return null;
  }

  private static final String[] OWNED_COMMANDS = {
      AUTHENTICATE_USER};

  public String[] getOwnedCommands() {
    return OWNED_COMMANDS;
  }

  public Sink getCommandSink(boolean side) {
    return null;
  }

  public ServiceHelper getHelper(Agent a) {
    UntraceabilityHelper untraceabilityHelper = new UntraceabilityHelper(
        myProfile);
    return untraceabilityHelper;
  }

}
