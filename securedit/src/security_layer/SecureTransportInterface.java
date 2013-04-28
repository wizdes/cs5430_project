package security_layer;

import application.encryption_demo.Message;
import javax.crypto.SecretKey;
import security_layer.authentications.SRPAuthenticationTransport;
import transport_layer.discovery.DiscoveryMessage;
import transport_layer.discovery.DiscoveryResponseMessage;

/**
 * Interface for the secure transport layer.
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    //Sending messages and processing messages
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m);
    public boolean sendAESEncryptedMessage(String destination, String docID, Message m, SecretKey secretKey, SecretKey HMACKey);
    public boolean sendPlainTextMessage(String destination, Message m);
    public boolean sendPlainTextMessage(String destination, String docID, Message m);
    public boolean processEncryptedMessage(String sourceOfMessage, String docID, EncryptedMessage encryptedMsg);
    
    //Writing and reading files.
    public boolean writeEncryptedFile(String filename, char[] password, Message contents);
    public Message readEncryptedFile(String filename, char[] password);
    public String readUnencryptedFile(String filename);

    public void shutdown();

    //FOR TESTING PURPOSES ONLY: Used to easily add peers to network.
    public void addPeer(String peerIdent, String host, int port);
    
    //Discovery peers
    public void broadcastDiscovery();
    public void processDiscoveryMessage(DiscoveryMessage dm);
    public void processDiscoveryResponse(DiscoveryResponseMessage msg);
    
    //Add the authentication processor to the security layer.
    public void setAuthenticationTransport(SRPAuthenticationTransport a);
    
}
