/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import encryption.AES;
import java.beans.Transient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import network.Network;
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
    
    public AESEncryptedMessage(Node t, Node f, String mid, SecretKey secret) {
        super(t, f, mid);
        this.secret = secret;
    }
    
    @Override
    public byte[] serialize() {
        byte[] messageBytes = super.serializeRaw();
        byte[] cryptedBytes = new AES(this.secret).encrypt(messageBytes);
        return withEncryptionType(cryptedBytes, ENC_TYPE_AES);
    }
}
