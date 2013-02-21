/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;
import encryption.AES;
import javax.crypto.SecretKey;
import network.Node;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class AESEncryptedMessageTest {
    
    @Test
    public void testSerialize() {
        Node from = new Node("1", "localhost", 4444);
        Node to = new Node("2", "localhost", 4445);
        SecretKey secret = AES.generateKey("password", "secret");
        
        Message m = new AESEncryptedMessage(to, from, "messageID", secret);
        byte[] serialized = m.serialize();
        Message obj = Message.fromBytes(serialized, secret);
        assertEquals(m, obj);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
    }
    
}
