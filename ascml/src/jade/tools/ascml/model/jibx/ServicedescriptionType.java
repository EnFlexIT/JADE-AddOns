package jade.tools.ascml.model.jibx;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;


public class ServicedescriptionType {
  protected String name;

  protected String type;

  protected String ownership;


  public void addProtocol(String protocol) {
    protocolList.add(protocol);
  }

  public String getProtocol(int index) {
    return (String)protocolList.get( index );
  }

  public int sizeProtocolList() {
    return protocolList.size();
  }

  public void addOntology(String ontology) {
    ontologyList.add(ontology);
  }

  public String getOntology(int index) {
    return (String)ontologyList.get( index );
  }

  public int sizeOntologyList() {
    return ontologyList.size();
  }

  public void addLanguage(String language) {
    languageList.add(language);
  }

  public String getLanguage(int index) {
    return (String)languageList.get( index );
  }

  public int sizeLanguageList() {
    return languageList.size();
  }

  public void addProperty(ServicedescriptionTypeProperty property) {
    propertyList.add(property);
  }

  public ServicedescriptionTypeProperty getProperty(int index) {
    return (ServicedescriptionTypeProperty)propertyList.get( index );
  }

  public int sizePropertyList() {
    return propertyList.size();
  }

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

  public String getOwnership() {
    return this.ownership;
  }

  public void setOwnership(String ownership) {
    this.ownership = ownership;
  }

  protected ArrayList protocolList = new ArrayList();

  protected ArrayList ontologyList = new ArrayList();

  protected ArrayList languageList = new ArrayList();

  protected ArrayList propertyList = new ArrayList();

}
