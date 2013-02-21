/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package messages;

import javax.crypto.SecretKey;
import network.Node;

/**
 *
 */
public class DemoMessage extends AESEncryptedMessage {
    private String message;
    
    public DemoMessage(Node t, String message, SecretKey secret) {
        super(t, secret);
        this.message = message;
    }
    
    public DemoMessage(Node t, Node f, String mid, String m, SecretKey secret) {
        super(t, f, mid, secret);
        this.message = m;
    }
        
    public String getContent() {
        return message;
    }

}
