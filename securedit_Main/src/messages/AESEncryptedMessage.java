/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import encryption.AES;
import java.beans.Transient;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import network.Node;

/**
 *
 */
public class AESEncryptedMessage extends Message {
    
    private SecretKey secret;
    
    public AESEncryptedMessage(Node t, SecretKey secret) {
        super(t);
        this.secret = secret;
    }
    
    @Override
    public byte[] serialize() {
        byte[] messageBytes = super.serialize();
        byte[] cryptedBytes = new AES(this.secret).encrypt(messageBytes);
        return withEncryptionType(cryptedBytes, ENC_TYPE_AES);
    }
}
