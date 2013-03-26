/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer;

import javax.crypto.SealedObject;

/**
 *
 */
class EncryptedAESHumanAuthMessage implements EncryptedMessage {
    EncryptedAESMessage aesMessage;
    
    EncryptedAESHumanAuthMessage(EncryptedAESMessage aesMessage){
        this.aesMessage = aesMessage;
    }

    @Override
    public String getAlgorithm() {
        return aesMessage.getAlgorithm();
    }
}
