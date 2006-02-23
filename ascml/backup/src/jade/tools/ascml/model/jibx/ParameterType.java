package jade.tools.ascml.model.jibx;

public class ParameterType {
  protected String name;

  protected String type;

  protected String description;

  protected String optional;


  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getOptional() {
    return this.optional;
  }

  public void setOptional(String optional) {
    this.optional = optional;
  }

}
