package examples.security;

import jade.core.*;
import jade.core.behaviours.*;
import jade.core.security.*;
import jade.security.*;
import jade.security.impl.*;
import jade.lang.acl.*;
import java.io.*;
import starlight.util.Base64;

public class EncryptionAgent extends Agent {
  
  public static final String services = Profile.DEFAULT_SERVICES
    + ";jade.core.security.encryption.EncryptionService"
    + ";jade.core.security.SecurityService";
  
  public void setup() {
    try {
      System.out.println("EncryptionAgent launched");
      // Gets the public key of the receiver and trust it
      SecurityHelper se = (SecurityHelper)getHelper(SecurityService.NAME);
      //ObjectInputStream in = new ObjectInputStream(new FileInputStream("Security/TOTO.keystore"));
      // Deserialize the object
      //KeyPair keypair = (KeyPair) in.readObject();
      //in.close();
      //se.addTrustedKey(new AID("TOTO",false),new JADEKey("RSA",keypair.getPublic().getEncoded()));
      String algo = (String)getArguments()[0];
      byte[] key =  Base64.decode(((String)getArguments()[1]).toCharArray());
      AID rcv = new AID("TOTO",false);
      JADEPrincipal prin = new JADEPrincipalImpl(rcv.getName(),new SDSINameImpl(key,algo,null));
      se.addTrustedPrincipal(prin);
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(rcv);
      msg.setContent("This is a test content");
      se.toEncrypt(msg);
      System.out.println(msg);
      System.out.println(msg.getEnvelope());
      send(msg);
      msg = blockingReceive();
      System.out.println("STEP 2: Is reply encrypted (should be \"false\")? "+se.isEncrypted(msg));
      System.out.println("STEP 2: "+msg);
    }
    catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  public static class ReceiverAgent extends Agent {
    
    public void setup() {
      System.out.println("ReceiverAgent launched");

      addBehaviour(new CyclicBehaviour() {
          
          public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
              try {
                SecurityHelper se = (SecurityHelper)getHelper(jade.core.security.SecurityService.NAME);
                System.out.println("STEP 1: Is message encrypted (should be true)? "+se.isEncrypted(msg));
                System.out.println("STEP 1: "+msg);
                msg = msg.createReply();
                System.out.println("REPLY: "+msg);
                msg.setContent("Dummy answer");
                myAgent.send(msg);
              }
              catch (Exception e) {
                e.printStackTrace();
              }
            }
            else {
              block();
            }
          }
          
        }); 
      
      try {
        jade.core.Runtime rt = jade.core.Runtime.instance();
        ProfileImpl p2 = new ProfileImpl(false);
        p2.setParameter(Profile.SERVICES,services);
        SDSIName key = ((SecurityHelper)getHelper(SecurityService.NAME)).getPrincipal().getSDSIName();
        String params = "("+key.getAlgorithm()+","+new String(Base64.encode(key.getEncoded()))+")";
        System.out.println(params);
        p2.setParameter(Profile.AGENTS,"TA:examples.security.EncryptionAgent"+params);
        p2.setParameter(SecurityHelper.KEY_ALGO,"RSA");
        rt.createAgentContainer(p2);
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  } // End of SecurityReceiverAgent class

  public static void main(String[] args) {
    try {
      jade.core.Runtime rt = jade.core.Runtime.instance();
			rt.setCloseVM(true);
      
			ProfileImpl p = new ProfileImpl("localhost", 1099, null);
      p.setParameter(Profile.SERVICES,services);
      p.setParameter(Profile.AGENTS,"TOTO:examples.security.EncryptionAgent$ReceiverAgent");
      rt.createMainContainer(p);
		} 
    catch (Exception e) {
			e.printStackTrace();
		}
  }
  
}
