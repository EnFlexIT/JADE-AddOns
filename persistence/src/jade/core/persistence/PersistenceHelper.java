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


import jade.core.ServiceHelper;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;



/**

   The vertical interface for the JADE kernel-level service managing
   saving and retrieving agents and containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public interface PersistenceHelper extends ServiceHelper {


    /**
       The name of a persistence-specific profile parameter, stating
       which repository to load the current container from.
    */
    public static final String LOAD_FROM = "load-from";

    /**
       The name of this service.
    */
    public static final String NAME = "jade.core.persistence.Persistence";


    /**
       This command name represents the <code>save-agent</code>
       action, requested by an entity external from the agent to be
       saved.
    */
    static final String SAVE_AGENT = "Save-Agent";

    /**
       This command name represents the <code>load-agent</code>
       action, requested by an entity external from the agent to be
       loaded.
    */
    static final String LOAD_AGENT = "Load-Agent";

    /**
       This command name represents the <code>reload-agent</code>
       action, requested by an entity external from the agent to be
       reloaded.
    */
    static final String RELOAD_AGENT = "Reload-Agent";

    /**
       This command name represents the <code>delete-agent</code>
       action, requested by an entity external from the agent to be
       loaded.
    */
    static final String DELETE_AGENT = "Delete-Agent";

    /**
       This command name represents the <code>freeze-agent</code>
       action, requested by an entity external from the agent to be
       frozen.
    */
    static final String FREEZE_AGENT = "Freeze-Agent";

    /**
       This command name represents the <code>thaw-agent</code>
       action.
    */
    static final String THAW_AGENT = "Thaw-Agent";

    /**
       This command name represents the <code>save-myself</code>
       action, requested by the agent that is to be saved.
    */
    static final String SAVE_MYSELF = "Save-Myself";

    /**
       This command name represents the <code>reload-myself</code>
       action, requested by the agent that wants to restore its state
       to a previously saved one.
    */
    static final String RELOAD_MYSELF = "Reload-Myself";

    /**
       This commmand name represents the <code>freeze-myself</code>
       action, requested by the agent that wants to be frozen in a
       persistent store.
    */
    static final String FREEZE_MYSELF = "Freeze-Myself";

    /**
       This command name represents the <code>save-agent-group</code>
       action.
    */
    static final String SAVE_AGENT_GROUP = "Save-Agent-Group";

    /**
       This command name represents the
       <code>delete-agent-group</code> action, requested by an entity
       external from the agent group to be deleted.
    */
    static final String DELETE_AGENT_GROUP = "Delete-Agent-Group";

    /**
       This command name represents the <code>load-agent-group</code>
       action.
    */
    static final String LOAD_AGENT_GROUP = "Load-Agent-Group";


    /**
       This command name represents the <code>save-container</code>
       action.
    */
    static final String SAVE_CONTAINER = "save-container";

    /**
       This command name represents the <code>load-container</code>
       action.
    */
    static final String LOAD_CONTAINER = "load-container";

    /**
       This command name represents the <code>delete-container</code>
       action.
    */
    static final String DELETE_CONTAINER = "delete-container";


    void saveAgent(AID agentID, String repository) throws ServiceException, NotFoundException, IMTPException;
    void loadAgent(AID agentID, String repository, ContainerID where) throws ServiceException, IMTPException, NotFoundException, NameClashException;
    void reloadAgent(AID agentID, String repository) throws ServiceException, IMTPException, NotFoundException;
    void deleteAgent(AID agentID, String repository, ContainerID where) throws ServiceException, IMTPException, NotFoundException;
    void freezeAgent(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, NotFoundException, IMTPException;
    void thawAgent(AID agentID, String repository, ContainerID newContainer) throws ServiceException, NotFoundException, IMTPException;

    void saveMyself(AID agentID, String repository) throws ServiceException, NotFoundException, IMTPException;
    void reloadMyself(AID agentID, String repository) throws ServiceException, IMTPException, NotFoundException;
    void freezeMyself(AID agentID, String repository) throws ServiceException, NotFoundException, IMTPException;

    void saveContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException;
    void loadContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException, NameClashException;
    void deleteContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException;


    /***
	TO BE IMPLEMENTED

    void saveAgentGroup(...) throws ServiceException, IMTPException...
    void loadAgentGroup(...) throws ServiceException, IMTTPException...

    Some more management methods...

    ***/



}
