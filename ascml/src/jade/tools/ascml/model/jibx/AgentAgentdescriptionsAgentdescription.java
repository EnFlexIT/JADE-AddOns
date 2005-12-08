package jade.tools.ascml.model.jibx;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.ArrayList;


public class AgentAgentdescriptionsAgentdescription {
  protected AgentidType name;


  public AgentidType getName() {
    return this.name;
  }

  public void setName(AgentidType name) {
    this.name = name;
  }

  public void addService(String service) {
    serviceList.add(service);
  }

  public String getService(int index) {
    return (String)serviceList.get( index );
  }

  public int sizeServiceList() {
    return serviceList.size();
  }

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

  protected ArrayList serviceList = new ArrayList();

  protected ArrayList protocolList = new ArrayList();

  protected ArrayList ontologyList = new ArrayList();

  protected ArrayList languageList = new ArrayList();

}
