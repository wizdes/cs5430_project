/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import encryption.AES;
import javax.crypto.SecretKey;
import network.Node;

/**
 *
 */
public class AESEncryptedMessage extends Message {
    
    transient
    private SecretKey secret;
    
    public AESEncryptedMessage(Node t, SecretKey secret) {
        super(t);
        this.secret = secret;
    }

    public AESEncryptedMessage(Node t, String mid, SecretKey secret) {
        super(t, mid);
        this.secret = secret;
    }    
    
    public AESEncryptedMessage(Node t, Node f, String mid, SecretKey secret) {
        super(t, f, mid);
        this.secret = secret;
    }
    
    public void setSecret(SecretKey secret) {
        this.secret = secret;
    }
    
    @Override
    public byte[] serialize() {
        byte[] messageBytes = super.serializeRaw();
        byte[] cryptedBytes = new AES(this.secret).encrypt(messageBytes);
        return withEncryptionType(cryptedBytes, ENC_TYPE_AES);
    }
}
