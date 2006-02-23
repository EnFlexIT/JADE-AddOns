package jade.tools.ascml.absmodel.dependency;

/**
 * 
 */
public interface IDependency
{
	String AGENTTYPE_DEPENDENCY			= "agenttype_dependency";
	String AGENTINSTANCE_DEPENDENCY		= "agentinstance_dependency";
	String SOCIETYTYPE_DEPENDENCY		= "societytype_dependency";
	String SOCIETYINSTANCE_DEPENDENCY	= "societyinstance_dependency";
	String SERVICE_DEPENDENCY			= "service_dependency";
	String DELAY_DEPENDENCY				= "delay_dependency";
	String ALL = "ALL";
	String STATE_STARTING		= "STARTING";
	String STATE_FUNCTIONAL		= "FUNCTIONAL";
	String STATE_NONFUNCTIONAL	= "NON-FUNCTIONAL";
	String STATE_STOPPING		= "STOPPING";
	String STATE_ERROR			= "ERROR";
	String STATE_UNKNOWN		= "UNKNOWN";

	/**
	 * Get the type of the depenendency.
	 * Possible values are AGENTTYPE_DEPENDENCY, AGENTINSTANCE_DEPENDENCY,
	 * SOCIETYTYPE_DEPENDENCY, SOCIETYINSTANCE_DEPENDENCY, SERVICE_DEPENDENCY, DELAY_DEPENDENCY.
	 * @return  The type of the dependency.
	 */
	String getType();

	/**
	 * Return whether this is an active dependency or not.
	 * The ASCML activly engages in fulfilling an active dependency or
	 * just waits for a not-active dependency to hold.
	 * @return  true, if this is an active dependency, false otherwise.
	 */
	boolean isActive();

	/**
	 * Set whether this is an active dependency or not.
	 * The ASCML activly engages in fulfilling an active dependency or
	 * just waits for a not-active dependency to hold.
	 * @param active  true, if this is an active dependency, false otherwise.
	 */
	void setActive(boolean active);

	/**
	 * Set whether this is an active dependency or not.
	 * The ASCML activly engages in fulfilling an active dependency or
	 * just waits for a not-active dependency to hold.
	 * @param active  true, if this is an active dependency, false otherwise.
	 */
	void setActive(String active);

	String toString();
}
