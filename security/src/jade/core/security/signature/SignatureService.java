/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.core.security.signature;

//#MIDP_EXCLUDE_FILE

import jade.core.AgentContainer;
import jade.core.BaseService;
import jade.core.Filter;
import jade.core.Profile;
import jade.core.ProfileException;
import jade.core.Service;
import jade.core.Sink;


/**
 * Implementation of the SignatureService, which role is to sign and verify
 * messages that have been encoded by the EncodingService
 *
 * @author Nicolas Lhuillier - Motorola Labs
 */
public class SignatureService extends BaseService {

  // the filter performing the real authorization check
  private SignatureFilter filter;

  // logging verbosity
  private int verbosity;

  private static final String VERBOSITY_KEY = "jade_core_security_SecurityService_verbosity";

  public String getName() {
    return SignatureSlice.NAME;
  }

  public void init(AgentContainer ac, Profile p) throws ProfileException {
    super.init(ac, p);

    // set verbosity level for logging
    try {
      verbosity = Integer.parseInt(p.getParameter(VERBOSITY_KEY,"0"));
    }
    catch (Exception e) {
      // Ignore and keep default (0)
    }

    // create and initialize the filter of this service
    filter = new SignatureFilter();
    filter.init(ac);
  }

  public Filter getCommandFilter(boolean direction) {

    if (direction == Filter.INCOMING) {
      return filter.in;
    }
    else {
      return filter.out;
    }

  }

  /* *****************************************************
   *   Dummy implementation of Sink and Slice interfaces
   * *****************************************************/

  public Class getHorizontalInterface() {
    return null;
  }

  public Service.Slice getLocalSlice() {
    return null;
  }

  public Sink getCommandSink(boolean side) {
    return null;
  }

  public String[] getOwnedCommands() {
    return null;
  }

}
