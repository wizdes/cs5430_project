/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;
import encryption.AES;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.SecretKey;
import network.Network;
import network.Node;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class DemoMessageTest extends MessageTest {
    
    @Test
    @Override
    public void testSerialize() {
        Node from = new Node("1", "localhost", 4444);
        Node to = new Node("2", "localhost", 4445);
        SecretKey secret = AES.generateKey("password", "secret");
        
        DemoMessage m = new DemoMessage(to, from, "messageID", "hello world", secret);
        byte[] serialized = m.serialize();
        DemoMessage obj = (DemoMessage)Message.fromBytes(serialized, secret);
        assertEquals(m, obj);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
        assertEquals("hello world", obj.getContent());
    }

    @Test
    @Override
    public void testSending() {
        SecretKey secret = AES.generateKey("password", "secret");
        DemoMessage m = new DemoMessage(to, from, "messageID", "hello world", secret);
        Network myNetwork = new Network(to);
        myNetwork.setSecret(secret);
        Network sendNetwork = new Network(from);
        
        SendMessageThread thread = new SendMessageThread(sendNetwork, m);
        
        thread.start();
        
        List<Message> messages = (LinkedList)myNetwork.waitForMessages();
        
        assertEquals(messages.size(), 1);
        
        DemoMessage obj = (DemoMessage)messages.get(0);
        assertEquals(obj, m);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
        assertEquals("hello world", obj.getContent());
        
        myNetwork.shutdown();
        sendNetwork.shutdown();
    }       
    
}
