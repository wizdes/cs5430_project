/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Message;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public boolean sendAESEncryptedMessage(String destination, Message m);
    public boolean sendRSAEncryptedMessage(String destination, Message m);
    public Message processEncryptedMessage(String sourceOfMessage, EncryptedMessage encryptedMsg) throws NoSuchAlgorithmException;
    public boolean writeEncryptedFile(String filename, Message contents);
    public Message readEncryptedFile(String filename);
    public String readUnencryptedFile(String filename);
    public boolean authenticate(String destination);
    public void shutdown();
    
}
