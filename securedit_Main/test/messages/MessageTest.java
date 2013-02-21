/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;
import network.Node;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class MessageTest {
    
    public MessageTest() {
    }

    @Test
    public void testSerialize() {
        Node from = new Node("1", "localhost", 4444);
        Node to = new Node("2", "localhost", 4445);
        Message m = new Message(to, from, "messageID");
        byte[] serialized = m.serialize();
        Message obj = Message.fromBytes(serialized);
        assertEquals(obj, m);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
    }
    
    //@Test
    public void testEncrypredSerialize() {
        Node from = new Node("1", "localhost", 4444);
        Node to = new Node("2", "localhost", 4445);
        Message m = new Message(to, from, "messageID");
        byte[] serialized = m.serializeEncrypted("salt", "password");
        Message obj = Message.fromEncryptedBytes(serialized, "salt", "password");
        assertEquals(obj, m);
        assertEquals(obj.getTo(), m.getTo());
        assertEquals(obj.getFrom(), m.getFrom());
        assertEquals(obj.getMessageId(), m.getMessageId());
    }
}
