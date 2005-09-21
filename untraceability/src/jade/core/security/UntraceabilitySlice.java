package jade.core.security;

//#MIDP_EXCLUDE_FILE

import jade.core.*;

/**
 *
 * The horizontal interface for the JADE kernel-level service enabling
 * the untraceable migration of mobile agents.
 *
 * <p>The class was created according to <code>jade.core.security.SecuritySlice</code>.</p>
 *
 * @see jade.core.security.SecuritySlice
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 */
public interface UntraceabilitySlice
    extends Service.Slice {

  public static final String NAME = "jade.core.security.Untraceability";
}
