/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB S.p.A.

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

package jade.security.impl;

import jade.security.SecurityFactory;
import jade.security.JADEAuthority;
import jade.security.JADEAccessController;
import jade.core.security.authentication.JADEUserAuthenticatorImpl;
import jade.security.JADEPrincipal;
import jade.security.SDSIName;
import jade.core.security.authentication.JADEUserAuthenticator;


/**

    @author Giosue Vitaglione - Telecom Italia LAB
	@version $Date$ $Revision$
*/
public class JADESecurityFactory extends SecurityFactory {

  public JADEAuthority newJADEAuthority() {
    return new JADEAuthorityImpl();
  }

  public JADEAccessController newJADEAccessController(
                    String name, JADEAuthority authority, String policy) {
    return new JADEAccessControllerImpl(name, authority, policy);
  }

  public JADEUserAuthenticator newJADEUserAuthenticator() {
    return new JADEUserAuthenticatorImpl();
  }

  public JADEPrincipal newJADEPrincipal(SDSIName sdsiname) {
    return new JADEPrincipalImpl(sdsiname);
  }
  public JADEPrincipal newJADEPrincipal(String name, SDSIName sdsiname) {
    return new JADEPrincipalImpl(name, sdsiname);
  }
} // end SecurityFactory

