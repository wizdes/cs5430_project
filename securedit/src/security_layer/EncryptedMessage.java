/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

/**
 *
 * @author Patrick C. Berens
 */
public interface EncryptedMessage extends java.io.Serializable {
    public String getAlgorithm();
}
