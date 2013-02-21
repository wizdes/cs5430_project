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
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author goggin
 */
public class AESEncryptedMessageTest extends MessageTest {
    
    @Test
    @Override
    public void testSerialize() {
        SecretKey secret = AES.generateKey("password", "secret");
        
        Message m = new AESEncryptedMessage(to, from, "messageID", secret);
        byte[] serialized = m.serialize();
        Message obj = Message.fromBytes(serialized, secret);
        assertEquals(m, obj);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
    }
    
    @Test
    @Override
    public void testSending() {
        SecretKey secret = AES.generateKey("password", "secret");
        Message m = new AESEncryptedMessage(to, from, "messageID", secret);
        Network myNetwork = new Network(to);
        myNetwork.setSecret(secret);
        Network sendNetwork = new Network(from);
        
        SendMessageThread thread = new SendMessageThread(sendNetwork, m);
        
        thread.start();
        
        List<Message> messages = (LinkedList)myNetwork.waitForMessages();
        
        assertEquals(messages.size(), 1);
        
        Message obj = messages.get(0);
        assertEquals(obj, m);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
        
        myNetwork.shutdown();
        sendNetwork.shutdown();
    }   
}
