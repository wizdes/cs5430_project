/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package security_layer;

import application.messages.Message;
import java.io.Serializable;

/**
 *
 * @author yjli_000
 */
public class HMACMessage implements Serializable{
    byte[] HMAC;
    Message m;
    HMACMessage(Message m, byte[] hmac)
    {
        this.HMAC = hmac;
        this.m = m;
    }
}
