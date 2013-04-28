package security_layer;

import javax.crypto.SealedObject;

/**
 * Instance of an encrypted AES file.
 * -Uses everything AESMessage does(iv, hmac), but also uses a salt and hmac salt.
 * -This allows the same password to be used for both AES key and HMAC key.
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
