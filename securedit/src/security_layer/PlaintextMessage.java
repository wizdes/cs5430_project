package security_layer;

import application.encryption_demo.Message;

/**
 * This is a plaintext message which during the few times we send things unencrypted.
 * -One example of this is during authentication where most of the messages are 
 *  in plaintext.
 * @author yjli
 */
public class PlaintextMessage implements java.io.Serializable {
    public Message message;
}
