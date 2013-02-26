/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.encryption_demo.Message;
import java.io.Serializable;

/**
 *
 * @author yjli_000
 */
class AESMessage implements Serializable{
    byte[] HMAC;
    Message m;
    AESMessage(Message m, byte[] hmac)
    {
        this.HMAC = hmac;
        this.m = m;
    }
    
    byte[] getHMAC() {
        return HMAC;
    }

    Message getMessage() {
        return m;
    } 
    
}
