/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package security_layer;

import application.encryption_demo.Message;
import java.io.Serializable;

/**
 *
 */
class ApplicationMessage implements Serializable {
    
    Message message;
    long counter;
    
    ApplicationMessage(Message m, long c) {
        this.message = m;
        this.counter = c;
    }
    
}
