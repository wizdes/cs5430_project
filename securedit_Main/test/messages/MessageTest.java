/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messages;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import network.Network;
import network.Node;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class MessageTest {
    
    protected Message message;
    protected Node from;
    protected Node to;
    
    public MessageTest() {
        from = new Node("1", "localhost", 4444);
        to = new Node("2", "localhost", 4445);
        message = new Message(to, from, "messageID");
    }

    @Test
    public void testSerialize() {
        byte[] serialized = message.serialize();
        Message obj = Message.fromBytes(serialized);
        assertEquals(obj, message);
        assertEquals(message.getTo(), obj.getTo());
        assertEquals(message.getFrom(), obj.getFrom());
        assertEquals(message.getMessageId(), obj.getMessageId());
    }
    
    @Test
    public void testSending() {        
        Network myNetwork = new Network(to);
        Network sendNetwork = new Network(from);
        
        SendMessageThread thread = new SendMessageThread(sendNetwork, message);
        
        thread.start();
        
        List<Message> messages = (LinkedList)myNetwork.waitForMessages();
        
        assertEquals(messages.size(), 1);
        
        Message obj = messages.get(0);
        assertEquals(obj, message);
        assertEquals(message.getTo(), obj.getTo());
        assertEquals(message.getFrom(), obj.getFrom());
        assertEquals(message.getMessageId(), obj.getMessageId());
        
        myNetwork.shutdown();
        sendNetwork.shutdown();
    }
    
    protected static class SendMessageThread extends Thread {
        private Network network;
        private Message message;
        
        public SendMessageThread(Network n, Message m) {
            network = n;
            message = m;
        }
        
        @Override
        public void run() {
            network.sendMessage(message);
        }
    }
}
