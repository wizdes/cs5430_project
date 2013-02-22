
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
public class EncryptedMsgwNonceTest extends MessageTest {
    
    @Test
    @Override
    public void testSerialize() {
        SecretKey secret = AES.generateKey("password", "secret");
        
        Integer nonce = 500;
        EncryptedMsgwNonce m = new EncryptedMsgwNonce(to, "messageID", "hello world", nonce, secret);
        byte[] serialized = m.serialize();
        EncryptedMsgwNonce obj = (EncryptedMsgwNonce)Message.fromBytes(serialized, secret);
        assertEquals(m, obj);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
        assertEquals("hello world", obj.getMsg());
    }

    @Test
    @Override
    public void testSending() {
        SecretKey secret = AES.generateKey("password", "secret");
        Integer nonce = 500;
        EncryptedMsgwNonce m = new EncryptedMsgwNonce(to, "messageID", "hello world", nonce, secret);
        
        Network myNetwork = new Network(to);
        myNetwork.setSecret(secret);
        Network sendNetwork = new Network(from);
        
        MessageTest.SendMessageThread thread = new MessageTest.SendMessageThread(sendNetwork, m);
        
        thread.start();
        
        List<Message> messages = (LinkedList)myNetwork.waitForMessages();
        
        assertEquals(messages.size(), 1);
        
        EncryptedMsgwNonce obj = (EncryptedMsgwNonce)messages.get(0);
        assertEquals(obj, m);
        assertEquals(m.getTo(), obj.getTo());
        assertEquals(m.getFrom(), obj.getFrom());
        assertEquals(m.getMessageId(), obj.getMessageId());
        assertEquals("hello world", obj.getMsg());
        
        myNetwork.shutdown();
        sendNetwork.shutdown();
    }   
    
}
