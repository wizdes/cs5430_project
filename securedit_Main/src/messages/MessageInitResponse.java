/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import javax.crypto.SecretKey;
import network.Node;

/**
 *
 * @author Yi
 */
public class MessageInitResponse extends AESEncryptedMessage {
    private Integer nonce;
    private Integer nonce_prime;
    
    public MessageInitResponse(Node t, String mid, Integer nonce, Integer nonce_prime, SecretKey secret) {
        super(t, mid, secret);
        this.nonce = nonce;
        this.nonce_prime = nonce_prime;
    }
    
    public Integer getNonce() {
        return this.nonce;
    }
    
    public Integer getNoncePrime(){
        return this.nonce_prime;
    }
}
