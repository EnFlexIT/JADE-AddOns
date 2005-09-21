package jade.core.security;

import java.io.*;
import java.nio.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;

import jade.core.*;
import jade.core.security.untraceability.*;

/**
 * Provides an agent all methods for accessing untraceability functionalities.
 *
 * Usually each agent has an <code>UntraceabilityHelper</code> object accessible through
 * the <code>getHelper()</code> method of the <code>Agent</code> class.
 *
 * @author Rafal Leszczyna
 * @version 1.0 gamma
 */
public class UntraceabilityHelper
    implements jade.core.ServiceHelper {

  static final int NONCE_LENGTH_IN_BYTES = 8;
  static final int MD5_LENGTH_IN_BYTES = 16;
  static final String KEY_STORAGE_FILE_NAME = "key.txt";

  private UntraceableAgent myAgent;
  private Profile myProfile = null;
  private MessageDigest myMD5 = null;
  private Random myRandom = null;
  private Cipher myAES = null;
  private SecretKey mySecretKey = null;

  private class DecryptedAESEncryptedID {
    public Location predecessorLoc;
    public byte[] locationsMD5;
  }

  /**
   * Initializes a random number generator.
   */
  private void initRandom() {
    myRandom = new Random();
  }

  /**
   * An auxiliary private method aiming at generation of an AES secret key
   * and its storage to a file.
   */
  private void AESKeyGenAndSave(){
    try {
      System.out.println(
          "Generating the symmetric key and storing it into a file...");
      KeyGenerator kg = null;
      kg = KeyGenerator.getInstance("AES");
      mySecretKey = kg.generateKey();
      FileOutputStream fos = null;
      ObjectOutputStream oos = null;
      fos = new FileOutputStream(KEY_STORAGE_FILE_NAME);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(mySecretKey);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Initializes a secret key for AES symmetric cryptography.
   *
   * <p>The key is read from a key storage file.
   * If the file doesn't exist, it is created and the key generated.</p>
   */
  private void initAESKey() {
    File file = new File(KEY_STORAGE_FILE_NAME);
    long fileLength;

    try {
      fileLength=file.length();

      if (fileLength==0L){
//        file.delete();
        file.createNewFile();
        AESKeyGenAndSave();
        return;
      }
      else {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        fis = new FileInputStream(KEY_STORAGE_FILE_NAME);
        ois = new ObjectInputStream(fis);
        mySecretKey = (SecretKey) ois.readObject();
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(-1);
    }
}

  /**
   * Initializes the AES symmetric cryptography.
   */
  private void initAES() {
    initAESKey();
    myAES = null;
    try {
      myAES = Cipher.getInstance("AES");
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Creates an instance of the MD5 one way function.
   */
  private void initMD5() {
    try {
      myMD5 = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException ex) {
    }
  }

  /**
   * Converts a location description (encapsulated in the <code>Location</code> class)
   * to an array of bytes.
   */
  private byte[] location2Bytes(Location location) {
    String sLocation;
    sLocation = location.getName() + ';' + location.getAddress();
    return sLocation.getBytes();
  }

  /**
   * Converts an array of bytes to a location description.
   *
   * <p>This method does the reverse of <code>location2Bytes</code>.</p>
   */
  private Location bytes2Location(byte[] bytes) {
    String sLocation;
    String name;
    String address;
    sLocation = new String(bytes);
    int index = sLocation.indexOf(';');
    address = sLocation.substring(index + 1);
    name = sLocation.substring(0, index);
    ContainerID location = new ContainerID();
    location.setName(name);
    location.setAddress(address);
    return location;
  }

  /***
   * Converts a byte array into a <code>String</code>.
   */
  private String bytes2String(byte[] bytes) {
    String sBytes = "|";
    for (int l = 0; l < bytes.length; l++) {
      sBytes += bytes[l] + "|";
    }
    return sBytes;
  }

  /**
   * Returns a nonce i.e. a random string of bytes.
   * @return the nonce
   */
  private byte[] getNonce() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(NONCE_LENGTH_IN_BYTES);
    byte[] bytes = byteBuffer.array();
    myRandom.nextBytes(bytes);
    return bytes;
  }

  /**
   * Creates a new <code>UntraceabilityHelper</code> instance.
   */
  public UntraceabilityHelper() {
    initRandom();
    initAES();
    initMD5();
  }

  /**
   * Creates a new <code>UntraceabilityHelper</code> with the given profile.
   * @param p profile
   */
  public UntraceabilityHelper(Profile p) {
    this();
    myProfile = p;
  }

  /**
   * Initializes the helper.
   * @param agent Agent
   */
  public void init(Agent agent) {
    myAgent = (UntraceableAgent) agent;
  }

  /**
   * Calculates a value of the MD5 one way function on the characters' strings'
   * concatenation of the names of agent's - preceeding, current and succeeding
   * - locations.
   * @param predecessorLoc the preceeding location i.e.
   * the last location from which the agent came to the current location
   * @param successorLoc the succeeding location i.e. the location
   * to which the agent is going to move
   * @return the value of the MD5 one way function
   */
  public byte[] get3LocationsMD5(Location predecessorLoc, Location successorLoc) {
    String s3Locations;
    byte[] locationsMD5 = null;
    Location currentLoc = myAgent.here();
    s3Locations = predecessorLoc.getName() + predecessorLoc.getAddress() +
        currentLoc.getName() + currentLoc.getAddress() +
        successorLoc.getName() + successorLoc.getAddress();
    locationsMD5 = myMD5.digest(s3Locations.getBytes());
    s3Locations = bytes2String(locationsMD5);
    return locationsMD5;
  }

  /**
   * Returns <i>encrypted location identifier</i>.
   * The identifier is formed by encrypting (using the AES symmetric cryptography)
   * a byte concatenation of three byte strings based on:
   * <ul>
   * <li>succeeding location (provided by the argument)
   * <li>a value of the MD5 one way function (see the <code>get3LocationsMD5</code>'s description)
   * <li>a nonce
   * </ul>
   * @param successorLoc the succeeding location i.e. the location
   * to which the agent is going to move
   * @return the result of the encryption
   */
  public byte[] getAESEncryptedID(Location successorLoc) {
    byte[] aesEncryptedID = null;
    byte[] bPredecessorLoc = location2Bytes(myAgent.myPredecessorLoc);
    byte[] locationsMD5 = get3LocationsMD5(myAgent.myPredecessorLoc,
                                           successorLoc);
    byte[] nonce = getNonce();
    ByteArrayOutputStream baosAESEncryptedID = new ByteArrayOutputStream();
    try {
      baosAESEncryptedID.write(bPredecessorLoc);
      baosAESEncryptedID.write(locationsMD5);
      baosAESEncryptedID.write(nonce);
    }
    catch (IOException ex) {
    }

    aesEncryptedID = baosAESEncryptedID.toByteArray();
    try {
      myAES.init(Cipher.ENCRYPT_MODE, mySecretKey);
    }
    catch (InvalidKeyException ex1) {
    }
    try {
      aesEncryptedID = myAES.doFinal(aesEncryptedID);
    }
    catch (BadPaddingException ex2) {
    }
    catch (IllegalBlockSizeException ex2) {
    }
    catch (IllegalStateException ex2) {
    }
    return aesEncryptedID;
  }

  /**
   * Extracts the location from <i>encrypted location identifier</i> (see <code>
   * getAESEncryptedID</code>).
   * <p>
   * As an intermediate step of the process, the MD5 value
   * (see the <code>get3LocationsMD5</code>'s description) is calculated
   * based on the current input and compared to the MD5 value stored in the
   * <i>encrypted location identifier</i>. If the MD5 values don't match,
   * the <code>null</code> value is returned. </p>
   * @param aesEncryptedID the <i>encrypted location identifier</i>
   * @return the location or <code>null</code>
   * The <code>null</code> value is returned any inconsistency between the MD5
   * values is detected.
   */
  public Location getLocation(byte[] aesEncryptedID) {
    DecryptedAESEncryptedID decryptedAESEncryptedID = null;
    decryptedAESEncryptedID = decryptAESEncryptedID(aesEncryptedID);
    byte[] locationsMD5 = get3LocationsMD5(decryptedAESEncryptedID.
                                           predecessorLoc,
                                           myAgent.myPredecessorLoc);
    String s3Locations = bytes2String(locationsMD5);
    boolean areMD5Equal = myMD5.isEqual(locationsMD5,
                                        decryptedAESEncryptedID.locationsMD5);
    if (areMD5Equal) {
      return decryptedAESEncryptedID.predecessorLoc;
    }
    else {
      return null;
    }
  }

  /**
   * Extracts the location from <i>encrypted location identifier</i> (see <code>
   * getAESEncryptedID</code>) without checking consistency of MD5 values (see <code>
   * getLocation</code>).
   *
   * The method was provided for testing purposes and shouldn't be used
   * out of this aim.
   *
   * @param aesEncryptedID the <i>encrypted location identifier</i>
   * @return the location.
   * @deprecated This method shouldn't be used. Use the <code>
   * getLocation</code> instead.
   */
  public Location getLocationAnyway(byte[] aesEncryptedID) {
    DecryptedAESEncryptedID decryptedAESEncryptedID = null;
    decryptedAESEncryptedID = decryptAESEncryptedID(aesEncryptedID);
    byte[] locationsMD5 = get3LocationsMD5(decryptedAESEncryptedID.
                                           predecessorLoc,
                                           myAgent.myPredecessorLoc);
    String s3Locations = bytes2String(locationsMD5);
    return decryptedAESEncryptedID.predecessorLoc;
  }

  private DecryptedAESEncryptedID decryptAESEncryptedID(byte[] aesEncryptedID) {
    try {
      myAES.init(Cipher.DECRYPT_MODE, mySecretKey);
    }
    catch (InvalidKeyException ex1) {
    }
    try {
      aesEncryptedID = myAES.doFinal(aesEncryptedID);
    }
    catch (BadPaddingException ex2) {
    }
    catch (IllegalBlockSizeException ex2) {
    }
    catch (IllegalStateException ex2) {
    }
    byte[] bPredecessorLoc = ByteBuffer.allocate(aesEncryptedID.length -
                                                 NONCE_LENGTH_IN_BYTES -
                                                 MD5_LENGTH_IN_BYTES).array();
    DecryptedAESEncryptedID decryptedAESEncryptedID = new
        DecryptedAESEncryptedID();
    decryptedAESEncryptedID.locationsMD5 = ByteBuffer.allocate(
        MD5_LENGTH_IN_BYTES).array();
    ByteBuffer bbAESEncryptedID = ByteBuffer.wrap(aesEncryptedID);
    bbAESEncryptedID.get(bPredecessorLoc, 0,
                         aesEncryptedID.length - NONCE_LENGTH_IN_BYTES -
                         MD5_LENGTH_IN_BYTES);
    bbAESEncryptedID.get(decryptedAESEncryptedID.locationsMD5, 0,
                         MD5_LENGTH_IN_BYTES);
    decryptedAESEncryptedID.predecessorLoc = bytes2Location(bPredecessorLoc);
    return decryptedAESEncryptedID;
  }

  /**
   * Tests functioning of the class.
   */
  public void serviceTest() {
    System.out.println("serviceTest");
    byte[] locationsMD5 = get3LocationsMD5(myAgent.here(), myAgent.here());
    String s3Locations = bytes2String(locationsMD5);
    System.out.println("bytes2String(locationsMD5): " + s3Locations);
    byte[] nonce = getNonce();
    String sNonce = bytes2String(nonce);
    System.out.println("nonce1: " + sNonce);
    nonce = getNonce();
    sNonce = bytes2String(nonce);
    System.out.println("nonce2: " + sNonce);
    byte[] aesEncryptedID = getAESEncryptedID(myAgent.here());
    String sAESEncryptedID = bytes2String(aesEncryptedID);
    System.out.println("sAESEncryptedID: " + sAESEncryptedID);
    System.out.println("aesEncryptedID.length: " + aesEncryptedID.length);
    Location predecessorLoc = null;
    DecryptedAESEncryptedID decryptedAESEncryptedID = null;
    decryptedAESEncryptedID = decryptAESEncryptedID(aesEncryptedID);
    System.out.println("decryptedAESEncryptedID.predecessorLoc.getName(): " +
                       decryptedAESEncryptedID.predecessorLoc.getName());
    System.out.println("decryptedAESEncryptedID.predecessorLoc.getAddress(): " +
                       decryptedAESEncryptedID.predecessorLoc.getAddress());
    s3Locations = bytes2String(decryptedAESEncryptedID.locationsMD5);
    System.out.println("bytes2String(decryptedAESEncryptedID.locationsMD5: " +
                       s3Locations);
    System.out.println(
        "myMD5.isEqual(locationsMD5, decryptedAESEncryptedID.locationsMD5): " +
        myMD5.isEqual(locationsMD5, decryptedAESEncryptedID.locationsMD5));
  }

}
