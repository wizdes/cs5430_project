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
public class ChallengeResponse extends AESEncryptedMessage {
    
    private Integer nonce;
    
    public ChallengeResponse(Node t, String mid, Integer nonce, SecretKey secret) {
        super(t, mid, secret);
        this.nonce = nonce;
    }
    
    public Integer getNonce() {
        return this.nonce;
    }
}
