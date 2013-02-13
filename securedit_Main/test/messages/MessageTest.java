/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;

import java.io.IOException;
import java.io.Serializable;
import network.Node;
import org.junit.Test;
import static org.junit.Assert.*;

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
        String serialized = m.serialize();
        Message obj = Message.fromString(serialized);
        assertEquals(obj, m);
        assertEquals(obj.getTo(), m.getTo());
        assertEquals(obj.getFrom(), m.getFrom());
    }

}
