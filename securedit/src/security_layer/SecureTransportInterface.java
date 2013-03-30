/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Messages.Message;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.SecretKey;
import transport_layer.discovery.DiscoveryResponseMessage;

/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public boolean sendAESEncryptedMessage(String destination, Message m);
    public boolean sendAESEncryptedMessage(String destination, Message m, SecretKey secretKey, SecretKey HMACKey);
    public boolean sendRSAEncryptedMessage(String destination, Message m);
    public boolean sendPlainTextMessage(String destination, Message m);
    public boolean processPlaintextMessage(String sourceOfMessage, PlaintextMessage msg);
    public boolean processEncryptedMessage(String sourceOfMessage, EncryptedMessage encryptedMsg) throws InvalidHMACException;
    public boolean writeEncryptedFile(String filename, Message contents);
    public Message readEncryptedFile(String filename);
    public String readUnencryptedFile(String filename);
    public boolean authenticate(String destination);

    public void shutdown();

    //FOR TESTING PURPOSES ONLY
    public void addPeer(String peerIdent, String host, int port);
    
    public ArrayList<Integer> findPeers(int myID);
    public boolean initializeHumanAuthenticate(String ID);
    
    public void broadcastDiscovery();
    public void processDiscoveryResponse(DiscoveryResponseMessage msg);

    public boolean addPIN(String ID, String PIN);
    public String getPIN(String ID);
    public void displayPIN(String ID, String PIN);
}
