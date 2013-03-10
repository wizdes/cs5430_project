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
class EncryptedAESFile extends EncryptedAESMessage{
    String hmacSalt;
    public EncryptedAESFile(SealedObject encryptedObject, byte[] iv, byte[] hmac, String hmacSalt) {
        super(encryptedObject, iv, hmac);
        this.hmacSalt = hmacSalt;
    }
    
}
