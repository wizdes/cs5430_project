/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Messages.Message;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.SecretKey;
import security_layer.authentications.AuthenticationTransport;
import transport_layer.discovery.DiscoveryMessage;
import transport_layer.discovery.DiscoveryResponseMessage;

/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m);
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m, SecretKey secretKey, SecretKey HMACKey);
//    public boolean sendRSAEncryptedMessage(String destination, Message m);
    public boolean sendPlainTextMessage(String destination, Message m);
//    public boolean processPlaintextMessage(String sourceOfMessage, PlaintextMessage msg);
    public boolean processEncryptedMessage(String sourceOfMessage, String docID, EncryptedMessage encryptedMsg) throws InvalidHMACException;
    public boolean writeEncryptedFile(String filename, char[] password, Message contents);
    public Message readEncryptedFile(String filename, char[] password);
    public String readUnencryptedFile(String filename);
//    public boolean authenticate(String destination);

    public void shutdown();

    //FOR TESTING PURPOSES ONLY
    public void addPeer(String peerIdent, String host, int port);
    
    public ArrayList<Integer> findPeers(int myID);
//    public boolean initializeHumanAuthenticate(String ID);
    
    public void broadcastDiscovery();
    public void processDiscoveryMessage(DiscoveryMessage dm);
    public void processDiscoveryResponse(DiscoveryResponseMessage msg);
    
    public void setAuthenticationTransport(AuthenticationTransport a);
//    public boolean addPIN(String ID, String PIN);
//    public String getPIN(String ID);
//    public void displayPIN(String ID, String PIN);
    
}
