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
 * @author Yi
 */
public class EncryptedMsgwNonce extends AESEncryptedMessage {
    private Integer nonce;
    private String msg;
    
    public EncryptedMsgwNonce(Node t, String mid, String msg,Integer nonce, SecretKey secret) {
        super(t, msg, secret);
        this.nonce = nonce;
        this.msg = msg;
    }
    
    public Integer getNonce() {
        return this.nonce;
    }
    
    public String getMsg() {
        return this.msg;
    }
}
