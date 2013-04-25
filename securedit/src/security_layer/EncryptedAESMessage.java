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
class EncryptedAESMessage implements EncryptedMessage {
    byte[] iv;
    SealedObject encryptedObject;
    byte[] HMAC;
    
    
    EncryptedAESMessage(SealedObject encryptedObject, byte[] iv, byte[] hmac){
        assert iv.length == 16: iv;
        this.iv = iv;
        this.encryptedObject = encryptedObject;
        this.HMAC = hmac;
    }

    @Override
    public String getAlgorithm() {
        return encryptedObject.getAlgorithm();
    }
}

