/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

/**
 *
 * @author Patrick C. Berens
 */
public interface SecureTransportInterface {
    public java.io.Serializable sendEncryptedMessage(java.io.Serializable msg);
    public java.io.Serializable receiveEncryptedMessage();
    public java.io.Serializable writeEncryptedFile(String filename, java.io.Serializable contents);
    public java.io.Serializable readEncryptedFile(String filename);
}
