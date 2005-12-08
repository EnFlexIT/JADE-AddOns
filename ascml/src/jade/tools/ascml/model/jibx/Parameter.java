package jade.tools.ascml.model.jibx;

import java.util.ArrayList;


public class Parameter
{
	protected String name;

	protected String type;

	protected String description;

	protected String optional;

	protected String value;


	public String getValue()
	{
		return this.value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void addConstraint(AgentParametersParameterConstraint constraint)
	{
		constraintList.add(constraint);
	}

	public AgentParametersParameterConstraint getConstraint(int index)
	{
		return (AgentParametersParameterConstraint) constraintList.get(index);
	}

	public int sizeConstraintList()
	{
		return constraintList.size();
	}

	protected ArrayList constraintList = new ArrayList();

}
