package jade.tools.ascml.model.jibx;

import java.util.ArrayList;


public class AgentParameters
{
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

	protected ArrayList parameterList = new ArrayList();

	protected ArrayList parametersetList = new ArrayList();

}
