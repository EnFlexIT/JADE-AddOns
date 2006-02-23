package jade.tools.ascml.model.jibx;

import java.util.ArrayList;


public class AgentidType {
  protected String name;


  public void addAddress(String address) {
    addressList.add(address);
  }

  public String getAddress(int index) {
    return (String)addressList.get( index );
  }

  public int sizeAddressList() {
    return addressList.size();
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  protected ArrayList addressList = new ArrayList();

}
