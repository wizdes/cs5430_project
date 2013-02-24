/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application.encryption_demo;

import application.messages.EncryptedMessage;
import application.messages.Message;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import security_layer.SecureTransportInterface;
import transport_layer.network.Node;

/**
 *
 * @author goggin
 */
public class CommunicationTest {
    
    Node myNode = new Node("1", "localhost", 4001);
    Node theirNode = new Node("2", "localhost", 4002);
    String password = "1234567890123456";
    CommunicationInterface myCommunicator;
    CommunicationInterface theirCommunicator;
        
    @Before
    public void setUp() throws Exception {
        myCommunicator = new Communication(password, myNode);
        SecureTransportInterface s1 = myCommunicator.getSecureTransport();
        theirCommunicator = new Communication(password, theirNode);
        theirCommunicator.getSecureTransport().setKeys(s1.getKeys());
    }

    @After
    public void tearDown() throws Exception {
        myCommunicator.shutdown();
        theirCommunicator.shutdown();
    }
        
    @Test
    public void testSendMessage() {
        int iterations = 100;
        int recieved = 0;
        new SendingThread(theirCommunicator, new PlainMessageSender(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                assertEquals("message-" + recieved++, m.getMessageId());
            }
        }
        
        assertEquals(recieved, 100);
    }
                
    @Test
    public void testSendAESMessage() {
        int iterations = 100;
        int recieved = 0;
        new SendingThread(theirCommunicator, new AESMessageSender(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                EncryptedMessage em = (EncryptedMessage)m;
                assertEquals("message-" + recieved, em.getMessageId());
                assertEquals("hello, world-" + recieved++, (String)em.getDecryptedObject());
            }
        }
        
        assertEquals(recieved, 100);
    }    
    
    @Test
    public void testSendRSAMessage() {
        int iterations = 100;
        int recieved = 0;
        new SendingThread(theirCommunicator, new RSAMessageSender(), myNode, iterations).start();
        
        while (recieved < iterations) {
            Collection<Message> messages = myCommunicator.waitForMessages();
            for (Message m : messages) {
                EncryptedMessage em = (EncryptedMessage)m;
                assertEquals("message-" + recieved, em.getMessageId());
                assertEquals("hello, world-" + recieved++, (String)em.getDecryptedObject());
            }
        }
        
        assertEquals(recieved, 100);
    }     
    
    @Test
    public void testWaitForReply() {
        ArrayList<String> recieved_ids = new ArrayList<>();
        int iterations = 100;
        
        new ReplyServerThread(theirCommunicator, iterations).start();
        
        for (int i = 0; i < iterations; i++) {
            Message toSend = new Message(theirNode, i + "");
            Message response = myCommunicator.sendMessageAndAwaitReply(toSend);
            assertNotNull(response);
            assertEquals(toSend.getMessageId(), response.getReplyTo());
            recieved_ids.add(response.getMessageId());
        }
        
        assertEquals(iterations, recieved_ids.size());
    }
    
    private static class SendingThread extends Thread {
        private CommunicationInterface communicator;
        int iterations;
        private Node target;
        private MessageSender sender;
        
        public SendingThread(CommunicationInterface c, MessageSender sender, Node target, int i) {
            communicator = c;
            this.target = target;
            iterations = i;
            this.sender = sender;
        }
        
        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                sender.sendMessage(communicator, target, i);
            }
        }
    }
    
    private static interface MessageSender {
        public void sendMessage(CommunicationInterface c, Node target, int i);
    }    
    
    private static class PlainMessageSender implements MessageSender {
        
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            Message m = new Message(target, "message-" + i);
            c.sendMessage(m);
        }

    }
    
    private static class AESMessageSender implements MessageSender {
        
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            EncryptedMessage m = new EncryptedMessage(target, "message-" + i);
            c.sendAESEncryptedMessage(m, "hello, world-" + i);
        }

    }
    
    private static class RSAMessageSender implements MessageSender {
        
        @Override
        public void sendMessage(CommunicationInterface c, Node target, int i) {
            EncryptedMessage m = new EncryptedMessage(target, "message-" + i);
            c.sendRSAEncryptedMessage(m, "hello, world-" + i);
        }

    }   
    
    private static class ReplyServerThread extends Thread {
        private CommunicationInterface communicator;
        int iterations;

        public ReplyServerThread(CommunicationInterface c, int i) {
            communicator = c;
            iterations = i;
        }
        
        @Override
        public void run() {
            int recieved = 0;
            while (recieved < iterations) {
                for (Message m : communicator.waitForMessages()) {
                    Message reply = new Message(m.getFrom(), "client-" + recieved++);
                    reply.setReplyTo(m.getMessageId());
                    communicator.sendMessage(reply);
                }
            }
        }
    }    
}
