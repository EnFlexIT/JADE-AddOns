package examples.security;

import jade.core.*;
import jade.core.behaviours.*;
import jade.core.security.*;
import jade.lang.acl.*;

public class SignatureAgent extends Agent {
  
  public void setup() {
    try {
      System.out.println("SignatureTestAgent launched");
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(new AID("TOTO",false));
      SecurityHelper se = (SecurityHelper)getHelper(jade.core.security.SecurityService.NAME);
      //se.setSignatureAlgorithm("DSA");
      se.toSign(msg);
      System.out.println(msg);
      System.out.println(msg.getEnvelope());
      send(msg);
      System.out.println("STEP 1: Message sent");
      msg = blockingReceive();
      System.out.println("STEP 2: Is reply signed (should be \"false\")? "+se.isSigned(msg));
      System.out.println(msg);
    }
    catch (Exception e) {
			e.printStackTrace();
		}
  }
  
  public static class ReceiverAgent extends Agent {
    
    public void setup() {
      System.out.println("Receiver Agent launched");
      
      addBehaviour(new CyclicBehaviour() {
          
          public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
              try {
                SecurityHelper se = (SecurityHelper)getHelper(jade.core.security.SecurityService.NAME);
                System.out.println("STEP 1: Is message signed (should be true)? "+se.isSigned(msg));
                System.out.println("STEP 1: Principal: "+se.getPrincipal(msg).toString());
                msg = msg.createReply();
                myAgent.send(msg);
                System.out.println("STEP 2: Reply sent");
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
    }
  } // End of SecurityReceiverAgent class

  public static void main(String[] args) {
    try {
			jade.core.Runtime rt = jade.core.Runtime.instance();
			rt.setCloseVM(true);
			ProfileImpl p = new ProfileImpl("localhost", 1099, null);
      String services = Profile.DEFAULT_SERVICES
                        + ";jade.core.security.signature.SignatureService"
                        + ";jade.core.security.SecurityService";
      p.setParameter(Profile.SERVICES,services);
      p.setParameter(Profile.AGENTS,"TOTO:examples.security.SignatureAgent$ReceiverAgent");
      rt.createMainContainer(p);
      
      ProfileImpl p2 = new ProfileImpl(false);
      p2.setParameter(Profile.SERVICES,services);
      p2.setParameter(Profile.AGENTS,"TA:examples.security.SignatureAgent");
      p2.setParameter(SecurityHelper.KEY_ALGO,"DSA");
      rt.createAgentContainer(p2);
			
		} 
    catch (Exception e) {
			e.printStackTrace();
		}
  }
  
}

