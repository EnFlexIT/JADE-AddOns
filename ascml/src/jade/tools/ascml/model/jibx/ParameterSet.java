package jade.tools.ascml.model.jibx;

import java.util.ArrayList;
import java.util.ArrayList;


public class ParameterSet {
	protected String name;

		protected String type;

		protected String description;

		protected String optional;

  public void addValue(String value) {
    valueList.add(value);
  }

  public String getValue(int index) {
    return (String)valueList.get( index );
  }

  public int sizeValueList() {
    return valueList.size();
  }

  public void addConstraint(AgentParametersParametersetConstraint constraint) {
    constraintList.add(constraint);
  }

  public AgentParametersParametersetConstraint getConstraint(int index) {
    return (AgentParametersParametersetConstraint)constraintList.get( index );
  }

  public int sizeConstraintList() {
    return constraintList.size();
  }

  protected ArrayList valueList = new ArrayList();

  protected ArrayList constraintList = new ArrayList();

}
