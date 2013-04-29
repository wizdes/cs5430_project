package security_layer;

import application.encryption_demo.Message;
import java.io.Serializable;

/**
 * This specifies the message being encrypted is an message from the application.
 * -This is useful when multiplexing on the other end.
 * -It also contains a counter which decreases the number of nonces we need in
 *  our protocol.
 */
class ApplicationMessage implements Serializable { 
    Message message;    //Message from the communication layer.
    long counter;
    
    ApplicationMessage(Message m, long c) {
        this.message = m;
        this.counter = c;
    }
    
}
