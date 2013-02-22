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
public class MessageInitRequest extends AESEncryptedMessage {
    private Integer nonce;
    
    public MessageInitRequest(Node t, String mid, Integer nonce, SecretKey secret) {
        super(t, mid, secret);
        this.nonce = nonce;
    }
    
    public Integer getNonce() {
        return this.nonce;
    }
}
