/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Message;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.SecretKey;

/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public boolean sendAESEncryptedMessage(String destination, Message m);
    public boolean sendAESEncryptedMessage(String destination, Message m, SecretKey secretKey);
    public boolean sendRSAEncryptedMessage(String destination, Message m);
    public Message processEncryptedMessage(String sourceOfMessage, EncryptedMessage encryptedMsg) throws NoSuchAlgorithmException;
    public boolean writeEncryptedFile(String filename, Message contents);
    public Message readEncryptedFile(String filename);
    public String readUnencryptedFile(String filename);
    public boolean authenticate(String destination);

    public void shutdown();

    public ArrayList<Integer> findPeers(int myID);
    public boolean initializeHumanAuthenticate(String ID);
}
