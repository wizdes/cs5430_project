/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer;

import javax.crypto.SealedObject;

/**
 *
 */
public class EncryptedAuthenticationMessage extends EncryptedAESMessage {

    EncryptedAuthenticationMessage(SealedObject encryptedObject, byte[] iv, byte[] hmac){
        super(encryptedObject, iv, hmac);
    }
}
