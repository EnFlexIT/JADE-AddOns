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

package jade.core.persistence;

//#MIDP_EXCLUDE_FILE


import jade.core.Service;
import jade.core.Filter;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;

import jade.security.CertificateFolder;
import jade.security.AuthException;

import jade.util.leap.List;

/**

   The horizontal interface for the JADE kernel-level service managing
   saving and retrieving agents and containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public interface PersistenceSlice extends Service.Slice {


    /**
       The name of this service.
    */
    public static final String NAME = "jade.core.persistence.Persistence";


    // Constants for the names of horizontal commands associated to methods
    static final String H_SAVEAGENT = "1";
    static final String H_LOADAGENT = "2";
    static final String H_DELETEAGENT = "3";
    static final String H_DELETEFROZENAGENT = "4";
    static final String H_FREEZEAGENT = "5";
    static final String H_THAWAGENT = "6";
    static final String H_SETUPFROZENAGENT = "7";
    static final String H_SETUPTHAWEDAGENT = "8";
    static final String H_FROZENAGENT = "9";
    static final String H_THAWEDAGENT = "10";

    void saveAgent(AID agentID, String repository) throws IMTPException, NotFoundException;
    void loadAgent(AID agentID, String repository) throws IMTPException, NotFoundException;
    void deleteAgent(AID agentID, String repository) throws IMTPException, NotFoundException;
    void deleteFrozenAgent(AID agentID, String repository, Long agentFK) throws IMTPException, NotFoundException;
    void freezeAgent(AID agentID, String repository, ContainerID bufferContainer) throws IMTPException, NotFoundException;
    void thawAgent(AID agentID, String repository, ContainerID newContainer) throws IMTPException, NotFoundException;
    Long setupFrozenAgent(AID agentID, Long agentFK, ContainerID cid, String repository) throws IMTPException, NotFoundException;
    void setupThawedAgent(AID agentID, Long agentFK, ContainerID cid, String repository, List bufferedMessages) throws IMTPException, NotFoundException;
    void frozenAgent(AID agentID, ContainerID home, ContainerID buffer) throws ServiceException, IMTPException, NotFoundException;
    void thawedAgent(AID agentID, ContainerID buffer, ContainerID home) throws ServiceException, IMTPException, NotFoundException;

}
