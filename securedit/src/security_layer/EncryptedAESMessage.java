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
    
    EncryptedAESMessage(SealedObject encryptedObject, byte[] iv){
        assert iv.length == 16: iv;
        this.iv = iv;
        this.encryptedObject = encryptedObject;
    }

    @Override
    public String getAlgorithm() {
        return encryptedObject.getAlgorithm();
    }
}
