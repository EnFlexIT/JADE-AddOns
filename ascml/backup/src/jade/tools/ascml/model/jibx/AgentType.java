package jade.tools.ascml.model.jibx;

import jade.tools.ascml.absmodel.IDocument;
import jade.tools.ascml.absmodel.IAgentDescription;
import jade.tools.ascml.absmodel.IAgentParameter;
import jade.tools.ascml.absmodel.IAgentParameterSet;
import jade.tools.ascml.model.AbstractAgentModel;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.onto.Status;

import javax.swing.*;
import java.util.Vector;
import java.util.ArrayList;

public class AgentType
{
	public final static String NAME_UNKNOWN = "Unknown";

	/**
	 * This constant is used to indicate, that this model has successfully been loaded.
	 */
	public final static String STATUS_OK		= "successfully loaded";

	/**
	 * This constant is used to indicate, that at least one error occurred while loading the model.
	 */
	public final static String STATUS_ERROR		= "loading error";

	/**
	 * This constant is used to indicate, that at least one referenced agenttype
	 * has NOT been loaded successfully
	 */
	public final static String STATUS_REFERENCE_ERROR	= "erroneous reference";

	protected AgentServicedescriptions servicedescriptions;

	protected AgentAgentdescriptions agentdescriptions;

	/** The agent's type name (not fully qualified) */
	protected String name;

    /** The agent's package-name (not fully qualified) */
	protected String packageName;

	/** The agent class name. */
	protected String className;

	/** The platform-identifier (e.g. jade). */
	protected String platformType;

	/** The agent type's description. */
	protected String description;

	/** The path and name of the icon used to represent this agent in the repository-tree */
	protected String iconName;

	protected ArrayList parameterList = new ArrayList();

	protected ArrayList parametersetList = new ArrayList();

	protected AgentParameters parameters;
	
	/** The document. */
	protected IDocument document;

	/**
	 * The status indicates if this model has successfully been loaded.
	 * Possible stati are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	protected String status;

	/**
	* This Exception may contain a set of detailed String-messages in case the status is != STATUS_OK
	*/
	protected ModelException statusException;

	/**
	 * The modelChangedListener are informed when some changes in the
	 * model-object are made
	 */
    protected Vector modelChangedListener;

	/**
	 *  Set the name of this agentType.
	 *  @param name  agentType's name.
	 */
	public void setName(String name)
	{
		if(name == null)
			name = "";
		this.name = name.trim();
	}

	/**
	 *  Get the name of this agentType.
	 *  @return  agentType's name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Get the package-name of this agentType
	 * @return packageName as String (e.g. examples.party)
	 */
	public String getPackageName()
	{
		return packageName;
	}

	/**
	 * Set the package-name of this agentType
	 * @param packageName  The packageName as String (e.g. examples.party)
	 */
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public String getStatus()
	{
		return this.status;
	}

	/**
	 * Set the status of this model. The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public void setStatus(String newStatus)
	{
		this.status = newStatus;
	}

	/**
	 * Set the type of agent-platform (e.g. jade) this agenttype is designed to run on.
	 * @param platformType  The platformType as String, possible values may be
	 * PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public void setPlatformType(String platformType)
	{
		if(platformType == null)
			platformType = "";
		this.platformType = platformType.trim();
	}

	/**
	 * Get the type of agent-platform (e.g. jade) this agenttype is designed to run on.
	 * return The platformType as String, possible values may be PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public String getPlatformType()
	{
		return platformType;
	}

	/**
	 * Update the status of this model.
	 * The status indicates, whether this model is ready to run or if it contains errors.
	 */
	/*public void updateStatus()
	{
		String newStatus = STATUS_OK;

		if (getName().equals(AbstractAgentModel.NAME_UNKNOWN))
		{
		    statusException = new ModelException("The agenttype is unknown (possibly not contained within the repository).", "The agenttype is unkwown, which means, that it couldn't be found in the repository. Please first load the agenttype before going on.");
			newStatus = STATUS_ERROR;
		}
		else
		{
			statusException = new ModelException("The agenttype '" + getName() + "' is errorneous.", "The AgentType contains errors.");

			if ((getName() == null) || getName().equals(""))
			{
				statusException.addExceptionDetails("A name for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if ((getFullyQualifiedName() == null) || getFullyQualifiedName().equals(""))
			{
				statusException.addExceptionDetails("A fully qualified name for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if ((getClassName() == null) || getClassName().equals(""))
			{
				statusException.addExceptionDetails("A class-name for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if ((getPlatformType() == null) || getPlatformType().equals(""))
			{
				statusException.addExceptionDetails("The platform-type of this agent is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
			if (getDocument() == null)
			{
				statusException.addExceptionDetails("The source-document for this agenttype is missing", "write me!");
				newStatus = STATUS_ERROR;
			}
		}

		if (getStatus() != newStatus)
		{
			status = newStatus;
			throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
		}
	}
    */
	/**
	 *  Returns the fully qualified agentType-name. The name is composed of
	 *  the package-name and the agentType-name, for example my.packageName.agentTypeName
	 *  would be a correct 'fully qualified' agentTypeName.
	 *  @return  fully qualified name of the agentType.
	 */
	public String getFullyQualifiedName()
	{
		if ((getPackageName() == null) || getPackageName().equals(""))
			return name;
		else
			return getPackageName()+"."+name;
	}

	/**
	 *  Set the description of this agentType.
	 *  @param description  agentType's description.
	 */
	public void setDescription(String description)
	{
		if (description == null)
			description = "";
		this.description = description.trim();
	}

	/**
	 *  Get the agent's description.
	 *  @return  agent's description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @param agentDescription  the agentType's AgentDescriptionModel.
	 */
	/*public void setAgentDescription(IAgentDescription agentDescription)
	{
		this.agentDescription = agentDescription;
	}*/

	/**
	 *  Set the classname of this agentType.
	 *  @param className  agentType's classname.
	 */
	public void setClassName(String className)
	{
		if (className == null)
			className = "";
		this.className = className.trim();
	}

	/**
	 *  Set the document.
	 *  @param document  The document.
	 */
	public void setDocument(IDocument document)
	{
		this.document = document;
	}

	/**
	 *  Set the image-icon-name. Using the getIcon-method
	 *  an imageicon is constructed out of this name.
	 *  @param iconName  The image-icon.
	 */
	public void setIconName(String iconName)
	{
		this.iconName = iconName;
	}

	/**
	 *  Get the image-icon.
	 *  @return The image-icon.
	 */
	public String getIconName()
	{
		if (iconName != null)
			return iconName;
		else
			return ImageIconLoader.AGENTTYPE; // get default-iconName
	}

	/**
	 *  Get the image-icon.
	 *  @return The image-icon.
	 */
	public ImageIcon getIcon()
	{
		if (iconName != null)
			return ImageIconLoader.createImageIcon(iconName);
		else
			return ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE); // get default-icon
	}

	public void addParameter(Parameter parameter)
	{
		parameterList.add(parameter);
	}

	public Parameter getParameter(int index)
	{
		return (Parameter) parameterList.get(index);
	}

	public int sizeParameterList()
	{
		return parameterList.size();
	}

	public void addParameterset(ParameterSet parameterset)
	{
		parametersetList.add(parameterset);
	}

	public ParameterSet getParameterset(int index)
	{
		return (ParameterSet) parametersetList.get(index);
	}

	public int sizeParametersetList()
	{
		return parametersetList.size();
	}

	/**
	 * Get the status-exception, which describe errors or warnings in detail.
	 * @return  A ModelException containing detailed status-messages as Strings.
	 */
	public ModelException getStatusException()
	{
		if (statusException.hasExceptionDetails() || statusException.hasNestedExceptions())
			return statusException;
		else
			return null;
	}

	/**
	 * Set the ModelChangedListener. These are informed in case of changes in this model.
	 * @param modelChangedListener  A Vector containing ModelChangedListener
	 */
	public void setModelChangedListener(Vector modelChangedListener)
	{
		this.modelChangedListener = modelChangedListener;
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		throwModelChangedEvent(eventCode, null);
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	public void throwModelChangedEvent(String eventCode, Object userObject)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode, userObject);

		ModelChangedListener[] listener = new ModelChangedListener[modelChangedListener.size()];
		modelChangedListener.toArray(listener);

		for (int i=0; i < listener.length; i++)
		{
			listener[i].modelChanged(event);
		}
	}

	/**
	 *  This method returns a short String with the agentType-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this agentType
	 */
	public String toString()
	{
		return getName(); // + " " + super.toString();
	}

	/**
	 *  This method returns a formatted String showing the agentType-model.
	 *  @return  formatted String showing ALL information about this agentType.
	 */
	public String toFormattedString()
	{
		String str = "";

		/*str += "  Agent-Type : name = " + getName() + "\n";
		str += "     package = " + getDocument().getPackageName() + "\n";
		str += "     class = " + className + "\n";
		str += "     desc = " + description + "\n";

		Iterator keys = parameters.keySet().iterator();
		while (keys.hasNext())
		{
			String oneKey = (String)keys.next();
			HashMap optionMap = (HashMap)parameters.get(oneKey);

			str +=  "   parameter : name = " + optionMap.get("name")        + "\n";
			str +=  "     type = " + optionMap.get("type")        + "\n";
			str +=  "     optional = " + optionMap.get("optional")    + "\n";
			str +=  "     description = " + optionMap.get("description") + "\n";
			str +=  "     value = " + optionMap.get("value")       + "\n";
			str +=  "     constraints = " + optionMap.get("constraints") + "\n";

		}

		keys = parameterSets.keySet().iterator();
		while (keys.hasNext())
		{
			String oneKey   = (String)keys.next();
			HashMap optionMap = (HashMap)parameterSets.get(oneKey);

			str +=  "   param-set : name = " + optionMap.get("name")        + "\n";
			str +=  "     type = " + optionMap.get("type")        + "\n";
			str +=  "     optional = " + optionMap.get("optional")    + "\n";
			str +=  "     description = " + optionMap.get("description") + "\n";
			str +=  "     value = " ;

			Iterator oneSet = ((Collection)optionMap.get("value")).iterator();
			str += "[";
			while (oneSet.hasNext())
			{
				str += oneSet.next() + ",";
			}
			str += "]\n";

			str +=  "     constraints = ";
			oneSet = ((Collection)optionMap.get("constraints")).iterator();
			str += "[";
			while (oneSet.hasNext())
			{
				str += oneSet.next() + ",";
			}
			str += "]\n";
		} */
		return str;
	}
}
