package security_layer;

import javax.crypto.SealedObject;

/**
 * Represents an AES encrypted message that is either sent over the wire or to a file.
 * -Uses iv and hmac.
 * -Java's SealedObject contains the message encrypted.
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

