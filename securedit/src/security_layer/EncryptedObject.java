/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import javax.crypto.SealedObject;

/**
 *
 * @author Patrick C. Berens
 */
public class EncryptedObject implements java.io.Serializable {
    byte[] iv;
    SealedObject encryptedObject;

    public EncryptedObject(SealedObject encryptedObject, byte[] iv){
        assert iv.length == 16: iv;
        this.iv = iv;
        this.encryptedObject = encryptedObject;
    }
}
