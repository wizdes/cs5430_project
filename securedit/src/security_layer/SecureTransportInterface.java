/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.messages.Message;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import transport_layer.network.Node;


/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public java.io.Serializable sendAESEncryptedMessage(Message m);
    public java.io.Serializable sendRSAEncryptedMessage(Message m);
    public Message processEncryptedMessage(Serializable encryptedNetMsg) throws NoSuchAlgorithmException;
    public java.io.Serializable writeEncryptedFile(String filename, java.io.Serializable contents);
    public java.io.Serializable readEncryptedFile(String filename);
    public java.io.Serializable readUnencryptedFile(String filename);
    public boolean authenticate(Node dest);
    public void shutdown();
}
