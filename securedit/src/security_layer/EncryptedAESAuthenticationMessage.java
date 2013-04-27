/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer;

import javax.crypto.SealedObject;

/**
 *
 */
public class EncryptedAESAuthenticationMessage extends EncryptedAESMessage {

    EncryptedAESAuthenticationMessage(SealedObject encryptedObject, byte[] iv, byte[] hmac){
        super(encryptedObject, iv, hmac);
    }
}
