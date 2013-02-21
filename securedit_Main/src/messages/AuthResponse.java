/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import encryption.AES;
import encryption.RSA_Crypto;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import network.Node;

/**
 *
 */
public class AuthResponse extends Message {
    
    private SecretKey secret;
    private PublicKey public_key;
    private Integer nonce;
    
    public AuthResponse(Node t, String mid, SecretKey secret, Integer nonce, PublicKey public_key) {
        super(t, mid);
        this.secret = secret;
        this.nonce = nonce;
        this.public_key = public_key;
    }
    
    public SecretKey getSecret() {
        return this.secret;
    }
    
    public Integer getNonce() {
        return nonce;
    }
    
    @Override
    public byte[] serialize() {
        byte[] messageBytes = super.serializeRaw();
        byte[] cryptedBytes = RSA_Crypto.PublicKeyEncrypt(public_key, messageBytes);
        return withEncryptionType(cryptedBytes, ENC_TYPE_RSA);
    }
}
