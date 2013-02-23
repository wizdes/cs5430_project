/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.messages.EncryptedMessage;
import java.security.NoSuchAlgorithmException;


/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public java.io.Serializable sendAESEncryptedMessage(EncryptedMessage m, java.io.Serializable contents);
    public java.io.Serializable sendRSAEncryptedMessage(EncryptedMessage m, java.io.Serializable contents);
    public EncryptedMessage processEncryptedMessage(EncryptedMessage encryptedNetMsg) throws NoSuchAlgorithmException;
    public java.io.Serializable writeEncryptedFile(String filename, java.io.Serializable contents);
    public java.io.Serializable readEncryptedFile(String filename);
    public java.io.Serializable readUnencryptedFile(String filename);
    public EncryptionKeys getKeys();
    public void setKeys(EncryptionKeys keys);
}
