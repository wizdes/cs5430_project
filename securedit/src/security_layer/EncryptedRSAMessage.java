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
class EncryptedRSAMessage implements EncryptedMessage{
    SealedObject encryptedObject;
    
    EncryptedRSAMessage(SealedObject encryptedObject){
        this.encryptedObject = encryptedObject;
    }

    @Override
    public String getAlgorithm() {
        return encryptedObject.getAlgorithm();
    }
}
