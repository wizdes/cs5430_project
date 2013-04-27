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
    byte[] salt;
    byte[] hmacSalt;
    
    EncryptedAESFile(SealedObject encryptedObject, byte[] iv, byte[] hmac, byte[] hmacSalt, byte[] salt) {
        super(encryptedObject, iv, hmac);
        this.salt = salt;
        this.hmacSalt = hmacSalt;
    }
    
}
