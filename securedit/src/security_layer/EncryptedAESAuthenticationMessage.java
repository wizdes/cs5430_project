package security_layer;

import javax.crypto.SealedObject;

/**
 * This specifies the message was encrypted with AES within an authentication
 * process.
 * -This is useful when multiplexing on the other end.
 * -This is necessary, since authentication(during setup) encryption using a
 *  One-time PIN key which is within a separate data structure.
 */
public class EncryptedAESAuthenticationMessage extends EncryptedAESMessage {

    EncryptedAESAuthenticationMessage(SealedObject encryptedObject, byte[] iv, byte[] hmac){
        super(encryptedObject, iv, hmac);
    }
}
