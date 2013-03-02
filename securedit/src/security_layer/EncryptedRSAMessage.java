/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import java.security.SignedObject;
import javax.crypto.SealedObject;

/**
 *
 * @author Patrick C. Berens
 */
class EncryptedRSAMessage implements EncryptedMessage{
    SignedObject signedObject;
    
    EncryptedRSAMessage(SignedObject signedObject){
        this.signedObject = signedObject;
    }

    @Override
    public String getAlgorithm() {
        return signedObject.getAlgorithm();
    }
}
