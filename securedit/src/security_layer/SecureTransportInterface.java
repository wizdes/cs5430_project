/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.NoSuchAlgorithmException;


/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public java.io.Serializable sendAESEncryptedMessage(java.io.Serializable msg);
    public java.io.Serializable sendRSAEncryptedMessage(java.io.Serializable msg);
    public void processEncryptedMessage(java.io.Serializable encryptedNetMsg) throws NoSuchAlgorithmException;
    public java.io.Serializable writeEncryptedFile(String filename, java.io.Serializable contents);
    public java.io.Serializable readEncryptedFile(String filename);
    public java.io.Serializable readUnencryptedFile(String filename);
}
